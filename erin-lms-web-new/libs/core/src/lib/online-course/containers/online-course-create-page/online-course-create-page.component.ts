import {Component, OnInit} from '@angular/core';
import {OnlineCourseSandboxService} from "../../online-course-sandbox.service";
import {OnlineCourseModel} from "../../models/online-course.model";
import {Survey} from "../../../common/common.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {CertificateModel} from "../../../certificate/model/certificate.model";
import {map} from "rxjs/operators";
import {forkJoin} from "rxjs";
import {EMPTY_CATEGORY} from "../../models/online-course.constant";
import {CategoryItem} from "../../../../../../shared/src/lib/shared-model";
import {EMPTY_SURVEY} from "../../../survey/survey.constants";
import {EMPTY_CERTIFICATE} from "../../../certificate/model/certificate.constants";

@Component({
  selector: 'jrs-online-course-create-page',
  template: `
      <jrs-button class="create-page-return-btn"
        [iconName]="'arrow_back_ios'"
        [iconColor]="'secondary'"
        [noOutline]="true"
        [isMaterial]="true"
        [size]="'medium'"
        [bold]="true"
        [textColor]="'text-link'"
        (clicked)="goBack()"
        [attr.data-test-id]="'back-button'">БУЦАХ
      </jrs-button>
      <jrs-section>
        <jrs-header-text [size]="'medium'">ЦАХИМ СУРГАЛТ ҮҮСГЭХ</jrs-header-text>
        <ng-container *ngIf="!loading">
          <form [formGroup]="onlineCourseFormGroup">
            <div class="row gx-5 margin-top">
              <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12 margin-top">
                <jrs-input-field
                  [formGroup]="onlineCourseFormGroup"
                  [formType]="'name'"
                  [placeholder]="'Сургалтын нэр'"
                  [selectedType]="'text'"
                  [movePlaceholder]="true"
                  [required]="true"
                  [attr.data-test-id]="'course-name-input-field'">
                </jrs-input-field>
                <div class="row gx-1">
                  <div class="col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                    <jrs-dropdown-view
                      [formGroup]="onlineCourseFormGroup"
                      [formType]="'category'"
                      [placeholder]="'Сургалтын ангилал'"
                      [padding]="true"
                      [outlined]="true"
                      [values]="categories"
                      [required]="true"
                      (selectedValue)="categorySelected($event)"
                      [attr.data-test-id]="'course-category-dropdown-field'">
                    </jrs-dropdown-view>
                  </div>
                  <div class="col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                    <jrs-dropdown-view
                      [formGroup]="onlineCourseFormGroup"
                      [formType]="'type'"
                      [chooseFirst]="true"
                      [defaultValue]="this.selectedType"
                      [placeholder]="'Хамрах хүрээ'"
                      [required]="true"
                      [padding]="true"
                      [outlined]="true"
                      [values]="courseTypes"
                      [attr.data-test-id]="'course-type-dropdown-field'">
                    </jrs-dropdown-view>
                  </div>
                  <div class="col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                    <jrs-input-field
                      [formGroup]="onlineCourseFormGroup"
                      [formType]="'credit'"
                      [placeholder]="'Багц цаг'"
                      [required]="false"
                      [selectedType]="'number'">
                    </jrs-input-field>
                  </div>
                </div>

                <div class="row gx-1 ">
                  <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                    <jrs-dropdown-view
                      [formGroup]="onlineCourseFormGroup"
                      [formType]="'survey'"
                      [placeholder]="'Үнэлгээний хуудас'"
                      [padding]="true"
                      [icon]="'expand_more'"
                      [outlined]="true"
                      [values]="surveys"
                      [attr.data-test-id]="'course-scoring-dropdown-field'">
                    </jrs-dropdown-view>
                  </div>

                  <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                    <jrs-dropdown-view
                      [formGroup]="onlineCourseFormGroup"
                      [formType]="'certificate'"
                      [placeholder]="'Сертификат'"
                      [icon]="'expand_more'"
                      [padding]="true"
                      [outlined]="true"
                      [values]="certificates"
                      [attr.data-test-id]="'course-certificate-dropdown-field'">
                    </jrs-dropdown-view>
                  </div>
                </div>
                <div class="row gx-1">
                  <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                    <jrs-text-area
                      [placeholder]="'Товч утга'"
                      [size]="'medium'"
                      [formGroup]="onlineCourseFormGroup"
                      [formType]="'summary'"
                      [attr.data-test-id]="'course-summary-textarea-field'">
                    </jrs-text-area>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </ng-container>
        <jrs-skeleton-loader [amount]="6" [load]="loading"></jrs-skeleton-loader>
      </jrs-section>
      <div class="flex-center margin-top margin-bottom-large">
        <jrs-button
          class="side-margin"
          [title]="'ҮҮСГЭХ'"
          [color]="'primary'"
          [size]="'medium'"
          [float]="'center'"
          [load]="loading || saving"
          (clicked)="createCourse(false)"
          [attr.data-test-id]="'create-button'">
        </jrs-button>
        <jrs-button
          class="side-margin center"
          [title]="'ҮРГЭЛЖЛҮҮЛЭХ'"
          [color]="'primary'"
          [size]="'medium'"
          [load]="loading || saving"
          (clicked)="createCourse(true)"
          [attr.data-test-id]="'next-button'">
        </jrs-button>
      </div>
  `
})
export class OnlineCourseCreatePageComponent implements OnInit {
  loading = true;
  saving = false;
  categories: CategoryItem[] = [];
  selectedCategory = EMPTY_CATEGORY;
  surveys: Survey[] = [];
  emptySurvey = EMPTY_SURVEY;
  emptyCertificate = EMPTY_CERTIFICATE;
  certificates: CertificateModel[] = [];
  onlineCourseFormGroup: FormGroup;
  courseTypes = this.sb.constants.COURSE_TYPES;
  selectedType: string;
  compId = 'online-create-page';
  leaveWithoutSave = "Цахим сургалт үүсгэхгүй буцах уу?";

