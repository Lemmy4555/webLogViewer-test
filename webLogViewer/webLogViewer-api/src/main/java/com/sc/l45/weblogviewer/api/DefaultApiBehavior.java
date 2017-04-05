package com.sc.l45.weblogviewer.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;

import com.sc.l45.weblogviewer.api.utils.Timer;
import com.sc.l45.weblogviewer.logging.LoggerFactory;

/**
 * Specifica i metodi fondamentali di un'endpoint. Le classi che estendono {@link DefaultApiBehavior}
 * devono gestire le eccezioni rilanciate dalla business logic delle api.
 * <br><br>
 * {@linkplain DefaultApiBehavior#before()}.
 * @param <T> La classe che rappresenta la Response in output.
 */
abstract class DefaultApiBehavior<T> {
    protected final static Logger logger = LoggerFactory.getApiCallsLogger();
    UriInfo ui;
    private Timer timer = new Timer();
    
    DefaultApiBehavior(UriInfo ui) {
       this.ui = ui;
    }
    
    /**
     * Rappresenta la logica di controllo che determina se la request e valida o no.
     * @throws Exception
     */
    abstract void before() throws Exception;
    
    /**
     * Rappresenta la logica di effettiva dell'API.
     * @throws Exception
     */
    abstract T api() throws Exception;

    Response call() {
        try {
            callStart();
            before();
            T response = api();
            return Response.status(200).entity(response).build();
        } catch (Exception e) {
            return ApiErrorUtil.jsonError(e.getMessage(), ui.getPath(), e);
        } finally {
            callEnd();
        }
    }

    protected void callEnd() {
        logger.info("Fine chiamata /{} in {}", ui.getPath(), timer.time());
    }

    protected void callStart() {
        logger.info("Inizio chiamata /{}", ui.getPath());
    }
}
