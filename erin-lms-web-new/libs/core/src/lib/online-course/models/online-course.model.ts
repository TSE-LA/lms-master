import {CourseModel, SmallDashletModel, TestQuestion} from "../../../../../shared/src/lib/shared-model";
import {UsersGroups} from "../../common/common.model";
import {SpentTimeRatio} from "../../report/model/report.model";


export interface OnlineCourseModel extends CourseModel {
  assignedLearners?: string[],
  assignedDepartments?: string[],
  assignedDepartmentNames?: string[],
  thumbnailUrl?: string;
  userGroups?: UsersGroups;
  surveyId?: string;
  hasSurvey?: boolean;
  certificateId?: string;
  hasCertificate?: boolean;
  contentId?: string;
  testId?: string;
  hasTest?: boolean;
  note?: string;
  progress?: number;
  publishDate?: Date;
  credit?: number;
  enrollmentState?: EnrollmentState;
}

export interface OnlineCourseTableViewModel extends CourseModel {
  thumbnailUrl: string;
  authorAndDate: string;
  surveyId?: string;
  certificateId?: string;
  chaptersCount?: number;
  progress?: number;
  enrollmentState?: EnrollmentState;
  viewersCount?: number;
}

export interface UpdateOnlineCourseModel extends CourseModel {
  thumbnailId: string;
  isUpdateThumbnail: boolean;
  hasTest: boolean;
  hasSurvey: boolean;
}

export interface ShortCourseModel {
  id: string;
  name: string;
}

export enum EnrollmentState {
  COMPLETED,
  IN_PROGRESS,
  NEW
}

export interface OnlineCourseProgress {
  moduleName: string;
  progress: number;
  sections?: OnlineCourseSections[];
}

export interface OnlineCourseSections {
  name: string;
  fileId?: string;
  index?: number;
}

export interface OnlineCourseStatisticBundleModel {
  statistics: OnlineCourseStatistic[];
  dashlets?: SmallDashletModel[];
}

export interface OnlineCourseStatistic {
  username: string;
  groupName?: string;
  progress: number;
  spentTimeRatio?: SpentTimeRatio;
  score?: string;
  firstLaunchDate: string;
  lastLaunchDate?: string;
  views: number;
  spentTime: string;
  certificateDate?: string;
  doneSurvey: boolean;
  spentTimeOnTest?: string;
}

export interface TestModel {
  testId: string;
  testName: string;
  threshold: number;
  attempt: number;
  questions: TestQuestion[];
}

export interface PublishingModel {
  assignedLearners: string[];
  assignedGroups: string[];
  sendEmail: boolean;
  sendSms: boolean;
  publishDate?: Date;
  memo?: string;
  //    TODO: Remove this field when version is 3.0.0
  autoChildDepartmentEnroll?: boolean;
}
export interface AttachmentModel{
  attachmentFolderId: string;
  fileName: string;
  attachmentId: string;
}

