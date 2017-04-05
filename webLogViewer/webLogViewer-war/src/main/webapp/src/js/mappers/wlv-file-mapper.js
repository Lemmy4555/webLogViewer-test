class WlvFileMapper {
  constructor() {
    var self = this;
    var WlvFileMapper = this;

    WlvFileMapper.mapVlwFile = function(json) {
      if(json.__class__ === WlvFile.constructor.name) {
        return new WlvFile(
          json.path,
          json.readContent,
          json.size,
          json.rowsRead,
          json.encoding
        );
      } else if(json.__class__ === WlvFileComplete.constructor.name) {
        return new WlvFileComplete(
          json.path,
          json.readContent,
          json.size,
          json.rowsRead,
          json.encoding,
          json.rowsInFile,
          json.lastRowRead
        );
      }
    }
  }
}

module.export = new WlvFileMapper();