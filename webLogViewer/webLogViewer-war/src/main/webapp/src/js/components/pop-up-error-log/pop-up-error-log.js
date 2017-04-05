require("./pop-up-error-log.css");

class PopUpErrorLog extends HTMLDivElement{
  constructor() {
    super();
  }

  createdCallback() {
    var self = this;
    var PopUpErrorLog = this;
    this.innerHTML = require("html-loader!./pop-up-error-log.html");

    var popUpErrorLog = $(self);
    var popUpErrorLogListHtml = popUpErrorLog.find("ul");
    var popUpErrorLogElements = [];
    var increment = 0;

    PopUpErrorLog.showLog = function(message) {
      var popUpErrorLogElement = new PopUpErrorLogElement();
      popUpErrorLogElement.message = message;
      addElement(popUpErrorLogElement);
    }
    
    PopUpErrorLog.closeLastElement = function() {
      var lastElement = $(popUpErrorLogElements[popUpErrorLogElements.length - 1]);
      removeElement(lastElement);
    }

    function addElement(toAdd) {
      toAdd.onCloseClick(removeElement);
      var li = $("<li>").append(toAdd);
      popUpErrorLogListHtml.prepend(li);
      popUpErrorLogElements.unshift(toAdd);
      setTimeout(() => {
        $(toAdd).removeClass("hide").addClass("show");
        keepMessagesListHeightUnder70();
      }, 100);
    }

    function removeElement(toRemove) {
      toRemove.removeClass("show").addClass("hide");
      popUpErrorLogElements.pop();
      setTimeout(() => {
        toRemove.remove();
      }, 200);
    }

    function calcMesssagesListSizeInPercentage() {
      var totHeight = 0;
      popUpErrorLogListHtml.children().each((i, e) => {
        if($(e).children("[is=" + elementClassName + "]").hasClass("show")) {
          totHeight += $(e).outerHeight(true);
        }
      });
      if(totHeight > $(window).outerHeight(true)) {
        return 100;
      }
      return 100 - (($(window).outerHeight(true) - totHeight) / $(window).outerHeight(true) * 100);
    }

    /**
     * Mantiene l'altezza della lista dei messaggi sotto il 70%
     */
    function keepMessagesListHeightUnder70() {
      if(calcMesssagesListSizeInPercentage() > 70) {
        self.closeLastElement();
        keepMessagesListHeightUnder70();
      }
    }
  }
}

class PopUpErrorLogElement extends HTMLDivElement {
  constructor () {
    super();
  }

  createdCallback() {
    var self = this;
    var PopUpErrorLogElement = this;
    this.innerHTML = require("html-loader!./pop-up-error-log-element.html");

    var popUpErrorLogElement = $(self);
    var message = null;

    var onCloseClick = function (element) {};

    popUpErrorLogElement.addClass("hide");

    popUpErrorLogElement.find(".header i").on("click", () => {
      onCloseClick($(self));
    });

    PopUpErrorLogElement.setMessage = function(newMessage) {
      message = newMessage;
      var messageArea = popUpErrorLogElement.children(".message");
      messageArea.html(newMessage);
    }
    
    PopUpErrorLogElement.getMessage = function() {
      return message;
    }

    PopUpErrorLogElement.onCloseClick = function(callback) {
      onCloseClick = callback;
    }
  }

  get message() {
    this.getMessage();
  }

  set message(message) {
    this.setMessage(message);
  }
}

var elementClassName = "pop-up-error-log-element";
PopUpErrorLogElement = document.registerElement(elementClassName, { 
  extends: "div",
  prototype: Object.create(PopUpErrorLogElement.prototype)
});

document.registerElement("pop-up-error-log", { 
  extends: "div",
  prototype: Object.create(PopUpErrorLog.prototype)
});