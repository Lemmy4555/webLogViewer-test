package com.sc.l45.weblogviewer.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;
import javax.swing.filechooser.FileSystemView;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import com.sc.l45.weblogviewer.api.cache.CacheControlMgr;
import com.sc.l45.weblogviewer.api.config.RestPaths;
import com.sc.l45.weblogviewer.api.exceptions.FileTooBigException;
import com.sc.l45.weblogviewer.api.file.mgr.FileMgr;
import com.sc.l45.weblogviewer.api.mgr.ApiMgr;
import com.sc.l45.weblogviewer.api.responses.DefaultDirResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileDataResponse;
import com.sc.l45.weblogviewer.api.responses.FileListDataResponse;

@Path("")
public class RestApi {
    @Inject private ApiMgr apiMgr;
    @Inject private FileMgr fileMgr;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.GET_FILE_DATA)
    public Response getFileData(@QueryParam("filePath") String filePath, @Context UriInfo ui) {
        return new DefaultApiBehavior<FileDataResponse>(ui) {

            @Override
            void before() throws FileNotFoundException, IOException, FileTooBigException {
                fileMgr.checkFile(filePath);
            }

            @Override
            FileDataResponse api() {
                File file = new File(filePath);
                return new FileDataResponse(file.getName(), file.isFile());
            }
            
        }.call();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.GET_FILE_LIST)
    public Response getFileList(@QueryParam("filePath") String filePath, @Context UriInfo ui) {
        return new DefaultApiBehavior<FileListDataResponse>(ui) {

            @Override
            void before() throws FileNotFoundException, IOException, FileTooBigException {
                if (filePath == null || filePath.isEmpty()) {
                    throw new IllegalArgumentException("Il parametro path non e stato valorizzato");
                }
                fileMgr.checkFile(filePath);
            }

            @Override
            FileListDataResponse api() throws FileNotFoundException, IOException {
                return apiMgr.getFileList(filePath);
            }
            
        }.call();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.GET_HOME_DIR)
    public Response getHomeDir(@Context UriInfo ui) {
        return new DefaultApiBehavior<FileListDataResponse>(ui) {

            @Override
            void before() {
            }

            @Override
            FileListDataResponse api() throws Exception {
                File home = FileSystemView.getFileSystemView().getRoots()[0];
                String homePath = home.getAbsolutePath();
                FileListDataResponse fileListDataResponse = apiMgr.getFileList(homePath);
                return new DefaultDirResponse(homePath, fileListDataResponse);
            }
            
        }.call();
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.GET_TAIL_TEXT)
    public Response getTailText(@QueryParam("filePath") String filePath, @QueryParam("maxRowsToRead") String maxRowsToRead, 
            @QueryParam("getLength") boolean isLengthToGet, @QueryParam("pointer") long pointer,
            @Context Request request, @Context UriInfo ui) {
        Response response = new DefaultFileApiBehavior<FileContentResponse>(ui) {
            private int maxRowsToReadInner;
            private File file = new File(filePath);
            private long pointerInner;
            
            @Override
            FileContentResponse api() throws IOException {
                return apiMgr.getTailText(file, maxRowsToReadInner, pointerInner, isLengthToGet);
            }

            @Override
            void before() throws FileNotFoundException, IOException, FileTooBigException {
                fileMgr.checkFile(filePath);
                
                if (StringUtils.isEmpty(maxRowsToRead)) {
                    maxRowsToReadInner = 4;
                } else {
                    maxRowsToReadInner = Integer.parseInt(maxRowsToRead);
                }
                
                if(pointer < 0) {
                	pointerInner = 0;
                } else {
                	pointerInner = pointer;
                }
            }
            
            @Override
            CacheControlMgr cacheControl() throws Exception {
                return new CacheControlMgr(request, file, Integer.toString(maxRowsToReadInner));
            }
        }.call();
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.GET_TEXT_FROM_LINE)
    public Response getTextFromLine(@QueryParam("filePath") String filePath, @QueryParam("lineFrom") String lineFrom, 
            @Context Request request, @Context UriInfo ui) {
        return new DefaultFileApiBehavior<FileContentResponse>(ui) {
            private File file = new File(filePath);
            
            @Override
            FileContentResponse api() throws IOException {
                int lineFromInt = Integer.parseInt(lineFrom);
                return apiMgr.getTextFromLine(file, lineFromInt);
            }

            @Override
            void before() throws FileNotFoundException, IOException, FileTooBigException {
                fileMgr.checkFile(filePath);
            }
            
            @Override
            CacheControlMgr cacheControl() throws Exception {
                return new CacheControlMgr(request, file, lineFrom);
            }
        }.call();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.READ_FILE)
    public Response readFile(@QueryParam("filePath") String filePath, 
            @Context Request request, @Context UriInfo ui) {
        return new DefaultFileApiBehavior<FileContentResponse>(ui) {
            private File file = new File(filePath);
            
            @Override
            FileContentResponse api() throws IOException {
                return apiMgr.readFile(file);
            }

            @Override
            void before() throws FileNotFoundException, IOException, FileTooBigException {
                fileMgr.checkFile(filePath);
            }
            
            @Override
            CacheControlMgr cacheControl() throws Exception {
                return new CacheControlMgr(request, file);
            }
        }.call();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.GET_TEXT_FROM_POINTER)
    public Response getTextFromPointer(@QueryParam("filePath") String filePath, @QueryParam("pointer") String pointer,
            @QueryParam("isTotRowsToGet") String isTotRowsToGet, @Context Request request, @Context UriInfo ui) {
        return new DefaultFileApiBehavior<FileContentResponse>(ui) {
            private File file = new File(filePath);
            
            @Override
            FileContentResponse api() throws IOException {
                int pointerInt = Integer.parseInt(pointer);
                return apiMgr.getTextFromPointer(file, pointerInt, Boolean.parseBoolean(isTotRowsToGet));
            }

            @Override
            void before() throws FileNotFoundException, IOException, FileTooBigException {
                fileMgr.checkFile(filePath);
            }
            
            @Override
            CacheControlMgr cacheControl() throws Exception {
                return new CacheControlMgr(request, file, pointer);
            }
        }.call();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.GET_FULL_FILE)
    public Response getFullFile(@QueryParam("filePath") String filePath,
            @Context Request request, @Context UriInfo ui) {
        return new DefaultFileApiBehavior<FileContentResponse>(ui) {
            private File file = new File(filePath);
            
            @Override
            FileContentResponse api() throws IOException {
                return apiMgr.getTextFromLine(file, 0);
            }

            @Override
            void before() throws FileNotFoundException, IOException, FileTooBigException {
                fileMgr.checkFile(filePath);
            }
            
            @Override
            CacheControlMgr cacheControl() throws Exception {
                return new CacheControlMgr(request, file);
            }
        }.call();
    }

}
