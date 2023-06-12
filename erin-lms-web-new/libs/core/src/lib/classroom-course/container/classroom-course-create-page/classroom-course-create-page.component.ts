import {Component, OnInit, ViewChild} from "@angular/core";
import {catchError, map} from "rxjs/operators";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ClassroomCourseSandboxService} from "../../classroom-course-sandbox.service";
import {CreateClassroomCourseModel, InstructorDropdownModel} from "../../model/classroom-course.model";
import {CertificateModel} from "../../../certificate/model/certificate.model";
import {Survey, SurveyStatus} from "../../../common/common.model";
import {DateItemsComponent} from "../../component/date-and-time/date-items.component";
import {forkJoin, Observable, throwError} from "rxjs";
import {CategoryItem} from "../../../../../../shared/src/lib/shared-model";
import {FormUtil} from "../../../../../../shared/src/lib/utilities/form.util";

@Component({
  template: `
    <jrs-button class="create-page-sidenav-content"
                [iconName]="'arrow_back_ios'"
                [iconColor]="'secondary'"
                [noOutline]="true"
                [isMaterial]="true"
                [size]="'medium'"
                [bold]="true"
                [textColor]="'text-link'"
                (clicked)="goBack()">БУЦАХ
    </jrs-button>
    <jrs-section>
      <jrs-header-text [size]="'medium'">СУРГАЛТЫН ТӨЛӨВЛӨГӨӨ</jrs-header-text>
      <form [formGroup]="classroomCourseFormGroup">
        <div class="row gx-5">
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
                  [values]="categoryItems"
                  (selectedValue)="changeCategory($event)">
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
                  [values]="typeOptions">
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
                  [size]="'medium'"
                  [placeholder]="'Багц цаг'"
                  [required]="false"
                  [selectedType]="'number'">
                </jrs-input-field>
              </div>
            </div>

            <jrs-text-area
              [placeholder]="'Товч утга'"
              [size]="'medium'"
              [formGroup]="classroomCourseFormGroup"
              [formType]="'description'">
            </jrs-text-area>
          </div>

          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6">
            <jrs-input-field
              [formGroup]="classroomCourseFormGroup"
              [formType]="'address'"
              [placeholder]="'Байршил'"
              [selectedType]="'text'"
              [movePlaceholder]="true"
              [padding]="false"
              [required]="true">
            </jrs-input-field>

            <jrs-scroll [height]="'250px'" [color]="'primary'" [size]="'small'">
              <date-items #dateItemsComponent>
              </date-items>
            </jrs-scroll>
          </div>
        </div>
      </form>
    </jrs-section>


    <div class="flex-center margin-top margin-bottom-large">
      <jrs-button
        [title]="'ҮҮСГЭХ'"
        [size]="'medium'"
        [load]="loading"
        (clicked)="submitClassroomCourse()">
      </jrs-button>
    </div>
  `
})
export class ClassroomCourseCreatePageComponent implements OnInit {
  @ViewChild('dateItemsComponent') dateItemsComponent: DateItemsComponent;
  classroomCourseFormGroup: FormGroup;
  categoryItems: CategoryItem[] = [];
  instructors: InstructorDropdownModel[] = [];
  currentUser = this.sb.username;
  dropdownLoader: boolean;
  typeOptions = [];
  certificates: CertificateModel[] = [];
  surveyTemplates: Survey[] = [];
  selectedCategory: CategoryItem;
  attachment: File;
  attachmentId: string;
  attachmentPath: string;
  attachmentRenderedName: string;
  loading = false;
  load = false;
  error: boolean;
  selectedSurvey: Survey = {
    id: '',
    name: '',
    questionCount: 0,
    status: SurveyStatus.INACTIVE,
    createdDate: '',
    modifiedDate: '',
    author: '',
    description: ''
  };
  private attachmentMimeType: string;

  constructor(private sb: ClassroomCourseSandboxService) {
  }

  ngOnInit() {
    this.load = true;
    this.typeOptions = this.sb.constants.COURSE_TYPES;
    this.setFormGroup();
    const tasks$ = [];
    tasks$.push(this.getAllCategories());
    tasks$.push(this.getAllInstructors());
    tasks$.push(this.getAllSurveyTemplates());
    tasks$.push(this.getAllCertificates());
    forkJoin(...tasks$).subscribe(() => {
      this.load = false;
    }, () => {
      this.load = false;
      this.sb.openSnackbar('Танхимын сургалт үүсгэх хуудас ачаллахад алдаа гарлаа');
    });
  }

  goBack(): void {
    this.sb.goBack();
  }

  uploadFile(file: File): void {
    this.attachment = file;
    this.attachmentMimeType = file.type;
    this.sb.uploadAttachment(this.attachment).subscribe((res: any) => {
      if (res) {
        this.attachmentId = res.fileId;
        this.attachmentPath = res.path;
        this.attachmentRenderedName = res.name;
      }
    });
  }

