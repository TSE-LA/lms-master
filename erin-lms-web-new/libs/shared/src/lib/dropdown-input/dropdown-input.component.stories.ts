import {Meta, moduleMetadata} from '@storybook/angular';
import {DropdownInputComponent} from "./dropdown-input.component";
import {boolean, radios, select, text} from "@storybook/addon-knobs";
import {LoaderComponent} from "../loader/loader.component";
import {IconsComponent} from "../icons/icons.component";

export default {
  title: 'Input/Dropdown Input',
  component: DropdownInputComponent,
  decorators: [
    moduleMetadata({
      declarations: [LoaderComponent, IconsComponent],
    }),
  ],
} as Meta

export const Default = () => ({
  component: DropdownInputComponent,
  props: {
    defaultValue: text('placeholder text', 'Огноо'),
    size: radios('Sizes', {Small: 'small', Medium: 'medium', Large: 'large'}, 'medium'),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn', LIGHT: 'light'}, 'primary'),
    disabled: boolean('disabled', false),
    outlined: boolean('outlined', false),
    load: boolean('loader', false),
    noOutline: boolean("No outline", false),
  }
})
export const DefaultWithIcon = () => ({
  component: DropdownInputComponent,
  props: {
    icon: text('Icon', 'expand_more'),
    defaultValue: text('placeholder text', 'Огноо'),
    size: radios('Sizes', {Small: 'small', Medium: 'medium', Large: 'large', EqualToInput : 'equalToInput'}, 'medium'),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn',LIGHT: 'light'}, 'primary'),
    disabled: boolean('disabled', false),
    outlined: boolean('outlined', false),
    load: boolean('loader', false),
    noOutline: boolean("No outline", false),
  }
})
