import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {CLASSROOM_COURSE_REPORT_TABLE_COLUMNS} from "../../model/report.constants";
import {ReportSandboxService} from "../../services/report-sandbox.service";
import {Subscription} from "rxjs";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {CategoryItem, SmallDashletModel} from "../../../../../../shared/src/lib/shared-model";
import {ClassroomReportModel} from "../../../classroom-course/model/classroom-course.model";

@Component({
  selector: 'jrs-classroom-course-report',
  template: `
    <jrs-small-dashlets-bundle [dashlets]="dashlets" [loading]="loading"></jrs-small-dashlets-bundle>
    <jrs-section class="margin-bottom-large" [maxWidth]="loading ? '98%' : '76vw'" [width]="'100%'">
      <div class="flex">
        <span class="spacer"></span>
        <jrs-dropdown-view
          class="margin-left"
          [label]="'Ангилал'"
          [defaultValue]="selectedCategory? selectedCategory.name: ''"
          [outlined]="true"
          [tooltip]="true"
          [size]="'medium'"
          [icon]="'expand_more'"
          [values]="categories"
          [width]="'200px'"
          [hasSuffix]="true"
          (selectedValue)="categorySelect($event)">
        </jrs-dropdown-view>
        <jrs-button
          jrsTooltip="{{reportDownloadTooltip}}"
          placement="{{placement}}"
          delay="0"
          class="margin-left"
          style="margin-top: 15px"
          [iconName]="'file_download'"
          [isMaterial]="true"
          [iconColor]="'light'"
          [size]=" 'icon-medium-responsive'"
          (clicked)="reportDownload()">
        </jrs-button>
      </div>
      <div class="margin-top">
        <jrs-dynamic-table
          [notFoundText]="notFoundText"
          [loading]="loading"
          [minWidth]="'unset'"
          [maxWidth]="'unset'"
          [height]="'unset'"
          [tableMinHeight]="'45vh'"
          [freezeFirstColumn]="true"
          [dataPerPage]="100"
          [dataSource]="dataSource"
          [tableColumns]="tableColumns">
        </jrs-dynamic-table>
      </div>
    </jrs-section>
    <jrs-page-loader [show]="downloading"></jrs-page-loader>
  `,
  styles: []
})
export class ClassroomCourseReportComponent implements OnInit, AfterViewInit, OnDestroy {
  categories: CategoryItem[] = [{name: 'БҮГД', id: 'all'}];
  selectedCategory: CategoryItem;
  startDate: string;
  endDate: string;
  placement = 'bottom';
  reportDownloadTooltip = 'Тайлан татах';
  selectedGroupId: string;
  notFoundText = 'Мэдээлэл олдсонгүй';
  tableColumns = CLASSROOM_COURSE_REPORT_TABLE_COLUMNS;
  loading = false;
  dataSource: ClassroomReportModel[] = [];
  downloading: boolean;
  groupSubscription: Subscription;
  startDateSubscription: Subscription;
  endDateSubscription: Subscription;
  dashlets: SmallDashletModel[] = [{}, {}, {}, {}]

  constructor(private sb: ReportSandboxService) {
  }

  ngOnInit(): void {
    this.sb.getClassroomCourseCategories().subscribe(res => {
      this.categories = this.categories.concat(res);
      this.selectedCategory = this.categories[0];
      this.updateTable();
    }, () => {
      this.sb.snackbarOpen("Ангилалын мэдээлэл авахад алдаа гарлаа");
    });
  }

  ngAfterViewInit(): void {
    this.groupSubscription = this.sb.getSelectedGroupId().subscribe(res => {
      this.selectedGroupId = res;
      this.updateTable();
    });
    this.startDateSubscription = this.sb.getStartDate().subscribe(start => {
      this.startDate = start;
      this.updateTable();
    });
    this.endDateSubscription = this.sb.getEndDate().subscribe(end => {
      this.endDate = end;
      this.updateTable();
    });
  }


  ngOnDestroy(): void {
    this.groupSubscription.unsubscribe();
    this.startDateSubscription.unsubscribe();
    this.endDateSubscription.unsubscribe();
  }

  categorySelect(value): void {
    this.selectedCategory = value;
    this.updateTable();
  }

  updateTable(): void {
    if (this.startDate && this.endDate && this.selectedCategory && this.selectedGroupId) {
      this.loading = true;
      this.sb.getClassroomCourseReport(this.startDate, this.endDate, this.selectedCategory.id, this.selectedGroupId).subscribe(res => {
        this.dataSource = res.reports;
        this.dashlets = res.dashlets;
        this.loading = false;
      }, () => {
        this.loading = false;
        this.sb.snackbarOpen("Тайлангийн мэдээлэл авахад алдаа гарлаа");
      });
    }
  }

  reportDownload(): void {
    if (this.dataSource.length === 0) {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.decline = false;
      config.title = 'Анхаар!';
      config.data = {info: (`Тайлангийн мэдээлэл олдсонгүй.`)};
      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
      dialogRef.afterClosed.subscribe(result => {
        return;
      });
    } else {
      this.downloading = true;
      const duration: string[] = [];
      for (const data of this.dataSource) {
        duration.push(data.id + "-" + data.duration);
      }
      this.sb.downloadClassroomCourseExcelReport(
        this.startDate,
        this.endDate,
        this.selectedCategory.id,
        this.selectedGroupId,
        duration).subscribe(() =>
          this.downloading = false,
        () => {
          this.downloading = false;
          this.sb.snackbarOpen("Тайлангийн мэдээлэл татахад алдаа гарлаа");
        });
    }
  }

}
