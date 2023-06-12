import {Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'jrs-text-area',
  template: `
    <form [formGroup]="formGroup">
      <label *ngIf="!placeholder" class="label-style">
        {{label}}
        <span *ngIf="required" class="required-star-style">*</span>
      </label>
      <div class="container"
           [class.error-label]="getError() || error">
        <textarea [formControlName]="formType"
                  [placeholder]="placeholder? placeholder: ''"
                  [(ngModel)]="value"
                  (ngModelChange)="inputChange(value)"
                  (focusin)="movePlaceholder = true"
                  (focusout)="checkEmptyFields(value);!value? movePlaceholder = false: null"
                  class="textarea-style">
      </textarea>
        <span *ngIf="getError() && errorText" class="error-style" aria-live="polite">&#9888; {{errorText}}</span>
        <div *ngIf="!!value||movePlaceholder" class="move">
          <label class="placeholder-text-style move">
            {{placeholder}}
            <span *ngIf="required && !label" class="required-star-style">*</span>
          </label>
        </div>
        <span *ngIf="error && errorText" class="error-style" aria-live="polite">&#9888; {{errorText}}</span>
      </div>
    </form>
  `,
  styleUrls: ['./text-area.component.scss']
})
export class TextAreaComponent implements OnChanges {
  @Input() formGroup = new FormGroup({empty: new FormControl('')});
  @Input() formType = "empty";
  @Input() label: string;
  @Input() value: string;
  @Input() disabled: boolean;
  @Input() placeholder: string;
  @Input() required: boolean;
  @Input() errorText: string;
  @Input() error: boolean;
  @Input() size = 'medium';
  @Output() inputChanged = new EventEmitter();
  movePlaceholder: boolean;

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "disabled") {
        this.disableField();
      }
      if (prop == 'formType') {
        this.formGroup.controls[this.formType].valueChanges.subscribe(value => {
          this.value = value;
        });
      }
    }
  }

  inputChange(event): void {
    this.inputChanged.emit(event);
  }

  checkEmptyFields(value): boolean {
    if (this.required && (value === null || value === undefined || !value)) {
      return this.error = true;
    } else {
      return this.error = false
    }
  }

  getError(): boolean {
    return !this.formGroup.controls[this.formType].pristine && this.formGroup.controls[this.formType].invalid;
  }

  private disableField(): void {
    if (this.disabled) {
      this.formGroup.controls[this.formType].disable();
    } else {
      this.formGroup.controls[this.formType].enable();
    }
  }
}
