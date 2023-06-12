import {Component, ViewChild} from '@angular/core';
import {OnlineCourseSandboxService} from "../../online-course-sandbox.service";
import {OnlineCourseInfoComponent} from "../online-course-sections/online-course-info/online-course-info.component";
import {ActivatedRoute} from "@angular/router";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {CourseContentStructureComponent} from "../online-course-sections/course-content-structure/course-content-structure.component";
import {OnlineCourseTestStructureComponent} from "../online-course-sections/online-course-test-structure/online-course-test-structure.component";
import {AttachmentModel, OnlineCourseModel, PublishingModel} from "../../models/online-course.model";
import {OnlineCoursePublishConfigComponent} from "../online-course-sections/online-course-publish-config/online-course-publish-config.component";
import {from, Observable} from "rxjs";
import {PublishStatus} from "../../../../../../shared/src/lib/shared-model";
import {DateFormatter} from "../../../../../../shared/src/lib/utilities/date-formatter.util";
import {OnlineCourseEnrollmentComponent} from "../online-course-sections/online-course-enrollment/online-course-enrollment.component";
import {ConfirmCheckboxDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-checkbox-dialog/confirm-checkbox-dialog.component";
import {FileAttachment} from "../../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {FileAttachmentComponent} from "../../../../../../shared/src/lib/file-attachment/file-attachment.component";
import {concatAll, reduce} from "rxjs/operators";

@Component({
  selector: 'jrs-online-course-create-page',
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
      <jrs-header-text [size]="'medium'">ЦАХИМ СУРГАЛТЫН МЭДЭЭЛЭЛ</jrs-header-text>
      <div class="margin-left-double">
        <jrs-info-panel *ngIf="pending" [text]="publishDate + ' цагт ирээдүйд нийтлэхээр тохируулсан байна.'" [width]="'94%'"></jrs-info-panel>
      </div>
      <div class="row gx-5 margin-top">
        <div class="margin-top" [ngClass]="!published? 'col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6':
                                                         'col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12'">
          <jrs-online-course-info #infoSection [course]="onlineCourse" [load]="load" (testChange)="changeTest($event)"></jrs-online-course-info>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-6" *ngIf="!published">
          <jrs-course-content-structure
            #structureSection
            [course]="onlineCourse"
            (attachmentChange)="assignAttachment($event)"
            [changedAttachment]="changedAttachment"
            [newAttachment]="newAttachments"
          ></jrs-course-content-structure>
        </div>
      </div>
    </jrs-section>

    <jrs-file-attachment #attachmentSection
                         [course]="onlineCourse"
                         (attachmentChange)="changeAttachment($event)"
                         [attachments]="attachments"
                         [oldAttachments]="oldAttachment"
                         [newAttachments]="newAttachments">
    </jrs-file-attachment>

    <jrs-online-course-test-structure *ngIf="!published" #testSection [course]="onlineCourse" [hasTest]="hasTest"></jrs-online-course-test-structure>

    <jrs-section *ngIf="published">
      <div class="margin-left-double">
        <jrs-online-course-enrollment #enrollmentSection [course]="onlineCourse"
                                      (openInClicked)="saveAndNavigateToGroupEnrollment()"></jrs-online-course-enrollment>
      </div>
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
  `
})
export class OnlineCourseUpdatePageComponent {
  @ViewChild('infoSection') infoSection: OnlineCourseInfoComponent;
  @ViewChild('structureSection') structureSection: CourseContentStructureComponent;
  @ViewChild('testSection') testSection: OnlineCourseTestStructureComponent;
  @ViewChild('enrollmentSection') enrollmentSection: OnlineCourseEnrollmentComponent;
  @ViewChild('attachmentSection') attachmentSection: FileAttachmentComponent;
  ATTACHMENT_FILES = this.sb.constants.ATTACHMENT_FILES;
  downloading: boolean;
  oldAttachment:AttachmentModel[]=[];
  onlineCourse: OnlineCourseModel;
  hasTest = true;
  saving: boolean;
  publishing: boolean;
  percentage: number;
  pending: boolean;
  publishDate: string;
  published: boolean;
  load: boolean;
  attachments: FileAttachment[] = [];
  newAttachments: FileAttachment[] = [];
  changedAttachment: FileAttachment[];

  SEND_SMS = 'user.onlineCourse.sendSms';

  private courseId: string;
  private saveOrder: any[] = [];

  constructor(private sb: OnlineCourseSandboxService, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      this.courseId = params.get('id');
      this.load = true;
      this.sb.getOnlineCourseById(this.courseId).subscribe(res => {
        this.onlineCourse = res;
        this.hasTest = res.hasTest;
        if (this.onlineCourse.publishStatus == PublishStatus.PENDING) {
          this.pending = true;
          this.publishDate = DateFormatter.dateFormatWithTime(this.onlineCourse.publishDate);
        } else if (this.onlineCourse.publishStatus == PublishStatus.PUBLISHED) {
          this.published = true;
        }
        this.load = false;
      }, () => {
        this.sb.openSnackbar("Цахим сургалт ачаалахад алдаа гарлаа!");
        this.load = false;
      });
    })
    this.sb.getAttachment(this.courseId).subscribe((res: AttachmentModel[]) => {
      this.oldAttachment = res;
    });
  }

  goBack(): void {
    const infoChanged = this.infoSection.isDataChanged();
    const structureChanged = this.structureSection && this.structureSection.isDataChanged();
    const testChange = this.testSection && this.testSection.isDataChanged();
    const enrollSection = this.enrollmentSection && this.enrollmentSection.isDataChanged();
    if (infoChanged || structureChanged || testChange || enrollSection) {
      const config = new DialogConfig();
      config.outsideClick = false;
      config.title = "Мэдээллийг хадгалахгүй шууд гарах уу?"
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
          this.infoSection.sendNotification = res.send;
          this.saving = true;
          save.subscribe(() => {
            this.saving = false;
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

  saveAndNavigateToGroupEnrollment(): void {
    this.saving = true;
    this.saveCourse().subscribe(() => {
      this.saving = false;
      this.sb.navigateByUrl('/online-course/update/' + this.onlineCourse.id + '/group-enrollment');
    }, () => {
      this.saving = false;
    })
  }

  private navigateBack(): void {
    this.sb.navigateByUrl('/online-course/container');
  }

  assignAttachment($event: FileAttachment[]): void {
    this.attachments = $event;
  }

  changeAttachment($event: FileAttachment[]): void {
    this.changedAttachment = $event;
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

    if(this.newAttachments) {
      this.saveOrder = this.attachmentSection.saveAttachment(this.saveOrder, this.courseId)
    }

    if (this.enrollmentSection && this.enrollmentSection.isDataChanged()) {
      this.saveOrder = this.enrollmentSection.saveEnrollment(this.saveOrder);
    }
    return from(this.saveOrder).pipe(
      concatAll(),
      reduce((acc, data) => acc.concat(data), [])
    );
  }

  private publishCourse(): void {
    const config = new DialogConfig();
    config.outsideClick = false;
    config.background = true;
    config.width = "60vw";
    config.data = {
      course: this.onlineCourse,
      hasSms: this.sb.getPermission(this.SEND_SMS)
    }
    this.sb.openDialog(OnlineCoursePublishConfigComponent, config).afterClosed.subscribe((publish: PublishingModel) => {
      if (publish) {
        this.publishing = true;
        this.sb.publishCourse(this.courseId, publish).subscribe(() => {
          this.publishing = false;
          this.sb.navigateByUrl('/online-course');
        }, () => {
          this.publishing = false;
          this.sb.openSnackbar("Нийтлэхэд алдаа гарлаа!")
        });
      }
    });
  }
}
