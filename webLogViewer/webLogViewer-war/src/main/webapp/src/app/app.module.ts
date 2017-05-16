import { NgModule } from "@angular/core";
import { BrowserModule }  from "@angular/platform-browser";
import { HttpModule, JsonpModule } from '@angular/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from "./app.component";
import { FolderExplorer } from "Components/folder-explorer/folder-explorer.component";
import { FolderExplorerElement } from "Components/folder-explorer/element/folder-explorer-element.component";
import { FilePathInputText } from "Components/file-path-input-text/file-path-input-text.component";
import { L45Loader } from "Components/l45-loader/l45-loader.component";
import { MessagesBox } from "Components/messages-box/message-box.component";
import { PopUpErrorLog } from "Components/pop-up-error-log/pop-up-error.component";
import { FileViewer } from "Components/file-viewer/file-viewer.component";
import { ApiService } from 'Services/api/api.service';
import { DbService } from 'Services/db/db.service';
import { PopUpErrorLogElement } from './components/pop-up-error-log/element/pop-up-error-element.component';

import '../styles/styles.scss';
import { FilePathViewer } from './components/file-path-viewer/file-path-viewer.component';

@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    JsonpModule,
    BrowserAnimationsModule
  ],
  declarations: [
    AppComponent,
    FolderExplorer,
    FilePathInputText,
    L45Loader,
    MessagesBox,
    FolderExplorerElement,
    PopUpErrorLog,
    FileViewer,
    PopUpErrorLogElement,
    FilePathViewer
  ],
  providers: [
    ApiService,
    DbService
  ],
  bootstrap: [ 
    AppComponent
  ]
})
export class AppModule { }
