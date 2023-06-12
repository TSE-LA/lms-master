import {Component, EventEmitter, Input, Output} from '@angular/core';
import {LearnerInfo} from "../shared-model";

@Component({
  selector: 'jrs-header-with-filter',
  template: `
    <div class="header-container">
      <div class="row gx-5">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6 flex justify-start align-center">
          <div class="title">{{title}}</div>
          <div class="subtitle margin-left">{{subtitle}}</div>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6 flex flex-center">
          <div class="filters">
            <jrs-autocomplete-dropdown
              [filterBy]="filterBy"
              [values]="searchValues"
              (selectedValue)="filterUsersByName($event)">
            </jrs-autocomplete-dropdown>
            <jrs-dropdown-view
              class="role-dropdown"
              [values]="roles"
              [size]="'medium'"
              [defaultValue]="'Эрх'"
              [outlined]="true"
              [icon]="'expand_more'"
              (selectedValue)="filterUsersByRole($event)">
            </jrs-dropdown-view>
          </div>
        </div>
      </div>
    </div>`,
  styleUrls: ['./header-with-filter.component.scss']
})
export class HeaderWithFilterComponent {
  @Input() roles = [];
  @Input() filterBy: string;
  @Input() searchValues: any[];
  @Input() title: string;
  @Input() subtitle: string;
  @Output() filterByName = new EventEmitter<LearnerInfo>();
  @Output() filterByRole = new EventEmitter<string>();

  filterUsersByName(user: LearnerInfo): void {
    this.filterByName.emit(user);
  }

  filterUsersByRole($event: any): void {
    this.filterByRole.emit($event.name);
  }
}
