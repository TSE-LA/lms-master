import {Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';

@Component({
  selector: 'jrs-progress',
  template: `
    <svg class="progress-circle"
         [class]="[size]"
         width="50"
         height="50"
         fill="transparent">
      <circle
        [style.strokeDasharray]="circumference"
        [style.strokeDashoffset]="0"
        class="progress-circle-background"
        cx="25"
        cy="25"
        r="23"
        stroke-width="5"/>
      <circle #progressCircle
              [style.strokeDasharray]="circumference"
              [style.strokeDashoffset]="strokeDashoffset"
              class="progress-circle-circle"
              cx="25"
              cy="25"
              r="23"
              stroke-width="3"/>
      <text class="progress-circle-text" x="25" y="25" text-anchor="middle"
            alignment-baseline="middle">{{progress <= 100 ? progress + '%' : '100%'}}</text>
    </svg>`,
  styleUrls: ['./progress.component.scss']
})
export class ProgressComponent implements OnChanges {
  @ViewChild('progressCircle') progressCircle: ElementRef;
  @Input() progress = 0;
  @Input() size = 'medium'
  radius = 23;
  circumference = 2 * Math.PI * this.radius;
  strokeDashoffset: number;

  ngOnChanges(changes: SimpleChanges) {
    if (changes.progress.currentValue !== changes.progress.previousValue) {
      this.calcProgress(changes.progress.currentValue);
    }
    this.roundProgress();
  }

  private calcProgress(progress: number) {
    if (progress === null || progress < 0) {
      this.progress = 0;
    }
    this.strokeDashoffset = this.circumference - (this.progress / 100) * this.circumference;

    if (this.strokeDashoffset < 0) {
      this.strokeDashoffset = 0
    }
  }

  roundProgress(): void {
    this.progress = Math.ceil(this.progress)
  }
}
