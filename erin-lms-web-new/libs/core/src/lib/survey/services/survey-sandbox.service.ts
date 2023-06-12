import {Injectable, Type} from '@angular/core';
import {SurveyService} from "./survey.service";
import {Observable} from "rxjs";
import {CreateSurveyModel, Survey} from "../model/survey.model";
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {DialogService} from "../../../../../shared/src/lib/dialog/dialog.service";
import {DialogConfig} from "../../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../../shared/src/lib/dialog/dialog-ref";
import {TestQuestion} from "../../../../../shared/src/lib/shared-model";

@Injectable({
  providedIn: 'root'
})
export class SurveySandboxService {

  constructor(private service: SurveyService,
    private location: Location,
    private router: Router,
    private snackbarService: SnackbarService,
    private dialogService: DialogService
  ) {
  }

  navigateByUrl(url: string): void {
    this.router.navigateByUrl(url);
  }

  createAssessment(survey: CreateSurveyModel): Observable<any> {
    return this.service.createSurvey(survey.name, survey.summary);
  }

  deleteSurvey(assessmentId: string): Observable<any> {
    return this.service.deleteSurvey(assessmentId);
  }

  getAssessments(): Observable<Survey[]> {
    return this.service.getSurveys(false);
  }

  getSurveyById(assessmentId: string): Observable<Survey> {
    return this.service.getSurveyById(assessmentId);
  }

  updateSurvey(surveyId: string, name: string, description: string): Observable<any> {
    return this.service.updateSurvey(surveyId, name, description);
  }

  goBack(): void {
    this.router.navigateByUrl('/survey/container');
  }

  openSnackbar(text: string, success?: boolean): void {
    this.snackbarService.open(text, success)
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config)
  }

  getSurveyQuestions(questionsId: string): Observable<TestQuestion[]> {
    return this.service.getSurveyQuestions(questionsId);
  }

  saveQuestions(questions: TestQuestion[], surveyId: string, contentId: string): Observable<any> {
    return this.service.saveQuestions(questions, surveyId, contentId);
  }

  cloneSurvey(id: string, selectedSurvey: Survey): Observable<any> {
    return this.service.cloneSurvey(id, selectedSurvey);
  }
}
