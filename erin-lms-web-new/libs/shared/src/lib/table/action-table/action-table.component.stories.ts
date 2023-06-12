import {Meta, moduleMetadata} from '@storybook/angular';
import {boolean, object} from "@storybook/addon-knobs";
import {InputFieldComponent} from "../../input-field/input-field.component";
import {ActionTableComponent} from "./action-table.component";
import {OverlayComponent} from "../../overlay/overlay.component";
import {ButtonComponent} from "../../button/button.component";
import {PaginatorComponent} from "../../paginator/paginator.component";
import {DropdownComponent} from "../../dropdown/dropdown.component";
import {IconsComponent} from "../../icons/icons.component";
import {MenuComponent} from "../../menu/menu.component";
import {TooltipDirective} from "../../tooltip/tooltip.directive";
import {CircleButtonComponent} from "../../circle-button/circle-button.component";
import {NotFoundPageComponent} from "../../not-found-page/not-found-page.component";

export default {
  title: 'Table/Action table',
  component: ActionTableComponent,
  decorators: [
    moduleMetadata({
      declarations: [
        InputFieldComponent,
        OverlayComponent,
        ButtonComponent,
        PaginatorComponent,
        DropdownComponent,
        IconsComponent,
        TooltipDirective,
        MenuComponent,
        NotFoundPageComponent,
      CircleButtonComponent]
    }),
  ],
} as Meta

export const ActionTable = () => ({
  component: ActionTableComponent,
  props: {
    tableColumns: object('Table columns', [
      {name:"НЭР", id: "she"},
      {name:"ЭХЛЭХ ОГНОО", id: "saw"},
      {name:"ДУУСАХ ОГНОО", id: "shore"},
      {name:"ТАЙЛАН", id: "help"}]),
    dataSource: object('Datasource', [
      {she: "saw", saw: "saw", shore: "shee", help: "oooooo", id:"12"},
      {she: "saw", saw: "saw", shore: "shee", help: "oooooo", id:"1"},
      {she: "saw", saw: "saw", shore: "shee", help: "oooooo", id:"2"},
      {she: "saw", saw: "saw", shore: "shee", help: "oooooo", id:"3"}]),
    contextActions: object('Table actions', [{id: "aa", name: "ww", action: "ee"}, {id: "aa", name: "hi im action", action: "ee"}]),
    action: boolean('Action column', true)
  }
})
