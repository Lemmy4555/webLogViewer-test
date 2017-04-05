var WlvFile = require("Models/wlv-file.js");
var WlvFileComplete = require("Models/wlv-file-complete.js");

/**
 * Classe Singleton che si occupa di chiamata l'indexedDB per le operazioni di CRUD
 */
class DbManagerInstance {
  constructor() {
    var self = this;
    var DbManagerInstance = this;
    var logger = new Logger(self.constructor.name);

    /** Rappresenta il DB */
    var db = null;
    /** Callback richiamata nel momento in cui si effettua una connessione al DB */
    var isConnected = false;
    const dbName = Globals.DB_NAME;
    
    /**
     * Si collega al database e chiama la callback in input
     */
    DbManagerInstance.connect = function(callback) {
      if(isConnected) {
        logger.warn("La connessione al db e gia stata effettuata");
        return;
      }
      if(!callback || typeof callback !== 'function') {
        throw new TypeError("Per connettersi al DBIndexed e necessario fornire una callback")
      }
      //Tentativo di connessione al DB
      var request = indexedDB.open(dbName);
      request.onerror = function(event) {
        logger.error("Errore durante la connessione all\'indexedDB. Nome del db: %s", dbName);
      };
      request.onsuccess = function(event) {
        db = event.target.result;
        callback();
        return;
      };
      request.onupgradeneeded = function(event) {
        //Se il db non esiste o non e aggiornato, viene rigenerato.
        db = event.target.result;
        var objectStore = db.createObjectStore(DbFilesEnums.tableName, { keyPath: DbFilesEnums.pk });
        objectStore.createIndex(DbFilesEnums.pk, DbFilesEnums.pk, { unique: true });
      };
    };

    DbManagerInstance.getFile = function(filePath) {
      logger.debug("Tentativo di lettura del file: %s", filePath);
      return db.transaction([DbFilesEnums.tableName]).objectStore(DbFilesEnums.tableName).get(filePath);
    };

    DbManagerInstance.putFile = function(data) {
      logger.debug("Tentativo di update del file: %s", data.path);
      return db.transaction([DbFilesEnums.tableName], "readwrite").objectStore(DbFilesEnums.tableName).put(data.toJson());
    };

    DbManagerInstance.addFile = function(data) {
      logger.debug("Tentativo di inserimento del file: %s", data.path);
      return db.transaction([DbFilesEnums.tableName], "readwrite").objectStore(DbFilesEnums.tableName).add(data.toJson());
    };
  }
}

/**
 * Classe statica esposta verso l'esterno, l'unico suo metodo e db().
 * Il metodo db() permette di accedere all'istanza singleton del DbManager.
 */
class DbManager {
  constructor() {
    var self = this;

    var dbManagerInstance = null;

    /**
     * Restituisce l'istanza del DbManager
     */
    self.db = function() {
      if(dbManagerInstance) {
        return dbManagerInstance;
      }
      dbManagerInstance = new DbManagerInstance();
      return dbManagerInstance;
    }
  }
}

/**
 * Enum con i dati della tabella "files" sull'IndexedDB
 */
class _DbFilesEnums {
  constructor() {
    var _DbFilesEnums = this;

    _DbFilesEnums.tableName = "files";
    _DbFilesEnums.pk = "path"
  }
}
var DbFilesEnums = new _DbFilesEnums();

module.exports = new DbManager();