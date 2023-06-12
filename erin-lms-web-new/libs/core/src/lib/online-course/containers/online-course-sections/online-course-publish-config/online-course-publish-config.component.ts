import {Component, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {FormUtil} from "../../../../../../../shared/src/lib/utilities/form.util";
import {DateFormatter} from "../../../../../../../shared/src/lib/utilities/date-formatter.util";
import {OnlineCourseModel} from "../../../models/online-course.model";
import {DialogConfig} from "../../../../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../../../../shared/src/lib/dialog/dialog-ref";
import {OnlineCourseEnrollmentComponent} from "../online-course-enrollment/online-course-enrollment.component";
import {OnlineCourseSandboxService} from "../../../online-course-sandbox.service";
import {forkJoin} from "rxjs";

@Component({
  template: `
    <jrs-scroll [color]="'primary'" [height]="'70vh'">
      <jrs-online-course-enrollment #enrollmentSection [course]="course" (openInClicked)="openGroupEnrollment()"></jrs-online-course-enrollment>
      <div class="margin-top margin-left">
        <jrs-header-text [size]="'medium'" [margin]="false">НИЙТЛЭХ ТОХИРГОО</jrs-header-text>
      </div>
      <div class="row gx-1">
        <div class="col-media_s-12 col-media_sm-6 col-media_md-6 col-media_xl-4">
          <jrs-date-picker
            [formGroup]="publishFormGroup"
            [formType]="'publishDate'"
            [datePickerSize]="'standard'"
            [label]="'Нийтлэх өдөр'"
            [width]="'100%'">
          </jrs-date-picker>
        </div>
        <div class="col-media_s-12 col-media_sm-6 col-media_md-6 col-media_xl-2">
          <jrs-time-picker
            [formGroup]="publishFormGroup"
            [hourFormType]="'hour'"
            [minuteFormType]="'min'"
            [label]="'Нийтлэх цаг'"
            [size]="'long'">
          </jrs-time-picker>
        </div>
      </div>
      <div class="row gx-1 margin-bottom">
        <div class="col-media_s-12 col-media_sm-6 col-media_md-3 col-media_xl-3">
          <jrs-checkbox
            [padding]="false"
            [formGroup]="publishFormGroup"
            [formType]="'mail'"
            [text]="'Мэйл илгээх'">
          </jrs-checkbox>
        </div>
        <div class="col-media_s-12 col-media_sm-6 col-media_md-3 col-media_xl-3">
          <jrs-checkbox
            *ngIf="hasSms"
            [padding]="false"
            [formGroup]="publishFormGroup"
            [formType]="'sms'"
            [text]="'Mессеж илгээх'">
          </jrs-checkbox>
        </div>
      </div>
      <div class="row gx-1">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
          <jrs-text-area
            [size]="'medium'"
            [label]="'Тэмдэглэл'"
            [formGroup]="publishFormGroup"
            [formType]="'note'">
          </jrs-text-area>
        </div>
      </div>
    </jrs-scroll>
    <jrs-action-buttons [submitButton]="'Нийтлэх'" [declineButton]="'Болих'" (submitted)="publish()" (declined)="close()"></jrs-action-buttons>
    <jrs-page-loader [show]="saving"></jrs-page-loader>
  `,
  styles: []
})
export class OnlineCoursePublishConfigComponent {
  @ViewChild('enrollmentSection') enrollmentSection: OnlineCourseEnrollmentComponent;
  course: OnlineCourseModel;
  publishFormGroup: FormGroup;
  hasSms: boolean;

  saving = false;

  constructor(private sb: OnlineCourseSandboxService, private config: DialogConfig, public dialog: DialogRef) {
    this.course = config.data.course;
    this.hasSms = config.data.hasSms;
    this.setupPublishForm()
  }

  isFormValid(): boolean {
    return FormUtil.isFormValid(this.publishFormGroup);
  }

  close(): void {
    this.dialog.close();
  }

  publish(): void {
    if (FormUtil.isFormValid(this.publishFormGroup)) {
      const date = new Date(this.publishFormGroup.controls.publishDate.value);
      const hour: number = this.publishFormGroup.controls.hour.value;
      const minutes: number = this.publishFormGroup.controls.min.value;
      this.dialog.close({
        assignedLearners: this.enrollmentSection.getLearnerIds(),
        assignedGroups: this.course.assignedDepartmentNames,
        sendEmail: this.publishFormGroup.controls.mail.value,
        sendSms: this.publishFormGroup.controls.sms.value,
        publishDate: isNaN(date.getDate()) ? null : new Date(date.getFullYear(), date.getMonth(), date.getDate(),
          hour, minutes),
      });
    }
  }

  openGroupEnrollment(): void {
    const saveOrder = []
    this.saving = true;
    forkJoin(this.enrollmentSection.saveEnrollment(saveOrder)).subscribe(() => {
      this.saving = false;
      this.dialog.close();
      this.sb.navigateByUrl(`/online-course/update/${this.course.id}/group-enrollment`);
    }, () => {
      this.saving = false;
    })
  }

  private setupPublishForm(): void {
    const hour = this.course.publishDate ? DateFormatter.zeroDigitPlace(this.course.publishDate.getHours()) : '00';
    const min = this.course.publishDate ? DateFormatter.zeroDigitPlace(this.course.publishDate.getMinutes()) : '00';
    const date = this.course.publishDate ? new Date(this.course.publishDate) : new Date();
    this.publishFormGroup = new FormGroup({
      mail: new FormControl(false, [Validators.required]),
      sms: new FormControl(false, [Validators.required]),
      note: new FormControl(),
      publishDate: new FormControl(date, [Validators.required]),
      hour: new FormControl(hour, [Validators.required]),
      min: new FormControl(min, [Validators.required])
    });
  }
}
