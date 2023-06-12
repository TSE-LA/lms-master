import {Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: ' jrs-checkbox',
  template: `
    <form [formGroup]="formGroup">
      <div [ngClass]="{'with-text': text, 'disabled': disabled}" [class.padding]="padding">
        <input type="checkbox"
               [class]="[color, size]"
               [class.no-outline]="noOutline"
               [checked]="check"
               [indeterminate]="indeterminate"
               [formControlName]="formType"
               [disabled]="disabled"
               (click)="this.clickedOnCheck()"/>
        <label *ngIf="text" class="checkbox-label">
          {{text}}
        </label>
      </div>
    </form>
  `,
  styleUrls: ['./checkbox-input.component.scss']
})
export class CheckboxInputComponent implements OnChanges {
  @Input() formGroup = new FormGroup({
    empty: new FormControl('')
  });
  @Input() formType = "empty";
  @Input() text: string;
  @Input() check: boolean;
  @Input() noOutline: boolean;
  @Input() disabled: boolean;
  @Input() indeterminate: boolean;
  @Input() padding = true;
  @Input() color = 'light';
  @Input() size = 'medium';
  @Output() checked = new EventEmitter();

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "formGroup") {
        this.formGroup.controls[this.formType].valueChanges.subscribe(res => {
          this.check = res;
        })
      }
      if (prop == "check") {
        this.formGroup.controls[this.formType].setValue(this.check);
      }
    }
  }

  clickedOnCheck(): void {
    this.check = !this.check;
    this.formGroup.controls[this.formType].setValue(this.check);
    this.checked.emit(this.check);
  }
}
