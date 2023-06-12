import {Meta, moduleMetadata} from "@storybook/angular";
import {boolean, object} from "@storybook/addon-knobs";
import {MenuComponent} from "./menu.component";
import {IconsComponent} from "../icons/icons.component";
import {CircleButtonComponent} from "../circle-button/circle-button.component";
import {DropdownComponent} from "../dropdown/dropdown.component";
import {OverlayComponent} from "../overlay/overlay.component";
import {TooltipDirective} from "../tooltip/tooltip.directive";

export default {
  title: 'Menu',
  component: MenuComponent,
  decorators: [
    moduleMetadata({
      imports: [],
      declarations: [CircleButtonComponent, DropdownComponent, IconsComponent, OverlayComponent, TooltipDirective]
    }),
  ],
} as Meta

export const Default = () => ({
  component: MenuComponent,
  props: {
    contextActions: object('Table actions', [{id: "aa", name: "ww", action: "ee"}, {id: "aa", name: "hi im action", action: "ee"}]),
    context: boolean('Context menu style', false),
    noCircle: boolean('Without circle around it', false),
    actions: {
      handles: ['selectedAction', 'toggleMenu'],
    }
  }
});
