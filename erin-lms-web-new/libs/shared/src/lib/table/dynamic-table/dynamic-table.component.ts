import {AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {TableColumn, TableColumnType} from "../../shared-model";
import {SortDirection} from '../model/table-constants';

@Component({
  selector: 'jrs-dynamic-table',
  template: `
    <div class="container"
         [class.loading]="loading"
         [style.min-width]="minWidth"
         [style.height]="height">
      <jrs-scroll [height]="dataToShow.length > 0 ? tableMinHeight : '150px'" [color]="dataToShow.length > 0 ? 'secondary' : 'transparent'" [size]="'medium'" [horizontalScrollEnabled]="true">
        <table border="0" cellspacing="0" cellpadding="0" class="table-style" [class.freeze]="freezeFirstColumn && !loading">
          <thead class="table-header">
          <tr>
            <th *ngFor="let header of tableColumns" class="th sort" [style.width]="header.width"
                [class.firstChild]="freezeFirstColumn && header.type !== STATUS && header.type !== ICON && header.type !==  BOOLEAN && header.type !== ACTION && !loading">
              <div class="header" [class.flex]="!isFilterShow(header.type)">
                  <div  *ngIf="header.type ===  BOOLEAN && !loading" class="checkbox-style">
                    <jrs-checkbox
                      class="checkbox"
                      [disabled]="header.disabled"
                      [padding]="false"
                      [check]="allItemsSelected"
                      (checked)="checkAllItem($event, header.id)">
                    </jrs-checkbox>
                  </div>
                  <div *ngIf="header.type ===  STATUS && !loading">
                    <jrs-icon class="icon header-icon-padding"
                              [size]="'medium'"
                              [name]="'done'"
                              [mat]="true"
                              [color]="'transparent'">
                    </jrs-icon>
                  </div>
                  <div class="flex" *ngIf="header.type !==  BOOLEAN && !loading" (click)="sortTable(header.id, header.sortDirection)">
                    {{header.name}}
                    <jrs-icon *ngIf="isOrderShow(header.type)"
                              id="{{header.id + 'sort'}}"
                              class="icon hidden"
                              [mat]="true"
                              [color]="'primary'">{{getIcon(header.sortDirection)}}
                    </jrs-icon>
                  </div>
              </div>
              <div>
                <jrs-input-field class="column-filter"
                                 *ngIf="isFilterShow(header.type)"
                                 id="{{header.id+'-filter'}}"
                                 [padding]="false"
                                 [selectedType]="'text'"
                                 (inputChanged)="filterColumn(header.id, $event)"
                                 [attr.data-test-id]="'column-filter-' + header.id">
                </jrs-input-field>
                <ng-container *ngIf="header.action && checkedData.length > 0">
                  <div class="flex icons-margin">
                    <jrs-circle-button
                      *ngFor="let action of contextActions"
                      [jrsTooltip]="action.name"
                      [size]="'medium'"
                      [isMaterial]="true"
                      [iconName]="action.icon"
                      [color]="'none'"
                      [iconColor]="'primary'"
                      (clicked)="onHeaderSelected(action)">
                    </jrs-circle-button>
                  </div>
                </ng-container>
              </div>
            </th>
          </tr>
          </thead>
          <tbody *ngIf="dataToShow.length > 0 && !loading"
                [attr.data-test-id]="'report-dynamic-table-body'">
          <tr id="tableRowId" *ngFor="let data of dataToShow; let index = index" class="border-bottom row-style"
              [class.highlight]="data.upcoming" (dblclick)="doubleClicked(data)"
              (click)="clicked(data)"
              [attr.data-test-id]="'table-row-' + index">
            <td *ngFor="let header of this.tableColumns" class="td" id="{{header.id}}">
              <div *ngIf="header.type ===  BOOLEAN && !loading">
                <jrs-checkbox
                  class="checkbox"
                  [padding]="false"
                  [disabled]="header.disabled"
                  [check]="data[header.id]"
                  (checked)="selectItem($event, data, header.id)">
                </jrs-checkbox>
              </div>
              <jrs-input-field
                *ngIf="header.type ===  INPUT && !loading"
                [placeholder]="' '"
                [selectedType]="'number'"
                [padding]="false"
                [size]="'small'"
                [value]="data[header.id]"
                (inputChanged)="data[header.id] = $event">
              </jrs-input-field>
              <span *ngIf="header.type ===  ORDERED_NUMBER && !loading">
                {{data.id ? getOrderNumber(data.id) : (index + 1)}}
              </span>
              <span *ngIf="isDataShow(header.type)" [attr.data-jrs-table-cell-data]="data[header.id]"
                    jrsTooltip="{{data[header.id]}}">
                {{data[header.id]}}
              </span>
              <div *ngIf="header.type === ACTION && !loading">
                <jrs-circle-button
                  [size]="'medium'"
                  [isMaterial]="true"
                  [matTooltip]="header.tooltip"
                  [iconName]="header.iconName"
                  [color]="header.backgroundColor? header.backgroundColor:'gray'"
                  [iconColor]="header.iconColor? header.iconColor:'light'"
                  (clicked)="this.performAction(index, data)">
                </jrs-circle-button>
              </div>
              <div *ngIf="header.type ===  STATUS && data[header.id] && !loading">
                <jrs-circle-button
                  [size]="'medium'"
                  [isMaterial]="true"
                  [iconName]="header.iconName"
                  [color]="header.backgroundColor? header.backgroundColor:'gray'"
                  [iconColor]="header.iconColor? header.iconColor:'light'"
                  (clicked)="this.performAction(index, data)">
                </jrs-circle-button>
              </div>

              <div *ngIf="header.type ===  ICON && data[header.id] && !loading">
                <jrs-circle-button
                  [size]="'medium'"
                  [isMaterial]="true"
                  [iconName]="data[header.id].iconName"
                  [color]="'none'"
                  [iconColor]="data[header.id].iconColor || 'gray'"
                  (clicked)="this.performAction(index, data)">
                </jrs-circle-button>
              </div>

              <div *ngIf="header.type === CONTEXT &&
                          !loading && !(checkedData.length > 0) && hasAction">
                <jrs-menu id="{{data.id}}"
                          class="context-button"
                          (click)="rowSelect(data)"
                          [noCircle]="noCircle"
                          [contextActions]="contextActions"
                          (selectedAction)="onActionSelect(data.id, data.status, $event)">
                </jrs-menu>
              </div>

              <div *ngIf="header.type === NAVIGATE && !loading">
                <jrs-button [iconName]="'timeline'"
                            [size]="'medium'"
                            [color]="'dark-disabled'"
                            [iconSize]="'medium-large'"
                            [padding]="true"
                            (clicked)="this.performAction(index, data)">
                </jrs-button>
              </div>

              <div *ngIf="header.id === 'statisticSurvey' && !loading">
                <jrs-button
                  *ngIf="header.type === SURVEY_DOWNLOAD"
                  [isMaterial]="true"
                  [iconColor]="'light'"
                  [disabled]="!data.doneSurvey"
                  [size]="'icon-medium-responsive'"
                  [title]="'ТАТАХ'"
                  (clicked)="this.performAction(index, data)">
                </jrs-button>
                <p *ngIf="header.type !== SURVEY_DOWNLOAD">
                  {{data.doneSurvey ? 'Бөглөсөн' : 'Бөглөөгүй'}}
                </p>
              </div>
              <div *ngIf="header.id === 'statisticFeedback' && !loading">
                <jrs-button
                  *ngIf="header.type === SHOW_FEEDBACK"
                  [isMaterial]="true"
                  [iconColor]="'light'"
                  [disabled]="!data.hasFeedback"
                  [size]="'icon-medium-responsive'"
                  [title]="'ХАРАХ'"
                  (clicked)="this.performAction(index, data)">
                </jrs-button>
              </div>
              <div *ngIf="header.type === PROGRESS && !loading" class="progress-bar">
                <div class="progress"
                     jrsTooltip="{{getTooltip(data[header.id])}}"
                     [class.border-radius]="getDisplayData(data[header.id]) < 80"
                     [style.display]=getDisplayAttribute(data[header.id])
                     [style.width]=getWidth(data[header.id])>
                </div>
                <span jrsTooltip="{{getTooltip(data[header.id])}}">
                  {{getDisplayData(data[header.id])}}
                </span>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </jrs-scroll>
      <jrs-not-found-page
        *ngIf="!loading && dataToShow.length <= 0"
        [text]="notFoundText"
        [size]="'small'"
        [show]="dataSource.length === 0">
      </jrs-not-found-page>
      <div class="paginator">
        <jrs-paginator *ngIf="dataSource.length > 0 && tableData !== null"
                       [perPageNumber]="dataPerPage"
                       [contents]="tableData"
                       (pageClick)="updateDataToShow($event)"
                       (pageNumberChange)="changePageNumber($event)">
        </jrs-paginator>
      </div>
    </div>
  `,
  styleUrls: ['./dynamic-table.component.scss']
})
export class DynamicTableComponent implements OnChanges, AfterViewInit {
  @Input() dataSource: any[];
  @Input() tableColumns: TableColumn[] = [];
  @Input() notFoundText: string;
  @Input() loading: boolean;
  @Input() contextActions: any[] = [];
  @Input() maxWidth = '70vw';
  @Input() minWidth = '65vw';
  @Input() minHeight = '52vh';
  @Input() height = '52vh';
  @Input() tableMinHeight = 'unset';
  @Input() tableMinWidth = 'unset';
  @Input() hasLoader = true;
  @Input() dataPerPage = 10;
  @Input() freezeFirstColumn = false
  @Input() noCircle = false;
  @Input() role: string;
  @Input() hasAction = true;

  @Output() clickOnRow = new EventEmitter<any>();
  @Output() doubleClickOnRow = new EventEmitter<any>();
  @Output() rowAction = new EventEmitter<any>();
  @Output() rowSelected = new EventEmitter<any>();
  @Output() selectedAction = new EventEmitter<any>();
  @Output() selectedHeaderAction = new EventEmitter<any>();
  @Output() checkboxAction = new EventEmitter<any>();
  @Output() actionRow = new EventEmitter<any>();


  readonly ACTION = TableColumnType.ACTION;
  readonly BOOLEAN = TableColumnType.BOOLEAN;
  readonly INPUT = TableColumnType.INPUT;
  readonly CONTEXT = TableColumnType.CONTEXT;
  readonly STATUS = TableColumnType.STATUS;
  readonly NAVIGATE = TableColumnType.NAVIGATE;
  readonly PROGRESS = TableColumnType.PROGRESS;
  readonly SURVEY_DOWNLOAD = TableColumnType.SURVEY_DOWNLOAD;
  readonly SHOW_FEEDBACK = TableColumnType.SHOW_FEEDBACK;
  readonly ORDERED_NUMBER = TableColumnType.ORDERED_NUMBER;
  readonly ICON = TableColumnType.ICON;

  tableData: any[];
  dataForFilter: any[];
  dataToShow: any[];
  checkedData: any[] = [];
  filter = new Map();
  selectedHeader: string;
  unsortedData: any[];
  allItemsSelected: boolean;
  currentPage = 1;

  constructor(private cdRef: ChangeDetectorRef) {
  }

  ngAfterViewInit() {
    this.tableColumns.forEach(column => {
      column.sortDirection = SortDirection.NEUTRAL;
    });
    this.cdRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["dataSource"]) {
      if (this.dataSource != null) {
        this.dataToShow = [];
        this.tableData = [...this.dataSource];
        this.unsortedData = [...this.dataSource];
        this.dataForFilter = [...this.dataSource];
        this.setFilters();
        this.cdRef.detectChanges();
      }
    }
  }

  updateDataToShow(data): void {
    this.dataToShow = data;
    this.cdRef.detectChanges()
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

  filterColumn(columnId: string, text: string): void {
    this.filter.set(columnId, text.toLowerCase());
    this.setFilters();
  }

  clicked(data: any): void {
    this.clickOnRow.emit(data)
  }

  doubleClicked(data: any): void {
    this.doubleClickOnRow.emit(data)
  }

  performAction(index, data) {
    const dataIndex = index + (this.dataPerPage * (this.currentPage - 1));
    this.rowAction.emit({dataIndex, data});
  }

  onActionSelect(id: string, status: string, action): void {
    if (action) {
      this.selectedAction.emit({id, status, action});
    }
  }

  isDataShow(type: TableColumnType): boolean {
    return type !== this.ACTION &&
      type !== this.CONTEXT &&
      type !== this.STATUS &&
      type !== this.BOOLEAN &&
      type !== this.INPUT &&
      type !== this.PROGRESS &&
      type !== this.ICON &&
      type !== this.NAVIGATE;
  }

  isOrderShow(type: TableColumnType): boolean {
    return type !== this.ACTION &&
      type !== this.CONTEXT &&
      type !== this.STATUS &&
      type !== this.BOOLEAN &&
      type !== this.INPUT &&
      type !== this.ICON &&
      type !== this.NAVIGATE;
  }

  isFilterShow(type: TableColumnType): boolean {
    return type !== this.ACTION &&
      type !== this.CONTEXT &&
      type !== this.STATUS &&
      type !== this.BOOLEAN &&
      type !== this.ORDERED_NUMBER &&
      type !== this.ICON &&
      type !== this.NAVIGATE;
  }

  getWidth(progress: number | any): string {
    return typeof progress == "object" ? 'calc(' + progress.name + '%)' : 'calc(' + progress + '%)';
  }

  getDisplayAttribute(progress: number | any): string {
    let displayAttribute;
    if (typeof progress == "object") {
      displayAttribute = progress.name > 0 ? "block" : "none";
    } else {
      displayAttribute = progress > 0 ? "block" : "none";
    }
    return displayAttribute;
  }

  rowSelect(row): void {
    this.rowSelected.emit(row);
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

  selectItem(checked: boolean, data: any, columnId): void {
    let index: number;
    if(data.id) {
      index = this.getOrderNumber(data.id)
    } else {
      index = this.dataSource.findIndex(res => res.displayName === data.displayName)
    }
    this.dataSource[index][columnId] = checked;
    this.checkedData = this.dataSource.filter(data => data[columnId] == true);
    this.checkboxAction.emit(this.checkedData);
  }

  checkAllItem(checked: boolean, columnId: string): void {
    this.dataSource.forEach(data => {
        data[columnId] = checked;
      }
    );
    this.checkedData = this.dataSource.filter(data => data[columnId] == true);
    this.checkboxAction.emit(this.checkedData);
  }

  getOrderNumber(id: string): number {
    return this.dataSource.findIndex(data => data.id == id);
  }

  changePageNumber(page): void {
    this.currentPage = page;
  }

  getDisplayData(data: any) {
    return typeof data == "object" ? data.name : data;
  }

  getTooltip(data: any) {
    return typeof data == "object" ? data.toolTip : data;
  }

  onHeaderSelected(action): void {
    if (action) {
      this.selectedHeaderAction.emit(action);
    }
  }
}
