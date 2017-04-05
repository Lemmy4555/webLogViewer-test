var FileViewerHandler = require("Handlers/file-viewer-handler.js");
var DbManager = require("Db/db-manager.js");
var FolderExplorerHandler = require("Handlers/folder-explorer-handler.js");
var FilePathInputText = require("Components/file-path-input-text/file-path-input-text.js");
var MessagesBox = require("Components/messages-box/messages-box.js");
var FilePathViewer = require("Components/file-path-viewer/file-path-viewer.js");
var CacheHelper = require("Helpers/cache-helper.js");
var PopUpErrorLog = require("Components/pop-up-error-log/pop-up-error-log.js");

/*
* Inizializzazione App
*/
$(document).on("DOMContentLoaded", () => {
  app.init()
});

class App {
  constructor() {
    var self = this;
    var App = this;
    var logger = new Logger(self.constructor.name);
    
    var fileViewerUpdater;
    var lastRunningUpdateRequest;
    var fileViewerHandler;
    var folderExplorer;
    var folderExplorerHandler;
    var filePathInputText;
    var filePathViewer;
    
    App.openNewFileFromInputText = function(inputText) {
      fileViewerHandler.openNewFile($(inputText).val());
    }
    
    /**
     * Metodo di inizializzazione della webapp.
     * Inizializza tutti i componenti HTML/JS necessari e recupera dal DB
     * l'ultimo file letto.
     */
    App.init = function() {
      var popUpErrorLog = $("[is=pop-up-error-log]")[0];
      var fileViewer = $("[is=file-viewer]")[0];
      fileViewerHandler = new FileViewerHandler(fileViewer);
      fileViewerHandler
        .onOpenNewFileError( (message) => {
          fileViewer.showMessage(message);
        })
        .onUnhandledError((message) => {
          popUpErrorLog.showLog(message);
        });

      folderExplorer = $("[is=folder-explorer]")[0];
      filePathViewer = $("[is=file-path-viewer]")[0];
      folderExplorerHandler = new FolderExplorerHandler(folderExplorer);
      folderExplorerHandler
        .onApiCallError((message) => {
          folderExplorer.showMessage(message);
        })
        .onOpenFile(fileViewerHandler.openNewFile)
        .onOpenFolder(filePathViewer.update);

      filePathInputText = $("[is=file-path-input-text]")[0];
      filePathInputText.onPathInsert(folderExplorerHandler.navigateTo);

      //Recupera dal DB l'ultimo file aperto.
      DbManager.db().connect(() => {
        if(CacheHelper.getLastOpenedFile()) {
          fileViewerHandler.openNewFile(CacheHelper.getLastOpenedFile());
        } else {
          logger.warn("Non c'e nessun file in cache");
          fileViewer.showMessage("Selezionare un file da leggere");
        }
      });
    }
  }
}
var app = new App();