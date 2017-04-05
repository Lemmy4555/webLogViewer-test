/**
 * Wrappa parte dei metodi di "console".
 * I metodi di log (log, info, warn, debug) sono stati "potenziati" aggiungendo all'inizio
 * della stringa loggata il nome dato all'istanza del Logger e la data in cui viene effettuato il log
 * in modo da avere un risultato simile a Log4J.
 * es:
 * 31/01/2017 17:57:27:451 DEBUG (TailFileJob): Il file e stato letto
 */
module.exports = class Logger {
  constructor(logName) {
    var self = this;
    var Logger = this;
    if(!logName) {
      throw new Error("Inserire il nome del logger");
    }

    Logger.group = function() {
      console.group();
    }

    Logger.groupEnd = function() {
      console.groupEnd();
    }
    
    Logger.debug = function(/*args...*/) {
      var args = Globals.varArgsToArray(arguments);
      if(args.length === 0) {
        return;
      }
      log("debug", args);
    }

    Logger.log = function(/*args...*/) {
      var args = Globals.varArgsToArray(arguments);
      if(args.length === 0) {
        return;
      }
      log("log", args);
    }

    Logger.info = function(/*args...*/) {
      var args = Globals.varArgsToArray(arguments);
      if(args.length === 0) {
        return;
      }
      log("info", args);
    }

    Logger.error = function(/*args...*/) {
      var args = Globals.varArgsToArray(arguments);
      if(args.length === 0) {
        return;
      }
      log("error", args);
    }

    Logger.warn = function(/*args...*/) {
      var args = Globals.varArgsToArray(arguments);
      if(args.length === 0) {
        return;
      }
      log("warn", args);
    }

    function log(level/*, args...*/) {
      var args = Globals.varArgsToArray(arguments, 1);

      if(typeof args[0] === "string") {
        var message = args[0];
        var nonMessageArgs = args.slice(1, args.length);
        writeLog(level, message, nonMessageArgs);
      } else {
        writeLog(level, "", args);
      }
    }

    function writeLog(level, message, params) {
      var params = [head(level) + message].concat(params);
      console[level].apply(null, params);
    }

    function findParamsSinceError(/*args...*/) {
      var args = Globals.varArgsToArray(arguments);

      var error = null;
      var params = [];
      args.forEach(function(e) {
        if(e instanceof Error) {
          error = e;
          return;
        } else {
          params.push(e);
        }
      });
      return {
        params: params,
        error: error
      }
    }

    function formatMessage(message/*, params...*/) {
      var params = Globals.varArgsToArray(arguments, 1);
      /* Inverto la stringa per utilizzare il negative lookahead perche il negative lookbehind
        non e supportato da js */
      var reversed = reverseString(message);
      var regex = /\}\{(?!(\\))/g
      var i = 0;
      params.forEach(function(e) {
        var match = regex.exec(reversed);
        if(!match) {
          return;
        }
        var reversedParam = reverseString(new String(params[i]));
        reversed = reversed.substr(0, match.index) + reversedParam + reversed.substr(match.index + 2, reversed.length);
        i++;
      });
      return reverseString(reversed);
    }

    function reverseString(str) {
      // Step 1. Use the split() method to return a new array
      var splitString = str.split(""); // var splitString = "hello".split("");
      // ["h", "e", "l", "l", "o"]
  
      // Step 2. Use the reverse() method to reverse the new created array
      var reverseArray = splitString.reverse(); // var reverseArray = ["h", "e", "l", "l", "o"].reverse();
      // ["o", "l", "l", "e", "h"]
  
      // Step 3. Use the join() method to join all elements of the array into a string
      var joinArray = reverseArray.join(""); // var joinArray = ["o", "l", "l", "e", "h"].join("");
      // "olleh"
      
      //Step 4. Return the reversed string
      return joinArray; // "olleh"
    }

    function date() {
      var d = new Date();
      return Globals.leftPad(d.getDate(), 2) + "/" + 
         Globals.leftPad((d.getMonth() + 1), 2) + "/" + Globals.leftPad(d.getFullYear(), 2) + " " +
         Globals.leftPad(d.getHours(), 2) + ":" + Globals.leftPad(d.getMinutes(), 2) + ":" + 
         Globals.leftPad(d.getSeconds(), 2) + ":" + Globals.leftPad(d.getMilliseconds(), 3);
    }

    function head(level) {
      return date() + " " + level.toUpperCase() + " (" + logName + "): ";
    }
  }
}