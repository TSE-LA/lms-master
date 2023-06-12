import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {CategoryItem, TimedCourseModel} from "../../../../../../../shared/src/lib/shared-model";
import {FormUtil} from "../../../../../../../shared/src/lib/utilities/form.util";
import {TimedCourseSandboxService} from "../../../services/timed-course-sandbox.service";
import {DateFormatter} from "../../../../../../../shared/src/lib/utilities/date-formatter.util";
import {catchError, map} from "rxjs/operators";
import {throwError} from "rxjs";

@Component({
  selector: 'jrs-timed-course-info',
  template: `
    <div *ngIf="!load">
      <jrs-input-field
        [formGroup]="timedCourseFormGroup"
        [formType]="'name'"
        [placeholder]="'Урамшууллын нэр'"
        [selectedType]="'text'"
        [movePlaceholder]="true"
        [required]="true">
      </jrs-input-field>
      <div class="row gx-1">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6">
          <jrs-dropdown-view
            [formGroup]="timedCourseFormGroup"
            [formType]="'category'"
            [placeholder]="'Үйлчилгээний төрөл'"
            [padding]="true"
            [outlined]="true"
            [values]="categories">
          </jrs-dropdown-view>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6">
          <jrs-dropdown-view
            [formGroup]="timedCourseFormGroup"
            [formType]="'target'"
            [placeholder]="'Зорилтот хэрэглэгч'"
            [padding]="true"
            [outlined]="true"
            [values]="targets">
          </jrs-dropdown-view>
        </div>
      </div>

      <div class="row gx-1 ">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
          <jrs-dropdown-view
            [formGroup]="timedCourseFormGroup"
            [formType]="'type'"
            [placeholder]="'Төрөл'"
            [padding]="true"
            [icon]="'expand_more'"
            [outlined]="true"
            [values]="courseTypes"
            (selectedValue)="typeChange($event)">
          </jrs-dropdown-view>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
          <jrs-input-field
            [formGroup]="timedCourseFormGroup"
            [formType]="'code'"
            [placeholder]="'Нөхцөлийн код'"
            [selectedType]="'text'"
            [movePlaceholder]="true"
            [required]="true">
          </jrs-input-field>
        </div>
      </div>

      <div class="row gx-1">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
          <jrs-input-field
            [formGroup]="timedCourseFormGroup"
            [formType]="'keyword'"
            [placeholder]="'Түлхүүр үг'"
            [selectedType]="'text'"
            [movePlaceholder]="true"
            [required]="true">
          </jrs-input-field>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-3 col-media_xl-3">
          <jrs-date-picker
            [formGroup]="timedCourseFormGroup"
            [formType]="'startDate'"
            [datePickerSize]="'standard'"
            [margin]="false"
            [width]="'100%'">
          </jrs-date-picker>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-3 col-media_xl-3">
          <jrs-date-picker
            *ngIf="hasEndDate"
            [formGroup]="timedCourseFormGroup"
            [formType]="'endDate'"
            [margin]="false"
            [datePickerSize]="'standard'"
            [width]="'100%'">
          </jrs-date-picker>
        </div>
      </div>
      <div class="row gx-1" *ngIf="!published">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
          <jrs-checkbox
            [check]="hasTest"
            [padding]="false"
            (checked)="changeCheck($event)"
            [text]="'Сорилтой эсэх?'">
          </jrs-checkbox>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
          <jrs-checkbox
            [formGroup]="timedCourseFormGroup"
            [formType]="'feedback'"
            [padding]="false"
            [text]="'Санал асуулгатай эсэх?'">
          </jrs-checkbox>
        </div>
      </div>
      <div class="row gx-1 margin-top">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
          <jrs-text-area
            [placeholder]="'Товч утга'"
            [size]="'medium'"
            [formGroup]="timedCourseFormGroup"
            [formType]="'summary'">
          </jrs-text-area>
        </div>
      </div>
      <div class="row gx-1 margin-top" *ngIf="published">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
          <jrs-text-area
            [placeholder]="'Тэмдэглэл'"
            [size]="'medium'"
            [formGroup]="timedCourseFormGroup"
            [formType]="'note'">
          </jrs-text-area>
        </div>
      </div>
    </div>

    <jrs-skeleton-loader [amount]="5" [load]="load"></jrs-skeleton-loader>
  `,
  styles: []
})
export class TimedCourseInfoComponent implements OnChanges {
  @Input() course: TimedCourseModel;
  @Input() published = false;
  @Input() load = false;
  @Output() testChange = new EventEmitter<boolean>();
  timedCourseFormGroup: FormGroup;
  categories: CategoryItem[] = []
  targets = this.sb.getTargets();
  courseTypes = this.sb.getCourseTypes();
  hasEndDate: boolean;
  hasTest = false;

