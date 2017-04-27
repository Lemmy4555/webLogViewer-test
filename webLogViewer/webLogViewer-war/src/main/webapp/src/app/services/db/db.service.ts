import { Logger } from 'Logger/logger';
import { File } from "Models/file.model";
import { Constants } from 'Util/constants';
import { FileCompleteJson } from 'Models/file-complete.json';
import { Injectable, OnInit } from '@angular/core';

/**
 * Classe Singleton che si occupa di chiamata l'indexedDB per le operazioni di CRUD
 */
@Injectable()
export class DbService {
  private logger: Logger = new Logger(this.constructor.name);
  private isConnected: boolean = false;
  private readonly dbName = Constants.DB_NAME;

  private IndxDb: IDBFactory;
  /** Rappresenta il DB */
  private db: IDBDatabase;

  constructor() {
    var DbManagerInstance = this;

    /** Callback richiamata nel momento in cui si effettua una connessione al DB */
    var isConnected = false;

  }

  public getFile = function (filePath: string) {
    if(!Constants.USE_CACHE_DB) {
      filePath = Constants.UNVALID_PATH;
    }
    this.logger.debug("Tentativo di lettura del file: %s", filePath);
    return this.db.transaction([DbFilesConstants.tableName]).objectStore(DbFilesConstants.tableName).get(filePath);
  };

  public putFile = function (data: FileCompleteJson) {
    this.logger.debug("Tentativo di update del file: %s", data.path);
    return this.db.transaction([DbFilesConstants.tableName], "readwrite").objectStore(DbFilesConstants.tableName).put(data);
  };

  public addFile = function (data: FileCompleteJson) {
    this.logger.debug("Tentativo di inserimento del file: %s", data.path);
    return this.db.transaction([DbFilesConstants.tableName], "readwrite").objectStore(DbFilesConstants.tableName).add(data);
  };

  /**
   * Si collega al database e chiama la callback in input
   */
  public connect(callback: () => void): DbService {
    if (this.isConnected) {
      this.logger.warn("La connessione al db e gia stata effettuata");
      return;
    }

    if (!Constants.USE_CACHE_DB) {
        this.logger.warn("USE_CACHE_DB e false, il db verra utilizzato per recuperare i file salvati, per qualsiasi chiamata di get" +
          " il path di input verra settato a: " + Constants.UNVALID_PATH);
    }
    //Tentativo di connessione al DB
    var request = window.indexedDB.open(this.dbName);
    request.onerror = (event) => {
      this.logger.error("Errore durante la connessione all\'indexedDB. Nome del db: %s", this.dbName);
    };
    request.onsuccess = (event: any) => {
      this.db = event.target.result;
      callback();
      return;
    };
    request.onupgradeneeded = (event: any) => {
      //Se il db non esiste o non e aggiornato, viene rigenerato.
      this.db = event.target.result;
      var objectStore = this.db.createObjectStore(DbFilesConstants.tableName, { keyPath: DbFilesConstants.pk });
      objectStore.createIndex(DbFilesConstants.pk, DbFilesConstants.pk, { unique: true });
    };
  }
}

/**
 * Enum con i dati della tabella "files" sull'IndexedDB
 */
class DbFilesConstants {
  public static readonly tableName: string = "files";
  public static readonly pk: string = "path";
}