import {Component, OnInit} from '@angular/core';
import {GroupNode, SmallDashletModel, TableColumn, TimedCourseModel} from "../../../../../../shared/src/lib/shared-model";
import {ActivatedRoute} from "@angular/router";
import {TimedCourseSandboxService} from "../../services/timed-course-sandbox.service";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {TimedCourseStatistics} from "../../models/timed-course.model";

@Component({
  selector: 'jrs-timed-course-statistics',
  template: `
    <jrs-button class="create-page-sidenav-content"
                [iconName]="'arrow_back_ios'"
                [iconColor]="'secondary'"
                [noOutline]="true"
                [isMaterial]="true"
                [size]="'medium'"
                [bold]="true"
                [textColor]="'text-link'"
                (clicked)="goBack()">БУЦАХ
    </jrs-button>
    <jrs-section [background]="'section-background-secondary'" [minWidth]="'fit-content'" [width]="'85vw'" [height]="'unset'">
      <div class="header">
        <jrs-header-text [size]="'large'" [margin]="true" class="header-margin"
                         [class.long-text]="this.courseTitle.length > 50">{{this.courseTitle}}</jrs-header-text>
        <div class="spacer"></div>
        <jrs-date-interval-picker
          [month]="true"
          [startDate]="startDate"
          [endDate]="endDate"
          (startDateChange)="this.startDateChange($event)"
          (endDateChange)="this.endDateChange($event)">
        </jrs-date-interval-picker>
        <jrs-drop-down-tree-view
          class="margin-left"
          [selectedGroupName]="selectedGroupName"
          [allGroups]="groups"
          [width]="'150px'"
          [label]="'Алба, хэлтэс'"
          (selectedNode)="groupChange($event)">
        </jrs-drop-down-tree-view>
        <jrs-button
          jrsTooltip="{{downloadStatisticsTooltip}}"
          placement="{{placement}}"
          delay="0"
          class="margin-left"
          style="margin-top: 15px"
          [iconName]="'file_download'"
          [isMaterial]="true"
          [iconColor]="'light'"
          [size]=" 'icon-medium-responsive'"
          (clicked)="downloadStatistics()">
        </jrs-button>
      </div>
      <div class="dashlets">
        <jrs-small-dashlet
          *ngFor="let dashlet of smallDashlets"
          [title]="dashlet.title"
          [info]="dashlet.info"
          [loading]="loading"
          [height]="'120px'"
          [backgroundColor]="'gray-background'"
          [hasDropDown]="dashlet.hasDropDown"
          [imageSrc]="dashlet.imageSrc"
          [extraData]="dashlet.extraData"
          [navigateLink]="dashlet.navigateLink">
        </jrs-small-dashlet>
      </div>
      <div class="margin-top">
        <jrs-dynamic-table
          [notFoundText]="notFoundText"
          [loading]="loading"
          [minWidth]="'unset'"
          [maxWidth]="'unset'"
          [tableMinHeight]="'51vh'"
          [dataSource]="dataSource"
          [tableColumns]="tableColumns"
          (rowAction)="showRowSurvey($event)">
        </jrs-dynamic-table>
      </div>
    </jrs-section>
    <jrs-page-loader [show]="downloading"></jrs-page-loader>
  `,
  styleUrls: ['./timed-course-statistics.component.scss']
})
export class TimedCourseStatisticsComponent implements OnInit {
  startDate: string;
  endDate: string;
  selectedGroupId: string;
  selectedGroupName: string;
  smallDashlets: SmallDashletModel[] = [];
  dataSource: TimedCourseStatistics[] = [];
  tableColumns: TableColumn[] = this.sb.constants.TIMED_COURSE_STATISTICS_DEFAULT_COLUMN;
  downloading: boolean;
  notFoundText = 'Мэдээлэл олдсонгүй';
  groups: GroupNode[] = [];
  loading: boolean;
  courseId: string;
  timedCourseProperties: TimedCourseModel;
  courseTitle = '';
  downloadStatisticsTooltip = 'Статистик татах';
  placement = 'bottom';

  constructor(private route: ActivatedRoute, private sb: TimedCourseSandboxService) {
    this.startDate = route.snapshot.queryParamMap.get("startDate");
    this.endDate = route.snapshot.queryParamMap.get("endDate");
    this.courseId = route.snapshot.paramMap.get("id");
  }

  ngOnInit(): void {
    this.loading = true;
    this.loadPage();
  }

  getGroups(): void {
    this.sb.getAllGroups(false).subscribe((groups) => {
      this.selectedGroupName = groups[0].name;
      this.selectedGroupId = groups[0].id;
      this.groups = groups;
      this.groupChange(this.groups[0]);
    }, () => {
      this.sb.openSnackbar("Алба хэлтэсийн мэдээлэл авахад алдаа гарлаа.");
    });
  }

  startDateChange(event: string): void {
    this.startDate = event;
    this.getStatistics();
  }

  endDateChange(event: string): void {
    this.endDate = event;
    this.getStatistics();
  }

  groupChange(value: GroupNode): void {
    this.selectedGroupName = value.name;
    this.selectedGroupId = value.id;
    this.getStatistics();
  }

  showRowSurvey(row): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Суралцагчийн бөглөсөн асуулга.";
    config.decline = false;
    config.data = {
      info: (`${row.data.feedback}`)
    };
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe();
  }

  loadPage(): void {
    this.sb.getTimedCourseById(this.courseId).subscribe(res => {
      this.timedCourseProperties = res;
      if (this.timedCourseProperties.hasFeedBack) {
        this.tableColumns = this.tableColumns.concat(this.sb.constants.TIMED_COURSE_STATISTICS_FEEDBACK_COLUMN);
      }
      this.courseTitle = this.timedCourseProperties.name;
    }, () => {
      this.sb.openSnackbar("Урамшууллынн мэдээлэл авахад алдаа гарлаа.");
    });
    this.getGroups();
  }

  downloadStatistics(): void {
    this.downloading = true;
    this.sb.downloadStatistics(this.courseId, this.startDate, this.endDate, this.selectedGroupId).subscribe(
      () => this.downloading = false,
      () => {
        this.downloading = false;
        this.sb.openSnackbar("Мэдээлэл татахад алдаа гарлаа.");
      });
  }

  getStatistics(): void {
    if (this.startDate != null && this.endDate != null && this.selectedGroupId != null) {
      this.loading = true;
      this.sb.getTimedCourseStatistics(this.courseId, this.startDate, this.endDate, this.selectedGroupId).subscribe(res => {
        this.dataSource = res.statistics;
        this.smallDashlets = res.dashlets;
        this.loading = false;
      }, () => {
        this.loading = false;
        this.sb.openSnackbar("Статистик мэдээлэл авахад алдаа гарлаа.");
      });
    }
  }

  goBack(): void {
    this.sb.goBack();
  }

}
