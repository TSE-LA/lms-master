import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { TableColumn, TimedCourseModel } from '../../shared-model';
import { DropdownModel } from "../../dropdown/dropdownModel";
import { SortDirection } from "../model/table-constants";

@Component({
  selector: 'jrs-timed-course-table',
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
        <div class="column-filter" *ngFor="let column of tableColumns">
          <jrs-input-field
            [padding]="false"
            [placeholder]="' '"
            [selectedType]="'text'"
            (inputChanged)="filterColumn(column.id, $event)">
          </jrs-input-field>
        </div>
      </div>
      <div class="table" *ngIf="dataSource.length > 0">
        <div *ngFor="let data of this.dataToShow"
             [ngClass]="{'new-course':data.new}"
             class="timed-course-table-row"
             (mouseenter)="showContextButton(data, true)"
             (mouseleave)="showContextButton(data, false)">
          <div (click)="rowClick(data.id)" class="course-category">
            <div class="status">
              <div *ngIf="data.new" class="notification"></div>
            </div>
            <div class="category-name">{{data.categoryName}}</div>
          </div>
          <div (click)="rowClick(data)" class="keyword">{{data.keyword}}</div>
          <div (click)="rowClick(data)" class="code">{{data.code}}</div>
          <div (click)="rowClick(data)" class="course-name">{{data.name}}</div>
          <div (click)="rowClick(data)" class="start-date">{{data.startDate}}</div>
          <div (click)="rowClick(data)" class="end-date">{{data.endDate}}</div>
          <div (click)="rowClick(data)" class="author">{{data.author}}</div>
          <div (click)="rowClick(data)" class="created-date">{{data.createdDate}}</div>
          <jrs-menu id="{{data.id}}"
                    class="context-button"
                    *ngIf="hasMenu"
                    [contextActions]="contextActions"
                    (selectedAction)="selectAction(data, $event)"
                    (toggleMenu)="triggerContext(data)">
          </jrs-menu>
        </div>
      </div>
      <jrs-paginator *ngIf="dataSource.length > 0 && tableData !== null" class="paginator" [perPageNumber]="10" [contents]="tableData"
                     (pageClick)="changePage($event)"></jrs-paginator>
      <jrs-not-found-page [text]="notFoundText" [show]="dataSource.length === 0" [size]="'large'"></jrs-not-found-page>
    </div>`,
  styleUrls: ['./timed-course-table.component.scss']
})
export class TimedCourseTableComponent implements OnChanges, AfterViewInit {
  @Input() dataSource: TimedCourseModel[] = [];
  @Input() rowAction: DropdownModel;
  @Input() tableColumns: TableColumn[] = [];
  @Input() contextActions: DropdownModel[] = [];
  @Input() notFoundText: string;
  @Input() hasMenu: boolean;
  @Input() loading: boolean;
  @Output() getContextActions = new EventEmitter<TimedCourseModel>();
  @Output() selectedAction = new EventEmitter<any>();
  @Output() rowClicked = new EventEmitter<TimedCourseModel>()
  @Output() rowSelection = new EventEmitter<TimedCourseModel>()
  contextClicked: boolean;
  dataForFilter: TimedCourseModel[];
  tableData: TimedCourseModel[];
  dataToShow: TimedCourseModel[];
  filter = new Map();
  selectedCourse: TimedCourseModel;
  selectedHeader: string;
  unsortedData: any[];

  constructor(private cdRef: ChangeDetectorRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "dataSource") {
        if (this.dataSource != null) {
          this.tableData = this.dataSource;
          this.unsortedData = [...this.dataSource];
          this.dataForFilter = this.dataSource;
        }
      }
    }
  }

  ngAfterViewInit() {
    this.tableColumns.forEach(column => {
      column.sortDirection = SortDirection.NEUTRAL;
    });
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

  changePage(promos): void {
    this.dataToShow = promos;
    this.cdRef.detectChanges();
  }

  triggerContext(row: TimedCourseModel): void {
    this.contextClicked = true;
    this.selectedCourse = row;
    this.rowSelection.emit(row);
  }

  showContextButton(row: TimedCourseModel, show: boolean): void {
    this.getContextActions.emit(row);
    const element = document.getElementById(row.id);
    if (element) {
      if (show) {
        element.style.display = 'unset';
      } else if (!show && !this.contextClicked) {
        element.style.display = 'none';
      }
    }
  }

  selectAction(row: TimedCourseModel, action): void {
    this.contextClicked = false;
    const element = document.getElementById(row.id);
    if (element) {
      element.style.display = 'none';
      if (action) {
        this.selectedAction.emit(action);
      }
    }
  }

  rowClick(row): void {
    this.rowClicked.emit(row);
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
