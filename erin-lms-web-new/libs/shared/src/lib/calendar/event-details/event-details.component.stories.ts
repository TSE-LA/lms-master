import {moduleMetadata, Meta} from '@storybook/angular';
import {EventDetailsComponent} from './event-details.component';
import {boolean, object} from "@storybook/addon-knobs";
import {TooltipDirective} from "../../tooltip/tooltip.directive";

export default {
  title: 'Calendar/Event details',
  component: EventDetailsComponent,
  decorators: [
    moduleMetadata({
      imports: [],
      declarations: [TooltipDirective]
    })
  ],
} as Meta<EventDetailsComponent>;

export const DefaultEventDetail = () => ({
  component: EventDetailsComponent,
  props: {
    descriptionName: object('Descriptions',
      ['Сургалтын төрөл', 'Алба хэлтэс', 'Суралцагчийн тоо', 'Багш']),
    descriptionValues: object('Description values', [
      'Сургалтын хяналтын хэлтэс', 'Ерөнхий ур чадвар', 'БТХ, СУА, ӨБХ', '25', 'Оюунзаяа']),
    load: boolean('Load', false)
  }
});
