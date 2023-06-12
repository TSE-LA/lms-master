import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";
import {TableColumn, TableColumnType} from "../../../../../shared/src/lib/shared-model";
import {AnswerResultType, ExamModel, ExamState, ExamType} from "./exam.model";

export const EXAM_TYPES = [
  {id: 'all', name: "БҮГД"},
  {id: ExamType.OFFICIAL, name: "Албан ёсны шалгалт"},
  {id: ExamType.SELF_TRAINING, name: "Өөрийгөө бэлдэх шалгалт"},
];

export const EXAM_SHOW_RESULT_TYPES = [
  {id: AnswerResultType.AFTER_EXAM, name: "Шалгалт дууссаны дараа харуулах"},
  {id: AnswerResultType.SHOW_CORRECT, name: "Шууд зөв хариулт харуулах"},
  {id: AnswerResultType.SHOW_WRONG, name: "Шууд буруу хариулт харуулах"},
  {id: AnswerResultType.SHOW_NOTHING, name: "Зөв буруу хариулт харуулахгүй"},
];

export const EXAM_STATES = [
  {id: 'all', name: 'БҮГД'},
  {id: ExamState.NEW, name: 'Шинэ'},
  {id: ExamState.STARTED, name: 'Эхэлсэн'},
  {id: ExamState.FINISHED, name: 'Дууссан'}
];

export const EXAM_BANK_COLUMNS: TableColumn[] = [
  {name: ' ', id: 'notPublished', type: TableColumnType.STATUS, iconName: 'visibility_off', iconColor: 'gray', backgroundColor: 'none'},
  {name: 'Нэр', id: 'name', width: '2fr'},
  {name: 'Үргэлжлэх', id: 'duration'},
  {name: 'Тэнцэх оноо', id: 'passingScore', width: '0.7fr'},
  {name: 'Нийт оноо', id: 'maxScore', width: '0.7fr'},
  {name: 'Статус', id: 'examStatus'},
  {name: 'Эхлэх', id: 'start', width: '1.5fr'},
  {name: 'Дуусах', id: 'end', width: '1.5fr'},
  {name: 'Асуулт', id: 'questionCount', width: '0.5fr'},
  {name: 'Өөрчилсөн', id: 'modified'},
  {name: ' ', id: 'menu', type: TableColumnType.CONTEXT, iconName: 'more_vert'}
];

export const EXAM_EDIT: DropdownModel = {
  id: 'user.exam.edit',
  name: 'Засах',
  action: '/edit'
};

export const EXAM_DELETE: DropdownModel = {
  id: 'user.exam.delete',
  name: 'Устгах',
  action: '/delete',
  textColor: 'red'
};

export const EXAM_ACTIONS = [EXAM_EDIT, EXAM_DELETE]

export const DEFAULT_GROUP = [{
  parent: '',
  id: '',
  name: 'demo',
  nthSibling: 1,
  children: [{
    parent: '',
    id: '',
    name: 'demo child',
    nthSibling: 2,
    children: []
  }]
}];

export const EXAMINEE_TABLE_COLUMN: TableColumn[] = [
  {name: 'Нэр', id: 'name', width: '2fr'},
  {name: 'Үүсгэсэн', id: 'author'},
  {name: 'Эхлэх огноо', id: 'startDate'},
  {name: 'Дуусах огноо', id: 'endDate'},
  {name: 'Эхлэх цаг', id: 'startTime'},
  {name: 'Дуусах цаг', id: 'endTime'},
  {name: 'Статус', id: 'status'},
];

export const EXAM_REPORT_TABLE_COLUMN: TableColumn[] = [
  {name: 'Суралцагч', id: 'learnerName'},
  {name: 'Статус', id: 'learnerPassStatus'},
  {name: 'Оноо', id: 'score'},
  {name: 'Дүн', id: 'learnerGradeInPercentage'},
  {name: 'Зарцуулсан хугацаа', id: 'learnerSpentTime'},
  {id: "action", name: "Татах", type: TableColumnType.ACTION, iconName: 'file_download', iconColor: 'gray', backgroundColor: 'none'}
];

export const DEFAULT_EXAM: ExamModel = {
  id: null,
  name: "",
  description: "",
  categoryId: "",
  groupId: "",
  type: ExamType.OFFICIAL,
  enrolledGroups: [],
  enrolledLearners: [],
  publishDate: '2021-12-19',
  publishTime: "00 : 00",
  sendEmail: false,
  sendSms: false,
  mailText: "",
  smsText: "",
  notPublished: false,
  isEditable: false,
  config: {
    questionIds: new Set(),
    randomQuestions: [],
    autoStart: true,
    showAnswerResult: AnswerResultType.AFTER_EXAM,
    shuffleQuestion: true,
    shuffleAnswer: false,
    questionsPerPage: false,
    threshold: 1,
    attempt: 1,
    certificateId: null,
    startDate: '2021-12-19',
    endDate: '2021-12-19',
    startTime: "00 : 00",
    endTime: "00 : 00",
    duration: 60,
    maxScore: 0
  }
}
