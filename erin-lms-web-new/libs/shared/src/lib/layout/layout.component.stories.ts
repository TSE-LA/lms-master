import {Meta, moduleMetadata} from "@storybook/angular";
import {CommonModule} from "@angular/common";
import {LayoutComponent} from "./layout.component";
import {ButtonComponent} from "../button/button.component";
import {IconsComponent} from "../icons/icons.component";
import {LayoutSidenavComponent} from "./layout-side-navigation/layout-side-navigation.component";
import {HeaderToolbarComponent} from "./header-toolbar/header-toolbar.component";
import {OverlayComponent} from "../overlay/overlay.component";
import {DropdownComponent} from "../dropdown/dropdown.component";
import {DropdownInputComponent} from "../dropdown-input/dropdown-input.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";



export default {
  title: 'Layout/Layout',
  component: LayoutComponent,
  parameters: {actions: {argTypesRegex: '^on.*'}},
  argTypes: {onClick: {action: 'clicked'}},
  excludeStories: /.*Data$/,
  decorators: [
    moduleMetadata({
      declarations: [
        IconsComponent,
        LayoutSidenavComponent,
        ButtonComponent,
        DropdownInputComponent,
        DropdownComponent,
        HeaderToolbarComponent,
        OverlayComponent],
      imports: [CommonModule, BrowserAnimationsModule],
    }),
  ],
} as Meta

export const Default = () => ({
  component: LayoutComponent,
  props: {
  }
});
