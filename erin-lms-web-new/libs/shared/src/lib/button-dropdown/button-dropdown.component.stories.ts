import {Meta, moduleMetadata} from '@storybook/angular';
import {object, select, text} from "@storybook/addon-knobs";
import {DropdownComponent} from "../dropdown/dropdown.component";
import {DropdownInputComponent} from "../dropdown-input/dropdown-input.component";
import {ButtonDropdownComponent} from "./button-dropdown.component";
import {LoaderComponent} from "../loader/loader.component";
import {ButtonComponent} from "../button/button.component"


export default {
  title: 'Button Dropdown',
  component: ButtonDropdownComponent,
  decorators: [
    moduleMetadata({
      declarations: [DropdownComponent, DropdownInputComponent, LoaderComponent, ButtonComponent],
    }),
  ],
} as Meta

export const Default = () => ({
  component: ButtonDropdownComponent,
  props: {
    title: text('Title', 'Сургалтын төрлүүд'),
    size: select('Sizes', {Small: 'small', Medium: 'medium', Large: 'large', Long: 'long'}, 'medium'),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn'}, 'primary'),
    width: select('Dropdown width', {Short: 'short', Long: 'long'}, 'short'),
    values: object('List of items', [
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'}])
  }
})
