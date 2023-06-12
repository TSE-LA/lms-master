import {Component, OnInit} from '@angular/core';
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {EXAMINEE_TABLE_COLUMN} from "../../model/exam.constants";
import {LearnerExamListModel} from "../../model/exam.model";
import {ALL_CHOICE} from "../../../common/common.model";
import {CategoryItem} from "../../../../../../shared/src/lib/shared-model";

@Component({
  selector: 'jrs-examinee-page',
  template: `
    <jrs-section [maxWidth]="'72vw'" [width]="'100%'" [height]="'72vh'" [background]="'section-background-secondary'">
      <div class="inline-flex">
        <jrs-dropdown-view
          [defaultValue]="selectedCategoryName"
          [values]="categories"
          (selectedValue)="changeCategory($event)"
          [label]="'Шалгалтын ангилал'"
          [color]="'light'"
          [width]="'180px'"
          [outlined]="true"
          [load]="loading"
          [tooltip]="true"
          [size]="'medium'">
        </jrs-dropdown-view>
        <jrs-icon
          class="info-icon"
          [mat]="true"
          [size]="'medium'"
          [color]="'primary'"
          placement="{{placement}}"
          delay="0"
          jrsTooltip="{{tooltip}}">info
        </jrs-icon>
      </div>
      <jrs-dynamic-table
        [loading]="loading"
        [dataSource]="data"
        [hasLoader]="false"
        [tableMinHeight]="'61vh'"
        [tableColumns]="columns"
        [notFoundText]="notFountText"
        (clickOnRow)="clickedOnRow($event)">
      </jrs-dynamic-table>
    </jrs-section>
  `,
  styleUrls: ['./examinee-page.component.scss']
})
export class ExamineePageComponent implements OnInit {
  loading: boolean;
  data: LearnerExamListModel[] = [];
  columns = EXAMINEE_TABLE_COLUMN;
  notFountText = 'Шалгалт олдсонгүй';
  categories: CategoryItem[] = [ALL_CHOICE];
  selectedCategoryName: string;
  selectedCategory: CategoryItem;
  dataForFilter: LearnerExamListModel[] = [];
  filter = new Map();
  placement = 'right';
  tooltip = '7 хоногийн дотор эхлэх шалгалтууд тодорч харагдаж байна.';


  constructor(private sb: ExamSandboxService) {}


  ngOnInit(): void {
    this.loadPage();
  }

  loadPage(): void {
    this.loading = true;
    this.sb.getExamCategories().subscribe(res => {
      this.categories = this.categories.concat(res);
      this.selectedCategory = this.sb.getExamCategory() ? this.sb.getExamCategory() : this.categories[0];
      this.selectedCategoryName = this.selectedCategory.name;
    }, () => {
      this.loading = false;
    });
    this.sb.getLearnersAllExam().subscribe(res => {
      this.data = res;
      this.dataForFilter = this.data;
      this.loading = false;
    }, () => {
      this.loading = false;
    });
  }

  changeCategory(category): void {
    this.selectedCategoryName = category.name;
    let tempDataForFilter = this.dataForFilter;
    if (category.id == 'all') {
      this.data = this.dataForFilter;
    } else {
      tempDataForFilter = tempDataForFilter.filter(data => {
        if (data.categoryId == category.id) {
          return data;
        }
      })
      this.data = tempDataForFilter;
    }
  }

  clickedOnRow(exam: any) {
    this.sb.navigateByUrl('/exam/launch/' + exam.id);
  }
}
