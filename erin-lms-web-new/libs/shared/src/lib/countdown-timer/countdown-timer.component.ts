import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'jrs-countdown-timer',
  template: `
    <div class="container">
      <div class="timer">
        <span> Шалгалт дуусахад </span>
        <span class="hour">{{hour}} : </span>
        <span class="minute">{{minute}} : </span>
        <span class="second">{{second}}</span>
      </div>
      <div class="progress-wrapper">
        <div id="progress"></div>
      </div>
    </div>
  `,
  styleUrls: ['./countdown-timer.component.scss']
})
export class CountdownTimerComponent implements OnChanges {
  @Input() remainingTime: number;
  @Input() durationInSeconds: number;
  @Output() timeOut = new EventEmitter<string>();
  timerInterval;
  secondsLeft: number;
  hour = '00';
  minute = '00';
  second = '00';

  ngOnChanges(changes: SimpleChanges) {
    for (let prop in changes) {
      if (prop == 'remainingTime') {
        if (this.remainingTime != null) {
          this.timer(this.remainingTime);
        }
      }
    }
  }

  timer(seconds): void {
    if (seconds > 0) {
      const now = Date.now();
      const then = now + seconds * 1000;
      this.displayLeftTime(seconds);

      this.timerInterval = setInterval(() => {
        this.secondsLeft = Math.round((then - Date.now()) / 1000);
        if (this.secondsLeft < 0) {
          this.timeOver();
          return;
        }
        this.displayLeftTime(this.secondsLeft);
      }, 1000);
    } else {
      this.timeOver();
    }
  }

  displayLeftTime(seconds): void {
    const progress = document.getElementById('progress');
    const percentage = Math.round((seconds * 100) / this.durationInSeconds);
    progress.style.width = percentage + '%';
    if (percentage < 20) {
      progress.classList.add('ending');
      document.querySelector('.hour').classList.add('ending');
      document.querySelector('.minute').classList.add('ending');
      document.querySelector('.second').classList.add('ending');
    }
    const hours = '0' + Math.floor(seconds / 3600);
    const remainderMinutes = '0' + Math.floor((seconds % 3600) / 60);
    const remainderSeconds = '0' + seconds % 60;
    this.hour = hours.slice(-2);
    this.minute = remainderMinutes.slice(-2);
    this.second = remainderSeconds.slice(-2);
  }

  timeOver(): void {
    this.clearInterval();
    this.timeOut.emit('timeOut');
  }

  clearInterval(): void {
    clearInterval(this.timerInterval)
  }
}
