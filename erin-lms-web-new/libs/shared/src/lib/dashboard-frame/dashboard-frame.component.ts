import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DashletModel} from "../dashlets/dashlet-model";
import {LearnerSuccessDashletModel, PublishedCourseCountData} from "../../../../core/src/lib/dashboard/dashboard-model/dashboard-model";
import {GroupNode, SmallDashletModel} from "../shared-model";
import {ReceivedCertificateModel} from "../../../../core/src/lib/certificate/model/certificate.model";

@Component({
  selector: 'jrs-dashboard-frame',
  template: `
    <div class="container" *ngIf="!hasCertificateDashlet">
      <div class="header" *ngIf="this.dateFilter">
        <jrs-drop-down-tree-view
          [selectedGroupName]="selectedGroupName"
          [allGroups]="allGroups"
          [width]="'150px'"
          [label]="'Алба, хэлтэс'"
          (selectedNode)="selectGroup($event)">
        </jrs-drop-down-tree-view>
        <jrs-date-interval-picker [month]="true" (startDateChange)="this.startDate.emit($event)"
                                  (endDateChange)="this.endDate.emit($event)"></jrs-date-interval-picker>
      </div>
      <div class="dashlets" [ngClass]="{'no-timed-course': !hasTimedCourseCountDashlet}">
        <div class="big-dashlets">
          <jrs-course-count-dashlet
            *ngIf="hasTimedCourseCountDashlet"
            [dashletInfo]="publishedPromotionDashlet"
            [chartDataBundle]="publishedPromoData"
            [types]="publishedPromotionTypes">
          </jrs-course-count-dashlet>
          <jrs-course-count-dashlet
            *ngIf="hasOnlineCourseCountDashlet"
            [dashletInfo]="publishedOnlineCourseDashlet"
            [chartDataBundle]="publishedOnlineCourseData"
            [types]="publishedCourseTypes">
          </jrs-course-count-dashlet>
        </div>
        <div class="small-dashlets">
          <jrs-small-dashlet
            *ngFor="let dashlet of smallDashletsData"
            [title]="dashlet.title"
            [info]="dashlet.info"
            [loading]="activitiesLoading"
            [hasDropDown]="dashlet.hasDropDown"
            [imageSrc]="dashlet.imageSrc"
            [extraData]="dashlet.extraData"
            [navigateLink]="dashlet.navigateLink"
            (navigateToActivityTable)="navigate(dashlet.navigateLink)">
          </jrs-small-dashlet>
        </div>
      </div>
    </div>
    <div *ngIf="hasCertificateDashlet" class="learner-dashboard">
      <jrs-header-text class="header-text">МИНИЙ АМЖИЛТ</jrs-header-text>
      <jrs-learner-success-dashlet
        *ngIf="hasTimedCourseSuccessDashlet"
        #timedCourseSuccess
        [title]="'УРАМШУУЛАЛ'"
        [data]="timedLearnerSuccessData"
        [loading]="loadingTimedLearnerSuccess"
        (getLearnerSuccessData)="getTimedLearnerSuccessDashletData($event)">
      </jrs-learner-success-dashlet>
      <jrs-learner-success-dashlet
        #onlineCourseSuccess
        [title]="'ЦАХИМ СУРГАЛТ'"
        [data]="onlineLearnerSuccessData"
        [loading]="loadingOnlineLearnerSuccess"
        [large]="!hasTimedCourseSuccessDashlet"
        (getLearnerSuccessData)="getOnlineLearnerSuccessDashletData($event)">
      </jrs-learner-success-dashlet>
      <jrs-certificate-dashlet *ngIf="certificateDashletData.length !== 0" [certificateDashletData]="certificateDashletData"></jrs-certificate-dashlet>
    </div>
  `,
  styleUrls: ['./dashboard-frame.component.scss']
})
export class DashboardFrameComponent {
  @Input() publishedPromoData: PublishedCourseCountData[] = [];
  @Input() publishedOnlineCourseData: PublishedCourseCountData[] = [];
  @Input() smallDashletsData: SmallDashletModel[] = [];
  @Input() style: string;
  @Input() publishedPromotionTypes;
  @Input() publishedCourseTypes;
  @Input() hasTimedCourseCountDashlet: boolean;
  @Input() hasOnlineCourseCountDashlet: boolean;
  @Input() hasTimedCourseSuccessDashlet: boolean;
  @Input() allGroups: any[];
  @Input() selectedGroupName: string;
  @Input() activitiesLoading: boolean;
  @Input() dateFilter: boolean;
  @Input() publishedOnlineCourseDashlet: DashletModel;
  @Input() publishedPromotionDashlet: DashletModel;
  @Input() hasCertificateDashlet: boolean;
  @Input() onlineLearnerSuccessData: LearnerSuccessDashletModel;
  @Input() timedLearnerSuccessData: LearnerSuccessDashletModel;
  @Input() loadingOnlineLearnerSuccess: boolean;
  @Input() loadingTimedLearnerSuccess: boolean;
  @Input() certificateDashletData: ReceivedCertificateModel[] = [];
  @Output() startDate = new EventEmitter<string>();
  @Output() endDate = new EventEmitter<string>();
  @Output() selectedGroup = new EventEmitter<GroupNode>();
  @Output() navigateToActivityTable = new EventEmitter<string>();
  @Output() getOnlineLearnerSuccessData = new EventEmitter<any>();
  @Output() getTimedLearnerSuccessData = new EventEmitter<any>();
  show: boolean;

  onDropdownClick(event): void {
    this.show = !this.show;
  }

  selectGroup(node: GroupNode): void {
    this.selectedGroup.emit(node);
  }

  navigate(navigateUrl): void {
    this.navigateToActivityTable.emit(navigateUrl);
  }

  getOnlineLearnerSuccessDashletData(event: any): void {
    this.getOnlineLearnerSuccessData.emit(event);
  }

  getTimedLearnerSuccessDashletData(event: any): void {
    this.getTimedLearnerSuccessData.emit(event);
  }
}
