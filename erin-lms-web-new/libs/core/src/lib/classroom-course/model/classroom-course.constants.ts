import {CourseState, EventAction, StateItem} from "./classroom-course.model";
import {UserRoleProperties} from "../../common/common.model";
import {TableColumn, TableColumnType} from "../../../../../shared/src/lib/shared-model";

export const MONTHS = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];
export const EVENT_PREVIEW_ROWS = [
  'Зохион байгуулагч',
  'Сургалтын төрөл',
  'Суралцагчийн тоо',
  'Багш'
];

export const GRADE_TABLE_KEYS = ['Багш', 'Дүн 1', 'Дүн 2', 'Дүн 3', 'Дундаж дүн', 'Ирц'];

export const ACTION_EDIT: EventAction = {
  name: 'Засах',
  action: '/edit',
  id: 'user.classroomCourse.update'
};

export const ACTION_START: EventAction = {
  name: 'Сургалт эхлүүлэх',
  action: '/start',
  id: 'user.classroomCourse.start'
};

export const ACTION_LAUNCH: EventAction = {
  name: 'Сургалт харах',
  action: '/launch',
  id: 'user.classroomCourse.view'
};

export const ACTION_EDIT_ATTENDANCE: EventAction = {
  name: 'Ирц бүртгэх',
  action: '/attendance',
  id: 'user.classroomCourse.gradeTable'
};

export const ACTION_DELETE: EventAction = {
  textColor: 'red',
  name: 'Устгах',
  action: '/delete',
  id: 'user.classroomCourse.delete'
};

export const ACTION_CANCEL: EventAction = {
  name: 'Цуцлах',
  action: '/cancel',
  id: 'user.classroomCourse.cancel'
};

export const ACTION_POSTPONE: EventAction = {
  name: 'Хойшлуулах',
  action: '/postpone',
  id: 'user.classroomCourse.postpone'
};

export const STATE_ITEMS: StateItem[] = [
  {
    role: UserRoleProperties.adminRole.id, name: 'ШИНЭ',
    state: CourseState.new, class: 'new',
    actions: [ACTION_EDIT, ACTION_DELETE],
  },
  {
    role: UserRoleProperties.adminRole.id, name: 'ИЛГЭЭСЭН',
    state: CourseState.sent, class: 'sent',
    actions: [ACTION_EDIT, ACTION_POSTPONE, ACTION_CANCEL, ACTION_DELETE],
  },
  {
    role: UserRoleProperties.adminRole.id, name: 'БҮРДЭЭГҮЙ', optionalName: 'БҮРДСЭН',
    state: CourseState.received, class: 'received',
    actions: [ACTION_EDIT, ACTION_POSTPONE, ACTION_CANCEL, ACTION_DELETE],
  },
  {
    role: UserRoleProperties.adminRole.id, name: 'БАТАЛГААЖСАН',
    state: CourseState.ready, class: 'ready', color: '#3399FF',
    actions: [ACTION_START, ACTION_EDIT, ACTION_POSTPONE, ACTION_CANCEL, ACTION_DELETE],
  },
  {
    role: UserRoleProperties.managerRole.id, name: 'БАТАЛГААЖСАН',
    state: CourseState.ready, class: 'ready', color: '#3399FF',
    actions: [ACTION_EDIT],
  },
  {
    role: UserRoleProperties.adminRole.id, name: 'ЭХЭЛСЭН',
    state: CourseState.started, class: 'started', color: '#0DC361',
    actions: [ACTION_EDIT_ATTENDANCE, ACTION_EDIT, ACTION_CANCEL, ACTION_DELETE],
  },
  {
    role: UserRoleProperties.adminRole.id, name: 'ДУУССАН',
    state: CourseState.done, class: 'done', color: '#ACAEB4',
    actions: [ACTION_DELETE],
  },
  {
    role: UserRoleProperties.adminRole.id, name: 'ЦУЦАЛСАН',
    state: CourseState.canceled, class: 'canceled', color: '#FF6565',
    actions: [ACTION_EDIT, ACTION_DELETE],
  },
  {
    role: UserRoleProperties.adminRole.id, name: 'ХОЙШЛОГДСОН',
    state: CourseState.postponed, class: 'postponed', color: '#FF9342',
    actions: [ACTION_EDIT, ACTION_CANCEL, ACTION_DELETE],
  },
  {
    role: UserRoleProperties.adminRole.id, name: 'НИЙТЛЭГДСЭН',
    state: CourseState.published, class: 'published', color: '#288bda',
    actions: [ACTION_EDIT, ACTION_CANCEL, ACTION_DELETE],
  }
];

