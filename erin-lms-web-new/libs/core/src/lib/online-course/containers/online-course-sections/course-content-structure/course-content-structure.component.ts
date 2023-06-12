import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {StructureUploadFile} from "../../../../../../../shared/src/lib/shared-model";
import {
  CourseStructure,
  FileAttachment,
  ModuleFileType,
  StructureModule
} from "../../../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {Subject, throwError} from "rxjs";
import {OnlineCourseSandboxService} from "../../../online-course-sandbox.service";
import {DialogConfig} from "../../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {OnlineCourseModel} from "../../../models/online-course.model";
import {catchError, map} from "rxjs/operators";
import {ContentStructureComponent} from "../../../../../../../shared/src/lib/structures/content-structure/content-structure.component";

@Component({
  selector: 'jrs-course-content-structure',
  template: `
    <jrs-content-structure
      #structure
      [load]="loading"
      [moduleData]="moduleData"
      (uploadEmit)="fileUpload($event)"
      (moduleChange)="updateModule($event)">
    </jrs-content-structure>
    <jrs-page-loader [show]="uploadingFile"></jrs-page-loader>
    <jrs-percentage-progress
      [percentage]="convertPercentage"
      [text]="this.progressText"
      [loading]="this.progressStarted"
      (completed)="stopConversionLoad()">
    </jrs-percentage-progress>
  `,
  styles: []
})
export class CourseContentStructureComponent implements OnChanges {
  @ViewChild('structure') structure: ContentStructureComponent;
  @Input() course: OnlineCourseModel;
  @Input() changedAttachment: FileAttachment[];
  @Input() newAttachment;
  @Output() attachmentChange = new EventEmitter<FileAttachment[]>();
  loading: boolean;
  initialModuleData: StructureModule[] = [];
  moduleData: StructureModule[] = [];
  uploadingFile: boolean;
  progressText: string;
  progressStarted: boolean;
  CONVERTER_CONFIRM_DIALOG_TITLE = 'ВИДЕО ХУВИРГАГЧ';
  CONVERTER_CONFIRM_DIALOG_MSG = 'Тайлбар: Веб хөтчийн дэмждэггүй видео оруулсан байна. Видеог хувиргахад хугацаа шаардагдана. Үргэлжлүүлэх үү?';
  convertPercentage = 0;
  savingStructure: boolean;
  private firstTime = true;
  private pdfSplitSubject = new Subject<number>();
  private pdfUploading: boolean;
  private currentSectionIndex: number;
  private currentModuleIndex: number;
  private serverSentEvent: EventSource;
  private currentUpload: StructureUploadFile;
  private structureModel: CourseStructure;

