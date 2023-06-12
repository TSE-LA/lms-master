import {Meta, moduleMetadata} from '@storybook/angular';
import {boolean, object, radios, select, text} from "@storybook/addon-knobs";
import {DropdownComponent} from "../dropdown/dropdown.component";
import {DropdownInputComponent} from "../dropdown-input/dropdown-input.component";
import {DropdownViewComponent} from "./dropdown-view.component";
import {LoaderComponent} from "../loader/loader.component";
import {ButtonComponent} from "../button/button.component"
import {OverlayComponent} from "../overlay/overlay.component"
import {IconsComponent} from "../icons/icons.component";
import {TooltipDirective} from "../tooltip/tooltip.directive";
import {ReactiveFormsModule} from "@angular/forms";


export default {
  title: 'Input/Dropdown View',
  component: DropdownViewComponent,
  decorators: [
    moduleMetadata({
      declarations: [
        OverlayComponent,
        DropdownComponent,
        DropdownInputComponent,
        LoaderComponent,
        ButtonComponent,
        IconsComponent,
      TooltipDirective],
     imports: [ReactiveFormsModule]
    }),
  ],
} as Meta

export const Default = () => ({
  component: DropdownViewComponent,
  props: {
    defaultValue: text('placeholder text', 'Огноо'),
    size: radios('Sizes', {Small: 'small', Medium: 'medium', Large: 'large'}, 'medium'),
    color: select('Color', {PRIMARY: 'primary', SECONDARY: 'secondary', WARN: 'warn'}, 'primary'),
    disabled: boolean('disabled', false),
    outlined: boolean('outlined', false),
    load: boolean('loader', false),
    noOutline: boolean("No outline", false),
    icon: text('Icon', 'expand_more'),
    width: select('Dropdown width', {Short:'short', Long: 'long'}, 'long'),
    values: object('List of items', [
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'}])
  }
})
