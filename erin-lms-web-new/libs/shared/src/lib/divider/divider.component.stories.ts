import {Meta, moduleMetadata} from "@storybook/angular";
import {CommonModule} from "@angular/common";
import {DividerComponent} from "./divider.component";

export default {
  title: 'Divider',
  component: DividerComponent,
  decorators: [
    moduleMetadata({
      // imports both components to allow component composition with storybook
      imports: [CommonModule],
    }),
  ],
} as Meta

export const DefaultDivider = () => ({
  component: DividerComponent,
  props: {}
});