  isDateValid(): boolean {
    if (this.dateItemsComponent != null) {
      for (const date of this.dateItemsComponent.getDateItems()) {
        if (date.date == null || date.startHour == null || date.startHour != date.startHour || date.startMinute == null
          || date.startMinute != date.startMinute || date.endHour == null || date.endHour != date.endHour
          || date.endMinute == null || date.endMinute != date.endMinute) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  submitClassroomCourse(): void {
    this.dateItemsComponent.checkError();
    if (!FormUtil.isFormValid(this.classroomCourseFormGroup) || !this.isDateValid()) {
      return;
    }
    this.loading = true;
    this.sb.createClassroomCourse(this.getClassroomCourse(), this.attachmentMimeType).subscribe(() => {
      this.loading = false;
      this.sb.openSnackbar('Танхимын сургалт амжилттай үүсгэлээ', true);
      this.navigateToCalendar();
    }, () => {
      this.loading = false;
      this.sb.openSnackbar('Танхимын сургалт үүсгэхэд алдаа гарлаа');
    });
  };

  navigateToCalendar(): void {
    this.sb.navigateByUrl('/classroom-course')
  }

  changeCategory(category: CategoryItem): void {
    this.selectedCategory = category;
  }

  private setFormGroup(): void {
    this.classroomCourseFormGroup = new FormGroup({
      name: new FormControl('', [Validators.required]),
      category: new FormControl('', [Validators.required]),
      instructor: new FormControl('', [Validators.required]),
      maxEnrollmentCount: new FormControl('', [Validators.required]),
      type: new FormControl('', [Validators.required]),
      file: new FormControl(''),
      fileName: new FormControl(''),
      description: new FormControl(''),
      address: new FormControl(''),
      survey: new FormControl(''),
      certificate: new FormControl(''),
      credit: new FormControl()
    });
  }

  private getAllCertificates(): Observable<any> {
    return this.sb.getCertificates().pipe(map((cetificates) => {
      this.certificates = cetificates;
      return cetificates;
    }))
  }

  private getAllCategories(): Observable<any> {
    return this.sb.getClassroomCourseCategories().pipe(map((categories) => {
      if (categories) {
        this.categoryItems = categories;
        this.selectedCategory = this.categoryItems[0];
        this.classroomCourseFormGroup.controls.category.setValue(this.selectedCategory.name);
        this.load = false;
      } else {
        this.categoryItems = [];
      }
      return categories;
    }), catchError(error => {
      this.dropdownLoader = false;
      return throwError(error)
    }));
  }


  private getAllInstructors(): Observable<any> {
    return this.sb.getInstructors().pipe(map((instructors) => {
      this.classroomCourseFormGroup.controls.instructor.setValue({name: this.currentUser, id: this.currentUser});
      this.instructors = instructors;
    }));
  }

  private getAllSurveyTemplates(): Observable<any> {
    return this.sb.getSurveys().pipe(map((surveys: Survey[]) => {
      this.surveyTemplates = surveys;
      this.selectedSurvey = this.surveyTemplates[0];
      return surveys;
    }));
  }

  private isFormValid(): boolean {
    let invalids = 0;
    for (const [key] of Object.entries(this.classroomCourseFormGroup.controls)) {
      if (this.classroomCourseFormGroup.controls[key].invalid) {
        invalids++;
        this.classroomCourseFormGroup.controls[key].markAsDirty();
      }
    }
    return invalids == 0;
  }

  private getClassroomCourse(): CreateClassroomCourseModel {
    return {
      name: this.classroomCourseFormGroup.controls.name.value,
      categoryId: this.selectedCategory.id,
      type: this.classroomCourseFormGroup.controls.type.value.id,
      address: this.classroomCourseFormGroup.controls.address.value,
      instructor: this.classroomCourseFormGroup.controls.instructor.value.name,
      instructorNumber: this.instructors.find(admin => admin.name === this.classroomCourseFormGroup.controls.instructor.value.name).phoneNumber,
      attachedFileId: this.attachmentId,
      attachFileName: this.attachmentPath,
      attachFileRenderedName: this.attachmentRenderedName,
      maxEnrollmentCount: this.classroomCourseFormGroup.controls.maxEnrollmentCount.value,
      surveyId: this.classroomCourseFormGroup.controls.survey.value.id,
      certificateId: this.classroomCourseFormGroup.controls.certificate.value.id,
      credit: this.classroomCourseFormGroup.controls.credit.value,
      description: this.classroomCourseFormGroup.controls.description.value,
      dateItems: this.dateItemsComponent.getDateItems(),
    };
  }
}
