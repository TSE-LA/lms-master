import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UserManagementContainerComponent} from './user-management-container/user-management-container/user-management-container.component';
import {SharedModule} from "../../../../shared/src";
import {RouterModule} from "@angular/router";
import {JarvisCommonModule} from "../common/common.module";
import {ReactiveFormsModule} from "@angular/forms";
import {UserCreateDialogComponent} from './user-management-container/user-create-dialog/user-create-dialog.component';
import {ProfileContainerComponent} from "./profile-container/profile-container.component";
import {EditProfileContainerComponent} from "./edit-profile-container/edit-profile-container.component";
import {ViewProfileContainerComponent} from "./view-profile-container/view-profile-container.component";
import { UserUpdateDialogComponent } from './user-management-container/user-update-dialog/user-update-dialog.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatDatepickerModule} from "@angular/material/datepicker";


@NgModule({
  declarations: [
    UserManagementContainerComponent,
    UserCreateDialogComponent,
    ProfileContainerComponent,
    EditProfileContainerComponent,
    ViewProfileContainerComponent,
    UserUpdateDialogComponent
  ],
  imports: [
    SharedModule,
    JarvisCommonModule,
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatDatepickerModule
  ]
})
export class UserManagementModule {
}
