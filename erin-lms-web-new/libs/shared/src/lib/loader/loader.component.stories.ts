import {Meta} from "@storybook/angular";
import {select} from "@storybook/addon-knobs";
import {LoaderComponent} from "./loader.component";

export default {
  title: 'Loader',
  component: LoaderComponent
} as Meta

export const Default = () => ({
  component: LoaderComponent,
  props: {
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn', DARK: 'dark', LIGHT: 'light'}, 'primary'),
  }
});
