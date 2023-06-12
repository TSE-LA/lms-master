/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ButtonComponent } from "./button/button.component";
import { CardComponent } from "./card/card.component";
import { ThemeManagerDirective } from "./theme/directive/theme-manager.directive";
import { LoginComponent } from "./login/login.component";
import { FieldsComponent } from "./fields/fields.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { BreakPointObserverService } from "./theme/services/break-point-observer.service";
import { ThemeService } from "./theme/services/theme.service";
import { IconsComponent } from "./icons/icons.component";
import { InputFieldComponent } from "./input-field/input-field.component";
import { DropdownComponent } from "./dropdown/dropdown.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { BrowserModule } from "@angular/platform-browser";
import { LoaderComponent } from "./loader/loader.component";
import { DropdownInputComponent } from "./dropdown-input/dropdown-input.component";
import { DropdownViewComponent } from "./dropdown-view/dropdown-view.component";
import { ButtonDropdownComponent } from "./button-dropdown/button-dropdown.component";
import { OverlayComponent } from "./overlay/overlay.component";
import { DatePickerComponent } from "./date-picker/date-picker.component";
import { DateIntervalPickerComponent } from "./date-interval-picker/date-interval-picker.component";
import { HeaderToolbarComponent } from "./layout/header-toolbar/header-toolbar.component";
import { LayoutSidenavComponent } from "./layout/layout-side-navigation/layout-side-navigation.component";
import { LayoutComponent } from "./layout/layout.component";
import { CalendarEventComponent } from "./calendar/calendar-event/calendar-event.component";
import { CalendarFrameComponent } from "./calendar/calendar-frame/calendar-frame.component";
import { CalendarNodeComponent } from "./calendar/calendar-node/calendar-node.component";
import { CalendarSkeletonLoaderComponent } from "./calendar/calendar-skeleton-loader/calendar-skeleton-loader.component";
import { RouterModule } from "@angular/router";
import { SectionBackgroundComponent } from "./layout/section-backround/section-background.component";
import { ScrollComponent } from "./scroll/scroll.component";
import { TooltipDirective } from "./tooltip/tooltip.directive";
import { DividerComponent } from "./divider/divider.component";
import { EventDetailsComponent } from "./calendar/event-details/event-details.component";
import { IconLabelComponent } from "./calendar/location-time/icon-label.component";
import { HeaderTextComponent } from "./header-text/header-text.component";
import { ImageViewerComponent } from "./image-viewer/image-viewer.component";
import { EnrollmentSectionComponent } from "./enrollment/enrollment-section/enrollment-section.component";
import { CheckboxInputComponent } from "./checkbox-input/checkbox-input.component";
import { GradeSectionComponent } from "./grade-section/grade-section.component";
import { PaginatorComponent } from "./paginator/paginator.component";
import { ProgressComponent } from "./progress/progress.component";
import { CircleButtonComponent } from "./circle-button/circle-button.component";
import { ContentStructureComponent } from "./structures/content-structure/content-structure.component";
import { FileFieldComponent } from "./file-upload/file-field.component";
import { TimePickerComponent } from "./time-picker/time-picker.component";
import { TestStructureComponent } from "./structures/test-structure/test-structure.component";
import { RadioButtonComponent } from "./radio-button/radio-button.component";
import { CourseCountDashletComponent } from "./dashlets/course-count-dashlet/course-count-dashlet.component";
import { SnackbarComponent } from "./snackbar/snackbar.component";
import { SnackbarService } from "./snackbar/snackbar.service";
import { DialogComponent } from "./dialog/dialog.component";
import { DashboardFrameComponent } from "./dashboard-frame/dashboard-frame.component";
import { RadioInputComponent } from "./radio-input/radio-input.component";
import { TimedCourseTableComponent } from "./table/timed-course-table/timed-course-table.component";
import { TabGroupComponent } from "./tab/tab-group.component";
import { TabComponent } from "./tab/tab.component";
import { ActionTableComponent } from "./table/action-table/action-table.component";
import { MenuComponent } from "./menu/menu.component";
import { TextAreaComponent } from "./text-area/text-area.component";
import { SmallDashletComponent } from "./small-dashlet/small-dashlet.component";
import { TreeViewComponent } from "./tree-view/tree-view.component";
import { DynamicComponentDirective } from "./dynamic-component/dynamic-component.directive";
import { DropDownTreeViewComponent } from "./drop-down-tree-view/drop-down-tree-view.component";
import { SkeletonLoaderComponent } from "./skeleton-loader/skeleton-loader.component";
import { DialogService } from "./dialog/dialog.service";
import { InsertionDirective } from "./dialog/insertion.directive";
import { NotesDialogComponent } from "./dialog/notes-dialog/notes-dialog.component";
import { ActionButtonsComponent } from "./dialog/action-buttons/action-buttons.component";
import { ConfirmDialogComponent } from "./dialog/confirm-dialog/confirm-dialog.component";
import { NotFoundPageComponent } from "./not-found-page/not-found-page.component";
import { LearnerActivityTableComponent } from "./table/learner-activity-table/learner-activity-table.component";
import { CardSkeletonLoaderComponent } from "./card-skeleton-loader/card-skeleton-loader.component";

