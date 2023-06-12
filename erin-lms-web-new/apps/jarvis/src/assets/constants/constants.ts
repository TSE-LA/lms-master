export const CONSTANTS = {
  USER_ROLES: [
    {name: 'БАГШ', id: 'LMS_ADMIN'},
    {name: 'СУРАЛЦАГЧ', id: 'LMS_EMPLOYEE'},
    {name: 'АХЛАХ', id: 'LMS_SUPERVISOR'},
    {name: 'МЕНЕЖЕР', id: 'LMS_MANAGER'}
  ],
  COURSE_FILTER_TYPES: [
    {name: 'БҮГД', id: 'all'},
    {name: 'СУРАЛЦАГЧ', id: 'EMPLOYEE'},
    {name: 'АХЛАХ', id: 'SUPERVISOR'},
    {name: 'МЕНЕЖЕР', id: 'MANAGER'}
  ],
  COURSE_TYPES: [
    {name: 'СУРАЛЦАГЧ', id: 'EMPLOYEE'},
    {name: 'АХЛАХ', id: 'SUPERVISOR'},
    {name: 'МЕНЕЖЕР', id: 'MANAGER'}
  ],
  COURSE_ACTIVITY_REPORT_TYPES: [
    {name: 'Цахим сургалт', id: 'onlineCourse'},
    {name: 'Танхимын сургалт', id: 'classroomCourse'}
  ],
  REPORT_CATEGORIES: [
    {name: "Цахим сургалт", path: "/online-course-report", active: false},
    {name: "Танхимын сургалт", path: "/classroom-course-report", active: false},
    {name: "Сургалтын идэвх", path: "/course-activity-report", active: false},
    {name: "Үнэлгээний хуудас", path: "/survey-report", active: false},
    {name: "Шалгалт", path: "/exam-report", active: false}
  ],
  COURSE_NAMES: [
    {name: 'Цахим сургалт', id: 'onlineCourse'},
    {name: 'Танхимын сургалт', id: 'classroomCourse'}
  ],
  START_EXAM_CONFIG: [
    {name: 'Шалгалтын хугацаа автоматаар эхлэх', selected: true, state: true, autoFirst: true},
    {name: 'Шалгалтын хугацааг ЭХЛЭХ товч дарж эхлүүлэх', selected: false, state: false, autoFirst: true}
  ],
  NAVIGATION_ITEMS: [
    {name: 'Хяналтын булан', iconName: 'jrs-dashboard', link: '/dashboard', id: 'app.navigation.dashboard'},
    {name: 'Тайлан', iconName: 'jrs-analytics', link: '/report', id: 'app.navigation.report'},
    {name: 'Цахим сургалт', iconName: 'jrs-computer', link: '/online-course', id: 'app.navigation.onlineCourse'},
    {name: 'Танхимын сургалт', iconName: 'jrs-school', link: '/classroom-course/container', id: 'app.navigation.classroomCourse'},
    {name: 'Үнэлгээний хуудас', iconName: 'jrs-star', link: '/survey', id: 'app.navigation.assessment'},
    {name: 'Сертификат', iconName: 'jrs-award', link: '/certificate', id: 'app.navigation.certificate'},
    {name: 'Шалгалт', iconName: 'jrs-feather', link: '/exam', id: 'app.navigation.exam'}],
  HIDDEN_NAV_ITEMS: [
    {name: 'Профайл', link: '/profile', iconName: '', id: 'app.navigation.profile'},
    {name: 'Групп тохиргоо', link: '/group-management', iconName: '', id: 'app.navigation.group-management'},
    {name: 'Хэрэглэгчийн тохиргоо', link: '/user-management', iconName: '', id: 'app.navigation.user-management'},
    {name: 'Системийн тохиргоо', link: '/settings', iconName: '', id: 'app.navigation.user-management'},
  ],
  ONLINE_COURSE_STATISTICS_DEFAULT_COLUMN: [
    {id: 'username', name: 'Нэр'},
    {id: 'groupName', name: 'Алба хэлтэс'},
    {id: 'progress', name: 'Танилцсан хувь /Агуулга/', type: 'PROGRESS'},
    {id: 'spentTimeRatio', name: 'Танилцсан хувь /Хугацаа/', type: 'PROGRESS'},
    {id: 'firstLaunchDate', name: 'Анх танилцсан'},
    {id: 'lastLaunchDate', name: 'Сүүлд танилцсан'},
    {id: 'views', name: 'Үзсэн удаа'},
    {id: 'spentTime', name: 'Зарцуулсан хугацаа'},
  ],
  ONLINE_COURSE_STATISTICS_TEST_COLUMN: [
    {id: 'score', name: 'Оноо'},
    {id: 'spentTimeOnTest', name: 'Сорилд зарцуулсан хугацаа'}
  ],
  ONLINE_COURSE_STATISTICS_CERTIFICATE_COLUMN: [
    {id: 'certificateDate', name: 'Сертификат авсан огноо'}
  ],
  ONLINE_COURSE_STATISTICS_SURVEY_COLUMN: [
    {id: 'statisticSurvey', name: 'Үнэлгээ'}
  ],
  ONLINE_COURSE_STATISTICS_DOWNLOAD_OPTIONS: [
    {id: 'downloadStatistic', name: 'Статистик тайлан татах'}
  ],
  CALENDAR_NAV_ITEM:
    {name: 'Сургалтын нэгдсэн хуанли', link: '/event-calendar', iconName: 'calendar', id: 'app.navigation.event-calendar'},
  LOGO_URL: 'assets/images/header-logo.png',
  DARK_LOGO_URL: 'assets/images/header-logo-dark.png',
  PUBLISHED_COURSE_COUNT_TYPES: [{name: 'Суралцагчийн сургалт', id: 'EMPLOYEE'}, {name: 'Ахлахын сургалт', id: 'SUPERVISOR'}, {
    name: 'Менежерийн сургалт',
    id: 'MANAGER'
  }],
  OTHER_COURSE_SEARCH_TABLE_COLUMNS: [
    {id: 'title', name: 'Гарчиг'},
    {id: 'category', name: 'Ангилал'},
    {id: 'type', name: 'Хамрах хүрээ'},
    {id: 'date', name: 'Огноо'},
    {id: 'author', name: 'Админ'}
  ],
  PUBLISHED_PROMOTION_COUNT_TYPES: [{name: 'Үндсэн үйлчилгээ', id: 'MAIN'}, {name: 'Одоо байгаа', id: 'CURRENT'}],
  SENT_SUCCESS_MSG: 'Амжилттай илгээлээ!',
  CONFIRM_DIALOG_TITLE: 'Анхааруулга',
  CONFIRM_DIALOG_MSG_ON_ENTER: 'Таны оролдлого дууссан тул сорил бөглөх боломжгүйг анхаарна уу.',
  SAVE_DATA_LOADER_SUCCESS_MSG: 'Үзсэн хувь хадгалж байна түр хүлээнэ үү!',
  SAVE_DATA_LOADER_ERROR_MSG: 'Хадгалах амжилтгүй боллоо!',
  CONFIRM_SURVEY_DIALOG_MSG_ON_EXIT: 'Үнэлгээний хуудас нэг л удаа илгээгдэнэ \n хариултаа сайн нягталж илгээнэ үү',
  CONFIRM_DIALOG_LAST_ATTEMPT_MSG_ON_EXIT: 'Таны сүүлийн оролдлого тул сорилоо бөглөнө үү!.',
  CONFIRM_DIALOG_MSG_ON_EXIT: 'ДУУСГАХ товч даралгүй гарахад сорилын оролдлогод тооцно.\nТа гарахдаа итгэлтэй байна уу',
  SENT_SURVEY_WARNING_MSG: 'Үнэлгээний хуудас нэг л удаа илгээгдэнэ',
  TEST_COMPLETION_MSG: 'Баяр хүргэе. Та сорилыг амжилттай дуусгалаа.',
  ONLINE_COURSE_CONTINUE_DIALOG_TITLE: 'Цахим сургалт үргэлжлүүлэх',
  COURSE_CONTINUE_DIALOG_MSG: 'Сүүлд орхисноос үргэлжлүүлэх үү? Хэрэв үгүй бол эхний бүлгээс эхэлнэ.',
  CHAPTERS_HEADER: 'Агуулга',
  DESCRIPTION_HEADER: 'Танилцуулга',
  CONFIRM_SUBMIT_BTN_TEXT: 'Тийм',
  ONLINE_COURSE_ROUTER_ID: 'app.navigation.onlineCourse',
  NOTIFY_DIALOG_SUBMIT_BTN_TEXT: 'Ойлголоо',
  WARNING_DIALOG_SUBMIT_BTN_TEXT: 'Ойлголоо',
  COURSE_INTRODUCTION: 'Сургалтын талаар товч танилцуулга',
  OTHER_COURSE: 'Бусад сургалтууд',
  CURRENT_FILES: 'Файлын сан',
  ATTACHMENT_FILES: 'Хавсралт'
};