  constructor(private sb: OnlineCourseSandboxService, private cd: ChangeDetectorRef) {
    this.moduleData = this.getStructureInitialValue();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "course" && this.course) {
        this.loadData();
      }
    }
  }

  isDataChanged(): boolean {
    if (!this.structure.isReady()) {
      return false;
    }
    return !!this.changedAttachment || (JSON.stringify(this.moduleData) !== JSON.stringify(this.initialModuleData));
  }

  fileUpload(model: StructureUploadFile): void {
    this.currentUpload = model;
    this.pdfUploading = model.uploadFileType.includes('PDF');
    this.uploadingFile = true;
    this.prepPdfProgress(this.pdfUploading);
    this.sb.uploadFile(model.file, this.moduleData[model.module].sections[model.section].name, this.course.id).subscribe((res) => {
      this.moduleData[model.module].uploadFileType = model.uploadFileType;
      if (!this.pdfUploading) {
        this.convertVideo(res, model);
      }
    }, () => {
      this.uploadingFile = false;
    });
  }

  private getAttachments(): FileAttachment[] {
    return this.changedAttachment ? this.changedAttachment : this.structureModel.attachments;
  }

  saveStructure(order: any[]): any[] {
    this.savingStructure = true;
    const attachments = this.getAttachments();
    if (this.firstTime) {
      order.push(this.sb.saveContentStructure(attachments, this.moduleData, this.course.id).pipe(map(res => {
        this.assignStructureData(res);
        this.savingStructure = false;
        this.sb.openSnackbar("Цахим сургалтын агуулгыг амжилттай үүсгэлээ.", true);
      }), catchError(err => {
        this.savingStructure = false;
        this.sb.openSnackbar("Цахим сургалтын агуулгыг үүсгэхэд алдаа гарлаа!");
        return throwError(err);
      })));
    } else {
      const newAttachments: FileAttachment[] = [];
      const existingAttachments = this.structureModel.attachments.map(attachment => attachment.id);
      attachments.forEach(attachment => {
        if(!existingAttachments.includes(attachment.id)) {
          newAttachments.push(attachment)
        }
      })
      order.push(this.sb.updateContentStructure(newAttachments, this.moduleData, this.course.id).pipe(map(res => {
        this.assignStructureData(res);
        this.savingStructure = false;
        this.sb.openSnackbar("Цахим сургалтын агуулгыг амжилттай хадгаллаа.", true);
      }), catchError(err => {
        this.savingStructure = false;
        this.sb.openSnackbar("Цахим сургалтын агуулгыг хадгалахад алдаа гарлаа!");
        return throwError(err);
      })));
    }
    return order;
  }

  isPublishReady(): boolean {
    return this.structure.isReady();
  }

  stopConversionLoad(): void {
    if (this.pdfUploading) {
      this.sb.getSection(
        this.currentUpload.file,
        this.moduleData[this.currentUpload.module].sections[this.currentUpload.section].name,
        this.course.id).subscribe(section => {
        this.addSectionsToModule(this.currentUpload.module, this.currentUpload.section, section);
      }, () => {
        this.sb.openSnackbar("Хөрвүүлсэн PDF ачаалахад алдаа гарлаа!");
      })
    }
    setTimeout(() => {
      this.uploadingFile = false;
      this.progressStarted = false;
      this.cd.detectChanges();
    }, 3000);
  }

  updateModule(changedModule: StructureModule[]): void {
    this.moduleData = changedModule;
  }

  private loadData(): void {
    this.loading = true;
    this.sb.getOnlineCourseStructure(this.course.id).subscribe((structure: CourseStructure) => {
      this.assignStructureData(structure);
      this.attachmentChange.emit(structure.attachments)
      this.loading = false;
    }, () => {
      this.loading = false;
      this.sb.openSnackbar("Цахим сургалтын агуулга ачаалахад алдаа гарлаа!")
    });
  }

  private convertVideo(res, model: StructureUploadFile): void {
    if (!res.codecSupported) {
      this.currentSectionIndex = model.section;
      this.currentModuleIndex = model.module;
      this.uploadingFile = false;
      const config = new DialogConfig();
      config.width = '500px';
      config.title = this.CONVERTER_CONFIRM_DIALOG_TITLE;
      config.submitButton = "Үргэлжлүүлэх";
      config.declineButton = "Болих"
      config.data = {
        info: this.CONVERTER_CONFIRM_DIALOG_MSG
      };

      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config)
      dialogRef.afterClosed.subscribe((convertConfirmed) => {
        if (convertConfirmed) {
          this.trackVideoPercentage(res, model);
        } else {
          this.uploadingFile = false;
          return;
        }
      });
    } else {
      this.uploadingFile = false;
      this.addSectionsToModule(model.module, model.section, res);
    }
  }


  private trackVideoPercentage(res, model: StructureUploadFile): void {
    this.sb.getServerSentEvent().subscribe((sse) => {
      this.serverSentEvent = sse;
    })
    this.sb.convertVideo(res.absolutePath, res.attachmentName[0].id,
      this.moduleData[model.module].sections[model.section].name, model.file).subscribe((convertedFile) => {
      this.progressText = "Видео хөрвүүлэлт...";
      this.convertPercentage = 0;
      this.progressStarted = true;
      this.sb.getCodecConvertingPercentage().subscribe((percentage: number) => {
        this.convertPercentage = percentage;
        this.cd.detectChanges();
      });
      this.addSectionsToModule(model.module, model.section, convertedFile);
    }, () => {
      this.uploadingFile = false;
    });
  }


  private getStructureInitialValue(): StructureModule[] {
    return [{
      updatedName: 'БҮЛЭГ 1',
      name: undefined,
      opened: true,
      sections: [{name: 'Хичээл', fileId: undefined}],
      uploadFileType: ModuleFileType.VIDEO
    }];
  }


  private prepPdfProgress(pdfUploading: boolean): void {
    if (pdfUploading) {
      this.uploadingFile = false;
      this.pdfSplitSubject = this.sb.getPdfSplitPercentage();
      this.progressText = "PDF Хөрвүүлэлт...";
      this.convertPercentage = 0;
      this.progressStarted = true;
      this.pdfSplitSubject.subscribe((percentage: number) => {
        this.convertPercentage = percentage;
        this.cd.detectChanges();
      });
    }
  }

  private addSectionsToModule(moduleIndex: number, sectionIndex: number, resModule: StructureModule): void {
    const responseSections = resModule.sections;
    this.moduleData[moduleIndex].sections.splice(sectionIndex, 1, ...responseSections.slice(0, responseSections.length));
  }

  private assignStructureData(structure: CourseStructure): void {
    this.structureModel = structure;
    const modules = structure.modules;
    if (modules && modules.length > 0) {
      this.initialModuleData = this.mapStructure(modules);
      this.moduleData = this.mapStructure(modules);
    }
  }

  private mapStructure(modules: StructureModule[]): StructureModule[] {
    const newModules = []
    this.firstTime = false;
    for (const module of modules) {
      const structure: StructureModule = {
        name: module.name,
        opened: false,
        updatedName: module.updatedName,
        uploadFileType: module.uploadFileType,
        moduleFolderId: module.moduleFolderId,
        sections: []
      };
      for (const section of module.sections) {
        structure.sections.push({name: section.name, fileId: section.fileId});
      }
      newModules.push(structure);
    }
    return newModules;
  }
}
