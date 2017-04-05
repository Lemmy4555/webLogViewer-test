package com.sc.l45.weblogviewer.api.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

public class CacheControlMgr {
    
    private ResponseResources resources;
    
    public CacheControlMgr(Request request, File file, String... someSalt) throws IOException {
        BasicFileAttributes fileAttr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        CacheControl cc = new CacheControl();
        cc.setMaxAge(31536000);
        
        String creationDateHash = Integer.toString(fileAttr.creationTime().hashCode());
        String modifiedDateHash = Integer.toString(fileAttr.lastModifiedTime().hashCode());
        String filePathHash = Integer.toString(file.getAbsolutePath().hashCode());
        String someSaltHashCode = getHashCodeFromArrayOfString(someSalt);
        
        EntityTag eTag = new EntityTag(creationDateHash + modifiedDateHash + filePathHash + someSaltHashCode);
        
        ResponseBuilder builder = request.evaluatePreconditions(eTag);
        Response response304 = null;
        if(builder != null) {
            response304 = builder.cacheControl(cc).build();
        }
        
        resources = new ResponseResources(response304, eTag);
    }
    
    public ResponseResources resources() {
        return resources;
    }
    
    private String getHashCodeFromArrayOfString(String[] strings) {
        StringBuilder result = new StringBuilder();
        for(String string : strings) {
            result.append(Integer.toString(string.hashCode()));
        }
        return result.toString();
    }
    
    public class ResponseResources {
        private Response response304;
        private EntityTag eTag;
        
        private ResponseResources(Response response304, EntityTag eTag) {
            this.response304 = response304;
            this.eTag = eTag;
        }
        
        public Response getResponse304() {
            return response304;
        }
        
        public EntityTag getEntityTag() {
            return eTag;
        }
    }
}
