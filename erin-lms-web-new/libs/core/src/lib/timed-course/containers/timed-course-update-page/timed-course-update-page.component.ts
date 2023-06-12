import {Component, ViewChild} from '@angular/core';
import {TimedCourseSandboxService} from "../../services/timed-course-sandbox.service";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {ActivatedRoute} from "@angular/router";
import {PublishStatus, TimedCourseModel} from "../../../../../../shared/src/lib/shared-model";
import {TimedCourseInfoComponent} from "../timed-course-sections/timed-course-info/timed-course-info.component";
import {TimedCourseContentStructureComponent} from "../timed-course-sections/timed-course-content-structure/timed-course-content-structure.component";
import {TimedCourseTestComponent} from "../timed-course-sections/timed-course-test/timed-course-test.component";
import {TimedCourseEnrollmentComponent} from "../timed-course-sections/timed-course-enrollment/timed-course-enrollment.component";
import {TimedCourseAttachmentsComponent} from "../timed-course-sections/timed-course-attachments/timed-course-attachments.component";
import {DateFormatter} from "../../../../../../shared/src/lib/utilities/date-formatter.util";
import {forkJoin, Observable, of} from "rxjs";
import {PublishingModel} from "../../../online-course/models/online-course.model";
import {TimedCoursePublishConfigComponent} from "../timed-course-sections/timed-course-publish-config/timed-course-publish-config.component";
import {FileAttachment} from "../../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {ConfirmCheckboxDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-checkbox-dialog/confirm-checkbox-dialog.component";

@Component({
  selector: 'jrs-timed-course-update-page',
  template: `
    <jrs-button
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
      <jrs-header-text [size]="'medium'">УРАМШУУЛЛЫН МЭДЭЭЛЭЛ</jrs-header-text>
      <div class="margin-left-double">
        <jrs-info-panel *ngIf="pending" [text]="publishDate + ' цагт ирээдүйд нийтлэхээр тохируулсан байна.'" [width]="'94%'"></jrs-info-panel>
      </div>
      <div class="row gx-5 margin-top">
        <div [ngClass]="!published? 'col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6':
                                    'col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12'">
          <jrs-timed-course-info #infoSection [course]="course" [published]="published" [load]="load" (testChange)="changeTest($event)"></jrs-timed-course-info>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6">
          <jrs-timed-course-content-structure *ngIf="!published" #structureSection
                                              [course]="course"
                                              (attachmentChange)="assignAttachment($event)"
                                              [changedAttachment]="changedAttachment">
          </jrs-timed-course-content-structure>
        </div>
      </div>
    </jrs-section>

    <jrs-timed-course-attachments *ngIf="!published"
                                  #attachmentSection
                                  [course]="course"
                                  (attachmentChange)="changeAttachment($event)"
                                  [attachments]="attachments"></jrs-timed-course-attachments>
    <jrs-timed-course-test *ngIf="!published" #testSection [course]="course" [hasTest]="hasTest"></jrs-timed-course-test>
    <jrs-section *ngIf="published">
      <jrs-timed-course-enrollment #enrollmentSection [course]="course"
                                   (openInClicked)="saveAndNavigateToGroupEnrollmentPage()">
      </jrs-timed-course-enrollment>
    </jrs-section>
    <div class="flex-center margin-top margin-bottom-large">
      <jrs-button
        class="side-margin"
        [title]="'ХАДГАЛАХ'"
        [color]="'primary'"
        [size]="'medium'"
        [float]="'center'"
        [load]="saving || publishing"
        (clicked)="save()">
      </jrs-button>
      <jrs-button
        *ngIf="!published"
        class="side-margin margin-left"
        [title]="'НИЙТЛЭХ'"
        [color]="'primary'"
        [size]="'medium'"
        [float]="'center'"
        [load]="saving || publishing"
        (clicked)="publish()">
      </jrs-button>
    </div>
    <jrs-page-loader [show]="saving || publishing" [text]="saving? 'Хадгалж байна...': 'Нийтэлж байна...'"></jrs-page-loader>
  `,
  styles: []
})
export class TimedCourseUpdatePageComponent {
  @ViewChild('infoSection') infoSection: TimedCourseInfoComponent;
  @ViewChild('structureSection') structureSection: TimedCourseContentStructureComponent;
  @ViewChild('attachmentSection') attachmentSection: TimedCourseAttachmentsComponent;
  @ViewChild('testSection') testSection: TimedCourseTestComponent;
  @ViewChild('enrollmentSection') enrollmentSection: TimedCourseEnrollmentComponent;
  courseId: string;
  course: TimedCourseModel;
  attachments: FileAttachment[] = [];
  changedAttachment: FileAttachment[];
  published: boolean;
  publishDate: string;
  pending: boolean;
  hasTest = false;
  saving: boolean;
  publishing: boolean;
  load = false;
  SEND_SMS = 'user.promotion.sendSms';
  private saveOrder: any[] = [];

