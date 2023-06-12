import {Component} from '@angular/core';
import {DialogConfig} from "../dialog-config";
import {DialogRef} from "../dialog-ref";

@Component({
  selector: 'jrs-notes-dialog',
  template: `
    <div>{{bodyText}}</div>
    <jrs-text-area class="margin-bottom" (inputChanged)="getInput($event)"></jrs-text-area>
    <jrs-action-buttons (submitted)="submit()"
                        (declined)="close()"
                        [submitButton]="submitButton"
                        [declineButton]="'Болих'">
    </jrs-action-buttons>
  `,
  styleUrls: ['./notes-dialog.component.scss']
})
export class NotesDialogComponent {
  notes = "";
  bodyText: string;
  label: string;
  submitButton: string;

  constructor(public config: DialogConfig, public dialog: DialogRef) {
    this.bodyText = config.data.info;
    this.label = config.data.label;
    this.submitButton = config.data.submitButton;
  }

  close(): void {
    this.dialog.close(false);
  }

  submit(): void {
    this.dialog.close(this.notes);
  }

  getInput(value: string): void {
    this.notes = value;
  }
}
