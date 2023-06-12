import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";

export const TIMED_COURSE_ACTION_LAUNCH: DropdownModel = {name: 'Үзэх', action: '/launch', id: 'user.promotion.launch'};
export const TIMED_COURSE_ACTION_EDIT: DropdownModel = {name: 'Засах', action: '/edit', id: 'user.promotion.edit'};
export const TIMED_COURSE_ACTION_STATISTIC: DropdownModel = {name: 'Статистик', action: '/statistic', id: 'user.promotion.statistic'};
export const TIMED_COURSE_ACTION_DELETE: DropdownModel = {name: 'Устгах', action: '/delete', id: 'user.promotion.delete', textColor: 'red'};
export const TIMED_COURSE_ACTION_HIDE: DropdownModel = {name: 'Нуух', action: '/hide', id: 'user.promotion.hide'};
export const TIMED_COURSE_ACTION_CLONE: DropdownModel = {name: 'Хуулах', action: '/clone', id: 'user.promotion.clone'};
export const TIMED_COURSE_ACTION_READ: DropdownModel = {name: 'Унших', action: '/read', id: 'user.promotion.learn'};

export const TIMED_COURSE_PUBLISHED_ACTIONS: DropdownModel[] = [
  TIMED_COURSE_ACTION_LAUNCH,
  TIMED_COURSE_ACTION_EDIT,
  TIMED_COURSE_ACTION_STATISTIC,
  TIMED_COURSE_ACTION_CLONE,
  TIMED_COURSE_ACTION_HIDE,
  TIMED_COURSE_ACTION_DELETE
];
export const EXPIRED_PUBLISHED_TIMED_COURSE_ACTIONS: DropdownModel[] = [
  TIMED_COURSE_ACTION_LAUNCH,
  TIMED_COURSE_ACTION_EDIT,
  TIMED_COURSE_ACTION_STATISTIC,
  TIMED_COURSE_ACTION_CLONE,
  TIMED_COURSE_ACTION_HIDE,
  TIMED_COURSE_ACTION_DELETE
];
export const TIMED_COURSE_NOT_PUBLISHED_ACTIONS: DropdownModel[] = [
  TIMED_COURSE_ACTION_EDIT,
  TIMED_COURSE_ACTION_CLONE,
  TIMED_COURSE_ACTION_DELETE
];
export const TIMED_COURSE_PENDING_ACTIONS: DropdownModel[] = [
  TIMED_COURSE_ACTION_EDIT,
  TIMED_COURSE_ACTION_CLONE,
  TIMED_COURSE_ACTION_DELETE
];

export const TIMED_COURSE_READ_STATISTICS_ACTIONS: DropdownModel[] = [
  TIMED_COURSE_ACTION_READ,
  TIMED_COURSE_ACTION_STATISTIC
]

export const TIMED_COURSE_READ_ACTION: DropdownModel[] = [
  TIMED_COURSE_ACTION_READ
]

export const TARGET_OPTIONS: DropdownModel[] = [
  {id: 'Mass', name: 'Mass'},
  {id: 'Direct', name: 'Direct'}
]

