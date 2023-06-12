import {moduleMetadata, Meta, Story} from '@storybook/angular';
import {ContentStructureComponent} from './content-structure.component';
import {IconsComponent} from "../../icons/icons.component";
import {OverlayComponent} from "../../overlay/overlay.component";
import {ButtonComponent} from "../../button/button.component";
import {boolean, object} from "@storybook/addon-knobs";
import {InputFieldComponent} from "../../input-field/input-field.component";
import {TooltipDirective} from "../../tooltip/tooltip.directive";
import {CircleButtonComponent} from "../../circle-button/circle-button.component";
import {ReactiveFormsModule} from "@angular/forms";

export default {
  title: 'Structure/Content',
  component: ContentStructureComponent,
  decorators: [
    moduleMetadata({
      declarations: [
        OverlayComponent,
        IconsComponent,
        ButtonComponent,
        InputFieldComponent,
        TooltipDirective,
        CircleButtonComponent
      ],
      imports: [ReactiveFormsModule]
    }),
  ],
} as Meta<ContentStructureComponent>;

export const Default: Story = () => ({
  component: ContentStructureComponent,
  props: {
    disabled: boolean('Disabled or read only', false),
    load: boolean('Load', false),
    moduleData: object('Data', [
      {
        name: "HI2", updateName: "Hello this is a module", sections: [
          {
            name: "Хуудас нэг",
            fileId: "123456",
          },
          {
            name: "Хуудас нэг",
            fileId: "123456",
          }],
        opened: false
      },
      {
        name: "HI21", updateName: "HEllo2", sections: [
          {
            name: "Хуудас хоёр",
            fileId: "123456",
          },
          {
            name: "Хуудас нэг",
            fileId: "123456",
          }],
        opened: true
      }
    ])
  }
})
