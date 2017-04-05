var WlvFile = require("Models/wlv-file.js");
var WlvApi = require("Api/wlv-api.js");

/**
 * Job che dato un file rimane in lettura e se il file viene modificato torna le nuove righe aggiunte.
 */
module.exports = class TailFileJob {
  constructor() {
    var TailFileJob = this;
    var self = this;
    var logger = new Logger(self.constructor.name);

    /** tempo di attesa dopo ogni request */
    const UPDATE_INTERVAL = 10000;
    /** get request eseguita per ottenere il testo dal file */
    var getRequest = null;
    /** setInterval che si occupa di fare la chiamata get */
    var job = null;
    /** il file che si sta leggendo attualmente */
    var file = null;

    /** 
     * callback richiamata quando viene eseguita una request
     * @param {WlvFile} updatedFile file aggiornato con le ultime righe inserite
     */
    var onFileTailed = function (updatedFile) {};

    /**
     * Avvia un nuovo job che rimane in lettura del file in input, quando il file in input
     * viene modificato, il nuovo contenuto del file viene tornato tramite la callback onFileTailed.
     * Se c'e gia un job in esecuzione, questo viene terminato e l'eventuale request alle API in corso viene
     * annullata
     */
    TailFileJob.tail = function(toTail) {
      self.terminateJob();
      startNewJob(toTail);
      return self;
    }

    TailFileJob.onFileTailed = function(callback) {
      onFileTailed = callback;
      return self;
    }

    /**
     * Avvia un nuovo job che rimane in lettura del file in input
     */
    var startNewJob = function(fileInner) {
      logger.debug("E stato richiesto un nuovo job per il tail del file " + fileInner.path);
      self.setFile(fileInner);
      job = setInterval(function() {
        logger.debug("Il file tailed ora ha " + file.readContent.length + " righe");
        if(getRequest) {
          logger.warn("E gia stata effettuata una richiesta alle API per il tail, si attendera che finisca");
          return;
        }
        getRequest = WlvApi.getTextFromPointer(file.path, file.size);
        getRequest.done((result) => {
          logger.debug("La chiamata alle API per il tail e avvenuta con successo per il file " + file.path);
          result = new WlvFile(
            file.path, 
            result.readContent, 
            result.rowsRead,
            result.size,
            result.encoding);
          if(file.size >= result.size) {
            logger.debug("Il file da tailare non e cambiato")
            return;
          }
          file = new WlvFile(
            file.path, 
            file.readContent.concat(result.readContent), 
            file.rowsRead += result.rowsRead,
            result.size,
            result.encoding);
          onFileTailed(result);
        }).catch((error) => {
          if(Globals.isAjaxUnreacheableError(error)) {
            var message = Globals.ajaxUnreacheableErrorLogHandling(Globals.UNREACHABLE_ERR + " durante il tail del file " + file.path, error);
            logger.warn(message.std);
          } else {
            logger.warn("Errore nel tail del file %s: %s", file.filePath, error.responseText);
          }
        }).always(function() {
          getRequest = null;
        });
        
      }, UPDATE_INTERVAL);
    }

    TailFileJob.getFile = function() {
      return file;
    }

    TailFileJob.setFile = function(fileToSet) {
      if(!(fileToSet instanceof WlvFile)) {
        throw new TypeError("il file da tailare deve essere di tipo WlvFile");
      }
      if(getRequest || job) {
        /*
        * Il tail ferma il job azzerando le variabili getRequest e job,
        * poi setta il file e avvia il nuovo job.
        * In questo modo al set del file, il job si riavvia col nuovo file in input.
        */
        tail(fileToSet);
      } else {
        file = fileToSet;
      }
    }

    /**
     * Termina il job se e in esecuzione e cancella l'eventuale request in corso.
     */
    TailFileJob.terminateJob = function() {
      if(getRequest) {
        getRequest.abort();
        getRequest = null;
      }
      if(job) {
        clearInterval(job);
      }
      job = null;
      file = null;
    }
  }

  get file() {
    return this.getFile();
  }

  set file(file) {
    this.setFile(file);
  }
}