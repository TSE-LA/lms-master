import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GroupManagementContainerComponent } from './group-management-container/group-management-container.component';
import {SharedModule} from "../../../../shared/src";
import {RouterModule} from "@angular/router";
import {JarvisCommonModule} from "../common/common.module";
import {ReactiveFormsModule} from "@angular/forms";
import { AddUserDialogComponent } from './containers/add-user-dialog/add-user-dialog.component';
import { EditGroupDialogComponent } from './containers/edit-group-dialog/edit-group-dialog.component';
import { AddGroupDialogComponent } from './containers/add-group-dialog/add-group-dialog.component';



@NgModule({
  declarations: [
    GroupManagementContainerComponent,
    AddUserDialogComponent,
    EditGroupDialogComponent,
    AddGroupDialogComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule,
    JarvisCommonModule,
    ReactiveFormsModule
  ]
})
export class GroupManagementModule { }
