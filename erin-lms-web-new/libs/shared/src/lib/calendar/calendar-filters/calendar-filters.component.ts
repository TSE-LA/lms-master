import {Component, EventEmitter, Output} from "@angular/core";
import {CALENDAR_FILTERS_STATE, CALENDAR_FILTERS_TYPE} from "../../shared-constants";

@Component({
  selector: 'jrs-calendar-filters',
  template: `
    <div class="container">
      <div class="p-10">
        <jrs-header-text [margin]="false" [size]="'small'">
          АНГИЛАЛ
        </jrs-header-text>
        <jrs-divider></jrs-divider>
        <div *ngFor="let item of type" class="filters-style">
          <jrs-checkbox [padding]="false" [size]="'m-small'" [check]="item.checked" [color]="item.id" (checked)="checked(item.id, $event)"></jrs-checkbox>
          <Text class="text-style">{{item.name}}</Text>
        </div>
      </div>

      <div class="p-10">
        <jrs-header-text [margin]="false" [size]="'small'">
          ТӨЛӨВ
        </jrs-header-text>
        <jrs-divider></jrs-divider>
        <div *ngFor="let item of state" class="filters-style">
          <jrs-checkbox [padding]="false" [size]="'m-small'" [color]="item.id" [check]="item.checked" (checked)="checked(item.id, $event)"></jrs-checkbox>
          <Text class="text-style">{{item.name}}</Text>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./calendar-filters.component.scss']
})
export class CalendarFiltersComponent {
  type = CALENDAR_FILTERS_TYPE;
  state = CALENDAR_FILTERS_STATE;
  @Output() onCheck = new EventEmitter();

  checked(id: string, isChecked: boolean) {
    this.onCheck.emit({ id, isChecked });
  }
}
