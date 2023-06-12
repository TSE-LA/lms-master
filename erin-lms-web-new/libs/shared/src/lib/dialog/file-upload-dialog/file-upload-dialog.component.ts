import {Component} from '@angular/core';
import {DialogConfig} from "../dialog-config";
import {DialogRef} from "../dialog-ref";

@Component({
  selector: 'jrs-file-upload-dialog',
  template: `
    <div class="margin-bottom">{{this.info}}</div>
    <jrs-file-field
      [acceptFileTypes]="fileTypes"
      [acceptRegex]="allowedTypesRegex"
      (selectionChange)="changeFile($event)">
    </jrs-file-field>
    <jrs-action-buttons [submitButton]="submitButton"
                        [declineButton]="'Болих'"
                        [decline]="decline"
                        (submitted)="submit()"
                        (declined)="close()"></jrs-action-buttons>`,
  styleUrls: ['./file-upload-dialog.component.scss']
})
export class FileUploadDialogComponent {

  info: string;
  decline: boolean;
  attachment: File;
  attachmentMimeType: string;
  fileTypes: string;
  allowedTypesRegex: RegExp;
  submitButton: string;

  constructor(public config: DialogConfig, public dialog: DialogRef) {
    this.info = config.data.info;
    this.fileTypes = config.data.fileTypes ? config.data.fileTypes : "*";
    this.allowedTypesRegex = config.data.allowedRegexp;
    this.submitButton = config.submitButton? config.submitButton : "Үүсгэх";
    this.decline = this.config.decline;
  }


  submit(): void {
    this.dialog.close({status: true, file: this.attachment, mimetype: this.attachmentMimeType});
  }

  close(): void {
    this.dialog.close(false);
  }

  changeFile(file: File): void {
    this.attachment = file;
    this.attachmentMimeType = file.type;
  }
}
