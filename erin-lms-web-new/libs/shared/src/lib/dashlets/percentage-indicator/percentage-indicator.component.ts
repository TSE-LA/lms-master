import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'jrs-percentage-indicator',
  template: `
    <div class="container">
      <span class="percentage">{{overallPercentage}}</span>
      <div *ngIf="up" class="indicator">
        <jrs-icon class="icon" [mat]="true" [color]="'primary'">north</jrs-icon>
        <span class="primary">{{changedPercentage}}</span>
      </div>
      <div *ngIf="!up" class="indicator">
        <jrs-icon class="icon" [mat]="true" [color]="'warn'">south</jrs-icon>
        <span class="warn">{{changedPercentage}}</span>
      </div>
    </div>
  `,
  styleUrls: ['./percentage-indicator.component.scss']
})
export class PercentageIndicatorComponent implements OnInit {
  @Input() overallPercentage: string;
  @Input() up: boolean;
  @Input() changedPercentage: string;

  constructor() {
  }

  ngOnInit(): void {
  }

}
