import {AfterViewInit, Component, OnInit} from '@angular/core';
import {DashboardSandboxService} from "../../service/dashboard-sandbox.service";
import {LearnerSuccessDashletModel, PublishedCourseCountData} from "../../dashboard-model/dashboard-model";
import {UserRoleProperties} from "../../../common/common.model";
import {GroupNode, SmallDashletModel} from "../../../../../../shared/src/lib/shared-model"
import {DashletModel} from "../../../../../../shared/src/lib/dashlets/dashlet-model";
import {ActivatedRoute} from "@angular/router";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {NAVIGATION_TEXT, NAVIGATION_TITLE} from "../../dashboard-model/dashboard-constant";
import {ReceivedCertificateModel} from "../../../certificate/model/certificate.model";

@Component({
  selector: 'jrs-dashboard-container',
  template: `
    <jrs-dashboard-frame
      *canAccess="onlineCourseCountDashletId"
      [activitiesLoading]="activitiesLoading"
      [allGroups]="allGroups"
      [publishedOnlineCourseData]="publishedOnlineCourseData"
      [publishedPromoData]="publishedPromotionData"
      [publishedCourseTypes]="onlineCourseTypes"
      [publishedPromotionTypes]="promoTypes"
      [hasTimedCourseCountDashlet]="hasTimedCourseDashlet"
      [hasOnlineCourseCountDashlet]="hasOnlineCourseDashlet"
      [selectedGroupName]="selectedGroupName"
      [smallDashletsData]="smallDashlets"
      [dateFilter]="!isEmployee"
      [publishedOnlineCourseDashlet]="publishedOnlineCourseDashlet"
      [publishedPromotionDashlet]="publishedPromotionDashlet"
      (selectedGroup)="selectGroup($event)"
      (startDate)="startDate = $event; changeDate()"
      (endDate)="endDate = $event; changeDate()"
      (navigateToActivityTable)="navigate($event)">
    </jrs-dashboard-frame>

    <jrs-dashboard-frame
      *canAccess="learnerDashletPermission"
      [onlineLearnerSuccessData]="onlineLearnerSuccessData"
      [timedLearnerSuccessData]="timedLearnerSuccessData"
      [loadingOnlineLearnerSuccess]="loadingOnlineLearnerSuccess"
      [loadingTimedLearnerSuccess]="loadingTimedLearnerSuccess"
      [hasCertificateDashlet]="hasCertificateDashlet"
      [certificateDashletData]="certificateDashletData"
      [hasTimedCourseSuccessDashlet]="hasTimedCourseSuccessDashlet"
      (getOnlineLearnerSuccessData)="getOnlineLearnerSuccessData($event)"
      (getTimedLearnerSuccessData)="getTimedLearnerSuccessData($event)">
    </jrs-dashboard-frame>
  `
})
export class DashboardContainerComponent implements OnInit, AfterViewInit {
  publishedOnlineCourseData: PublishedCourseCountData[] = [];
  publishedPromotionData: PublishedCourseCountData[] = [];
  loading: boolean;
  promoTypes = this.sb.constants.PUBLISHED_PROMOTION_COUNT_TYPES;
  onlineCourseTypes = this.sb.constants.PUBLISHED_COURSE_COUNT_TYPES;
  startDate: string;
  endDate: string;
  hasTimedCourseDashlet: boolean;
  hasOnlineCourseDashlet: boolean;
  hasOnlineCourseActivityDashlet: boolean;
  hasTimedCourseSuccessDashlet: boolean;
  loadingOnlineLearnerSuccess: boolean;
  loadingTimedLearnerSuccess: boolean;
  role: string;
  allGroups: GroupNode[];
  selectedGroupName: string;
  selectedGroupId: string;
  selectedGroup: GroupNode;
  smallDashlets: SmallDashletModel[] = [{}, {}, {}, {}];
  activitiesLoading: boolean;
  isEmployee: boolean;
  hasCertificateDashlet: boolean;
  onlineLearnerSuccessData: LearnerSuccessDashletModel;
  timedLearnerSuccessData: LearnerSuccessDashletModel;
  mediaState: number;
  certificateDashletData: ReceivedCertificateModel[] = [];

