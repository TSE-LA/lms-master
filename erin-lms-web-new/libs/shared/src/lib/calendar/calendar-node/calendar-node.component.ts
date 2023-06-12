import {Component, Input, Output, EventEmitter, ViewChild, OnChanges} from '@angular/core';
import {CalendarEvent} from "../../shared-model";
import {DropdownModel} from "../../dropdown/dropdownModel";
import {DropdownComponent} from "../../dropdown/dropdown.component";

@Component({
  selector: 'jrs-calendar-node',
  template: `
    <div class="day-number" [class.next]="style === 'next'" [class.prev]="style === 'prev'">{{day}}</div>
    <div class="events" [ngClass]="{'mobile-version': mobile}" (click)="onNodeClick()">
      <jrs-dropdown
        #dropdown
        [hasPrefix]="true"
        [values]="this.dropDownValues"
        (selectionChange)="onEventSelect($event)">
      </jrs-dropdown>
      <jrs-calendar-event
        *ngFor="let event of events"
        [style]="style"
        [eventTitle]="event.title"
        [eventState]="getStateColor(event.state)"
        [courseType]="getTypeColor(event.eventType)"
        [eventId]="event.courseId"
        [mobile]="mobile"
        (eventHover)="this.eventHovered($event)"
        (eventClick)="eventClicked($event)">
      </jrs-calendar-event>
    </div>
  `,
  styleUrls: ['./calendar-node.component.scss']
})
export class CalendarNodeComponent {
  @ViewChild('dropdown') dropdown: DropdownComponent;
  @Input() events: CalendarEvent[];
  @Input() day: number;
  @Input() mobile = false;
  @Input() style: string;
  @Output() eventHover = new EventEmitter();
  @Output() eventClick = new EventEmitter();
  @Output() eventSelect = new EventEmitter<string>();
  dropDownValues: DropdownModel[];

  eventHovered(event): void {
    this.eventHover.emit(event)
  }

  getStateColor(state): string {
    if (state == 'DONE') {
      return '#ACAEB4';
    } else if (state == 'STARTED') {
      return '#0DC361';
    } else if (state == 'CANCELED') {
      return '#FF6565';
    } else if (state == 'POSTPONED') {
      return '#FF9342';
    } else if (state == 'PUBLISHED') {
      return '#0e72ea';
    } else {
      return '#3B86FF';
    }
  }

  getTypeColor(type): string {
    if (type == 'online-course') {
      return '#E8F7FD';
    } else if (type == 'classroom-course') {
      return '#caffdb';
    } else if (type == 'webinar') {
      return '#EEEAFF';
    }
  }

  eventClicked(eventId: string): void {
    this.eventClick.emit(eventId);
  }

  onNodeClick(): void {
    if (this.mobile && this.events.length > 0) {
      this.dropDownValues = this.getDropDownValues();
      this.dropdown.manualOpen();
    }
  }

  getDropDownValues(): DropdownModel[] {
    const result: DropdownModel[] = [];
    for (const event of this.events) {
      result.push({name: event.title, id: event.courseId, background: this.getStateColor(event.state)})
    }
    return result;
  }

  onEventSelect(event): void {
    this.eventSelect.emit(event.id);
  }
}
