import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ExamContainerComponent} from './containers/exam-container/exam-container.component';
import {ExamComponent} from './components/exam/exam.component';
import {RouterModule} from "@angular/router";
import {SharedModule} from "../../../../shared/src";
import {ExamCreatePageComponent} from './containers/exam-create-page/exam-create-page.component';
import {JarvisCommonModule} from "../common/common.module";
import {ReactiveFormsModule} from "@angular/forms";
import {ExamBasicInfoSectionComponent} from './containers/exam-create-page/exam-basic-info-section/exam-basic-info-section.component';
import {ExamQuestionsSectionComponent} from './containers/exam-create-page/exam-questions-section/exam-questions-section.component';
import {ExamConfigSectionComponent} from './containers/exam-create-page/exam-config-section/exam-config-section.component';
import {ExamLaunchContainerComponent} from './containers/exam-launch-container/exam-launch-container.component';
import {ExamBankComponent} from './containers/exam-bank/exam-bank.component';
import {TakeExamContainerComponent} from './containers/take-exam-container/take-exam-container.component';
import {QuestionBankComponent} from './containers/question-bank/question-bank.component';
import {QuestionCreatePageComponent} from './containers/question-create-page/question-create-page.component';
import {ExamUpdatePageComponent} from './containers/exam-update-page/exam-update-page.component';
import {QuestionUpdatePageComponent} from './containers/question-update-page/question-update-page.component';
import {ExamineePageComponent} from './containers/examinee-page/examinee-page.component';
import { ExamPublishConfigSectionComponent } from './containers/exam-create-page/exam-publish-config-section/exam-publish-config-section.component';
import { ExamEnrollSectionComponent } from './containers/exam-create-page/exam-enroll-section/exam-enroll-section.component';
import {QuestionSelectDialogComponent} from "./containers/exam-create-page/question-select-dialog/question-select-dialog.component";
import {ExamStatisticsComponent} from "./containers/exam-statistic-page/exam-statistics-page.component";
import {AddExamGroupDialogComponent} from "./containers/add-exam-group-dialog/add-exam-group-dailog.component"
import {EditExamGroupDialogComponent} from "./containers/edit-exam-group-dialog/edit-exam-group-dialog.component";

@NgModule({
  declarations: [
    ExamContainerComponent,
    ExamCreatePageComponent,
    ExamBasicInfoSectionComponent,
    ExamQuestionsSectionComponent,
    ExamConfigSectionComponent,
    ExamLaunchContainerComponent,
    ExamBankComponent,
    TakeExamContainerComponent,
    QuestionBankComponent,
    QuestionCreatePageComponent,
    QuestionSelectDialogComponent,
    ExamUpdatePageComponent,
    QuestionUpdatePageComponent,
    ExamineePageComponent,
    ExamStatisticsComponent,
    ExamPublishConfigSectionComponent,
    ExamEnrollSectionComponent,
    ExamComponent,
    AddExamGroupDialogComponent,
    EditExamGroupDialogComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule,
    JarvisCommonModule,
    ReactiveFormsModule
  ]
})
export class ExamModule { }