import { ExamLaunchComponent } from "./exam-launch/exam-launch.component";
import { ExamDetailComponent } from "./exam-launch/exam-detail/exam-detail.component";
import { ConfettiComponent } from "./confetti/confetti.component";
import { TakeExamComponent } from "./take-exam/take-exam.component";
import { QuestionNavigatorComponent } from "./question-navigator/question-navigator.component";
import { CountdownTimerComponent } from "./countdown-timer/countdown-timer.component";
import { QuestionListComponent } from "./question-list/question-list.component";
import { LabelComponent } from "./label/label.component";
import { SingleChoiceQuestionComponent } from "./question/single-choice-question/single-choice-question.component";
import { MultiChoiceQuestionComponent } from "./question/multi-choice-question/multi-choice-question.component";
import { ImageAttachButtonComponent } from "./image-attach-button/image-attach-button.component";
import { DynamicTableComponent } from "./table/dynamic-table/dynamic-table.component";
import { SelectedQuestionsComponent } from "./question/selected-questions/selected-questions.component";
import { RandomSelectedQuestionsComponent } from "./question/random-selected-questions/random-selected-questions.component";
import { DisableCopyEventsDirective } from "./directive/disable-copy-events.directive";
import { PageLoaderComponent } from "./loader/page-loader/page-loader.component";
import { InfoPanelComponent } from "./info-panel/info-panel.component";
import { CertificateComponent } from "./certificate/certificate.component";
import { GridContainerComponent } from "./grid-container/grid-container.component";
import { FileUploadDialogComponent } from "./dialog/file-upload-dialog/file-upload-dialog.component";
import { CerrificateSkeletonLoaderComponent } from "./certificate/cerrificate-skeleton-loader.component";
import { CertificateDashletComponent } from "./dashlets/certificate-dashlet/certificate-dashlet.component";
import { SwiperModule } from "swiper/angular";
import { FileViewDialogComponent } from "./dialog/file-view-dialog/file-view-dialog.component";
import { FileViewerComponent } from "./file-viewer/file-viewer.component";
import { LinkifyPipe } from "./pipes/pipes/linkify.pipe";
import { LearnerSuccessDashletComponent } from "./dashlets/learner-success-dashlet/learner-success-dashlet.component";
import { PercentageIndicatorComponent } from "./dashlets/percentage-indicator/percentage-indicator.component";
import { PercentageProgressComponent } from "./percentage-progress/percentage-progress.component";
import { ConfirmCheckboxDialogComponent } from "./dialog/confirm-checkbox-dialog/confirm-checkbox-dialog.component";
import { ChartComponent } from "./chart/chart.component";
import { LegendsComponent } from "./legends/legends.component";
import { FillQuestionResultComponent } from "./fill-question-result/fill-question-result.component";
import { ListComponent } from "./file-list/list.component";
import { SearchBoxComponent } from "./search-box/search-box.component";
import { SmallDashletsBundleComponent } from "./small-dashlets-bundle/small-dashlets-bundle.component";
import { UserInfoDashletComponent } from "./user-info-dashlet/user-info-dashlet.component";
import { SlideToggleComponent } from "./slide-toggle/slide-toggle.component";
import { MaterialModule } from "./material/material.module";
import { AutocompleteDropdownComponent } from "./autocomplete-dropdown/autocomplete-dropdown.component";
import { TreeViewCheckboxComponent } from "./tree-view-checkbox/tree-view-checkbox.component";
import { GroupEnrollmentSectionComponent } from "./enrollment/group-enrollment-section/group-enrollment-section.component";
import { ColumnListComponent } from "./column-list/column-list.component";
import { HeaderWithFilterComponent } from "./header-with-filter/header-with-filter.component";
import { ImageViewComponent } from "./image-view/image-view.component";
import { UnderscoreDropdownComponent } from "./underscore-dropdown/underscore-dropdown.component";
import { MatSelectModule } from "@angular/material/select";
import { FileAttachmentComponent } from "./file-attachment/file-attachment.component";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatToolbarModule } from "@angular/material/toolbar";
import { CalendarFiltersComponent } from "./calendar/calendar-filters/calendar-filters.component";
import { CalendarSidenavComponent } from "./calendar/calendar-sidenav/calendar-sidenav.component";
import { MatTooltipModule } from "@angular/material/tooltip";
import { SideDrawerComponent } from "./side-drawer/side-drawer.component";
@NgModule({
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    RouterModule,
    CommonModule,
    SwiperModule,
    MaterialModule,
    MatSelectModule,
    MatToolbarModule,
    MatSidenavModule,
    MatTooltipModule,
  ],
  providers: [
    ThemeService,
    BreakPointObserverService,
    SnackbarService,
    DialogService,
  ],
  declarations: [
    ButtonComponent,
    CardComponent,
    ThemeManagerDirective,
    IconsComponent,
    LoginComponent,
    FieldsComponent,
    InputFieldComponent,
    DropdownComponent,
    CalendarNodeComponent,
    CalendarEventComponent,
    CalendarFrameComponent,
    CalendarFiltersComponent,
    CalendarSidenavComponent,
    LoaderComponent,
    DatePickerComponent,
    DropdownInputComponent,
    DropdownViewComponent,
    ButtonDropdownComponent,
    OverlayComponent,
    DateIntervalPickerComponent,
    HeaderToolbarComponent,
    LayoutSidenavComponent,
    LayoutComponent,
    HeaderTextComponent,
    CalendarSkeletonLoaderComponent,
    SectionBackgroundComponent,
    ScrollComponent,
    TooltipDirective,
    DividerComponent,
    EventDetailsComponent,
    IconLabelComponent,
    ImageViewerComponent,
    TimedCourseTableComponent,
    TooltipDirective,
    EnrollmentSectionComponent,
    CheckboxInputComponent,
    GradeSectionComponent,
    PaginatorComponent,
    ProgressComponent,
    CourseCountDashletComponent,
    ProgressComponent,
    RadioInputComponent,
    ContentStructureComponent,
    CircleButtonComponent,
    TimePickerComponent,
    FileFieldComponent,
    TestStructureComponent,
    RadioButtonComponent,
    DashboardFrameComponent,
    SnackbarComponent,
    DialogComponent,
    TabGroupComponent,
    TabComponent,
    ActionTableComponent,
    MenuComponent,
    TextAreaComponent,
    InsertionDirective,
    NotesDialogComponent,
    ActionButtonsComponent,
    ConfirmDialogComponent,
    NotFoundPageComponent,
    SkeletonLoaderComponent,
    DynamicComponentDirective,
    DropdownViewComponent,
    TreeViewComponent,
    DropDownTreeViewComponent,
    SmallDashletComponent,
    CardSkeletonLoaderComponent,
    LearnerActivityTableComponent,
    ExamLaunchComponent,
    ExamDetailComponent,
    ConfettiComponent,
    TakeExamComponent,
    QuestionNavigatorComponent,
    CountdownTimerComponent,
    QuestionListComponent,
    QuestionListComponent,
    ConfettiComponent,
    LabelComponent,
    SingleChoiceQuestionComponent,
    MultiChoiceQuestionComponent,
    ImageAttachButtonComponent,
    SelectedQuestionsComponent,
    RandomSelectedQuestionsComponent,
    DisableCopyEventsDirective,
    DynamicTableComponent,
    PageLoaderComponent,
    InfoPanelComponent,
    CertificateComponent,
    GridContainerComponent,
    FileUploadDialogComponent,
    CerrificateSkeletonLoaderComponent,
    InfoPanelComponent,
    CertificateDashletComponent,
    FileViewDialogComponent,
    FileViewerComponent,
    LinkifyPipe,
    LearnerSuccessDashletComponent,
    PercentageIndicatorComponent,
    PercentageProgressComponent,
    ConfirmCheckboxDialogComponent,
    PercentageProgressComponent,
    ListComponent,
    ChartComponent,
    LegendsComponent,
    FillQuestionResultComponent,
    SearchBoxComponent,
    SmallDashletsBundleComponent,
    UserInfoDashletComponent,
    SlideToggleComponent,
    AutocompleteDropdownComponent,
    TreeViewCheckboxComponent,
    GroupEnrollmentSectionComponent,
    ColumnListComponent,
    HeaderWithFilterComponent,
    UnderscoreDropdownComponent,
    ImageViewComponent,
    FileAttachmentComponent,
    SideDrawerComponent,
  ],
  entryComponents: [
    DialogComponent,
    SnackbarComponent,
    NotesDialogComponent,
    ConfirmDialogComponent,
  ],
  exports: [
    CardComponent,
    ButtonComponent,
    ThemeManagerDirective,
    LoginComponent,
    InputFieldComponent,
    CalendarNodeComponent,
    CalendarEventComponent,
    CalendarFrameComponent,
    CalendarFiltersComponent,
    CalendarSidenavComponent,
    IconsComponent,
    DropdownComponent,
    DatePickerComponent,
    DropdownInputComponent,
    OverlayComponent,
    DateIntervalPickerComponent,
    HeaderToolbarComponent,
    LayoutSidenavComponent,
    CalendarSkeletonLoaderComponent,
    DropdownViewComponent,
    HeaderTextComponent,
    SectionBackgroundComponent,
    ScrollComponent,
    DividerComponent,
    EventDetailsComponent,
    IconLabelComponent,
    ImageViewerComponent,
    CheckboxInputComponent,
    EnrollmentSectionComponent,
    GradeSectionComponent,
    PaginatorComponent,
    ProgressComponent,
    TimedCourseTableComponent,
    RadioInputComponent,
    ProgressComponent,
    CircleButtonComponent,
    ContentStructureComponent,
    TimePickerComponent,
    FileFieldComponent,
    TestStructureComponent,
    RadioButtonComponent,
    CourseCountDashletComponent,
    DashboardFrameComponent,
    SnackbarComponent,
    DialogComponent,
    TabGroupComponent,
    TabComponent,
    ActionTableComponent,
    MenuComponent,
    TextAreaComponent,
    SkeletonLoaderComponent,
    DynamicComponentDirective,
    DropdownViewComponent,
    TreeViewComponent,
    DropDownTreeViewComponent,
    SmallDashletComponent,
    ConfirmDialogComponent,
    NotesDialogComponent,
    NotFoundPageComponent,
    ExamLaunchComponent,
    TakeExamComponent,
    ExamLaunchComponent,
    LabelComponent,
    SingleChoiceQuestionComponent,
    ImageAttachButtonComponent,
    MultiChoiceQuestionComponent,
    LearnerActivityTableComponent,
    SelectedQuestionsComponent,
    RandomSelectedQuestionsComponent,
    DisableCopyEventsDirective,
    DynamicTableComponent,
    TooltipDirective,
    LoaderComponent,
    PageLoaderComponent,
    LearnerActivityTableComponent,
    PageLoaderComponent,
    InfoPanelComponent,
    LearnerActivityTableComponent,
    NotFoundPageComponent,
    CardSkeletonLoaderComponent,
    GridContainerComponent,
    CertificateComponent,
    CerrificateSkeletonLoaderComponent,
    CardSkeletonLoaderComponent,
    ConfettiComponent,
    FileViewDialogComponent,
    FileViewerComponent,
    ActionButtonsComponent,
    ConfettiComponent,
    LinkifyPipe,
    PercentageIndicatorComponent,
    PercentageProgressComponent,
    ListComponent,
    ConfirmCheckboxDialogComponent,
    PercentageProgressComponent,
    ChartComponent,
    FillQuestionResultComponent,
    SearchBoxComponent,
    SmallDashletsBundleComponent,
    UserInfoDashletComponent,
    SlideToggleComponent,
    ReactiveFormsModule,
    AutocompleteDropdownComponent,
    TreeViewCheckboxComponent,
    GroupEnrollmentSectionComponent,
    ColumnListComponent,
    HeaderWithFilterComponent,
    UnderscoreDropdownComponent,
    ImageViewComponent,
    FileAttachmentComponent,
    SideDrawerComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SharedModule {}
