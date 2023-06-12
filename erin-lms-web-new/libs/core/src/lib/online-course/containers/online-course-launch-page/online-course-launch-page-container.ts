import {ChangeDetectorRef, Component, HostListener, NgZone, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from "@angular/core";
import {OnlineCourseSandboxService} from "../../online-course-sandbox.service";
import {ScormRuntimeComponent} from "../../../scorm/component/scorm-runtime.component";
import {RuntimeDataModel, ScoModel} from "../../../scorm/model/runtime-data.model";
import {ActivatedRoute, NavigationStart, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {ScormTimeUtil} from "../../../scorm/util/scorm-time.util";
import {AbstractOnlineCourse} from "../abstract-online-course";
import {UserRoleProperties} from "../../../common/common.model";
import {EnrollmentState, OnlineCourseModel, OnlineCourseProgress,AttachmentModel} from "../../models/online-course.model";
import {DEFAULT_THUMBNAIL_URL, ONLINE_COURSE_CARD_BUTTON_TEXTS} from "../../models/online-course.constant";
import SwiperCore, {Navigation, Pagination} from "swiper";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {CertificateModel, GenerateCertificateModel} from "../../../certificate/model/certificate.model";
import {finalize} from "rxjs/operators";
import {DateFormatter} from "../../../../../../shared/src/lib/utilities/date-formatter.util";
import {FileViewDialogComponent} from "../../../../../../shared/src/lib/dialog/file-view-dialog/file-view-dialog.component";
import {
  CourseStructure,
  StructureModule
} from "../../../../../../shared/src/lib/structures/content-structure/content-structure.model";

SwiperCore.use([Pagination, Navigation]);

@Component({
  selector: 'jrs-online-course-launch-page',
  template: `
    <jrs-confetti *ngIf="igniteConfetti"></jrs-confetti>
    <jrs-achievement *ngIf="showAchievement" (finished)="onAchievementFinished()"></jrs-achievement>
    <div class="online-course-launch-page-container" browserTab (checkEvent)="checkBeforeUnloadEvent($event)">
      <div [ngClass]="moveChapters ? 'responsive-grid' : 'display-grid'">
        <div class="scorm-section">
          <div class="return">
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
            <jrs-header-text [size]="'medium'" [margin]="false" class="header-margin"
                             [class.long-text]="onlineCourseTitle.length > 50">{{onlineCourseTitle}}</jrs-header-text>
            <div class="spacer"></div>
            <div *ngIf="moveChapters" [class.display-none]="hide"
                 class="flex" (click)="showProgress()">
              <jrs-icon class="icon material-icons show-chapters-btn cursor-pointer"
                        [size]="'large'"
                        [color]="'secondary'"
                        [mat]="true">west
              </jrs-icon>

            </div>
          </div>
          <div class="scorm-runtime">
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
        </div>

        <div *ngIf="!moveChapters" class="chapters-section">
          <div class="title-container">
            <jrs-header-text class="chapters-title" [size]="'small'" [margin]="false">
              {{CHAPTERS_HEADER.toUpperCase()}}
            </jrs-header-text>
            <span class="spacer"></span>
            <jrs-icon class="icon material-icons cursor-pointer"
                      (click)="moveProgressSection()"
                      [size]="'medium'"
                      [color]="'purple-gray'"
                      [mat]="true">
              close
            </jrs-icon>
          </div>
          <div class="border-top">
            <div *ngFor="let progressData of onlineCourseProgressData; let i = index">
              <a tabIndex="{{i}}"
                 (click)="isCertificate(progressData.moduleName) ? onCertificateCalled(true) : onChapterClicked(i)"
                 [disabled]="getDisabledChapterValue(progressData, i)">
                <jrs-course-progress [chapterIndex]="i"
                                     [noProgress]="isCertificate(progressData.moduleName) || scos[i].isTest || scos[i].isSurvey"
                                     [showCheckMark]="(isCertificate(progressData.moduleName)|| isTestCompleted(scos[i]) || isSurveyCompleted(scos[i]))
                                          && hasCompletedChapters"
                                     [progressData]="progressData"
                                     [selectedChapter]="scos[currentIndex].scoName == progressData.moduleName"
                                     [unLocked]="!getDisabledChapterValue(progressData, i)">
                </jrs-course-progress>
              </a>
            </div>
          </div>
        </div>
      </div>

      <div [ngClass]="moveChapters ? 'responsive-grid' : 'display-grid'">
        <jrs-tab-group class="tab-style" [chooseFirst]="moveChapters">
          <jrs-tab *ngIf="moveChapters" [label]="CHAPTERS_HEADER" [active]="moveChapters">
            <div *ngFor="let progressData of onlineCourseProgressData; let i = index">
              <a tabIndex="{{i}}"
                 (click)="isCertificate(progressData.moduleName) ? onCertificateCalled(true) : onChapterClicked(i)"
                 [disabled]="getDisabledChapterValue(progressData, i)">
                <jrs-course-progress *ngIf="moveChapters" [chapterIndex]="i"
                                     [noProgress]="isCertificate(progressData.moduleName) || scos[i].isTest || scos[i].isSurvey"
                                     [showCheckMark]="(isCertificate(progressData.moduleName)|| isTestCompleted(scos[i]) || isSurveyCompleted(scos[i]))
                                          && hasCompletedChapters"
                                     [progressData]="progressData"
                                     [selectedChapter]="scos[currentIndex].scoName == progressData.moduleName"
                                     [unLocked]="!getDisabledChapterValue(progressData, i)">
                </jrs-course-progress>
              </a>
            </div>
          </jrs-tab>

          <jrs-tab [label]="DESCRIPTION_HEADER" [active]="!moveChapters">
            <div class="inline-flex author-and-date-section" [class.display-content]="isMobile">
              <jrs-header-text [size]="'small'" [margin]="false">
                {{COURSE_INTRODUCTION}}
              </jrs-header-text>
              <span class="spacer"></span>
              <span class="author float-right">{{onlineCourseAuthor}}</span>
              <span class="divider float-right"> | </span>
              <span class="date float-right date-margin">{{onlineCourseModifiedDate}}</span>
            </div>
            <div class="description-section">
              <span [innerHTML]="onlineCourseDescription | linkify"></span>
            </div>
          </jrs-tab>

          <jrs-tab [label]="OTHER_COURSE" [active]="getTabBoolean(moveChapters)">
            <swiper [slidesPerView]="mediaState"
                    [spaceBetween]="2"
                    [slidesPerGroup]="mediaState"
                    [loop]="false"
                    [keyboard]="true"
                    [mousewheel]="true"
                    [navigation]="true"
                    [class.small-swiper]="mediaState == 1"
                    class="mySwiper">

              <ng-container *ngFor="let onlineCourse of suggestedCourses">
                <ng-template swiperSlide>
                  <jrs-card
                    class="card-size"
                    [course]="onlineCourse"
                    [defaultThumbnailUrl]="defaultThumbnailUrl"
                    [buttonText]="getCardButtonText(onlineCourse.publishStatus)"
                    [rating]="3"
                    [rates]="15"
                    [state]="'New'"
                    [size]="'small'"
                    (clicked)="clicked(onlineCourse)">
                  </jrs-card>
                </ng-template>
              </ng-container>
            </swiper>
          </jrs-tab>

          <jrs-tab [label]="ATTACHMENT_FILES" [active]="getTabBoolean(moveChapters)">
            <div class="attachments" *ngFor="let attachment of attachments">
              <a style="padding-left: 15px" href="javascript:void(0)" (click)="downloadAttachment(attachment.attachmentFolderId, attachment.attachmentId, attachment.fileName)">
                {{ attachment.fileName }}
              </a>
            </div>
          </jrs-tab>
        </jrs-tab-group>
      </div>
    </div>
  `,
  styleUrls: ['./online-course-launch-page-container.scss'],
  encapsulation: ViewEncapsulation.None
})
export class OnlineCourseLaunchPageContainer extends AbstractOnlineCourse implements OnInit, OnDestroy {
  @ViewChild('scormRuntime') scormRuntime: ScormRuntimeComponent;
  data: Map<string, RuntimeDataModel> = new Map<string, RuntimeDataModel>();
  defaultThumbnailUrl = DEFAULT_THUMBNAIL_URL;
  suggestedCourses: OnlineCourseModel[] = [];
  onlineCourses: OnlineCourseModel[];
  courses: OnlineCourseModel[];
  launchUrl: string;
  onlineCourseId: string;
  startTime: Date;
  scos: ScoModel[];
  currentIndex = 0;
  categoryId: string;
  isOnFocus: boolean;
  autoSave: NodeJS.Timer;
  loading = false;
  saveRuntimeDataLoader = false;
  isContinue: boolean;
  hasCompletedSingleChapter = false;
  hasCompletedChapters = false;
  isDisabled: boolean;
  onlineCourseAuthor: string;
  onlineCourseModifiedDate: string;
  saveScormDataSubscription: Subscription;
  show: boolean;
  isSaved = false;
  isTestSaved = false;
  isTest = false;
  logoutCalled = false;
  igniteConfetti = false;
  getCourseLimit = 5;
  nextCourses: OnlineCourseModel[];
  showAchievement: boolean;
  moveChapters = false;
  openDialog: boolean;
  isMobile = false;
  hide = false;
  selectedChapter: boolean;
  onlineCourseProgressData: OnlineCourseProgress[] = [];
  CHAPTERS_HEADER = this.sb.constants.CHAPTERS_HEADER;
  DESCRIPTION_HEADER = this.sb.constants.DESCRIPTION_HEADER;
  ATTACHMENT_FILES = this.sb.constants.ATTACHMENT_FILES;
  COURSE_INTRODUCTION = this.sb.constants.COURSE_INTRODUCTION;
  OTHER_COURSE = this.sb.constants.OTHER_COURSE;

  AUTOSAVE_INTERVAL = 15000;
  TEST_COMPLETION_MSG = '';
  mediaState = 3;
  attachments:AttachmentModel[]=[];
  downloading: boolean;
  private surveySent: boolean;
  private testAnswer: boolean;

  constructor(sb: OnlineCourseSandboxService,
    private zone: NgZone,
    private domSanitizer: DomSanitizer,
    private router: Router,
    private route: ActivatedRoute,
    private cd: ChangeDetectorRef) {
    super(sb);
    this.launchUrl = this.getLaunchUrl('loading-page.html');
    this.isOnFocus = true;
    router.events.forEach(event => {
      if (event instanceof NavigationStart) {
        this.getResponsiveSize();
      }
    })
  }

  ngOnInit() {
    this.getResponsiveSize();
    this.startTime = new Date();
    this.route.paramMap.subscribe(params => {
      this.onlineCourseId = params.get('id');
      this.openDialog = params.get('isContinue') === 'true';
      if (this.openDialog == true) {
        this.openCourseContinueDialog();
      } else {
        this.loadPage();
      }
    })
    this.saveScormDataSubscription = this.sb.onScormSaveDataCalled().subscribe(() => {
      this.logoutCalled = true;
      this.onScormSaveDataCalled();
    }, () => {
      this.loading = false;
    })
  }

  ngOnDestroy(): void {
    this.disableAutoSave();
    if (!this.logoutCalled) {
      if (!this.scos[this.currentIndex].isTest) {
        this.setSessionTime();
      }
      this.scormRuntime.SetValue('cmi.exit', 'suspend');
      this.scormRuntime.Terminate('');
      if (!this.scos[this.currentIndex].isSurvey || !this.surveySent) {
        this.saveRuntimeData(this.data);
      }
    }
    this.saveScormDataSubscription.unsubscribe();
  }


  downloadAttachment(attachmentFolderId: string, attachmentId: string, fileName: string): void {
    this.downloading = true;
    this.sb.downloadAttachment(attachmentFolderId, attachmentId, fileName).subscribe(
      () => this.downloading = false,
      () => {
        this.downloading = false;
        this.sb.openSnackbar("Хавсралт татахад алдаа гарлаа.");
      });
  }

  checkBeforeUnloadEvent(event) {
    if (event) {
      this.saveRuntimeDataLoader = true;
      this.saveBeforeUnloadData(this.data);
    }
  }

  goBack(): void {
    this.sb.navigateByUrl('online-course/container')
  }

  clicked(onlineCourse: OnlineCourseModel): void {
    this.loading = true;
    this.saveRuntimeData(this.data);
    if (onlineCourse.publishStatus === 'PUBLISHED') {
      this.sb.role == UserRoleProperties.adminRole.id && onlineCourse.enrollmentState == EnrollmentState.IN_PROGRESS ?
        this.sb.navigateByUrl('/online-course/launch/' + onlineCourse.id + '/' + true) :
        this.sb.navigateByUrl('/online-course/launch/' + onlineCourse.id + '/' + false);
      this.cd.detectChanges();
      this.loading = false;
    }
  }

  @HostListener('document:click', ['$event'])
  documentClick(event: Event): void {
    this.showAchievement = false;
  }

  propertiesLoaded(): void {
  }

  canDeactivate(): boolean {
    return !(!this.isTestSaved && this.scos[this.currentIndex].isTest);
  }

  getCourseSuggestions(): void {
    this.sb.getOnlineCourses(this.categoryId, 'all', 'all')
      .subscribe((onlineCourses: OnlineCourseModel[]) => {
        this.suggestedCourses = onlineCourses.filter(course => course.id !== this.onlineCourseProperties.id);
      })
  }

  toggleDropdown(index: number): void {
    if (this.currentIndex === index) {
      this.show = !this.show;
    } else {
      this.show = true;
    }
    this.currentIndex = index;
  }

  getCardButtonText(publishStatus: string): string {
    return ONLINE_COURSE_CARD_BUTTON_TEXTS[publishStatus];
  }

  loadPage(): void {
    this.sb.getOnlineCourseById(this.onlineCourseId).subscribe(res => {
      this.categoryId = res.categoryId;
      this.onlineCourseProperties = res;
      this.onlineCourseTitle = this.onlineCourseProperties.name
      this.onlineCourseAuthor = this.onlineCourseProperties.author;
      this.onlineCourseModifiedDate = DateFormatter.toISODateString(this.onlineCourseProperties.modifiedDate);
      this.onlineCourseDescription = this.onlineCourseProperties.description ? this.onlineCourseProperties.description : '';
      this.sb.getScoData(this.onlineCourseId).subscribe(scorm => {
        this.sb.getOnlineCourseStructure(this.onlineCourseId).subscribe((structure: CourseStructure) => {
          this.scos = scorm;
          if (!this.hasCompletedAllChapters()) {
            this.triggerAutoSave();
          }
          let lastChapterIndex = 0;
          if (this.isContinue) {
            lastChapterIndex = this.getLastChapterIndex(this.scos);
          } else {
            this.resetChapters(this.scos);
          }
          this.currentIndex = lastChapterIndex;
          this.data = this.scos[lastChapterIndex].runtimeData;
          this.onlineCourseProgressData = this.collectProgressData(this.scos, structure.modules);
          if (this.onlineCourseProperties.hasCertificate) {
            this.onlineCourseProgressData.push({moduleName: 'СЕРТИФИКАТ', progress: 0});
          }
          this.launchUrl = this.getLaunchUrl(this.scos[lastChapterIndex].path);
          this.hasCompletedSingleChapter = this.hasCompletedChapter();
          this.hasCompletedChapters = this.hasCompletedAllChapters();
          this.isDisabled = this.data.get("cmi.completion_status").data === 'completed';
          this.loading = false;
        }, (() => {
          this.loading = false;
          this.errorMessage = OnlineCourseLaunchPageContainer.DEFAULT_ERROR_MESSAGE;
        }));
        this.getCourseSuggestions();
      })
      this.sb.getAttachment(this.onlineCourseId).subscribe((res: AttachmentModel[]) => {
        this.attachments = res;
      });
    }, (() => {
      this.loading = false;
      this.errorMessage = OnlineCourseLaunchPageContainer.DEFAULT_ERROR_MESSAGE;
    }))
  }

  showProgress(): void {
    this.moveChapters = false;
  }

  triggerAutoSave(): void {
    window.addEventListener("blur", () => {
      if (document.activeElement === document.getElementById('scormContentFrame')) {
        window.focus();
      } else {
        this.isOnFocus = false;
      }
    });
    window.addEventListener("focus", () => {
      this.isOnFocus = true;
    })

    this.autoSave = setInterval(() => {
      if (this.isOnFocus) {
        this.saveRuntimeData(this.data, true);
      }
    }, this.AUTOSAVE_INTERVAL);
  }

  onCommitted(event: Map<string, RuntimeDataModel>): void {
    this.saveRuntimeData(event);
  }

  sanitizeUrl(url: string): SafeResourceUrl {
    return url ?
      this.domSanitizer.bypassSecurityTrustResourceUrl(this.sb.baseUrl + url) : this.sb.baseUrl + '/default-thumbnail.mp4';
  }

  onAchievementFinished(): void {
    setTimeout(() => {
      this.zone.run(() => {
        this.showAchievement = false;
      })
    }, 6000)
  }

  getTabBoolean(moveChapters): boolean {
    if (moveChapters) {
      return !moveChapters;
    }
  }

  isCertificate(moduleName: string): boolean {
    return moduleName === 'СЕРТИФИКАТ';
  }

  stopConfetti(): void {
    setTimeout(() => {
      this.igniteConfetti = false;
    }, 5000)
  }

  moveProgressSection() {
    this.moveChapters = true;
  }

  getDisabledChapterValue(progressDatum, i): boolean {
    if (!this.scos[i]) {
      return !this.hasCompletedChapters && this.isCertificate(progressDatum.moduleName);
    }
    return !this.hasCompletedChapters && (this.scos[i].isTest || this.scos[i].isSurvey);
  }

  onCertificateCalled(open: boolean): void {
    if (this.hasCompletedChapters) {
      this.sb.certificateService.getCertificateById(this.onlineCourseProperties.certificateId).subscribe((res) => {
        this.generateCertificate(res, open);
      });
    }
  }

  onDataChanged(event: any): void {
    if (event.cmiElement === 'cmi.progress_measure') {
      this.onlineCourseProgressData[this.currentIndex].progress = event.value;
      this.cd.detectChanges();
    } else if (event.cmiElement === 'cmi.success_status') {
      const isPassed = this.scormRuntime.GetValue('cmi.success_status') === 'passed';
      if (isPassed) {
        this.igniteConfetti = true;
        this.TEST_COMPLETION_MSG = this.sb.constants.TEST_COMPLETION_MSG;
        this.stopConfetti();
        this.isTestSaved = true;
      }
    } else if (event.cmiElement === 'cmi.score.raw') {
      this.onScoreChanged();
    } else if (event.cmiElement === 'cmi.comments_from_learner.1.comment' || (this.scos[this.currentIndex].isSurvey &&
      event.cmiElement === 'cmi.completion_status' && this.scormRuntime.GetValue('cmi.completion_status') === 'completed')) {
      this.zone.run(() => {
        const config = new DialogConfig();
        config.outsideClick = true;
        config.title = this.sb.constants.SENT_SUCCESS_MSG;
        config.decline = false;
        config.submitButton = this.sb.constants.WARNING_DIALOG_SUBMIT_BTN_TEXT;
        config.data = {
          info: (this.sb.constants.SENT_SURVEY_WARNING_MSG)
        }
        const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config)
        this.afterDialogClose(dialogRef);
      });
    } else if (event.cmiElement === 'cmi.completion_status' &&
      this.scormRuntime.GetValue('cmi.completion_status') === 'completed') {
      this.hasCompletedSingleChapter = true;
      if (!this.hasCompletedChapters) {
        this.hasCompletedChapters = this.hasCompletedAllChapters();
        this.showAchievement = this.hasCompletedChapters;
        this.autoGenerateCertificate();
        this.disableAutoSave();
      }
      this.cd.detectChanges();
    }
  }

  onChapterClicked(index: number): void {
    const sco = this.scos[index];
    this.isSaved = sco.isTest;
    if (sco.scoName === 'Үнэлгээний хуудас') {
      this.surveySent = sco.runtimeData.get("cmi.suspend_data").data.includes("singleChoice") &&
        sco.runtimeData.get("cmi.suspend_data").data.includes("multiChoice") &&
        sco.runtimeData.get("cmi.suspend_data").data.includes("fillInBlank");
    }
    if (sco.isTest) {
      this.isTestSaved = false;
    }
    if (((sco.isTest) && !this.hasCompletedChapters) || (sco.isSurvey && !this.hasCompletedChapters)) {
      return;
    }
    if (sco.isTest && this.hasCompletedChapters && sco.runtimeData.get('cmi.completion_threshold').data !== 'unknown' &&
      parseInt(sco.runtimeData.get('cmi.completion_threshold').data, 10) <= 1) {
      this.isSaved = false;
      this.onTestSelected(index);
    } else if (this.scos[this.currentIndex].isTest) {
      if (sco.scoName === 'ТЕСТ') {
        return;
      }
      this.isSaved = true;
      this.onTestExit(() => this.switchChapter(index));
    } else if (this.scos[index].isSurvey) {
      this.onSurveySelected(() => {
        this.switchChapter(index);
      });
    } else {
      this.switchChapter(index);
    }
  }

  private resetChapters(scos: ScoModel[]): void {
    for (const sco of scos) {
      if (sco.isTest || sco.isQuestionnaire || sco.isSurvey) {
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

  private collectProgressData(scos: ScoModel[], module: StructureModule[]): OnlineCourseProgress[] {
    const result: OnlineCourseProgress[] = [];
    for (let sco = 0; sco < scos.length; sco++) {
      result.push({
        moduleName: scos[sco].scoName.toUpperCase(),
        progress: parseFloat(scos[sco].runtimeData.get('cmi.progress_measure').data),
        sections: []
      });
      for (let mod = 0; mod < module.length; mod++) {
        if (sco === mod && scos[sco].scoName !== 'ҮНЭЛГЭЭНИЙ ХУУДАС' && scos[sco].scoName !== 'СЕРТИФИКАТ' && scos[sco].scoName !== 'ТЕСТ') {
          for (const sectionElement of module[mod].sections) {
            result[sco].sections.push(sectionElement);
          }
        }
      }
    }
    return result;
  }

  private getLastChapterIndex(scos: ScoModel[]): number {
    for (let index = 0; index < scos.length; index++) {
      const runtimeData = scos[index].runtimeData;
      const exitStatus = runtimeData.get('cmi.exit').data;
      if (exitStatus === 'suspend') {
        return index;
      }
    }
    return 0;
  }

  private getLaunchUrl(path: string) {
    return this.sb.baseUrl + '/alfresco/' + path;
  }

  private async saveRuntimeData(data: Map<string, RuntimeDataModel>, hideLoader?: boolean): Promise<void> {
    this.setSessionTime();
    const currentSco = this.scos[this.currentIndex];
    currentSco.runtimeData = data;
    if (!hideLoader) {
      this.loading = true
    }
    if (this.saveRuntimeDataLoader) {
      this.sb.snackbarService.open(this.sb.constants.SAVE_DATA_LOADER_SUCCESS_MSG, true)
    }
    await this.sb.saveRuntimeData(this.onlineCourseProperties.id, currentSco).toPromise().then((res: Map<string, RuntimeDataModel>) => {
      currentSco.runtimeData = this.isAdmin() ? currentSco.runtimeData : res;
      this.startTime = new Date();
      this.loading = false;
      this.saveRuntimeDataLoader = false;
    }, () => {
      this.loading = false;
      if (this.saveRuntimeDataLoader) {
        this.sb.snackbarService.open(this.sb.constants.SAVE_DATA_LOADER_ERROR_MSG, false)
      }
      this.saveRuntimeDataLoader = false;
    });
  }

  private saveBeforeUnloadData(data: Map<string, RuntimeDataModel>): void {
    const currentSco = this.scos[this.currentIndex];
    if (!currentSco.isTest && !currentSco.isSurvey && !this.isAdmin()) {
      this.saveRuntimeData(data);
    }
  }

  private setSessionTime(): void {
    const endTime = new Date();
    const scormTime = ScormTimeUtil.convertToSCORMTime(endTime.getTime() - this.startTime.getTime());
    this.scormRuntime.SetValue('cmi.session_time', scormTime);
  }

  private getResponsiveSize(): void {
    this.sb.getMediaBreakPointChange().subscribe(res => {
      const onMobileDevice = (res == "media_sm" || res == "media_s" || res == "media_xs");
      if (onMobileDevice) {
        this.isMobile = true;
        this.hide = true;
        this.moveChapters = true;
        this.mediaState = 1;
      } else {
        if (res === 'media_xxl') {
          this.mediaState = 4;
          this.moveChapters = false;
          this.isMobile = false;
          this.hide = false;
        } else if (res === 'media_xl') {
          this.mediaState = 3;
          this.moveChapters = false;
          this.isMobile = false;
          this.hide = false;
        } else if (res === 'media_lg') {
          this.mediaState = 3
          this.moveChapters = true;
          this.isMobile = false;
          this.hide = false;
        } else if (res === 'media_md') {
          this.mediaState = 3;
          this.moveChapters = true;
          this.isMobile = false;
          this.hide = true;
        } else if (res === 'media_sm') {
          this.mediaState = 2;
          this.moveChapters = true;
          this.hide = true;
        } else if (res === 'media_s') {
          this.mediaState = 1;
          this.moveChapters = true;
          this.isMobile = true;
          this.hide = true;
        }
      }
    })
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
    if (!this.surveySent) {
      this.sb.saveRuntimeData(this.onlineCourseProperties.id, currentSco)
        .subscribe(((res: Map<string, RuntimeDataModel>) => {
          currentSco.runtimeData = this.isAdmin() ? currentSco.runtimeData : res;
          this.loading = false;
          this.sb.notifyScormDataSent();
        }));
    } else {
      this.sb.notifyScormDataSent();
      this.logoutCalled = true;
      this.loading = false;
    }
  }

  private onTestExit(callback: () => void): void {
    const successStatus = this.data.get('cmi.success_status');
    const remainingAttempts = parseInt(this.data.get('cmi.completion_threshold').data, 10);
    if (successStatus.data === 'failed' || successStatus.data === 'passed') {
      callback();
      return;
    } else if (remainingAttempts === 1) {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.title = this.sb.constants.CONFIRM_DIALOG_TITLE;
      config.decline = false;
      config.submitButton = this.sb.constants.WARNING_DIALOG_SUBMIT_BTN_TEXT;
      config.data = {
        info: (this.sb.constants.CONFIRM_DIALOG_LAST_ATTEMPT_MSG_ON_EXIT)
      }
      this.sb.openDialog(ConfirmDialogComponent, config);

    } else {
      if (this.testAnswer === undefined && remainingAttempts > 1) {
        const config = new DialogConfig();
        config.outsideClick = true;
        config.title = this.sb.constants.CONFIRM_DIALOG_TITLE;
        config.data = {
          info: (this.sb.constants.CONFIRM_DIALOG_MSG_ON_EXIT)
        }
        const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
        dialogRef.afterClosed.subscribe(result => {
          if (result) {
            return callback();
          }
        })
      } else if (this.testAnswer === false) {
        this.testAnswer = undefined;
        callback();
      }
    }
  }

  private onSurveySelected(callback: () => void): void {
    const successStatus = this.data.get('cmi.success_status');
    if (successStatus.data === 'failed' || successStatus.data === 'passed') {
      callback();
      return;
    }
    if (!this.surveySent) {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.title = this.sb.constants.CONFIRM_DIALOG_TITLE;
      config.decline = false;
      config.submitButton = this.sb.constants.NOTIFY_DIALOG_SUBMIT_BTN_TEXT;
      config.data = {
        info: (this.sb.constants.CONFIRM_SURVEY_DIALOG_MSG_ON_EXIT)
      }
      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config)
      dialogRef.afterClosed.subscribe(result => {
        if (result) {
          callback();
        }
      })
    } else {
      callback();
    }
  }

  private isTestCompleted(scoModel: ScoModel): any {
    if (scoModel.isTest) {
      return scoModel.runtimeData.get('cmi.completion_status').data = 'completed';
    }
    return false;
  }

  private isSurveyCompleted(scoModel: ScoModel): boolean {
    if (scoModel.isSurvey) {
      return scoModel.runtimeData.get('cmi.completion_status').data === 'completed';
    }
    return false;
  }

  private disableAutoSave(): void {
    clearInterval(this.autoSave);
  }

  private generateCertificate(res: CertificateModel, open: boolean): void {
    const config = new DialogConfig();
    config.outsideClick = false;
    config.background = false;
    config.width = 'auto';
    config.data = {
      courseName: this.onlineCourseProperties.name,
      source: 'alfresco/Learner-Certificates/' + this.onlineCourseProperties.id + '/' + this.sb.username + '/' + 'certificate.PDF'
    }
    this.sb.certificateService.generateCertificate(res, this.sb.username, this.onlineCourseProperties.name, 'Learner-Certificates/' +
      this.onlineCourseProperties.id + '/' + this.sb.username, 'certificate', this.onlineCourseId).subscribe((certificateRes: GenerateCertificateModel) => {
      this.sb.certificateService.receiveCertificate(this.onlineCourseProperties.id, this.onlineCourseProperties.certificateId, this.sb.username, certificateRes.documentId).pipe(finalize(() => {
        if (open) {
          this.sb.openDialog(FileViewDialogComponent, config);
        }
      })).subscribe();
    }, () => {
      if (open) {
        this.sb.openDialog(FileViewDialogComponent, config);
      }
    })
  }

  private hasCompletedChapter(): boolean {
    for (const i of this.scos) {
      if (i.isTest || i.isQuestionnaire || i.isSurvey) {
        continue;
      }
      const data = i.runtimeData;
      const completionStatus = data.get('cmi.completion_status');

      if (completionStatus.data === 'completed') {
        return true;
      }
    }

    return false;
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

  private isAdmin(): boolean {
    return this.sb.role === UserRoleProperties.adminRole.id;
  }

  private async switchChapter(index: number, notToSave?: boolean): Promise<void> {
    const sco = this.scos[index];
    this.scormRuntime.Terminate('');
    if (sco.scoName === 'Үнэлгээний хуудас') {
      this.surveySent = sco.runtimeData.get("cmi.suspend_data").data.includes("singleChoice") &&
        sco.runtimeData.get("cmi.suspend_data").data.includes("multiChoice") &&
        sco.runtimeData.get("cmi.suspend_data").data.includes("fillInBlank");
    }
    if (!this.surveySent && !notToSave) {
      await this.saveRuntimeData(this.data);
    }
    this.currentIndex = index;
    this.data = this.scos[index].runtimeData;
    this.launchUrl = this.getLaunchUrl(this.scos[index].path);
    this.cd.detectChanges();
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
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.switchChapter(index, true);
      }
    })
  }

  private autoGenerateCertificate(): void {
    if (this.onlineCourseProperties.hasCertificate) {
      this.onCertificateCalled(false);
    }
  }

  private afterDialogClose(dialogRef): void {
    dialogRef.afterClosed.subscribe(() => {
      const nextChapter = this.currentIndex + 1;
      if (!this.scos[nextChapter]) {
        if (!this.scos[this.currentIndex].isTest) {
          this.setSessionTime()
        }
        this.scormRuntime.SetValue('cmi.exit', 'suspend');
        this.scormRuntime.Terminate('');
        if (!this.scos[this.currentIndex].isSurvey || !this.surveySent) {
          this.saveRuntimeData(this.data);
        }
        setTimeout(() => {
          this.igniteConfetti = true;
          this.stopConfetti();
        }, 500)
        this.ngOnInit();
      } else {
        this.onChapterClicked(nextChapter);
      }
      this.stopConfetti();
    })
  }

  private openCourseContinueDialog(): void {
    setTimeout(() => {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.title = this.sb.constants.ONLINE_COURSE_CONTINUE_DIALOG_TITLE;
      config.data = {
        info: (this.sb.constants.COURSE_CONTINUE_DIALOG_MSG)
      };

      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
      dialogRef.afterClosed.subscribe(result => {
        this.isContinue = !!result;
        this.loadPage();
      })
    }, 1000);
  }

  private onScoreChanged(): void {
    const maxScore = this.scormRuntime.GetValue('cmi.score.max');
    const score = this.scormRuntime.GetValue('cmi.suspend_data');
    this.cd.detectChanges();
    this.zone.run(() => {
      const remainingAttempts = parseInt(this.data.get('cmi.completion_threshold').data, 10);
      if (remainingAttempts > 1 && score < maxScore) {
        this.isSaved = false;
        const config = new DialogConfig();
        config.outsideClick = true;
        config.data = {
          info: ('\nТаны сорилын оноо ' + score + '/' + maxScore + ' Та үлдсэн оролдлогоо ашиглах уу?')
        }
        const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
        dialogRef.afterClosed.subscribe(result => {
          this.testAnswer = result;
          if (result) {
            this.scormRuntime.Terminate('');
            const currentSco = this.scos[this.currentIndex];
            currentSco.runtimeData = this.data;
            this.launchUrl = this.getLaunchUrl('loading-page.html');
            this.sb.saveRuntimeData(this.onlineCourseProperties.id, currentSco).toPromise().then((runtime: Map<string, RuntimeDataModel>) => {
              currentSco.runtimeData = this.isAdmin() ? currentSco.runtimeData : runtime;
              this.data = this.scos[this.currentIndex].runtimeData;
              setTimeout(() => {
                this.launchUrl = this.getLaunchUrl(this.scos[this.currentIndex].path);
                this.cd.detectChanges();
              }, 1000);
            })
          } else {
            const nextChapter = this.currentIndex + 1;
            if (!this.scos[nextChapter]) {
              this.sb.getRouterService().navigate(['/online-course/container'], {relativeTo: this.route})
            } else {
              this.onChapterClicked(nextChapter);
            }
            this.stopConfetti();
          }
        });
      } else {
        this.igniteConfetti = true
        const config = new DialogConfig();
        config.outsideClick = true;
        config.decline = false;
        config.submitButton = this.sb.constants.NOTIFY_DIALOG_SUBMIT_BTN_TEXT;
        config.data = {
          info: (this.TEST_COMPLETION_MSG + `\nТаны сорилын оноо ${score} / ${maxScore}`)
        }
        const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
        this.isTestSaved = true;
        this.afterDialogClose(dialogRef)
      }
    })
  }
}