  constructor(private sb: TimedCourseSandboxService, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      this.courseId = params.get('id');
      this.loadCourse();
    })
  }

  goBack(): void {
    const infoChanged = this.infoSection.isDataChanged();
    const structureChanged = this.structureSection && this.structureSection.isDataChanged();
    const testChange = this.testSection && this.testSection.isDataChanged();
    const enrollSection = this.enrollmentSection && this.enrollmentSection.isDataChanged();
    if (infoChanged || structureChanged || testChange || enrollSection) {
      const config = new DialogConfig();
      config.outsideClick = false;
      config.title = "Мэдээлэл хадгалахгүй шууд гарах уу?"
      config.data = {
        info: "'Тийм' гэж дарвал мэдээлэл хадгалагдахгүй болохыг анхаарна уу."
      }
      this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
        if (res) {
          this.navigateBack();
        }
      });
    } else {
      this.navigateBack();
    }
  }

  assignAttachment($event: FileAttachment[]): void {
    this.attachments = $event;
  }

  changeAttachment($event: FileAttachment[]): void {
    this.changedAttachment = $event;
  }

  changeTest($event: boolean): void {
    this.hasTest = $event;
  }

  save(): void {
    const save = this.saveCourse();
    if (this.saveOrder.length > 0) {
      const config = new DialogConfig();
      config.submitButton = 'Үргэлжлүүлэх';
      config.declineButton = 'Болих';
      config.title = 'Мэдэгдэл илгээх'
      config.data = {hasUnited: true, hasSms: this.sb.getPermission(this.SEND_SMS)};
      this.sb.openDialog(ConfirmCheckboxDialogComponent, config).afterClosed.subscribe(res => {
        if (res) {
          this.saving = true;
          save.subscribe(() => {
            this.saving = false;
            if (res.send) {
              this.sb.sendUpdateNotification(this.course.code, this.course.name, this.course.id).subscribe(() => {
                },
                () => this.sb.openSnackbar('Смс, имэйл илгээхэд алдаа гарлаа!'));
            }
            this.navigateBack();
          }, () => {
            this.saving = false;
          });
        }
      })
    } else {
      this.sb.openSnackbar('Өөрчлөлт оруулаагүй байна!', true);
    }
  }

  publish(): void {
    const save = this.saveCourse();
    if (this.structureSection.isPublishReady() && this.testSection.isPublishReady()) {
      if (this.saveOrder.length > 0) {
        this.saving = true;
        save.subscribe(() => {
          this.saving = false;
          this.publishCourse();
        }, () => {
          this.saving = false;
        })
      } else {
        this.publishCourse();
      }
    } else {
      this.sb.openSnackbar('Алдаатай байна, засаад дахин нийтэлнэ үү!')
    }
  }

  saveAndNavigateToGroupEnrollmentPage(): void {
    const save = this.saveCourse();
    if (this.saveOrder.length > 0) {
      this.saving = true;
      save.subscribe(() => {
        this.saving = false;
        this.sb.navigateByUrl(`/timed-course/update/${this.course.id}/group-enrollment`);
      }, () => {
        this.saving = false;
        this.sb.openSnackbar('Урамшууллын мэдээлэл хадгалахад алдаа гарлаа!')
      })
    } else {
      this.sb.navigateByUrl(`/timed-course/update/${this.course.id}/group-enrollment`);
    }

  }

  private navigateBack(): void {
    this.sb.navigateByUrl('/timed-course/container');
  }

  private saveCourse(): Observable<any> {
    this.saveOrder = [];
    if (this.infoSection.isDataChanged()) {
      this.saveOrder = this.infoSection.saveInfo(this.saveOrder);
    }
    if (this.structureSection && this.structureSection.isDataChanged()) {
      this.saveOrder = this.structureSection.saveStructure(this.saveOrder);
    }
    if (this.testSection && this.testSection.isDataChanged() && !this.testSection.hasErrors()) {
      this.saveOrder = this.testSection.saveTest(this.saveOrder);
    }
    if (this.testSection && this.testSection.hasErrors()) {
      this.saveOrder = [];
      this.sb.openSnackbar("Алдаатай байна засаад дахин оролдоно уу!");
      return of();
    }
    if (this.enrollmentSection && this.enrollmentSection.isDataChanged()) {
      this.saveOrder = this.enrollmentSection.saveEnrollment(this.saveOrder);
    }
    return forkJoin(this.saveOrder);
  }

  private loadCourse(): void {
    this.load = true;
    this.sb.getTimedCourseById(this.courseId).subscribe(res => {
        this.course = res;
        if (this.course.publishStatus == PublishStatus.PENDING) {
          this.pending = true;
          this.publishDate = DateFormatter.dateFormatWithTime(this.course.publishDate);
        } else if (this.course.publishStatus == PublishStatus.PUBLISHED) {
          this.published = true;
        }
        this.load = false;
      }, () => {
        this.sb.openSnackbar('Урамшууллыг ачаалахад алдаа гарлаа!');
        this.load = false;
      }
    )
  }

  private publishCourse(): void {
    const config = new DialogConfig();
    config.outsideClick = false;
    config.background = true;
    config.width = "60vw";
    config.data = {
      course: this.course,
      hasSms: this.sb.getPermission(this.SEND_SMS)
    }
    this.sb.openDialog(TimedCoursePublishConfigComponent, config).afterClosed.subscribe((publish: PublishingModel) => {
      if (publish) {
        this.publishing = true;
        this.sb.publishCourse(this.courseId, publish).subscribe(() => {
          this.publishing = false;
          this.navigateBack();
        }, () => {
          this.publishing = false;
          this.sb.openSnackbar("Нийтлэхэд алдаа гарлаа!")
        });
      }
    });
  }
}
