import {Component, OnInit} from '@angular/core';
import {CategoryItem, GroupNode} from "../../../../../../shared/src/lib/shared-model";
import {QUESTION_ACTIONS, QUESTION_BANK_COLUMNS, QUESTION_DELETE, QUESTION_EDIT} from "../../model/question.constants";
import {ALL_CHOICE} from "../../../common/common.model";
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {QuestionBank} from "../../model/question.model";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {GROUP_DELETE, GROUP_EDIT, QUESTION_GROUP_ACTIONS} from "../../../group-management/model/group.model";
import {EditExamGroupDialogComponent} from "../edit-exam-group-dialog/edit-exam-group-dialog.component";
import {AddExamGroupDialogComponent} from "../add-exam-group-dialog/add-exam-group-dailog.component";

@Component({
  selector: 'jrs-question-bank',
  template: `
    <div class="container">
      <jrs-section [height]="'72vh'" [width]="'200px'" [minWidth]="'fit-content'" [background]="'section-background-secondary'">
        <div class="tree-flex">
          <jrs-button
            [color]="'primary'"
            [size]="'long'"
            [title]="'Групп нэмэх'"
            [width]="'auto'"
            [iconColor]="'light'"
            (click)="openAddQuestionGroupDialog()">
          </jrs-button>
        </div>
        <jrs-scroll [height]="'72vh'" [color]="'primary'" [size]="'medium'" [horizontalScrollEnabled]="true">
          <jrs-tree-view
            [nodes]="groups"
            [isDropDown]="false"
            [load]="groupLoading"
            [selectedNode]="selectedGroup"
            [contextActions]="groupActions"
            [hasMenu]="true"
            (nodeSelect)="changeGroup($event)"
            (actionSelected)="actionTriggered($event)">
          </jrs-tree-view>
        </jrs-scroll>
      </jrs-section>
      <jrs-section [height]="'72vh'" [minWidth]="'fit-content'" [width]="'unset'" [background]="'section-background-secondary'">
        <div class="flex place-flex-end margin-bottom">
          <jrs-label [size]="'medium'" [text]="getTotal()"></jrs-label>
          <span class="spacer"></span>
          <div>
            <jrs-button
              [size]="'long'"
              [title]="'Асуулт нэмэх'"
              (clicked)="navigateToCreate()">
            </jrs-button>
          </div>
          <div class="margin-left">
            <jrs-dropdown-view
              [defaultValue]="selectedCategoryName"
              [values]="categories"
              [label]="'Асуултын ангилал'"
              [color]="'light'"
              [width]="'180px'"
              [outlined]="true"
              [load]="categoryLoading"
              [tooltip]="true"
              [size]="'medium'"
              (selectedValue)="changeCategory($event)">
            </jrs-dropdown-view>
          </div>
        </div>
        <jrs-dynamic-table
          [notFoundText]="notFountText"
          [loading]="tableLoading"
          [minWidth]="'unset'"
          [maxWidth]="'unset'"
          [tableMinHeight]="'61vh'"
          [dataSource]="questionData"
          [contextActions]="questionActions"
          (selectedAction)="actionTriggered($event)"
          (rowSelected)="questionSelected($event)"
          [tableColumns]="columns">
        </jrs-dynamic-table>
      </jrs-section>
    </div>
  `,
  styleUrls: ['./question-bank.component.scss']
})
export class QuestionBankComponent implements OnInit {
  notFountText = 'Асуулт олдсонгүй';
  groups: GroupNode[] = [];
  tableLoading: boolean;
  columns = QUESTION_BANK_COLUMNS;
  questionActions = QUESTION_ACTIONS;
  categories: CategoryItem[] = [];
  groupActions = [];
  selectedCategoryName: string;
  categoryLoading: boolean;
  groupLoading: boolean;
  questionData: QuestionBank[] = [];
  selectedGroup: GroupNode;

  private selectedCategory: CategoryItem;
  private selectedQuestion: QuestionBank;
  private loading: boolean;


  constructor(private sb: ExamSandboxService) {
    this.groupActions = this.sb.filterPermission(QUESTION_GROUP_ACTIONS)
  }

  ngOnInit(): void {
    this.categoryLoading = true;
    this.sb.getQuestionCategories().subscribe(res => {
      this.categories = res;
      this.categories.unshift(ALL_CHOICE);
      this.selectedCategory = this.sb.getQuestionCategory() ? this.sb.getQuestionCategory() : this.categories[0];
      this.selectedCategoryName = this.selectedCategory.name;
      this.getGroups();
      this.categoryLoading = false;
    }, () => {
      this.categoryLoading = false;
    });
  }

  navigateToCreate(): void {
    this.sb.navigateByUrl('exam/question/create');
  }

  questionSelected(question: QuestionBank): void {
    this.selectedQuestion = question;
  }

  actionTriggered(event): void {
    const action = event.action;
    const node= event.node;
    switch (action) {
      case QUESTION_DELETE:
        this.deleteQuestion();
        break;
      case QUESTION_EDIT:
        this.sb.navigateByUrl('exam/question/update/' + this.selectedQuestion.id);
        break;
      case GROUP_EDIT:
        this.openEditQuestionGroupDialog(event.node);
        break;
      case GROUP_DELETE:
        this.deleteQuestionGroup(node);
        break;
      default:
        break;
    }

  }

