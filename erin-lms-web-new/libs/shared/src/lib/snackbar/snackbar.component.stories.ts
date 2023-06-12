import {Meta, moduleMetadata} from "@storybook/angular";
import {boolean, select, text} from "@storybook/addon-knobs";
import {SnackbarComponent} from "./snackbar.component";
import {IconsComponent} from "../icons/icons.component";
import {ButtonComponent} from "../button/button.component";

export default {
  title: 'Info/Snackbar',
  component: SnackbarComponent,
  decorators: [
    moduleMetadata({
      declarations: [ButtonComponent, IconsComponent],
    }),
  ]
} as Meta

export const Default = () => ({
  component: SnackbarComponent,
  props: {
    id: text('Id', '123456789'),
    snackbarText: text('Snackbar text', 'this is a text'),
    show: boolean('show', true),
    status: select("status", {ERROR: 'error', SUCCESS: 'success'}, 'success')
  },

});
