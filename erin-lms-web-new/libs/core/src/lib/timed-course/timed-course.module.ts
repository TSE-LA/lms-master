import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TimedCourseContainerComponent} from './containers/timed-course-container/timed-course-container.component';
import {SharedModule} from "../../../../shared/src";
import {JarvisCommonModule} from "../common/common.module";
import {RouterModule} from "@angular/router";
import { TimedCourseUpdatePageComponent } from './containers/timed-course-update-page/timed-course-update-page.component';
import { TimedCourseLaunchPageComponent } from './containers/timed-course-launch-page/timed-course-launch-page.component';
import { TimedCourseComponent } from './component/timed-course/timed-course.component';
import { TimedCourseCreatePageComponent } from './containers/timed-course-create-page/timed-course-create-page.component';
import { TimedCourseInfoComponent } from './containers/timed-course-sections/timed-course-info/timed-course-info.component';
import { TimedCourseContentStructureComponent } from './containers/timed-course-sections/timed-course-content-structure/timed-course-content-structure.component';
import { TimedCourseTestComponent } from './containers/timed-course-sections/timed-course-test/timed-course-test.component';
import { TimedCourseEnrollmentComponent } from './containers/timed-course-sections/timed-course-enrollment/timed-course-enrollment.component';
import { TimedCoursePublishConfigComponent } from './containers/timed-course-sections/timed-course-publish-config/timed-course-publish-config.component';
import { TimedCourseAttachmentsComponent } from './containers/timed-course-sections/timed-course-attachments/timed-course-attachments.component';
import {ReactiveFormsModule} from "@angular/forms";
import { TimedCourseStatisticsComponent } from './containers/timed-course-statistics/timed-course-statistics.component';
import {ScormModule} from "../scorm/scorm.module";
import {TimedCourseReadingPageComponent} from "./containers/timed-course-reading-page/timed-course-reading-page.component";
import { TimedCourseGroupEnrollmentPageComponent } from './containers/timed-course-group-enrollment-page/timed-course-group-enrollment-page.component';


@NgModule({
  declarations: [
    TimedCourseContainerComponent,
    TimedCourseUpdatePageComponent,
    TimedCourseLaunchPageComponent,
    TimedCourseComponent,
    TimedCourseCreatePageComponent,
    TimedCourseInfoComponent,
    TimedCourseContentStructureComponent,
    TimedCourseTestComponent,
    TimedCourseEnrollmentComponent,
    TimedCoursePublishConfigComponent,
    TimedCourseAttachmentsComponent,
    TimedCourseStatisticsComponent,
    TimedCourseReadingPageComponent,
    TimedCourseGroupEnrollmentPageComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule,
    JarvisCommonModule,
    ReactiveFormsModule,
    ScormModule
  ]
})
export class TimedCourseModule {
}
