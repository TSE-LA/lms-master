import {SmallDashletModel} from "../../../../../shared/src/lib/shared-model";
import {ClassroomReportModel} from "../../classroom-course/model/classroom-course.model";

export interface TimedCourseReportModel {
  reports: TimedCourseReport[];
  dashlets: SmallDashletModel[];
}

export interface TimedCourseReport {
  category?: string;
  state?: string;
  id: string;
  code: string;
  name: string;
  createdDate: string;
  date?: Date;
  author: string;
  progress: number;
  totalEnrollment: number;
  views: number;
  hasTest: string;
  questions: number;
  score: number;
  feedbackCount: number;
}

export interface OnlineCourseReport {
  id?: string;
  name?: string;
  type?: string;
  state?: string;
  enrolledGroup?: EnrolledGroup[];
  enrollmentCount?: number;
  totalViewers?: number;
  progress?: number;
  completedViewers?: number;
  hasCertificate?: string;
  repeatedViewersCount?: number;
  survey?: number;
  receivedViewers?: number;
  author?: string;
  enrolledGroupIds: string[];
  enrolledLearners: string[];
  averageSpentTimeOnTest: string;
  testScore: string;
}

export interface OnlineCourseAnalytics {
  report: OnlineCourseReport[];
  groupEnrollmentCount;
  dashlets: SmallDashletModel[];
}

export interface CourseActivityReportModel {
  dashlet: SmallDashletModel;
  reports: CourseActivityReport[];
}

export interface ClassroomCourseReportModel {
  dashlets: SmallDashletModel[];
  reports: ClassroomReportModel[];
}

export interface CourseActivityReport {
  categoryId?: string;
  category?: string;
  courseType?: string;
  promoCategory?: string;
  name: string;
  progress?: number;
  spentTimeRatio?: SpentTimeRatio;
  testScore: number;
  certification?: string;
  feedBack?: string;
  assessment?: string;
  views?: number;
  spentTime?: string;
  firstViewDate?: string;
  lastViewDate?: string;
  attendance?: any;
  teacher?: string;
  startTime?: string;
  endTime?: string;
  date?: string;
  spentTimeOnTest?: string;
}

export interface SpentTimeRatio {
  name: number;
  toolTip?: string;
}

export interface EnrolledGroup {
  enrollmentCount?: number;
  enrolledGroup: string;
}

export interface SurveyReportAnswer {
  value: string;
  count?: number;
}

export interface SurveyReport {
  index: number;
  question: string;
  questionType: string;
  answers: SurveyReportAnswer[];
}

export interface UserInfoWithContacts {
  name: string;
  displayName: string;
  email: string;
  phoneNumber: string;
  role: string;
  lastName: string;
  firstName: string;
}

export interface DetailedExamReportTableModel {
  id: string,
  title: string,
  category: string,
  status: string,
  duration: string,
  questionCount: number,
  totalRuntime: string,
  passedCount: string,
  averageScore: string,
  averageSpentTime: string,
  statistics: boolean
}

export interface ExamReportMappedModel {
  category: string;
  type: string;
  status: string;
  group: string;
}
