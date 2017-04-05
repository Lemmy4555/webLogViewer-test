/**
 * Rappresenta un file da leggere per la webapp
 */
module.exports = class WlvFile {
  constructor(path, readContent, rowsReadToSet, sizeToSet, encoding) {
		var self = this;
		var WlvFile = this;

		/** Percorso assoluto del file */
	  WlvFile.path = path;
		/** Contenuto letto dalle API */
	  WlvFile.readContent = readContent;
		/** Dimensione totale del file */
		var size = 0;
		/** Numero totale di righe lette */
	  var rowsRead = 0;
		/** Encoding file */
	  WlvFile.encoding = encoding;

		WlvFile.setSize = function(toSet) {
			if(!toSet) {
				size = 0;
				return;
			}
			if(typeof toSet === "string") {
				size = parseInt(toSet);
			} else {
				size = toSet;
			}
		}

		WlvFile.getSize = function() {
			return size;
		}

		WlvFile.setRowsRead = function(toSet) {
			if(!toSet) {
				rowsRead = 0;
				return;
			}
			if(typeof toSet === "string") {
				rowsRead = parseInt(toSet);
			} else {
				rowsRead = toSet
			}
		}

		WlvFile.getRowsRead = function(toSet) {
			return rowsRead;
		}

		WlvFile.toJson = function() {
			return {
				"path": self.path,
				"readContent": self.readContent,
				"size": size,
				"rowsRead": rowsRead,
				"encoding": self.encoding,
				"__class__": self.constructor.name
			};
		}
		
		self.setSize(sizeToSet);
		self.setRowsRead(rowsReadToSet);
  }

	set size(size) {
		this.setSize(size);
	}

	get size() {
		return this.getSize();
	}

	set rowsRead(rowsRead) {
		this.setRowsRead(rowsRead);
	}

	get rowsRead() {
		return this.getRowsRead();
	}

	get _json() {
		self.toJson();
	}
}