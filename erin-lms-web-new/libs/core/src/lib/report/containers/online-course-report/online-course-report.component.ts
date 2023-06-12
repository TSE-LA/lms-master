import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {ONLINE_COURSE_REPORT_TABLE_COLUMN} from "../../model/report.constants";
import {ReportSandboxService} from "../../services/report-sandbox.service";
import {Subscription} from "rxjs";
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import {OnlineCourseReport} from "../../model/report.model";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {CategoryItem, SmallDashletModel} from "../../../../../../shared/src/lib/shared-model";

@Component({
  selector: 'jrs-online-course-report',
  template: `
    <jrs-small-dashlets-bundle [dashlets]="dashlets" [loading]="loading"></jrs-small-dashlets-bundle>
    <jrs-section class="margin-bottom-large" [maxWidth]="loading ? '98%' : '87vw'" [width]="'100%'">
      <div class="flex margin-left">
        <span class="spacer"></span>
        <jrs-dropdown-view
          class="margin-left"
          [label]="'Ангилал'"
          [defaultValue]="selectedCategory? selectedCategory.name: ''"
          [outlined]="true"
          [size]="'medium'"
          [icon]="'expand_more'"
          [width]="'180px'"
          [tooltip]="true"
          [values]="categories"
          [hasSuffix]="true"
          (selectedValue)="categorySelect($event)">
        </jrs-dropdown-view>
        <jrs-dropdown-view
          class="margin-left"
          [label]="'Хамрах хүрээ'"
          [defaultValue]="selectedType? selectedType.name: ''"
          [outlined]="true"
          [size]="'medium'"
          [width]="'180px'"
          [icon]="'expand_more'"
          [values]="types"
          [hasSuffix]="true"
          (selectedValue)="stateSelect($event)">
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
        <jrs-button
          jrsTooltip="{{downloadAllReportTooltip}}"
          placement="{{placement}}"
          delay="0"
          class="margin-left"
          style="margin-top: 15px"
          [iconName]="'description'"
          [isMaterial]="true"
          [iconColor]="'light'"
          [size]=" 'icon-medium-responsive'"
          (clicked)="downloadAllReport()">
        </jrs-button>
      </div>
      <div class="margin-top ">
        <jrs-dynamic-table
          [notFoundText]="notFoundText"
          [loading]="loading"
          [minWidth]="'unset'"
          [maxWidth]="'unset'"
          [tableMinHeight]="'48vh'"
          [dataPerPage]="10"
          [freezeFirstColumn]="true"
          [dataSource]="dataSource"
          [tableColumns]="tableColumns"
          (rowAction)="selectRow($event)">
        </jrs-dynamic-table>
      </div>
    </jrs-section>
    <jrs-page-loader [show]="downloading"></jrs-page-loader>
  `,
  styles: []
})
export class OnlineCourseReportComponent implements OnInit, AfterViewInit, OnDestroy {
  categories: CategoryItem[] = [{name: 'БҮГД', id: 'all'}];
  selectedCategory: CategoryItem;
  startDate: string;
  endDate: string;
  placement = 'bottom';
  reportDownloadTooltip = 'Тайлан татах';
  downloadAllReportTooltip = 'Дэлгэрэнгүй тайлан татах';
  types: DropdownModel[] = [{name: 'БҮГД', id: 'all'}];
  selectedType: DropdownModel;
  selectedGroupId: string;
  notFoundText = 'Мэдээлэл олдсонгүй';
  tableColumns = ONLINE_COURSE_REPORT_TABLE_COLUMN;
  loading = false;
  downloading: boolean;
  dataSource: OnlineCourseReport[] = [];
  groupSubscription: Subscription;
  startDateSubscription: Subscription;
  endDateSubscription: Subscription;
  dashlets: SmallDashletModel[] = [{}, {}, {}, {}];


  constructor(private sb: ReportSandboxService) {
  }


  ngOnInit(): void {
    this.types = this.types.concat(this.sb.constants.COURSE_TYPES);
    this.selectedType = this.types[0];
    this.sb.getOnlineCourseCategories().subscribe(res => {
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
      this.updateQueryParams();
      this.updateTable();
    });
    this.startDateSubscription = this.sb.getStartDate().subscribe(start => {
      this.startDate = start;
      this.updateQueryParams();
      this.updateTable();
    });
    this.endDateSubscription = this.sb.getEndDate().subscribe(end => {
      this.endDate = end;
      this.updateQueryParams();
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

  stateSelect(value): void {
    this.selectedType = value;
    this.updateTable();
  }

  private updateTable(): void {
    if (this.startDate && this.endDate && this.selectedCategory && this.selectedType && this.selectedGroupId) {
      this.loading = true;
      this.sb.getOnlineCourseReport(this.startDate, this.endDate, this.selectedCategory.id, this.selectedType.id, this.selectedGroupId).subscribe(res => {
        this.dataSource = res.report;
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
      dialogRef.afterClosed.subscribe();
    } else {
      this.downloading = true
      this.sb.downloadOnlineCourseExcelReport(this.dataSource).subscribe(() => {
        this.downloading = false;
      }, () => {
        this.downloading = false;
        this.sb.snackbarOpen("Тайлангийн мэдээлэл татахад алдаа гарлаа");
      })
    }
  }

  downloadAllReport(): void {
    if (this.dataSource.length === 0) {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.decline = false;
      config.title = 'Анхаар!';
      config.data = {info: (`Тайлангийн мэдээлэл олдсонгүй.`)};
      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
      dialogRef.afterClosed.subscribe();
    } else {
      this.downloading = true
      this.sb.downloadAllOnlineCourseExcelReport(this.startDate, this.endDate, this.selectedCategory.id, this.selectedType.id, this.selectedGroupId).subscribe(() => {
        this.downloading = false;
      }, () => {
        this.downloading = false;
        this.sb.snackbarOpen("Тайлангийн мэдээлэл татахад алдаа гарлаа");
      })
    }
  }

  selectRow(event): void {
    this.sb.navigate('/online-course/statistics/' + event.data.id, {startDate: this.startDate, endDate: this.endDate});
  }

  private updateQueryParams(): void {
    this.sb.updateQueryParams({startDate: this.startDate, endDate: this.endDate})
  }
}
