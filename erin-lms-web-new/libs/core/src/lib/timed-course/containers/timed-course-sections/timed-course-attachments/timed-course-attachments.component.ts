import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {TimedCourseSandboxService} from "../../../services/timed-course-sandbox.service";
import {FileUploadDialogComponent} from "../../../../../../../shared/src/lib/dialog/file-upload-dialog/file-upload-dialog.component";
import {DialogConfig} from "../../../../../../../shared/src/lib/dialog/dialog-config";
import {TimedCourseModel} from "../../../../../../../shared/src/lib/shared-model";
import {ConfirmDialogComponent} from "../../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {FileAttachment} from "../../../../../../../shared/src/lib/structures/content-structure/content-structure.model";

@Component({
  selector: 'jrs-timed-course-attachments',
  template: `
    <jrs-section>
      <div class="row gx-5">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
          <div class="flex margin-bottom">
            <jrs-header-text [size]="'medium'" [margin]="false">ХАВСРАЛТ ФАЙЛ</jrs-header-text>
            <span class="spacer"></span>
            <jrs-button
              [size]="'long'"
              [outline]="true"
              [iconName]="'attach_file'"
              [iconColor]="'gray'"
              [textColor]="'text-gray'"
              (clicked)="addAttachment()"
              [title]="'Хавсралт нэмэх'">
            </jrs-button>
          </div>
          <jrs-list [list]="attachments" [notFoundText]="'файл хавсаргана уу'" (delete)="deleteAttachment($event)"></jrs-list>
          <jrs-page-loader [show]="uploading|| deleting"></jrs-page-loader>
        </div>
      </div>
    </jrs-section>
  `,
  styles: []
})
export class TimedCourseAttachmentsComponent implements OnChanges {
  @Input() course: TimedCourseModel;
  @Input() attachments: FileAttachment[] = [];
  @Output() attachmentChange = new EventEmitter<FileAttachment[]>();
  uploadFileTypes = ".docx, .xlsx, .jpeg, .jpg, .png, .svg, .mp4, .webm, .pdf";
  uploadFileRegexp = /(\.docx|\.xlsx|\.jpeg|\.jpg|\.png|\.svg|\.mp4|\.webm|\.pdf)$/i;
  loading: boolean;
  uploading: boolean;
  deleting: boolean;

  constructor(private sb: TimedCourseSandboxService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "attachments" && this.attachments) {
        this.attachments = this.mapAttachments(this.attachments);
      }
    }
  }

  addAttachment(): void {
    const config = new DialogConfig();
    config.title = "Хавсралт файл нэмэх"
    config.outsideClick = true;
    config.data = {
      info: "Зөвхөн дараах төрлийн файлууд хавсаргах боломжтой: " + this.uploadFileTypes,
      fileTypes: this.uploadFileTypes,
      allowedRegexp: this.uploadFileRegexp
    }
    this.sb.openDialog(FileUploadDialogComponent, config).afterClosed.subscribe(value => {
      if (value) {
        this.uploading = true;
        this.sb.uploadAttachment(value.file).subscribe((res) => {
          this.attachments.push(res);
          this.attachmentChange.emit(this.attachments);
          this.uploading = false;
        }, () => {
          this.uploading = false;
        });
      }
    })
  }

  deleteAttachment(file: FileAttachment): void {
    const config = new DialogConfig();
    config.title = "Хавсралт файл устгах"
    config.outsideClick = true;
    config.data = {
      info: "Устгасан файл сэргээж болохгүйг анхаарна уу.",
    }
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
        this.deleting = true;
        this.sb.deleteAttachment(file.id, file.type, this.course.id).subscribe(() => {
          this.attachments = this.attachments.filter(attachment => attachment.id != file.id);
          this.attachmentChange.emit(this.attachments);
          this.deleting = false;
          this.sb.openSnackbar("Хавсралт амжилттай устгалаа.", true);
        }, () => {
          this.deleting = false;
          this.sb.openSnackbar("Хавсралт устгахад алдаа гарлаа!");
        });
      }
    })
  }

  private mapAttachments(attachments: FileAttachment[]): FileAttachment[] {
    const newAttachments = [];
    for (const attachment of attachments) {
      newAttachments.push(Object.assign(attachment));
    }
    return newAttachments;
  }
}
