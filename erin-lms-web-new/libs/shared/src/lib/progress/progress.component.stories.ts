import {Meta} from '@storybook/angular';
import {number, select} from "@storybook/addon-knobs";
import {ProgressComponent} from "./progress.component";


export default {
  title: 'Progress',
  component: ProgressComponent,
} as Meta

export const Default
  = () => ({
  component: ProgressComponent,
  props: {
    progress: number("Display number", 12),
    size: select('Sizes', {Small: 'small', Medium: 'medium'}, 'small')
  }
})
