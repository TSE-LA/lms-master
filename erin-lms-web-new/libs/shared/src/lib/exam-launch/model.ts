import {EventAction} from "../../../../core/src/lib/classroom-course/model/classroom-course.model";

export interface ExamDetailKey {
  name: string;
  icon?: string;
  color?: string;
}
export interface ExamDetail {
  keys: ExamDetailKey[];
  values: any[];
}
