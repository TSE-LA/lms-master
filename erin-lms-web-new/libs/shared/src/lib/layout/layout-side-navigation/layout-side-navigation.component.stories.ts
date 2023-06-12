import {Meta, moduleMetadata} from "@storybook/angular";
import {boolean, object} from "@storybook/addon-knobs";
import {LayoutSidenavComponent} from "./layout-side-navigation.component";
import {IconsComponent} from "../../icons/icons.component";
import {CommonModule} from "@angular/common";
import {OverlayComponent} from "../../overlay/overlay.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";



export default {
  title: 'Layout/Sidenav',
  component: LayoutSidenavComponent,
  parameters: {actions: {argTypesRegex: '^on.*'}},
  argTypes: {onClick: {action: 'clicked'}},
  excludeStories: /.*Data$/,
  decorators: [
    moduleMetadata({
      declarations: [ IconsComponent, OverlayComponent],
      imports: [CommonModule, BrowserAnimationsModule],
    }),
  ],
} as Meta

export const Default = () => ({
  component: LayoutSidenavComponent,
  props: {
    navItems: object('Values', [
      {name: 'Хяналтын булан', link: '/dashboard', iconName: 'jrs-bubbles'},
      {name: 'Тайлан', link: '/dashboard', iconName: 'jrs-analytics'},
      {name: 'Урамшуулал', link: '/promotion', iconName: 'jrs-price-tag'},
      {name: 'Онлйан сургалт', link: '/online-course', iconName: 'jrs-computer'},
      {name: 'Үнэлгээний хуудас', link: '/survey', iconName: 'jrs-settings'}]),
    opened: boolean('Opened', true)
  }
});
