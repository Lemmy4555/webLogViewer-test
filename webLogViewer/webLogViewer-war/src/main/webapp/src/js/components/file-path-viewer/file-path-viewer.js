require("./file-path-viewer.css");
require("./file-path-viewer-element.css");

class FilePathViewer extends HTMLDivElement {
  constructor() {
    super();
  }

  createdCallback() {
    var html = require("html-loader!./file-path-viewer.html");
    var self = this;
    var FilePathViewer = this;
    self.innerHTML = html;

    var currentPath = new Path("");

    FilePathViewer.update = function(path) {
      if(!path) {
        path = "";
      }
      if(typeof path !== "string") {
        throw new TypeError("path deve essere una stringa");
      }
      $(self).html("");
      currentPath.asArray.forEach(function (part, index) {
        var element = new FilePathViewerElement().init(index, part);
        $(self).append(element);
      });
    }
      
  }
}

class Path {
  constructor(path) {
    var self = this;
    var Path = this;

    const winDiskRegex = /[A-Za-z]:\/\//;
    const SMBRegex = /^\/\/\w+/;
    const UnixRegex = /^\/\w+/;

    //Il path in input viene salvato come stringa e come array
    var asArray;
    var asString;

    Path.getAsArray = function() {
      return asArray;
    }

    Path.getAsString = function() {
      return asString;
    }

    //Setta il path in input
    Path.set = function(path) {
      asArray = [];
      /*
       * La lettera del disco di windows, il doppio slash del percorso di 
       * rete e lo slash iniziale dei percorsi UNIX sono da considerarsi dei folder
       * e pertanto vanno salvati così come scritti nel path asArray
       */
      var specialFolderEnd;
      if (winDiskRegex.test(path)) {
        specialFolderEnd = 4;
      } else if (SMBRegex.test(path)) {
        specialFolderEnd = 2;
      } else if (UnixRegex.test(path)) {
        specialFolderEnd = 1;
      } else if (path === ""){
        asArray = [];
        asString = path;
        return self;
      } else {
        throw new Error("Il percorso inserito non e valido")
      }
      asArray.push(path.substring(0, specialFolderEnd));
      asArray.concat(path.substring(specialFolderEnd).split("/"));

      asString = Globals.unixPath(path).replace("/");
      return self;
    }

    self.path = path;
  }

  set path(path) {
    this.set(path);
  }

  get asArray() {
    return this.getAsArray();
  }

  get asString() {
    return this.getAsString();
  }
}

class PathPart {
  constructor(name, isNavigable) {
    var self = this;
    var PathPart = this;

    PathPart.getName = function() {
      return name;
    }

    PathPart.isNavigable = function() {
      return isNavigable;
    }
  }

  get navigable() {
    this.isNavigable();
  }

  get name() {
    this.getNavigable();
  }
}

class FilePathViewerElement extends HTMLDivElement {
  constructor() {
    super();
  }

  createdCallback() {
    var html = "";
    var self = this;
    var FilePathViewerElement = this;
    self.innerHTML = html;

    var pathPart = "";
    var onNavigate = function () {};
    var nameLabel = $(self).children("span").first();

    function update(name, navigable) {
      nameLabel.html(name);

      self.off("click");
      if(navigable) {
        self.on("click", function() {
          onNavigate(this.name);
        });
      }
    }

    FilePathViewerElement.getName = function() {
      return pathPart.name;
    }

    FilePathViewerElement.isNavigable = function() {
      return pathPart.isNavigable;
    }

    FilePathViewerElement.getInfo = function() {
      return self.pathPart;
    }

    FilePathViewerElement.init = function(id, pathPartToSet) {
      $(self).attr("id", id);
      update(pathPartToSet.name, pathPartToSet.navigable);
      pathPart = pathPartToSet;
      return self;
    }

    FilePathViewerElement.onNavigate = function(callback) {
      onNavigate = callback;
    }

  }

  set pathPart(pathPart) {
    this.update(pathPart);
  }

  get pathPart() {
    return this.getInfo();
  }

  get name() {
    return this.getName();
  }

  get navigable() {
    return this.isNavigable();
  }
}

module.exports = document.registerElement('file-path-viewer', { 
  extends: 'div',
  prototype: Object.create(FilePathViewer.prototype)
});