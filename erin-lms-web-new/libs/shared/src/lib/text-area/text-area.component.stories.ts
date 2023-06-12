import {Meta, moduleMetadata} from "@storybook/angular";
import {boolean, select, text} from "@storybook/addon-knobs";
import {TextAreaComponent} from "./text-area.component";
import {ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";

export default {
  title: 'Text area',
  decorators: [
    moduleMetadata({
      imports: [ReactiveFormsModule, CommonModule],
    })
  ],
} as Meta

export const Default = () => ({
  component: TextAreaComponent,
  props: {
    label: text('Label',null),
    value: text('Value', null),
    disabled: boolean('Disabled', false),
    placeholder: text('Placeholder text', 'Please hold her'),
    required: boolean('Required?', false),
    errorText: text('Error text', "Буруу бичлээ"),
    error: boolean('Error', false),
    size: select('Size', {Large: 'large', Medium: 'medium', Small: 'small'}, 'medium'),
  }
});
