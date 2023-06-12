import {ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {StructureUploadFile, TimedCourseModel} from "../../../../../../../shared/src/lib/shared-model";
import {ContentStructureComponent} from "../../../../../../../shared/src/lib/structures/content-structure/content-structure.component";
import {FileAttachment, ModuleFileType, StructureModule} from "../../../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {Subject, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {TimedCourseSandboxService} from "../../../services/timed-course-sandbox.service";
import {TimedCourseStructure} from "../../../models/timed-course.model";

@Component({
  selector: 'jrs-timed-course-content-structure',
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
export class TimedCourseContentStructureComponent implements OnChanges {
  @ViewChild('structure') structure: ContentStructureComponent;
  @Input() course: TimedCourseModel;
  @Input() changedAttachment: FileAttachment[];
  @Output() attachmentChange = new EventEmitter<FileAttachment[]>();
  loading: boolean;
  initialModuleData: StructureModule[] = [];
  moduleData: StructureModule[] = [];
  uploadingFile: boolean;
  progressText: string;
  progressStarted: boolean;
  convertPercentage = 0;
  savingStructure: boolean;
  private firstTime = true;
  private pdfSplitSubject = new Subject<number>();
  private pdfUploading: boolean;
  private currentUpload: StructureUploadFile;
  private structureModel: TimedCourseStructure;

  constructor(private sb: TimedCourseSandboxService, private cd: ChangeDetectorRef) {
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
        this.uploadingFile = false;
        this.addSectionsToModule(model.module, model.section, res);
      }
    }, () => {
      this.uploadingFile = false;
    });
  }

  saveStructure(order: any[]): any[] {
    this.savingStructure = true;
    const attachments = this.getAttachments();
    if (this.firstTime) {
      order.push(this.sb.saveContentStructure([], attachments, this.moduleData, this.course.id).pipe(map(res => {
        this.assignStructureData(res);
        this.savingStructure = false;
        this.sb.openSnackbar("Урамшууллын агуулга амжилттай үүсгэлээ.", true);
      }), catchError(err => {
        this.savingStructure = false;
        this.sb.openSnackbar("Урамшууллын агуулга хадгалахад алдаа гарлаа!");
        return throwError(err);
      })));
    } else {
      const newAttachments: FileAttachment[] = [];
      const existingAttachments = this.structureModel.attachments.map(attachment=>attachment.id);
      attachments.forEach(attachment=>{
        if(!existingAttachments.includes(attachment.id)){
          newAttachments.push(attachment);
        }
      })
      order.push(this.sb.updateContentStructure([], newAttachments, this.moduleData, this.course.id).pipe(map(res => {
        this.assignStructureData(res);
        this.savingStructure = false;
        this.sb.openSnackbar("Урамшууллын агуулга амжилттай хадгаллаа.", true);
      }), catchError(err => {
        this.savingStructure = false;
        this.sb.openSnackbar("Урамшууллын агуулга хадгалахад алдаа гарлаа!");
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

  private getAttachments(): FileAttachment[] {
    return this.changedAttachment ? this.changedAttachment : this.structureModel.attachments;
  }

  private loadData(): void {
    this.loading = true;
    this.sb.getTimedCourseStructure(this.course.id).subscribe((structure: TimedCourseStructure) => {
      this.assignStructureData(structure);
      this.attachmentChange.emit(structure.attachments);
      this.loading = false;
    }, () => {
      this.loading = false;
      this.sb.openSnackbar("Урамшууллын агуулга ачаалахад алдаа гарлаа!")
    });
  }

  private getStructureInitialValue(): StructureModule[] {
    return [{
      updatedName: 'PDF',
      name: undefined,
      opened: true,
      sections: [{name: 'Хичээл', fileId: undefined}],
      uploadFileType: ModuleFileType.PDF
    },
      {
        updatedName: 'PPT',
        name: undefined,
        opened: true,
        sections: [{name: 'Хичээл', fileId: undefined}],
        uploadFileType: ModuleFileType.PDF
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

  private assignStructureData(structure: TimedCourseStructure): void {
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
