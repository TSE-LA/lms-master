import {Component} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {DialogRef} from "../../../../../../shared/src/lib/dialog/dialog-ref";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {GroupNode} from "../../../../../../shared/src/lib/shared-model";

@Component({
  selector: 'jrs-add-group-dialog',
  template: `
    <jrs-drop-down-tree-view
      *ngIf="selectedGroupName"
      [selectedGroupName]="selectedGroupName"
      [allGroups]="groups"
      [width]="'100%'"
      [label]="'Дээд групп'"
      [size]="'large'"
      (selectedNode)="selectGroup($event)">
    </jrs-drop-down-tree-view>
    <div class="margin-top">
      <jrs-input-field
        [formGroup]="formGroup"
        [formType]="'name'"
        [placeholder]="'Группийн нэр'"
        [selectedType]="'text'"
        [movePlaceholder]="true"
        [required]="true">
      </jrs-input-field>
    </div>

    <jrs-action-buttons [declineButton]="'Болих'" [submitButton]="'Үүсгэх'" (declined)="close()"
                        (submitted)="save()"></jrs-action-buttons>
  `,
  styles: []
})
export class AddExamGroupDialogComponent {
  formGroup = new FormGroup({
    name: new FormControl('')
  });
  groups: GroupNode[] = [];
  selectedGroupName: string;
  private selectedParentGroup: GroupNode;


  constructor(private dialog: DialogRef, private config: DialogConfig) {
    let parent = this.config.data.parent;
    if (parent) {
      this.selectedParentGroup = parent;
      this.groups = [this.selectedParentGroup]
      this.selectedGroupName = this.selectedParentGroup.name;
    }
  }

  close(): void {
    this.dialog.close();
  }

  save(): void {
    this.dialog.close({
      parent: this.selectedParentGroup ? this.selectedParentGroup : null,
      groupName: this.formGroup.controls.name.value
    });
  }

  selectGroup($event: GroupNode): void {
    this.selectedParentGroup = $event;
    this.selectedGroupName = this.selectedParentGroup.name;
  }
}
