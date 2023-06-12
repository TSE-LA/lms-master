import {Meta, moduleMetadata} from "@storybook/angular";
import {boolean, object} from "@storybook/addon-knobs";
import {TestStructureComponent} from "./test-structure.component";
import {QuestionTypes} from "../../shared-model";
import {OverlayComponent} from "../../overlay/overlay.component";
import {IconsComponent} from "../../icons/icons.component";
import {ButtonComponent} from "../../button/button.component";
import {InputFieldComponent} from "../../input-field/input-field.component";
import {TooltipDirective} from "../../tooltip/tooltip.directive";
import {CircleButtonComponent} from "../../circle-button/circle-button.component";
import {CheckboxInputComponent} from "../../checkbox-input/checkbox-input.component";
import {RadioButtonComponent} from "../../radio-button/radio-button.component";
import {DropdownComponent} from "../../dropdown/dropdown.component";
import {ReactiveFormsModule} from "@angular/forms";

export default {
  title: 'Structure/Test',
  component: TestStructureComponent,
  decorators: [
    moduleMetadata({
      declarations: [
        OverlayComponent,
        IconsComponent,
        ButtonComponent,
        InputFieldComponent,
        TooltipDirective,
        CircleButtonComponent,
        CheckboxInputComponent,
        RadioButtonComponent,
        DropdownComponent
      ],
      imports: [
        ReactiveFormsModule
      ]

    }),
  ]
} as Meta
export const Default = () => ({
  component: TestStructureComponent,
  props: {
    disabled: boolean('Disabled or read only', false),
    load: boolean('Load', false),
    questions: object('Data', [
      {
        title: 'First question', answers: [
          {value: 'Correct answer', correct: true, score: 1},
          {value: 'Wrong answer', correct: false, score: 1},],
        type: QuestionTypes.MULTIPLE_CHOICE
      },
      {
        title: 'Second question', answers: [
          {value: 'Correct answer', correct: true, score: 1},
          {value: 'Wrong answer', correct: false, score: 1},
          {value: 'Other wrong answer', correct: false, score: 1}],
        type: QuestionTypes.SINGLE_CHOICE
      }
    ]),
    disableAnswerSelection: boolean('Disable', false),
  }
});
