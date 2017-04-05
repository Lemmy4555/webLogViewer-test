/**
 * Classe statica con costanti e metodi comuni a tutta la webapp
 */
class Globalz{
  constructor() {
    var self = this;
    var Globalz = this;

    Globalz.API_ROOT = __APIROOT__,
    Globalz.API_HOME = __APIROOT__ + "/" + __APICONTEXT__ + "/api/",
    Globalz.MAX_DB_CONTENT_STORAGE = 2,//1048576, 1MB
    Globalz.DB_NAME = 'webLogViewerDB',
    Globalz.UNREACHABLE_ERR = "Non e stata reperita alcuna risposta dalle API";

    
    /**
    * Trasforma l'oggetto arguments di un metodo in un array contenente i singoli valori
    * @param {IArguments} inputArgs parametri di input del funzione chiamante
    * @param {number} [startSlice=0] index da cui prendere i parametri in poi
    * @return Se il primo valore di inputArgs e un array e la lunghezza dell'array e di un elemento
    *         verra tornato l'array, altrimenti verranno tornati tutti gli elementi di inputArgs a partire
    *         dall'elemento in posizione "startSlice"
    */
    Globalz.varArgsToArray = function(inputArgs, startSlice) {
      inputArgs = Array.from(inputArgs);
      if(!inputArgs || !(inputArgs instanceof Array)) {
        return [];
      }
      startSlice = startSlice && typeof startSlice === "number" ? startSlice : 0;
      var args = Array.prototype.slice.call(inputArgs, startSlice);
      if(args[0].constructor === Array && args.length === 1) {
        return args[0];
      }
      return args;
    },
    
    /**
    * @param {string} str stringa in input
    * @param {length} length lughezza della stringa in output
    * @param {string} [charpad=0] carattere con cui riempire la stringa, se viene passta
                                  una stringa verra considerato solo il primo carattere charpad[0]
    */
    Globalz.leftPad = function(str, length, charpad) {
      str = str | "";
      str = new String(str);
      if(typeof length !== "number" && length < 1) {
        return str;
      }
      charpad = charpad | "0";
      if(charpad.length > 1) {
        charpad = charpad[0];
      }
      var pad = Array(length + 1).join(charpad);
      return pad.substring(0, length - str.length) + str;
    }

    /**
    * Gestione di default di errori ajax per cui ci e stato un problema
    * nel chiamare le API.
    * 
    * @param {string} message messaggio da loggare nel caso si sia verificato un errore
    *                         per cui non sono state chiamate le API
    * @param {string} error errore restituito dalla chiamata ajax
    * 
    * @return false se l'errore che si e verificato contiene una risposta da parte delle API,
    *         altrimenti torna il messaggio in input alimentato con le informazioni sull'errore
    */
    Globalz.ajaxUnreacheableErrorLogHandling = function(message, error) {
      var htmlMessage = message;
      var stdMessage = message;
      if(error.responseText) {
        return false;
      } else {
        var errorMessage = null;
        if(error.status != 0) {
          errorMessage = "Errore (" + error.status + ") " + error.statusText;
        } else {
          errorMessage = "Errore (" + error.status + ") Non e stato possibile raggiungere le API";
        }
        stdMessage += ". " + errorMessage;
        htmlMessage += ".<br><br>" + errorMessage;
        return {
          "std": stdMessage,
          "html": htmlMessage
        };
      }
    }

    Globalz.isAjaxUnreacheableError = function(error) {
      if(error.responseText) {
        return false;
      } else {
        return true;
      }
    }

    /** 
     * Converte il path in input
     * @param {string} path path da convertire
     */
    Globalz.unixPath = function(path) {
      return path.replace(/\\\\/g, "/").replace(/\\/g, "/");
    }
  }
}

module.exports = new Globalz();