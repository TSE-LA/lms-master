import {AfterViewInit, ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {DetailedLearnerActivity} from "../../../../../core/src/lib/dashboard/dashboard-model/dashboard-model";
import {TableColumn} from "../../shared-model";
import {SortDirection} from "../model/table-constants";

@Component({
  selector: 'jrs-learner-activity-table',
  template: `
    <div class="container" [class.loading]="loading">
      <div class="column-names">
        <div *ngFor="let column of tableColumns" class="sort" (click)="sortTable(column.id, column.sortDirection)">
          <span>{{column.name}}</span>
          <jrs-icon
            id="{{column.id + 'sort'}}"
            class="icon hidden"
            [mat]="true"
            [color]="'primary'">{{getIcon(column.sortDirection)}}
          </jrs-icon>
        </div>
      </div>
      <div class="column-filters">
        <div class="column-filter" *ngFor="let column of tableColumns" id="{{column.id+'-filter'}}">
          <jrs-input-field
            [padding]="false"
            [placeholder]="' '"
            [selectedType]="'text'"
            (inputChanged)="filterColumn(column.id, $event)">
          </jrs-input-field>
        </div>
      </div>
      <div class="table" *ngIf="dataToShow.length > 0">
        <div class="table-row" *ngFor="let data of dataToShow">
          <div class="table-column" *ngFor="let column of tableColumns" id="{{column.id}}">
            <div class="progress" *ngIf="column.id == 'status' && data[column.id] > 0" [ngStyle]="{'width':getWidth(data[column.id])}"></div>
            <span>{{column.id == 'status' ? data[column.id] + '%' : data[column.id]}}</span>
          </div>
        </div>
      </div>
      <jrs-not-found-page
        *ngIf="!loading && dataToShow.length <= 0"
        [text]="notFoundText"
        [size]="'small'"
        [show]="dataSource.length === 0">
      </jrs-not-found-page>
      <jrs-paginator *ngIf="dataSource.length > 0 && tableData !== null" class="paginator" [perPageNumber]="15" [contents]="tableData"
                     (pageClick)="changePage($event)"></jrs-paginator>
    </div>

  `,
  styleUrls: ['./learner-activity-table.component.scss']
})
export class LearnerActivityTableComponent implements OnChanges, AfterViewInit {
  @Input() dataSource: DetailedLearnerActivity[];
  @Input() tableColumns: TableColumn[] = [];
  @Input() notFoundText: string;
  @Input() loading: boolean;
  tableData: DetailedLearnerActivity[];
  dataForFilter: DetailedLearnerActivity[];
  dataToShow: DetailedLearnerActivity[];
  filter = new Map();
  selectedHeader: string;
  unsortedData: any[];

  constructor(private cdRef: ChangeDetectorRef) {
  }

  ngAfterViewInit() {
    this.tableColumns.forEach(column => {
      column.sortDirection = SortDirection.NEUTRAL;
    });
    this.cdRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "dataSource") {
        if (this.dataSource != null) {
          this.dataToShow = [];
          this.tableData = this.dataSource;
          this.unsortedData = [...this.dataSource];
          this.dataForFilter = this.dataSource;
        }
      }
    }
  }

  changePage(promos): void {
    this.dataToShow = promos;
    this.cdRef.detectChanges();
  }

  filterColumn(columnId: string, text: string): void {
    this.filter.set(columnId, text.toLowerCase());
    this.setFilters();
  }

  setFilters(): void {
    let tempDataForFilter = [...this.dataForFilter];
    for (const [key, value] of this.filter) {
      if (value != "") {
        tempDataForFilter = tempDataForFilter.filter(data => {
          if (typeof data[key] == 'string' && data[key].toLowerCase().includes(value)) {
            return true;
          } else {
            return !!(typeof data[key] == 'number' && data[key].toString().toLowerCase().includes(value));
          }
        })
      }
    }
    this.tableData = [...tempDataForFilter];
    this.unsortedData = [...tempDataForFilter];
    if (this.selectedHeader != null) {
      this.tableColumns.forEach(column => {
        if (this.selectedHeader == column.id) {
          column.sortDirection = SortDirection.NEUTRAL;
          document.getElementById(this.selectedHeader + 'sort').classList.add('hidden');
        }
      });
    }
  }

  getWidth(progress: number): string {
    return (document.getElementById('status').getBoundingClientRect().width * progress) / 100 - 20 + 'px'
  }

  sortTable(header: string, direction: SortDirection): void {
    if (this.selectedHeader != null && this.selectedHeader != header) {
      document.getElementById(this.selectedHeader + 'sort').classList.add('hidden');
      this.tableColumns.forEach(column => {
        if (column.id == this.selectedHeader) {
          column.sortDirection = SortDirection.NEUTRAL;
        }
      });
      document.getElementById(header + 'sort').classList.remove('hidden');
      this.selectedHeader = header;
      this.sort(direction);
    } else if (this.selectedHeader != null && this.selectedHeader == header) {
      this.sort(direction);
    } else {
      this.selectedHeader = header;
      document.getElementById(header + 'sort').classList.remove('hidden');
      this.sort(direction);
    }
  }

  sort(direction: SortDirection): void {
    const currentDirection = this.switchDirection(direction);
    this.tableColumns.forEach(column => {
      if (column.id == this.selectedHeader) {
        column.sortDirection = currentDirection;
      }
    });
    if (currentDirection === SortDirection.NEUTRAL) {
      this.tableData = this.unsortedData;
      document.getElementById(this.selectedHeader + 'sort').classList.add('hidden');
    } else {
      document.getElementById(this.selectedHeader + 'sort').classList.remove('hidden');
      this.tableData = [...this.unsortedData].sort((a, b) => {
        const isAsc = currentDirection === SortDirection.ASCENDING;
        return this.compare(a[this.selectedHeader], b[this.selectedHeader], isAsc);
      })
    }
  }

  compare(a: number | string, b: number | string, isAsc: boolean): any {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

  switchDirection(direction: SortDirection): SortDirection {
    switch (direction) {
      case SortDirection.NEUTRAL:
        return SortDirection.ASCENDING;
      case SortDirection.ASCENDING:
        return SortDirection.DESCENDING
      case SortDirection.DESCENDING:
        return SortDirection.NEUTRAL;
    }
  }

  getIcon(direction: SortDirection): string {
    switch (direction) {
      case SortDirection.ASCENDING: {
        return 'north';
      }
      case SortDirection.DESCENDING: {
        return 'south';
      }
      case SortDirection.NEUTRAL: {
        return 'north';
      }
    }
  }
}