  learnerDashletPermission = 'user.dashboard.employee.dashlet';
  onlineCourseCountDashletId = 'user.dashboard.dashlet.onlineCourseCount';


  publishedOnlineCourseDashlet: DashletModel = {
    name: 'ЦАХИМ СУРГАЛТ', id: 'PUBLISHED_ONLINE_COURSE', loading: false
  };
  publishedPromotionDashlet: DashletModel = {
    name: 'УРАМШУУЛАЛ', id: 'PUBLISHED_ONLINE_COURSE', loading: false
  };


  constructor(private sb: DashboardSandboxService,
    private route: ActivatedRoute) {
    this.role = sb.role;
    if (this.role == UserRoleProperties.supervisorRole.id) {
      this.onlineCourseTypes.pop();
    }
    this.hasTimedCourseDashlet = sb.getPermissionAccess('user.dashboard.dashlet.timedCourseCount');
    this.hasOnlineCourseDashlet = sb.getPermissionAccess('user.dashboard.dashlet.onlineCourseCount');
    this.hasOnlineCourseActivityDashlet = sb.getPermissionAccess('user.dashboard.dashlet.onlineCourseActivity');
    this.hasCertificateDashlet = this.sb.getPermissionAccess('user.dashboard.dashlet.certificate');
    this.hasTimedCourseSuccessDashlet = this.sb.getPermissionAccess('user.dashboard.dashlet.timedCourseSuccess');
  }

  ngOnInit(): void {
    this.activitiesLoading = true;
    this.loadPage();
  }

  ngAfterViewInit(): void {
    this.openNavigationDialog();
  }

  loadPage(): void {
    this.getCertificateDashletData();
    this.sb.getAllGroups(false).subscribe(res => {
      this.selectedGroupId = res[0].id;
      this.selectedGroupName = res[0].name;
      this.allGroups = res;
      this.getCourseActivityData();
    })
    this.getCourseCountData();
  }

  getCourseCountData(): void {
    if (this.startDate != null && this.endDate != null) {
      if (this.hasTimedCourseDashlet && this.hasOnlineCourseDashlet) {
        this.getOnlineCourseData();
        this.getTimedCourseData();
      } else if (!this.hasTimedCourseDashlet && this.hasOnlineCourseDashlet) {
        this.getOnlineCourseData();
      }
    }
  }

  changeDate(): void {
    this.loading = true;
    this.getCourseCountData();
  }

  getOnlineCourseData(): void {
    this.publishedOnlineCourseDashlet.loading = true;
    this.sb.getOnlineCourseData(this.startDate, this.endDate, this.role).subscribe(res => {
      this.publishedOnlineCourseData = res;
      this.publishedOnlineCourseDashlet.loading = false;
    }, () => {
      this.publishedOnlineCourseDashlet.loading = false;
      this.sb.openSnackbar('Мэдээлэл авахад алдаа гарлаа.', false);
    })
  }

  getTimedCourseData(): void {
    this.publishedPromotionDashlet.loading = true;
    this.sb.getTimedCourseData(this.startDate, this.endDate, this.role).subscribe(res => {
      this.publishedPromotionData = res;
      this.publishedPromotionDashlet.loading = false;
    }, () => {
      this.publishedPromotionDashlet.loading = false;
      this.sb.openSnackbar('Мэдээлэл авахад алдаа гарлаа.', false);
    })

  }

  getCourseActivityData(): void {
    if (this.hasTimedCourseDashlet) {
      this.getTimedCourseUserActivity();
    } else {
      this.getOnlineCourseUserActivity();
    }
  }

