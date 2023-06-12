import { NgModule } from "@angular/core";
import { AuthenticationModule } from "./authentication/authentication.module";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { SharedModule } from "../../../shared/src";
import { JarvisCommonModule } from "./common/common.module";
import { OnlineCourseModule } from "./online-course/online-course.module";
import { ClassroomCourseModule } from "./classroom-course/classroom-course.module";
import { TimedCourseModule } from "./timed-course/timed-course.module";
import { CertificateModule } from "./certificate/certificate.module";
import { DashboardModule } from "./dashboard/dashboard.module";
import { ScormModule } from "./scorm/scorm.module";
import { ReportModule } from "./report/report.module";
import { ExamModule } from "./exam/exam.module";
import { GroupManagementModule } from "./group-management/group-management.module";
import { SurveyModule } from "./survey/survey.module";
import { UserManagementModule } from "./user-management/user-management.module";
import { SettingsModule } from "./settings/settings.module";
import { AnnouncementModule } from "./announcement/announcement.module";

@NgModule({
  imports: [
    AuthenticationModule,
    SharedModule,
    RouterModule,
    ReportModule,
    CommonModule,
    OnlineCourseModule,
    ExamModule,
    ClassroomCourseModule,
    DashboardModule,
    TimedCourseModule,
    JarvisCommonModule,
    CertificateModule,
    UserManagementModule,
    GroupManagementModule,
    ScormModule,
    SurveyModule,
    SettingsModule,
    AnnouncementModule,
  ],
  declarations: [],
  providers: [AuthenticationModule],
  exports: [],
})
export class CoreModule {}
