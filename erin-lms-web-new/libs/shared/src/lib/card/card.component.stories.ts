import {Meta, moduleMetadata} from "@storybook/angular";
import {number, radios, text} from "@storybook/addon-knobs";
import {CardComponent} from "./card.component";
import {IconsComponent} from "../icons/icons.component";
import {ButtonComponent} from "../button/button.component";
import {MenuComponent} from "../menu/menu.component";
import {TooltipDirective} from "../tooltip/tooltip.directive";
import {OverlayComponent} from "../overlay/overlay.component";


export default {
  title: 'Card',
  component: CardComponent,
  parameters: {actions: {argTypesRegex: '^on.*'}},
  argTypes: {onClick: {action: 'clicked'}},
  excludeStories: /.*Data$/,
  decorators: [
    moduleMetadata({
      declarations: [IconsComponent, ButtonComponent, MenuComponent, TooltipDirective, OverlayComponent],
    }),
  ],
} as Meta

export const Default = () => ({
  component: CardComponent,
  props: {
    title: text('Title', 'UCBS Биллинг программ ашиглах заавар - Мобайл', 'Card Content'),
    author: text('Author', "dejidmaa.g", 'Card Content'),
    date: text('Date', "2020-10-05", 'Card Content'),
    state: text('State', "New", 'Card Content'),
    rating: number('Rating', 4, {min: 1, max: 5,}, 'Card Content'),
    rates: number('Rates', 12),
    progress: number('Progress', 90),
    repeat: number('Repeat', 1, {min: 1, max: 15,}),
    defaultThumbnailUrl: text('thumbnail', "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Taka_Shiba.jpg/1200px-Taka_Shiba.jpg"),
    thumbnailUrl: text('thumbnail', "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Taka_Shiba.jpg/1200px-Taka_Shiba.jpg"),
    size: radios('Sizes', {Small: 'small', Medium: 'medium', Large: 'large'}, 'medium'),
    buttonText : text('Button text', 'Танилцах')
  },
  actions: {
    handles: ['onClick', 'click .btn'],
  },
});


