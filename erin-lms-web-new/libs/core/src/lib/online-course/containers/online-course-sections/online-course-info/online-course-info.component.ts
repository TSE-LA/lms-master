import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {OnlineCourseModel} from "../../../models/online-course.model";
import {EMPTY_CATEGORY} from "../../../models/online-course.constant";
import {Survey} from "../../../../common/common.model";
import {CertificateModel} from "../../../../certificate/model/certificate.model";
import {CategoryItem, PublishStatus} from "../../../../../../../shared/src/lib/shared-model";
import {OnlineCourseSandboxService} from "../../../online-course-sandbox.service";
import {catchError, map} from "rxjs/operators";
import {forkJoin, throwError} from "rxjs";
import {FormUtil} from "../../../../../../../shared/src/lib/utilities/form.util";
import {EMPTY_SURVEY} from "../../../../survey/survey.constants";
import {EMPTY_CERTIFICATE} from "../../../../certificate/model/certificate.constants";

@Component({
  selector: 'jrs-online-course-info',
  template: `
    <div *ngIf="!load">
      <jrs-input-field
        [formGroup]="onlineCourseFormGroup"
        [formType]="'name'"
        [placeholder]="'Сургалтын нэр'"
        [selectedType]="'text'"
        [movePlaceholder]="true"
        [required]="true"
        id="'course-input-field'">
      </jrs-input-field>

      <div class="row gx-1">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-4">
          <jrs-dropdown-view
            [formGroup]="onlineCourseFormGroup"
            [formType]="'category'"
            [defaultValue]="selectedCategory.name"
            [placeholder]="'Сургалтын ангилал'"
            [padding]="true"
            [outlined]="true"
            [values]="categories"
            [id]="'course-category-dropdown-field'">
          </jrs-dropdown-view>
        </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-4">
            <jrs-dropdown-view
              [formGroup]="onlineCourseFormGroup"
              [formType]="'type'"
              [defaultValue]="this.selectedType"
              [placeholder]="'Хамрах хүрээ'"
              [padding]="true"
              [outlined]="true"
              [values]="courseTypes"
              [id]="'course-type-dropdown-field'">
            </jrs-dropdown-view>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-4">
            <jrs-input-field
              [formGroup]="onlineCourseFormGroup"
              [formType]="'credit'"
              [size]="'medium'"
              [movePlaceholder]="true"
              [placeholder]="'Багц цаг'"
              [required]="false"
              [selectedType]="'text'">
            </jrs-input-field>
          </div>
      </div>

      <div class="row gx-1">
        <div *ngIf="!published" class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
          <jrs-dropdown-view
            [formGroup]="onlineCourseFormGroup"
            [formType]="'survey'"
            [placeholder]="'Үнэлгээний хуудас'"
            [padding]="true"
            [icon]="'expand_more'"
            [outlined]="true"
            [values]="surveys"
            [id]="'course-scoring-dropdown-field'">
          </jrs-dropdown-view>
        </div>
        <div *ngIf="!published" class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
          <jrs-dropdown-view
            [formGroup]="onlineCourseFormGroup"
            [formType]="'certificate'"
            [placeholder]="'Сертификат'"
            [icon]="'expand_more'"
            [padding]="true"
            [outlined]="true"
            [values]="certificates"
            [id]="'course-certificate-dropdown-field'">
          </jrs-dropdown-view>
        </div>

        <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
          <jrs-file-field [acceptFileTypes]="thumbnailFileTypes"
                          [acceptRegex]="allowedThumbnailExtensions"
                          [fileName]="fileName"
                          [tooltip]="'Thumbnail хавсаргах'"
                          (selectionChange)="uploadThumbnail($event)"
                          [id]="'thumbnail-file-upload-field'">
          </jrs-file-field>
        </div>
      </div>
      <div class="row gx-1 margin-bottom" *ngIf="!published">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
          <jrs-checkbox [padding]="false" [check]="hasTest" (checked)="changeCheck($event)" [text]="'Сорилтой эсэх?'" [id]="'course-test-checkbox-field'"></jrs-checkbox>
        </div>
      </div>
      <jrs-text-area
        [placeholder]="'Товч утга'"
        [size]="'medium'"
        [formGroup]="onlineCourseFormGroup"
        [formType]="'summary'"
        [id]="'course-description-textarea-field'">
      </jrs-text-area>
    </div>
    <jrs-skeleton-loader [amount]="5" [load]="load"></jrs-skeleton-loader>

    <jrs-page-loader [show]="savingThumbnail"></jrs-page-loader>
  `,
  styles: []
})
export class OnlineCourseInfoComponent implements OnChanges {
  @Input() course: OnlineCourseModel;
  @Input() load = false;
  @Output() testChange = new EventEmitter<boolean>();
  onlineCourseFormGroup: FormGroup;
  categories: CategoryItem[] = []
  selectedCategory = EMPTY_CATEGORY;
  emptySurvey = EMPTY_SURVEY;
  emptyCertificate = EMPTY_CERTIFICATE;
  surveys: Survey[] = [];
  certificates: CertificateModel[] = [];
  courseTypes = this.sb.getCourseTypes();
  selectedType: string;
  thumbnailFileTypes = ".mp4, .webm, .jpeg, .png, .gif, .jpg";
  allowedThumbnailExtensions = /(\.mp4|\.webm|\.jpeg|\.png|\.gif|\.jpg)$/i;
  savingThumbnail: boolean;
  hasTestInitial = true;
  hasTest = true;
  published: boolean;
  sendNotification: boolean;
  fileName: string;
  private newThumbnail = false;
  private thumbnailId: string;
  private thumbnailPath: string;

