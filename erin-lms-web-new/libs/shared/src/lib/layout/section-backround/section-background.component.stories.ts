import {SectionBackgroundComponent} from "./section-background.component";
import {Meta} from "@storybook/angular";
import {boolean, select} from "@storybook/addon-knobs";

export default {
  title: 'Layout/Background',
  component: SectionBackgroundComponent
} as Meta
export const SimpleBackground = () => ({
  component: SectionBackgroundComponent,
  parameters: {
    layout: 'centered'
  },
   props: {
     color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary'}, 'primary'),
     outline: boolean('Outline', false)
   }
})
