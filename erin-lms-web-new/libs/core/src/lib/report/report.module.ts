import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReportContainerComponent} from './report-container/report-container.component';
import {SharedModule} from "../../../../shared/src";
import {RouterModule} from "@angular/router";
import {OnlineCourseReportComponent} from './containers/online-course-report/online-course-report.component';
import {ClassroomCourseReportComponent} from './containers/classroom-course-report/classroom-course-report.component';
import {CourseActivityReportComponent} from './containers/course-activity-report/course-activity-report.component';
import {SurveyReportComponent} from './containers/survey-report/survey-report.component';
import {JarvisCommonModule} from "../common/common.module";
import {ReactiveFormsModule} from "@angular/forms";
import {ExamModule} from "../exam/exam.module";
import {TimedCourseReportComponent} from "./containers/timed-course-report/timed-course-report.component";
import {ExamReportComponent} from "./containers/exam-report/exam-report.component";



@NgModule({
  declarations: [
    ReportContainerComponent,
    TimedCourseReportComponent,
    OnlineCourseReportComponent,
    ClassroomCourseReportComponent,
    CourseActivityReportComponent,
    SurveyReportComponent,
    ExamReportComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule,
    ExamModule,
    JarvisCommonModule,
    ReactiveFormsModule
  ]
})
export class ReportModule {
}
