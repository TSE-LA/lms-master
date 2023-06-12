import { RouterModule, Routes } from "@angular/router";
import { HomeComponent } from "../../../../libs/shared/src/lib/home/home.component";
import { LoginWrapperComponent } from "../../../../libs/core/src/lib/authentication/container/login-wrapper/login-wrapper.component";
import { AppLayoutComponent } from "../../../../libs/core/src/lib/common/components/app-layout/app-layout.component";
import { RouteGuardService } from "../../../../libs/core/src/lib/authentication/services/route-guard/route-guard.service";
import { PageNotFoundComponent } from "../../../../libs/core/src/lib/common/components/page-not-found/page-not-found.component";
import { ReportContainerComponent } from "../../../../libs/core/src/lib/report/report-container/report-container.component";
import { GroupManagementContainerComponent } from "../../../../libs/core/src/lib/group-management/group-management-container/group-management-container.component";
import { UserManagementContainerComponent } from "../../../../libs/core/src/lib/user-management/user-management-container/user-management-container/user-management-container.component";
import { ProfileContainerComponent } from "../../../../libs/core/src/lib/user-management/profile-container/profile-container.component";
import { OnlineCourseContainerComponent } from "../../../../libs/core/src/lib/online-course/containers/online-course-container/online-course-container.component";
import { CertificateContainerComponent } from "../../../../libs/core/src/lib/certificate/certificate-container/certificate-container.component";
import { ClassroomCourseComponent } from "../../../../libs/core/src/lib/classroom-course/component/classroom-course/classroom-course.component";
import { OnlineCourseCreatePageComponent } from "../../../../libs/core/src/lib/online-course/containers/online-course-create-page/online-course-create-page.component";
import { ClassroomCourseCreatePageComponent } from "../../../../libs/core/src/lib/classroom-course/container/classroom-course-create-page/classroom-course-create-page.component";
import { OnlineCourseComponent } from "../../../../libs/core/src/lib/online-course/component/online-course/online-course.component";
import { ClassroomCourseUpdatePageComponent } from "../../../../libs/core/src/lib/classroom-course/container/classroom-course-update-page/classroom-course-update-page.component";
import { OnlineCourseUpdatePageComponent } from "../../../../libs/core/src/lib/online-course/containers/online-course-update-page/online-course-update-page.component";
import { DashboardContainerComponent } from "../../../../libs/core/src/lib/dashboard/container/dashboard-container/dashboard-container.component";
import { ExamComponent } from "../../../../libs/core/src/lib/exam/components/exam/exam.component";
import { ExamContainerComponent } from "../../../../libs/core/src/lib/exam/containers/exam-container/exam-container.component";
import { ExamCreatePageComponent } from "../../../../libs/core/src/lib/exam/containers/exam-create-page/exam-create-page.component";
import { ExamLaunchContainerComponent } from "../../../../libs/core/src/lib/exam/containers/exam-launch-container/exam-launch-container.component";
import { TakeExamContainerComponent } from "../../../../libs/core/src/lib/exam/containers/take-exam-container/take-exam-container.component";
import { QuestionCreatePageComponent } from "../../../../libs/core/src/lib/exam/containers/question-create-page/question-create-page.component";
import { QuestionBankComponent } from "../../../../libs/core/src/lib/exam/containers/question-bank/question-bank.component";
import { ExamUpdatePageComponent } from "../../../../libs/core/src/lib/exam/containers/exam-update-page/exam-update-page.component";
import { QuestionUpdatePageComponent } from "../../../../libs/core/src/lib/exam/containers/question-update-page/question-update-page.component";
import { ExamineePageComponent } from "../../../../libs/core/src/lib/exam/containers/examinee-page/examinee-page.component";
import { ExamBankComponent } from "../../../../libs/core/src/lib/exam/containers/exam-bank/exam-bank.component";
import { ExamStatisticsComponent } from "../../../../libs/core/src/lib/exam/containers/exam-statistic-page/exam-statistics-page.component";
import { TimedCourseReportComponent } from "../../../../libs/core/src/lib/report/containers/timed-course-report/timed-course-report.component";
import { OnlineCourseReportComponent } from "../../../../libs/core/src/lib/report/containers/online-course-report/online-course-report.component";
import { ClassroomCourseReportComponent } from "../../../../libs/core/src/lib/report/containers/classroom-course-report/classroom-course-report.component";
import { CourseActivityReportComponent } from "../../../../libs/core/src/lib/report/containers/course-activity-report/course-activity-report.component";
import { SurveyReportComponent } from "../../../../libs/core/src/lib/report/containers/survey-report/survey-report.component";
import { ExamReportComponent } from "../../../../libs/core/src/lib/report/containers/exam-report/exam-report.component";
import { OnlineCourseLaunchPageContainer } from "../../../../libs/core/src/lib/online-course/containers/online-course-launch-page/online-course-launch-page-container";
import { CanDeactivateGuard } from "../../../../libs/core/src/lib/online-course/can-deactivate.guard";
import { OnlineCourseStatisticsComponent } from "../../../../libs/core/src/lib/online-course/containers/online-course-statistics/online-course-statistics.component";
import { SurveyContainerComponent } from "../../../../libs/core/src/lib/survey/container/survey-container/survey-container.component";
import { SurveyCreatePageComponent } from "../../../../libs/core/src/lib/survey/container/survey-create-page/survey-create-page.component";
import { SurveyUpdatePageComponent } from "../../../../libs/core/src/lib/survey/container/survey-update-page/survey-update-page.component";
import { SurveyComponent } from "../../../../libs/core/src/lib/survey/component/survey/survey.component";
import { SearchResultComponent } from "../../../../libs/core/src/lib/common/components/search-result/search-result.component";
import { LearnerActivityTableContainerComponent } from "../../../../libs/core/src/lib/dashboard/container/course-activity-dashlet-container/learner-activity-table-container.component";
import { ClassroomCourseAttendancePageComponent } from "../../../../libs/core/src/lib/classroom-course/container/classroom-course-attendance-page/classroom-course-attendance-page.component";
import { ClassroomCourseSurveyLaunchPageComponent } from "../../../../libs/core/src/lib/classroom-course/container/classroom-course-survey-launch-page/classroom-course-survey-launch-page.component";
import { SystemSettingsComponent } from "../../../../libs/core/src/lib/settings/system-settings/system-settings.component";
import { OnlineCourseGroupEnrollmentPageComponent } from "../../../../libs/core/src/lib/online-course/containers/online-course-group-enrollment-page/online-course-group-enrollment-page.component";
import { EditProfileContainerComponent } from "../../../../libs/core/src/lib/user-management/edit-profile-container/edit-profile-container.component";
import { ViewProfileContainerComponent } from "../../../../libs/core/src/lib/user-management/view-profile-container/view-profile-container.component";
import { ClassroomCourseTableComponent } from "../../../../libs/core/src/lib/classroom-course/container/classroom-course-table/classroom-course-table.component";
import { EventCalendarContainerComponent } from "../../../../libs/core/src/lib/dashboard/container/event-calendar-container/event-calendar-container.component";
import { AnnouncementPageComponent } from "libs/core/src/lib/announcement/container/announcement-page/announcement-page.component";

