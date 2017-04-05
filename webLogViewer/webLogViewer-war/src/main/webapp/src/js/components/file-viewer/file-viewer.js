require("./file-viewer.css");
var MessagesBox = require("Components/messages-box/messages-box.js");

class FileViewer extends HTMLDivElement{
  constructor () {
    super();
  }
  
  createdCallback() {
    var self = this;
    var FileViewer = this;
    self.innerHTML = require("html-loader!./file-viewer.html");

    var logger = new Logger(self.constructor.name);
    var textArea = $(self).find("code");
    var messagesBox = $(self).find("[is=messages-box]")[0];

    /** Lista di chunk scritti a video */
    var contentList = [];

    var isEndLineRegex = /^[\r\n]+$/g;
    var hasEndLineRegex = /.+[\r\n]+$/g;

    FileViewer.showMessage = function(message) {
      messagesBox.showMessage(message);
    }
    
    FileViewer.closeMessage = function() {
      messagesBox.closeMessage();
    }

    FileViewer.writeText = function(content) {
      if(content && content.length > 0) {
        var html = convertChunkToHtml(content);
        textArea.append(html);
        logger.debug("Sono state scritte %i righe", content.length);
        scrollDown();
        contentList.push(content);
      }
    }

    function convertChunkToHtml(chunk) {
      var html = "";
      chunk.forEach((e) => {
        if(isEndLine(e)) {
          html += "<br>";
          return;
        }
        html += "<span>" + e + "</span>";
        if(hasEndLine(e)) {
          html += "<br>";
        }
      });
      return $.parseHTML(html);
    }

    function hasEndLine(toCheck) {
      hasEndLineRegex.lastIndex = 0;
      return hasEndLineRegex.test(toCheck);
    }

    function isEndLine(toCheck) {
      var res = toCheck.match(isEndLineRegex);
      return res ? res[0] === toCheck : false;
    }

    FileViewer.clear = function() {
      textArea.html("");
      contentList = [];
    }

    FileViewer.getContentList = function() {
      return contentList;
    }
    
    function scrollDown() {
      var n = textArea.height();
      $(self).find(".content-wrapper").animate({ scrollTop: n }, 10);
    }
  }

  get contentList () {
    return this.getContentList();
  }
  
}

module.exports = document.registerElement('file-viewer', { 
  extends: 'div',
  prototype: Object.create(FileViewer.prototype)
});