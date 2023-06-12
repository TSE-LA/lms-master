import {number, object} from "@storybook/addon-knobs";
import {Meta, moduleMetadata} from "@storybook/angular";
import {CalendarNodeComponent} from "./calendar-node.component";
import {CalendarEventComponent} from "../calendar-event/calendar-event.component";

export default {
  title: 'Calendar/Calendar',
  component: CalendarNodeComponent,
  decorators: [
    moduleMetadata({
      declarations: [CalendarEventComponent]
    }),
  ],
} as Meta
export const CalendarNode = () => ({
  component: CalendarNodeComponent,
  parameters:{
    layout:'centered',
  },
  props: {
    day: number('day', 15),
    events: object('Events of day', [
      {title: 'New Course', state: '#3B86FF', id: '123456'},
      {title: 'Started Course', state: '#1ABC9C', id: '123456'},
      {title: 'Done Course', state: '#bbbbbb', id: '123456'},
      {title: 'Postponed Course', state: '#FF6565', id: '123456'}
    ]),
  },
});
