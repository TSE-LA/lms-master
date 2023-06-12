import {TableColumn, TableColumnType} from "../../../../../shared/src/lib/shared-model";

export const LEARNER_ACTIVITY_TABLE_COLUMN: TableColumn[] = [
  {name: 'НЭР', id: 'username', width: '2fr'},
  {name: 'ГРУПП', id: 'groupPath'},
  {name: 'ЭРХ', id: 'role'},
  {name: 'ДУНДАЖ ҮЗСЭН ХУВЬ', id: 'progress', type: TableColumnType.PROGRESS},
  {name: '', id: 'userHistory', type: TableColumnType.ACTION, iconName: 'launch', iconColor: 'gray', backgroundColor: 'none',tooltip:'Сургалтын түүх'},
]
export const LEARNER_ACTIVITY_SHORT_TABLE: TableColumn[] = [
  {name: 'НЭР', id: 'username', width: '2fr'},
  {name: 'ГРУПП', id: 'groupPath'},
  {name: 'ЭРХ', id: 'role'},
  {name: 'ДУНДАЖ ҮЗСЭН ХУВЬ', id: 'progress', type: TableColumnType.PROGRESS},
]

export const NAVIGATION_TEXT = "Та LMS системийн шинэ хувилбар луу шилжиж байна. " +
  "Одоогоор системийн шинэчлэл бүрэн дуусаагүй, Хяналтын самбар, Шалгалтын меню ашиглах боломжтой. " +
  "'Үргэлжлүүлэх' товчийг дарж шинэ хувилбарт шилжин орно уу. " +
  "Хэрэв бусад меню-г ашиглах бол 'Буцах' товчийг дарж хуучин хувилбарт шилжинэ үү.";

export const NAVIGATION_TITLE = "Шинэ хувилбар";