  constructor(private sb: OnlineCourseSandboxService) {
    this.setForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "course" && this.course) {
        this.hasTest = this.course.hasTest;
        this.hasTestInitial = this.course.hasTest;
        this.thumbnailPath = this.course.thumbnailUrl;
        if (this.course.publishStatus == PublishStatus.PUBLISHED) {
          this.published = true;
        }
        this.loadData();
      }
    }
  }

  saveInfo(order: any[]): any[] {
    const valid = FormUtil.isFormValid(this.onlineCourseFormGroup);
    if (valid) {
      const mappedCourse = this.mapFormToCourse();
      order.push(this.sb.updateOnlineCourse(mappedCourse, this.course.id, this.sendNotification).pipe(map(() => {
        this.course = Object.assign(mappedCourse);
        if (this.newThumbnail) {
          this.savingThumbnail = true;
          this.sb.updateThumbnail(this.thumbnailId, this.course.id).subscribe(() => {
            this.sb.openSnackbar("Цахим сургалтын thumbnail амжилттай хадгаллаа.", true);
            this.newThumbnail = false;
            this.savingThumbnail = false;
          }, () => {
            this.sb.openSnackbar("Цахим сургалтын thumbnail хадгалахад алдаа гарлаа.");
            this.savingThumbnail = false;
          });
        } else {
          this.sb.openSnackbar("Цахим сургалт амжилттай хадгаллаа.", true);
        }
      }), catchError(err => {
        this.sb.openSnackbar("Цахим сургалт хадгалахад алдаа гарлаа.");
        return throwError(err);
      })));
    }
    return order;
  }

  isDataChanged(): boolean {
    return this.newThumbnail || (JSON.stringify(this.course) !== JSON.stringify(this.mapFormToCourse()));
  }

  uploadThumbnail(file: File): void {
    this.sb.thumbnailFileName = file.name;
    this.savingThumbnail = true;
    this.sb.uploadThumbnail(file).subscribe((res: any) => {
      this.savingThumbnail = false;
      if (res) {
        this.thumbnailId = res.fileId;
        this.thumbnailPath = res.path;
        this.newThumbnail = true;
      }
    }, () => {
      this.savingThumbnail = false;
      this.sb.snackbarService.open("Файл ачааллахад алдаа гарлаа!")
    });
  }

  changeCheck(check: boolean): void {
    this.hasTest = check;
    this.testChange.emit(this.hasTest);
  }

  private setForm(): void {
    this.onlineCourseFormGroup = new FormGroup({
      name: new FormControl('', [Validators.required]),
      category: new FormControl('', [Validators.required]),
      type: new FormControl('', [Validators.required]),
      survey: new FormControl(this.emptySurvey),
      certificate: new FormControl(this.emptyCertificate),
      summary: new FormControl(''),
      credit: new FormControl(0)
    })
  }

  private loadData(): void {
    const tasks = [];
    tasks.push(this.sb.getOnlineCourseCategories().pipe(map(res => {
      this.categories = res;
      this.selectedCategory = this.sb.getSelectedCategory() ? this.sb.getSelectedCategory() : this.categories[0];
      return res;
    }), catchError(err => {
      this.sb.openSnackbar("Цахим сургалын ангилал ачааллахад алдаа гарлаа!");
      return throwError(err)
    })));

    tasks.push(this.sb.getCourseSurveyTemplates().pipe(map(res => {
      this.surveys = res;
      this.surveys.unshift(this.emptySurvey);
      return res;
    }), catchError(err => {
      this.sb.openSnackbar("Үнэлгээний хуудас ачааллахад алдаа гарлаа!");
      return throwError(err)
    })));

    tasks.push(this.sb.getCertificateTemplates().pipe(map(res => {
      this.certificates = res;
      this.certificates.unshift(this.emptyCertificate);
      return res;
    }), catchError(err => {
      this.sb.openSnackbar("Сертификат ачааллахад алдаа гарлаа!");
      return throwError(err)
    })));

    if (this.sb.thumbnailFileName) {
      this.fileName = this.sb.thumbnailFileName;
    }

    forkJoin(tasks).subscribe(() => {
      this.assignFormValues();
    });
  }

  private assignFormValues(): void {
    this.onlineCourseFormGroup.controls.name.setValue(this.course.name);
    const courseCategory = this.categories.find(category => category.id == this.course.categoryId);
    this.sb.setSelectedCategory(courseCategory);
    this.onlineCourseFormGroup.controls.category.setValue(courseCategory);
    const courseType = this.courseTypes.find(type => type.id == this.course.typeId);
    this.onlineCourseFormGroup.controls.type.setValue(courseType);
    this.onlineCourseFormGroup.controls.name.setValue(this.course.name);
    this.onlineCourseFormGroup.controls.credit.setValue(this.course.credit);
    this.onlineCourseFormGroup.controls.summary.setValue(this.course.description);
    if (this.course.surveyId) {
      const courseSurvey = this.surveys.find(survey => survey.id == this.course.surveyId);
      this.onlineCourseFormGroup.controls.survey.setValue(courseSurvey);
    }
    if (this.course.certificateId) {
      const courseCertificate = this.certificates.find(certificate => certificate.id == this.course.certificateId);
      this.onlineCourseFormGroup.controls.certificate.setValue(courseCertificate);
    }
  }

  private mapFormToCourse(): OnlineCourseModel {
    const controls = this.onlineCourseFormGroup.controls;
    return {
      id: this.course.id,
      name: controls.name.value,
      categoryId: controls.category.value.id,
      description: controls.summary.value,
      typeId: controls.type.value.id,
      createdDate: this.course.createdDate,
      contentId: this.course.contentId,
      testId: this.course.testId,
      credit: controls.credit.value,
      hasTest: this.hasTest,
      publishStatus: this.course.publishStatus,
      publishDate: this.course.publishDate,
      surveyId: controls.survey.value ? (controls.survey.value.id ? controls.survey.value.id : null) : this.course.surveyId,
      hasSurvey: controls.survey.value ? !!controls.survey.value.id : !!controls.survey.value,
      certificateId: controls.certificate.value ? (controls.certificate.value.id ? controls.certificate.value.id : null) : this.course.certificateId,
      hasCertificate: controls.certificate.value ? !!controls.certificate.value.id : !!controls.certificate.value,
      assignedLearners: this.course.assignedLearners,
      assignedDepartments: this.course.assignedDepartments,
      modifiedDate: this.course.modifiedDate,
      thumbnailUrl: this.thumbnailPath,
      author: this.course.author
    }
  }
}
