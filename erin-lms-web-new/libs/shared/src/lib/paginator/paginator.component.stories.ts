import { moduleMetadata, Story, Meta } from '@storybook/angular';
import { PaginatorComponent } from './paginator.component';

export default {
  title: 'PaginatorComponent',
  component: PaginatorComponent,
  decorators: [
    moduleMetadata({
      imports: [],
    })
  ],
} as Meta<PaginatorComponent>;

const Template: Story<PaginatorComponent> = (args: PaginatorComponent) => ({
  component: PaginatorComponent,
  props: args,
});


export const Primary = Template.bind({});
Primary.args = {
    contents:  {},
    displayNumber:  8,
}
