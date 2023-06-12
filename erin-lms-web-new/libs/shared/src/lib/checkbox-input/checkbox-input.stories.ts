import {Meta} from "@storybook/angular";
import {boolean, text} from "@storybook/addon-knobs";
import {CheckboxInputComponent} from "./checkbox-input.component";

export default {
  title: 'Input/Checkbox',
  component: CheckboxInputComponent,
  parameters: {
    layout: 'centered',
  },
} as Meta
export const Checkbox = () => ({
  component: CheckboxInputComponent,
  props: {
    text: text( 'Test text', 'Hello World!'),
    check: boolean('Check', false)
  }
});
