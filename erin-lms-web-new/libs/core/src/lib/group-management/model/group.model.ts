import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";
import {TableColumn, TableColumnType} from "../../../../../shared/src/lib/shared-model";

export const USER_REMOVE: DropdownModel = {
  id: 'user.group.delete',
  name: 'Устгах',
  action: '/delete',
  textColor: 'red'
};

export const USER_COLUMNS: TableColumn[] = [
  {name: ' ', id: 'notInGroup', type: TableColumnType.STATUS, iconName: 'visibility_off', iconColor: 'gray', backgroundColor: 'none'},
  {id: "name", name: "ХЭРЭГЛЭГЧИЙН НЭР"},
  {id: "role", name: "ЭРХ"},
  {id: "lastname", name: "ЭЦЭГ /ЭХ/-ИЙН НЭР"},
  {id: "firstname", name: "НЭР"},
  {id: "action", name: "", type: TableColumnType.ACTION, iconName: 'delete', iconColor: 'warn', backgroundColor: 'none'}];

export const USER_COLUMNS_NO_DELETE: TableColumn[] = [
  {name: ' ', id: 'notInGroup', type: TableColumnType.STATUS, iconName: 'visibility_off', iconColor: 'gray', backgroundColor: 'none'},
  {id: "name", name: "ХЭРЭГЛЭГЧИЙН НЭР"},
  {id: "role", name: "ЭРХ"},
  {id: "lastname", name: "ЭЦЭГ /ЭХ/-ИЙН НЭР"},
  {id: "firstname", name: "НЭР"},];

export const USER_ADD_COLUMNS: TableColumn[] = [
  {id: "name", name: "ХЭРЭГЛЭГЧИЙН НЭР"},
  {id: "lastname", name: "ЭЦЭГ /ЭХ/-ИЙН НЭР"},
  {id: "firstname", name: "НЭР"},
  {id: "action", name: "", type: TableColumnType.ACTION, iconName: 'add'}];

export interface UserCourses {
  username: string;
  courses: any[]
};

export interface User {
  name: string;
  role: string;
  groupId: string;
  firstname: string;
  lastname: string;
  membershipId: string;
  notInGroup: boolean;
  selected: boolean;
}

export const GROUP_EDIT: DropdownModel = {name: 'Засах', action: '/edit', id: 'user.group.edit'};
export const GROUP_ADD: DropdownModel = {name: 'Нэмэх', action: '/add', id: 'user.group.add'};
export const GROUP_DELETE: DropdownModel = {name: 'Устгах', action: '/delete', id: 'user.group.delete', textColor: 'red'};


export const GROUP_ACTIONS: DropdownModel[] = [
  GROUP_EDIT,
  GROUP_ADD,
  GROUP_DELETE
];

export const QUESTION_GROUP_ACTIONS: DropdownModel[] = [
  GROUP_EDIT,
  GROUP_DELETE
];
