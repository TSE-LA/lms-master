import {Component, Input} from '@angular/core';
import {LegendModel} from "./legend.model";

@Component({
  selector: 'jrs-legends',
  template: `
    <div class="wrapper">
      <div *ngFor="let legend of legends" class="legend">
        <div class="prefix" [ngStyle]="{'background-color': legend.color}"></div>
        <span class="text">{{legend.text}}</span>
      </div>
    </div>
  `,
  styleUrls: ['./legends.component.scss']
})
export class LegendsComponent  {
  @Input() legends: LegendModel[];
}
