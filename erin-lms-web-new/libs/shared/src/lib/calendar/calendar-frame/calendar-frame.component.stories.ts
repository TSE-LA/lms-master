import {object} from "@storybook/addon-knobs";
import {Meta, moduleMetadata} from "@storybook/angular";
import {CalendarNodeComponent} from "../calendar-node/calendar-node.component";
import {CalendarFrameComponent} from "./calendar-frame.component";
import {CalendarEventComponent} from "../calendar-event/calendar-event.component";
import {CalendarEventPreviewComponent} from "../calendar-event-preview/calendar-event-preview.component";
import {IconsComponent} from "../../icons/icons.component";
import {DropdownComponent} from "../../dropdown/dropdown.component";
import {ButtonComponent} from "../../button/button.component";
import {DUMMY_CALENDAR_DATA} from "../../constants/calendar-constants";


export default {
  title: 'Calendar/Calendar',
  component: CalendarFrameComponent,
  decorators: [
    moduleMetadata({
      declarations: [
        CalendarNodeComponent,
        CalendarEventComponent,
        CalendarEventPreviewComponent,
        IconsComponent,
        DropdownComponent,
        ButtonComponent]
    }),
  ],
} as Meta
export const CalendarFrame = () => ({
  component: CalendarFrameComponent,
  parameters: {
    layout: 'centered',
  },
  props: {
    calendarProperties: object('CalendarProperties',
      DUMMY_CALENDAR_DATA
    ),
  },
});
