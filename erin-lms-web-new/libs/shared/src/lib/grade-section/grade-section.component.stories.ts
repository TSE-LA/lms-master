import {Meta} from "@storybook/angular";
import {object, select, text} from "@storybook/addon-knobs";
import {GradeSectionComponent} from "./grade-section.component";
import {GRADE_TABLE_KEYS} from "../../../../core/src/lib/classroom-course/model/classroom-course.constants";

export default {
  title: 'Section/Grade',
  component: GradeSectionComponent
} as Meta

export const Grade = () => ({
  component: GradeSectionComponent,
  props: {
    keys: object('Keys', GRADE_TABLE_KEYS),
    values: object('Values', ['Instructor Name', '95', '75', '85', '85', 'Ирсэн'])
  }
})
