import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Group} from "../../../../common/common.model";
import {DEFAULT_EXAM, EXAM_TYPES} from "../../../model/exam.constants";
import {ExamSandboxService} from "../../../services/exam-sandbox.service";
import {FormUtil} from "../../../../../../../shared/src/lib/utilities/form.util";
import {CategoryItem, GroupNode} from "../../../../../../../shared/src/lib/shared-model";
import {ExamModel} from "../../../model/exam.model";

@Component({
  selector: 'jrs-exam-basic-info-section',
  template: `
    <form [formGroup]="examFormGroup">
      <jrs-section
        [color]="'primary'"
        [outline]="false">
        <div class="margin-top">
          <jrs-header-text [size]="'medium'" [margin]="false">ШАЛГАЛТЫН МЭДЭЭЛЭЛ</jrs-header-text>
        </div>

        <div class="row gx-1 margin-top-large">
          <div class="col-media_s-12 col-media_sm-6 col-media_md-6 col-media_xl-6">
            <jrs-input-field
              [placeholder]="'Шалгалтын нэр'"
              [formGroup]="examFormGroup"
              [formType]="'name'"
              [movePlaceholder]="true"
              [errorText]="'Заавал бөглөнө үү'"
              [selectedType]="'text'"
              [required]="true">
            </jrs-input-field>
          </div>
          <div class="col-media_s-12 col-media_sm-3 col-media_md-3 col-media_xl-3">
            <jrs-dropdown-view
              [placeholder]="'Шалгалтын ангилал'"
              [formGroup]="examFormGroup"
              [formType]="'category'"
              [icon]="'expand_more'"
              [outlined]="true"
              [errorText]="'Заавал сонгоно уу'"
              [padding]="true"
              [values]="examCategories"
              [required]="true"
              [load]="categoriesLoading"
              (selectedValue)="selectionChange($event)">
            </jrs-dropdown-view>
          </div>
          <div class="col-media_s-12 col-media_sm-3 col-media_md-3 col-media_xl-3">
            <jrs-drop-down-tree-view
              [allGroups]="examGroups"
              [selectedGroupName]="selectedGroupName"
              [placeholder]="'Шалгалтын групп'"
              [formGroup]="examFormGroup"
              [formType]="'group'"
              [required]="true"
              [errorText]="'Заавал сонгоно уу'"
              (selectedNode)="selectGroup($event)">
            </jrs-drop-down-tree-view>
          </div>
        </div>

        <!--          <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">-->
        <!--            <jrs-dropdown-view-->
        <!--              [placeholder]="'Шалгалтын төрөл'"-->
        <!--              [formGroup]="examFormGroup"-->
        <!--              [formType]="'type'"-->
        <!--              [icon]="'expand_more'"-->
        <!--              [errorText]="'Заавал сонгоно уу'"-->
        <!--              [outlined]="true"-->
        <!--              [padding]="true"-->
        <!--              [values]="examTypes"-->
        <!--              [required]="true">-->
        <!--            </jrs-dropdown-view>-->
        <!--          </div>-->
        <!--        </div>-->

        <div class="row gx-1">
          <div class="col-media_s-12 col-media_xl-12 col-media_md-12 col-media_sm-12">
            <jrs-text-area
              [placeholder]="'Товч агуулга'"
              [formGroup]="examFormGroup"
              [formType]="'summary'">
            </jrs-text-area>
          </div>
        </div>
      </jrs-section>
    </form>
  `,
  styles: []
})
export class ExamBasicInfoSectionComponent implements OnInit, OnChanges {
  @Input() exam: ExamModel = DEFAULT_EXAM;
  examFormGroup: FormGroup;
  examCategories: CategoryItem[];
  examGroups: Group[];
  examTypes = EXAM_TYPES;
  categoriesLoading: boolean;
  groupsLoading: boolean;
  selectedGroupName: string;

  constructor(private sb: ExamSandboxService) {
    this.setupForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'exam' && this.exam) {
        this.setupFormWithInitialValue();
      }
    }
  }

  ngOnInit(): void {
    this.categoriesLoading = true;
    this.sb.getExamCategories().subscribe(res => {
      this.examCategories = res;
      this.categoriesLoading = false;
    }, () => {
      this.sb.snackbarOpen('Шалгалтын ангилал ачаалахад алдаа гарлаа');
      this.categoriesLoading = false;
    });
    this.groupsLoading = true;
    this.sb.getExamGroup().subscribe(res => {
      this.examGroups = res;
      this.groupsLoading = false;
    }, () => {
      this.sb.snackbarOpen('Шалгалтын групп ачаалахад алдаа гарлаа');
      this.groupsLoading = false;
    })
  }

  isFormValid(): boolean {
    return FormUtil.isFormValid(this.examFormGroup);
  }

  selectionChange(category): void {
    this.sb.setExamCategory(category);
  }

  selectGroup(groupNode: GroupNode): void {
    this.selectedGroupName = groupNode.name;
    this.examFormGroup.controls.group.setValue(groupNode);
  }

  private setupForm(): void {
    this.examFormGroup = new FormGroup({
      name: new FormControl('', [Validators.required]),
      summary: new FormControl(''),
      category: new FormControl('', [Validators.required]),
      group: new FormControl('', [Validators.required])
    })
  }

  findGroup(examGroups: GroupNode[], id: string): GroupNode {
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

  private setupFormWithInitialValue(): void {
    this.categoriesLoading = true;
    this.sb.getExamCategories().subscribe(res => {
      this.examCategories = res;
      const category = res.find(res => res.id == this.exam.categoryId);
      this.examFormGroup.controls.category.setValue(category);
      this.examFormGroup.controls.name.setValue(this.exam.name);
      this.examFormGroup.controls.summary.setValue(this.exam.description);
      this.categoriesLoading = false;
    }, () => {
      this.sb.snackbarOpen('Шалгалтын ангилал ачаалахад алдаа гарлаа');
      this.categoriesLoading = false;
    });
    this.groupsLoading = true;
    this.sb.getExamGroup().subscribe(res => {
      let node = this.findGroup(res, this.exam.groupId);
      this.examFormGroup.controls.group.setValue(node);
      this.groupsLoading = false;
    }, () => {
      this.sb.snackbarOpen('Шалгалтын групп ачаалахад алдаа гарлаа');
      this.groupsLoading = false;
    });
  }
}