  constructor(private sb: TimedCourseSandboxService) {
    this.setForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "course" && this.course) {
        this.loadData();
      }
    }
  }

  saveInfo(order: any[]): any[] {
    const valid = FormUtil.isFormValid(this.timedCourseFormGroup);
    if (valid) {
      const mappedCourse = this.mapFormToCourse();
      order.push(this.sb.updateTimedCourse(mappedCourse).pipe(map(() => {
        this.course = Object.assign(mappedCourse);
      }), catchError(err => {
        this.sb.openSnackbar("Урамшуулал хадгалахад алдаа гарлаа.");
        return throwError(err);
      })));
    }
    return order;
  }

  isDataChanged(): boolean {
    return JSON.stringify(this.course) !== JSON.stringify(this.mapFormToCourse());
  }

  changeCheck(check: boolean): void {
    this.hasTest = check;
    this.testChange.emit(this.hasTest);
  }

  typeChange(value): void {
    this.sb.setSelectedType(value);
    this.hasEndDate = this.timedCourseFormGroup.controls.type.value.id !== "MAIN";
  }


  private setForm(): void {
    this.timedCourseFormGroup = new FormGroup({
      name: new FormControl('', [Validators.required]),
      category: new FormControl('', [Validators.required]),
      target: new FormControl('', [Validators.required]),
      type: new FormControl('', [Validators.required]),
      startDate: new FormControl('', [Validators.required]),
      endDate: new FormControl('', [Validators.required]),
      code: new FormControl('', [Validators.required]),
      keyword: new FormControl('', [Validators.required]),
      test: new FormControl(false, [Validators.required]),
      feedback: new FormControl(false),
      summary: new FormControl(''),
      note: new FormControl('')
    })
  }

  private loadData(): void {
    this.hasTest = this.course.hasTest;
    this.testChange.emit(this.hasTest);
    if (this.course.state !== "MAIN") {
      this.hasEndDate = true;
    }

    this.sb.getTimedCourseCategories().subscribe(res => {
      this.categories = res;
      this.assignFormValues();
    }, () => {
      this.sb.openSnackbar("Урамшууллын ангилал ачааллахад алдаа гарлаа!");
    });
  }

  private assignFormValues(): void {
    this.timedCourseFormGroup.controls.name.setValue(this.course.name);
    const courseCategory = this.categories.find(category => category.id == this.course.categoryId);
    this.sb.setSelectedCategory(courseCategory);
    this.timedCourseFormGroup.controls.category.setValue(courseCategory);
    const target = this.targets.find(target => target.name == this.course.target);
    this.timedCourseFormGroup.controls.target.setValue(target);
    const type = this.courseTypes.find(type => type.id == this.course.state);
    this.timedCourseFormGroup.controls.type.setValue(type);
    this.timedCourseFormGroup.controls.startDate.setValue(DateFormatter.isoToDatePickerStringValue(this.course.startDate));
    if (this.course.endDate) {
      this.timedCourseFormGroup.controls.endDate.setValue(this.course.endDate);
    } else {
      this.timedCourseFormGroup.controls.endDate.setValue(this.course.startDate);
    }
    this.timedCourseFormGroup.controls.code.setValue(this.course.code);
    this.timedCourseFormGroup.controls.keyword.setValue(this.course.keyword);
    this.timedCourseFormGroup.controls.summary.setValue(this.course.description);
    this.timedCourseFormGroup.controls.test.setValue(this.course.hasTest);
    this.timedCourseFormGroup.controls.feedback.setValue(this.course.hasFeedBack);
    this.timedCourseFormGroup.controls.note.setValue(this.course.note);
  }

  private mapFormToCourse(): TimedCourseModel {
    const controls = this.timedCourseFormGroup.controls;
    const state = controls.type.value ? (controls.type.value.id ? controls.type.value.id : null) : this.course.state;
    return {
      id: this.course.id,
      name: controls.name.value,
      author: this.course.author,
      categoryId: controls.category.value ? (controls.category.value.id ? controls.category.value.id : null) : this.course.categoryId,
      contentId: this.course.contentId,
      description: controls.summary.value,
      endDate: state !== "MAIN" ? DateFormatter.toISODateString(controls.endDate.value) : null,
      startDate: DateFormatter.toISODateString(controls.startDate.value),
      keyword: controls.keyword.value,
      state: controls.type.value ? (controls.type.value.id ? controls.type.value.id : null) : this.course.state,
      code: controls.code.value,
      target: controls.target.value ? (controls.target.value.id ? controls.target.value.id : null) : this.course.target,
      hasTest: this.hasTest,
      hasFeedBack: controls.feedback.value,
      enrollments: this.course.enrollments,
      status: this.course.status,
      publishStatus: this.course.publishStatus,
      publishDate: this.course.publishDate,
      modifiedDate: this.course.modifiedDate,
      note: controls.note.value,
      department: this.course.department
    }
  }
}
