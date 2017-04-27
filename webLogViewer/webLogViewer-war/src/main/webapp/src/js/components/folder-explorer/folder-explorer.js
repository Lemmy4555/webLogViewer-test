require("./folder-explorer.css"); 
var FolderExplorerView = require("Components/folder-explorer/folder-explorer-view.js");
require("Components/l45-loader/l45-loader.js");

/**
 * Componente che rappresenta la view per esplorare le cartelle sul filesystem delle API
 */
class FolderExplorer extends HTMLDivElement {
  constructor () {
    super();
  }
  
  createdCallback() {
    var self = this;
    var FolderExplorer = this;
    this.innerHTML = require("html-loader!./folder-explorer.html");

    var logger = new Logger(self.constructor.name);

    /** callback richiamata quando si preme un'elemento della view */
    var onFolderExplorerElementClick = function(fileName, isFile) {};
    /** callback richiamata quando si preme il tasto per la cartella superiore */
    var onFolderExplorerUpClick = function() {};
    /** callback richiamata quando si preme il tasto per la Home */
    var onFolderExplorerHomeClick = function() {};

    /** Visualizza messaggi di errore nel componente */
    var messagesBox = $(self).find("[is=messages-box]")[0];
    /** Loader caricamento */
    var l45Loader = $(self).find("[is=l45-loader]")[0];
    /** rappresenta la lista di elementi del folder corrente */
    var ulFolderExplorerViewer = $(self).find('ul');
    /** Tasto per andare alla cartella superiore */
    var folderUpButton = $(self).find(".folder-up");
    /** Tasto per andare alla cartella superiore */
    var homeButton = $(self).find(".go-home");
    /** Indica se il tasto per la cartella superiore e abilitato */
    var isFolderUpEnabled = true;

    homeButton.on("click", function() {
      onFolderExplorerHomeClick();
    });

    FolderExplorer.showLoader = function() {
      ulFolderExplorerViewer.empty();
      messagesBox.closeMessage();
      l45Loader.showLoader();
      self.disableFolderUp();
    }
    
    FolderExplorer.hideLoader = function() {
      l45Loader.hideLoader();
      self.enableFolderUp();
    }
    
    FolderExplorer.showMessage = function(message) {
      ulFolderExplorerViewer.empty();
      l45Loader.hideLoader();
      messagesBox.showMessage(message);
      self.disableFolderUp();
    }
    
    FolderExplorer.closeMessage = function() {
      messagesBox.closeMessage();
      self.enableFolderUp();
    }

    FolderExplorer.disableFolderUp = function() {
      if(isFolderUpEnabled) {
        folderUpButton.addClass("disabled");
        folderUpButton.off("click");
        isFolderUpEnabled = false;
      }
    }

    FolderExplorer.enableFolderUp = function() {
      if(!isFolderUpEnabled) {
        folderUpButton.removeClass("disabled");
        /** evento richiamato quando si preme il tasto per la cartella superiore */
        folderUpButton.on("click", function() {
          logger.debug("Navigazione cartella superiore");
          onFolderExplorerUpClick();
        });
        isFolderUpEnabled = true;
      }
    }

    /**
     * Aggiorna la view con gli elementi da visualizzare in input. 
     * struttura di input:
     * {toView: [{type: 'folder', name: 'test'},{type: 'file', name: 'test2'}]}
     * 
     * @param {FolderExplorerView} foldersToView struttura di cartelle da visualizzare, toView rappresenta gli elementi
     *                                           nella folder corrente da visualizzare mentre up rappresenta la cartella superiore
     */
    FolderExplorer.updateView = function(foldersToView) {
      if(!(foldersToView instanceof FolderExplorerView)) {
        throw new TypeError("foldersToView deve essere di tipo FolderExplorerView per essere visualizzato");
      }
      var ulFolderExplorerViewerNew = $("<ul></ul>");
      var resultHtml = [];
      foldersToView.toView = foldersToView.toView.sort(orderFoldersViewByName);
      foldersToView.toView.forEach(function (e) {
        var type = getType(e.isFile);
        var html = self.htmlFolderExplorerHtmlElement(type, e.name);
        html.on("click", function() {
          var name = this.name;
          var isFileBoolean = isFile(this.type);
          logger.debug("Premuto l'elemento %s del folder-explorer", name);
          onFolderExplorerElementClick(name, isFileBoolean);
        });
        resultHtml.push(html);
      });
      ulFolderExplorerViewerNew.append(resultHtml);
      ulFolderExplorerViewer.replaceWith(ulFolderExplorerViewerNew);
      ulFolderExplorerViewer = ulFolderExplorerViewerNew;
      logger.debug("La view del folder-explorer e stata aggiornata");
    }

    function orderFoldersViewByName(a, b) {
      return a.name.toLowerCase().localeCompare(b.name.toLowerCase());
    }

    /**
     * @return {string} "file" if true; "folder" if false
     */
    function getType(isFile) {
      return isFile ? "file" : "folder";
    }

    /**
     * @return {boolean} true if "file"; false if "file"
     */
    function isFile(type) {
      return type === "file" ? true : false;
    }

    /**
     * @return {FolderExplorerElement} un FolderExplorerElement inizializzato con i parametri in input
     */
    FolderExplorer.htmlFolderExplorerHtmlElement = function(type, name) {
      var result = new FolderExplorerElement();
      result.init(type, name);
      return $(result);
    }
    
    FolderExplorer.onFolderExplorerElementClick = function (callback) {
      onFolderExplorerElementClick = callback;
      return self;
    }

    FolderExplorer.onFolderExplorerUpClick = function (callback) {
      onFolderExplorerUpClick = callback;
      return self;
    }

    FolderExplorer.onFolderExplorerHomeClick = function(callback) {
      onFolderExplorerHomeClick = callback;
      return self;
    }
  }
}

/**
 * Componente privato che rappresenta un elemento visualizzato nel FolderExplorer
 */
class FolderExplorerElement extends HTMLLIElement {
  constructor () {
    super();
  }

  createdCallback() {
    var self = this;
    var FolderExplorerElement = this;

    self.innerHTML = require("html-loader!./folder-explorer-element.html");

    /** file o folder */
    self.type = null;
    /** nome cartella o nome file */
    self.name = null;

    var folderExplorerElement = $(self);
    var textArea = folderExplorerElement.find("span");
    var icon = folderExplorerElement.find("i");
    
    FolderExplorerElement.init = function (type, name) {
      self.type = type;
      self.name = name;
      folderExplorerElement.addClass('folder-explorer-' + type);
      icon.addClass("fa-" + type);
      textArea.html(name);
    }
  }
}

module.exports = document.registerElement('folder-explorer', { 
  extends: 'div',
  prototype: Object.create(FolderExplorer.prototype)
});

FolderExplorerElement = document.registerElement('folder-explorer-element', { 
  extends: 'li',
  prototype: Object.create(FolderExplorerElement.prototype)
});