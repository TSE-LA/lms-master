import {moduleMetadata, Meta} from '@storybook/angular';
import {IconLabelComponent} from './icon-label.component';
import {boolean, text} from "@storybook/addon-knobs";
import {IconsComponent} from "../../icons/icons.component";
import {CommonModule} from "@angular/common";

export default {
  title: 'Calendar/Location and time',
  component: IconLabelComponent,
  decorators: [
    moduleMetadata({
      imports: [CommonModule],
      declarations: [IconsComponent]
    })
  ],
} as Meta<IconLabelComponent>;

export const DefaultTimeLocation = () => ({
  component: IconLabelComponent,
  props: {
    location: text('Location', 'Central Tower'),
    time: text('Time', '16:00-18:00'),
    load: boolean('Load', false)
  }
});

