import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'jrs-dropdown-input',
  template: `
    <label *ngIf="label" class="label-style">
      {{label}}
      <span *ngIf="required" class="required-star-style">*</span>
    </label>
    <div id="select-container" *ngIf="!skeletonLoader"
         [class]="[size, color]"
         [class.disabled]="disabled || load"
         [class.fit]="fitContent"
         [class.bottom-padding]="padding"
         [class.error-label]="getError()"
         [style.width]="width">
      <div class="select"
           [class.outlined]="outlined && !disabled"
           [class.loading]="load"
           [class.no-outline]="noOutline && !disabled"
           (click)="onClick()">
        <span class="default-value">
          {{value ? value : placeholder}}
        </span>
        <span *ngIf="!value && required" class="required-star-style">*</span>
        <div *ngIf="(!label && placeholder) &&!!value" class="move">
          <label class="placeholder-text-style">
            {{placeholder}}
            <span *ngIf="required" class="required-star-style">*</span>
          </label>
        </div>
        <ng-content select="[text]"></ng-content>
        <div class="left-side">
          <jrs-icon *ngIf="icon" class="icon" [class.hide-icon]="load" [class.open]="show" [mat]="true">{{icon}}</jrs-icon>
          <div *ngIf="load" class="loader">
            <jrs-loader [color]="'light'"></jrs-loader>
          </div>
        </div>
      </div>
      <ng-content></ng-content>
      <span *ngIf="getError() && errorText" class="error-style" aria-live="polite">&#9888; {{errorText}}</span>
    </div>
    <div class="loading" *ngIf="skeletonLoader">
      <div class="shimmer"></div>
    </div>
  `,
  styleUrls: ['./dropdown-input.component.scss']
})
export class DropdownInputComponent implements OnInit {
  @Input() formGroup = new FormGroup({empty: new FormControl('')});
  @Input() formType = "empty";
  @Input() value: string;
  @Input() placeholder: string;
  @Input() label: string;
  @Input() fitContent = false;
  @Input() errorText: string;
  @Input() icon: string;
  @Input() size = 'medium';
  @Input() color = 'primary';
  @Input() load: boolean;
  @Input() disabled: boolean;
  @Input() outlined: boolean;
  @Input() noOutline: boolean;
  @Input() show: boolean;
  @Input() width = "100%";
  @Input() padding: boolean;
  @Input() skeletonLoader: boolean;
  @Input() required: boolean;
  @Output() clicked = new EventEmitter<boolean>();

  ngOnInit(): void {
    this.formGroup.controls[this.formType].valueChanges.subscribe(value => {
      if (value) {
        this.value = value.name;
      }

    });
  }

  onClick(): void {
    this.clicked.emit();
  }

  getError(): boolean {
    return !this.formGroup.controls[this.formType].pristine && this.formGroup.controls[this.formType].invalid;
  }
}
