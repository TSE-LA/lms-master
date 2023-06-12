import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {ClassroomCourseModel, ClassroomCourseUpdateModel, InstructorDropdownModel} from "../../../model/classroom-course.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {EMPTY_SURVEY} from "../../../../survey/survey.constants";
import {EMPTY_CERTIFICATE} from "../../../../certificate/model/certificate.constants";
import {CategoryItem} from "../../../../../../../shared/src/lib/shared-model";
import {CertificateModel} from "../../../../certificate/model/certificate.model";
import {Survey} from "../../../../common/common.model";
import {forkJoin, Observable, throwError} from "rxjs";
import {ClassroomCourseSandboxService} from "../../../classroom-course-sandbox.service";
import {FormUtil} from "../../../../../../../shared/src/lib/utilities/form.util";
import {catchError, map} from "rxjs/operators";
import {DateFormatter} from "../../../../../../../shared/src/lib/utilities/date-formatter.util";
import {TimeUtil} from "../../../../../../../shared/src/lib/utilities/time.util";

@Component({
  selector: 'jrs-classroom-course-info',
  template: `
    <jrs-section>
      <jrs-header-text [size]="'medium'">СУРГАЛТЫН ТӨЛӨВЛӨГӨӨ</jrs-header-text>
      <form [formGroup]="classroomCourseFormGroup" >
        <div class="row gx-5" *ngIf="!load">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6">

            <jrs-input-field
              [formGroup]="classroomCourseFormGroup"
              [formType]="'name'"
              [placeholder]="'Сургалтын нэр'"
              [selectedType]="'text'"
              [movePlaceholder]="true"
              [required]="true">
            </jrs-input-field>

            <div class="row gx-1">
              <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6">
                <jrs-dropdown-view
                  [formGroup]="classroomCourseFormGroup"
                  [placeholder]="'Сургалтын ангилал'"
                  [formType]="'category'"
                  [size]="'medium'"
                  [chooseFirst]="true"
                  [icon]="'expand_more'"
                  [outlined]="true"
                  [load]="load"
                  [required]="true"
                  [padding]="true"
                  [values]="categories">
                </jrs-dropdown-view>
              </div>

              <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6">
                <jrs-dropdown-view
                  [formGroup]="classroomCourseFormGroup"
                  [placeholder]="'Сургагч багш'"
                  [formType]="'instructor'"
                  [icon]="'expand_more'"
                  [size]="'medium'"
                  [required]="true"
                  [chooseFirst]="false"
                  [outlined]="true"
                  [load]="load"
                  [padding]="true"
                  [values]="instructors">
                </jrs-dropdown-view>
              </div>
            </div>

            <div class="row gx-1">
              <div class="col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                <jrs-input-field
                  [formGroup]="classroomCourseFormGroup"
                  [formType]="'maxEnrollmentCount'"
                  [placeholder]="'Хүний тоо'"
                  [selectedType]="'number'"
                  [movePlaceholder]="true"
                  [required]="true">
                </jrs-input-field>
              </div>
              <div class="col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                <jrs-dropdown-view
                  [formGroup]="classroomCourseFormGroup"
                  [placeholder]="'Хамрах хүрээ'"
                  [formType]="'type'"
                  [icon]="'expand_more'"
                  [size]="'medium'"
                  [required]="true"
                  [outlined]="true"
                  [load]="load"
                  [padding]="true"
                  [values]="courseTypes">
                </jrs-dropdown-view>
              </div>
              <div class="margin col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                <jrs-file-field [formGroup]="classroomCourseFormGroup"
                                [fileName]="'fileName'"
                                [file]="'file'"
                                (selectionChange)="uploadFile($event)">
                </jrs-file-field>
              </div>
            </div>

            <div class="row gx-1">
              <div class="col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                <jrs-dropdown-view
                  [formGroup]="classroomCourseFormGroup"
                  [placeholder]="'Үнэлгээний хуудас'"
                  [formType]="'survey'"
                  [icon]="'expand_more'"
                  [size]="'medium'"
                  [outlined]="true"
                  [load]="load"
                  [padding]="true"
                  [values]="surveyTemplates">
                </jrs-dropdown-view>
              </div>

              <div class="col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                <jrs-dropdown-view
                  [formGroup]="classroomCourseFormGroup"
                  [formType]="'certificate'"
                  [size]="'medium'"
                  [placeholder]="'Сертификат'"
                  [icon]="'expand_more'"
                  [outlined]="true"
                  [load]="load"
                  [padding]="true"
                  [values]="certificates">
                </jrs-dropdown-view>
              </div>

              <div class="col-media_s-12 col-media_xl-4 col-media_md-4 col-media_sm-12">
                <jrs-input-field
                  [formGroup]="classroomCourseFormGroup"
                  [formType]="'credit'"
                  [selectedType]="'text'"
                  [movePlaceholder]="true"
                  [placeholder]="'Багц цаг'"
                >
                </jrs-input-field>
              </div>
            </div>

            <jrs-text-area
              [placeholder]="'Товч утга'"
              [size]="'medium'"
              [formGroup]="classroomCourseFormGroup"
              [formType]="'summary'">
            </jrs-text-area>

            <div *ngIf="!published" class="margin-bottom">
              <jrs-checkbox
                [padding]="false"
                [check]="openSuperInvitation"
                [text]="'Ахлах менежер сургалтад урих урилга илгээх'"
                (checked)="openInvitationSection($event)">
              </jrs-checkbox>
            </div>
          </div>

          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6">
            <div class="row gx-1">
              <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                <jrs-input-field
                  [formGroup]="classroomCourseFormGroup"
                  [formType]="'address'"
                  [placeholder]="'Байршил'"
                  [selectedType]="'text'"
                  [movePlaceholder]="true"
                  [padding]="false"
                  [required]="true">
                </jrs-input-field>
              </div>
            </div>
            <div class="row gx-1">
              <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
                <jrs-date-picker
                  [formGroup]="classroomCourseFormGroup"
                  [formType]="'startDate'"
                  [datePickerSize]="'standard'"
                  [label]="'Эхлэх өдөр'"
                  [width]="'100%'">
                </jrs-date-picker>
              </div>
              <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
                <jrs-time-picker
                  [formGroup]="classroomCourseFormGroup"
                  [hourFormType]="'startHour'"
                  [minuteFormType]="'startMin'"
                  [label]="'Эхлэх цаг'"
                  [size]="'long'">
                </jrs-time-picker>
              </div>
              <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
                <jrs-time-picker
                  [formGroup]="classroomCourseFormGroup"
                  [hourFormType]="'endHour'"
                  [minuteFormType]="'endMin'"
                  [label]="'Дуусах цаг'"
                  [size]="'long'">
                </jrs-time-picker>
              </div>
            </div>
          </div>
        </div >
      </form>
      <jrs-skeleton-loader [load]="load" [amount]="5"></jrs-skeleton-loader>
    </jrs-section>
    <jrs-page-loader [show]="uploading"></jrs-page-loader>
  `,
  styles: []
})
export class ClassroomCourseInfoComponent implements OnChanges {
  @Input() course: ClassroomCourseModel;
  @Input() published: boolean;
  @Output() openInvitation = new EventEmitter<boolean>();
  initialCourse: ClassroomCourseUpdateModel;
  courseId: string;
  classroomCourseFormGroup: FormGroup;
  emptySurvey = EMPTY_SURVEY;
  emptyCertificate = EMPTY_CERTIFICATE;
  categories: CategoryItem[] = [];
  instructors: InstructorDropdownModel[] = [];
  courseTypes: any[] = [];
  certificates: CertificateModel[] = [];
  surveyTemplates: Survey[] = [];
  currentUser = this.sb.username;
  openSuperInvitation = false;