  constructor(private sb: OnlineCourseSandboxService) {
  }

  ngOnInit(): void {
    const tasks$ = [];
    tasks$.push(this.sb.getOnlineCourseCategories().pipe(map(res => {
      this.categories = res;
      this.selectedCategory = this.sb.getSelectedCategory() ? this.sb.getSelectedCategory() : this.categories[0];
    })));
    tasks$.push(this.sb.getCourseSurveyTemplates().pipe(map(res => {
      this.surveys = res;
      this.surveys.unshift(this.emptySurvey);
    })));
    tasks$.push(this.sb.getCertificateTemplates().pipe(map(res => {
      this.certificates = res;
      this.certificates.unshift(this.emptyCertificate);
    })));
    forkJoin(...tasks$).subscribe(() => {
      this.setForm();
      this.loading = false;
    }, () => {
      this.sb.openSnackbar("Цахим сургалт үүсгэх хуудас ачаалахад алдаа гарлаа");
      this.loading = false;
    });
  }

  goBack(): void {
    this.sb.goBack();
  }

  createCourse(saveAndContinue: boolean): void {
    if (!this.isFormValid()) {
      return;
    }
    this.saving = true;
    this.sb.createOnlineCourse(this.getOnlineCourseModel()).subscribe((id) => {
      this.sb.setSelectedCategory(this.selectedCategory);
      this.saving = false;
      this.sb.openSnackbar("Цахим сургалт амжилттай үүсгэлээ", true);
      if (saveAndContinue) {
        this.sb.navigateByUrl('/online-course/update/' + id);
      } else {
        this.goBack();
      }
    }, () => {
      this.saving = false;
      this.sb.openSnackbar("Цахим сургалт үүсгэхэд алдаа гарлаа");
    })
  }

  categorySelected(category): void {
    this.selectedCategory = category;
  }

  private setForm(): void {
    this.onlineCourseFormGroup = new FormGroup({
      name: new FormControl('', [Validators.required]),
      category: new FormControl(this.selectedCategory, [Validators.required]),
      type: new FormControl(this.courseTypes[0], [Validators.required]),
      survey: new FormControl(''),
      certificate: new FormControl(''),
      summary: new FormControl(''),
      credit: new FormControl()
    })
  }

  private getOnlineCourseModel(): OnlineCourseModel {
    return {
      id:'',
      name: this.onlineCourseFormGroup.controls.name.value.trim(),
      categoryId: this.selectedCategory.id,
      typeId: this.onlineCourseFormGroup.controls.type.value.id,
      credit: this.onlineCourseFormGroup.controls.credit.value,
      description: this.onlineCourseFormGroup.controls.summary.value,
      surveyId: this.onlineCourseFormGroup.controls.survey.value.id,
      certificateId: this.onlineCourseFormGroup.controls.certificate.value.id
    };
  }

  private isFormValid(): boolean {
    let invalids = 0;
    for (const [key] of Object.entries(this.onlineCourseFormGroup.controls)) {
      if (this.onlineCourseFormGroup.controls[key].invalid) {
        invalids++;
        this.onlineCourseFormGroup.controls[key].markAsDirty();
      }
    }
    return invalids == 0;
  }
}
