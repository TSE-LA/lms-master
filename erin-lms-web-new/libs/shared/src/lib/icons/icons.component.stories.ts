import {Meta} from "@storybook/angular";
import {boolean, select, text} from "@storybook/addon-knobs";
import {IconsComponent} from "./icons.component";

export default {
  title: 'Icon',
  component: IconsComponent
} as Meta

export const Default = () => ({
  component: IconsComponent,
  props: {
    name: text('Icon name', 'jrs-download'),
    mat: boolean('From Material icon?', false),
    iconColor: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn', DARK: 'dark', LIGHT: 'light', GRAY: 'gray'}, 'primary'),
  },
  actions: {
    handles: ['onClick', 'click .btn'],
  },
});
