import {Component, OnInit} from '@angular/core';
import {DialogRef} from "../../../../../../../shared/src/lib/dialog/dialog-ref";
import {QuestionBank} from "../../../model/question.model";
import {CategoryItem, GroupNode, TableColumn} from "../../../../../../../shared/src/lib/shared-model";
import {ExamSandboxService} from "../../../services/exam-sandbox.service";
import {SELECT_QUESTION_COLUMNS} from "../../../model/question.constants";
import {DialogConfig} from "../../../../../../../shared/src/lib/dialog/dialog-config";

@Component({
  selector: 'jrs-question-select-dialog',
  template: `
    <jrs-scroll [color]="'primary'" [height]="'80vh'">
      <div class="flex">
        <span class="spacer"></span>
        <jrs-drop-down-tree-view
          [selectedGroupName]="selectedGroupName"
          [allGroups]="groups"
          [width]="'150px'"
          [label]="'Групп'"
          (selectedNode)="selectGroup($event)">
        </jrs-drop-down-tree-view>
        <jrs-dropdown-view
          class="margin-left"
          [defaultValue]="selectedCategoryName"
          [size]="'medium'"
          [width]="'150px'"
          [label]="'Ангилал'"
          [icon]="'expand_more'"
          [outlined]="true"
          [padding]="true"
          [values]="categories"
          (selectedValue)="selectCategory($event)">
        </jrs-dropdown-view>
      </div>

      <jrs-dynamic-table
        [dataSource]="dataSource"
        [tableColumns]="tableColumns"
        [notFoundText]="'Асуултын мэдээлэл олдсонгүй'"
        [loading]="loadTable"
        [minWidth]="'unset'"
        [maxWidth]="'unset'"
        [tableMinHeight]="'45vh'"
        (rowAction)="addToSelected($event)">
      </jrs-dynamic-table>

      <jrs-selected-questions
        [questions]="selectedQuestions"
        [addButton]="false"
        (removedQuestion)="updateTable($event)">
      </jrs-selected-questions>
    </jrs-scroll>

    <jrs-action-buttons
      [submitButton]="'НЭМЭХ'"
      [declineButton]="'БОЛИХ'"
      (submitted)="add()" (declined)="close()">
    </jrs-action-buttons>
  `
})
export class QuestionSelectDialogComponent implements OnInit {
  dataSource: QuestionBank[] = [];
  tableColumns: TableColumn[] = SELECT_QUESTION_COLUMNS;
  loadTable: boolean;
  groups: GroupNode[] = [];
  categories: CategoryItem[] = [];
  selectedGroupName = "";
  selectedCategoryName = "";
  selectedQuestions: QuestionBank[] = [];
  private selectedCategory: CategoryItem;
  private selectedGroup: GroupNode;
  private selectedIds: Set<string>;


  constructor(private config: DialogConfig, private dialog: DialogRef, private sb: ExamSandboxService) {
    this.selectedIds = this.config.data.selectedIds;
    this.groups = this.config.data.groups;
    this.categories = this.config.data.categories;
    this.selectedCategory = this.config.data.selectedCategory;
    this.selectedGroup = this.config.data.selectedGroup;
    this.selectedGroupName = this.selectedGroup.name;
    this.selectedCategoryName = this.selectedCategory.name;
  }

  ngOnInit(): void {
    this.getQuestions();

  }

  close(): void {
    this.dialog.close();
  }

  add(): void {
    this.dialog.close(this.selectedQuestions);
  }

  selectGroup(groupNode: GroupNode): void {
    this.selectedGroup = groupNode;
    this.selectedGroupName = this.selectedGroup.name;
    this.getQuestions();
  }

  selectCategory(category): void {
    this.selectedCategory = category;
    this.getQuestions();
  }

  addToSelected(event): void {
    this.dataSource.splice(event.dataIndex, 1);
    this.dataSource = [...this.dataSource];
    this.selectedQuestions.push(event.data);
    this.selectedQuestions = [...this.selectedQuestions];
  }

  updateTable(question: QuestionBank): void {
    this.dataSource.unshift(question);
    this.dataSource = [...this.dataSource];
  }

  private getQuestions(): void {
    this.loadTable = true;
    const selectedNewIds = new Set(this.selectedQuestions.map(question => question.id));
    this.sb.getActiveQuestions(this.selectedCategory.id, this.selectedGroup.id).subscribe(res => {
      this.dataSource = res.filter(question =>
        !this.selectedIds.has(question.id) && !selectedNewIds.has(question.id));
      this.loadTable = false;
    }, () => {
      this.loadTable = false;
      this.sb.snackbarOpen("Асуултын сан ачаалахад алдаа гарлаа");
    });
  }
}
