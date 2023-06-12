import {TabGroupComponent} from "./tab-group.component";
import {Meta, moduleMetadata} from "@storybook/angular";
import {TabComponent} from "./tab.component";
import {select} from "@storybook/addon-knobs";

export default {
  title: 'Tab',
  decorators: [
    moduleMetadata({
      declarations: [TabGroupComponent, TabComponent]
    })
  ]
} as Meta

export const Simple = () => ({
  component: TabGroupComponent,
  props: {
    size: select('size', {small: 'small', medium: 'medium', large: 'large'}, 'small')
  },
  template: `<jrs-tab-group>
                <jrs-tab [label]="'tab 1'">Tab 1 Content</jrs-tab>
                <jrs-tab [label]="'tab 2'">Tab 2 Content</jrs-tab>
                <jrs-tab [label]="'tab 3'">Tab 3 Content</jrs-tab>
            </jrs-tab-group>`
})
