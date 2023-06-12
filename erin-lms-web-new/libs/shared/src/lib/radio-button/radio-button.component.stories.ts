import {Meta} from "@storybook/angular";
import {boolean, text} from "@storybook/addon-knobs";
import {RadioButtonComponent} from "./radio-button.component";
import {action} from "@storybook/addon-actions";

export default {
  title: 'Input/Radio button',
  component: RadioButtonComponent,
  parameters: {
    layout: 'centered',
  },
} as Meta
export const Default = () => ({
  component: RadioButtonComponent,
  props: {
    text: text( 'Label', 'This is a label.'),
    check: boolean('Check', false),
    clicked: action('Clicked')
  }
});
