import {TableColumn} from "../../../../../shared/src/lib/shared-model";

export const GENDER_FEMALE =
  {
    id: 'FEMALE',
    name: "Эмэгтэй"
  };

export const GENDER_MALE =
  {
    id: 'MALE',
    name: "Эрэгтэй"
  };

export const GENDER_OTHER =
  {
    id: 'NA',
    name: 'Бусад'
  }
export const LAWYER =
  {
    id: 'lawyer',
    name: "Шүүгч"
  };
export const CHIEF_LAWYER =
  {
    id: 'chiefLawyer',
    name: "Ерөнхий Шүүгч"
  };
export const OTHER =
  {
    id: 'other',
    name: "Бусад"
  };
export const PROFILE_GENDER = [GENDER_MALE, GENDER_FEMALE, GENDER_OTHER]
export const JOB_TITLE = [LAWYER, CHIEF_LAWYER, OTHER]

export const PROFILE_COURSE_INFO_TABLE_COLUMNS: TableColumn[] = [
  {id: 'name', name: 'Сургалтын нэр', width: '2fr'},
  {id: 'creditInfo', name: 'Төрөл'},
  {id: 'type', name: 'Хэлбэр'},
  {id: 'credit', name: 'Багц цаг'},
];
