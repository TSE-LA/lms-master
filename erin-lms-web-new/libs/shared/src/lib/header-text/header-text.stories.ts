import {HeaderTextComponent} from "./header-text.component";
import {Meta} from "@storybook/angular";
import {select, text} from "@storybook/addon-knobs";

export default {
  title: 'Text/header',
  component: HeaderTextComponent
} as Meta

export const TitleText = () => ({
  component: HeaderTextComponent,
  props: {
    defaultValue: text('Text', 'Танхимын сургалтын төлөвлөгөө'),
    size: select('Size', {Small: 'small', Medium: 'Medium', Large: 'large'}, 'large')
  }
})
