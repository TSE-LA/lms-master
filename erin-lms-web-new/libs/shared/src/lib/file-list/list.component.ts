import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'jrs-list',
  template: `
    <div *ngFor="let item of list; let index=index"
         class="item"
         draggable="true">
      <div class="item-name-area">

        <span class="order-number">
          {{index + 1}}.
        </span>
        <span>
          {{item.name}}
        </span>
      </div>
      <div class="trash-button" (click)="deleteFile(item)">
        <jrs-icon
          [size]="'medium'"
          [color]="'light'"
          [mat]="false">
          jrs-trash-can
        </jrs-icon>
      </div>
    </div>
    <jrs-not-found-page [size]="'smallest'" [show]="list.length < 1" [text]="notFoundText"></jrs-not-found-page>
  `,
  styleUrls: ['./list.component.scss']
})
export class ListComponent {
  @Input() list: any[] = [];
  @Input() notFoundText = "";
  @Output() delete = new EventEmitter();

  deleteFile(item) {
    this.delete.emit(item);
  }
}
