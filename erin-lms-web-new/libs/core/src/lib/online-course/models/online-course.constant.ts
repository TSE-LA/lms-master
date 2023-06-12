import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";
import {AnswerChoice, CategoryItem, QuestionTypes, TestQuestion} from "../../../../../shared/src/lib/shared-model";
import {DEFAULT_ANSWER} from "../../../../../shared/src/lib/shared-constants";

export const DEFAULT_THUMBNAIL_URL = '/alfresco/default-thumbnail.jpeg';


export const USER_ROLES = {
  LMS_ADMIN: 'Багш',
  LMS_SUPERVISOR: 'Ахлах',
  LMS_USER: 'Суралцагч',
  LMS_MANAGER: 'Менежер'
}

export const PUBLISH_STATUS = {
  PUBLISHED: 'PUBLISHED',
  UNPUBLISHED: 'UNPUBLISHED',
  READY_TO_PUBLISH: 'READY_TO_PUBLISH'
}

export const ONLINE_COURSE_EDIT: DropdownModel = {
  id: 'user.onlineCourse.edit',
  name: 'Засах',
  action: '/edit'
};

export const ONLINE_COURSE_STATISTICS: DropdownModel = {
  id: 'user.onlineCourse.analytic',
  name: 'Статистик',
  action: '/statistics'
};

export const ONLINE_COURSE_DELETE: DropdownModel = {
  id: 'user.onlineCourse.delete',
  name: 'Устгах',
  action: '/delete',
  textColor: 'red'
};

export const ONLINE_COURSE_LAUNCH: DropdownModel = {
  id: 'user.onlineCourse.launch',
  name: 'Үзэх',
  action: '/launch'
};
export const ONLINE_COURSE_HIDE: DropdownModel = {
  id: 'user.onlineCourse.hide',
  name: 'Нуух',
  action: '/hide'
}
export const ONLINE_COURSE_UNPUBLISHED_ACTION: DropdownModel[] = [
  ONLINE_COURSE_EDIT,
  ONLINE_COURSE_DELETE
];

export const ONLINE_COURSE_PUBLISHED_ACTION: DropdownModel[] = [
  ONLINE_COURSE_LAUNCH,
  ONLINE_COURSE_EDIT,
  ONLINE_COURSE_STATISTICS,
  ONLINE_COURSE_HIDE,
  ONLINE_COURSE_DELETE
];

export const ONLINE_COURSE_CARD_BUTTON_TEXTS = {
  PUBLISHED: "ҮЗЭХ",
  UNPUBLISHED: "ЗАСАХ",
  PENDING: 'ЗАСАХ'
}

export const EMPTY_CATEGORY: CategoryItem = {id: '', name: ''};

export const DEFAULT_ANSWER_STRUCTURE: AnswerChoice[] = [
  DEFAULT_ANSWER,
  DEFAULT_ANSWER];
export const DEFAULT_QUESTION: TestQuestion = new TestQuestion("", DEFAULT_ANSWER_STRUCTURE, QuestionTypes.SINGLE_CHOICE);
