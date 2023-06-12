import {QuestionTypeModel, QuestionTypes, TableColumn} from "./shared-model";

export const DEFAULT_ANSWER = {value: null, correct: false, weight: 1, id: 0, column: 1, formName: '0'};
export const TIMED_COURSE_COLUMN: TableColumn[] = [
  {name: 'ТӨРӨЛ', id: 'categoryName'},
  {name: 'ТҮЛХҮҮР ҮГ', id: 'keyword'},
  {name: 'КОД', id: 'code'},
  {name: 'НӨХЦӨЛИЙН НЭР', id: 'courseName'},
  {name: 'ЭХЛЭХ', id: 'startDate'},
  {name: 'ДУУСАХ', id: 'endDate'},
  {name: 'НИЙТЭЛСЭН', id: 'createdAdmin'},
  {name: 'НИЙТЭЛСЭН ОГНОО', id: 'cratedDate'}
];

export const CALENDAR_FILTERS_TYPE = [
  {id: 'classroom-course', name: 'Танхимын сургалт', color: '#2BB18B', background: '#E3FFEC', checked: true},
  {id: 'online-course', name: 'Цахим сургалт', color: '#288BDA', background: '#E8F7FD', checked: true},
  // {id: 'webinar', name: 'Вебинар', color: '#5C24B2', background: '#EEEAFF', checked: true}
]

export const CALENDAR_FILTERS_STATE = [
  {id: 'PUBLISHED', name: 'Нийтлэгдсэн', color: '#0e72ea', background: '#e6f0fc', checked: true},
  {id: 'STARTED', name: 'Эхэлсэн', color: '#0DC361', background: '#E8F8F5', checked: true},
  {id: 'POSTPONED', name: 'Хойшлогдсон', color: '#FF9342', background: '#FFF4EC', checked: true},
  {id: 'CANCELED', name: 'Цуцалсан', color: '#FF6565', background: '#FFEFEF', checked: true},
  {id: 'DONE', name: 'Дууссан', color: '#ACAEB4', background: '#F7F7F9', checked: true}
]

export const QUESTION_TYPES: QuestionTypeModel[] = [
  {name: "Олон сонголттой", value: QuestionTypes.MULTIPLE_CHOICE},
  {name: "Нэг сонголттой", value: QuestionTypes.SINGLE_CHOICE}
]
export const SELECT_QUESTION_TYPE: QuestionTypeModel[] = [
  {name: "Нэг сонголттой", value: QuestionTypes.SINGLE_CHOICE}
]
export const TIMED_COURSE_SEARCH = 'timed-course';
export const ONLINE_COURSE_SEARCH = 'online-course';
export const CLASSROOM_COURSE_SEARCH = 'classroom-course';
