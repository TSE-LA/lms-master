import {Meta} from "@storybook/angular";
import {InputFieldComponent} from "./input-field.component";
import {boolean, radios, select, text} from "@storybook/addon-knobs";

export default {
  title: 'Input-field'
} as Meta

export const Simple = () => ({
  component: InputFieldComponent,
  props: {
    selectedType: select('Type',{Text: 'text', Date: 'date', Number: 'number', Textarea: 'textarea'}, 'text'),
    placeholderText: text('placeholder text', 'Сургалтын нэр'),
    movePlaceholder: boolean('move placeholder', false),
    iconName: text('icon name', 'jrs-search'),
    isMaterial: boolean('From material?', false),
    required: boolean('required', false),
    disabled: boolean('disabled', false),
    errorText: text('error text', 'Please fill out this field'),
    error: boolean('error', false)
  }
});
export const SimpleWithLabel = () => ({
  component: InputFieldComponent,
  props: {
    labelText: text('labelText', 'Сургалтын нэр'),
    required: boolean('required', false),
    disabled: boolean('disabled', false),
    errorText: text('error text', 'Please fill out this field'),
    error: boolean('error', false)
  }
});
export const textarea = () => ({
  component: InputFieldComponent,
  props: {
    selectedType: select('Type',{Text: 'text', Date: 'date', Number: 'number', Textarea: 'textarea'}, 'textarea'),
    labelText: text('labelText', 'Сургалтын нэр'),
    required: boolean('required', false),
    disabled: boolean('disabled', false),
    errorText: text('error text', 'Please fill out this field'),
    error: boolean('error', false)
  }
});
