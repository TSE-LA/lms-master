import {Component, OnInit} from '@angular/core';
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {ALL_CHOICE} from "../../../common/common.model";
import {EXAM_ACTIONS, EXAM_BANK_COLUMNS, EXAM_DELETE, EXAM_EDIT} from "../../model/exam.constants";
import {ExamBank} from "../../model/exam.model";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {CategoryItem, GroupNode} from "../../../../../../shared/src/lib/shared-model";
import {GROUP_ACTIONS, GROUP_ADD, GROUP_DELETE, GROUP_EDIT} from "../../../group-management/model/group.model";
import {EditExamGroupDialogComponent} from "../edit-exam-group-dialog/edit-exam-group-dialog.component";
import {AddExamGroupDialogComponent} from "../add-exam-group-dialog/add-exam-group-dailog.component";


@Component({
  selector: 'jrs-exam-bank',
  template: `
    <div class="container-tree-view">
      <jrs-section [overflow]="true" [height]="'72vh'" [minWidth]="'fit-content'" [width]="'200px'"
                   [background]="'section-background-secondary'">
        <div class="tree-flex">
          <jrs-button
            [color]="'primary'"
            [size]="'long'"
            [title]="'Групп нэмэх'"
            [width]="'auto'"
            [iconColor]="'light'"
            (click)="openAddExamGroupDialog()">
          </jrs-button>
        </div>
        <jrs-scroll [height]="'calc(100% - 50px)'" [color]="'primary'">
          <jrs-tree-view
            [nodes]="examGroups"
            [isDropDown]="false"
            [selectedNode]="selectedGroup"
            [contextActions]="groupActions"
            [hasMenu]="true"
            (nodeSelect)="changeGroup($event)"
            (actionSelected)="actionTriggered($event)">
          </jrs-tree-view>
        </jrs-scroll>
      </jrs-section>
      <jrs-section [height]="'72vh'" [minWidth]="'fit-content'" [width]="'unset'"
                   [background]="'section-background-secondary'">
        <div class="flex place-flex-end margin-bottom">
          <jrs-label [size]="'medium'" [text]="getTotal()"></jrs-label>
          <span class="spacer"></span>
          <div>
            <jrs-button
              [size]="'long'"
              [title]="'Шалгалт нэмэх'"
              (clicked)="navigateToCreate()">
            </jrs-button>
          </div>
          <div class="margin-left">
            <jrs-dropdown-view
              [defaultValue]="selectedCategoryName"
              [values]="categories"
              (selectedValue)="changeCategory($event)"
              [label]="'Шалгалтын ангилал'"
              [color]="'light'"
              [width]="'180px'"
              [outlined]="true"
              [load]="categoryLoading"
              [tooltip]="true"
              [size]="'medium'">
            </jrs-dropdown-view>
          </div>
        </div>
        <jrs-dynamic-table
          [notFoundText]="notFoundText"
          [loading]="groupLoading"
          [minWidth]="'unset'"
          [maxWidth]="'unset'"
          [tableMinHeight]="'61vh'"
          [dataSource]="examData"
          [contextActions]="examTableActions"
          (selectedAction)="actionTriggered($event)"
          [tableColumns]="columns">
        </jrs-dynamic-table>
      </jrs-section>
    </div>
    <jrs-page-loader [show]="creatingExamGroup||deletingExamGroup||updatingExamGroup"></jrs-page-loader>
  `,
  styles: []
})
export class ExamBankComponent implements OnInit {
  columns = EXAM_BANK_COLUMNS;
  examTableActions = EXAM_ACTIONS;
  examData: ExamBank[] = []
  loading = false;
  notFoundText = 'Шалгалт олдсонгүй';
  selectedCategoryName: string;
  categories: CategoryItem[] = [];

  selectedCategory: CategoryItem;
  groupActions = [];
  categoryLoading = false;
  groupLoading = false;

  deletingExamGroup = false;
  updatingExamGroup = false;
  creatingExamGroup = false;

  examGroups: GroupNode[] = [];
  selectedGroup: GroupNode = {
    parent: "",
    id: "",
    name: "",
    children: [],
    nthSibling: 0
  };

  constructor(private sb: ExamSandboxService) {
    this.groupActions = this.sb.filterPermission(GROUP_ACTIONS);
  }

  goBack(): void {
    this.sb.goBack();
  }

