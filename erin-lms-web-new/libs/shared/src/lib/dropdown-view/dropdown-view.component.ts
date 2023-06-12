import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'jrs-dropdown-view',
  template: `
    <div [class.bottom-padding]="padding">
      <jrs-dropdown
        [values]="values"
        [width]="'long'"
        [size]="size"
        [top]="'35px'"
        [moves]="true"
        [tooltip]="tooltip"
        [hasSuffix]="hasSuffix"
        (outsideClicked)="show = !show"
        (selectionChange)="onDropdownClick($event)">
        <jrs-dropdown-input [style]="style"
                            [size]="size"
                            [outlined]="outlined"
                            [color]="color"
                            [icon]="icon"
                            [load]="load"
                            [skeletonLoader]="skeletonLoader"
                            [show]="show"
                            [label]="label"
                            [errorText]="errorText"
                            [placeholder]="placeholder"
                            [disabled]="disabled"
                            [noOutline]="noOutline"
                            [width]="width"
                            [required]="required"
                            [value]="defaultValue"
                            [formGroup]="formGroup"
                            [formType]="formType"
                            (clicked)="toggleDropdown()">
        </jrs-dropdown-input>
      </jrs-dropdown>
    </div>

  `,
  styleUrls: ['./dropdown-view.component.scss']
})
export class DropdownViewComponent implements OnChanges {
  @Input() formGroup = new FormGroup({empty: new FormControl('')});
  @Input() formType = "empty";
  @Input() style: string;
  @Input() show: boolean;
  @Input() width = '100%'
  @Input() defaultValue: string;
  @Input() chooseFirst: boolean;
  @Input() errorText: string;
  @Input() icon = 'expand_more'
  @Input() size = 'medium'
  @Input() color = 'primary'
  @Input() load: boolean;
  @Input() disabled: boolean;
  @Input() outlined: boolean;
  @Input() noOutline: boolean;
  @Input() values: any[] = [];
  @Input() label: string;
  @Input() placeholder: string;
  @Input() padding: boolean;
  @Input() tooltip: boolean;
  @Input() skeletonLoader: boolean;
  @Input() required: boolean;
  @Input() hasSuffix = false;
  @Output() selectedValue = new EventEmitter<any>();

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "formType") {
        this.defaultValue = this.formGroup.controls[this.formType].value?.name;
      }
      if (prop == "values") {
        if (this.values && this.values.length > 0 && this.chooseFirst) {
          this.defaultValue = this.values[0].name;
          this.formGroup.controls[this.formType].setValue(this.values[0]);
        }
      }
    }
  }

  toggleDropdown(): void {
    this.show = !this.show;
  }

  onDropdownClick(value): void {
    this.defaultValue = value.name;
    this.show = !this.show;
    if (this.formType) {
      if (value.id === 'none') {
        this.formGroup.controls[this.formType].setValue('');
      } else {
        this.formGroup.controls[this.formType].setValue(value);
      }
    }
    this.selectedValue.emit(value);
  }
}
