import {Meta, moduleMetadata} from '@storybook/angular';
import {DropdownComponent} from "./dropdown.component";
import {boolean, object, radios} from "@storybook/addon-knobs";
import {LoaderComponent} from "../loader/loader.component";
import {OverlayComponent} from "../overlay/overlay.component"

export default {
  title: 'Input/Dropdown',
  component: DropdownComponent,
  decorators: [
    moduleMetadata({
      declarations: [LoaderComponent, OverlayComponent],
    }),
  ],
} as Meta

export const Default = () => ({
  component: DropdownComponent,
  props: {
    size: radios('Sizes', {Small: 'small', Medium: 'medium', Large: 'large'}, 'medium'),
    showOptions: boolean('Show', true),
    width: radios('Width', {Short: 'short', Long: 'long'}, 'inherit'),
    values: object('List of items', [
      {id: '0', name: 'Цахим сургалт'},
      {id: '1', name: 'Танхимын сургалт'},
      {id: '2', name: 'Хугацаатай сургалт'},
      {id: '3', name: 'Хугацаатай сургалт уртаар ааа'}])
  }
})
export const DefaultWithScroll = () => ({
  component: DropdownComponent,
  props: {
    size: radios('Sizes', {Small: 'small', Medium: 'medium', Large: 'large'}, 'medium'),
    showOptions: boolean('Show', true),
    width: radios('Width', {Short: 'short', Long: 'long'}, 'inherit'),
    values: object('List of items', [
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'}])
  }
})

export const ContextMenu = () => ({
  component: DropdownComponent,
  props: {
    size: radios('Sizes', {Small: 'small', Medium: 'medium', Large: 'large'}, 'medium'),
    showOptions: boolean('Show', true),
    context : boolean('Context', true),
    width: radios('Width', {Short: 'short', Long: 'long'}, 'short'),
    values: object('List of items', [
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'}])
  }
})