  ngOnInit(): void {
    this.categoryLoading = true;
    this.getExamGroups();
    this.sb.getExamCategories().subscribe(res => {
      this.categoryLoading = false;
      this.categories = res;
      this.categories.unshift(ALL_CHOICE);
      this.selectedCategory = this.sb.getExamCategory() ? this.sb.getExamCategory() : this.categories[0];
      this.selectedCategoryName = this.selectedCategory.name;
      this.sb.setExamCategory(this.selectedCategory);
      this.updateExamBank()
    }, () => {
      this.categoryLoading = false;
      this.sb.snackbarOpen("Шалгалтын ангилал ачаалахад алдаа гарлаа!")
    })
  }


  getTotal(): string {
    return 'НИЙТ /' + (this.examData ? this.examData.length.toString() : '0') + '/'
  }

  navigateToCreate(): void {
    this.sb.navigateByUrl('/exam/create');
  }

  actionTriggered(event): void {
    const action = event.action;
    const node: GroupNode = event.node;
    switch (action) {
      case EXAM_DELETE:
        this.deleteExam(event.id);
        break;
      case EXAM_EDIT:
        this.sb.navigateByUrl('exam/update/' + event.id);
        break;
    }
    switch (event.action) {
      case GROUP_EDIT:
        this.openEditExamGroupDialog(event.node);
        break;
      case GROUP_ADD:
        this.openAddExamGroupDialog(event.node);
        break;
      case GROUP_DELETE:
        this.deleteExamGroup(node);
        break;
      default:
        break;
    }
  }

  changeCategory(category): void {
    this.loading = true;
    this.selectedCategory = category;
    this.sb.setExamCategory(this.selectedCategory);
    this.selectedCategoryName = this.selectedCategory.name;
    this.updateExamBank();
  }

  changeGroup(group: GroupNode): void {
    this.selectedGroup = group;
    this.updateExamBank();
  }

  openAddExamGroupDialog(currentGroup?: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Групп нэмэх";
    config.data = {
      parent: currentGroup
    }
    this.sb.openDialog(AddExamGroupDialogComponent, config).afterClosed.subscribe(groupData => {
      if (groupData) {
        this.creatingExamGroup = true;
        const examGroupName = groupData.groupName.trim().toLowerCase();
        const found = this.findDuplicatedGroups(this.examGroups, examGroupName);
        if (found) {
          this.creatingExamGroup = false;
          this.sb.snackbarOpen("Давхардсан утга байна");
        } else {
          const parentId = groupData.parent ? groupData.parent.id : null;
          this.sb.addExamGroup(parentId, examGroupName).subscribe((id) => {
            this.creatingExamGroup = false;
            const newExamGroup: GroupNode = {
              id,
              parent: parentId,
              name: groupData.groupName,
              children: []
            }
            this.addExamGroupName(newExamGroup, groupData.parent);
            this.openCurrentGroup(this.examGroups, currentGroup);
            this.sb.snackbarOpen("Шалгалтын групп амжилттай үүсгэлээ.", true);
          }, () => {
            this.creatingExamGroup = false;
            this.sb.snackbarOpen("Шалгалтын групп үүсгэхэд алдаа гарлаа!");
          })
        }

      }
    })
  }

  openCurrentGroup(examGroups: GroupNode[], currentGroup: GroupNode): void {
    for (const group of examGroups) {
      if (group.id === currentGroup.id) {
        group.showChildren = true;
        break;
      } else {
        this.openCurrentGroup(group.children, currentGroup)
      }
    }
  }

  addExamGroupName(newExamGroup: GroupNode, node: GroupNode): void {
    if (node) {
      node.children.push(newExamGroup);
    } else {
      this.examGroups.push(newExamGroup);
    }
  }

  openEditExamGroupDialog(currentGroup: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Групп засах";
    config.data = {
      name: currentGroup.name
    }
    this.sb.openDialog(EditExamGroupDialogComponent, config).afterClosed.subscribe(newGroup => {
      if (newGroup) {
        this.updatingExamGroup = true;
        const examGroupNames = newGroup.trim();
        const found = this.findDuplicatedGroups(this.examGroups, examGroupNames);
        if (found) {
          this.updatingExamGroup = false;
          this.sb.snackbarOpen("Давхардсан утга байна");
        } else {
          this.sb.updateExamGroupName(newGroup.id, currentGroup, examGroupNames).subscribe(() => {
            this.updatingExamGroup = false;
            currentGroup.name = examGroupNames;
            this.sb.snackbarOpen("Групп амжилттай шинэчлэгдлээ.", true);
          }, () => {
            this.updatingExamGroup = false;
            this.sb.snackbarOpen("Групп засварлахад алдаа гарлаа!");
          });
        }
      }
    });
  }

