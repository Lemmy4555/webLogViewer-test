import { FileJson } from './file.json';
import { File } from './file.model';
import { FileCompleteJson } from './file-complete.json';
import { ToJson } from './to-json';
/**
 * Rappresenta un file da leggere per la webapp
 */
export class FileComplete extends File implements 
FileCompleteJson, ToJson<FileCompleteJson> {
  public rowsInFile: number;
  public lastRowRead: number;

  constructor(path: string, readContent: string,
    rowsRead: number, size: number, encoding: string,
    rowsInFile: number, lastRowRead?: number) {
      super(path, readContent, rowsRead, size, encoding);

    /** Indica il numero di righe nel file */
		this.rowsInFile = rowsInFile;

		/** Indica l'ultima riga letta nel file */
		this.lastRowRead = lastRowRead | rowsInFile;
  }

  public json(): FileCompleteJson {
    return <FileCompleteJson> this;
  }
}