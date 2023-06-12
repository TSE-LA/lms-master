import {Component, EventEmitter, Output} from '@angular/core';
import {SearchModel} from "../shared-model";

@Component({
  selector: 'jrs-search-box',
  template: `
    <div class="container" [ngClass]="{'opened': isOpen}">
      <div class="input">
        <input [(ngModel)]="searchValue" placeholder="Хайх утгаа бичнэ үү"  (keyup.enter)="emitSearch()">
        <div class="icon" (click)="click()">
          <jrs-icon
            [mat]="true"
            [color]="'gray'"
            [size]="'large'">{{icon}}
          </jrs-icon>
        </div>
      </div>
      <div class="dropdown">
        <jrs-checkbox
          class="checkbox"
          [disabled]="byCategory"
          [padding]="false"
          [check]="byName"
          [text]="'Нэр'"
          (checked)="byName = !byName">
        </jrs-checkbox>
        <jrs-checkbox
          class="checkbox"
          [disabled]="byCategory"
          [padding]="false"
          [check]="byDescription"
          [text]="'Товч агуулга'"
          (checked)="byDescription = !byDescription">
        </jrs-checkbox>
        <jrs-checkbox
          class="checkbox"
          [disabled]="byName || byDescription"
          [padding]="false"
          [check]="byCategory"
          [text]="'Ангилал'"
          (checked)="byCategory = !byCategory">
        </jrs-checkbox>
      </div>
    </div>
    <jrs-overlay [transparent]="true" [show]="isOpen" (clicked)="isOpen = !isOpen"></jrs-overlay>
  `,
  styleUrls: ['./search-box.component.scss']
})
export class SearchBoxComponent {
  @Output() search = new EventEmitter<SearchModel>()
  isOpen = false;
  icon = 'search';
  byName = false;
  byDescription = false;
  byCategory = false;
  searchValue: any;


  click(): void {
    if (!this.isOpen) {
      this.isOpen = true;
    } else {
      this.emitSearch();
    }
  }

  emitSearch(): void {
    if (this.searchValue.length > 0) {
      this.search.emit({
        searchValue: this.searchValue,
        byCategory: this.byCategory,
        byDescription: this.byDescription,
        byName: this.byName
      });
    }
  }
}
