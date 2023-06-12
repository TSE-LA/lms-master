import {Meta} from "@storybook/angular";
import {ScrollComponent} from "./scroll.component";
import {select} from "@storybook/addon-knobs";

export default {
  title: 'Scroll'
} as Meta

export const Scroll = () => ({
  component: ScrollComponent,
  props: {
    size: select('Size', {SMALL: 'small', MEDIUM: 'medium', LARGE: 'large'}, 'medium'),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary'}, 'secondary')
  }
})