  getOnlineCourseUserActivity(): void {
    this.sb.getOnlineCourseUserActivityData(this.selectedGroupId).subscribe((res: SmallDashletModel[]) => {
      this.smallDashlets = res;
      this.activitiesLoading = false;
    }, () => {
      this.activitiesLoading = false;
      this.sb.openSnackbar('Мэдээлэл авахад алдаа гарлаа.', false);
    });
  }

  getTimedCourseUserActivity(): void {
    this.sb.getTimedCourseUserActivityData(this.selectedGroupId, true).subscribe((res: SmallDashletModel[]) => {
      this.smallDashlets = res;
      this.activitiesLoading = false;
    }, () => {
      this.activitiesLoading = false;
      this.sb.openSnackbar('Мэдээлэл авахад алдаа гарлаа.', false);
    });
  }

  selectGroup(group: GroupNode): void {
    this.activitiesLoading = true;
    this.selectedGroup = group;
    this.selectedGroupId = group.id;
    this.getCourseActivityData();
    this.selectedGroupName = group.name;
  }

  navigate(url): void {
    this.sb.navigateByUrl(url);
  }

  getOnlineLearnerSuccessData(event: any): void {
    this.loadingOnlineLearnerSuccess = true
    this.sb.getOnlineLearnerSuccessData(event.selectedYear, event.selectedHalfYear, event.selectedGroup).subscribe(res => {
      this.onlineLearnerSuccessData = res;
      this.loadingOnlineLearnerSuccess = false;
    }, () => {
      this.loadingOnlineLearnerSuccess = false;
      this.sb.openSnackbar('Цахим сургалтын миний амжилт мэдээлэл ачааллахад алдаа гарлаа.', false);
    });
  }

  getTimedLearnerSuccessData(event: any): void {
    this.loadingTimedLearnerSuccess = true
    this.sb.getTimedLearnerSuccessData(event.selectedYear, event.selectedHalfYear, event.selectedGroup).subscribe(res => {
      this.timedLearnerSuccessData = res;
      this.loadingTimedLearnerSuccess = false;
    }, () => {
      this.loadingTimedLearnerSuccess = false;
      this.sb.openSnackbar('Урамшууллын миний амжилт мэдээлэл ачааллахад алдаа гарлаа.', false);
    });
  }

  private openNavigationDialog(): void {
    setTimeout(() => {
        this.route.url.subscribe(res => {
          const dialog = res[0].parameters.dialog;
          if (dialog == 'true') {
            const config = new DialogConfig();
            config.outsideClick = false;
            config.submitButton = "Үргэлжлүүлэх";
            config.declineButton = "Буцах";
            config.title = NAVIGATION_TITLE;
            config.data = {
              info: NAVIGATION_TEXT
            }
            this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
              if (!res) {
                const goBackLink = document.createElement('a');
                goBackLink.href = '/';
                document.body.appendChild(goBackLink);
                goBackLink.click();
                document.body.removeChild(goBackLink);
              }
            });
          }
        })
      },
      1000);
  }

  private getCertificateDashletData(): void {
    this.loading = true;
    this.getResponsiveSize();
    this.sb.getReceivedCertificates(this.sb.username).subscribe(res => {
      this.certificateDashletData = res;
      this.loading = false;
    })
  }

  private getResponsiveSize(): void {
    this.sb.getMediaBreakPointChange().subscribe(res => {
      const onMobileDevice = (res == "media_sm" || res == "media_s" || res == "media_xs");
      if (onMobileDevice) {
        this.mediaState = 1;
      } else {
        if (res === 'media_xxl') {
          this.mediaState = 4;
        } else if (res === 'media_xl') {
          this.mediaState = 4;
        } else if (res === 'media_lg') {
          this.mediaState = 3
        } else if (res === 'media_md') {
          this.mediaState = 3;
        } else if (res === 'media_sm') {
          this.mediaState = 2;
        } else if (res === 'media_s') {
          this.mediaState = 1;
        }
      }
    })
  }
}
