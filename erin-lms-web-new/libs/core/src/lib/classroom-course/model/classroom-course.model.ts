import {CourseModel} from "../../../../../shared/src/lib/shared-model";
import {FileAttachment} from "../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {DetailedUserInfo, UsersByGroup} from "../../common/common.model";
import {UserModel} from "../../user-management/models/user-management.model";

export interface ClassroomCourseModel extends CourseModel {
  address: string;
  instructor: string;
  instructorNumber: string;
  certificateId: string;
  surveyId: string;
  hasCertificate: boolean;
  hasSurvey: boolean;
  enrollment: Enrollment;
  state: string;
  previousState: string;
  dateAndTime: ClassroomDateAndTime;
  attachment?: FileAttachment;
  hasCompletedSurvey?: boolean;
  reason?: string;
  duration?: string;
  categoryName?: string;
  credit?: number;
}

export interface NewClassroomCourseModel extends CourseModel {
  categoryName?: string;
  address: string;
  instructor: string;
  instructorNumber: string;
  certificateId: string;
  surveyId: string;
  hasCertificate: boolean;
  hasSurvey: boolean;
  enrollment: Enrollment;
  state: string;
  previousState: string;
  dateAndTime: ClassroomDateAndTime;
  attachment?: FileAttachment;
  hasCompletedSurvey?: boolean;
  reason?: string;
  duration?: string;
  credit?: number;
}

export interface ClassroomCourseUpdateModel extends CourseModel {
  instructor: string,
  instructorNumber: string,
  maxEnrollmentCount: number,
  state: string,
  previousState: string,
  attachment: FileAttachment,
  surveyId: string,
  certificateId: string,
  credit?: number,
  address: string,
  startDate: Date,
  startTime: string,
  endTime: string
}

export interface ClassroomDateAndTime {
  startTime: string;
  endTime: string;
  dateString?: string;
  date?: Date;
  weekday?: string;
}

export interface Enrollment {
  assignedDepartments: string[];
  assignedLearners: string[];
  maxEnrollmentCount: number;
  groups?: any[];
  enrollmentMerged?: string;
  enrollmentCount?: number;
  isClassroomFull?: boolean;
  hasEnrolled?: boolean;
  departmentNames?: string;
}

export interface ClassroomReportModel extends CourseModel {
  surveyId: string;
  certificateId: string;
  address: string;
  instructor: string;
  instructorNumber: string;
  dateString: string;
  date: Date,
  startTime: string;
  endTime: string;
  assignedDepartments: string[];
  assignedLearners: string[];
  departmentNames: string;
  maxEnrollmentCount: number;
  enrollmentCount: number;
  isClassroomFull: boolean;
  hasEnrolled: boolean;
  state: string;
  groups?: any[];
  hasCertificate?: boolean;
  hasSurvey?: boolean;
  previousState?: string;
  attachmentName?: string;
  attachmentType?: string;
  reason?: string;
  duration?: string;
  categoryName?: string;
}

export interface CreateClassroomCourseModel {
  name: string;
  categoryId: string;
  type: string;
  address: string;
  instructor: string;
  instructorNumber: string;
  maxEnrollmentCount: number;
  attachedFileId?: string;
  attachFileName?: string;
  attachFileRenderedName?: string;
  description: string;
  surveyId?: string;
  certificateId?: string;
  credit?: number;
  state?: string;
  previousState?: string;
  dateItems?: DateItem[];
}

export interface CreateClassroomCourseTableModel {
  id: string;
  courseName: string;
  categoryName: string;
  status: string;
  maxEnrollmentCount: string;
  startDate: string;
  address: string;
  published: string;
  creationDate: string;
}

export enum CourseState {
  new = 'new',
  sent = 'sent',
  received = 'received',
  ready = 'ready',
  started = 'started',
  done = 'done',
  canceled = 'canceled',
  postponed = 'postponed',
  published = 'published'
}

export enum ClassroomCourseState {
  NEW = 'NEW',
  SENT = 'SENT',
  RECEIVED = 'RECEIVED',
  READY = 'READY',
  STARTED = 'STARTED',
  DONE = 'DONE',
  CANCELED = 'CANCELED',
  POSTPONED = 'POSTPONED'
}

export interface DateItem {
  date: Date;
  startHour: number;
  startMinute: number;
  endHour: number;
  endMinute: number;
}

export interface CourseProperties {
  courseName: string,
  date?: string,
  organizerName: string,
  courseType: string | void,
  courseCategory: string,
  time?: string
  courseState?: string,
  moderator: string,
  location: string,
  count: string,
  type: string,
  courseTypeColor: string,
  courseStateColor: string,
  attachment?: FileAttachment,
  actions?: EventAction[],
}

export interface EventActions {
  id: number,
  name: string
}

export interface EventAction {
  id: string;
  name: string;
  action: string;
  textColor?: string;
}

export interface StateItem {
  name: string;
  optionalName?: string;
  role: string;
  class: string;
  color?: string;
  state: CourseState;
  actions: EventAction[];
}

export interface ClassroomCourseAttendanceModel {
  displayName: string;
  username: string;
  groups: string;
  supervisor: string;
  invitation: Invitation;
  attendance: boolean;
  score1?: number;
  score2?: number;
  score3?: number;
}

export enum Invitation {
  SENT,
  UNSENT,
}

export interface ClassroomUsersModel {
  allCurrentUsers: DetailedUserInfo[];
  notInGroupUsers: string[];
  usersByGroup: UsersByGroup;
}

export interface InstructorDropdownModel extends UserModel {
  id: string,
  name: string,
}
