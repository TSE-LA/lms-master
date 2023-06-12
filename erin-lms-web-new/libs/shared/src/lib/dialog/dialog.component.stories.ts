import {Meta, moduleMetadata} from "@storybook/angular";
import {boolean} from "@storybook/addon-knobs";
import {IconsComponent} from "../icons/icons.component";
import {ButtonComponent} from "../button/button.component";
import {DialogComponent} from "./dialog.component";
import {OverlayComponent} from "../overlay/overlay.component";

export default {
  title: 'Info/Dialog',
  component: DialogComponent,
  decorators: [
    moduleMetadata({
      declarations: [ButtonComponent, IconsComponent, OverlayComponent],
    }),
  ]
} as Meta

export const Default = () => ({
  component: DialogComponent,
  props: {
    show : boolean('Show', false)
  },

});
