import {TableColumn, TableColumnType} from "../../../../../shared/src/lib/shared-model";
import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";

export const USER_TABLE_COLUMNS: TableColumn[] = [
  {id: 'check', name: '', type: TableColumnType.BOOLEAN, width: '60px'},
  {id: 'username', name: 'Хэрэглэгчийн нэр', width: '2fr'},
  {id: 'firstName', name: 'Нэр'},
  {id: 'lastName', name: 'Эцэг /эх/-ийн нэр'},
  {id: 'email', name: 'Имэйл'},
  {id: 'phoneNumber', name: 'Утас'},
  {id: 'dateLastModified', name: 'Өөрчилсөн огноо'},
  {id: 'status', name: 'Статус', type: TableColumnType.STATUS, iconName: 'visibility_off', iconColor: 'gray', backgroundColor: 'none'},
  {id: 'action', name: '', type: TableColumnType.CONTEXT, action: true, width: '1fr'}
];

export const USER_MANAGEMENT_EDIT_USER: DropdownModel = {name: 'Засах', action: '/edit', id: 'user.userManagement.edit', icon: 'edit'};
export const USER_MANAGEMENT_DELETE_USER: DropdownModel = {name: 'Устгах', action: '/delete', id: 'user.userManagement.delete', textColor: 'red', icon: 'delete'};
export const USER_MANAGEMENT_ARCHIVE_USER: DropdownModel = {name: 'Архивлах', action: '/archive', id: 'user.userManagement.archive', icon: 'archive'};
export const USER_MANAGEMENT_UNARCHIVE_USER: DropdownModel = {name: 'Архиваас гаргах', action: '/unarchive', id: 'user.userManagement.unarchive', icon: 'unarchive'};


export const USER_MANAGEMENT_CONTEXT_ACTIONS: DropdownModel[] = [
  USER_MANAGEMENT_EDIT_USER,
  USER_MANAGEMENT_ARCHIVE_USER,
  USER_MANAGEMENT_DELETE_USER,
];

export const USER_MANAGEMENT_CONTEXT_ACTIONS_ARCHIVED: DropdownModel[] = [
  USER_MANAGEMENT_UNARCHIVE_USER
];

export const USER_BULK_CONTEXT_ACTIONS_INDELIBLE: DropdownModel[] = [
  USER_MANAGEMENT_ARCHIVE_USER,
  USER_MANAGEMENT_UNARCHIVE_USER
];

export const USER_BULK_CONTEXT_ACTIONS: DropdownModel[] = [
  USER_MANAGEMENT_ARCHIVE_USER,
  USER_MANAGEMENT_UNARCHIVE_USER,
  USER_MANAGEMENT_DELETE_USER
];

export const CONFIRM_DIALOG_CONSTANTS = [
  {
    type: 'delete',
    title: 'Та хэрэглэгчийг устгахдаа итгэлтэй байна уу?',
    info: 'Устгасан хэрэглэгчийг дахин сэргээх боломжгүй. ' +
      'Системд байгаа хэрэглэгчийн бүх дата устана.',
  },
  {
    type: 'bulk_delete',
    title: 'Та олон хэрэглэгч устгахдаа итгэлтэй байна уу?',
    info: 'Устгасан хэрэглэгчид дахин сэргээх боломжгүй. ' +
      'Системд байгаа хэрэглэгчдийн бүх дата устана.',
  },
  {
    type: 'archive',
    title: 'Та хэрэглэгчийг архивлахдаа итгэлтэй байна уу?',
    info: 'Архивд орсон хэрэглэгч системд нэвтрэх боломжгүй болно.',
  },
  {
    type: 'bulk_archive',
    title: 'Та олон хэрэглэгчдийг архивлахдаа итгэлтэй байна уу?',
    info: 'Архивд орсон хэрэглэгчид системд нэвтрэх боломжгүй болно.',
  },
  {
    type: 'unarchive',
    title: 'Та хэрэглэгчийг архиваас гаргахдаа итгэлтэй байна уу?',
    info: 'Архиваас гаргасан хэрэглэгч системд нэвтрэх боломжтой болно.',
  },
  {
    type: 'bulk_unarchive',
    title: 'Та олон хэрэглэгчдийг архиваас гаргахдаа итгэлтэй байна уу?',
    info: 'Архиваас гаргасан хэрэглэгчид системд нэвтрэх боломжтой болно.',
  }
];

