import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SurveyContainerComponent} from "./container/survey-container/survey-container.component";
import {SharedModule} from "../../../../shared/src";
import {SurveyCreatePageComponent} from './container/survey-create-page/survey-create-page.component';
import {SurveyUpdatePageComponent} from './container/survey-update-page/survey-update-page.component';
import {JarvisCommonModule} from "../common/common.module";
import {RouterModule} from "@angular/router";
import { SurveyComponent } from './component/survey/survey.component';
import {ReactiveFormsModule} from "@angular/forms";
import { SurveyInfoComponent } from './container/survey-sections/survey-info/survey-info.component';
import { SurveyQuestionComponent } from './container/survey-sections/survey-question/survey-question.component';


@NgModule({
  declarations: [
    SurveyContainerComponent,
    SurveyCreatePageComponent,
    SurveyUpdatePageComponent,
    SurveyComponent,
    SurveyInfoComponent,
    SurveyQuestionComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    JarvisCommonModule,
    RouterModule,
    ReactiveFormsModule
  ]
})
export class SurveyModule { }
