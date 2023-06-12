import {NgModule} from "@angular/core";
import {OnlineCourseContainerComponent} from './containers/online-course-container/online-course-container.component';
import {SharedModule} from "../../../../shared/src";
import {JarvisCommonModule} from "../common/common.module";
import {OnlineCourseCreatePageComponent} from './containers/online-course-create-page/online-course-create-page.component';
import {OnlineCourseComponent} from './component/online-course/online-course.component';
import {OnlineCourseUpdatePageComponent} from "./containers/online-course-update-page/online-course-update-page.component";
import {ScormModule} from "../scorm/scorm.module";
import {RouterModule} from "@angular/router";
import {ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {OnlineCourseLaunchPageContainer} from "./containers/online-course-launch-page/online-course-launch-page-container";
import {SwiperModule} from "swiper/angular";
import { OnlineCourseInfoComponent } from './containers/online-course-sections/online-course-info/online-course-info.component';
import { CourseContentStructureComponent } from './containers/online-course-sections/course-content-structure/course-content-structure.component';
import { OnlineCourseStatisticsComponent } from './containers/online-course-statistics/online-course-statistics.component';
import { OnlineCourseTestStructureComponent } from './containers/online-course-sections/online-course-test-structure/online-course-test-structure.component';
import {OnlineCourseEnrollmentComponent} from "./containers/online-course-sections/online-course-enrollment/online-course-enrollment.component";
import { OnlineCoursePublishConfigComponent } from './containers/online-course-sections/online-course-publish-config/online-course-publish-config.component';
import { OnlineCourseGroupEnrollmentPageComponent } from './containers/online-course-group-enrollment-page/online-course-group-enrollment-page.component';

@NgModule({
  declarations: [
    OnlineCourseContainerComponent,
    OnlineCourseCreatePageComponent,
    OnlineCourseComponent,
    OnlineCourseUpdatePageComponent,
    OnlineCourseLaunchPageContainer,
    OnlineCourseInfoComponent,
    CourseContentStructureComponent,
    OnlineCourseStatisticsComponent,
    OnlineCourseEnrollmentComponent,
    OnlineCoursePublishConfigComponent,
    OnlineCourseTestStructureComponent,
    OnlineCourseGroupEnrollmentPageComponent
  ],
  imports: [
    SharedModule,
    RouterModule,
    CommonModule,
    ReactiveFormsModule,
    JarvisCommonModule,
    ScormModule,
    SwiperModule,
  ]
})
export class OnlineCourseModule {
}