export const CLASSROOM_COURSE_ATTENDANCE_COLUMN_INPROGRESS: TableColumn[] = [
  {
    id: 'action',
    name: '',
    type: TableColumnType.ACTION,
    iconName: "remove",
    backgroundColor: "warn",
    iconColor: "light"
  },
  {id: 'number', name: '№', type: TableColumnType.ORDERED_NUMBER},
  {id: 'displayName', name: 'Суралцагчийн нэр', width: '2fr'},
  {id: 'groups', name: 'Групп'},
  {id: 'supervisor', name: 'Ахлах'},
  {id: 'attendance', name: 'Ирц', type: TableColumnType.BOOLEAN},
  {id: 'score1', name: 'Дүн 1', type: TableColumnType.INPUT},
  {id: 'score2', name: 'Дүн 2', type: TableColumnType.INPUT},
  {id: 'score3', name: 'Дүн 3', type: TableColumnType.INPUT},
]

export const CLASSROOM_COURSE_ATTENDANCE_COLUMN_DONE = [
  {id: 'number', name: '№', type: TableColumnType.ORDERED_NUMBER},
  {id: 'displayName', name: 'Суралцагчийн нэр'},
  {id: 'groups', name: 'Групп'},
  {id: 'supervisor', name: 'Ахлах'},
  {id: 'attendance', name: 'Ирц', type: TableColumnType.BOOLEAN, disabled: true},
  {id: 'score1', name: 'Дүн 1'},
  {id: 'score2', name: 'Дүн 2'},
  {id: 'score3', name: 'Дүн 3'},
]
export const CLASSROOM_COURSE_TABLE_COLUMNS: TableColumn[] = [
  {id: 'courseName', name: 'Сургалтын нэр', width: '2fr'},
  {id: 'categoryName', name: 'Ангилал'},
  {id: 'status', name: 'Төлөв'},
  {id: 'maxEnrollmentCount', name: 'Хүний тоо'},
  {id: 'startDate', name: 'Эхлэх огноо'},
  {id: 'address', name: 'Байршил'},
  {id: 'published', name: 'Нийтэлсэн'},
  {id: 'creationDate', name: 'Үүсгэсэн огноо'},
  {id: 'menu', name: '', type: TableColumnType.CONTEXT, iconName: 'more_vert'}
];
export const COURSE_STATES = [
  {name: 'БҮГД', id: 'all'},
  {name: 'ШИНЭ', id: 'NEW'},
  {name: 'ЭХЭЛСЭН', id: 'STARTED'},
  {name: 'ДУУССАН', id: 'DONE'},
  {name: 'ЦУЦАЛСАН', id: 'CANCELED'},
  {name: 'ХОЙШЛУУЛСАН', id: 'POSTPONED'},
  {name: 'БАТАЛГААЖСАН', id: 'READY'},
];

export const SCORM_CMI_COMPLETION = "cmi.completion_status";
export const SURVEY_SUCCESSFUL_SENT_MESSAGE = 'Үнэлгээний хуудас амжилттай илгээгдлээ.';
export const SURVEY_WARNING_MESSAGE = 'Үнэлгээний хуудас нэг л удаа илгээгдэнэ. Үргэлжлүүлэх үү?';

