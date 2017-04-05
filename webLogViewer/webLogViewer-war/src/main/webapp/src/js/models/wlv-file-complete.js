var WlvFile = require("Models/wlv-file.js");

/**
 * Rappresenta un file da leggere per la webapp
 */
module.exports = class WlvFileComplete extends WlvFile {
  constructor(filePath, readContent, rowsRead, fileSize, encoding, rowsInFileToSet, lastRowReadToSet) {
		super(filePath, readContent, rowsRead, fileSize, encoding);
		var self = this;
		var WlvFileComplete = this;

		/** Indica il numero di righe nel file */
		var rowsInFile = 0;

		/** Indica l'ultima riga letta nel file */
		var lastRowRead = 0;

		WlvFileComplete.setRowsInFile = function(toSet) {
			if(!toSet) {
				size = 0;
			}
			if(typeof toSet === "string") {
				rowsInFile = parsetInt(toSet);
			}
		}

		WlvFileComplete.setRowsInFile = function(toSet) {
			if(!toSet) {
				rowsInFile = 0;
				return;
			}
			if(typeof toSet === "string") {
				rowsInFile = parseInt(toSet);
			} else {
				rowsInFile = toSet
			}
		}

		WlvFileComplete.getRowsInFile = function() {
			return rowsInFile;
		}

		WlvFileComplete.setLastRowRead = function(toSet) {
			if(!toSet) {
				lastRowRead = 0;
				return;
			}
			if(typeof toSet === "string") {
				lastRowRead = parseInt(toSet);
			} else {
				lastRowRead = toSet
			}
		}

		WlvFileComplete.getLastRowRead = function() {
			return lastRowRead;
		}

		WlvFileComplete.toJson = function() {
			return {
				"path": self.path,
				"readContent": self.readContent,
				"size": self.size,
				"rowsRead": self.rowsRead,
				"encoding": self.encoding,
				"rowsInFile": self.rowsInFile,
				"lastRowRead": self.lastRowRead,
				"__class__": self.constructor.name
			};
		}

		self.setRowsInFile(rowsInFileToSet);

		self.setLastRowRead(lastRowReadToSet);
		if(lastRowRead > rowsInFile) {
			this.lastRowRead = rowsInFile;
		}
  }
	
	set rowsInFile(rowsInFile) {
		this.setRowsInFile(rowsInFile);
	}

	get rowsInFile() {
		return this.getRowsInFile();
	}

	set lastRowRead(lastRowRead) {
		this.setLastRowRead(lastRowRead);
	}

	get lastRowRead() {
		return this.getLastRowRead();
	}
}