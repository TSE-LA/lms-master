import {
  TableColumn,
  TableColumnType,
} from "../../../../../shared/src/lib/shared-model";
import { DropdownModel } from "../../../../../shared/src/lib/dropdown/dropdownModel";

export const TABLE_COLUMNS: TableColumn[] = [
  { name: " ", id: "statusIcon", type: TableColumnType.ICON },
  { name: "Гарчиг", id: "title", width: "50%" },
  { name: "Багш", id: "author" },
  { name: "Үүсгэсэн огноо", id: "createdDate" },
  {
    name: " ",
    id: "menu",
    type: TableColumnType.CONTEXT,
    iconName: "more_vert",
  },
];

export const ANNOUNCEMENT_NOT_FOUND_TEXT = "Зар мэдээ байхгүй байна";

export const ANNOUNCEMENT_EDIT: DropdownModel = {
  id: "user.announcement.edit",
  name: "Засах",
  action: "/edit",
};

export const ANNOUNCEMENT_DELETE: DropdownModel = {
  id: "user.announcement.delete",
  name: "Устгах",
  action: "/delete",
  textColor: "red",
};
export const ANNOUNCEMENT_VIEW: DropdownModel = {
  id: "user.announcement.view",
  name: "Харах",
  action: "/clone",
};

export const ANNOUNCEMENT_STATISTIC: DropdownModel = {
  id: "user.announcement.statistic",
  name: "Статистик",
  action: "/statistic",
};

export const PUBLISHED_ANNOUNCEMENT_ACTIONS = [
  ANNOUNCEMENT_VIEW,
  ANNOUNCEMENT_EDIT,
  ANNOUNCEMENT_STATISTIC,
  ANNOUNCEMENT_DELETE,
];

export const UNPUBLISHED_ANNOUNCEMENT_ACTIONS = [
  ANNOUNCEMENT_VIEW,
  ANNOUNCEMENT_EDIT,
  ANNOUNCEMENT_DELETE,
];

export const STATISTICS_TABLE_COLUMNS: TableColumn[] = [
  { name: "Эцэг /Эх/", id: "lastName" },
  { name: "Нэр", id: "firstName" },
  { name: "Групп", id: "departmentName" },
  { name: "Танилцсан огноо", id: "viewedDate" },
];
