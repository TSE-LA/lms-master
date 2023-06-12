import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from "@angular/core";
import {CourseModel} from "../shared-model";
import {FileAttachment} from "../structures/content-structure/content-structure.model";
import {DialogConfig} from "../dialog/dialog-config";
import {FileUploadDialogComponent} from "../dialog/file-upload-dialog/file-upload-dialog.component";
import {FileAttachmentService} from "./file-attachment.service";
import {ConfirmDialogComponent} from "../dialog/confirm-dialog/confirm-dialog.component";
import {AttachmentModel} from "../../../../core/src/lib/online-course/models/online-course.model";
import {FormUtil} from "../utilities/form.util";
import {catchError, map} from "rxjs/operators";
import {throwError} from "rxjs";

@Component({
  selector: 'jrs-file-attachment',
  template: `
    <jrs-section>
      <div class="row gx-5">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
          <div class="flex margin-bottom">
            <jrs-header-text [size]="'medium'" [margin]="false">
              ХАВСРАЛТ ФАЙЛ
            </jrs-header-text>
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
          <div>
            <jrs-list [list]="attachments" [notFoundText]="'файл хавсаргана уу'" (delete)="deleteAttachment($event)"></jrs-list>
            <jrs-page-loader [show]="uploading|| deleting"></jrs-page-loader>
          </div>
        </div>
      </div>
    </jrs-section>
  `,
  styleUrls: ['./file-attachment.component.scss']
})
export class FileAttachmentComponent implements OnChanges{
  @Input() course: CourseModel;
  @Input() attachments: FileAttachment[] = [];
  @Input() oldAttachments: AttachmentModel[] = [];
  @Output() attachmentChange = new EventEmitter<FileAttachment[]>();
  uploadFileTypes = ".docx, .xlsx, .jpeg, .jpg, .png, .svg, .mp4, .webm, .pdf";
  uploadFileRegexp = /(\.docx|\.xlsx|\.jpeg|\.jpg|\.png|\.svg|\.mp4|\.webm|\.pdf)$/i;
  loading: boolean;
  uploading: boolean;
  deleting: boolean;
  @Input() newAttachments = [];

  constructor(private sb: FileAttachmentService) {
  }
  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "attachments" && this.attachments) {
        this.attachments = this.mapAttachments(this.attachments);
      }
    }
    if (this.oldAttachments.length > 0) {
      this.attachments = this.mapOldAttachments(this.oldAttachments);
    }
  }

  addAttachment(): void {
    const config = new DialogConfig();
    config.title = "Хавсралт файл нэмэх";
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
          this.newAttachments.push(res.id)
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
        this.sb.deleteAttachment(file.id, this.course.id).subscribe(() => {
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

  saveAttachment(order: any[], courseId): any[] {
    order.push(this.sb.updateAttachment(courseId, this.newAttachments).pipe(map((res) => {
        this.sb.openSnackbar("Цахим сургалтын хавсралт амжилттай хадгаллаа.", true);
      }), catchError(err => {
        this.sb.openSnackbar("Цахим сургалт хадгалахад алдаа гарлаа.");
        return throwError(err);
      })));
    return order;
  }

  private mapAttachments(attachments: FileAttachment[]): FileAttachment[] {
    const newAttachments = [];
    for (const attachment of attachments) {
      newAttachments.push(Object.assign(attachment));
    }
    return newAttachments;
  }

  private mapOldAttachments(attachments): FileAttachment[] {
    const newAttachments = [];
    for (const attachment of attachments) {
      attachment["name"] = attachment.fileName;
      attachment["id"] = attachment.attachmentId;
      newAttachments.push(Object.assign(attachment));
    }
    return newAttachments;
  }
}
