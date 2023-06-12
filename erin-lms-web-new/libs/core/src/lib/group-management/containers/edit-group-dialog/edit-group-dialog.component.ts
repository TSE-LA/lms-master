import {Component} from '@angular/core';
import {DialogRef} from "../../../../../../shared/src/lib/dialog/dialog-ref";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'jrs-edit-group-dialog',
  template: `
    <jrs-input-field
      [formGroup]="formGroup"
      [formType]="'name'"
      [placeholder]="'Группийн нэр'"
      [selectedType]="'text'"
      [movePlaceholder]="true"
      [required]="true">
    </jrs-input-field>
    <jrs-action-buttons [declineButton]="'Болих'" [submitButton]="'Хадгалах'" (declined)="close()" (submitted)="save()"></jrs-action-buttons>
  `,
  styles: []
})
export class EditGroupDialogComponent {
  formGroup = new FormGroup({
    name: new FormControl('')
  });

  constructor(private dialog: DialogRef, private config: DialogConfig) {
    this.formGroup.controls.name.setValue(this.config.data.name);
  }

  close(): void {
    this.dialog.close();
  }

  save(): void {
    this.dialog.close(this.formGroup.controls.name.value);
  }
}
