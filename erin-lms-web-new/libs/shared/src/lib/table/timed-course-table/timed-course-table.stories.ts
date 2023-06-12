import {Meta, moduleMetadata} from '@storybook/angular';
import {object} from "@storybook/addon-knobs";
import {TimedCourseTableComponent} from "./timed-course-table.component";
import {InputFieldComponent} from "../../input-field/input-field.component";
import {TIMED_COURSE_COLUMN} from "../model/table-constants";
import {NotFoundPageComponent} from "../../not-found-page/not-found-page.component";

export default {
  title: 'Table/TimedCourse',
  component: TimedCourseTableComponent,
  decorators: [
    moduleMetadata({
      declarations: [InputFieldComponent, NotFoundPageComponent]
    }),
  ],
} as Meta

export const TimedCourseTable = () => ({
  component: TimedCourseTableComponent,
  props: {
    tableColumns: object('Table Columns', TIMED_COURSE_COLUMN)
  }
})



