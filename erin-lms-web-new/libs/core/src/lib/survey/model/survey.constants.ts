import {TableColumn, TableColumnType} from "../../../../../shared/src/lib/shared-model";
import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";
import {EXAM_DELETE, EXAM_EDIT} from "../../exam/model/exam.constants";

export const TABLE_COLUMNS: TableColumn[] = [
  {name: 'Нэр', id: "name"},
  {name: 'Асуултын тоо', id: "questionCount"},
  {name: 'Статус', id: "status"},
  {name: 'Үүсгэсэн огноо', id: "createdDate"},
  {name: 'Админ', id: "admin"},
  {name: ' ', id: 'menu', type: TableColumnType.CONTEXT, iconName: 'more_vert'}
]

export const SURVEY_NOT_FOUND_TEXT = 'Үнэлгээ олдсонгүй!'


export const SURVEY_EDIT: DropdownModel = {
  id: 'user.exam.edit',
  name: 'Засах',
  action: '/edit'
};

export const SURVEY_DELETE: DropdownModel = {
  id: 'user.exam.delete',
  name: 'Устгах',
  action: '/delete',
  textColor: 'red'
};
export const SURVEY_CLONE: DropdownModel = {
  id: 'user.exam.delete',
  name: 'Хуулах',
  action: '/clone',
};
export const SURVEY_ACTIONS = [SURVEY_EDIT, SURVEY_CLONE, SURVEY_DELETE];

