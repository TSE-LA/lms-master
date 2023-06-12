import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {ReportSandboxService} from "../../services/report-sandbox.service";
import {TIMED_COURSE_REPORT_COLUMNS} from "../../model/report.constants";
import {Subscription} from "rxjs";
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DateIntervalDialogComponent} from "../../../common/components/date-interval-dialog/date-interval-dialog.component";
import {CategoryItem, SmallDashletModel} from "../../../../../../shared/src/lib/shared-model";

@Component({
  selector: 'jrs-timed-course-report',
  template: `
    <jrs-small-dashlets-bundle [dashlets]="dashlets" [loading]="loading"></jrs-small-dashlets-bundle>
    <jrs-section class="margin-bottom-large" [maxWidth]="loading ? '98%' : '87vw'" [width]="'100%'">
      <div class="flex">
        <span class="spacer"></span>
        <jrs-dropdown-view
          class="margin-left"
          [label]="'Ангилал'"
          [defaultValue]="selectedCategory? selectedCategory.name: ''"
          [outlined]="true"
          [size]="'medium'"
          [width]="'180px'"
          [tooltip]="true"
          [icon]="'expand_more'"
          [values]="categories"
          [hasSuffix]="true"
          (selectedValue)="categorySelect($event)">
        </jrs-dropdown-view>
        <jrs-dropdown-view
          class="margin-left"
          [label]="'Төлөв'"
          [width]="'180px'"
          [defaultValue]="selectedState? selectedState.name: ''"
          [outlined]="true"
          [size]="'medium'"
          [icon]="'expand_more'"
          [values]="states"
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
          jrsTooltip="{{promoDownloadTooltip}}"
          placement="{{placement}}"
          delay="0"
          class="margin-left"
          style="margin-top: 15px"
          [iconName]="'file_copy'"
          [isMaterial]="true"
          [iconColor]="'light'"
          [size]=" 'icon-medium-responsive'"
          (clicked)="promoDownload()">
        </jrs-button>
      </div>
      <div class="margin-top">
        <jrs-dynamic-table
          [notFoundText]="notFoundText"
          [loading]="loading"
          [minWidth]="'unset'"
          [maxWidth]="'unset'"
          [height]="'unset'"
          [dataPerPage]="100"
          [freezeFirstColumn]="true"
          [tableMinHeight]="'65vh'"
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
export class TimedCourseReportComponent implements OnInit, AfterViewInit, OnDestroy {
  categories: CategoryItem[] = [{name: 'БҮГД', id: 'all'}];
  selectedCategory: CategoryItem;
  startDate: string;
  endDate: string;
  placement = 'bottom';
  reportDownloadTooltip = 'Тайлан татах';
  promoDownloadTooltip = 'Промо тайлан';
  states: DropdownModel[] = [{name: 'БҮГД', id: 'all'}];
  selectedState: DropdownModel;
  selectedGroupId: string;
  notFoundText = 'Мэдээлэл олдсонгүй';
  tableColumns = TIMED_COURSE_REPORT_COLUMNS;
  loading = false;
  dataSource = [];
  downloading: boolean;
  groupSubscription: Subscription;
  startDateSubscription: Subscription;
  endDateSubscription: Subscription;
  dashlets: SmallDashletModel[] = [{}, {}, {}, {}];

  constructor(private sb: ReportSandboxService) {

  }

  ngOnInit(): void {
    this.states = this.states.concat(this.sb.constants.TIMED_COURSE_STATE);
    this.selectedState = this.states[0];
    this.sb.getTimedCourseCategories().subscribe(res => {
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
    this.selectedState = value;
    this.updateTable();
  }

  updateTable(): void {
    if (this.startDate && this.endDate && this.selectedCategory && this.selectedState && this.selectedGroupId) {
      this.loading = true;
      this.sb.getTimeCourseReport(this.startDate, this.endDate, this.selectedCategory.name, this.selectedState.id, this.selectedGroupId).subscribe(res => {
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
      this.downloading = true
      this.sb.downloadTimedCourseExcelReport(this.dataSource).subscribe(() => {
        this.downloading = false;
      }, () => {
        this.downloading = false;
        this.sb.snackbarOpen("Тайлангийн мэдээлэл татахад алдаа гарлаа");
      })
    }
  }

  promoDownload(): void {
    const config = new DialogConfig();
    config.outsideClick = false;
    config.title = 'Промо тайлан татах';
    config.width = '290px'
    config.data = {
      confirmText: 'ТАТАХ',
      declineText: 'БОЛИХ'
    };
    const dialogRef = this.sb.openDialog(DateIntervalDialogComponent, config);
    dialogRef.afterClosed.subscribe(res => {
      if (res) {
        this.downloading = true;
        this.sb.downloadPromoExcelReport(res.startDate, res.endDate).subscribe(() => {
            this.downloading = false;
          }, () => {
            this.downloading = false;
            this.sb.snackbarOpen("Тайлангийн мэдээлэл татахад алдаа гарлаа");
          }
        );
      } else {
        return;
      }
    })
  }

  selectRow(event): void {
    this.sb.navigate('/timed-course/statistics/' + event.data.id, {startDate: this.startDate, endDate: this.endDate});
  }

  private updateQueryParams(): void {
    this.sb.updateQueryParams({startDate: this.startDate, endDate: this.endDate})
  }
}
