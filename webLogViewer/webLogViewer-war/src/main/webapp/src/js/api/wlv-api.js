/**
 * Classe statica che contiene tutti metodi per la comunicazione con le API.
 * Ogni metodo restituisce l'oggetto ajaxDeferred di jquery.
 * @static
 */
class WlvApi {
  constructor() {
	  var WlvApi = this;

    WlvApi.getTailText = function(filePath, rowsFromEnd, isLengthToGet) {
      var params = {
        'filePath': filePath,
        'rowsFromEnd': rowsFromEnd
      };
      if(isLengthToGet) {
        params["getLength"] = true;
      } else {
        params["getLength"] = false;
      }
      return $.ajax({
        url: Globals.API_HOME + "getTailText",
        method: "GET",
        cache: false,
        data: params
      });
    }

    WlvApi.getTextFromLine = function(filePath, lineFrom) {
      return $.ajax({
        url: Globals.API_HOME + "getTextFromLine",
        method: "GET",
        cache: false,
        data: {
          "filePath": filePath,
          "lineFrom": lineFrom
        }
      });
    }

    WlvApi.getTextFromPointer = function(filePath, pointer, isTotRowsToGet) {
      var params = {
        "filePath": filePath,
        "pointer": pointer
      };
      if(isTotRowsToGet) {
        params["isTotRowsToGet"] = true;
      } else {
        params["isTotRowsToGet"] = false;
      }
      return $.ajax({
        url: Globals.API_HOME + "getTextFromPointer",
        method: "GET",
        cache: false,
        data: params
      });
    }

    WlvApi.getFileData = function(filePath) {
      return $.ajax({
        url: Globals.API_HOME + "getFileData",
        method: "GET",
        cache: false,
        data: {
          "filePath" : filePath
        }
      });
    }

    WlvApi.getHomeDir = function(filePath) {
      return $.ajax({
        url: Globals.API_HOME + "getHomeDir",
        method: "GET",
        cache: true
      });
    }

    WlvApi.getFileList = function(path) {
      return $.ajax({
        url: Globals.API_HOME + "getFileList",
        method: "GET",
        cache: false,
        data: {
          "filePath" : path
        }
      });
    }

    WlvApi.getFullFile = function(path) {
      return $.ajax({
        url: Globals.API_HOME + "getFullFile",
        method: "GET",
        cache: false,
        data: {
          "filePath" : path
        }
      });
    }
  }
}

module.exports = new WlvApi();