import {Component} from '@angular/core';
import {DialogConfig} from "../dialog-config";
import {DialogRef} from "../dialog-ref";

@Component({
  selector: 'jrs-confirm-dialog',
  template: `
    <div class="confirm-body" [innerHTML]="info"></div>
    <jrs-action-buttons
      (submitted)="submit()"
      (declined)="close()"
      [submitButton]="submitButton"
      [declineButton]="declineButton"
      [decline]="decline"></jrs-action-buttons>
  `,
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent {
  info: string;
  decline = true;
  submitButton: string;
  declineButton: string;

  constructor(public config: DialogConfig, public dialog: DialogRef) {
    this.info = config.data.info;
    this.decline = this.config.decline;
    this.submitButton = this.config.submitButton ? this.config.submitButton : 'Тийм';
    this.declineButton = this.config.declineButton ? this.config.declineButton : 'Үгүй';
  }



  submit(): void {
    this.dialog.close(true);
  }

  close(): void {
    this.dialog.close(false);
  }
}