  uploading: boolean;
  load = true;

  attachmentName: string;
  private attachmentId: string;
  private attachmentRenderedName: string;

  constructor(private sb: ClassroomCourseSandboxService) {
    this.setFormGroup();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "course" && this.course) {
        this.courseId = this.course.id;
        this.initialCourse = this.mapToClassroomUpdateModel();
        this.loadPage();
      }
    }
  }

  isDataChanged(): boolean {
    return JSON.stringify(this.initialCourse) !== JSON.stringify(this.mapFormToClassroomUpdateModel());
  }

  goBack(): void {
    this.navigateToCalendar();
  }

  uploadFile(file: File): void {
    this.uploading = true;
    this.sb.uploadAttachment(file).subscribe((res: any) => {
      this.uploading = false;
      if (res) {
        this.attachmentId = res.fileId;
        this.attachmentRenderedName = res.name;
      }
    }, () => {
      this.uploading = false;
      this.sb.openSnackbar('Хавсралт ачаалахад алдаа гарлаа!');
    });
  }

  save(saveOrder: any[]): Observable<any>[] {
    if (!FormUtil.isFormValid(this.classroomCourseFormGroup)) {
      return saveOrder;
    }
    saveOrder.push(this.sb.updateClassroomCourse(this.mapFormToClassroomUpdateModel()).pipe(map(() => {

      if (this.attachmentId) {
        this.sb.updateAttachment(this.courseId, this.attachmentId).subscribe(() => {
          this.sb.openSnackbar('Танхимын сургалтын хавсралт шинэчлэгдлээ', true);
        }, () => {
          this.sb.openSnackbar('Танхимын сургалтын хавсралт засварлахад алдаа гарлаа!');
        });
      }
      this.sb.openSnackbar('Танхимын сургалт амжилттай шинэчлэгдлээ', true);
      this.navigateToCalendar();
    }), catchError(err => {
      this.sb.openSnackbar('Танхимын сургалт засварлахад алдаа гарлаа');
      return throwError(err);
    })));
    return saveOrder;
  };

  openInvitationSection($event: boolean): void {
    this.openInvitation.emit($event);
  }

  navigateToCalendar(): void {
    this.sb.navigateByUrl('/classroom-course')
  }

  private getAllCertificates(): Observable<any> {
    return this.sb.getCertificates().pipe(map((certificates) => {
      this.certificates = certificates;
      this.certificates.unshift(this.emptyCertificate);
      return certificates;
    }))
  }

  private getAllCategories(): Observable<any> {
    return this.sb.getClassroomCourseCategories().pipe(map((categories) => {
      if (categories) {
        this.categories = categories;
        this.load = false;
      } else {
        this.categories = [];
      }
      return categories;
    }), catchError(error => {
      return throwError(error)
    }));
  }

  private getAllInstructors(): Observable<any> {
    return this.sb.getInstructors().pipe(map((instructors) => {
      this.instructors = instructors;
    }));
  }

  private getAllSurveyTemplates(): Observable<any> {
    return this.sb.getSurveys().pipe(map((surveys: Survey[]) => {
      this.surveyTemplates = surveys;
      this.surveyTemplates.unshift(this.emptySurvey);
      return surveys;
    }));
  }

  private loadPage(): void {
    this.courseTypes = this.sb.constants.COURSE_TYPES;
    const observables = [];
    observables.push(this.getAllCategories());
    observables.push(this.getAllInstructors());
    observables.push(this.getAllSurveyTemplates());
    observables.push(this.getAllCertificates());
    this.load = true;
    forkJoin(observables).subscribe(() => {
      this.assignFormValues();
      this.load = false;
    }, () => {
      this.load = false;
      this.sb.openSnackbar('Танхимын сургалт засварлах хуудас ачаллахад алдаа гарлаа!');
    });
  }

  private assignFormValues(): void {
    this.classroomCourseFormGroup.controls.name.setValue(this.course.name);
    const courseCategory = this.categories.find(category => category.id == this.course.categoryId);
    this.classroomCourseFormGroup.controls.credit.setValue(this.course.credit)
    this.classroomCourseFormGroup.controls.category.setValue(courseCategory);
    this.classroomCourseFormGroup.controls.instructor.setValue({name: this.course.instructor, id: this.course.instructor});
    this.classroomCourseFormGroup.controls.maxEnrollmentCount.setValue(this.course.enrollment.maxEnrollmentCount);
    if (this.course.typeId) {
      const courseType = this.courseTypes.find(type => type.id == this.course.typeId);
      this.classroomCourseFormGroup.controls.type.setValue(courseType);
    }
    this.classroomCourseFormGroup.controls.fileName.setValue(this.course.attachment.renderedName);
    if (this.course.certificateId) {
      const courseCertificate = this.certificates.find(certificate => certificate.id == this.course.certificateId);
      this.classroomCourseFormGroup.controls.certificate.setValue(courseCertificate)
    }
    if (this.course.surveyId) {
      const courseSurvey = this.surveyTemplates.find(surveys => surveys.id == this.course.surveyId);
      this.classroomCourseFormGroup.controls.survey.setValue(courseSurvey)
    }
    this.classroomCourseFormGroup.controls.summary.setValue(this.course.description);
    this.classroomCourseFormGroup.controls.address.setValue(this.course.address);
    this.classroomCourseFormGroup.controls.startDate.setValue(new Date(this.course.dateAndTime.date));
    this.classroomCourseFormGroup.controls.startHour.setValue(TimeUtil.getHour(this.course.dateAndTime.startTime));
    this.classroomCourseFormGroup.controls.startMin.setValue(TimeUtil.getMinute(this.course.dateAndTime.startTime));
    this.classroomCourseFormGroup.controls.endHour.setValue(TimeUtil.getHour(this.course.dateAndTime.endTime));
    this.classroomCourseFormGroup.controls.endMin.setValue(TimeUtil.getMinute(this.course.dateAndTime.endTime));
  }

  private setFormGroup(): void {
    this.classroomCourseFormGroup = new FormGroup({
      name: new FormControl('', [Validators.required]),
      category: new FormControl('', [Validators.required]),
      instructor: new FormControl('', [Validators.required]),
      maxEnrollmentCount: new FormControl(0, [Validators.required]),
      type: new FormControl('', [Validators.required]),
      file: new FormControl(''),
      fileName: new FormControl(''),
      survey: new FormControl(this.emptySurvey),
      certificate: new FormControl(this.emptyCertificate),
      credit: new FormControl(0),
      summary: new FormControl(''),
      address: new FormControl(''),
      startDate: new FormControl(),
      startHour: new FormControl(),
      startMin: new FormControl(),
      endHour: new FormControl(),
      endMin: new FormControl()
    });
  }

  private mapToClassroomUpdateModel(): ClassroomCourseUpdateModel {
    return {
      id: this.course.id,
      name: this.course.name,
      categoryId: this.course.categoryId,
      typeId: this.course.typeId,
      description: this.course.description,
      instructor: this.course.instructor,
      instructorNumber: this.course.instructorNumber,
      maxEnrollmentCount: Number(this.course.enrollment.maxEnrollmentCount),
      state: this.course.state,
      previousState: this.course.previousState,
      attachment: {
        id: this.course.attachment.id,
        name: this.course.attachment.name,
        renderedName: this.course.attachment.renderedName
      },
      credit: this.course.credit,
      surveyId: this.course.surveyId,
      certificateId: this.course.certificateId,
      address: this.course.address,
      startDate: this.course.dateAndTime.date,
      startTime: this.course.dateAndTime.startTime,
      endTime: this.course.dateAndTime.endTime
    }
  }

  private mapFormToClassroomUpdateModel(): ClassroomCourseUpdateModel {
    const controls = this.classroomCourseFormGroup.controls;
    const startDate = new Date(DateFormatter.toISODateString(controls.startDate.value));
    return {
      id: this.course.id,
      name: controls.name.value,
      categoryId: controls.category.value.id,
      typeId: controls.type.value.id,
      description: controls.summary.value,
      instructor: controls.instructor.value.name,
      instructorNumber: this.instructors.find(instructor => instructor.name === this.classroomCourseFormGroup.controls.instructor.value.name).phoneNumber,
      maxEnrollmentCount: Number(controls.maxEnrollmentCount.value),
      state: this.course.state,
      previousState: this.course.previousState,
      attachment: {
        id: this.attachmentId ? this.attachmentId : this.course.attachment.id,
        name: this.attachmentName ? this.attachmentName : this.course.attachment.name,
        renderedName: this.attachmentRenderedName ? this.attachmentRenderedName : this.course.attachment.renderedName
      },
      surveyId: controls.survey.value ? (controls.survey.value.id ? controls.survey.value.id : null) : this.course.surveyId,
      certificateId: controls.certificate.value ? (controls.certificate.value.id ? controls.certificate.value.id : null) : this.course.certificateId,
      credit: Number(controls.credit.value),
      address: controls.address.value,
      startDate: startDate,
      startTime: controls.startHour.value + ':' + controls.startMin.value,
      endTime: controls.endHour.value + ':' + controls.endMin.value
    }
  }
}
