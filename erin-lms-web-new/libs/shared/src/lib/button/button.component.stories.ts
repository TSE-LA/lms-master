import {ButtonComponent} from './button.component';
import {boolean, select, text} from "@storybook/addon-knobs";
import {action} from "@storybook/addon-actions";
import {Meta, moduleMetadata} from "@storybook/angular";
import {CommonModule} from "@angular/common";
import {IconsComponent} from "../icons/icons.component";
import {LoaderComponent} from "../loader/loader.component";

export default {
  title: 'Button',
  component: ButtonComponent,
  decorators: [
    moduleMetadata({
      // imports both components to allow component composition with storybook
      declarations: [IconsComponent, LoaderComponent],
      imports: [CommonModule],
    }),
  ],
} as Meta

export const Default = () => ({
  component: ButtonComponent,
  props: {
    title: text('Text', 'Шалгалт эхлүүлэх'),
    disabled: boolean('Disabled', false),
    load: boolean('Loader', false),
    outline: boolean('Outline', false),
    noOutline: boolean('No outline', false),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn'}, 'primary'),
    textColor: select('Text color', {PRIMARY: 'text-primary', SECONDARY: 'text-secondary', WARN: 'text-warn', DARK: 'text-dark', LIGHT: 'text-light'}, 'text-light'),
    size: select('Sizes', {Small: 'small', Medium: 'medium', Large: 'large', Long: 'long'}, 'medium'),
    clicked: action('clicked')
  },
  actions: {
    handles: ['onClick', 'click .btn'],
  },
});

export const DefaultWithIconButton = () => ({
  component: ButtonComponent,
  props: {
    title: text('Title', 'Сургалт нэмэх'),
    disabled: boolean('Disabled', false),
    load: boolean('Loader', false),
    outline: boolean('Outline', false),
    noOutline: boolean('No outline', false),
    iconName: text('Icon name', 'add'),
    isMaterial: boolean('From material?', true),
    iconColor: select('Icon Color', {PRIMARY: 'primary', SECONDARY: 'secondary', DARK: 'dark', GRAY: 'gray', WARN: 'warn'}, 'light'),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn'}, 'primary'),
    textColor: select('Text color', {
      PRIMARY: 'text-primary',
      SECONDARY: 'text-secondary',
      LINK: 'text-link',
      WARN: 'text-warn',
      DARK: 'text-dark',
      LIGHT: 'text-light'
    }, 'text-light'),
    size: select('Sizes', {Small: 'small', Medium: 'medium', Large: 'large', Long: 'long'}, 'medium'),
    clicked: action('clicked')
  },
  actions: {
    handles: ['onClick', 'click .btn'],
  },
});

export const IconButton = () => ({
  component: ButtonComponent,
  props: {
    disabled: boolean('Disabled', false),
    iconName: text('Icon name', 'add'),
    isMaterial: boolean('From material?', true),
    iconColor: select('Icon Color', {DARK: 'dark', LIGHT: 'light', WARN: 'warn'}, 'light'),
    outline: boolean('Outline', false),
    noOutline: boolean('No outline', false),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn', DARK: 'dark', LIGHT: 'light'}, 'primary'),
    size: select('Sizes', {Medium: 'icon-medium', Large: 'icon-large', Long: 'icon-long'}, 'icon-medium'),
    clicked: action('clicked')
  }
});

export const SquareButton = () => ({
  component: ButtonComponent,
  props: {
    title: text('Title', 'Хавсралт файл татах'),
    disabled: boolean('Disabled', false),
    iconName: text('Icon name', 'jrs-download'),
    isMaterial: boolean('From material?', false),
    iconColor: select('Icon Color', {DARK: 'dark', LIGHT: 'light', GRAY: 'gray', WARN: 'warn'}, 'gray'),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn', DARK: 'dark', LIGHT: 'light'}, 'gray'),
    outline: boolean('Outline', false),
    noOutline: boolean('No outline', false),
    square: boolean('Square?', true),
    size: select('Sizes', {Medium: 'medium', Long: 'long'}, 'long'),
    clicked: action('clicked')
  }
});
