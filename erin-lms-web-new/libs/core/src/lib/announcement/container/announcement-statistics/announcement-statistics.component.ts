import { Component, Input, OnInit } from "@angular/core";
import { STATISTICS_TABLE_COLUMNS } from "../../model/announcement.constants";
import { AnnouncementStatistic } from "../../model/announcement.model";

@Component({
  selector: "jrs-announcement-statistics",
  template: `
    <div class="inline-flex">
      <jrs-label [size]="'small'" [text]="date + ' | ' + author"></jrs-label>
      <span class="spacer"></span>
      <jrs-label [size]="'medium'" [text]="'Танилцсан хэрэглэгчид'"></jrs-label>
      <jrs-label
        [size]="'medium'"
        [text]="totalViewPercentage + ' %'"
      ></jrs-label>
    </div>

    <jrs-dynamic-table
      [tableColumns]="tableColumns"
      [dataSource]="statistics"
      [minWidth]="'unset'"
      [maxWidth]="'unset'"
      [notFoundText]="announcementNotFound"
    >
    </jrs-dynamic-table>
  `,
  styles: [],
})
export class AnnouncementStatisticsComponent implements OnInit {
  @Input() statistics: AnnouncementStatistic[] = [];
  @Input() date: string;
  @Input() author: string;
  @Input() totalViewPercentage: number;
  announcementNotFound = "Хэрэглэгч танилцаагүй байна.";
  tableColumns = STATISTICS_TABLE_COLUMNS;
  constructor() {}

  ngOnInit(): void {}

  close(): void {}

  save(): void {}
}