  changeCategory(category): void {
    this.selectedCategory = category;
    this.selectedCategoryName = this.selectedCategory.name;
    this.sb.setQuestionCategory(category);
    this.updateQuestionBank();
  }

  changeGroup(group: GroupNode): void {
    this.selectedGroup = group;
    this.updateQuestionBank();
  }

  getTotal(): string {
    return `НИЙТ / ${this.questionData ? this.questionData.length.toString() : '0'}  /`
  }

  openEditQuestionGroupDialog(currentGroup: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Асуултын групп засах";
    config.data = {
      name: currentGroup.name
    }
    this.sb.openDialog(EditExamGroupDialogComponent, config).afterClosed.subscribe(newGroup => {
      if (newGroup) {
        this.loading = true;
        const groupName = newGroup.trim();
        const found = this.findDuplicatedGroups(this.groups, groupName);
        if (found) {
          this.loading = false;
          this.sb.snackbarOpen("Давхардсан утга байна");
        } else {
          this.sb.updateQuestionGroup(currentGroup.id, groupName).subscribe(() => {
            this.loading = false;
            currentGroup.name = groupName;
            this.sb.snackbarOpen("Асуултын групп амжилттай шинэчлэгдлээ.", true);
          }, () => {
            this.loading = false;
            this.sb.snackbarOpen("Асуултын групп засварлахад алдаа гарлаа!");
          });
        }
      }
    });
  }

  openAddQuestionGroupDialog(node?: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Асуултын групп нэмэх";
    config.data = {
      parent: node
    }
    this.sb.openDialog(AddExamGroupDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
        this.loading = true;
        const groupName = res.groupName.trim();
        const found = this.findDuplicatedGroups(this.groups, groupName);
        if (found) {
          this.loading = false;
          this.sb.snackbarOpen("Давхардсан утга байна");
        } else {
          const parentId = res.parent ? res.parent.id : null;
          this.sb.addQuestionGroup(res.id, groupName).subscribe((id) => {
            this.loading = false;
            const newExamGroup: GroupNode = {
              id,
              parent: parentId,
              name: res.groupName,
              children: []
            }
            this.groups.push(newExamGroup);
            this.sb.snackbarOpen("Асуултын групп амжилттай үүсгэлээ.", true);
          }, () => {
            this.loading = false;
            this.sb.snackbarOpen("Асуултын групп үүсгэхэд алдаа гарлаа!");
          })
        }
      }
    })
  }

  private findDuplicatedGroups(questionGroups: GroupNode[], newName: string): GroupNode {
    let found = questionGroups.find(group => group.name === newName)
    if (!found) {
      for (const group of questionGroups) {
        found = this.findDuplicatedGroups(group.children, newName);
        if (found) {
          break;
        }
      }
    }
    return found;
  }

  private updateQuestionBank(): void {
    this.loading = true;
    this.sb.getActiveQuestions(this.selectedCategory.id, this.selectedGroup.id).subscribe(res => {
      this.questionData = res;
      this.loading = false;
    }, () => {
      this.sb.snackbarOpen("Асуултууд ачаалахад алдаа гарлаа")
      this.loading = false;
    });
  }

  private getGroups(): void {
    this.loading = true;
    this.sb.getQuestionGroups().subscribe(res => {
      this.groups = res;
      this.selectedGroup = this.groups[0];
      this.loading = false;
      this.updateQuestionBank();
    }, () => {
      this.sb.snackbarOpen("Асуултын групп ачаалахад алдаа гарлаа");
      this.loading = false;
    })
  }

  private deleteQuestion(): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Асуулт устгах уу?";
    config.data = {
      info: (`Та '${this.selectedQuestion.value}' нэртэй асуулт устгахдаа итгэлтэй байна уу?\n
      Устгасан асуултыг дахин сэргээх боломжгүйг анхаарна уу.`)
    };
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.deleteQuestion(this.selectedQuestion.id).subscribe(() => {
          this.loading = false;
          this.updateQuestionBank();
          this.sb.snackbarOpen("Асуулт амжилттай устгалаа", true);
        }, () => {
          this.loading = false;
          this.sb.snackbarOpen("Асуулт устгахад алдаа гарлаа");
        });
      }
    });
  }

  private deleteQuestionGroup(currentGroup: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Асуултын групп устгах уу?";
    config.data = {
      info: `Та ${currentGroup.name} группийг устгахдаа итгэлтэй байна уу?` +
        `\nГруппэд бүртгэлтэй бүх асуулт устахыг анхаарна уу. ` +
        `Устгасан группийг дахин сэргээх боломжгүй.`
    }
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(deleteGroup => {
      if (deleteGroup) {
        this.loading = true;
        this.sb.deleteQuestionGroup(currentGroup.id).subscribe((deleted) => {
          this.loading = false;
            this.sb.snackbarOpen("Асуултын группийг амжилттай устгалаа!", true);
            this.removeFromTrees(currentGroup);
        }, () => {
          this.loading = false;
          this.sb.snackbarOpen("Асуултын группийг устгахад алдаа гарлаа!");
        });
      }
    });
  }

  private removeFromTrees(deleteQuestionGroup: GroupNode): void {
    let foundIndex;
    foundIndex = this.groups.findIndex(res => res.id === deleteQuestionGroup.id);
    if (foundIndex != undefined) {
      this.groups.splice(foundIndex, 1);
    }
  }
}
