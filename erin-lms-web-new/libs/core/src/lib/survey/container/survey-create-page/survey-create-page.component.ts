import {Component, ViewChild} from '@angular/core';
import {SurveySandboxService} from "../../services/survey-sandbox.service";
import {SurveyInfoComponent} from "../survey-sections/survey-info/survey-info.component";

@Component({
  selector: 'jrs-survey-create-page',
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

    <jrs-survey-info #surveyInfoSection></jrs-survey-info>

    <div class="flex-center margin-top margin-bottom-large">
      <jrs-button
        class="side-margin"
        [title]="'ҮҮСГЭХ'"
        [color]="'primary'"
        [size]="'medium'"
        [float]="'center'"
        [load]="creating"
        (clicked)="createSurvey(false)">
      </jrs-button>
      <jrs-button
        class="side-margin center"
        [title]="'ҮРГЭЛЖЛҮҮЛЭХ'"
        [color]="'primary'"
        [size]="'medium'"
        [load]="creating"
        (clicked)="createSurvey(true)">
      </jrs-button>
    </div>
    <jrs-page-loader [show]="creating" [text]="'Хадгалж байна...'"></jrs-page-loader>
  `,
  styles: []
})
export class SurveyCreatePageComponent {
  @ViewChild('surveyInfoSection') infoSection: SurveyInfoComponent;
  creating: boolean;

  constructor(private sb: SurveySandboxService) {
  }

  goBack(): void {
    this.sb.goBack();
  }

  createSurvey(saveAndContinue: boolean): void {
    if (!this.infoSection.isFormValid()) {
      return;
    }
    this.creating = true;
    this.sb.createAssessment(this.infoSection.mapFormToSurvey()).subscribe((res) => {
      this.creating = false;
      const id = res.entity;
      this.sb.openSnackbar("Үнэлгээ амжилттай үүсгэлээ.", true);
      if (saveAndContinue) {
        this.sb.navigateByUrl('/survey/update/' + id);
      } else {
        this.goBack();
      }
    }, () => {
      this.creating = false;
      this.sb.openSnackbar("Үнэлгээ үүсгэхэд алдаа гарлаа!");
    })
  }

}
