import {Meta, moduleMetadata} from "@storybook/angular";
import {DatePickerComponent} from "./date-picker.component";
import {ButtonComponent} from "../button/button.component";
import {IconsComponent} from "../icons/icons.component";
import {OverlayComponent} from "../overlay/overlay.component";
import {select, text} from "@storybook/addon-knobs";

export default {
  title: 'Input/Date-Picker',
  component: DatePickerComponent,
  parameters: {
    layout: 'centered',
  },
  decorators: [
    moduleMetadata({
      declarations: [ButtonComponent, IconsComponent, OverlayComponent]
    }),
  ],
} as Meta

export const SingleDatePicker = () => ({
  component: DatePickerComponent,
  props: {
    size: select('Size', {SMALL: 'small', MEDIUM: 'medium', LONG: 'long'}, 'long'),
    width: text('width', '100px')
  }
});