const routes: Routes = [
  {
    path: "login",
    component: LoginWrapperComponent,
  },
  {
    path: "",
    component: AppLayoutComponent,
    canActivateChild: [RouteGuardService],
    canActivate: [RouteGuardService],
    children: [
      {
        path: "",
        redirectTo: "dashboard",
        pathMatch: "full",
      },
      {
        path: "dashboard",
        component: DashboardContainerComponent,
        data: {
          title: "Хяналтын самбар",
        },
      },
      {
        path: "event-calendar",
        component: EventCalendarContainerComponent,
        data: {
          title: "Сургалтын нэгдсэн хуанли",
        },
      },
      {
        path: "search-result",
        component: SearchResultComponent,
        data: {
          title: "Хайлтын илэрц",
        },
      },
      {
        path: "classroom-course",
        component: ClassroomCourseComponent,
        data: {
          title: "Танхимын сургалт",
        },
        children: [
          {
            path: "",
            redirectTo: "container",
            pathMatch: "full",
          },
          {
            path: "container",
            component: ClassroomCourseTableComponent,
            data: {
              title: "Танхимын сургалт",
            },
          },
          {
            path: "create",
            component: ClassroomCourseCreatePageComponent,
            data: {
              title: "Танхимын сургалт",
            },
          },
          {
            path: "update/:id",
            component: ClassroomCourseUpdatePageComponent,
            data: {
              title: "Танхимын сургалт засварлах",
            },
          },
          {
            path: "attendance/:id",
            component: ClassroomCourseAttendancePageComponent,
            data: {
              title: "Танхимын сургалтын ирц",
            },
          },
          {
            path: "survey-launch/:id",
            component: ClassroomCourseSurveyLaunchPageComponent,
            data: {
              title: "Танхимын сургалт үнэлнээ бөглөх",
            },
          },
        ],
      },
      {
        path: "online-course",
        component: OnlineCourseComponent,
        data: {
          title: "Цахим сургалт",
        },
        children: [
          {
            path: "",
            redirectTo: "container",
            pathMatch: "full",
          },
          {
            path: "container",
            component: OnlineCourseContainerComponent,
            data: {
              title: "Цахим сургалт",
            },
          },
          {
            path: "create",
            component: OnlineCourseCreatePageComponent,
            data: {
              id: "app.online-course.create",
              title: "Цахим сургалт үүсгэх",
            },
          },
          {
            path: "update/:id/group-enrollment",
            component: OnlineCourseGroupEnrollmentPageComponent,
            data: {
              id: "app.online-course.group-enrollment",
              title: "Цахим сургалтанд суралцагч группээр оноох",
            },
          },
          {
            path: "update/:id",
            component: OnlineCourseUpdatePageComponent,
            data: {
              id: "app.online-course.update",
              title: "Цахим сургалт засварлах",
            },
          },
          {
            path: "launch/:id/:isContinue",
            component: OnlineCourseLaunchPageContainer,
            canDeactivate: [CanDeactivateGuard],
            data: {
              id: "app.online-course.launch",
              title: "Цахим сургалттай танилцах хуудас",
            },
          },
          {
            path: "statistics/:id",
            component: OnlineCourseStatisticsComponent,
            data: {
              id: "app.online-course.statistics",
              title: "Цахим сургалтын статистик",
            },
          },
          {
            path: "learner-activity",
            component: LearnerActivityTableContainerComponent,
            data: {
              title: "Хэрэглэгчийн идэвх",
            },
          },
        ],
      },
      {
        path: "exam",
        component: ExamComponent,
        data: {
          title: "Шалгалт",
        },
        children: [
          {
            path: "",
            redirectTo: "container",
            pathMatch: "full",
          },
          {
            path: "container",
            component: ExamContainerComponent,
            data: {
              title: "Шалгалт",
            },
            children: [
              {
                path: "",
                redirectTo: "examinee-list",
                pathMatch: "full",
              },
              {
                path: "list",
                component: ExamBankComponent,
                data: {
                  id: "app.exam.list",
                  title: "Шалгалтын жагсаалт",
                },
              },
              {
                path: "examinee-list",
                component: ExamineePageComponent,
                data: {
                  id: "app.exam.examinee-list",
                  title: "Шалгалтын жагсаалт",
                },
              },
              {
                path: "exam-statistics/:id",
                component: ExamStatisticsComponent,
                data: {
                  id: "app.navigation.exam-statistics",
                  title: "Шалгалтын статистик",
                },
              },
              {
                path: "exam-statistics",
                component: ExamStatisticsComponent,
                data: {
                  id: "app.navigation.exam-statistics",
                  title: "Шалгалтын статистик",
                },
              },
              {
                path: "question-list",
                component: QuestionBankComponent,
                data: {
                  id: "app.question.bank",
                  title: "Асуултын сан",
                },
              },
            ],
          },
          {
            path: "create",
            component: ExamCreatePageComponent,
            data: {
              id: "app.exam.create",
              title: "Шалгалт үүсгэх",
            },
          },
          {
            path: "launch/:id",
            component: ExamLaunchContainerComponent,
            data: {
              id: "app.exam.launch",
              title: "Шалгалттай танилцах",
            },
          },

          {
            path: "take/:id",
            component: TakeExamContainerComponent,
            data: {
              id: "app.exam.take",
              title: "Шалгалттай танилцах",
            },
          },
          {
            path: "update/:id",
            component: ExamUpdatePageComponent,
            data: {
              id: "app.exam.edit",
              title: "Шалгалт засварлах",
            },
          },
          {
            path: "question/create",
            component: QuestionCreatePageComponent,
            data: {
              id: "app.question.create",
              title: "Асуулт үүсгэх",
            },
          },
          {
            path: "question/update/:id",
            component: QuestionUpdatePageComponent,
            data: {
              id: "app.question.edit",
              title: "Асуулт засварлах",
            },
          },
        ],
      },
      {
        path: "report",
        component: ReportContainerComponent,
        data: {
          title: "Тайлан",
        },
        children: [
          {
            path: "",
            redirectTo: "online-course-report",
            pathMatch: "full",
          },
          {
            path: "promotion-report",
            component: TimedCourseReportComponent,
            data: {
              id: "app.promotion.report",
              title: "Урамшууллын тайлан",
            },
          },
          {
            path: "online-course-report",
            component: OnlineCourseReportComponent,
            data: {
              id: "app.online-course.report",
              title: "Цахим сургалт тайлан",
            },
          },
          {
            path: "classroom-course-report",
            component: ClassroomCourseReportComponent,
            data: {
              id: "app.classroom-course.report",
              title: "Танхимын сургалт тайлан",
            },
          },
          {
            path: "course-activity-report",
            component: CourseActivityReportComponent,
            data: {
              id: "app.course-activity.report",
              title: "Сургалтын идэвх тайлан",
            },
          },
          {
            path: "survey-report",
            component: SurveyReportComponent,
            data: {
              id: "app.survey.report",
              title: "Үнэлгээний тайлан",
            },
          },
          {
            path: "exam-report",
            component: ExamReportComponent,
            data: {
              id: "app.exam.report",
              title: "Шалгалтын тайлан",
            },
          },
        ],
      },
      {
        path: "group-management",
        component: GroupManagementContainerComponent,
        data: {
          title: "Группийн тохиргоо",
        },
      },
      {
        path: "user-management",
        component: UserManagementContainerComponent,
        data: {
          title: "Хэрэглэгчийн тохиргоо",
        },
      },
      {
        path: "profile",
        component: ProfileContainerComponent,
        data: {
          title: "Өөрийн мэдээлэл",
        },
        children: [
          {
            path: "",
            redirectTo: "view",
            pathMatch: "full",
          },
          {
            path: "view",
            component: ViewProfileContainerComponent,
            data: {
              title: "Өөрийн мэдээлэл",
            },
          },
          {
            path: "edit",
            component: EditProfileContainerComponent,
            data: {
              title: "Өөрийн мэдээлэл засах",
            },
          },
        ],
      },
      {
        path: "survey",
        component: SurveyComponent,
        data: {
          title: "Сургалтын үнэлгээний хуудас",
        },
        children: [
          {
            path: "",
            redirectTo: "container",
            pathMatch: "full",
          },
          {
            path: "container",
            component: SurveyContainerComponent,
            data: {
              title: "Сургалтын үнэлгээний хуудас",
            },
          },
          {
            path: "create",
            component: SurveyCreatePageComponent,
            data: {
              title: "Үнэлгээ үүсгэх хуудас",
            },
          },
          {
            path: "update/:id",
            component: SurveyUpdatePageComponent,
            data: {
              title: "Үнэлгээ засварлах хуудас",
            },
          },
        ],
      },
      {
        path: "certificate",
        component: CertificateContainerComponent,
        data: {
          title: "Сургалтын сертификат",
        },
      },
      {
        path: "file-viewer/:download/:courseName/:courseId/:learnerId/:path/:fileName",
        component: HomeComponent,
        data: { title: "Файл харах" },
      },
      {
        path: "settings",
        component: SystemSettingsComponent,
        data: {
          title: "Системийн тохиргоо",
        },
      },
      {
        path: "announcement",
        component: AnnouncementPageComponent,
        data: {
          title: "Зарлал, Мэдээлэл",
        },
      },
    ],
  },
  { path: "404", component: PageNotFoundComponent },
  { path: "**", redirectTo: "404" },
];

export const Routing = RouterModule.forRoot(routes, { useHash: true });
