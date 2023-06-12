import {ChangeDetectorRef, Component, Inject, NgZone, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {TimedCourseSandboxService} from "../../services/timed-course-sandbox.service";
import {RuntimeDataModel, ScoModel} from "../../../scorm/model/runtime-data.model";
import {TimedCourseProgress, TimedCourseStructure} from "../../models/timed-course.model";
import {ActivatedRoute, NavigationExtras, Router} from "@angular/router";
import {UserRoleProperties} from "../../../common/common.model";
import {ScormRuntimeComponent} from "../../../scorm/component/scorm-runtime.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {ScormTimeUtil} from "../../../scorm/util/scorm-time.util";
import {DateFormatter} from "../../../../../../shared/src/lib/utilities/date-formatter.util";
import {CheckComponent} from "../../../online-course/component/check-component";
import {Subscription} from "rxjs";
import {FileAttachment} from "../../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {TimedCourseModel} from "../../../../../../shared/src/lib/shared-model";

@Component({
  selector: 'jrs-timed-course-launch-page',
  template: `
    <jrs-confetti *ngIf="igniteConfetti"></jrs-confetti>
    <div class="timed-course-launch-container" disableKeyboardMouseEvents browserTab (checkEvent)="checkBeforeUnloadEvent($event)">
      <div [ngClass]="moveChapters ? 'responsive-grid' : 'display-grid'">
        <div class="scorm section">
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
                        [mat]="true">west
              </jrs-icon>

            </div>
          </div>
          <div class="scorm-runtime" *ngIf="!isAttachment">
            <div class="scorm-runtime-wrapper">
              <scorm-runtime
                #scormRuntime
                [data]="data"
                [launchUrl]="launchUrl"
                (dataChanged)="onDataChanged($event)"
                (committed)="onCommitted($event)">
              </scorm-runtime>
            </div>
          </div>

          <div *ngIf="isAttachment" class="course-content-viewer">
            <div *ngIf="isVideo">
              <iframe class="rendered-content" [src]="fileSrc | safe"></iframe>
            </div>
            <div *ngIf="!isVideo" class="landscape-orientation">
              <img id="imgContainer" [src]="fileSrc" disableKeyboardMouseEvents>
            </div>
          </div>
        </div>

        <div *ngIf="!moveChapters" class="chapters-section">

          <div class="border-bottom chapters-header">
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
            <div style="display: block; margin: 25px 0 5px">
              <span [innerHTML]="timedCourseDescription | linkify"></span>
            </div>
          </div>

          <div class="chapters-header" (click)="expandProgress = !expandProgress">
            <jrs-header-text class="chapters-title" [size]="'small'" [margin]="false">
              {{CHAPTERS_HEADER.toUpperCase()}}
            </jrs-header-text>
            <span class="spacer"></span>
            <jrs-icon class="icon material-icons icon-padding cursor-pointer"
                      *ngIf="timedCourseProgressData ? timedCourseProgressData.length > 0 : false"
                      [class.open]="expandProgress"
                      [size]="'medium-large'"
                      [color]="'purple-gray'"
                      [mat]="true">
              expand_more
            </jrs-icon>
          </div>

          <div *ngIf="expandProgress" class="border-top">
            <div *ngFor="let progressData of timedCourseProgressData; let i = index">
              <a tabindex="{{i}}" (click)="onChapterClicked(i)"
                 [disabled]="getDisabledChapterValue(progressData, i)">
                <jrs-course-progress [chapterIndex]="i"
                                     [noProgress]="scos[i].isTest || scos[i].isQuestionnaire"
                                     [showCheckMark]="(isTestCompleted(scos[i]) || isQuestionnaireCompleted(scos[i]) && hasCompletedChapters())"
                                     [progressData]="progressData"
                                     [selectedChapter]="scos[currentIndex].scoName == progressData.moduleName"
                                     [unLocked]="!getDisabledChapterValue(progressData, i)">
                </jrs-course-progress>
              </a>
            </div>
          </div>

          <div *ngIf="canAccess && currentFiles.length > 0">
            <div class="chapters-header" [class.border-bottom]="additionalFiles.length == 0" (click)="expandCurrentFile = !expandCurrentFile">
              <jrs-header-text class="chapters-title" [size]="'small'" [margin]="false">
                {{CURRENT_FILES_TITLE.toUpperCase()}}
              </jrs-header-text>
              <span class="spacer"></span>
              <jrs-icon class="icon material-icons icon-padding cursor-pointer"
                        *ngIf="currentFiles ? currentFiles.length > 0 : false"
                        [class.open]="expandCurrentFile"
                        [size]="'medium-large'"
                        [color]="'purple-gray'"
                        [mat]="true">
                expand_more
              </jrs-icon>
            </div>
            <div *ngIf="expandCurrentFile" class="files-container">
              <div *ngFor="let currentFile of currentFiles; let index = index">
                <a (click)="onFileClicked('currentFile', index)">
                  <jrs-course-progress [chapterIndex]="index" [currentFile]="currentFile">
                  </jrs-course-progress>
                </a>
              </div>
            </div>
          </div>

          <div *ngIf="additionalFiles.length > 0">
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
            <div *ngIf="expandAttachment" class="files-container">
              <div *ngFor="let attachment of additionalFiles; let index = index">
                <a (click)="onFileClicked('attachment', index)">
                  <jrs-course-progress [chapterIndex]="index" [currentFile]="attachment">
                  </jrs-course-progress>
                </a>
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
                <span class="author float-right">{{timedCourseAuthor}}</span>
                <span class="divider float-right"> | </span>
                <span class="date float-right date-margin">{{timedCourseModifiedDate}}</span>
              </div>
              <div class="description">
                <span [innerHTML]="timedCourseDescription | linkify"></span>
              </div>
            </jrs-tab>

            <jrs-tab [label]="CHAPTERS_HEADER">
              <div *ngFor="let progressData of timedCourseProgressData; let i = index">
                <a tabindex="{{i}}" (click)="onChapterClicked(i)"
                   [disabled]="getDisabledChapterValue(progressData, i)">
                  <jrs-course-progress *ngIf="moveChapters"
                                       [chapterIndex]="i"
                                       [noProgress]="scos[i].isTest || scos[i].isQuestionnaire"
                                       [showCheckMark]="isTestCompleted(scos[i]) || ((isQuestionnaireCompleted(scos[i])) && hasCompletedChapters())"
                                       [progressData]="progressData"
                                       [selectedChapter]="scos[currentIndex].scoName == progressData.moduleName"
                                       [unLocked]="!getDisabledChapterValue(progressData, i)">
                  </jrs-course-progress>
                </a>
              </div>
            </jrs-tab>

            <jrs-tab *ngIf="currentFiles && currentFiles.length > 0" [label]="CURRENT_FILES_TITLE">
              <div class="file-background" *ngFor="let currentFile of currentFiles; let index = index">
                <a (click)="onFileClicked('currentFile', index)">
                  <jrs-course-progress [chapterIndex]="index" [currentFile]="currentFile">
                  </jrs-course-progress>
                </a>
              </div>
            </jrs-tab>

            <jrs-tab *ngIf="additionalFiles && additionalFiles.length > 0" [label]="ADDITIONAL_FILES_TITLE">
              <div class="file-background" *ngFor="let attachment of additionalFiles; let index = index">
                <a (click)="onFileClicked('attachment', index)">
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
  styleUrls: ['./timed-course-launch-page.component.scss']
})
export class TimedCourseLaunchPageComponent implements OnInit, OnDestroy, CheckComponent {
  @ViewChild('scormRuntime') scormRuntime: ScormRuntimeComponent;
  data: Map<string, RuntimeDataModel> = new Map<string, RuntimeDataModel>();
  timedCourseProgressData: TimedCourseProgress[] = []
  scos: ScoModel[];
  launchUrl: string;
  timedCourseTitle: string;
  timedCourseAuthor: string;
  timedCourseModifiedDate: string;
  timedCourseDescription: string;
  moveChapters = false;
  canAccess: boolean;
  fileSrc: any;
  isVideo: boolean;
  startTime: Date;
  courseId: string;
  isContinue: boolean;
  igniteConfetti: boolean;
  hasCompletedSingleChapter: boolean;
  isTestSaved: boolean;
  autoSave: NodeJS.Timeout;
  loading: boolean;
  saveRuntimeDataLoader: boolean;
  isOnFocus: boolean;
  expandCurrentFile: boolean;
  expandAttachment: boolean;
  isAttachment: boolean;
  logoutCalled: boolean;
  saveScormDataSubscription: Subscription;
  expandProgress = true;
  isMobile: boolean;
  timedCourseId: string;
  timedCourseProperties: TimedCourseModel;
  errorMessage: string;

  currentIndex = 0;
  isImage = false;
  isPdf = false;
  currentFiles: FileAttachment[] = [];
  additionalFiles: FileAttachment[] = [];
  CHAPTERS_HEADER = this.sb.constants.CHAPTERS_HEADER;
  DESCRIPTION_HEADER = this.sb.constants.DESCRIPTION_HEADER;
  COURSE_INTRODUCTION = this.sb.constants.COURSE_INTRODUCTION;
  CURRENT_FILES_TITLE = this.sb.constants.CURRENT_FILES;
  ADDITIONAL_FILES_TITLE = this.sb.constants.ATTACHMENT_FILES;
  DEFAULT_ERROR_MESSAGE = 'Couldn\'t load page, try reloading page';
  AUTOSAVE_INTERVAL = 15000;
  TEST_COMPLETION_MSG = '';



  constructor(@Inject('baseUrl') private baseUrl: string,
    private sb: TimedCourseSandboxService,
    private cd: ChangeDetectorRef,
    private route: ActivatedRoute,
    private router: Router,
    private zone: NgZone) {
    this.getResponsiveSize()
    this.launchUrl = this.getLaunchUrl('loading-page.html');
    this.canAccess = sb.role === UserRoleProperties.adminRole.id;
  }

  ngOnInit(): void {
    this.getResponsiveSize();
    this.startTime = new Date();
    this.route.paramMap.subscribe(params => {
      this.timedCourseId = params.get('id');
      this.isContinue = params.get('isContinue') == 'true';
      if (this.isContinue == true) {
        this.openContinueCourseDialog();
      } else {
        this.loadPage();
      }
    });
    this.sb.getTimedCourseById(this.timedCourseId).subscribe(res => {
      this.timedCourseProperties = res;
      this.timedCourseTitle = this.timedCourseProperties.name;
      this.timedCourseDescription = this.timedCourseProperties.description ? this.timedCourseProperties.description : '';
      this.timedCourseAuthor = this.timedCourseProperties.author;
      this.timedCourseModifiedDate = DateFormatter.toISODateString(this.timedCourseProperties.modifiedDate);
    })
    this.saveScormDataSubscription = this.sb.onScormSaveDataCalled().subscribe(() => {
      this.logoutCalled = true;
      this.onScormSaveDataCalled();
    })
  }

  ngOnDestroy(): void {
    this.disableAutoSave();
    if (!this.logoutCalled) {
      if (this.scos) {
        if (!this.scos[this.currentIndex].isTest) {
          this.setSessionTime();
        }
      }
      if (this.scormRuntime) {
        this.scormRuntime.SetValue('cmi.exit', 'suspend');
        this.scormRuntime.Terminate('');
        this.saveRuntimeData(this.data);
      }
    }
    this.saveScormDataSubscription.unsubscribe();
  }

  propertiesLoaded(): void {
  }

  checkBeforeUnloadEvent(event): void {
    if (event) {
      this.saveRuntimeDataLoader = true;
      this.saveBeforeUnloadData(this.data)
    }
  }

  canDeactivate(): boolean {
    return !(!this.isTestSaved && this.scos[this.currentIndex].isTest && !this.canAccess);
  }

  goBack(): void {
    const data: NavigationExtras = {
      queryParams: {
        categoryData: this.timedCourseProperties.categoryId,
        courseId: this.timedCourseProperties.id
      }
    }
    this.sb.getRouterService().navigate(['/timed-course/container'], data);
  }

  onCommitted(event: Map<string, RuntimeDataModel>): void {
    this.saveRuntimeData(event);
  }

  closeProgressSection(): void {
    this.moveChapters = !this.moveChapters;
  }

  collectProgressData(scos: ScoModel[]): TimedCourseProgress[] {
    const result: TimedCourseProgress[] = [];
    for (const sco of scos) {
      result.push({
        moduleName: sco.scoName,
        progress: parseFloat(sco.runtimeData.get('cmi.progress_measure').data)
      })
    }
    return result;
  }

  onChapterClicked(index: number): void {
    this.isAttachment = false;
    const sco = this.scos[index];
    this.isTestSaved = sco.isTest;
    if (sco.isTest) {
      this.isTestSaved = false;
    }
    if ((sco.isTest || sco.isQuestionnaire) && !this.hasCompletedSingleChapter) {
      return;
    }
    if (sco.isTest && this.hasCompletedSingleChapter && sco.runtimeData.get('cmi.completion_threshold').data !== 'unknown' &&
      parseInt(sco.runtimeData.get('cmi.completion_threshold').data, 10) <= 1) {
      this.isTestSaved = false;
      this.onTestSelected(index);
    } else if (!this.canAccess && this.scos[this.currentIndex].isTest && this.scormRuntime.GetValue('cmi.suspend_data') === 'unknown') {
      if (sco.scoName === 'ТЕСТ') {
        return;
      }
      this.isTestSaved = true;
      this.onTestExit(() => this.switchChapter(index));
    } else {
      this.switchChapter(index);
    }
  }

  onFileClicked(attachmentType: string, index: number): void {
    const config = new DialogConfig();
    config.outsideClick = false;
    config.background = false;
    config.width = 'auto'
    this.isAttachment = true;
    let fileType: string;
    if (attachmentType == 'currentFile' && this.currentFiles[index] !== undefined) {
      fileType = this.fileType(this.currentFiles[index].type.toLowerCase());
      this.fileSrc = this.baseUrl + 'alfresco/Courses/' + this.timedCourseId + '/Course Content/Attachments/' + this.currentFiles[index].id + fileType + '#toolbar=0';
      config.data = {
        courseName: this.currentFiles[index].name,
        source: this.fileSrc
      }
    } else {
      if (this.additionalFiles[index] !== undefined) {
        fileType = this.fileType(this.additionalFiles[index].type.toLowerCase());
        this.fileSrc = this.baseUrl + 'alfresco/Courses/' + this.timedCourseId + '/Course Content/Attachments/' + this.additionalFiles[index].id + ((fileType === '.docx') ? '.pdf' : fileType) + '#toolbar=0';
        config.data = {
          courseName: this.additionalFiles[index].name,
          source: this.fileSrc,
        }
      }
    }
  }

  getDisabledChapterValue(progressData: TimedCourseProgress, i: number): boolean {
    if (!this.scos[i]) {
      return true;
    }
    return !this.hasCompletedChapters() && (this.scos[i].isTest || this.scos[i].isQuestionnaire);
  }

  isTestCompleted(scoModel: ScoModel): boolean {
    if (scoModel.isTest) {
      return scoModel.runtimeData.get('cmi.completion_status').data === 'completed';
    }
    return false;
  }

  isQuestionnaireCompleted(scoModel: ScoModel): boolean {
    if (scoModel.isQuestionnaire) {
      return scoModel.runtimeData.get('cmi.completion_status').data === 'completed'
    }
    return false;
  }

  hasCompletedChapters(): boolean {
    for (let i = 0; i < this.scos.length; i++) {
      if (this.scos[i].isTest || this.scos[i].isQuestionnaire) {
        continue;
      }

      const data = this.scos[i].runtimeData;
      const completionStatus = data.get('cmi.completion_status');

      if (completionStatus.data === 'completed') {
        return true;
      }
    }

    return false;
  }

  loadPage(): void {
    if (this.timedCourseId) {
      this.sb.getTimedCourseById(this.timedCourseId).toPromise().then(res => {
        this.timedCourseProperties = res;
        this.timedCourseTitle = this.timedCourseProperties.name;
        this.timedCourseDescription = this.timedCourseProperties.description;
        this.timedCourseAuthor = this.timedCourseProperties.author;
        this.timedCourseModifiedDate = DateFormatter.toISODateString(this.timedCourseProperties.modifiedDate);
        this.sb.getScoData(this.timedCourseId).subscribe(response => {
          this.scos = response;
          if (!this.hasCompletedAllChapters()) {
            this.triggerAutoSave();
          }
          let lastChapterIndex = 0;
          if (this.isContinue) {
            lastChapterIndex = this.getLastChapterIndex(this.scos);
          } else {
            this.resetChapters(this.scos);
          }
          if (this.scos[lastChapterIndex].isTest) {
            this.isTestSaved = true;
          }
          this.currentIndex = lastChapterIndex;
          this.data = this.scos[lastChapterIndex].runtimeData;
          this.timedCourseProgressData = this.collectProgressData(this.scos);
          this.launchUrl = this.getLaunchUrl(this.scos[lastChapterIndex].path);
          this.hasCompletedSingleChapter = this.hasCompletedChapters();
        }, (() => {
          this.errorMessage = this.DEFAULT_ERROR_MESSAGE;
        }))
      }).catch(() => {
        this.errorMessage = this.DEFAULT_ERROR_MESSAGE;
      })
      this.sb.getTimedCourseStructure(this.timedCourseId).subscribe((model: TimedCourseStructure) => {
        this.currentFiles = [];
        this.additionalFiles = [];
        for (const attachment of model.originalContent) {
            this.currentFiles.push(attachment);
        }
        for (const additionalFile of model.attachments) {
            this.additionalFiles.push(additionalFile);
        }
      })
    }
  }

  onDataChanged(event: any): void {
    if (event.cmiElement === 'cmi.progress_measure') {
      this.timedCourseProgressData[this.currentIndex].progress = event.value;
      this.cd.detectChanges();
    } else if (event.cmiElement == 'cmi.success_status') {
      const isPassed = this.scormRuntime.GetValue('cmi.success_status') === 'passed'
      if (isPassed) {
        this.igniteConfetti = true;
        this.TEST_COMPLETION_MSG = this.sb.constants.TEST_COMPLETION_MSG;
        this.stopConfetti();
        this.isTestSaved = true;
      }
    } else if (event.cmiElement === 'cmi.score.raw') {
      this.onScoreChanged();
    } else if (event.cmiElement === 'cmi.completion_status' && this.scormRuntime.GetValue('cmi.completion_status') === 'completed') {
      this.hasCompletedSingleChapter = true;
      if (this.hasCompletedAllChapters()) {
        this.disableAutoSave();
      }
      this.cd.detectChanges();
    } else if (event.cmiElement === 'cmi.comments_from_learner.1.comment') {
      this.zone.run(() => {
        const config = new DialogConfig();
        config.outsideClick = true;
        config.title = this.sb.constants.SENT_SUCCESS_MSG;
        config.decline = false;
        config.submitButton = this.sb.constants.NOTIFY_DIALOG_SUBMIT_BTN_TEXT;
        config.data = {
          info: (this.sb.constants.SENT_SURVEY_WARNING_MSG)
        }
        const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
        dialogRef.afterClosed.subscribe(res => {
          if (res) {
            this.sb.getRouterService().navigate(['/timed-course/container', {navigate: true}], {relativeTo: this.route})
          }
        })
      })
    }
  }

  resetChapters(scos: ScoModel[]): void {
    for (const sco of scos) {
      if (sco.isTest || sco.isQuestionnaire) {
        continue;
      }

      const cmiLocation = sco.runtimeData.get('cmi.location');
      cmiLocation.data = '0';

      const suspendData = sco.runtimeData.get('cmi.suspend_data');

      if (suspendData.data != null && suspendData.data !== '0' && suspendData.data !== 'unknown') {
        const splitByComma = suspendData.data.split(',');

        let newSuspendData = '';

        splitByComma.forEach(item => {
          const splitByEquals = item.split('=');

          if (splitByEquals[0] !== 'textProgressWeight' && splitByEquals[0] !== 'endPage') {
            const videoName = splitByEquals[0];
            const splitByVerticalBar = splitByEquals[1].split('|');
            const progressMeasure = parseFloat(splitByVerticalBar[1]);
            const progressWeight = parseFloat(splitByVerticalBar[2]);
            const isCompleted = (splitByVerticalBar[3]) === 'true';
            newSuspendData += videoName + '=0|' + progressMeasure + '|' + progressWeight + '|' + isCompleted + ',';
          } else {
            newSuspendData += item + ',';
          }
        });

        suspendData.data = newSuspendData.slice(0, -1);
      }
    }
  }

  onFullscreenClick(): void {
    const elem = document.getElementById('imgContainer');

    if (elem.requestFullscreen) {
      elem.requestFullscreen();
    }
  }

  private onScoreChanged(): void {
    const maxScore = this.scormRuntime.GetValue('cmi.score.max');
    const score = this.scormRuntime.GetValue('cmi.suspend_data');

    this.cd.detectChanges();
    this.zone.run(() => {
      const remainingAttempts = parseInt(this.data.get('cmi.completion_threshold').data, 10);
      if (remainingAttempts > 1 && score < maxScore) {
        this.isTestSaved = false;
        const config = new DialogConfig();
        config.outsideClick = true;
        config.submitButton = this.sb.constants.CONFIRM_SUBMIT_BTN_TEXT;
        config.data = {
          info: '\nТаны сорилын оноо ' + score + '/' + maxScore + ' Та үлдсэн оролдлогоо ашиглах уу?'
        }
        const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
        dialogRef.afterClosed.subscribe(res => {
          if (res) {
            this.scormRuntime.Terminate('');
            const currentSco = this.scos[this.currentIndex];
            currentSco.runtimeData = this.data;
            this.launchUrl = this.getLaunchUrl('loading-page.html');
            this.sb.saveRuntimeData(this.timedCourseProperties.contentId, currentSco).toPromise().then((res: Map<string, RuntimeDataModel>) => {
              currentSco.runtimeData = this.canAccess ? currentSco.runtimeData : res;
              this.data = this.scos[this.currentIndex].runtimeData;
              setTimeout(() => {
                this.launchUrl = this.getLaunchUrl(this.scos[this.currentIndex].path);
                this.cd.detectChanges();
              }, 1000);
            })
          } else {
            const nextChapter = this.currentIndex + 1;
            if (!this.scos[nextChapter]) {
              this.sb.getRouterService().navigate(['timed-course/container'], {relativeTo: this.route})
            } else {
              this.onChapterClicked(nextChapter);
            }
            this.stopConfetti();
          }
        })
      } else {
        const config = new DialogConfig();
        config.outsideClick = true;
        config.decline = false;
        config.submitButton = this.sb.constants.CONFIRM_SUBMIT_BTN_TEXT;
        config.data = {
          info: (this.TEST_COMPLETION_MSG + `\nТаны сорилын оноо ${score} / ${maxScore}`)
        }
        const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
        this.afterDialogClose(dialogRef)
      }
    })
  }

  private afterDialogClose(dialogRef): void {
    dialogRef.afterClosed.subscribe(() => {
      const nextChapter = this.currentIndex + 1;
      if (!this.scos[nextChapter]) {
        if (!this.scos[this.currentIndex].isTest) {
          this.setSessionTime();
        }
        this.sb.getRouterService().navigate(['/timed-course/container'], {relativeTo: this.route})
      } else {
        this.onChapterClicked(nextChapter);
      }
      this.stopConfetti();
    })
  }

  private setSessionTime(): void {
    const endTime = new Date();
    const scormTime = ScormTimeUtil.convertToSCORMTime(endTime.getTime() - this.startTime.getTime());
    if(this.scormRuntime) {
      this.scormRuntime.SetValue('cmi.session_time', scormTime);
    }
  }

  private getLaunchUrl(path: string): string {
    return this.baseUrl + '/alfresco/' + path;
  }

  private hasCompletedAllChapters(): boolean {
    for (const i of this.scos) {
      if (i.isTest || i.isQuestionnaire || i.isSurvey) {
        continue;
      }
      const data = i.runtimeData;
      const completionStatus = data.get('cmi.completion_status');
      if (completionStatus) {
        if (completionStatus.data !== 'completed') {
          return false;
        }
      }
    }
    return true;
  }

  private disableAutoSave(): void {
    clearInterval(this.autoSave);
  }

  private async saveRuntimeData(data: Map<string, RuntimeDataModel>, hideLoader?: boolean): Promise<void> {
    if (!this.scos[this.currentIndex].isTest) {
      this.setSessionTime();
    }
    const currentSco = this.scos[this.currentIndex];
    currentSco.runtimeData = data;
    if (!hideLoader) {
      this.loading = true;
    }
    if (!this.canAccess) {
      if (this.saveRuntimeDataLoader) {
        this.sb.openSnackbar(this.sb.constants.SAVE_DATA_LOADER_SUCCESS_MSG, true);
      }
      await this.sb.saveRuntimeData(this.timedCourseProperties.contentId, currentSco).toPromise().then((res: Map<string, RuntimeDataModel>) => {
        currentSco.runtimeData = this.canAccess ? currentSco.runtimeData : res;
        this.startTime = new Date();
        this.loading = false;
        this.saveRuntimeDataLoader = false;
      }, () => {
        this.loading = false;
        if (this.saveRuntimeDataLoader) {
          this.sb.openSnackbar(this.sb.constants.SAVE_DATA_LOADER_ERROR_MSG, false);
        }
        this.saveRuntimeDataLoader = false;
      })
    }
  }

  private async saveBeforeUnloadData(data: Map<string, RuntimeDataModel>): Promise<void> {
    const currentSco = this.scos[this.currentIndex];
    if (!currentSco.isTest && !currentSco.isQuestionnaire && !this.canAccess) {
      this.saveRuntimeData(data)
    }
  }

  private triggerAutoSave(): void {
    window.addEventListener('blur', () => {
      if (document.activeElement === document.getElementById('scormContentFrame')) {
        window.focus();
      } else {
        this.isOnFocus = false;
      }
    });
    window.addEventListener('focus', () => {
      this.isOnFocus = true;
    })
    this.autoSave = setInterval(() => {
      if (this.isOnFocus) {
        this.saveRuntimeData(this.data, true);
      }
    }, this.AUTOSAVE_INTERVAL);
  }

  private getLastChapterIndex(scos: ScoModel[]): number {
    for (let index = 0; index < scos.length; index++) {
      const runtimeData = scos[index].runtimeData;
      const exitStatus = runtimeData.get('cmi.exit').data;
      if (exitStatus == 'suspend') {
        return index;
      }
    }
    return 0;
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

  private onTestSelected(index: number): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = this.sb.constants.CONFIRM_DIALOG_TITLE;
    config.decline = false;
    config.submitButton = this.sb.constants.WARNING_DIALOG_SUBMIT_BTN_TEXT;
    config.data = {
      info: (this.sb.constants.CONFIRM_DIALOG_MSG_ON_ENTER)
    }
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(() => {
      this.switchChapter(index, true)
    })
  }

  private onTestExit(callback: () => void): void {
    const successStatus = this.data.get('cmi.success_status');
    const remainingAttempts = parseInt(this.data.get('cmi.completion_threshold').data, 10);
    if (successStatus.data === 'failed' || successStatus.data === 'passed') {
      callback();
      return;
    } else if (remainingAttempts === 1) {
      this.scormRuntime.SetValue('cmi.success_status', 'failed');
    }
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = this.sb.constants.CONFIRM_DIALOG_TITLE;
    config.submitButton = this.sb.constants.CONFIRM_SUBMIT_BTN_TEXT;
    config.data = {
      info: (this.sb.constants.CONFIRM_DIALOG_MSG_ON_EXIT)
    }
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(res => {
      if (res) {
        callback();
      }
    })
  }

  private switchChapter(index: number, notToSave?: boolean): void {
    this.scormRuntime.Terminate('');
    if (!notToSave) {
      this.saveRuntimeData(this.data);
    }
    this.currentIndex = index;
    this.data = this.scos[index].runtimeData;
    this.launchUrl = this.getLaunchUrl(this.scos[index].path);
    this.cd.detectChanges();
  }

  private onScormSaveDataCalled(): void {
    const endTime = new Date();
    const scormTime = ScormTimeUtil.convertToSCORMTime(endTime.getTime() - this.startTime.getTime());
    this.scormRuntime.SetValue('cmi.session_time', scormTime);
    this.scormRuntime.SetValue('cmi.exit', 'suspend');
    this.scormRuntime.Terminate('');
    const currentSco = this.scos[this.currentIndex];
    currentSco.runtimeData = this.data;
    this.loading = true;
    this.sb.saveRuntimeData(this.timedCourseProperties.contentId, currentSco).subscribe(((res: Map<string, RuntimeDataModel>) => {
      currentSco.runtimeData = this.canAccess ? currentSco.runtimeData : res;
      this.loading = false;
      this.sb.notifyScormDataSent();
    }));
  }

  private openContinueCourseDialog(): void {
    setTimeout(() => {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.title = this.sb.constants.TIMED_COURSE_CONTINUE_DIALOG_TITLE;
      config.data = {
        info: (this.sb.constants.COURSE_CONTINUE_DIALOG_MSG)
      };

      this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
        this.isContinue = !!res;
        this.loadPage();
      })
    }, 1000);
  }

  private stopConfetti(): void {
    setTimeout(() => {
      this.igniteConfetti = false;
    }, 5000)
  }

  private getResponsiveSize() {
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
}
