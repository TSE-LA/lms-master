import {ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'jrs-percentage-progress',
  template: `
    <jrs-overlay [show]="loading" [transparent]="false" [zIndex]="'1000'">
      <div class="container">
        <div [ngClass]="{'hide-progress': !loading}" class="progress-bar">
          <div class="progress-text">{{text + progressText}}</div>
          <div id="progress" class="progress">
          </div>
        </div>
      </div>

    </jrs-overlay>
  `,
  styleUrls: ['./percentage-progress.component.scss']
})
export class PercentageProgressComponent implements OnChanges {
  @Input() text: string;
  @Input() percentage = 0;
  @Input() loading: boolean;
  @Output() completed = new EventEmitter<boolean>();
  progressText = "";

  constructor(private cd: ChangeDetectorRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "percentage") {
        this.startProcessing()
      }
    }
  }

  private startProcessing(): void {
    const progressBar = document.getElementById("progress");
    if (progressBar) {
      this.progressText = +Math.round(this.percentage).toString() + "%";
      progressBar.style.width = (this.percentage * 3) + "px";
      this.cd.detectChanges();
    }
    if (Math.round(this.percentage) === 100) {
      progressBar.style.animationPlayState = 'paused';
      progressBar.style.backgroundImage = 'none';
      setTimeout(() => {
        progressBar.style.width = "0px";
        this.percentage = 0;
      }, 3000);
      this.cd.detectChanges();
      this.completed.emit(true);
    }
  }
}
