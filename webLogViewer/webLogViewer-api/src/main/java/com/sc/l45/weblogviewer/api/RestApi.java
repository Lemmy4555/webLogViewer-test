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
            void before() throws FileNotFoundException, IOException {
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
            void before() throws FileNotFoundException, IOException {
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
            @QueryParam("isTotRowsToGet") String isTotRowsToGet, @QueryParam("pointer") String pointer,
            @Context Request request, @Context UriInfo ui) {
        Response response = new DefaultFileApiBehavior<FileContentResponse>(ui) {
            private Integer maxRowsToReadInt;
            private File file = new File(filePath);
            private long pointerLong;
            private boolean isTotRowsToGetInner;
            
            @Override
            FileContentResponse api() throws IOException {
                return apiMgr.getTailText(file, maxRowsToReadInt, pointerLong, isTotRowsToGetInner);
            }

            @Override
            void before() throws FileNotFoundException, IOException {
                fileMgr.checkFile(filePath);
                
                if (StringUtils.isEmpty(maxRowsToRead)) {
                    maxRowsToReadInt = null;
                } else {
                    maxRowsToReadInt = Integer.parseInt(maxRowsToRead);
                }
                
                if(StringUtils.isEmpty(pointer)) {
                	pointerLong = 0;
                } else {
                	pointerLong = Long.parseLong(pointer);
                }
                
                if(pointerLong < 0) {
                	pointerLong = 0;
                }
                
                if(isTotRowsToGet == null || isTotRowsToGet.equals("")) {
            		isTotRowsToGetInner = false;
            	} else {
            		isTotRowsToGetInner = Boolean.parseBoolean(isTotRowsToGet);
            	}
            }
            
            @Override
            CacheControlMgr cacheControl() throws Exception {
        		return new CacheControlMgr(request, file);
            }
        }.call();
        return response;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestPaths.RestApi.GET_TEXT_FROM_LINE)
    public Response getTextFromLine(@QueryParam("filePath") String filePath, @QueryParam("lineFrom") String lineFrom, 
    		@QueryParam("maxRowsToRead") String maxRowsToRead, @QueryParam("isTotRowsToGet") String isTotRowsToGet, @Context Request request, @Context UriInfo ui) {
        return new DefaultFileApiBehavior<FileContentResponse>(ui) {
            private File file = new File(filePath);
            private int lineFromInt;
            private Integer maxRowsToReadInt;
            
            @Override
            FileContentResponse api() throws IOException {
                return apiMgr.getTextFromLine(file, lineFromInt, maxRowsToReadInt, Boolean.parseBoolean(isTotRowsToGet));
            }
            
            @Override
            void before() throws FileNotFoundException, IOException {
                fileMgr.checkFile(filePath);

                if (StringUtils.isEmpty(lineFrom)) {
                	lineFromInt = 0;
                } else {
                	lineFromInt = Integer.parseInt(lineFrom);
                }
                
                if(lineFromInt < 0) {
                	lineFromInt = 0;
                }
                
                if(StringUtils.isEmpty(maxRowsToRead)) {
                	maxRowsToReadInt = null;
                } else {
                	maxRowsToReadInt = Integer.parseInt(maxRowsToRead);
                }
            }
            
            @Override
            CacheControlMgr cacheControl() throws Exception {
            	if(maxRowsToReadInt != null) {
            		return new CacheControlMgr(request, file, String.valueOf(lineFromInt), String.valueOf(maxRowsToReadInt));
            	} else {
            		return new CacheControlMgr(request, file, String.valueOf(lineFromInt));
            	}
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
            void before() throws FileNotFoundException, IOException {
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
    		@QueryParam("maxRowsToRead") String maxRowsToRead, @QueryParam("isTotRowsToGet") String isTotRowsToGet, @Context Request request, @Context UriInfo ui) {
        return new DefaultFileApiBehavior<FileContentResponse>(ui) {
            private File file = new File(filePath);
            private long pointerLong;
            private Integer maxRowsToReadInt;
            
            @Override
            FileContentResponse api() throws IOException {
                return apiMgr.getTextFromPointer(file, pointerLong, maxRowsToReadInt, Boolean.parseBoolean(isTotRowsToGet));
            }

            @Override
            void before() throws FileNotFoundException, IOException {
                fileMgr.checkFile(filePath);

                if (StringUtils.isEmpty(pointer)) {
                	pointerLong = 0;
                } else {
                	pointerLong = Long.parseLong(pointer);
                }
                
                if(pointerLong < 0) {
                	pointerLong = 0;
                }
                
                if(StringUtils.isEmpty(maxRowsToRead)) {
                	maxRowsToReadInt = null;
                } else {
                	maxRowsToReadInt = Integer.parseInt(maxRowsToRead);
                }
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
                return apiMgr.getTextFromLine(file, 0, null, true);
            }

            @Override
            void before() throws FileNotFoundException, IOException {
                fileMgr.checkFile(filePath);
            }
            
            @Override
            CacheControlMgr cacheControl() throws Exception {
                return new CacheControlMgr(request, file);
            }
        }.call();
    }

}
