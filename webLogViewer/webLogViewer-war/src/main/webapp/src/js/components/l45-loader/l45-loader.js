require("./l45-loader.css");

class L45Loader extends HTMLDivElement{
  constructor() {
    super();
  }

  createdCallback() {
    var self = this;
    var L45Loader = this;
    this.innerHTML = require("html-loader!./l45-loader.html");

    var l45loader = $(self);

    L45Loader.showLoader = function(message) {
      l45loader.find(".loader").html(message);
      l45loader.addClass("visible");
    }
    
    L45Loader.hideLoader = function() {
      l45loader.find("loader").html("");
      l45loader.removeClass("visible");
    }
  }
}

module.exports = document.registerElement("l45-loader", {
  extends: "div",
  prototype: Object.create(L45Loader.prototype)
});