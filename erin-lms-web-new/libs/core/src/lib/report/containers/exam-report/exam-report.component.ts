import {AfterViewInit, Component, OnDestroy, OnInit} from "@angular/core";
import {CategoryItem, GroupNode, TableColumn} from "../../../../../../shared/src/lib/shared-model";
import {ReportSandboxService} from "../../services/report-sandbox.service";
import {EXAM_REPORT_TABLE_COLUMNS} from "../../model/report.constants";
import {DetailedExamReportTableModel, ExamReportMappedModel} from "../../model/report.model";
import {ALL_CHOICE} from "../../../common/common.model";
import {EXAM_STATES, EXAM_TYPES} from "../../../exam/model/exam.constants";
import {forkJoin, Subscription, throwError} from "rxjs";
import {catchError} from "rxjs/operators";

@Component({
  selector: 'jrs-exam-report',
  template: `
    <jrs-section [color]="'primary'" [maxWidth]="'87vw'" [width]="'100%'" [minHeight]="'70vh'">
      <div class="dropdowns-field">
        <jrs-dropdown-view
          class="dropdown-style margin-right"
          [label]="'Ангилал'"
          [chooseFirst]="true"
          [outlined]="true"
          [size]="'medium'"
          [icon]="'expand_more'"
          [tooltip]="true"
          [width]="'11vw'"
          [values]="categories"
          (selectedValue)="filterByCategory($event)">
        </jrs-dropdown-view>
        <jrs-dropdown-view
          class="dropdown-style margin-right"
          [label]="'Төрөл'"
          [chooseFirst]="true"
          [outlined]="true"
          [size]="'medium'"
          [tooltip]="true"
          [width]="'11vw'"
          [icon]="'expand_more'"
          [values]="types"
          (selectedValue)="filterByType($event)">
        </jrs-dropdown-view>
        <jrs-drop-down-tree-view
          class="dropdown-style margin-right"
          [label]="'Шалгалтын групп'"
          [allGroups]="examGroups"
          [selectedGroupName]="selectedGroupName"
          [placeholder]="'Шалгалтын групп сонгоно уу'"
          [size]="'medium'"
          [width]="'11vw'"
          (selectedNode)="filterByGroup($event)">
        </jrs-drop-down-tree-view>
        <jrs-dropdown-view
          class="dropdown-style margin-right"
          [label]="'Төлөв'"
          [chooseFirst]="true"
          [outlined]="true"
          [tooltip]="true"
          [width]="'11vw'"
          [size]="'medium'"
          [icon]="'expand_more'"
          [values]="states"
          (selectedValue)="filterByStates($event)">
        </jrs-dropdown-view>
      </div>

      <jrs-dynamic-table
        [notFoundText]="notFoundText"
        [loading]="loading"
        [minWidth]="'unset'"
        [maxWidth]="'unset'"
        [dataPerPage]="100"
        [tableMinHeight]="'59vh'"
        [freezeFirstColumn]="true"
        [dataSource]="dataSource"
        [tableColumns]="tableColumns"
        (rowAction)="navigateToStatistics($event)">
      </jrs-dynamic-table>
    </jrs-section>
  `,
  styleUrls: ['./exam-report.component.scss']
})
export class ExamReportComponent implements OnInit, AfterViewInit, OnDestroy {
  startDate: string;
  endDate: string;
  tableColumns: TableColumn[] = EXAM_REPORT_TABLE_COLUMNS;
  dataSource: DetailedExamReportTableModel[] = [];
  tableData: DetailedExamReportTableModel[] = [];
  dataForFilter: DetailedExamReportTableModel[] = [];
  dataToShow: DetailedExamReportTableModel[] = [];
  notFoundText = 'Мэдээлэл олдсонгүй';
  loading: boolean;
  disabled = false;
  filter = new Map();

  categories: CategoryItem[] = [ALL_CHOICE];
  types = EXAM_TYPES;
  states = EXAM_STATES;
  groups = [];
  examGroups: GroupNode[] = [];
  selectedCategory: CategoryItem = this.categories[0];
  selectedType = this.types[0];
  selectedState = this.states[0];
  selectedGroup = this.examGroups[0];
  startDateSubscription: Subscription;
  endDateSubscription: Subscription
  unsortedData: any[];
  selectedGroupName: string;

  constructor(private sb: ReportSandboxService) {
    this.sb.getStartDate().subscribe(start => {
      this.startDate = start;
      this.getExamData();
    });
    this.sb.getEndDate().subscribe(end => {
      this.endDate = end;
      this.getExamData();
    });

  }

  ngOnInit(): void {
    this.loading = true;
    const tasks$ = [];
    tasks$.push(this.getCategories());
    tasks$.push(this.getExamGroups());
    forkJoin(...tasks$).subscribe(() => {
      this.getExamData();
    }, () => {
      this.loading = false;
    })
  }

  ngAfterViewInit(): void {
    this.startDateSubscription = this.sb.getStartDate().subscribe(start => {
      this.startDate = start;
      this.getExamData();
    });
    this.endDateSubscription = this.sb.getEndDate().subscribe(end => {
      this.endDate = end;
      this.getExamData();
    });
  }

  getCategories(): void {
    this.sb.getExamCategories().subscribe(category => {
      if (category) {
        this.categories = this.categories.concat(category);
      }
    }, catchError(error => {
      this.loading = false;
      this.dataToShow = [];
      return throwError(error);
    }));
  }

  private getExamGroups(): void {
    this.loading = true;
    this.sb.getExamGroups().subscribe(examGroup => {
      this.loading = false;
      this.examGroups = examGroup;
    }, () => {
      this.loading = false;
      this.sb.snackbarOpen("Групп ачааллахад алдаа гарлаа!");
    });
  }

  getExamData(): void {
    this.loading = true;
    let filterItems: ExamReportMappedModel;
    if (this.selectedGroup && this.selectedCategory && this.selectedType && this.selectedState) {
      filterItems = {
        category: this.selectedCategory.id,
        status: this.selectedState.id,
        type: this.selectedType.id,
        group: this.selectedGroup.id
      }
      if (this.startDate != undefined && this.endDate !== undefined) {
        this.sb.getExamReport(this.startDate, this.endDate, filterItems).subscribe(res => {
          if (res) {
            this.loading = false
            this.dataSource = res;
            this.dataToShow = [];
            this.tableData = this.dataSource;
            this.unsortedData = [...this.dataSource];
            this.dataForFilter = this.dataSource;
          } else {
            this.dataToShow = [];
            this.loading = false;
          }
        }, () => {
          this.loading = false;
          this.dataToShow = [];
        })
      }
    } else {
      this.loading = false;
      this.dataToShow = [];
    }
  }

  navigateToStatistics(data: any): void {
    this.sb.navigateByUrl('exam/container/exam-statistics/' + data.data.id)
  }

  filterByType(type: any): void {
    this.loading = true;
    this.selectedType = type;
    this.getExamData();
  }

  filterByGroup(group: GroupNode): void {
    this.loading = true;
    this.selectedGroup = group;
    this.selectedGroupName = this.selectedGroup.name;
    this.getExamData();
  }

  filterByStates(state: any): void {
    this.loading = true;
    this.selectedState = state;
    this.getExamData();
  }

  filterByCategory(category: any): void {
    this.loading = true;
    this.selectedCategory = category;
    this.getExamData();
  }

  ngOnDestroy() {
    this.startDateSubscription.unsubscribe();
    this.endDateSubscription.unsubscribe();
  }
}
