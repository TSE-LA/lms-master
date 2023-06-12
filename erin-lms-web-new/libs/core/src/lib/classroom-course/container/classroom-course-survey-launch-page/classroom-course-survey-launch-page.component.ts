import {ChangeDetectorRef, Component, NgZone, OnDestroy, OnInit, ViewChild} from "@angular/core";
import {ScormRuntimeComponent} from "../../../scorm/component/scorm-runtime.component";
import {ActivatedRoute, Router} from "@angular/router";
import {RuntimeDataModel, ScoModel} from "../../../scorm/model/runtime-data.model";
import {ScormTimeUtil} from "../../../scorm/util/scorm-time.util";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {CourseProgress} from "../../../common/common.model";
import {Subscription} from "rxjs";
import {ClassroomCourseSandboxService} from "../../classroom-course-sandbox.service";
import {ClassroomCourseUtil} from "../../model/classroom-course-util";
import {SCORM_CMI_COMPLETION, SURVEY_SUCCESSFUL_SENT_MESSAGE, SURVEY_WARNING_MESSAGE} from "../../model/classroom-course.constants";
import {DialogService} from "../../../../../../shared/src/lib/dialog/dialog.service";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";

@Component({
  template: `
    <jrs-button
      [iconName]="'arrow_back_ios'"
      [iconColor]="'secondary'"
      [noOutline]="true"
      [isMaterial]="true"
      [size]="'medium'"
      [bold]="true"
      [textColor]="'text-link'"
      (clicked)="navigateToLaunch()">БУЦАХ
    </jrs-button>

    <jrs-section [height]="'calc(100% - 132px)'" [width]="'100%'" [color]="'transparent'">
      <jrs-header-text  [size]="'medium'">{{courseName}}</jrs-header-text>
      <scorm-runtime
        #scormRuntime
        [data]="data"
        [launchUrl]="launchUrl"
        [fullPage]="true"
        (dataChanged)="onDataChanged($event)"
        (committed)="onCommitted($event)">
      </scorm-runtime>
    </jrs-section>
    <jrs-page-loader [show]="loading || saving" [text]="'Ачааллаж байна...'"></jrs-page-loader>
  `
})
export class ClassroomCourseSurveyLaunchPageComponent implements OnInit, OnDestroy {
  @ViewChild('scormRuntime') scormRuntime: ScormRuntimeComponent;
  data: Map<string, RuntimeDataModel> = new Map<string, RuntimeDataModel>();
  launchUrl: string;
  courseName: string;
  loading = false;
  saving = false;
  private courseProgressData: CourseProgress[] = [];
  private startTime: Date;
  private courseId: string;
  private scos: ScoModel[];
  private saveScormDataSubscription: Subscription;
  private logoutCalled = false;
  private currentIndex = 0;

  constructor(
    public router: Router,
    private sb: ClassroomCourseSandboxService,
    private route: ActivatedRoute,
    public dialog: DialogService,
    private zone: NgZone,
    private cd: ChangeDetectorRef) {
    this.launchUrl = ClassroomCourseUtil.getLaunchUrl('loading-page.html');
  }

  ngOnInit() {
    this.startTime = new Date();
    this.route.paramMap.subscribe(params => {
      this.courseId = params.get('id');
      this.loadPage();
    });


    this.saveScormDataSubscription = this.sb.onScormSaveDataCalled().subscribe(() => {
      this.logoutCalled = true;
      this.onScormSaveDataCalled();
    }, () => {
      this.sb.openSnackbar("Үнэлгээ хадгалахад алдаа гарлаа!");
    });
  }

  ngOnDestroy(): void {
    if (!this.logoutCalled) {
      this.scormRuntime.SetValue('cmi.exit', 'suspend');
      this.scormRuntime.Terminate('');
    }
    this.saveScormDataSubscription.unsubscribe();
  }

  loadPage(): void {
    this.loading = true;
    this.sb.getClassroomCourseById(this.courseId).subscribe(course => {
      this.courseName = course.name;
      this.sb.getScoData(this.courseId).subscribe(scorm => {
        this.scos = scorm;
        const lastChapterIndex = 0;
        this.currentIndex = lastChapterIndex;
        this.data = this.scos[lastChapterIndex].runtimeData;
        if (this.data.get(SCORM_CMI_COMPLETION).data != "completed") {
          this.zone.run(() => {
            const config = new DialogConfig();
            config.width = "400px";
            config.data = {info: SURVEY_WARNING_MESSAGE}
            this.dialog.open(ConfirmDialogComponent, config).afterClosed.subscribe((res) => {
              if (!res) {
                this.navigateToLaunch();
              }
            });
          });
        }
        this.courseProgressData = ClassroomCourseUtil.collectProgressData(this.scos);
        this.launchUrl = ClassroomCourseUtil.getLaunchUrl(this.scos[lastChapterIndex].path);
        this.loading = false;
      }, () => {
        this.loading = false;
      this.sb.openSnackbar("Үнэлгээний хуудас ачааллахад алдаа гарлаа!");
      });
    }, () => {
      this.loading = false;
    });
  }

  onDataChanged(event: any): void {
    if (event.cmiElement === 'cmi.progress_measure') {
      this.courseProgressData[this.currentIndex].progress = event.value;
      this.cd.detectChanges();
    } else if (event.cmiElement === 'cmi.comments_from_learner.1.comment' || (this.scos[this.currentIndex].isSurvey &&
      event.cmiElement === 'cmi.completion_status' && this.scormRuntime.GetValue('cmi.completion_status') === 'completed')) {
      this.scormRuntime.SetValue('cmi.exit', 'suspend');
      this.scormRuntime.Terminate('');
      this.saveRuntimeData(this.data);
    } else if (event.cmiElement === 'cmi.completion_status' &&
      this.scormRuntime.GetValue('cmi.completion_status') === 'completed') {
      this.cd.detectChanges();
    }
  }

  onCommitted(event: Map<string, RuntimeDataModel>): void {
    this.saveRuntimeData(event);
  }

  navigateToLaunch(): void {
    this.router.navigate(['/classroom-course/launch/' + this.courseId]);
  }

  private onScormSaveDataCalled(): void {
    const endTime = new Date();
    const scormTime = ScormTimeUtil.convertToSCORMTime(endTime.getTime() - this.startTime.getTime());
    this.scormRuntime.SetValue('cmi.session_time', scormTime);
    this.scormRuntime.SetValue('cmi.exit', 'suspend');
    this.scormRuntime.Terminate('');
    const currentSco = this.scos[this.currentIndex];
    currentSco.runtimeData = this.data;
    this.sb.notifyScormDataSent();
    this.logoutCalled = true;
    this.loading = false;
  }

  private saveRuntimeData(data: Map<string, RuntimeDataModel>): void {
    const currentSco = this.scos[this.currentIndex];
    currentSco.runtimeData = data;
    this.saving = true;
    this.sb.saveRuntimeData(this.courseId, currentSco).toPromise().then((res: Map<string, RuntimeDataModel>) => {
      this.saving = false;
      this.sb.updateClassroomSurveyStatus(this.courseId).subscribe();
      currentSco.runtimeData = res;
      this.startTime = new Date();
      this.zone.run(() => {
        const config = new DialogConfig();
        config.data = {info: SURVEY_SUCCESSFUL_SENT_MESSAGE, submitButton: "Ойлоглоо"};
        config.decline = false;
        this.dialog.open(ConfirmDialogComponent, config).afterClosed.subscribe(() => {
          this.navigateToLaunch();
        });
      });
      this.navigateToLaunch();
    }, () => {
      this.saving = false;
    });
  }
}
