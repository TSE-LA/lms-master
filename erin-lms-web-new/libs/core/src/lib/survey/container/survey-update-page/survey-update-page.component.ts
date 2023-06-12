import {Component, ViewChild} from '@angular/core';
import {SurveySandboxService} from "../../services/survey-sandbox.service";
import {SurveyInfoComponent} from "../survey-sections/survey-info/survey-info.component";
import {Survey, SurveyStatus} from "../../model/survey.model";
import {ActivatedRoute} from "@angular/router";
import {SurveyQuestionComponent} from "../survey-sections/survey-question/survey-question.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {forkJoin} from "rxjs";

@Component({
  selector: 'jrs-survey-update-page',
  template: `
    <jrs-button class="create-page-return-btn"
                [iconName]="'arrow_back_ios'"
                [iconColor]="'secondary'"
                [noOutline]="true"
                [isMaterial]="true"
                [size]="'medium'"
                [bold]="true"
                [textColor]="'text-link'"
                (clicked)="goBack()">БУЦАХ
    </jrs-button>
    <jrs-survey-info #surveyInfoSection [survey]="this.survey"></jrs-survey-info>
    <jrs-survey-question *ngIf="!active" #surveyQuestionSection [survey]="this.survey"></jrs-survey-question>
    <div class="flex-center margin-top margin-bottom-large">
      <jrs-button
        class="side-margin"
        [title]="'ХАДГАЛАХ'"
        [color]="'primary'"
        [size]="'medium'"
        [float]="'center'"
        [load]="saving"
        (clicked)="saveSurvey()">
      </jrs-button>
    </div>
    <jrs-page-loader [show]="saving"></jrs-page-loader>
  `,
  styles: []
})
export class SurveyUpdatePageComponent {
  @ViewChild('surveyInfoSection') surveyInfo: SurveyInfoComponent;
  @ViewChild('surveyQuestionSection') surveyQuestion: SurveyQuestionComponent;
  surveyId: string;
  survey: Survey;
  saving = false;
  active = false;

  constructor(private sb: SurveySandboxService, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      this.surveyId = params.get('id');
      this.sb.getSurveyById(this.surveyId).subscribe(res => {
        this.survey = res;
        this.active = this.survey.status == SurveyStatus.ACTIVE;
      }, () => {
        this.sb.openSnackbar("Үнэлгээ ачаалахад алдаа гарлаа!")
      })
    })

  }

  goBack(): void {
    const infoChanged = this.surveyInfo.isDataChanged();
    const structureChanged = this.surveyQuestion && this.surveyQuestion.isDataChanged();
    if (infoChanged || structureChanged) {
      const config = new DialogConfig();
      config.outsideClick = false;
      config.title = "Мэдээлэл хадгалахгүй шууд гарах уу?"
      config.data = {
        info: "'Тийм' гэж дарвал мэдээлэл хадгалагдахгүй болохыг анхаарна уу."
      }
      this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
        if (res) {
          this.sb.goBack();
        }
      });
    } else {
      this.sb.goBack();
    }
  }

  saveSurvey(): void {
    let saveOrder = [];
    if (this.surveyInfo.isDataChanged()) {
      saveOrder = this.surveyInfo.save(saveOrder);
    }
    if (this.surveyQuestion && this.surveyQuestion.isDataChanged() && !this.surveyQuestion.hasErrors()) {
      saveOrder = this.surveyQuestion.save(saveOrder);
    }
    if (saveOrder.length > 0) {
      this.saving = true;
      forkJoin(saveOrder).subscribe(() => {
        this.saving = false;
        this.sb.goBack();
      }, () => {
        this.saving = false;
      });
    }
  }
}
