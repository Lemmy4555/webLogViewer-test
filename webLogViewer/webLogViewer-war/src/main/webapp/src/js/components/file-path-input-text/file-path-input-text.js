require("./file-path-input-text.css");

class FilePathInputText extends HTMLDivElement {
  constructor() {
    super();
  }

  createdCallback() {
    var self = this;
    var FilePathInputText = this;
    self.innerHTML = require("html-loader!./file-path-input-text.html");

    /*
     * WIN: (^([a-z]|[A-Z]):(?=\\(?![\0-\37<>:"/\\|?*])|\/(?![\0-\37<>:"/\\|?*])|$)|^\\(?=[\\\/][^\0-\37<>:"/\\|?*]+))((\\|\/)[^\0-\37<>:"/\\|?*]+|(\\|\/)$)*()$
     * UNIX: ^\/$|^(\/[^/\0]+)+\/?$
     */
    const regexFilePath = /(^([a-z]|[A-Z]):(?=\\(?![\0-\37<>:"/\\|?*])|\/(?![\0-\37<>:"/\\|?*])|$)|^\\(?=[\\\/][^\0-\37<>:"/\\|?*]+))((\\|\/)[^\0-\37<>:"/\\|?*]+|(\\|\/)$)*()$|^\/$|^(\/[^/\0]+)+\/?$/g;
    const inputText = $(self).children('input');
    const errorLabel = $(self).children('span');

    var logger = new Logger(self.constructor.name);

    self.path = "";
    var onPathInsert = function () {};

    $(self).on('keyup', function(e) {
      self.path = inputText.val();
      if(self.path) {
        if(isValidPath(self.path)) {
          hideError();
          if(e.keyCode == 13) {
            onPathInsert(self.path);
          }
        } else {
          displayError("Il path inserito non e valido");
        }
      } else {
        hideError();
      }
    });

    function isValidPath(path) {
      if(!path || typeof path !== "string") {
        return false;
      }
      //Resetta l'index della regex per evitare che il risultato si alterni (true, false, true, false, etc..)
      regexFilePath.lastIndex= 0
      return regexFilePath.test(path);
    }

    function displayError(message) {
      errorLabel.css("visibility", "visible")
      errorLabel.html(message);
    }

    function hideError() {
      errorLabel.css("visibility", "hidden");
      errorLabel.html("");
    }

    FilePathInputText.onPathInsert = function(callback) {
        onPathInsert = callback;
        return self;
      }
  }
}

module.exports = document.registerElement('file-path-input-text', { 
  extends: 'div',
  prototype: Object.create(FilePathInputText.prototype)
});