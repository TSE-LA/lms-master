import {Meta, moduleMetadata} from "@storybook/angular";

import {boolean, number, object} from "@storybook/addon-knobs";
import {CourseCountDashletComponent} from "./course-count-dashlet.component";
import {CHART_DATA, DASHLET_DATA} from "../dashlet-model";
import {TooltipDirective} from "../../tooltip/tooltip.directive";
import {DropdownComponent} from "../../dropdown/dropdown.component";
import {DropdownInputComponent} from "../../dropdown-input/dropdown-input.component";
import {DropdownViewComponent} from "../../dropdown-view/dropdown-view.component";

export default {
  title: 'Dashlets/Dashlet',
  component: CourseCountDashletComponent,
  parameters: {
    layout: 'centered',
  },
   decorators: [
    moduleMetadata({
      declarations: [TooltipDirective, DropdownComponent, DropdownInputComponent, DropdownViewComponent],
    }),
  ],
} as Meta

export const CourseCountByCategory = () => ({
  component: CourseCountDashletComponent,
  props: {
    totalCount: number('Total count', 106,),
    chartData: object('Chart data', CHART_DATA),
    dashletInfo: object('Dashlet info', DASHLET_DATA),
    loading: boolean('Loading', false)
  }
});
