import {Component, Input, Output, ViewChild, EventEmitter, ChangeDetectorRef} from '@angular/core';
import {ModuleFileType, StructureModule} from "./content-structure.model";
import {SnackbarService} from "../../snackbar/snackbar.service";
import {StructureUploadFile} from "../../shared-model";

@Component({
  selector: 'jrs-content-structure',
  template: `
    <ng-container *ngIf="!load">
      <div class=" row gx-1 margin-bottom">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6 margin-bottom">
          <jrs-button
            *ngIf="!disabled"
            [size]="'long'"
            [iconName]="pdfIcon"
            [iconColor]="'gray'"
            [width]="'100%'"
            [outline]="true"
            [textColor]="'text-gray'"
            (clicked)="addModule(pdfType)"
            [title]="'PDF бүлэг нэмэх'">
          </jrs-button>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
          <jrs-button
            *ngIf="!disabled"
            [size]="'long'"
            [iconName]="videoIcon"
            [iconColor]="'gray'"
            [width]="'100%'"
            [outline]="true"
            [textColor]="'text-gray'"
            (clicked)="addModule(videoType)"
            [title]="'Video бүлэг нэмэх'">
          </jrs-button>
        </div>
      </div>
      <jrs-label *ngIf="moduleNameError" [error]="true" [text]="'Бүлгийн нэр давхцаж байна! '"></jrs-label>
      <jrs-label *ngIf="emptySectionError" [error]="true" [text]="'Хоосон хичээл байна, устгана уу!'"></jrs-label>
      <ng-content></ng-content>
      <jrs-scroll [height]="'250px'" [color]="'primary'" [size]="'small'">
        <div class="row gx-1 structure-container">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <ng-container *ngFor="let module of moduleData; let moduleIndex=index">
              <div (drop)="drop(moduleIndex, $event)"
                   (dragover)="allowDrop(moduleIndex, $event)"
                   [class.drag-over]="dragIndex!== moduleIndex && moduleIndex === dragOverIndex">
                <div class="module"
                     draggable="true"
                     (dragstart)="drag(moduleIndex)">
                  <div class="drag-button">
                    <jrs-icon
                      [size]="'medium'"
                      [color]="'gray'"
                      [mat]="true">
                      drag_indicator
                    </jrs-icon>
                  </div>
                  <div class="module-input-area" [class.module-disabled]="disabled">
                    <jrs-icon
                      class="type-icon"
                      [size]="'medium'"
                      [color]="'gray'"
                      [mat]="true">
                      {{getTypeIcon(module)}}
                    </jrs-icon>
                    <span class="order-number">
                {{moduleIndex + 1}}.
              </span>
                    <input
                      [(ngModel)]="module.updatedName"
                      (input)="setModule(); checkName()"
                      [placeholder]="'Бүлэг'"
                      class="input">
                    <jrs-circle-button
                      class="dropdown-section"
                      [class.open-module]="module.opened"
                      [size]="'medium'"
                      [isMaterial]="true"
                      [iconName]="'expand_more'"
                      [color]="'none'"
                      [iconColor]="'gray'"
                      (click)="module.opened=!module.opened">
                    </jrs-circle-button>
                  </div>
                  <div class="trash-button" *ngIf="!disabled" (click)="deleteModule(moduleIndex)">
                    <jrs-icon
                      [size]="'medium'"
                      [color]="'light'"
                      [mat]="false">
                      jrs-trash-can
                    </jrs-icon>
                  </div>
                </div>
                <ng-container *ngIf="module.opened">
                  <div *ngFor="let section of module.sections; let sectionIndex = index" class="section">
                    <div class="section-input-area"
                         [class.section-disabled]="disabled"
                         [class.reserve-space]="!disabled && sectionIndex!==0">
                 <span class="order-number">
                 {{sectionIndex + 1}}.
                 </span>
                      <input
                        [(ngModel)]="section.name"
                        (input)="setModule()"
                        [placeholder]="'Хичээл'"
                        [disabled]="disabled"
                        class="input">
                      <jrs-icon *ngIf="!disabled && section.fileId"
                                [size]="'medium'"
                                [color]="'primary'"
                                [mat]="true"
                                class="uploaded-icon">
                        cloud_done
                      </jrs-icon>
                      <jrs-circle-button
                        *ngIf="!disabled && !section.fileId"
                        [size]="'medium'"
                        [isMaterial]="true"
                        [iconName]="'cloud_upload'"
                        [color]="'none'"
                        [iconColor]="emptySectionError? 'warn' : 'gray'"
                        (click)="fileInput.click()"
                        [attr.data-test-id]="'course-file-upload-button-' + sectionIndex">
                        <input #fileInput type="file"
                               id="fileUpload" name="pdfUpload"
                               [accept]="module.uploadFileType === pdfType? '.pdf': 'webm, .mp4'"
                               style="display:none;"
                               (change)="uploadFiles($event,moduleIndex, sectionIndex, module.uploadFileType)"/>
                      </jrs-circle-button>
                      <jrs-circle-button
                        *ngIf="!disabled"
                        [size]="'medium'"
                        [isMaterial]="true"
                        [iconName]="'close'"
                        [color]="'none'"
                        [iconColor]="'warn'"
                        (click)="deleteSection(moduleIndex, sectionIndex)"
                        [attr.data-test-id]="'course-file-remove-button-' + sectionIndex">
                      </jrs-circle-button>
                    </div>
                    <jrs-circle-button
                      *ngIf="!disabled && sectionIndex===0"
                      [size]="'medium'"
                      [isMaterial]="true"
                      [iconName]="'add'"
                      [color]="'gray'"
                      [iconColor]="'light'"
                      (click)="addSection(moduleIndex)"
                      [attr.data-test-id]="'course-content-add-button-' + sectionIndex">
                    </jrs-circle-button>
                  </div>
                </ng-container>
              </div>
            </ng-container>
          </div>
        </div>
      </jrs-scroll>
    </ng-container>
    <div *ngIf="load">
      <div *ngFor="let item of [].constructor(5)" class="shimmer"></div>
    </div>
  `,
  styleUrls: ['./content-structure.component.scss']
})
export class ContentStructureComponent {
  @Input() moduleData: StructureModule[] = [];
  @Input() disabled: boolean;
  @Input() load: boolean;
  @Output() moduleChange = new EventEmitter<StructureModule[]>();
  @Output() uploadEmit = new EventEmitter<StructureUploadFile>();
  @ViewChild('fileInput') fileInput;
  pdfType = ModuleFileType.PDF;
  videoType = ModuleFileType.VIDEO;
  pdfIcon = 'picture_as_pdf';
  videoIcon = 'movie';
  dragIndex: number;
  dragOverIndex: number;
  loading = false;
  moduleNameError: boolean;
  emptySectionError: boolean;


