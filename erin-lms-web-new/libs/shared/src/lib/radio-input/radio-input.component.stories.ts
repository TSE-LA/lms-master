import {Meta} from "@storybook/angular";
import {select, text} from "@storybook/addon-knobs";
import {RadioInputComponent} from "./radio-input.component";

export default {
  title: 'Input/Radio'
} as Meta

export const Radio = () => ({
  component: RadioInputComponent,
  props: {
    label: text('Label', 'Hello')
  }
})
