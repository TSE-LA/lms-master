import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-column-list',
  template: `
    <div class="column-list-container" [style.height]="height">
      <div *ngIf="dataSource.length > 0"
           class="column-list"
           [ngStyle]="{'grid-template-columns': 'repeat(' + columns + ', 1fr)'}">
        <div *ngFor="let column of [].constructor(columns); let columnIndex = index" class="column-list-title-container">
          <div *ngFor="let data of dataSource.slice(columnIndex * rows, rows * (columnIndex+1))" class="column-list-title">
            <div>{{data}}</div>
          </div>
        </div>
      </div>
      <jrs-not-found-page
        *ngIf="dataSource.length <= 0"
        [text]="notFoundText"
        [size]="'smallest'"
        [show]="dataSource.length === 0">
      </jrs-not-found-page>
    </div>
  `,
  styleUrls: ['./column-list.component.scss']
})
export class ColumnListComponent {
  @Input() dataSource: string[]
  @Input() notFoundText: string
  @Input() columns = 4;
  @Input() rows = 5;
  @Input() height = 'unset';
}
