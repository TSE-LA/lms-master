import {CategoryItem, GroupNode} from "../../../../shared/src/lib/shared-model";

export class NotificationModel {
  categories: Map<string, Notif> = new Map();

  constructor(categories?: Map<string, Notif>) {
    if (categories) {
      this.categories = categories;
    }
  }
}

export class Notif {
  total: number;
  userNewTotal: number;
  subCategory: Map<string, Notif> = new Map();

  constructor(total: number, userNewTotal: number) {
    this.total = total;
    this.userNewTotal = userNewTotal;
  }
}

export class AuthModel {
  userName: string;
  token: string;
  role: string;
  userGroups: string;
  permissions: PermissionItem[];
}

export interface FilterValue {
  columnName: string;
  value: string;
}

export interface Properties {
  disable: boolean;
  visible: boolean;
}

export interface ColumnDef {
  columnDef: string;
  headerText: string;
  progress?: boolean;
}

export interface PermissionItem {
  id: string;
  properties: Properties;
}

export interface User {
  userId: string;
  firstName: string;
  lastName: string;
}

export interface GroupMembers {
  group: Group;
  users: SimpleUserInfo[];
}

export interface Unit {
  value: string;
  alterName?: string;
  isUser: boolean;
  isGroup: boolean;
}

export interface UsersByGroup {
  allUsers: Map<string, DetailedUserInfo>;
  allMyUsers: Map<string, DetailedUserInfo>;
  groups: GroupNode
}

export class RoleProperties {
  name: string;
  roleUrl: string;
  id: string;
}

export class ComponentAction {
  id: string;
  actionIcon: string;
  actionName: string;
  action: string;
}

export interface FilterValue {
  columnName: string;
  value: string;
}

export interface IntervalDate {
  startDate: Date;
  endDate: Date;
}

export enum DateRange {
  TODAY = 'Өнөөдөр',
  YESTERDAY = 'Өчигдөр',
  WEEK = 'Долоо хоног',
  MONTH = 'Сар',
  YEAR = 'Жил'
}

export interface CourseProgress {
  moduleName: string;
  progress: number;
  sections?: CourseSections[];
}

export interface CourseSections {
  name: string;
  fileId?: string;
  index?: number;
}

export class UserRoleProperties {
  public static employeeRole: RoleProperties = {
    name: 'Суралцагч',
    id: 'LMS_USER',
    roleUrl: '/user-home-container'
  };
  public static supervisorRole: RoleProperties = {
    name: 'Ахлах',
    id: 'LMS_SUPERVISOR',
    roleUrl: '/supervisor-home-container'
  };
  public static adminRole: RoleProperties = {
    name: 'Админ',
    id: 'LMS_ADMIN',
    roleUrl: '/admin-home-container'
  };
  public static managerRole: RoleProperties = {
    name: 'Менежер',
    id: 'LMS_MANAGER',
    roleUrl: '/supervisor-home-container'
  };
}

export const FILTER_ROLES: RoleProperties[] = [
  {
    name: "БҮГД",
    roleUrl: "/all",
    id: "БҮГД"
  },
  UserRoleProperties.managerRole, UserRoleProperties.supervisorRole, UserRoleProperties.employeeRole]

export interface ReportType {
  id: string;
  name: string;
}

export interface UsersGroups {
  users: string[];
  groups: string[];
  managers?: string[];
}

export interface DetailedCourseModel {
  id: string;
  name: string;
  category: string;
  description: string;
  state: string;
  createdDate?: Date;
  endDate?: Date;
  startDate?: Date;
  code?: string;
  keyword?: string;
  target?: string;
  userGroups?: UsersGroups;
  modifiedDate?: Date;
  contentId?: string;
  hasTest?: boolean;
  hasFeedBack?: boolean;
  note?: string;
  publishDate?: Date;
  assessmentId?: string;
  hasSurvey?: boolean;
  certificateId?: string;
  hasCertificate?: boolean;
  author?: string;
  type?: string;
}

export const COURSE_PUBLISH_STATUS = [
  {name: 'БҮГД', id: 'all'},
  {name: 'НИЙТЭЛСЭН', id: 'published'},
  {name: 'НИЙТЛЭЭГҮЙ', id: 'unpublished'},
  {name: 'ХҮЛЭЭГДЭЖ БУЙ', id: 'pending'}
];

export interface TestChoice {
  value: string;
  correct: boolean;
  score: number;
}

export class AssessmentsModel {
  hasTest: boolean;
  hasFeedBack: boolean;
}

export interface ContentModule {
  documentPaths: ContentSection[];
  moduleIndex?: number;
}

export interface ContentSection {
  sectionPath: string;
  sectionIndex?: number;
}

export interface DateInterval {
  startDate: Date;
  endDate: Date;
  selectedValue?: string;
}

export interface DialogData {
  title?: string;
  message: string;
  alternativeMessage?: string;
  confirmButton?: string;
  hideCancelButton?: boolean;
}

export class RowEvent {
  row: any;
  $event: MouseEvent;
}

export const GREEN_CHIP_COLOR = 'var(--primary-color)';
export const WHITE_TEXT_COLOR = '#fffff0';
export const PALE_CHIP_COLOR = '#e0e0e0';
export const GRAY_TEXT_COLOR = '#535353';

export const ALL_CHOICE: CategoryItem = {name: 'БҮГД', id: 'all'};

export interface DetailedUserInfo {
  userId: string;
  username: string;
  displayName: string;
  email: string;
  phoneNumber: string;
  membership: Membership;
  clicked?: boolean;
  groupClicked?: boolean;
  path?: string;
  lastName?: string;
  firstName?: string;
}

export interface SimpleUserInfo {
  userId: string;
  username: string;
  displayName: string;
}

export interface UserInfo {
  userId?: string;
  username: string;
  displayName?: string;
  groupColor?: string;
  groupPath?: string;
  groupId?: string;
  email: string;
  phoneNumber: string;
  role?: string;
  roleId?: string;
  moreOption?: boolean;
  clicked?: boolean;
  groupClicked?: boolean;
  isActive?: boolean;
  membershipId?: string;
}

export interface Membership {
  userId: string;
  groupId: string;
  membershipId: string;
  roleId: string;
  username?: string
  displayName?: string;
  groupPath?: string;
  groupColor?: string;
}

export interface AddUserModel {
  roleId: string;
  users: SimpleUserInfo[];
}

export interface Role {
  id: string;
  name: string;
}

export interface Group {
  id: string;
  name: string;
}

export interface CheckedGroup {
  groupId: string;
  checkStatus: string;
}

export interface CheckedGroup {
  groupId: string;
  checkStatus: string;
}

export interface Survey {
  id: string;
  name: string;
  questionCount: number;
  status: SurveyStatus;
  createdDate: string;
  modifiedDate: string;
  author: string;
  description: string;
}

export enum SurveyStatus {
  ACTIVE,
  INACTIVE
}
