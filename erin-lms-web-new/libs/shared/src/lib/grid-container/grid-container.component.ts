import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BreakPointObserverService} from "../theme/services/break-point-observer.service";

@Component({
  selector: 'jrs-grid-container',
  template: `
    <div class="grid-container" [ngStyle]="{
    'grid-template-columns' : generateColumn(),
    'gap' : gap+'px',
    'grid-template-rows' : generateRows() }">
      <ng-content></ng-content>
    </div>`,
  styleUrls: ['./grid-container.component.scss']
})
export class GridContainerComponent {
  @Input() row = 2
  @Input() column = 4;
  @Input() gap = 10;
  @Input() data: any[] = [];
  @Output() gridChanged = new EventEmitter<any>();

  constructor(private bs: BreakPointObserverService) {
  }

  generateColumn(): string {
    let columns = '';
    for (let i = 0; i < this.column; i++) {
      columns = columns + `1fr `
    }
    return columns.trimEnd();
  }

  generateRows(): string {
    let rows = '';
    for (let i = 0; i < this.row; i++) {
      rows = rows + `1fr `
    }
    return rows.trimEnd();
  }

  private loadPage(): void {
    this.bs.getMediaBreakPointChange().subscribe(res => {
      switch (res) {
        case 'media_xxl' :
        case 'media_xl' :
          this.row = this.countRows(4);
          this.column = 4;
          break;
        case 'media_lg' :
        case 'media_ml' :
          this.row = this.countRows(3);
          this.column = 3;
          break;
        case  'media_md' :
        case  'media_sm' :
          this.row = this.countRows(2);
          this.column = 2;
          break;
        case  'media_s' :
        case  'media_xs' :
          this.row = this.countRows(1);
          this.column = 1;
          break;
        default:
          break;
      }
    })
    this.gridChanged.emit({row: this.row, column: this.column})
  }

  private countRows(column: number): number {
    let row;
    switch (column) {
      case 4 :
        row = this.data.length < 5 ? 1 : 2;
        break;
      case 3 :
        row = this.data.length < 4 ? 1 : this.data.length < 7 ? 2 : 1;
        break;
      case 2 :
        row = this.data.length > 4 ? 3 : this.data.length < 3 ? 1 : 2;
        break;
      case 1 :
        row = this.data.length > 5 ? 6 : this.data.length;
        break;
    }
    return row;
  }
}
