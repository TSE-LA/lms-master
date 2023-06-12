import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'jrs-input-field',
  template: `
    <form [formGroup]="formGroup" [class.bottom-padding]="padding" [class.no-edit]="noEdit">
      <label *ngIf="label" class="label-style">
        {{label}}
        <span *ngIf="required" class="required-star-style">*</span>
      </label>
      <div *ngIf="movePlaceholder"
           class="input-field"
           [class.error-label]="(getError() && required) || error || insideError"
           [class.move]="movePlaceholder && !label ">
        <input #text class="input-style" name="myInput" autocomplete="off"
               (focus)="checkEmptyFields()"
               [formControlName]="formType"
               (input)="onInputChange(text.value)"
               [type]="selectedType"
               [pattern]="pattern"
               [class]="[size]"
               [class.underline]="underline"
               [class.no-outline]="noOutline"
               [required]="required">
        <jrs-loader *ngIf="loading" [color]="'light'"></jrs-loader>

        <label class="label-text-style">
          {{placeholder}}
          <span *ngIf="required && !label" class="required-star-style">*</span>
        </label>
      </div>

      <div *ngIf="!movePlaceholder"
           class="input-field"
           [ngClass]="suffixIcon || prefixIcon? 'icon-input-config': ''">
        <div *ngIf="prefixIcon" class="icon">
          <jrs-icon [mat]="isMaterial" [size]="iconSize" [color]="iconColor">{{iconName}}</jrs-icon>
        </div>
        <input #text class="input-style"
               autocomplete="off"
               [formControlName]="formType"
               [class]="[size]"
               [pattern]="pattern"
               (focus)="checkEmptyFields()"
               (input)="onInputChange(text.value)"
               [type]="selectedType"
               [placeholder]="placeholder"
               [class.underline]="underline"
               [class.no-outline]="noOutline"
               [required]="required">
        <div *ngIf="suffixIcon" class="suffix-icon icon">
          <jrs-icon *ngIf="iconName" [mat]="isMaterial" [size]="iconSize" [color]="iconColor" (click)="suffixClicked()">{{iconName}}</jrs-icon>
          <jrs-loader *ngIf="loading" [color]="'light'"></jrs-loader>
        </div>
      </div>
      <span *ngIf="getError() && errorText" class="error-style" aria-live="polite">&#9888; {{errorText}}</span>
    </form>
  `,
  styleUrls: ['./input-field.component.scss']
})
export class InputFieldComponent implements OnChanges {
  @Input() formGroup = new FormGroup({
    inputField: new FormControl('')
  });
  @Input() formType = "inputField";
  @Input() label: string;
  @Input() value: any;
  @Input() disabled: boolean;
  @Input() placeholder: string = '';
  @Input() iconName: string;
  @Input() required: boolean;
  @Input() errorText: string;
  @Input() error = false;
  @Input() pattern: string;
  @Input() selectedType = 'text';
  @Input() movePlaceholder: boolean;
  @Input() isMaterial: boolean;
  @Input() iconColor = "gray";
  @Input() iconSize = 'medium-large';
  @Input() size = 'medium';
  @Input() style: string;
  @Input() prefixIcon: boolean;
  @Input() suffixIcon: boolean;
  @Input() underline: boolean;
  @Input() noOutline: boolean;
  @Input() padding = true;
  @Input() noEdit = false;
  @Input() loading = false;
  @Output() inputChanged = new EventEmitter();
  @Output() suffixClick = new EventEmitter();

  insideError: boolean;

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "disabled") {
        this.disableField();
      }
      if (prop == "value") {
        this.formGroup.controls[this.formType].setValue(this.value);
      }
    }
  }

  onInputChange(event): void {
    this.inputChanged.emit(event);
    this.value = event;
    this.checkEmptyFields();
  }

  checkEmptyFields(): void {
    this.insideError = this.formGroup.controls[this.formType].pristine && this.formGroup.controls[this.formType].invalid;
  }

  getError(): boolean {
    return this.error || this.insideError || (!this.formGroup.controls[this.formType].pristine && this.formGroup.controls[this.formType].invalid);
  }

  suffixClicked(): void {
    this.suffixClick.emit();
  }

  private disableField(): void {
    if (this.disabled) {
      this.formGroup.controls[this.formType].disable();
    } else {
      this.formGroup.controls[this.formType].enable();
    }
  }
}
