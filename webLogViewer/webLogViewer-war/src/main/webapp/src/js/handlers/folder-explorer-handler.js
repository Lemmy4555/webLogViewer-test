var FolderExplorer = require("Components/folder-explorer/folder-explorer.js");
var WlvApi = require("Api/wlv-api.js");
var FolderExplorerView = require("Components/folder-explorer/folder-explorer-view.js");

/**
 * Handler che si occupa di gestire il FolderExplorer: tiene traccia della cartella di default, della cartella
 * che si sta visualizzando correntemente e richiama FolderExplorer per aggiornarne la view in HTML.
 * Si occupa anche di effettuare le chiamate alle API necessarie per la gestione di FolderExplorer.
 */
module.exports = class FolderExplorerHandler {
  /**
   * @param {FolderExplorer} folderExplorer istanza del folderExplorer che verra alimentata con i dati aggiornati dall'Handler
   */
  constructor(folderExplorer) {
    if(!(folderExplorer instanceof FolderExplorer)) {
      throw new TypeError("FolderExplorerHandler puo essere istanziato solo con un'istanza di FolderExplorer in input");
    }
    var self = this;
    var FolderExplorerHandler = this;
    var logger = new Logger(self.constructor.name);

    /**
     * Cartella di default del Computer in cui si trova il server con le API, e la cartella di partenza del FolderExplorer
     * se e la prima volta che viene utilizzata la webapp
     */
    var DEFAULT_FOLDER = "";
    /** Cartella attualmente aperta, serve per poter navigare nel subfolders o per risalire alla cartella superiore */
    var currentFolder = "";
    /** callback richiamata nel momento in cui viene aperto un file e non una cartella */
    var onOpenFile = function (path) {};
    /** callback richiamata nel momento in cui viene aperto una cartella */
    var onOpenFolder = function (path) {};
    /** Callback richiamata quando si veriffica un errore nella chiamata alle API */
    var onApiCallError = function (message) {};

    /** 
     * Metodo che dato un percorso in input lo converte in un path di tipo UNIX e lo salva nella currentFolder
     * @param {string} path path da impostare
     */
    function setCurrentFolder(path) {
      currentFolder = Globals.unixPath(path);
    }

    /*
     * Quando viene premuto un elemento del FolderExplorer, il FolderExplorerHandler effettua le operazioni
     * necessarie per aprire un file o una cartella.
     */
    folderExplorer.onFolderExplorerElementClick(function(fileName, isFile) {
      //Se si preme su un elemento, il nome della cartella o del file viene concatenato al path corrente
      var path = Globals.unixPath(currentFolder) + "/" + fileName;
      if(!isFile) {
        openFolder(path);
      } else {
        openFile(path);
      }
    });

    /*
     * Quando viene premuto il tasto della cartella superiore del FolderExplorer il FolderExplorerHandler
     * elimina dal path l'ultimo elemento concatenato ed effettua la navigazione nella cartella
     */
    folderExplorer.onFolderExplorerUpClick(function() {
      openFolder(currentFolder.substr(0, currentFolder.lastIndexOf("/")));
    });

    /*
     * Quando viene premuto il tasto della Home
     */
    folderExplorer.onFolderExplorerHomeClick(function() {
      getAndGoToHomeDir();
    });

    /*
     * Chiama le API per ottenere la directory di default e la imposta come cartella corrente
     */
    getAndGoToHomeDir();

    function getAndGoToHomeDir() {
      if(DEFAULT_FOLDER === "") {
        folderExplorer.showLoader();
        WlvApi.getHomeDir().done(function (result) {
          DEFAULT_FOLDER = Globals.unixPath(result.path);
          logger.info("La default dir e stata settata a: %s", DEFAULT_FOLDER);
          self.navigateToHomeDir();
          folderExplorer.hideLoader();
        }).catch(function(error) {
          var message = null;
          if(Globals.isAjaxUnreacheableError(error)) {
            var handledMessage = Globals.ajaxUnreacheableErrorLogHandling(Globals.UNREACHABLE_ERR + " durante il recupero della home dir", error);
            message = handledMessage.html;
            logger.warn(handledMessage.std);
          } else {
            message = "Errore durante il recupero della home dir: " + error.responseText;
            logger.warn(message);
          }
          folderExplorer.hideLoader();
          onApiCallError(message);
        });
      } else {
        self.navigateToHomeDir();
      }
    }
    

    FolderExplorerHandler.onApiCallError = function(callback) {
      onApiCallError = callback;
      return this;
    }

    FolderExplorerHandler.navigateToHomeDir = function() {
      logger.debug("Navigazione alla default dir");
      folderExplorer.hideLoader();
      self.navigateTo(DEFAULT_FOLDER);
      folderExplorer.closeMessage();
    }

    /**
     * Aggiorna la view del FolderExplorer e chiama la callback quando la view e stata aggiornata.
     */
    function updateFolderExplorer(path, fileList) {
      setCurrentFolder(path);
      var view = new FolderExplorerView(fileList, )
      folderExplorer.updateView(view);
      
      onOpenFolder(path);
    }

    /**
     * Naviga al path specificato:
     * Chiama le API per ottenere le informazioni dell'elemento in cui si vuole navigare,
     * se l'elemento e una cartella viene aggiornata la view del FolderExplorer invece se
     * l'elemento e un file si aggiorna la view del FolderExplorer con il parent folder del file
     * e viene aggiornata la view del FileViewer con il contenuto del file.
     * @param {string} path path in cui si vuole navigare, prima di ogni cosa il path 
     *                      viene sempre convertito in formato UNIX
     */
    FolderExplorerHandler.navigateTo = function(path) {
      folderExplorer.showLoader();
      path = Globals.unixPath(path);
      WlvApi.getFileData(path).done(function(result) {
        folderExplorer.showLoader();
        if(!result.isFile) {
          openFolder(path);
        } else {
          openFile(path);
        }
      }).catch(function(error) {
        var message = null;
        if(Globals.isAjaxUnreacheableError(error)) {
          message = Globals.ajaxUnreacheableErrorLogHandling(Globals.UNREACHABLE_ERR + " durante la navigazione a " + path, error);
        } else {
          message = "Errore durante la navigazione a " + path + ": " + error.responseText;
        }
        logger.warn(message.std);
        onApiCallError(message.html);
      });
    }

    /**
     * Imposta la callback chiamata quando si apre un elemento di tipo File
     */
    FolderExplorerHandler.onOpenFile = function(callback) {
      onOpenFile = callback;
      return self;
    }

    /**
     * Imposta la callback chiamata quando si apre un elemento di tipo File
     */
    FolderExplorerHandler.onOpenFolder = function(callback) {
      onOpenFolder = callback;
      return self;
    }

    function openFolder(path) {
      logger.debug("Apertura cartella %s", path);
      folderExplorer.showLoader();
      WlvApi.getFileList(path).done(function(result) {
        updateFolderExplorer(path, result.fileList);
        folderExplorer.hideLoader();
        if(currentFolder.lastIndexOf("/") < 0) {
          folderExplorer.disableFolderUp();
        } else {
          folderExplorer.enableFolderUp();
        }
      }).catch(function(error) {
        if(Globals.isAjaxUnreacheableError(error)) {
          var message = Globals.ajaxUnreacheableErrorLogHandling(Globals.UNREACHABLE_ERR + " durante l'apertura della cartella " + path, error);
          logger.warn(message.std);
        } else {
          var error = "Errore durante l'apertura della cartella " + path +": " + error.responseText;
          logger.warn(error);
          folderExplorer.showMessage(error);
        }
      });
    }

    function openFile(path) {
      logger.debug("Apertura file %s", path);
      onOpenFile(path);
    }

  }
}