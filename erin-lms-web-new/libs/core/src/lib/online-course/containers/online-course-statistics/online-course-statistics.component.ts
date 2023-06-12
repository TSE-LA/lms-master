import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {GroupNode, SmallDashletModel, TableColumn} from "../../../../../../shared/src/lib/shared-model";
import {OnlineCourseSandboxService} from "../../online-course-sandbox.service";
import {OnlineCourseModel, OnlineCourseStatistic} from "../../models/online-course.model";
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";

@Component({
  selector: 'jrs-online-course-statistics',
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
    <jrs-section [background]="'section-background-secondary'" [maxWidth]="loading ? 'auto' : '87vw'" [width]="'100%'" [height]="'unset'">
      <div class="header">
        <jrs-header-text
          class="header-margin"
          [size]="'large'"
          [margin]="true"
          [class.long-text]="this.courseTitle.length > 50">
          {{this.courseTitle}}
        </jrs-header-text>
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
          [width]="'120px'"
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
        <jrs-button
          *ngIf="hasSurveyDownload"
          jrsTooltip="{{downloadSurveyTooltip}}"
          placement="{{placement}}"
          delay="0"
          class="margin-left"
          style="margin-top: 15px"
          [iconName]="'rate_review'"
          [isMaterial]="true"
          [iconColor]="'light'"
          [size]=" 'icon-medium-responsive'"
          (clicked)="downloadSurvey()">
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
          [tableMinHeight]="'50vh'"
          [dataPerPage]="10"
          [dataSource]="dataSource"
          [tableColumns]="tableColumns"
          (rowAction)="downloadRowSurvey($event)">
        </jrs-dynamic-table>
      </div>
    </jrs-section>
    <jrs-page-loader [show]="downloading"></jrs-page-loader>
  `,
  styleUrls: ['./online-course-statistics.component.scss']
})
export class OnlineCourseStatisticsComponent implements OnInit {
  startDate: string;
  endDate: string;
  selectedGroupId: string;
  selectedGroupName: string;
  smallDashlets: SmallDashletModel[] = [];
  dataSource: OnlineCourseStatistic[] = [];
  tableColumns: TableColumn[] = this.sb.constants.ONLINE_COURSE_STATISTICS_DEFAULT_COLUMN;
  downloading: boolean;
  notFoundText = 'Мэдээлэл олдсонгүй';
  groups: GroupNode[] = [];
  loading: boolean;
  courseId: string;
  onlineCourseProperties: OnlineCourseModel;
  courseTitle = '';
  placement = 'bottom';
  downloadStatisticsTooltip = 'Статистик татах';
  downloadSurveyTooltip = 'Үнэлгээ татах';
  hasSurveyDownload: boolean;
  downloadTypes: DropdownModel[] = this.sb.constants.ONLINE_COURSE_STATISTICS_DOWNLOAD_OPTIONS;

  constructor(private route: ActivatedRoute, private sb: OnlineCourseSandboxService) {
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

  downloadRowSurvey(row): void {
    this.downloading = true;
    this.sb.downloadSurveyInOneRowByExcel(this.courseId, this.startDate,
      this.endDate, this.selectedGroupId, row.data.username).subscribe(
      () => this.downloading = false,
      () => {
        this.downloading = false;
        this.sb.openSnackbar("Мэдээлэл татахад алдаа гарлаа.");
      });
  }

  loadPage(): void {
    this.sb.getOnlineCourseById(this.courseId).subscribe(res => {
      this.onlineCourseProperties = res;
      if (this.onlineCourseProperties.hasCertificate) {
        this.tableColumns = this.tableColumns.concat(this.sb.constants.ONLINE_COURSE_STATISTICS_CERTIFICATE_COLUMN);
      }
      if (this.onlineCourseProperties.hasSurvey) {
        this.tableColumns = this.tableColumns.concat(this.sb.constants.ONLINE_COURSE_STATISTICS_SURVEY_COLUMN);
        if (this.downloadTypes.length > 1) {
          this.hasSurveyDownload = true;
        }
      }
      if (this.onlineCourseProperties.hasTest) {
        this.tableColumns = this.tableColumns.concat(this.sb.constants.ONLINE_COURSE_STATISTICS_TEST_COLUMN);
      }
      this.courseTitle = this.onlineCourseProperties.name;
    }, () => {
      this.sb.openSnackbar("Цахим сургалтын мэдээлэл авахад алдаа гарлаа.");
    });
    this.getGroups();
  }

  getStatistics(): void {
    if (this.startDate != null && this.endDate != null && this.selectedGroupId != null) {
      this.dataSource = [];
      this.loading = true;
      this.sb.getOnlineCourseStatistics(this.courseId, this.startDate, this.endDate, this.selectedGroupId).subscribe(res => {
        this.dataSource = res.statistics;
        this.smallDashlets = res.dashlets;
        this.loading = false;
      }, () => {
        this.loading = false;
        this.sb.openSnackbar("Статистик мэдээлэл авахад алдаа гарлаа.");
      });
    }
  }

  downloadStatistics(): void {
    if (this.dataSource.length > 0) {
      this.downloading = true;
      this.sb.downloadStatistics(this.dataSource).subscribe(
        () => this.downloading = false,
        () => {
          this.downloading = false;
          this.sb.openSnackbar("Мэдээлэл татахад алдаа гарлаа.");
        });
    } else {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.title = "Анхаар !";
      config.data = {
        info: (`'${this.onlineCourseProperties.name}'  нэртэй цахим сургалттай хэрэглэгч танилцаагүй байна.'`)
      };
      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
      dialogRef.afterClosed.subscribe();
    }
  }

  downloadSurvey(): void {
    let hasData = false;
    this.dataSource.find(res => hasData = res.progress > 0 && res.doneSurvey);
    if (hasData) {
      this.downloading = true;
      this.sb.downloadSurvey(this.courseId, this.startDate,
        this.endDate, this.selectedGroupId).subscribe(
        () => this.downloading = false,
        () => {
          this.downloading = false;
          this.sb.openSnackbar("Мэдээлэл татахад алдаа гарлаа.");
        });
    } else {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.title = "Анхаар !";
      config.data = {
        info: (`'${this.onlineCourseProperties.name}' + ' нэртэй цахим сургалтын үнэлгээг бөглөсөн хэрэглэгч байхгүй байна.'`)
      };
      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
      dialogRef.afterClosed.subscribe();
    }
  }

  goBack(): void {
    this.sb.goBack();
  }

}