  private findGroup(examGroups: GroupNode[], id: string): GroupNode {
    let found = examGroups.find(group => group.id === id)
    if (!found) {
      for (const group of examGroups) {
        found = this.findGroup(group.children, id);
        if (found) {
          break;
        }
      }
    }
    return found;
  }

  private findDuplicatedGroups(examGroups: GroupNode[], newName: string): GroupNode {
    let found = examGroups.find(group => group.name === newName)
    if (!found) {
      for (const group of examGroups) {
        found = this.findDuplicatedGroups(group.children, newName);
        if (found) {
          break;
        }
      }
    }
    return found;
  }

  // TODO: Use here getExamsForBank instead of getAllExamsForBank
  private updateExamBank(): void {
    this.loading = true;
    if (this.selectedCategory == ALL_CHOICE) {
      this.sb.getExamsForBank(this.selectedGroup.id).subscribe(res => {
        this.examData = res;
        this.loading = false;
      }, () => {
        this.sb.snackbarOpen("Шалгалтын сан ачаалахад алдаа гарлаа");
        this.loading = false;
      });
    } else {
      this.sb.getExamsForBank(this.selectedGroup.id, this.selectedCategory.id).subscribe(res => {
        this.examData = res;
        this.loading = false;
      }, () => {
        this.sb.snackbarOpen("Шалгалтын сан ачаалахад алдаа гарлаа");
        this.loading = false;
      });
    }
  }

  private getExamGroups(): void {
    this.groupLoading = true;
    this.sb.getExamGroup().subscribe(examGroup => {
      this.groupLoading = false;
      this.examGroups = examGroup;
      this.selectedGroup = this.examGroups[0];
    }, () => {
      this.groupLoading = false;
      this.sb.snackbarOpen("Групп ачааллахад алдаа гарлаа!");
    });
  }

  private deleteExam(id: string): void {
    const exam = this.examData.find(exam => exam.id == id);
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Шалгалт устгах уу?";
    config.data = {
      info: (`Та '${exam.name}' нэртэй шалгалтыг устгахдаа итгэлтэй байна уу?\n
      Устгасан шалгалтыг дахин сэргээх боломжгүйг анхаарна уу.`)
    };
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.deleteExam(id).subscribe((deleted) => {
          this.loading = false;
          if (deleted) {
            this.updateExamBank();
            this.sb.snackbarOpen("Шалгалт амжилттай устгалаа", true);
          } else {
            this.sb.snackbarOpen("Шалгалт устгахад алдаа гарлаа");
          }
        }, () => {
          this.loading = false;
          this.sb.snackbarOpen("Шалгалт устгахад алдаа гарлаа");
        });
      }
    });
  }

  private deleteExamGroup(currentGroup: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Групп устгах уу?";
    config.data = {
      info: `Та ${currentGroup.name} группийг устгахдаа итгэлтэй байна уу?` +
        `\nГруппэд бүртгэлтэй бүх шалгалт устахыг анхаарна уу. ` +
        `Устгасан группийг дахин сэргээх боломжгүй.`
    }
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(deleteGroup => {
      if (deleteGroup) {
        this.deletingExamGroup = true;
        this.sb.deleteExamGroup(currentGroup.id).subscribe((deleted) => {
          this.deletingExamGroup = false;
          if (deleted) {
            this.sb.snackbarOpen("Шалгалтын группийг амжилттай устгалаа!", true);
            this.removeFromTree(currentGroup);
          } else {
            this.sb.snackbarOpen("Шалгалтын группийг устгахад алдаа гарлаа!");
          }
        }, () => {
          this.deletingExamGroup = false;
          this.sb.snackbarOpen("Шалгалтын группийг устгахад алдаа гарлаа!");
        });
      }
    });
  }

  private removeFromTree(deleteExamGroup: GroupNode): void {
    let foundIndex;
    if (deleteExamGroup.parent == null) {
      foundIndex = this.examGroups.findIndex(res => res.id === deleteExamGroup.id);
      if (foundIndex != undefined) {
        this.examGroups.splice(foundIndex, 1);
      }
    } else {
      let node = this.findGroup(this.examGroups, deleteExamGroup.parent);
      foundIndex = node.children.findIndex(res => res.id === deleteExamGroup.id);
      if (foundIndex != undefined) {
        node.children.splice(foundIndex, 1);
      }
    }
  }

}
