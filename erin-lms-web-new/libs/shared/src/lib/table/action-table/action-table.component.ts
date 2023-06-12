import {
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  EventEmitter,
  ViewChild
} from '@angular/core';
import {DropdownComponent} from "../../dropdown/dropdown.component";

export interface Column {
  id: string,
  name: string,
  width?: string
}

@Component({
  selector: 'jrs-action-table',
  template: `
    <div class="container">
      <div class="table">
        <table>
          <tr class="header-row">
            <ng-container *ngFor="let column of tableColumns">
              <th [style.width]="column.width">{{column.name.toUpperCase()}}</th>
            </ng-container>
            <th class="action-column"
                *ngIf="action"></th>
          </tr>
          <ng-container *ngIf="(tableData !== null|| tableData.length > 0) && !load">
            <tr class="table-row"
                *ngFor="let data of dataToShow">
              <td *ngFor="let column of tableColumns" [delay]="0" jrsTooltip="{{data[column.id]}}">{{data[column.id]}}</td>
              <td class="action-column"
                  *ngIf="action">
                <jrs-menu id="{{data.id}}"
                          class="context-button"
                          [contextActions]="contextActions"
                          (selectedAction)="onActionSelect(data.id, $event)"
                          (toggleMenu)="toggleMenu(data, $event)">
                </jrs-menu>
              </td>
            </tr>
          </ng-container>
        </table>
      </div>
      <jrs-paginator *ngIf="!load && tableData !== null" class="paginator" [perPageNumber]="dataPerPage" [contents]="tableData"
                     (pageClick)="onPageChange($event)"></jrs-paginator>
      <jrs-not-found-page [show]="!load && (!dataSource || dataSource.length < 1)"
                          [text]="'Мэдээлэл олдсонгүй'">
      </jrs-not-found-page>

      <jrs-skeleton-loader [load]="load" [amount]="10"></jrs-skeleton-loader>
    </div>
  `,
  styleUrls: ['./action-table.component.scss']
})
export class ActionTableComponent implements OnChanges {
  @ViewChild('dropdown') dropdown: DropdownComponent;
  @Input() dataSource = [];
  @Input() load: boolean;
  @Input() tableColumns: Column[] = [];
  @Input() contextActions: any[] = [];
  @Input() columnFilter: boolean;
  @Input() dataPerPage = 10;
  @Input() action = true;
  @Output() selectRow = new EventEmitter<any>();
  @Output() selectedAction = new EventEmitter<any>();
  tableData: any[];
  dataToShow: any[];
  filter = new Map();
  selectedRowId: string;

  constructor(private cdRef: ChangeDetectorRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.dataSource != null) {
      this.tableData = this.dataSource;
    }
  }

  onPageChange(datas): void {
    this.dataToShow = datas;
    this.cdRef.detectChanges();
  }

  showContextButton(rowId: string, show: boolean): void {
    const element = document.getElementById(rowId);
    if (this.selectedRowId == rowId) {
      element.style.display = 'unset';
      element.style.visibility = 'visible';
    } else if (this.selectedRowId == null) {
      if (show) {
        element.style.display = 'unset';
        element.style.visibility = 'visible';
      } else if (!show) {
        element.style.display = 'none';
      }
    } else {
      element.style.display = 'none';
    }
  }

  onActionSelect(id: string, action): void {
    //TODO remove when moving issue is investigated
    // this.hideAction();
    if (action) {
      this.selectedAction.emit({id, action});
    }
  }

  toggleMenu(row, opened: boolean): void {
    this.selectedRowId = opened ? row.id : null;
    this.selectRow.emit(row);
  }
}
