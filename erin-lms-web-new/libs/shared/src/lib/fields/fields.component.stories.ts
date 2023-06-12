import { moduleMetadata, Story, Meta } from '@storybook/angular';
import { FieldsComponent } from './fields.component';

export default {
  title: 'FieldsComponent',
  component: FieldsComponent,
  decorators: [
    moduleMetadata({
      imports: [],
    })
  ],
} as Meta<FieldsComponent>;

const Template: Story<FieldsComponent> = (args: FieldsComponent) => ({
  component: FieldsComponent,
  props: args,
});


export const Primary = Template.bind({});
Primary.args = {
}