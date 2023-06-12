import {TableColumn, TableColumnType} from "../../../../../shared/src/lib/shared-model";
import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";

export const ONLINE_COURSE_REPORT_TABLE_COLUMN: TableColumn[] = [
  {id: 'name', name: 'Нэр', width: '2fr'},
  {id: 'type', name: 'Төрөл'},
  {id: 'state', name: 'Хамрах хүрээ'},
  {id: 'enrollmentCount', name: 'Элсэлт'},
  {id: 'totalViewers', name: 'Нийт үзсэн'},
  {id: 'progress', name: 'Танилцсан хувь', type: TableColumnType.PROGRESS},
  {id: 'completedViewers', name: 'Бүрэн үзсэн'},
  {id: 'hasCertificate', name: 'Сертификаттай эсэх'},
  {id: 'receivedViewers', name: 'Сертификат авсан'},
  {id: 'repeatedViewersCount', name: 'Давтан үзсэн'},
  {id: 'testScore', name: 'Сорилын дундаж оноо'},
  {id: 'averageSpentTimeOnTest', name: 'Сорилд зарцуулсан хугацаа'},
  {id: 'menu', name: 'Статус', type: TableColumnType.NAVIGATE, iconName: 'timeline'}
];
export const ONLINE_COURSE_ACTIVITY_TABLE_COLUMN: TableColumn[] = [
  {id: 'name', name: 'Сургалтын нэр', width: '2fr'},
  {id: 'courseType', name: 'Хамрах хүрээ'},
  {id: 'progress', name: 'Танилцсан хувь /Агуулга/', type: TableColumnType.PROGRESS},
  {id: 'spentTimeRatio', name: 'Танилцсан хувь /Хугацаа/', type: TableColumnType.PROGRESS},
  {id: 'testScore', name: 'Сорилын оноо'},
  {id: 'spentTimeOnTest', name: 'Сорилд зарцуулсан хугацаа'},
  {id: 'certification', name: 'Сертификат'},
  {id: 'assessment', name: 'Үнэлгээ'},
  {id: 'views', name: 'Үзсэн давтамж'},
  {id: 'spentTime', name: 'Зарцуулсан хугацаа'},
  {id: 'firstViewDate', name: 'Анх танилцсан'},
  {id: 'lastViewDate', name: 'Сүүлд танилцсан'}
];
export const CLASSROOM_COURSE_ACTIVITY_TABLE_COLUMN: TableColumn[] = [
  {id: 'name', name: 'Сургалтын нэр', width: '2fr'},
  {id: 'courseType', name: 'Төрөл'},
  {id: 'attendance', name: 'Хамрагдсан байдал'},
  {id: 'testScore', name: 'Авсан оноо'},
  {id: 'certification', name: 'Сертификат'},
  {id: 'teacher', name: 'Багш'},
  {id: 'startTime', name: 'Эхлэх цаг'},
  {id: 'endTime', name: 'Дуусах цаг'},
  {id: 'date', name: 'Огноо'}
];
export const TIMED_COURSE_ACTIVITY_TABLE_COLUMNS: TableColumn[] = [
  {id: 'name', name: 'Сургалтын нэр', width: '2fr'},
  {id: 'category', name: 'Үйлчилгээний төрөл'},
  {id: 'progress', name: 'Танилцсан хувь', type: TableColumnType.PROGRESS},
  {id: 'testScore', name: 'Сорил'},
  {id: 'feedBack', name: 'Асуулга'},
  {id: 'views', name: 'Үзсэн давтамж'},
  {id: 'spentTime', name: 'Зарцуулсан хугацаа'},
  {id: 'firstViewDate', name: 'Анх танилцсан'},
  {id: 'lastViewDate', name: 'Сүүлд танилцсан'}
];
export const CLASSROOM_COURSE_REPORT_TABLE_COLUMNS: TableColumn[] = [
  {id: 'name', name: 'Нэр', width: '2fr'},
  {id: 'categoryName', name: 'Төрөл'},
  {id: 'dateString', name: 'Хэзээ'},
  {id: 'instructor', name: 'Үүсгэсэн'},
  {id: 'maxEnrollmentCount', name: 'Суралцагчийн тоо'},
  {id: 'address', name: 'Байршил'},
  {id: 'enrollmentCount', name: 'Ирц'},
  {id: 'groups', name: 'Групп'},
  {id: 'duration', name: 'Үргэлжлэх хугацаа'}
];
export const EXAM_REPORT_TABLE_COLUMNS: TableColumn[] = [
  {name: 'Гарчиг', id: 'title', width: '2fr'},
  {name: 'Ангилал', id: 'category'},
  {name: 'Төлөв', id: 'status'},
  {name: 'Хугацаа', id: 'duration'},
  {name: 'Асуултын тоо', id: 'questionCount'},
  {name: 'Нийт өгсөн', id: 'totalRuntime'},
  {name: 'Тэнцсэн', id: 'passedCount'},
  {name: 'Авсан оноо', id: 'averageScore'},
  {name: 'Зарцуулсан хугацаа', id: 'averageSpentTime'},
  {name: 'Статистик', id: 'statistics', type: TableColumnType.NAVIGATE, iconName: 'timeline'}
]

export const TIMED_COURSE_REPORT_COLUMNS: TableColumn[] = [
  {id: 'code', name: 'Код'},
  {id: 'name', name: 'Гарчиг', width: '2fr'},
  {id: 'createdDate', name: 'Огноо'},
  {id: 'author', name: 'Админ'},
  {id: 'totalEnrollment', name: 'Нийт танилцах'},
  {id: 'views', name: 'Танилцсан тоо'},
  {id: 'progress', name: 'Танилцсан хувь', type: TableColumnType.PROGRESS},
  {id: 'hasTest', name: 'Сорил'},
  {id: 'questions', name: 'Сорилын тоо'},
  {id: 'score', name: 'Дундаж оноо'},
  {name: 'Стат', id: 'menu', type: TableColumnType.NAVIGATE, iconName: 'timeline'}
]

export const TIMED_COURSE: DropdownModel = {name: 'Урамшуулал', id: 'timedCourse'};
export const ONLINE_COURSE: DropdownModel = {name: 'Цахим сургалт', id: 'onlineCourse'};
export const CLASSROOM_COURSE: DropdownModel = {name: 'Танхимын сургалт', id: 'classroomCourse'};

export class CLASSROOM_COURSE_TABLE_COLUMNS {
}
