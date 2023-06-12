import {Component, HostListener, OnInit} from '@angular/core';
import {TimedCourseSandboxService} from "../../services/timed-course-sandbox.service";
import {ActivatedRoute} from "@angular/router";
import {TimedCourseModel} from "../../../../../../shared/src/lib/shared-model";
import {DateFormatter} from "../../../../../../shared/src/lib/utilities/date-formatter.util";
import {FileAttachment, StructureModule} from "../../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {ContentModule} from "../../../common/common.model";

@Component({
  selector: 'jrs-timed-course-reading-container',
  template: `
    <div class="timed-course-reading-container" disableKeyboardMouseEvents>
      <div [ngClass]="moveChapters ? 'responsive-grid' : 'display-grid'">
        <div class="timed-course-content-viewer">
          <div class="header">
            <jrs-button
              [iconName]="'arrow_back_ios'"
              [iconColor]="'secondary'"
              [noOutline]="true"
              [isMaterial]="true"
              [size]="'icon-medium'"
              [bold]="true"
              [textColor]="'text-link'"
              (clicked)="goBack()">
            </jrs-button>
            <jrs-header-text class="header-margin"
                             [size]="'medium'"
                             [margin]="false"
                             [class.long-text]="timedCourseTitle ? timedCourseTitle.length > 50 : ''">
              {{timedCourseTitle}}
            </jrs-header-text>
            <div class="spacer"></div>
            <div *ngIf="moveChapters" [class.display-none]="!moveChapters || isMobile"
                 class="flex" (click)="closeProgressSection()">
              <jrs-icon class="icon material-icons show-chapters-btn cursor-pointer"
                        [size]="'large'"
                        [color]="'secondary'"
                        [mat]="true">
                west
              </jrs-icon>
            </div>
          </div>
          <div class="content-viewer">
            <div *ngIf="isVideo">
              <iframe class="content" [src]="contentSource | safe" style="background-color: transparent"></iframe>
            </div>
            <div *ngIf="!isVideo" class="landscape">
              <img id="imgContainer" [src]="contentSource" disableKeyboardMouseEvents>
            </div>
          </div>

          <div *ngIf="!isAttachment" class="page-slider-wrapper">
            <jrs-button
              [iconName]="'arrow_back_ios'"
              [iconColor]="'primary'"
              [noOutline]="true"
              [isMaterial]="true"
              [size]="'icon-small'"
              (clicked)="showPreviousImage()">
            </jrs-button>
            <span class="page-number">{{selectedSection + 1 + ' / ' + selectedModuleLength}}</span>
            <span class="page-number">{{'БҮЛЭГ: ' + renderedModuleNumber}}</span>
            <jrs-button
              [iconName]="'arrow_forward_ios'"
              [iconColor]="'primary'"
              [noOutline]="true"
              [isMaterial]="true"
              [size]="'icon-small'"
              (clicked)="showNextImage()">
            </jrs-button>
            <span *ngIf="!isVideo" id="fullscreen-button">
              <jrs-button
                [iconName]="'fullscreen'"
                [iconColor]="'primary'"
                [noOutline]="true"
                [isMaterial]="true"
                [size]="'small'"
                (clicked)="onFullscreenClick()">
            </jrs-button>
              </span>
          </div>
        </div>

        <div>
          <div *ngIf="!moveChapters" class="chapters-section">
            <div class="chapters-header border-bottom">
              <jrs-header-text class="chapters-title" [size]="'small'" [margin]="false">
                {{DESCRIPTION_HEADER.toUpperCase()}}
              </jrs-header-text>
              <span class="spacer"></span>
              <jrs-icon class="icon material-icons cursor-pointer"
                        (click)="closeProgressSection()"
                        [size]="'medium'"
                        [color]="'purple-gray'"
                        [mat]="true">
                close
              </jrs-icon>
            </div>

            <div class="description-text">
              <span class="author float-left">{{timedCourseAuthor}}</span>
              <span class="divider float-left"> | </span>
              <span class="date float-left date-margin">{{timedCourseModifiedDate}}</span>
              <div *ngIf="this.timedCourseDescription" class="description">
                <span [innerHTML]="timedCourseDescription | linkify"></span>
              </div>
            </div>

            <div class="chapters-header" (click)="expandProgress = !expandProgress">
              <jrs-header-text class="chapters-title" [size]="'small'" [margin]="false">
                {{CHAPTERS_HEADER.toUpperCase()}}
              </jrs-header-text>
              <span class="spacer"></span>
              <jrs-icon class="icon material-icons icon-padding cursor-pointer"
                        *ngIf="timedCourseChapters ? timedCourseChapters.length > 0 : false"
                        [class.open]="expandProgress"
                        [size]="'medium-large'"
                        [color]="'purple-gray'"
                        [mat]="true">
                expand_more
              </jrs-icon>
            </div>

            <div *ngIf="expandProgress">
              <div *ngFor="let chapterData of timedCourseChapters; let i = index">
                <div class="timed-course-module" (click)="onChapterClicked(i, 0)">
                  <div class="chapters-name">
                    {{chapterData.name}}
                  </div>
                </div>
              </div>
            </div>

            <div *ngIf="additionalFiles && additionalFiles.length > 0">
              <div class="border-bottom chapters-header" (click)="expandAttachment = !expandAttachment">
                <jrs-header-text class="chapters-title" [size]="'small'" [margin]="false">
                  {{ADDITIONAL_FILES_TITLE.toUpperCase()}}
                </jrs-header-text>
                <span class="spacer"></span>
                <jrs-icon class="icon material-icons icon-padding cursor-pointer"
                          *ngIf="additionalFiles ? additionalFiles.length > 0 : false"
                          [class.open]="expandAttachment"
                          [size]="'medium-large'"
                          [color]="'purple-gray'"
                          [mat]="true">
                  expand_more
                </jrs-icon>
              </div>
              <div *ngIf="expandAttachment" class="content-background">
                <div *ngFor="let attachment of additionalFiles; let index = index">
                  <a (click)="onFileClicked(index)">
                    <jrs-course-progress [chapterIndex]="index" [additionalFile]="attachment">
                    </jrs-course-progress>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="moveChapters" [ngClass]="moveChapters ? 'responsive-grid' : 'display-grid'">
        <div class="description-section">
          <jrs-tab-group class="tab-style" [chooseFirst]="true">
            <jrs-tab [label]="DESCRIPTION_HEADER" [active]="!moveChapters">
              <div class="inline-flex author-and-date" [class.mobile-display]="isMobile">
                <jrs-header-text [size]="'small'" [margin]="false">
                  {{COURSE_INTRODUCTION}}
                </jrs-header-text>
                <span class="spacer"></span>
                <div class="responsive-author-and-date">
                  <span class="author float-right">{{timedCourseAuthor}}</span>
                  <span class="divider float-right"> | </span>
                  <span class="date float-right date-margin">{{timedCourseModifiedDate}}</span>
                </div>
              </div>
              <div class="description responsive-description">
                <span [innerHTML]="timedCourseDescription | linkify"></span>
              </div>
            </jrs-tab>

            <jrs-tab [label]="CHAPTERS_HEADER">
              <div *ngFor="let chapterData of timedCourseChapters; let i = index">
                <div class="timed-course-module margin-left" (click)="onChapterClicked(i, 0)">
                  <div class="chapters-name">
                    {{chapterData.name}}
                  </div>
                </div>
              </div>
            </jrs-tab>

            <jrs-tab *ngIf="additionalFiles" [label]="ADDITIONAL_FILES_TITLE">
              <div class="file-background margin-left" *ngFor="let attachment of additionalFiles; let index = index">
                <a (click)="onFileClicked(index)">
                  <jrs-course-progress [chapterIndex]="index" [additionalFile]="attachment">
                  </jrs-course-progress>
                </a>
              </div>
            </jrs-tab>
          </jrs-tab-group>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./timed-course-reading-page.component.scss']
})
export class TimedCourseReadingPageComponent implements OnInit {
  content: StructureModule[] = [];
  contentFile: ContentModule[] = [];
  moveChapters: boolean;
  timedCourseTitle: string;
  isMobile: boolean;
  isVideo = true;
  contentSource: any;
  timedCourseAuthor: string;
  timedCourseModifiedDate: string;
  timedCourseDescription: string;
  expandProgress = true;
  timedCourseChapters: any;
  loader: string;
  additionalFiles: FileAttachment[] = [];
  timedCourseId: string;
  expandAttachment = true;
  errorMessage: string;
  DESCRIPTION_HEADER = this.sb.constants.DESCRIPTION_HEADER;
  COURSE_INTRODUCTION = this.sb.constants.COURSE_INTRODUCTION;
  CHAPTERS_HEADER = this.sb.constants.CHAPTERS_HEADER;
  ADDITIONAL_FILES_TITLE = this.sb.constants.ATTACHMENT_FILES;
  selectedSection = 0;
  loadingPageUrl: any;
  selectedModuleLength = 0;
  renderedModuleNumber = 0;
  loading: boolean;
  noDownload: any;
  isAttachment: boolean;

  private timedCourseProperties: TimedCourseModel;
  private selectedModule = 0;

  constructor(private sb: TimedCourseSandboxService, private route: ActivatedRoute) {
    this.getResponsiveSize()
    this.loading = true;
    this.loadingPageUrl = '/alfresco/loading-page.html';
    if (this.isVideo) {
      this.contentSource = this.loadingPageUrl;
    } else {
      this.contentSource = '';
    }
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'ArrowRight') {
      this.showNextImage();
    }
    if (event.key === 'ArrowLeft') {
      this.showPreviousImage();
    }
  }

  ngOnInit(): void {
    this.getResponsiveSize();
    this.route.paramMap.subscribe(params => {
      this.timedCourseId = params.get('id')
    })
    this.loadPage();
  }

  goBack(): void {
    this.sb.getRouterService().navigateByUrl('/timed-course/container');
  }

  closeProgressSection(): void {
    this.moveChapters = !this.moveChapters;
  }

  onChapterClicked(moduleIndex: number, sectionIndex: number): void {
    this.isAttachment = false;
    const pdf = this.contentFile[moduleIndex].documentPaths[sectionIndex].sectionPath;
    this.contentSource = pdf;
    this.isVideo = !pdf.toString().includes('.JPEG');
    this.selectedModule = moduleIndex;
    this.selectedSection = sectionIndex;
    this.selectedModuleLength = this.contentFile[moduleIndex].documentPaths.length;
    this.renderedModuleNumber = this.selectedModule + 1;
    this.loading = false;
    this.loadingPageUrl = '';
  }

  loadPage(): void {
    this.loading = true;
    this.sb.getTimedCourseById(this.timedCourseId).subscribe(res => {
      this.timedCourseProperties = res;
      this.timedCourseTitle = this.timedCourseProperties.name;
      this.timedCourseDescription = this.timedCourseProperties.description ? this.timedCourseProperties.description : '';
      this.timedCourseAuthor = this.timedCourseProperties.author;
      this.timedCourseModifiedDate = DateFormatter.toISODateString(this.timedCourseProperties.modifiedDate)
    })
    this.sb.getTimedCourseStructure(this.timedCourseId).subscribe((model: any) => {
      this.additionalFiles = [];
      this.timedCourseChapters = model.modules;
      for (const attachments of model.attachments) {
        this.additionalFiles.push(attachments);
      }
    }, () => {
      this.showError();
    });
    this.sb.getTimedCourseContent(this.timedCourseId).subscribe((model: any) => {
      this.contentFile = model
      this.onChapterClicked(0, 0);
    }, () => {
      this.showError();
    })
  }

  showPreviousImage(): void {
    if (this.selectedSection > 0 && this.selectedModule >= 0) {
      this.onChapterClicked(this.selectedModule, this.selectedSection - 1);
    } else if (this.selectedModule > 0) {
      this.onChapterClicked(this.selectedModule - 1, this.contentFile[this.selectedModule - 1].documentPaths.length - 1);
    }
  }

  showNextImage(): void {
    const moduleLength = this.contentFile[this.selectedModule].documentPaths.length;
    if (this.contentFile.length > this.selectedModule + 1 && moduleLength === this.selectedSection + 1 && moduleLength >= 1) {
      this.onChapterClicked(this.selectedModule + 1, 0);
    } else if (moduleLength > this.selectedSection + 1 && moduleLength > 1) {
      this.onChapterClicked(this.selectedModule, this.selectedSection + 1);
    }
  }

  onFullscreenClick(): void {
    const elem = document.getElementById('imgContainer');

    if (elem.requestFullscreen) {
      elem.requestFullscreen();
      // @ts-ignore
    } else if (elem.mozRequestFullScreen) { /* Firefox */
      // @ts-ignore
      elem.mozRequestFullScreen();
      // @ts-ignore
    } else if (elem.webkitRequestFullscreen) { /* Chrome, Safari and Opera */
      // @ts-ignore
      elem.webkitRequestFullscreen();
      // @ts-ignore
    } else if (elem.msRequestFullscreen) { /* IE/Edge */
      // @ts-ignore
      elem.msRequestFullscreen();
    }
  }

  private showError(): void {
    this.loading = false;
    this.errorMessage = 'Мэдээлэл дуудахад алдаа гарлаа.';
  }

  onFileClicked(index: number): void {
    this.isAttachment = true;
    let fileType: string;
    if(this.additionalFiles[index] !== undefined) {
      fileType = this.fileType(this.additionalFiles[index].type.toLowerCase());
      this.contentSource = 'alfresco/Courses/' + this.timedCourseId + '/Course Content/Attachments/' + this.additionalFiles[index].id + (fileType === '.docx' ? '.pdf' : fileType) + '#toolbar=0';
    }
  }

  private getResponsiveSize(): void {
    this.sb.getMediaBreakPointChange().subscribe(res => {
      const onMobileDevice = (res == "media_sm" || res == "media_s" || res == "media_xs");
      if (onMobileDevice) {
        this.moveChapters = true;
        this.isMobile = true;
      } else if (res == 'media_lg' || res == 'media_ml' || res == 'media_md') {
        this.moveChapters = true;
        this.isMobile = false;
      } else {
        this.moveChapters = false;
        this.isMobile = false;
      }
    })
  }

  private fileType(type: string): string {
    switch (type) {
      case '.docx':
      case '.mp4':
      case '.pdf':
        this.isVideo = true;
        break;
      case '.jpeg':
      case '.jpg':
      case '.png':
      case '.svg':
        this.isVideo = false;
        break;
      default:
        this.isVideo = true;
        break;
    }
    return type;
  }
}
