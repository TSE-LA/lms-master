import {Component} from '@angular/core';
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../../../shared/src/lib/dialog/dialog-ref";
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";

@Component({
  selector: 'jrs-date-interval-dialog',
  template: `
    <div *ngIf="info" class="info-style">{{info}}</div>
    <div [class.filters]="withState">
      <jrs-date-interval-picker
        [month]="true" (startDateChange)="this.setStartDate($event)"
        (endDateChange)="this.setEndDate($event)"></jrs-date-interval-picker>
      <jrs-dropdown-view *ngIf="withState" class="state-style"
                         [label]="'Төрөл'"
                         [defaultValue]="selectedState ? selectedState.name : ''"
                         [chooseFirst]="true"
                         [outlined]="true"
                         [size]="'medium'"
                         [width]="'12vw'"
                         [icon]="'expand_more'"
                         [values]="timedCourseStates"
                         [hasSuffix]="true"
                         (selectedValue)="selectState($event)">
      </jrs-dropdown-view>
    </div>
    <jrs-action-buttons class="btn-style"
      [submitButton]="confirmText"
      [declineButton]="declineText"
      (submitted)="submit()" (declined)="decline()">
    </jrs-action-buttons>
  `,
  styleUrls: ['./date-interval-dialog.component.scss']
})
export class DateIntervalDialogComponent {
  info: string;
  startDate: string;
  endDate: string;
  confirmText: string;
  declineText: string;
  withState = false;
  timedCourseStates: DropdownModel[] = [
    {id: 'EXPIRED', name: 'ХУГАЦАА ДУУССАН'},
    {id: 'all', name: 'БҮГД'},
    {id: 'MAIN', name: 'ҮНДСЭН ҮЙЛЧИЛГЭЭ'},
    {id: 'CURRENT', name: 'ОДОО БАЙГАА'}
  ];
  selectedState = this.timedCourseStates[0];

  constructor(private config: DialogConfig, private dialog: DialogRef) {
    this.info = config.data.info;
    this.confirmText = config.data.confirmText ? config.data.confirmText : 'Үргэлжлүүлэх';
    this.declineText = config.data.declineText ? config.data.declineText: 'Цуцлах';
    this.withState = config.data.withState;
  }


  setStartDate(startDate: string): void {
    this.startDate = startDate;
  }

  setEndDate(endDate: string): void {
    this.endDate = endDate;
  }

  submit(): void {
    this.dialog.close({startDate: this.startDate, endDate: this.endDate, selectedState: this.selectedState.id});
  }

  decline(): void {
    this.dialog.close();
  }

  selectState(state: DropdownModel): void {
    this.selectedState = state;
  }
}
