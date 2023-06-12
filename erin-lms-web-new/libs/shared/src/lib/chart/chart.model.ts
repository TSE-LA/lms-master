import {ChartType} from "chart.js";

export interface ChartDataModel {
  labels: string[];
  data: any[];
  chartType: ChartType;
  index?: number;
  text?: string;
}

export const CHART_COLORS = ['#1ABC9C', '#3B86FF', '#d4c44e', '#cc2929', '#d9a7a7', '#008f91', '#9e6f00', '#af0000', '#204ad4', '#4c90ba', '#adadad', '#9e6f00', '#de663e']
