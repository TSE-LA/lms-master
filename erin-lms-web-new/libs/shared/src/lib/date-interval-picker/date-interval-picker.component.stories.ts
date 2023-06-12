import {Meta, moduleMetadata} from "@storybook/angular";
import {ButtonComponent} from "../button/button.component";
import {IconsComponent} from "../icons/icons.component";
import {OverlayComponent} from "../overlay/overlay.component";
import {DateIntervalPickerComponent} from "./date-interval-picker.component";
import {DatePickerComponent} from "../date-picker/date-picker.component";

export default {
  title: 'Input/Date-Picker',
  component: DateIntervalPickerComponent,
  parameters: {
    layout: 'centered',
  },
  decorators: [
    moduleMetadata({
      declarations: [DatePickerComponent, ButtonComponent, IconsComponent, OverlayComponent]
    }),
  ],
} as Meta
export const DateIntervalPicker = () => ({
  component: DateIntervalPickerComponent,
});
