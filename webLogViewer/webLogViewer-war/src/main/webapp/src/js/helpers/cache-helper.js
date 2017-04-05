class CacheHelper {
  constructor() {
    var self = this;
    var CacheHelper = this;

    var keys = {
      lastOpenedFile: "last-opened-file"
    }

    CacheHelper.getLastOpenedFile = function() {
      return localStorage[keys.lastOpenedFile];
    }
    
    CacheHelper.setLastOpenedFile = function(lastOpenedFile) {
      localStorage[keys.lastOpenedFile] = lastOpenedFile;
    }
  }
}

module.exports = new CacheHelper();