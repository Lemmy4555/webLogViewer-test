require("./messages-box.css");

class MessagesBox extends HTMLDivElement{
  constructor() {
    super();
  }

  createdCallback() {
    var self = this;
    var MessagesBox = this;
    this.innerHTML = require("html-loader!./messages-box.html");

    var messagesBox = $(self);
    var textArea = messagesBox.find(".message");

    MessagesBox.showMessage = function(message) {
      textArea.html($.parseHTML(message));
      messagesBox.addClass("visible");
    }
    
    MessagesBox.closeMessage = function() {
      textArea.html("");
      messagesBox.removeClass("visible");
    }
  }
}

module.exports = document.registerElement("messages-box", { 
  extends: "div",
  prototype: Object.create(MessagesBox.prototype)
});