import {SmallDashletModel} from "../../../../../shared/src/lib/shared-model";
import {FileAttachment, StructureModule} from "../../../../../shared/src/lib/structures/content-structure/content-structure.model";


export interface TimedCourseProgress {
  moduleName: string;
  progress: number;
}

export interface TimedCourseStatisticBundleModel {
  statistics: TimedCourseStatistics[],
  dashlets: SmallDashletModel[]
}

export interface TimedCourseStatistics {
  username: string;
  feedback: string;
  hasFeedback: boolean;
  groupName: string;
  firstLaunchDate: string;
  lastLaunchDate: string;
  isLate: boolean;
  role: string;
  score: number;
  progress: number;
  totalEnrollment: number;
  spentTime: string;
}

export interface TimedCourseStructure {
  attachments: FileAttachment[];
  originalContent: FileAttachment[];
  modules: StructureModule[];
}
