var WlvApi = require('Api/wlv-api.js');
var FileViewer = require('Components/file-viewer/file-viewer.js');
var DbManager = require('Db/db-manager.js');
var WlvFile = require('Models/wlv-file.js');
var WlvFileComplete = require('Models/wlv-file-complete.js');
var WlvDbHelper = require('Helpers/wlv-db-helper.js');
var CacheHelper = require("Helpers/cache-helper.js");
var FileViewerWriterJob = require("Jobs/file-writer-job.js");
var TailFileJob = require("Jobs/tail-file-job.js");

/**
 * Handler che occupa di gestire la scrittura del contenuto di un file nel FileViewer.
 * L'Handler gestisce sia il job TailFileJob che si occupa di effettuare periodicamente una
 * chiamata all'API per controllare che il contenuto del file non sia cambiato; sia il job
 * FileViewerWriteJob che si occupa di scrivere stringhe molto grandi nel FileViewer dividendole
 * in chunk di stringhe piu piccole
 */
module.exports = class FileViewerHandler {

  /**
   * @param {FileViewer} fileViewer istanza del fileViewer su cui verra scritto il testo del file aperto
   */
  constructor(fileViewer) {
    if(!(fileViewer instanceof FileViewer)) {
      throw new TypeError("FileViewerHandler puo essere istanziato solo con un'istanza di FileViewer in input");
    }
    var FileViewerHandler = this;
    var self = this;
    var logger = new Logger(self.constructor.name);

    /** 
     * Istanza del job che si occupa di chiamare le API per ottenere il contenuto del file
     * da visualizzare aggiornato
     */
    var tailFileJob = new TailFileJob();
    /** Numero di caratteri da leggere dal fondo di un file non ancora aperto */
    var NEW_FILE_LINES_TO_READ = 200;
    /** Istanza del job che si occupa di scrivere strighe di grandi dimensioni nel FileViewer */
    var fileWriterJob = new FileViewerWriterJob(fileViewer);
    /** Callback richiamata quando si verfica un errore nell'apertura di un file */
    var onOpenNewFileError = function (message) {};
    /** Callback richiamata quando si verifica un errore non gestito */
    var onUnhandledError = function(message) {};

    /** 
     * Chiamata alle API che scarica tutto il file oppure la chiamata che ottiene l'ultima riga del file per
     * poi richiedere il file completo.
    */
    var fullFileApiCall = null;

    var filePath = null;

    /**
     * Quando il job recupera del testo da scrivere per il file in sincronizzazione che sta 
     * osservando, il FileViewerHandler deve aggiornare il contenuto del file
     * sul DB e poi deve alimentare il FileWriterJob con la nuova stringa da scrivere
     * sul FileViewer
     */
    function handleUnsincronizedTailedFile(result) {
      var fileForUpdate = new WlvFile(filePath, result.readContent, result.rowsRead, result.size, result.encoding);
      fileWriterJob.writeText(result.readContent);
    }

    /** 
     * Quando il job recupera del testo da scrivere per il file sincronizzato con il BE che sta 
     * osservando, il FileViewerHandler deve aggiornare il contenuto del file
     * sul DB e poi deve alimentare il FileWriterJob con la nuova stringa da scrivere
     * sul FileViewer
     */
    function handleSincronizedTailedFile(result) {
      var fileForUpdate = new WlvFile(filePath, result.readContent, result.rowsRead, result.size, result.encoding);
      WlvDbHelper.updateFile(fileForUpdate);
      fileWriterJob.writeText(result.readContent);
    }

    

    /**
     * Termina forzatamente il FileWriterJob che scrive sul FileWriter e lo riavvia con
     * il nuovo contenuto del file da scrivere
     * Inoltre riesegue il TailFileJob per il nuovo file da tailare.
     * 
     * @param {WlvFile} file - file da scrivere
     */
    function writeText(file) {
      fileViewer.clear();
      useFileWriterJob(file);
      tailFileJob.tail(file);
    }

    function useFileWriterJob(file) {
      fileWriterJob.terminateJob();
      fileWriterJob.writeText(file.readContent);
    }

    /**
     * Effettua tutte le operazioni necessarie per leggere un nuovo file e visualizzarlo sul
     * FileViewer.
     * Controlla se sul DB esiste il file che si sta tentando di leggere e se esiste legge il contenuto
     * che si trova sul DB e lo scrive sul FileViewer, poi fa partire il TailFileJob che si occupa di 
     * interrogare le API per ottenere il testo mancante rispetto all'ultima volta che e stato aggiornato
     * il DB.
     * Se il file non viene trovato su DB allora interroga le API per ottenerne il contenuto e aggiunge un
     * nuovo record sul DB con il contenuto letto.
     * Il file rimane in ascolto per aggiornamenti e la scrittura avviene tramite il FileWriterJob per gestire
     * stringhe di grosse dimensioni.
     * 
     * @param {string} filePath - percorso su filesystem del file da leggere
     */
    FileViewerHandler.openNewFile = function(filePath) {
      if(!filePath) {
        throw new Error("Non e stato inserito il percorso del file da leggere");
      }
      
      openNewFile(filePath, function(fileRead) {
        writeText(fileRead);
        CacheHelper.setLastOpenedFile(fileRead.path);
      });
    }

    FileViewerHandler.onOpenNewFileError = function(callback) {
      onOpenNewFileError = callback;
      return self;
    }

    FileViewerHandler.onUnhandledError = function(callback) {
      onUnhandledError = callback;
    }    

    function openNewFile(filePathInner, onFileRead) {
      filePath = filePathInner;
      var readFilePromise = new Promise(
        (success, reject) => {
          if(fullFileApiCall) {
            //Se era in corso una chiama per ottenere un file completo, la interrompo.
            fullFileApiCall.abort();
          }
          var request = DbManager.db().getFile(filePathInner);
          request.onsuccess = function(event) {
            if(request.result) {
              //Se il file e gia presente su DB
              logger.debug("Il file %s e stato trovato sul DB e verra riaperto", filePathInner);
              var result = request.result;
              var fileRead = new WlvFile(result.path, result.readContent, result.rowsRead, result.size, result.encoding);
              tailFileJob.onFileTailed(handleSincronizedTailedFile);
              success(fileRead);
            } else {
              tailFileJob.onFileTailed(handleUnsincronizedTailedFile);
              //Se il file non e stato ancora registrato su DB
              logger.debug("Il file %s non e stato trovato sul DB e verra richiesto alle API", filePathInner);
              WlvApi.getFileData(filePathInner).done(function(result) {
                if(result.isFile) {
                  WlvApi.getTailText(filePathInner, NEW_FILE_LINES_TO_READ)
                    .done(function(result) {
                      //Tail del file non ancora presente su DB
                      var fileToInsert = new WlvFile(filePathInner, result.readContent, result.rowsRead, result.size, result.encoding);
                      handleNewFileOpening(fileToInsert);
                      success(fileToInsert);
                    }).catch(function(error) {
                      if(Globals.isAjaxUnreacheableError(error)) {
                        var message = Globals.ajaxUnreacheableErrorLogHandling(Globals.UNREACHABLE_ERR + " durante la lettura del file " + filePathInner, error);
                        logger.warn(message.std);
                      } else {
                        logger.warn("Errore durante la chiamata alle API per leggere il file %s: %s", filePathInner, error.responseText);
                      }
                    })
                } else {
                  logger.warn("%s non e un file e per tanto il FileViewer non verra aggiornato", filePathInner);
                }
              }).catch(function(error) {
                if(!Globals.isAjaxUnreacheableError(error)) {
                  var errorMessage = null;
                  try {
                    errorMessage = JSON.parse(error.responseText).error;
                  } catch (e) {
                    errorMessage = error.responseText;
                  }
                  logger.warn("Errore durante la chiamata alle API per ottenere i dati del file %s, errore %s: %s",
                  filePathInner, error.status, errorMessage);
                  onOpenNewFileError("Errore " + error.status + ": " + errorMessage);
                } else {
                  var message = Globals.ajaxUnreacheableErrorLogHandling(Globals.UNREACHABLE_ERR, error);
                  logger.warn(message.std);
                  onOpenNewFileError(message.html);
                }
              })
            }
          }
        
          request.onerror = function(event) {
            logger.warn("Errore durante il reperimento del file %s dal db: %s", filePathInner, event.target.errorCode);
          };
        }
      );

      readFilePromise.then(onFileRead);
    }

    /**
     * Metodo richiamato quando si apre un file che non e stato ancora cachato su db.
     * Deve partire una chiamata che scarica gli ultimi 10MB di file.
     * Dopo aver completato queste operazioni occorre terminare il job che taila il file, poi
     * va lanciata la chiamata alle api manualmente e occorre allineare il file in cache con quello
     * visualizzato.
     * 
     * @param {WlvFile} fileToInsert 
     */
    function handleNewFileOpening(fileToInsert) {
      var path = fileToInsert.path;
      //Ottengo tutto il file
      fullFileApiCall = WlvApi.getFullFile(fileToInsert.path);
      fullFileApiCall.done((response) => {
          var fileToSync = new WlvFileComplete(fileToInsert.path, response.readContent, response.rowsRead, response.size, response.encoding, response.rowsInFile);
          //Siccome devo sencronizzare il file da mettere in cache con quanto si vede in output, fermo il job che taila il file a video
          var currentJobFileSize = tailFileJob.file.size;
          tailFileJob.terminateJob();
          //Effettuo la chiamata di tail sul file a video manualmente richiedendo anche il numero di righe nel file
          var manualUnsynchedFileReadRequest = WlvApi.getTextFromPointer(path, currentJobFileSize, true)
            .done((response) => {
              var fileReadManually = new WlvFileComplete(path, response.readContent, response.rowsRead, response.size, response.encoding, response.rowsInFile);
              useFileWriterJob(fileReadManually);
            });
          //Effettuo la chiamata di tail sul file da cachare perche mi servira per la sincronizzazione con quello a video
          var manualTosynchFileReadRequest = WlvApi.getTextFromPointer(path, fileToSync.size);
          $.when(manualUnsynchedFileReadRequest, manualTosynchFileReadRequest).done(
            (unsyncFileRensponse, syncFileResponse) => {
              unsyncFileRensponse = unsyncFileRensponse[0];
              syncFileResponse = syncFileResponse[0];
              //Confronto il file per la sincronizzazione con quello completo per ottenere il file aggiornato con tutto il contenuto
              var fileSync = new WlvFileComplete(path, 
                fileToSync.readContent.concat(syncFileResponse.readContent),
                parseInt(fileToSync.rowsRead) + parseInt(syncFileResponse.rowsRead), 
                syncFileResponse.size, syncFileResponse.encoding, 
                parseInt(fileToSync.rowsInFile) + parseInt(syncFileResponse.rowsRead));
              var minRowsInFile;
              var maxRowsInFile;
              var maxSize;
              var unsyncRowsInFile = parseInt(fileViewer.contentList.length);
              if(fileSync.rowsInFile < unsyncRowsInFile) {
                minRowsInFile = fileSync.rowsInFile;
                maxRowsInFile = unsyncRowsInFile;
                maxSize = unsyncFileRensponse.size;
              } else {
                minRowsInFile = unsyncRowsInFile;
                maxRowsInFile = fileSync.rowsInFile;
                maxSize = syncFileResponse.size;
              }
              //Confronto il file con tutto il contenuto con quello a video per ottenere il contenuto non ancora visulizzato in output
              var fileGap = new WlvFile(path, 
                  fileSync.readContent.slice(minRowsInFile, maxRowsInFile),
                  maxRowsInFile - minRowsInFile,
                  maxSize, fileToSync.encoding
                );
              //Cacho il file completo
              WlvDbHelper.addFile(fileSync);
              tailFileJob.onFileTailed((file) => {
                handleSincronizedTailedFile(file);
              });
              //Faccio partire il job per il file cachato, d'ora in poi il file in cache e quello a video sono sincronizzati
              writeText(fileGap);
            }).catch((error) => {
              var error = "Non e stato possibile inserire in cache il file che si sta visualizzando: ("
                + error.errorCode + ") " + error.responseText;
              logger.error(error);
              onUnhandledError(error);
            });
        }).catch((error) => {
          var errorMsg = "Non e stato possibile inserire in cache il file, errore durante il download: ("
           + error.errorCode + ") " + error.responseText;
          logger.error(errorMsg);
          onUnhandledError(errorMsg);
        });
    }
  }
}