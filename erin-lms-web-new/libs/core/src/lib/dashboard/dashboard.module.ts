import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {SharedModule} from "../../../../shared/src";
import {DashboardSandboxService} from "./service/dashboard-sandbox.service";
import {JarvisCommonModule} from "../common/common.module";
import {DashboardContainerComponent} from "./container/dashboard-container/dashboard-container.component";
import { LearnerActivityTableContainerComponent } from './container/course-activity-dashlet-container/learner-activity-table-container.component';
import {EventCalendarContainerComponent} from "./container/event-calendar-container/event-calendar-container.component";


@NgModule({
  declarations: [
    DashboardContainerComponent,
    LearnerActivityTableContainerComponent,
    EventCalendarContainerComponent
  ],
  providers: [DashboardSandboxService],
  imports: [
    CommonModule,
    SharedModule,
    JarvisCommonModule
  ]
})
export class DashboardModule { }
