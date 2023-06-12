import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CanAccessDirective} from "./directives/can-access.directive";
import {PageNotFoundComponent} from "./components/page-not-found/page-not-found.component";
import {PermissionService} from "./services/permission.service";
import {AppLayoutComponent} from "./components/app-layout/app-layout.component";
import {CommonSandboxService} from "./common-sandbox.service";
import {SharedModule} from "../../../../shared/src";
import {RouterModule} from "@angular/router";
import {DialogService} from "../../../../shared/src/lib/dialog/dialog.service";
import {SnackbarService} from "../../../../shared/src/lib/snackbar/snackbar.service";
import {ConfettiComponent} from "./components/confetti/confetti.component";
import {AchievementComponent} from "./components/achievement/achievement.component";
import {BeforeUnloadDirective} from "./directives/before-unload/before-unload.directive";
import {DisableKeyboardMouseEventsDirective} from "./directives/prevent-download-copy/disable-keyboard-mouse-events.directive";
import {SafePipe} from "./components/course-content-view/safe-pipe/safe-pipe";
import { DateIntervalDialogComponent } from './components/date-interval-dialog/date-interval-dialog.component';
import { CourseProgressComponent } from './components/course-progress/course-progress.component';
import { SearchResultComponent } from './components/search-result/search-result.component';


@NgModule({
  declarations: [
    AppLayoutComponent,
    CanAccessDirective,
    PageNotFoundComponent,
    ConfettiComponent,
    AchievementComponent,
    BeforeUnloadDirective,
    DateIntervalDialogComponent,
    BeforeUnloadDirective,
    DisableKeyboardMouseEventsDirective,
    SafePipe,
    CourseProgressComponent,
    SearchResultComponent
  ],
  imports: [
    RouterModule,
    SharedModule,
    CommonModule
  ],
  providers: [
    PermissionService,
    DialogService,
    SnackbarService,
    CommonSandboxService],
  exports: [
    AppLayoutComponent,
    CanAccessDirective,
    PageNotFoundComponent,
    ConfettiComponent,
    AchievementComponent,
    BeforeUnloadDirective,
    DisableKeyboardMouseEventsDirective,
    SafePipe,
    CourseProgressComponent
  ]
})

export class JarvisCommonModule {
}
