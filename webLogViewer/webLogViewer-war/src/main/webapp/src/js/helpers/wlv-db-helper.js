var DbManager = require('Db/db-manager.js');
var WlvFile = require('Models/wlv-file.js');

/**
 * Classe che si occupa di effettuare le chiamate al Db utilizzando il DbManager.
 * Eventuali controlli e operazioni aggiuntive sull'inserimento, sulla modifica o sulla lettura dei dati sono implementati
 * in questa classe statica
 * Per i metodi di update e inserimento, se la property readContent che rappresenta il contenuto di un file,
 * supera la dimensione massima di MAX_CONTENT_SIZE, il valore della property viene tagliato fino a raggiungere
 * la dimensione massima.
 * @static
 */
class WlvDbHelper {
  constructor () {
    var WlvDbHelper = this;
    var self = this;
    var logger = new Logger(self.constructor.name);

    /** 
     * Aggiorna il file in input sul DB con il nuovo valore di readContent, rowsRead e size
     * @param {WlvFile} file file con i dati aggiornati
     */
    WlvDbHelper.updateFile = function(file) {
      if(!(file instanceof WlvFile)) {
        throw new TypeError("Il file in input deve essere di tipo WlvFile");
      }
      var request = DbManager.db().getFile(file.path);
      request.onsuccess = function(event) {
        var result = request.result;
        if(!result) {
          logger.warn("Non e stato trovato nessun file salvato con il path: %s", file.path);
          return;
        }
        var fileRead = new WlvFile(result.path, result.readContent, result.rowsRead, result.size, result.encoding);
        fileRead.readContent = fileRead.readContent.concat(file.readContent);
        fileRead.rowsRead = fileRead.rowsRead + file.rowsRead;
        fileRead.size = file.size;
        var updateReq =  DbManager.db().putFile(fileRead);
        updateReq.onsuccess = function(event) {
          logger.debug("Aggiornato content sul db per il file: %s", fileRead.path);
        }
        updateReq.onerror = function(event) {
          logger.warn("Errore nell'update del file: %s", fileRead.path);
        }
      };
      request.onerror = function(event) {
        logger.warn("Errore nella lettura del file: %s", file.path);
      };
    }

    /** 
     * Inserisce il file in input sul DB
     * @param {WlvFile} file file con i dati da inserire
     */
    WlvDbHelper.addFile = function(file) {
      if(!(file instanceof WlvFile)) {
        throw new TypeError("Il file in input deve essere di tipo WlvFile");
      }
      file.readContent = file.readContent;
      var request = DbManager.db().addFile(file);
      request.onsuccess = function(event) {
        logger.debug('Inserito content su db per il file: ' + file.path);
      };
      request.onerror = function(event) {
        logger.warn("Errore nell'insert del file: " + file.path);
      };
    }
  }
}

module.exports = new WlvDbHelper();