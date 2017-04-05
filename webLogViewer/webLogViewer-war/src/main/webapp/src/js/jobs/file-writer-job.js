/**
 * Job che si occupa di scrivere stringhe di grosse dimensione sul FileViewer, dividendo la stringa
 * in chunk di stringhe piu piccole
 */
module.exports = class FileViewerWriterJob {
  constructor(fileViewer) {
    var FileViewerWriterJob = this;
    var self = this;
    var logger = new Logger(self.constructor.name);

    /** setInterval che si occupa di scrivere sul file viewer */
    var job = null;
    /** coda di stringhe da scrivere nel file viewer */
    var queue = [];
    /** contatore per la seguente coda */
    var counter = 0;
    /** intervall di tempo tra una scrittura di un chunk ed un'altra */
    const SLEEP_TIME = 1000; //ms -> 1 secondo
    /** dimensione massima del chunk della stringa da scrivere sul viewer */
    const MAX_CHUNK_SIZE = 200; //200 righe

    /**
     * Scrive la stringa in input sul FileViewer, dividendo la stringa in chunk di grandezza uguale a MAX_CHUNK_SIZE.
     * Tra una scrittura di un chunk e l'altra interoccorre il tempo SLEEP_TIME
     * Se il job sta gia scrivendo una stringa, la stringa in input viene comunque divisa in chunk che vengono accodati
     * a quelli che il job sta gia scrivendo
     */
    FileViewerWriterJob.writeText = function(toWrite) {
      if(toWrite && typeof toWrite === "string") {
        toWrite = [toWrite];
      }
      if(!Array.isArray(toWrite)) {
        throw new TypeError('Il testo da scrivere deve essere una stringa o un array di stringhe');
      }
      logger.debug("Richiesto l\'inserimento di una stringa di %i righe", toWrite.length);
      toWrite = chunkArray(toWrite);
      queue = queue.concat(toWrite);
      logger.debug("Dopo aver inserito %i chunk in coda, ci sono %i chunk da scrivere", toWrite.length, queue.length);
      //Se il job non e gia in esecuzione ne avvia uno nuovo
      startNewJob();
    }

    var chunkArray = function(toChuck) {
      var result = [];
      var counter = 0;
      var chunk = null;
      toChuck.forEach((e) => {
        if(counter < MAX_CHUNK_SIZE) {
          if(chunk === null) {
            chunk = [];
          }
          chunk.push(e);
          counter++;
        } else {
          result.push(chunk);
          chunk = [];
          counter = 1;
          chunk.push(e);
        }
      });
      if(chunk) {
        result.push(chunk);
      }
      logger.debug("Il contenuto del file e stato diviso in %s chunk da %i righe l'uno", result.length, MAX_CHUNK_SIZE);
      return result;
    }

    /**
     * Interrompe il job se e in esecuzione e reinizializza tutte le variaibili per farlo ripartire da 0
     */
    FileViewerWriterJob.terminateJob = function() {
      if(job) {
        logger.debug("Si sta terminando il job con %i/%i elementi elaborati", counter, queue.length);
        clearInterval(job);
        job = null;
        counter = 0;
        queue = [];
        logger.debug("Il job e stato terminato");
      }
    }

    /**
     * Avvia un nuovo job se il job corrente non e gia in esecuzioe
     */
    var startNewJob = function() {
      if(!job) {
        logger.debug("Si sta avviando un nuovo job")
        job = setInterval(function() {
          var chunk = queue[counter];
          fileViewer.writeText(chunk);
          counter++;
          logger.debug("Elaborato elemento %i di %i elementi in coda", counter, queue.length);
          if(counter >= queue.length) {
            logger.debug("Il job ha elaborato tutti i %i elementi in coda e verra terminato", queue.length);
            self.terminateJob();
          }
        }, SLEEP_TIME);
      }
    }
  }
}