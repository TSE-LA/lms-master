import {Component, OnInit, ViewChild} from '@angular/core';
import {TimedCourseSandboxService} from "../../services/timed-course-sandbox.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DateItemsComponent} from "../../../classroom-course/component/date-and-time/date-items.component";
import {CategoryItem, TimedCourseModel} from "../../../../../../shared/src/lib/shared-model";
import {TARGET_OPTIONS} from "../../models/timed-course.constant";
import {DateFormatter} from "../../../../../../shared/src/lib/utilities/date-formatter.util";
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import {FormUtil} from "../../../../../../shared/src/lib/utilities/form.util";

@Component({
  selector: 'jrs-timed-course-create-page',
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
    <jrs-section>
      <jrs-header-text [size]="'medium'">{{CREATE_TIMED_COURSE_HEADER}}</jrs-header-text>
      <ng-container *ngIf="!loading">
        <form [formGroup]="timedCourseFormGroup">
          <div class="row gx-5 margin-top">
            <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12 margin-top">
              <jrs-input-field
                [formGroup]="timedCourseFormGroup"
                [formType]="'name'"
                [placeholder]="'Сургалтын нэр'"
                [selectedType]="'text'"
                [movePlaceholder]="true"
                [required]="true">
              </jrs-input-field>

              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                  <jrs-dropdown-view
                    [formGroup]="timedCourseFormGroup"
                    [formType]="'category'"
                    [placeholder]="'Үйлчилгээний төрөл'"
                    [padding]="true"
                    [outlined]="true"
                    [values]="categoryItems"
                    (selectedValue)="selectCategory($event)">
                  </jrs-dropdown-view>
                </div>
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                  <jrs-dropdown-view
                    [formGroup]="timedCourseFormGroup"
                    [formType]="'target'"
                    [chooseFirst]="true"
                    [placeholder]="'Зорилтот хэрэглэгч'"
                    [padding]="true"
                    [outlined]="true"
                    [values]="TARGET_MASS_OPTIONS"
                    (selectedValue)="selectTarget($event)">
                  </jrs-dropdown-view>
                </div>
              </div>

              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                  <jrs-dropdown-view
                    [formGroup]="timedCourseFormGroup"
                    [formType]="'state'"
                    [placeholder]="'Төрөл'"
                    [padding]="true"
                    [outlined]="true"
                    [values]="TIMED_COURSE_STATE"
                    (selectedValue)="selectState($event)">
                  </jrs-dropdown-view>
                </div>
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                  <jrs-input-field
                    [formGroup]="timedCourseFormGroup"
                    [formType]="'code'"
                    [required]="true"
                    [placeholder]="'Нөхцөлийн код'"
                    [movePlaceholder]="true">
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
                <div class="col-media_s-6 col-media_sm-6 col-media_md-3 col-media_xl-3">
                  <jrs-date-picker
                    [padding]="false"
                    [formGroup]="timedCourseFormGroup"
                    [error]="!areDatesValid()"
                    [errorText]="'Эхлэх огноо зөв оруулна уу'"
                    [margin]="false"
                    [formType]="'startDate'"
                    [width]="'100%'">
                  </jrs-date-picker>
                  <div *ngIf="!areDatesValid()" class="date-error">Огноо буруу байна дахин нягтална уу</div>
                </div>
                <div class="col-media_s-6 col-media_sm-6 col-media_md-3 col-media_xl-3">
                  <jrs-date-picker
                    *ngIf="this.selectedState !== TIMED_COURSE_STATE[0]"
                    [formGroup]="timedCourseFormGroup"
                    [errorText]="'Дуусах огноо зөв оруулна уу'"
                    [error]="!areDatesValid()"
                    [margin]="false"
                    [formType]="'endDate'"
                    [width]="'100%'">
                  </jrs-date-picker>
                </div>
              </div>

              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                  <jrs-text-area
                    [placeholder]="'Товч утга'"
                    [size]="'medium'"
                    [formGroup]="timedCourseFormGroup"
                    [formType]="'description'">
                  </jrs-text-area>
                </div>
              </div>
            </div>
          </div>
        </form>
      </ng-container>
    </jrs-section>
    <div class="flex-center margin-top margin-bottom-large">
      <jrs-button
        class="side-margin"
        [title]="'ҮҮСГЭХ'"
        [color]="'primary'"
        [size]="'medium'"
        [float]="'center'"
        [load]="loading || saving"
        (clicked)="createCourse(false)">
      </jrs-button>
      <jrs-button
        class="side-margin center"
        [title]="'ҮРГЭЛЖЛҮҮЛЭХ'"
        [color]="'primary'"
        [size]="'medium'"
        [load]="loading || saving"
        (clicked)="createCourse(true)">
      </jrs-button>
    </div>
  `,
  styleUrls: ['./timed-course-create-page.component.scss']
})
export class TimedCourseCreatePageComponent implements OnInit {
  @ViewChild('dateItemsComponent') dateItemsComponent: DateItemsComponent;
  timedCourseFormGroup: FormGroup;
  loading: boolean;
  categoryItems: CategoryItem[] = [];
  selectedCategory: CategoryItem;
  selectedTarget: DropdownModel;
  selectedState: DropdownModel;
  disabled = false;
  saving = false;
  CREATE_TIMED_COURSE_HEADER = this.sb.constants.CREATE_TIMED_COURSE;
  TARGET_MASS_OPTIONS = TARGET_OPTIONS;
  TIMED_COURSE_STATE = this.sb.constants.TIMED_COURSE_STATE ? this.sb.constants.TIMED_COURSE_STATE : [];

  constructor(private sb: TimedCourseSandboxService) {
  }

  ngOnInit(): void {
    this.setForm();
    this.sb.getTimedCourseCategories().subscribe(res => {
      this.categoryItems = res;
      this.selectedCategory = this.sb.getSelectedCategory();
      this.timedCourseFormGroup.controls.category.setValue(this.selectedCategory);
    })
    this.selectedTarget = this.TARGET_MASS_OPTIONS[0];
    this.checkDateState();
  }

  goBack(): void {
    this.sb.navigateByUrl('/timed-course/container');
  }

  selectCategory(category: any): void {
    this.selectedCategory = category
    this.sb.setSelectedCategory(this.selectedCategory);
  }

  selectTarget(directMass: any): void {
    this.selectedTarget = directMass;
  }

  selectState(state: DropdownModel): void {
    this.selectedState = state;
    this.sb.setSelectedType(this.selectedState)
  }

  createCourse(saveAndContinue: boolean): void {
    if (FormUtil.isFormValid(this.timedCourseFormGroup) && this.areDatesValid()) {
      this.saving = true;
      this.sb.createTimedCourse(this.getTimedCourseModel()).subscribe((id) => {
        this.sb.setSelectedCategory(this.selectedCategory);
        this.saving = false;
        this.sb.openSnackbar("Урамшуулал амжилттай үүсгэлээ", true);
        if (saveAndContinue) {
          this.sb.navigateByUrl('/timed-course/update/' + id.entity.id);
        } else {
          this.goBack()
        }
      }, () => {
        this.saving = false;
        this.sb.openSnackbar("Урамшуулал үүсгэхэд алдаа гарлаа");
      })
    }
  }

  areDatesValid(): boolean {
    const state = this.timedCourseFormGroup.controls.state.value;
    const startDate = new Date(this.timedCourseFormGroup.controls.startDate.value);
    const endDate = new Date(this.timedCourseFormGroup.controls.endDate.value);
    const endDateValidator = new Date();
    endDateValidator.setHours(0, 0, 0, 0);
    if (state === this.TIMED_COURSE_STATE[1] || state === this.TIMED_COURSE_STATE[2]) {
      return !(startDate > endDate)
    } else {
      return true;
    }
  }

  private setForm(): void {
    this.selectedState = this.sb.getSelectedType() ? this.sb.getSelectedType() : this.TIMED_COURSE_STATE[0];
    this.timedCourseFormGroup = new FormGroup({
      name: new FormControl('', [Validators.required]),
      category: new FormControl('', [Validators.required]),
      target: new FormControl('', [Validators.required]),
      keyword: new FormControl('', [Validators.required]),
      state: new FormControl(this.selectedState, [Validators.required]),
      code: new FormControl('', [Validators.required]),
      description: new FormControl(''),
      startDate: new FormControl(DateFormatter.dateFormat(new Date, '-'), [Validators.required]),
      endDate: new FormControl(DateFormatter.dateFormat(new Date, '-'), [Validators.required]),
    })
  }

  private getTimedCourseModel(): TimedCourseModel {
    return {
      id: '',
      name: this.timedCourseFormGroup.controls.name.value.trim(),
      categoryId: this.selectedCategory.id,
      target: this.selectedTarget.id,
      state: this.selectedState.id,
      code: this.timedCourseFormGroup.controls.code.value.trim(),
      keyword: '[' + this.timedCourseFormGroup.controls.keyword.value.trim() + ']',
      startDate: this.timedCourseFormGroup.controls.startDate.value,
      endDate: this.selectedState == this.TIMED_COURSE_STATE[0] ? '-' : this.timedCourseFormGroup.controls.endDate.value,
      description: this.timedCourseFormGroup.controls.description.value
    }
  }

  private checkDateState(): void {
    const state = this.timedCourseFormGroup.controls.state.value;
    this.timedCourseFormGroup.controls.startDate.enable();
    if (state.name == this.TIMED_COURSE_STATE[0].name) {
      this.disabled = true;
      return;
    }
    this.disabled = false;
  }
}
