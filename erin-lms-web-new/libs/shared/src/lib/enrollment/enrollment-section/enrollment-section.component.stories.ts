import {Meta, moduleMetadata} from "@storybook/angular";
import {EnrollmentSectionComponent} from "./enrollment-section.component";
import {ButtonComponent} from "../../button/button.component";
import {IconsComponent} from "../../icons/icons.component";
import {InputFieldComponent} from "../../input-field/input-field.component";
import {DropdownViewComponent} from "../../dropdown-view/dropdown-view.component";
import {DropdownInputComponent} from "../../dropdown-input/dropdown-input.component";
import {DropdownComponent} from "../../dropdown/dropdown.component";
import {OverlayComponent} from "../../overlay/overlay.component";
import {LoaderComponent} from "../../loader/loader.component";
import {CheckboxInputComponent} from "../../checkbox-input/checkbox-input.component";
import {object, text} from "@storybook/addon-knobs";
import {GROUP_MEMBERS} from "../../constants/group-management-constants";

export default {
  title: 'Section/Enrollment',
  component: EnrollmentSectionComponent,
  parameters: {
    layout: 'centered',
  },
  decorators: [
    moduleMetadata({
      declarations: [
        ButtonComponent,
        IconsComponent,
        InputFieldComponent,
        DropdownViewComponent,
        DropdownInputComponent,
        DropdownComponent,
        OverlayComponent,
        LoaderComponent,
        CheckboxInputComponent],
    }),
  ]
} as Meta
export const EnrollmentSection = () => ({
  component: EnrollmentSectionComponent,
  parameters: {
    layout: 'centered',
  },
  props: {
    title: text('Title', 'ШАЛГАЛТАД ХАМРАГДАХ СУРАЛЦАГЧИД'),
    usersAvailableToEnroll: object('Users available to enroll', GROUP_MEMBERS),
    groups: object('Groups',
      [{name: 'all', id: 'all'},
        {name: '1234', id: '1234'},
        {name: '12rrr34', id: '12rrr34'}]),
    roles: object('Roles',
      [
        {name: 'all', id: 'all'},
        {name: 'User', id: 'User'},
        {name: 'Manager', id: 'Manager'},
        {name: 'Instructor', id: 'Instructor'}])
  },
});