export const CREATE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай үүслээ.';
export const CREATE_USER_FAILURE_MSG = 'Хэрэглэгч үүсгэхэд алдаа гарлаа.';

export const CHECK_USERNAME_FAILURE_MSG = 'Хэрэглэгчийн нэр шалгахад алдаа гарлаа.';

export const GET_USERS_FAILURE_MSG = 'Хэрэглэгчид авахад алдаа гарлаа.';

export const DELETE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай устгалаа.';
export const DELETE_USER_FAILURE_MSG = 'Хэрэглэгч устгах үйлдэл амжилтгүй боллоо. Уг хэрэглэгчид сургалт оноогдсон байна.';

export const BULK_DELETE_USER_SUCCESS_MSG = 'Хэрэглэгчдийг амжилттай устгалаа.';
export const BULK_DELETE_USER_FAILURE_MSG = 'Хэрэглэгчдийг устгахад алдаа гарлаа.';

export const UPDATE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай засварлалаа.';
export const UPDATE_USER_FAILURE_MSG = 'Хэрэглэгч өөрчлөхөд алдаа гарлаа.';

export const ARCHIVE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай архивлалаа.';
export const ARCHIVE_USER_FAILURE_MSG = 'Хэрэглэгч архивлахад алдаа гарлаа.';

export const UNARCHIVE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай архиваас гаргалаа.';
export const UNARCHIVE_USER_FAILURE_MSG = 'Хэрэглэгч архиваас гаргахад алдаа гарлаа.';

export const BULK_ARCHIVE_USER_SUCCESS_MSG = 'Хэрэглэгчдийг амжилттай архивлалаа.';
export const BULK_ARCHIVE_USER_FAILURE_MSG = 'Хэрэглэгчдийг архивлахад алдаа гарлаа.';

export const BULK_UNARCHIVE_USER_SUCCESS_MSG = 'Хэрэглэгчдийг амжилттай архиваас гаргалаа.';
export const BULK_UNARCHIVE_USER_FAILURE_MSG = 'Хэрэглэгчдийг архиваас гаргахад алдаа гарлаа.';

export const IMPORT_USERS_SUCCESS_MSG = 'Хэрэглэгчид амжилттай бүртгэгдлээ';
export const IMPORT_USERS_FAILURE_MSG = 'Хэрэглэгчид бүртгэхэд алдаа гарлаа';

export const USER_DOWNLOAD_SUCCESS_MSG = "Хэрэглэгчийн мэдээлэл амжилттай татлаа!";
export const USER_DOWNLOAD_FAILURE_MSG = "Хэрэглэгчийн мэдээлэл татахад алдаа гарлаа!";

export const CREATE_BUTTON = 'user.userManagement.add';
export const IMPORT_BUTTON = 'user.userManagement.export';
export const EXPORT_BUTTON = 'user.userManagement.import';

export const BULK_ARCHIVE_BUTTON = 'user.userManagement.bulkArchive';
export const BULK_UNARCHIVE_BUTTON = 'user.userManagement.bulkUnarchive';
export const BULK_DELETE_BUTTON = 'user.userManagement.bulkDelete';

export const USER_IMPORT_ERROR_TEXT = ' буруу форматтай тул таны ' +
  'оруулсан файлаас хэрэглэгч үүсгэж чадсангүй! Жишээ нь: ' +
  '<br>Хэрэглэгчийн нэр: davaasuren.g ' +
  '<br>Email: support@jarvis.mn ' +
  '<br>Нууц үг: Pass1234 ' +
  '<br>Утас: 88****** ' +
  '<br>Овог: Галмандах ' +
  '<br>Нэр: Даваасүрэн ' +
  '<br>Хүйс: male ' +
  '<br>Албан тушаал: Менежер';

export const DUPLICATE_USER_TITLE = 'Давхацсан хэрэглэгчид';
export const DUPLICATE_USER_ERROR_TEXT =  ' давхцсан учир шинээр үүсээгүйг анхаарна уу.';
export const WRONG_FORMAT_USER_ERROR_TEXT =  'Буруу форматтай хэрэглэгчид';


