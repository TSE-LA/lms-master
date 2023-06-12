import {radios, text} from "@storybook/addon-knobs";
import {Meta} from "@storybook/angular";
import {CalendarEventComponent} from "./calendar-event.component";

export default {
  title: 'Calendar/Calendar',
  component: CalendarEventComponent
} as Meta
export const CalendarEvent = () => ({
  component: CalendarEventComponent,
  parameters: {
    layout: 'centered',
  },
  props: {
    eventTitle: text('Event title', 'Event title'),
    eventState: radios(
      'Event state',
      {
        NewSentReceived: '#3B86FF',
        ReadyStarted: '#1ABC9C',
        CanceledPostponed: '#FF6565',
        Done: '#D7DAE2'
      }, '#1ABC9C')
  },
});