  constructor(private snackbarService: SnackbarService, private cd: ChangeDetectorRef) {
  }

  drop(dropIndex: number, e): void {
    e.preventDefault();
    const temp = this.moduleData[this.dragIndex];
    this.moduleData[this.dragIndex] = this.moduleData[dropIndex];
    this.moduleData[dropIndex] = temp;
    this.dragIndex = null;
    this.dragOverIndex = null;
  }

  allowDrop(dragOverIndex, e): void {
    e.preventDefault();
    this.dragOverIndex = dragOverIndex;
  }

  drag(dragIndex: number): void {
    this.dragIndex = dragIndex;
    this.moduleData.forEach(value => value.opened = false);
  }

  uploadFiles(event, moduleIndex, sectionIndex, moduleFileType): void {
    const allowedExtensions = /(\.mp4|\.webm|\.pdf)$/i;
    const allowedVideoExtensions = /(\.mp4|\.webm)$/i;
    const allowedDocumentExtensions = /(\.pdf)$/i;
    const uploadedFile = event.target.files[0];
    const inputFileName = event.target.files[0].name;
    const filesize = Number(((uploadedFile.size / 1024) / 1024).toFixed(4)); // MB
    if (filesize > 500) {
      this.snackbarService.open('Файлын хэмжээ 350mb ээс хэтэрч байна!');
      return;
    }
    const data: StructureUploadFile = {file: uploadedFile, module: moduleIndex, section: sectionIndex, uploadFileType: moduleFileType};
    if (moduleFileType === ModuleFileType.PDF && allowedDocumentExtensions.exec(inputFileName)) {
      this.uploadEmit.emit(data);
    } else if (moduleFileType === ModuleFileType.PDF && !allowedDocumentExtensions.exec(inputFileName)) {
      this.snackbarService.open('PDF файл оруулна уу.');
    } else if (moduleFileType === ModuleFileType.VIDEO && allowedVideoExtensions.exec(inputFileName)) {
      this.uploadEmit.emit(data);
    } else if (moduleFileType === ModuleFileType.VIDEO && !allowedVideoExtensions.exec(inputFileName)) {
      this.snackbarService.open('MP4 эсвэл WebM файл оруулна уу.');
    } else if (moduleFileType == null && allowedExtensions.exec(inputFileName)) {
      this.uploadEmit.emit(data);
    } else {
      this.snackbarService.open('PDF, MP4 эсвэл WebM файл оруулна уу.');
    }
  }

  deleteModule(index: number): void {
    if (!(this.moduleData.length <= 1 && index < 1)) {
      this.moduleData.splice(index, 1);
    }
    this.setModule();
  }

  addModule(uploadFileType: ModuleFileType): void {
    this.moduleData.push({
      updatedName: 'Бүлэг - ' + (this.moduleData.length + 1),
      name: 'Бүлэг - ' + (this.moduleData.length + 1),
      sections: [{name: 'Хичээл', fileId: undefined}],
      opened: true,
      attachmentName: [],
      additionalFile: [],
      uploadFileType: uploadFileType
    });
    this.setModule();
    this.checkName();
  }

  addSection(index: number): void {
    this.moduleData[index].sections.push({name: 'Хичээл - ' + index, fileId: undefined});
    this.setModule();
  }

  deleteSection(moduleIndex: number, sectionIndex: number): void {
    const sections = this.moduleData[moduleIndex].sections;
    if (sections.length > 1) {
      sections.splice(sectionIndex, 1);
    } else {
      this.deleteModule(moduleIndex);
    }
    this.cd.detectChanges();
    this.setModule();
  }

  setModule(): void {
    this.moduleChange.emit(this.moduleData);
  }

  getTypeIcon(module): string {
    return module.uploadFileType == this.pdfType ? this.pdfIcon : this.videoIcon;
  }

  checkName(): void {
    const list = new Set(this.moduleData.map(module => module.updatedName));
    this.moduleNameError = list.size != this.moduleData.length;
  }

  isReady(): boolean {
    this.emptySectionError = false;
    for (const module of this.moduleData) {
      for (const section of module.sections) {
        if (!section.fileId) {
          this.emptySectionError = true;
          return false;
        }
      }
    }
    return !this.moduleNameError;
  }
}
