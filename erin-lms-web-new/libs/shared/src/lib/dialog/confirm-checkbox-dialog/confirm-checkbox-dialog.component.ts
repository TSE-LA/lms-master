import {Component} from '@angular/core';
import {DialogConfig} from "../dialog-config";
import {DialogRef} from "../dialog-ref";

@Component({
  selector: 'jrs-confirm-dialog',
  template: `
    <div>{{info}}</div>
    <span *ngIf="!hasUnited">
      <jrs-checkbox
        *ngIf="hasSms"
        [padding]="false"
        [check]="sendSms"
        [text]="'Смс мэдэгдэл илгээх'"
        (checked)="changeSms($event)">
      </jrs-checkbox>
      <jrs-checkbox
        [padding]="false"
        [check]="sendMail"
        [text]="'Имэйл мэдэгдэл илгээх'"
        (checked)="changeEmail($event)">
      </jrs-checkbox>
    </span>
    <span *ngIf="hasUnited">
      <jrs-checkbox
        [padding]="false"
        [check]="send"
        [text]="'Имэйл' + (hasSms? ', смс': '') + ' мэдэгдэл илгээх'"
        (checked)="changeSend($event)">
      </jrs-checkbox>
     </span>
    <jrs-action-buttons
      (submitted)="submit()"
      (declined)="close()"
      [submitButton]="submitButton"
      [declineButton]="declineButton"
      [decline]="decline">
    </jrs-action-buttons>
  `,
  styleUrls: ['./confirm-checkbox-dialog.component.scss']
})
export class ConfirmCheckboxDialogComponent {
  info: string;
  decline: boolean;
  submitButton: string;
  declineButton: string;
  sendSms: boolean;
  sendMail: boolean;
  send: boolean;
  hasSms = false;
  hasUnited = false;

  constructor(public config: DialogConfig, public dialog: DialogRef) {
    this.info = config.data.info;
    this.decline = this.config.decline;
    this.submitButton = this.config.submitButton ? this.config.submitButton : 'Тийм';
    this.declineButton = this.config.declineButton ? this.config.declineButton : 'Үгүй';
    this.hasSms = this.config.data.hasSms;
    this.hasUnited = this.config.data.hasUnited;
  }

  submit(): void {
    if (this.hasUnited) {
      this.dialog.close({send: this.send});
    } else {
      this.dialog.close({sendSms: this.sendSms, sendMail: this.sendMail});
    }
  }

  close(): void {
    this.dialog.close(false);
  }

  changeSms($event: boolean): void {
    this.sendSms = $event;
  }

  changeEmail($event: boolean): void {
    this.sendMail = $event;
  }

  changeSend($event: boolean): void {
    this.send = $event
  }
}
