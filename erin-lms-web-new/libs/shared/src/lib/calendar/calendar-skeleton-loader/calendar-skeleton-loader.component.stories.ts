import {object} from "@storybook/addon-knobs";
import {Meta, moduleMetadata} from "@storybook/angular";
import {CalendarNodeComponent} from "../calendar-node/calendar-node.component";
import {CalendarSkeletonLoaderComponent} from "./calendar-skeleton-loader.component";
import {CalendarEventComponent} from "../calendar-event/calendar-event.component";
import {DUMMY_CALENDAR_DATA} from "../../constants/calendar-constants";


export default {
  title: 'Calendar/Calendar',
  component: CalendarSkeletonLoaderComponent,
  parameters: {
    layout: 'centered',
  },
  decorators: [
    moduleMetadata({
      declarations: [CalendarNodeComponent, CalendarEventComponent]
    }),
  ],
} as Meta
export const CalendarSkeletonLoader = () => ({
  component: CalendarSkeletonLoaderComponent,

  props: {
    calendarProperties: object('CalendarProperties',
      DUMMY_CALENDAR_DATA
    ),
  },
});
