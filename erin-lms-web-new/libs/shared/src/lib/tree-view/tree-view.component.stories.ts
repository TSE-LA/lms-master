import {Meta, moduleMetadata} from "@storybook/angular";
import {TreeViewComponent} from "./tree-view.component";
import {DynamicComponentDirective} from "../dynamic-component/dynamic-component.directive";
// import {TreeViewNodeComponent} from "./tree-view-node/tree-view-node.component";

export default {
  title: 'Tree view',
  parameters: {
    layout: 'centered',
  },
  decorators: [
    moduleMetadata({
      declarations: [DynamicComponentDirective, /*TreeViewNodeComponent*/],
    }),
  ],
} as Meta

export const Default = () => ({
  component: TreeViewComponent,

  // props: {
  //   label: text('Label',null),
  //   value: text('Value', null),
  //   disabled: boolean('Disabled', false),
  //   placeholder: text('Placeholder text', 'Please hold her'),
  //   required: boolean('Required?', false),
  //   errorText: text('Error text', "Буруу бичлээ"),
  //   error: boolean('Error', false),
  //   size: select('Size', {Large: 'large', Medium: 'medium', Small: 'small'}, 'medium'),
  // }
});
