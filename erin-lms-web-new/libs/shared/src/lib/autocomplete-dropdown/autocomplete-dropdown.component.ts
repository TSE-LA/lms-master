import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild, ViewEncapsulation} from '@angular/core';
import {DropdownComponent} from "../dropdown/dropdown.component";
import {FormControl, FormGroup} from "@angular/forms";
import {map, startWith} from "rxjs/operators";
import {Observable} from "rxjs";
import {MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";

@Component({
  selector: 'jrs-autocomplete-dropdown',
  template: `
    <form [formGroup]="formGroup">
      <label *ngIf="label" class="autocomplete-label-style">
        {{label}}
      </label>
      <div class="autocomplete-input-container ">
        <div class="autocomplete-icon">
          <jrs-icon class="autocomplete-jrs-icon" [mat]="true" [size]="'medium-large'" [color]="'gray'">search</jrs-icon>
        </div>
        <input #text class="autocomplete-input"
               autocomplete="off"
               [formControlName]="formType"
               [class]=""
               [type]="'text'"
               [placeholder]="'Хайх'"
               [matAutocomplete]="auto">
      </div>
    </form>
    <mat-autocomplete autoActiveFirstOption #auto="matAutocomplete" (optionSelected)="optionSelected($event)" [displayWith]="getOptionText">
      <mat-option *ngFor="let option of filteredOptions | async" [value]="option">
        <div class="option">
          <span>{{option.name}}</span>
          <span *ngIf="option.groupPath">{{option.groupPath}}</span>
        </div>
      </mat-option>
    </mat-autocomplete>
  `,
  styleUrls: ['./autocomplete-dropdown.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AutocompleteDropdownComponent<T> implements OnChanges {
  @ViewChild('dropdown') dropdown: DropdownComponent;
  @Input() formGroup = new FormGroup({empty: new FormControl('')});
  @Input() formType = "empty";
  @Input() filterBy: string;
  @Input() defaultValue: T;
  @Input() load: boolean;
  @Input() values: T[] = [];
  @Input() label: string;
  @Input() placeholder: string;
  @Input() skeletonLoader: boolean;
  @Output() selectedValue = new EventEmitter<T>();
  filteredOptions: Observable<T[]>;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['values']) {
      this.filteredOptions = this.formGroup.controls[this.formType].valueChanges.pipe(
        startWith(''),
        map(value => {
            return this._filter(value);
          }
        ),
      );
    }
    if (changes["defaultValue"]) {
      this.formGroup.controls[this.formType].setValue(this.defaultValue);
    }
  }

  getOptionText(option: T): string {
    return option ? option['name'] : option;
  }

  optionSelected(selectedEvent: MatAutocompleteSelectedEvent): void {
    this.selectedValue.emit(selectedEvent.option.value);
  }

  private _filter(value): T[] {
    let filterValue = "";
    if (value && value[this.filterBy]) {
      filterValue = value[this.filterBy].toLowerCase();
    } else if (value) {
      filterValue = value.toLowerCase();
    }
    return this.values.filter(option => option[this.filterBy].toLowerCase().includes(filterValue));
  }
}
