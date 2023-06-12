import {Meta,} from '@storybook/angular';
import {boolean,} from "@storybook/addon-knobs";
import {OverlayComponent} from "./overlay.component"

export default {
  title: 'Overlay',
  component: OverlayComponent,
  decorators: [],
} as Meta

export const Default = () => ({
  component: OverlayComponent,
  props: {
    show: boolean('Show', false),
    transparent : boolean('Transparent', false)
  }
})
