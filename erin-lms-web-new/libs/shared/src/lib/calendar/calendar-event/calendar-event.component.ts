import {Component, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'jrs-calendar-event',
  template: `
    <div [class.next]="style === 'next'" [class.prev]="style === 'prev'" [ngClass]="{'mobile-version': mobile}">
      <div [jrsTooltip]="eventTitle"
        id="calendar-event"
        class="calendar-event"
        [ngStyle]="{'border-color': eventState, 'background': courseType}"
        (click)="onClicked($event)"
        (mouseover)="onHover($event)"
        (mouseout)="clearTimeOut()">
        {{eventTitle}}
      </div>
    </div>`,
  styleUrls: ['./calendar-event.component.scss']
})
export class CalendarEventComponent {

  @Input() eventState: string;
  @Input() courseType: string;
  @Input() eventTitle: string;
  @Input() eventId: string;
  @Input() style: string;
  @Input() mobile = false;
  @Output() eventHover = new EventEmitter();
  @Output() eventClick = new EventEmitter();

  timer: any;

  onHover(mouse): void {
    if (!this.mobile) {
      this.timer = window.setTimeout(() => {
        this.eventHover.emit({positionX: mouse.clientX, positionY: mouse.clientY, id: this.eventId});
      }, 1000);
    }
  }

  clearTimeOut(): void {
    if (this.timer) {
      window.clearTimeout(this.timer);
    }
  }

  onClicked(mouse): void {
    if (!this.mobile) {
      this.eventClick.emit({positionX: mouse.clientX, positionY: mouse.clientY, id: this.eventId});
    }
  }
}
