package com.sc.l45.weblogviewer.api;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sc.l45.weblogviewer.api.cache.CacheControlMgr;

/**
 * Si occupa di gestire la cache e la gestione degli errori per gli endpoint di lettura di un file.
 * @param <T>
 */
abstract class DefaultFileApiBehavior<T> extends DefaultApiBehavior<T> {
    private CacheControlMgr ccMgr;
    
    DefaultFileApiBehavior(UriInfo ui) {
       super(ui);
    }
    
    abstract CacheControlMgr cacheControl() throws Exception;
    
    @Override
    Response call() {
        EntityTag eTag;
        try {
            callStart();
            before();
            ccMgr = cacheControl();
            Response response304 = ccMgr.resources().getResponse304();
            if(response304 != null) {
                return response304;
            }
            eTag = ccMgr.resources().getEntityTag();
            T response = api();
            return Response.status(200).tag(eTag).entity(response).build();
        } catch (Exception e) {
            return ApiErrorUtil.jsonError(e.getMessage(), ui.getPath(), e);
        } finally {
            callEnd();
        }
    }
}