
import {boolean} from "@storybook/addon-knobs";
import {Meta, moduleMetadata} from "@storybook/angular";
import {CommonModule} from "@angular/common";
import {FileFieldComponent} from "./file-field.component";
import {IconsComponent} from "../icons/icons.component";
import {ButtonComponent} from "../button/button.component";
import {ReactiveFormsModule} from "@angular/forms";

export default {
  title: 'File field',
  component: FileFieldComponent,
  decorators: [
    moduleMetadata({
      // imports both components to allow component composition with storybook
      declarations: [IconsComponent, ButtonComponent],
      imports: [CommonModule, ReactiveFormsModule],
    }),
  ],
} as Meta

export const Default = () => ({
  component: FileFieldComponent,
  props: {
    load: boolean('Load', false)
  }
});
