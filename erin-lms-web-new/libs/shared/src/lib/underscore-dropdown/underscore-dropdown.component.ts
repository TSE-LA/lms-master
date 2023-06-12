import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'jrs-underscore-dropdown',
  template: `
    <form [formGroup]="formGroup">
      <mat-form-field class="{{className}}">
        <mat-select [formControlName]="formType" (selectionChange)="onEventSelect($event)" [required] = "required">
          <mat-option *ngFor="let value of values" [value] = "value">
            {{value}}
          </mat-option>
        </mat-select>
      </mat-form-field>
    </form>
  `,
  styleUrls: ['./underscore-dropdown.component.scss']
})
export class UnderscoreDropdownComponent implements OnInit {
  @Input() formGroup = new FormGroup({empty: new FormControl('')});
  @Input() formType = "empty";
  @Input() required = false;
  @Input() values = [];
  @Input() className = '';
  @Input() defaultValue = '';
  @Output() selectionChange = new EventEmitter<any>();

  ngOnInit(): void {
    this.formGroup.controls[this.formType].setValue(this.formGroup.controls[this.formType].value)
  }

  onEventSelect(event: any) {
    this.selectionChange.emit(event.value);
  }
}
