# webLogViewer-test
Versione sperimentale del WebLogViewer scritta in JQuery/Javascript.

Web App che permette la visualizzazione di file di log su un server remoto.

Questa versione è stata scritta con lo scopo di provare le potenzialità di queste tecnologie lato client:

* [IndexedDB](https://developer.mozilla.org/en/docs/Web/API/IndexedDB_API)
* [HTMLCustomElements v1](https://developer.mozilla.org/en-US/docs/Web/Web_Components/Custom_Elements), [more here...](https://developer.mozilla.org/en-US/docs/Web/Web_Components/Custom_Elements)
* [HTMLCustomElements v0](https://www.w3.org/TR/custom-elements/)
* [Webpack](https://webpack.github.io/)

Lato back-end è stato sviluppato un servizio REST con JAX-RS, la lettura dei file su file system avviene tramite due versione customizzate di java.io.BufferedReader e org.apache.commons.io.input.ReversedLinesFileReader.

I JUnit test sono eseguiti trami Arquillian su una versione Embedded del container di Glassfish 3.

# Per iniziare

E possibile utilizzare l'applicazione sia sotto forma di EAR contenente sia il front-end che le API che sotto forma di due pacchetti separati, un war per le API e la parte front-end al di sotto di un HTTP Server esterno.

Per generare tutti i pacchetti eseguire questo comando nella directory principale.
`mvn clean install`

Se si desidera compilare il front-end singolarmente eseguire questi comandi sotto: /webLogViewer-war/src/main/webapp
`npm install
npm run build
lite-server start`
