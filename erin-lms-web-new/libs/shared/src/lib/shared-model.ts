import {ModuleFileType} from "./structures/content-structure/content-structure.model";
import {SortDirection} from "./table/model/table-constants";

export interface CourseModel {
  id: string;
  name: string;
  description: string;
  author?: string;
  createdDate?: string;
  typeId?: string;
  categoryId?: string;
  publishStatus?: PublishStatus;
  isReadyToPublish?: boolean;
  modifiedDate?: string;
}

export interface TimedCourseModel extends CourseModel {
  startDate: string;
  endDate: string;
  keyword: string;
  state: string;
  code: string,
  target: string;
  department?: string;
  contentId?: string;
  hasTest?: boolean;
  hasFeedBack?: boolean;
  enrollments?: Enrollments;
  status?: Status;
  publishDate?: Date;
  categoryName?: string;
  new?: boolean;
  note?: string;
}

export interface CalendarPageProperties {
  months: CalendarDayProperties[]
}

export interface CalendarDayProperties {
  month?: string,
  index: number,
  events: CalendarEvent[]
}

export interface CalendarEvent {
  title: string,
  state: string,
  courseId: string,
  eventType: string
}

export interface LearnerInfo {
  name: string,
  role: string,
  lastname?: string,
  firstname?: string,
  username?: string;
  groupPath?: string,
  selected?: boolean,
  groupName?: string,
  groupId?: string,
  cannotRemove?: boolean,
  highlight?: boolean;
}

export interface SnackbarConfig {
  title: string
  status: string,
  delay: number,
  position: string

}

export interface AnswerChoice {
  value: string;
  correct: boolean;
  weight: number;
  column: number;
  formName?: string;
}

export enum QuestionTypes {
  SINGLE_CHOICE = 'SINGLE_CHOICE',
  MULTIPLE_CHOICE = 'MULTI_CHOICE',
  FILL_IN_BLANK = 'FILL_IN_BLANK'
}

export class TestQuestion {
  question: string;
  answers: AnswerChoice[];
  type: QuestionTypes;
  required: boolean;

  constructor(title: string, choices: AnswerChoice[], type: QuestionTypes) {
    this.question = title;
    this.answers = choices;
    this.type = type
  }

  hasCorrectAnswers(): boolean {
    for (const choice of this.answers) {
      if (choice.correct) {
        return true;
      }
    }
    return false;
  }

  hasCorrectAnswer(): boolean {
    let status = false;
    const atLeastOneCorrect = this.hasAtleastOneCorrect();
    if (atLeastOneCorrect && this.type === QuestionTypes.MULTIPLE_CHOICE) {
      status = this.hasCorrectAnswerMultipleChoice();
    } else if (atLeastOneCorrect && this.type === QuestionTypes.SINGLE_CHOICE) {
      status = this.hasCorrectAnswerClosed();
    }
    return status;
  }

  hasAtleastOneCorrect(): boolean {
    for (const choice of this.answers) {
      if (choice.correct) {
        return true
      }
    }
    return false;
  }

  hasCorrectAnswerClosed(): boolean {
    let atLeastOneCorrect = false;
    for (const choice of this.answers) {
      if (choice.correct) {
        atLeastOneCorrect = true;
        break;
      }
    }
    return atLeastOneCorrect;
  }

  hasCorrectAnswerMultipleChoice(): boolean {
    for (const choice of this.answers) {
      if (!choice.correct) {
        return true;
      }
    }
    return false;
  }


  hasValidAnswers(): boolean {
    for (const answer of this.answers) {
      if (answer.value.length < 1) {
        return false;
      }
    }

    return true;
  }

  hasDuplicatedAnswers(): boolean {
    const uniqueAnswers = this.answers.reduce((acc, curr) => {
      if (!acc.find(answer => answer['value'] === curr['value'])) {
        acc.push(curr);
      }
      return acc;
    }, []);

    return this.answers.length !== uniqueAnswers.length;
  }
}

export interface Enrollments {
  users: string[];
  groups: string[];
  managers?: string[];
}

export enum PublishStatus {
  PUBLISHED = 'PUBLISHED',
  UNPUBLISHED = 'UNPUBLISHED',
  PENDING = 'PENDING',
}

export enum CourseState {
  EXPIRED = 'EXPIRED',
  MAIN = 'MAIN',
  CURRENT = 'CURRENT',
}

export enum TableColumnType {
  ACTION = "ACTION",
  TEXT = "TEXT",
  STATUS = "STATUS",
  ORDERED_NUMBER = "ORDERED_NUMBER",
  CONTEXT = "CONTEXT",
  PROGRESS = "PROGRESS",
  TEXT_PROGRESS = "TEXT_PROGRESS",
  NAVIGATE = "NAVIGATE",
  SURVEY_DOWNLOAD = "SURVEY_DOWNLOAD",
  SHOW_FEEDBACK = "SURVEY_DOWNLOAD",
  BOOLEAN = "BOOLEAN",
  INPUT = "INPUT",
  ICON = "ICON"
}

export enum CourseType {
  CLASSROOM_COURSE = 'ClassroomCourse',
  ONLINE_COURSE = 'OnlineCourse',
  WEBINAR = 'Webinar'
}

export enum CourseState {
  STARTED = 'Started',
  CANCELED = 'Canceled',
  FINISHED = 'Finished',
  POSTPONED = 'Postponed',
  EXPECTED = 'Expected'
}


export interface TableColumn {
  name: string,
  id: string,
  type?: TableColumnType,
  iconName?: string,
  iconColor?: string,
  backgroundColor?: string,
  sortDirection?: SortDirection,
  disabled?: boolean,
  width?: string,
  action?: boolean,
  tooltip?: string
}

export interface SmallDashletModel {
  title?: string,
  info?: any,
  imageSrc?: string,
  date?: string,
  hasDropDown?: boolean,
  navigateLink?: string,
  extraData?: any,
  empty?: boolean
}

export enum Status {
  NEW = '#',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = '',
  AUDIT = 'AUDIT'
}

export interface GroupNode {
  parent: string;
  id: string;
  name: string;
  children?: GroupNode[];
  nthSibling?: number;
  color?: string;
  editable?: boolean;
  path?: string;
  selected?: boolean;
  indeterminate?: boolean;
  showChildren?: boolean;
  checked?: boolean;
  allChildChecked?: boolean;
}

export interface QuestionTypeModel {
  name: string;
  value: QuestionTypes
}

export interface Question {
  id: string;
  value: string;
  type: QuestionTypeModel
  answers: AnswerChoice[];
  totalScore: number;
  category: CategoryItem;
  group: GroupNode;
  correctText: string;
  wrongText: string;
  contentId: string;
  contentName: string;
  hasContent: boolean;
}

export interface SimpleQuestion {
  id: string;
  name: string;
  type: QuestionTypeModel
  totalScore: number;
  categoryId: string;
  groupId: string;
}

export interface CategoryItem {
  id: string;
  name: string;
  index?: number;
  newCount?: number;
  count?: number;
  color?: string;
}

export interface RandomQuestion {
  index: number;
  category: CategoryItem;
  group: GroupNode;
  score: number;
  totalAmount: number;
  available: number;
}

export interface StructureUploadFile {
  file: File,
  module: number,
  section: number,
  uploadFileType: ModuleFileType
}

export interface SearchModel {
  searchValue: any;
  byName: boolean;
  byDescription: boolean;
  byCategory: boolean;
  courseType?: string;
}

export interface CourseShortModel {
  id: string;
  name: string;
}
