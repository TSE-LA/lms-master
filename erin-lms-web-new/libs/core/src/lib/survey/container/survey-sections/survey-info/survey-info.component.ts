import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {CreateSurveyModel, Survey, SurveyStatus} from "../../../model/survey.model";
import {FormUtil} from "../../../../../../../shared/src/lib/utilities/form.util";
import {Observable, throwError} from "rxjs";
import {SurveySandboxService} from "../../../services/survey-sandbox.service";
import {catchError, map} from "rxjs/operators";

@Component({
  selector: 'jrs-survey-info',
  template: `
    <jrs-section [color]="'primary'" [outline]="false">

      <jrs-header-text [size]="'medium'">ҮНЭЛГЭЭНИЙ МЭДЭЭЛЭЛ</jrs-header-text>
      <div class="margin-left-double">
        <jrs-info-panel *ngIf="active" [text]="'Идэвхтэй үнэлгээний хуудасны асуултууд засварлах боломжгүйг анхаарна уу.'" [width]="'94%'"></jrs-info-panel>
      </div>
      <ng-container>
        <form [formGroup]="surveyFormGroup"></form>
        <div class="row gx-5 margin-top">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12 margin-top">
            <jrs-input-field
              [formGroup]="surveyFormGroup"
              [formType]="'name'"
              [placeholder]="'Үнэлгээний нэр'"
              [selectedType]="'text'"
              [errorText]="'Заавал бөглөнө үү'"
              [movePlaceholder]="true"
              [required]="true">
            </jrs-input-field>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-text-area
              [placeholder]="'Товч утга'"
              [size]="'medium'"
              [formGroup]="surveyFormGroup"
              [formType]="'summary'"
              [required]="true">
            </jrs-text-area>
          </div>
        </div>
      </ng-container>
    </jrs-section>`,
  styles: []
})
export class SurveyInfoComponent implements OnChanges {
  @Input() survey: Survey;
  surveyFormGroup: FormGroup;
  initialSurvey: CreateSurveyModel;
  active = false;

  constructor(private sb: SurveySandboxService) {
    this.setForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'survey' && this.survey) {
        this.active = this.survey.status == SurveyStatus.ACTIVE;
        this.initialSurvey = {name: this.survey.name, summary: this.survey.summary}
        this.assignFormValues();
      }
    }
  }

  isFormValid(): boolean {
    return FormUtil.isFormValid(this.surveyFormGroup);
  }

  isDataChanged(): boolean {
    return JSON.stringify(this.initialSurvey) !== JSON.stringify(this.mapFormToSurvey());
  }

  mapFormToSurvey(): CreateSurveyModel {
    return {
      name: this.surveyFormGroup.controls.name.value.trim(),
      summary: this.surveyFormGroup.controls.summary.value
    }
  }

  save(order: Observable<any>[]): Observable<any>[] {
    const model = this.mapFormToSurvey();
    order.push(this.sb.updateSurvey(this.survey.id, model.name, model.summary).pipe(map(() => {
      this.sb.openSnackbar("Үнэлгээний мэдээлэл амжилттай хадгаллаа.", true);
      this.initialSurvey = {name: model.name, summary: model.summary}
    }), catchError(err => {
      this.sb.openSnackbar("Үнэлгээний мэдээлэл хадгалахад алдаа гарлаа.");
      return throwError(err);
    })));
    return order;
  }

  private setForm(): void {
    this.surveyFormGroup = new FormGroup({
      name: new FormControl('', [Validators.required]),
      summary: new FormControl(''),
    })
  }

  private assignFormValues(): void {
    this.surveyFormGroup.controls.name.setValue(this.survey.name);
    this.surveyFormGroup.controls.summary.setValue(this.survey.summary);
  }
}
