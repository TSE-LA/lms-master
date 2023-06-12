import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SharedModule} from "../../../../shared/src";
import {RouterModule} from "@angular/router";
import {ClassroomCourseService} from "./services/classroom-course.service";
import {ClassroomCourseComponent} from "./component/classroom-course/classroom-course.component";
import {JarvisCommonModule} from "../common/common.module";
import {ClassroomCourseCreatePageComponent} from "./container/classroom-course-create-page/classroom-course-create-page.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {DateItemsComponent} from "./component/date-and-time/date-items.component";
import {DialogService} from "../../../../shared/src/lib/dialog/dialog.service";
import {SnackbarService} from "../../../../shared/src/lib/snackbar/snackbar.service";
import {ClassroomCourseUpdatePageComponent} from './container/classroom-course-update-page/classroom-course-update-page.component';
import {ClassroomCourseInfoComponent} from './container/classroom-course-sections/classroom-course-info/classroom-course-info.component';
import {ClassroomCourseSuperInvitationComponent} from './container/classroom-course-sections/classroom-course-super-invitation/classroom-course-super-invitation.component';
import {ClassroomCoursePublishConfigComponent} from './container/classroom-course-sections/classroom-course-publish-config/classroom-course-publish-config.component';
import {ClassroomCourseAttendancePageComponent} from './container/classroom-course-attendance-page/classroom-course-attendance-page.component';
import {ScormModule} from "../scorm/scorm.module";
import {ClassroomCourseSurveyLaunchPageComponent} from "./container/classroom-course-survey-launch-page/classroom-course-survey-launch-page.component";
import { ClassroomCourseAddLearnerDialogComponent } from './component/classroom-course-add-learner-dialog/classroom-course-add-learner-dialog.component';
import {ClassroomCourseTableComponent} from "./container/classroom-course-table/classroom-course-table.component";


@NgModule({
  declarations: [
    DateItemsComponent,
    ClassroomCourseTableComponent,
    ClassroomCourseComponent,
    ClassroomCourseComponent,
    ClassroomCourseCreatePageComponent,
    ClassroomCourseUpdatePageComponent,
    ClassroomCourseInfoComponent,
    ClassroomCourseSuperInvitationComponent,
    ClassroomCoursePublishConfigComponent,
    ClassroomCourseAttendancePageComponent,
    ClassroomCourseSurveyLaunchPageComponent,
    ClassroomCourseAddLearnerDialogComponent
  ],
  imports: [
    SharedModule,
    RouterModule,
    CommonModule,
    JarvisCommonModule,
    ReactiveFormsModule,
    ScormModule,
    FormsModule
  ],
  exports: [
    DateItemsComponent
  ],
  schemas: [
    CUSTOM_ELEMENTS_SCHEMA
  ],
  providers: [ClassroomCourseService, DialogService, SnackbarService]
})
export class ClassroomCourseModule {
}
