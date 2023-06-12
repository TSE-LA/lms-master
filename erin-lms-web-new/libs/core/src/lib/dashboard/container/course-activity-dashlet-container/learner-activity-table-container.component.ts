import {Component, OnInit} from '@angular/core';
import {DashboardSandboxService} from "../../service/dashboard-sandbox.service";
import {DetailedLearnerActivity} from "../../dashboard-model/dashboard-model";
import {GroupNode, TableColumn} from "../../../../../../shared/src/lib/shared-model";
import {
  LEARNER_ACTIVITY_SHORT_TABLE,
  LEARNER_ACTIVITY_TABLE_COLUMN
} from "../../dashboard-model/dashboard-constant";

@Component({
  selector: 'jrs-learner-activity-table-container',
  template: `
    <jrs-button
      [iconName]="'arrow_back_ios'"
      [iconColor]="'secondary'"
      [noOutline]="true"
      [isMaterial]="true"
      [size]="'medium'"
      [bold]="true"
      [textColor]="'text-link'"
      (clicked)="goBack()">БУЦАХ
    </jrs-button>
    <jrs-section [width]="'78vw'" [height]="'unset'" [minWidth]="'unset'">
      <div class="two-part-header">
        <div class="inline-flex">
          <jrs-header-text>НИЙТ СУРАЛЦАГЧИД</jrs-header-text>
          <jrs-header-text [bold]="false">{{'/ ' + selectedGroupName + ' ' + average + '% /'}}</jrs-header-text>
        </div>
        <jrs-drop-down-tree-view
          class="place-self-end"
          [size]="'small'"
          [width]="'120px'"
          [selectedGroupName]="selectedGroupName"
          [allGroups]="allGroups"
          (selectedNode)="selectGroup($event)">
        </jrs-drop-down-tree-view>
      </div>
      <jrs-dynamic-table *ngIf="learnerHistory"
                         [height]="'100%'"
                         [notFoundText]="this.selectedGroupName + suffixText"
                         [loading]="loading"
                         [minWidth]="'unset'"
                         [maxWidth]="'unset'"
                         [dataPerPage]="100"
                         [dataSource]="dataSource"
                         [tableColumns]="tableColumns"
                         (rowAction)="navigateToProfile($event)">
      </jrs-dynamic-table>
      <jrs-dynamic-table *ngIf="!learnerHistory"
                         [height]="'100%'"
                         [notFoundText]="this.selectedGroupName + suffixText"
                         [loading]="loading"
                         [minWidth]="'unset'"
                         [maxWidth]="'unset'"
                         [dataPerPage]="100"
                         [dataSource]="dataSource"
                         [tableColumns]="columns">
      </jrs-dynamic-table>
    </jrs-section>
  `
})
export class LearnerActivityTableContainerComponent implements OnInit {
  selectedGroupId: string;
  selectedGroupName = "";
  allGroups: GroupNode[] = [];
  tableColumns: TableColumn[] = LEARNER_ACTIVITY_TABLE_COLUMN;
  columns: TableColumn[] = LEARNER_ACTIVITY_SHORT_TABLE;
  dataSource: DetailedLearnerActivity[] = [];
  suffixText = ' группэд дата олдсонгүй';
  loading: boolean;
  isOnlineCourseActivity: boolean
  learnerHistory: boolean
  average = 0;

  constructor(private sb: DashboardSandboxService) {
    this.isOnlineCourseActivity = sb.getPermissionAccess('app.navigation.online-course-activity');
    this.learnerHistory = sb.getPermissionAccess('app.navigation.learnerHistory');
  }

  ngOnInit(): void {
    this.loading = true;
    this.sb.getAllGroups(false).subscribe(res => {
      this.selectedGroupId = res[0].id;
      this.selectedGroupName = res[0].name;
      this.allGroups = res;
      if (this.isOnlineCourseActivity == true) {
        this.getOnlineCourseActivity();
      } else {
        this.getTimedCourseActivity();
      }
    })
  }

  selectGroup(selectedGroup: GroupNode): void {
    this.loading = true;
    this.selectedGroupId = selectedGroup.id;
    this.selectedGroupName = selectedGroup.name;
    if (this.isOnlineCourseActivity == true) {
      this.getOnlineCourseActivity();
    } else {
      this.getTimedCourseActivity();
    }
  }

  getTimedCourseActivity(): void {
    this.sb.getTimedCourseUserActivityData(this.selectedGroupId, false).subscribe((res: DetailedLearnerActivity[]) => {
      this.dataSource = res;
      this.calculateAveragePercentage();
      this.loading = false;
    }, () => {
      this.loading = false;
    })
  }

  getOnlineCourseActivity(): void {
    this.sb.getOnlineCourseLearnerActivityData(this.selectedGroupId).subscribe((res: DetailedLearnerActivity[]) => {
      this.dataSource = res;
      this.calculateAveragePercentage();
      this.loading = false;
    }, () => {
      this.loading = false;
    })
  }

  goBack(): void {
    this.sb.goBack();
  }

  navigateToProfile(event: any): void {
    this.sb.navigate('/profile/view', event.data.username);
  }

  private calculateAveragePercentage(): void {
    let total = 0;
    this.dataSource.forEach(activity => {
      if (isNaN(activity.progress)) {
        activity.progress = 0;
      }
      total += activity.progress
    });
    this.average = Number((total / this.dataSource.length).toFixed(2));
  }
}
