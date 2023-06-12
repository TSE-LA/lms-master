import {DataModelConstraint} from "../constant/data-model-constraint";

export interface RuntimeDataModel {
  data: string;
  readonly constraint: DataModelConstraint;
}
export interface ScoModel {
  scoName: string;
  path: string;
  index?: number;
  runtimeData: Map<string, RuntimeDataModel>;
  isTest: boolean;
  isQuestionnaire: boolean;
  isSurvey?: boolean;
}
