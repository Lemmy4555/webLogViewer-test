import { Component, ViewChild, ElementRef, EventEmitter, Output } from '@angular/core';
import { Logger } from 'Logger/logger';
import { MessagesBox } from 'Components/messages-box/message-box.component';

@Component({
  selector: "file-viewer",
  templateUrl: "./file-viewer.component.html",
  styleUrls: ["./file-viewer.component.css"]
})
export class FileViewer {
  private logger: Logger = new Logger(this.constructor.name);

  private _contentList: Array<string> = [];

  private isEndLineRegex: RegExp = /^[\r\n]+$/g;
  private hasEndLineRegex: RegExp = /.+[\r\n]+$/g;

  @ViewChild("textArea")
  private textArea: ElementRef;
  @ViewChild(MessagesBox)
  private messagesBox: MessagesBox;
  @ViewChild("contentWrapper")
  private contentWrapper: ElementRef;

  public showMessage(message: string) {
    this.messagesBox.showMessage(message);
  }

  public closeMessage() {
    this.messagesBox.closeMessage();
  }

  public writeText = function (content: Array<string>) {
    if (content && content.length > 0) {
      let html = this.convertChunkToHtml(content);
      $(this.textArea.nativeElement).append(html);
      this.logger.debug("Sono state scritte %i righe", content.length);
      this.scrollDown();
      this._contentList = this._contentList.concat(content);
    }
  }

  private convertChunkToHtml(chunk: Array<string>) {
    var html = "";
    chunk.forEach((e) => {
      if (this.isEndLine(e)) {
        html += "<br>";
        return;
      }
      html += "<span>" + e + "</span>";
      if (this.hasEndLine(e)) {
        html += "<br>";
      }
    });
    return html;
  }

  private hasEndLine(toCheck: string) {
    this.hasEndLineRegex.lastIndex = 0;
    return this.hasEndLineRegex.test(toCheck);
  }

  private isEndLine(toCheck: string) {
    var res = toCheck.match(this.isEndLineRegex);
    return res ? res[0] === toCheck : false;
  }

  public clear() {
    this.textArea.nativeElement.innerHTML = "";
    this._contentList = [];
  }

  public getContentList = function () {
    return this.contentList;
  }

  private scrollDown() {
    var n = this.contentWrapper.nativeElement.scrollHeight;
    $(this.contentWrapper.nativeElement).animate({ scrollTop: n }, 10);
  }

  get contentList() {
    return this._contentList;
  }
}
