import {Meta, moduleMetadata} from '@storybook/angular';
import {number, object} from "@storybook/addon-knobs";
import {ButtonComponent} from "../button/button.component"
import {IconsComponent} from "../icons/icons.component";
import {PaginatorComponent} from "./paginator.component";


export default {
  title: 'Paginator',
  component: PaginatorComponent,
  decorators: [
    moduleMetadata({
      declarations: [ButtonComponent, IconsComponent],
    }),
  ],
} as Meta

export const Default
  = () => ({
  component: PaginatorComponent,
  props: {
    perPageNumber : number("Display number" , 25),
    contents: object('Content', [
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 4, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 5, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 6, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 7, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 8, name: 'Цахим сургалт'},
      {id: 9, name: 'Танхимын сургалт'},
      {id: 10, name: 'Хугацаатай сургалт'},
      {id: 11, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 12, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 13, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 14, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 0, name: 'Цахим сургалт'},
      {id: 1, name: 'Танхимын сургалт'},
      {id: 2, name: 'Хугацаатай сургалт'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'},
      {id: 3, name: 'Хугацаатай сургалт уртаар ааа'}])
  }
})
