import {Meta, moduleMetadata} from "@storybook/angular";
import {CircleButtonComponent} from "./circle-button.component";
import {boolean, select, text} from "@storybook/addon-knobs";
import {action} from "@storybook/addon-actions";
import {IconsComponent} from "../icons/icons.component";

export default {
  title: 'Circle button',
  component: CircleButtonComponent,
  decorators: [
    moduleMetadata({
      imports: [],
      declarations: [IconsComponent]
    }),
  ],
} as Meta

export const DefaultCircle = () => ({
  component: CircleButtonComponent,
  props: {
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn', GRAY: 'gray', NONE: 'none'}, 'primary'),
    size: select('Sizes', {Small: 'small', Medium: 'medium', Large: 'large'}, 'medium'),
    iconName: text('Icon name', 'add'),
    iconColor: select('Icon Color', {DARK: 'dark', LIGHT: 'light', GRAY: 'gray', WARN: 'warn'}, 'light'),
    isMaterial: boolean('Is icon from material', true),
    disabled: boolean('Is it disabled?', false),
    clicked: action('clicked')
  },
  actions: {
    handles: ['onClick', 'click .btn'],
  },
});
