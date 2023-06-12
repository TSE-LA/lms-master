import {
  AnswerChoice,
  Question,
  RandomQuestion,
  SimpleQuestion,
  TableColumn,
  TableColumnType
} from "../../../../../shared/src/lib/shared-model";
import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";
import {QUESTION_TYPES} from "../../../../../shared/src/lib/shared-constants";

export const QUESTION_BANK_COLUMNS: TableColumn[] = [
  {name: ' ', id: 'hasImage', type: TableColumnType.STATUS, iconName: 'image_blank', iconColor: 'gray', backgroundColor: 'none'},
  {name: 'Нэр', id: 'value', width: '2fr'},
  {name: 'Групп', id: 'group'},
  {name: 'Ангилал', id: 'category'},
  {name: 'Оноо', id: 'score', width: '0.5fr'},
  {name: 'Төрөл', id: 'type'},
  {name: 'Үүсгэсэн', id: 'created'},
  {name: 'Өөрчилсөн', id: 'modified'},
  {name: ' ', id: 'menu', type: TableColumnType.CONTEXT, iconName: 'more_vert'}
];

export const DEFAULT_QUESTION: Question = {
  value: "", type: QUESTION_TYPES[1], id: "",
  category: {name: "", id: ""},
  group: {name: "", id: "", parent: "", nthSibling: 0, children: []},
  correctText: "",
  wrongText: "",
  totalScore: 1,
  contentId: "",
  hasContent: false,
  contentName: "",
  answers: []
};

export const DEFAULT_ANSWER_1: AnswerChoice = {value: null, correct: true, weight: 1, column: 1, formName: '0'};
export const DEFAULT_ANSWER_2: AnswerChoice = {value: null, correct: true, weight: 0, column: 1, formName: '1'};
export const DEFAULT_ANSWER_3: AnswerChoice = {value: null, correct: true, weight: 0, column: 1, formName: '2'};

export const DEFAULT_SIMPLE_QUESTION: SimpleQuestion = {
  name: "Энэ бол асуулт",
  type: QUESTION_TYPES[0],
  id: "",
  categoryId: "",
  groupId: "",
  totalScore: 1
};

export const DEFAULT_RANDOM_QUESTION: RandomQuestion = {
  index: 1,
  category: {id: "123", name: "Oyukas"},
  group: {id: "123", name: "groups", children: [], parent: null, nthSibling: 1},
  score: 1,
  totalAmount: 12,
  available: 100
};

export const QUESTION_EDIT: DropdownModel = {
  id: 'user.question.edit',
  name: 'Засах',
  action: '/edit'
};

export const QUESTION_DELETE: DropdownModel = {
  id: 'user.question.delete',
  name: 'Устгах',
  action: '/delete',
  textColor: 'red'
};

export const QUESTION_ACTIONS = [QUESTION_EDIT, QUESTION_DELETE];


export const SELECT_QUESTION_COLUMNS: TableColumn[] = [
  {name: ' ', id: 'hasImage', type: TableColumnType.STATUS, iconName: 'image_blank', iconColor: 'gray', backgroundColor: 'none'},
  {id: "value", name: "АСУУЛТЫН НЭР"},
  {id: "score", name: "АСУУЛТЫН ОНОО"},
  {id: "type", name: "ТӨРӨЛ"},
  {id: "created", name: "ҮҮСГЭСЭН"},
  {id: "action", name: "COНГОХ", type: TableColumnType.ACTION, iconName: 'add'}];
