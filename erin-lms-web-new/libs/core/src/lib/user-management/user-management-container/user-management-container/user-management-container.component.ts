import {Component, OnInit, ViewChild} from '@angular/core';
import {TableColumn} from "../../../../../../shared/src/lib/shared-model";
import * as userConst from "../../models/user-management.constant";
import {UserDetailedModel, UserModel, UserStatus} from "../../models/user-management.model";
import {UserCreateDialogComponent} from "../user-create-dialog/user-create-dialog.component";
import {UserManagementSandboxService} from "../../service/user-management-sandbox.service";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DynamicTableComponent} from "../../../../../../shared/src/lib/table/dynamic-table/dynamic-table.component";
import {FileUploadDialogComponent} from "../../../../../../shared/src/lib/dialog/file-upload-dialog/file-upload-dialog.component";
import {UserManagementMapper} from "../../models/user-management.mapper";
import {UserUpdateDialogComponent} from "../user-update-dialog/user-update-dialog.component";
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";

@Component({
  selector: 'jrs-user-management-container',
  template: `
    <jrs-section>
      <div class="flex">
        <jrs-header-text>Системийн хэрэглэгч</jrs-header-text>
        <jrs-header-text [bold]="false">{{' / Нийт: ' + dataSource.length + ' - Сонгосон: ' + totalSelected + ' /'}}</jrs-header-text>
        <span class="spacer"></span>
        <jrs-button
          *canAccess="IMPORT_BUTTON"
          class="margin-left"
          [size]="'icon-medium'"
          [jrsTooltip]="'Импорт'"
          [iconName]="'file_upload'"
          (clicked)="openImportDialog()">
        </jrs-button>
        <jrs-button
          *canAccess="EXPORT_BUTTON"
          class="margin-left"
          [jrsTooltip]="'Экспорт'"
          [size]="'icon-medium'"
          [iconName]="'file_download'"
          (clicked)="export()">
        </jrs-button>
        <jrs-button
          *canAccess="CREATE_BUTTON"
          class="margin-left"
          [title]="'Хэрэглэгч нэмэх'"
          [size]="'medium'"
          (clicked)="openCreateUserDialog()">
        </jrs-button>
      </div>

      <jrs-dynamic-table
        #userTable
        [height]="'100%'"
        [notFoundText]="notFoundText"
        [loading]="loading"
        [minWidth]="'unset'"
        [maxWidth]="'unset'"
        [dataPerPage]="10"
        [dataSource]="dataSource"
        [tableColumns]="tableColumn"
        [contextActions]="currentActions"
        [noCircle]="true"
        (rowSelected)="getContextActions($event)"
        (selectedAction)="actionSelected($event)"
        (selectedHeaderAction)="headerActionSelected($event)"
        (checkboxAction)="collectData($event)">
      </jrs-dynamic-table>
    </jrs-section>
    <jrs-page-loader [show]="downloading || importing || performingAction"></jrs-page-loader>
  `,
  styles: []
})
export class UserManagementContainerComponent implements OnInit {
  @ViewChild("userTable") userTable: DynamicTableComponent;
  notFoundText: string;
  loading: boolean;
  dataSource: UserDetailedModel[] = [];
  tableColumn: TableColumn[] = userConst.USER_TABLE_COLUMNS;
  editable = true;
  totalSelected = 0;
  currentActions: DropdownModel[] = [];

  CREATE_BUTTON = userConst.CREATE_BUTTON;
  IMPORT_BUTTON = userConst.IMPORT_BUTTON;
  EXPORT_BUTTON = userConst.EXPORT_BUTTON;
  confirmConstants = userConst.CONFIRM_DIALOG_CONSTANTS;
  contextActions = userConst.USER_MANAGEMENT_CONTEXT_ACTIONS

  downloading: boolean;
  importing: boolean;
  performingAction: boolean;

  private currentSelectedUser: UserDetailedModel;
  private selectedUsers: UserDetailedModel[] = [];

  constructor(public sb: UserManagementSandboxService) {
    this.sb.canEdit().subscribe((res: boolean) => {
      this.editable = res;
    }, () => {
      this.editable = false;
    });
  }

  ngOnInit(): void {
    this.refreshTable();
  }

  bulkDeletable(): boolean {
    return this.selectedUsers.filter(user => !user.deletable || user.status !== UserStatus.ACTIVE).length === 0;
  }

  openCreateUserDialog(): void {
    const config = new DialogConfig();
    config.title = "Хэрэглэгчийн бүртгэл";

    this.sb.openDialog(UserCreateDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
        this.dataSource = [...this.dataSource, res];
      }
    });
  }

  openUpdateUserDialog(updatingUser: UserModel): void {
    const config = new DialogConfig();
    config.title = "Хэрэглэгчийн мэдээлэл засах";
    config.data = {user: updatingUser};
    this.sb.openDialog(UserUpdateDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
        this.replaceUserInTable(res);
        this.refreshTable();
      }
    });
  }

  openBulkDeleteUsersDialog(): void {
    const config = new DialogConfig();
    const data = this.confirmConstants.find(constant => constant.type === 'bulk_delete');
    config.title = data.title;
    config.submitButton = "Устгах";
    config.declineButton = "Болих"
    config.data = {
      info: data.info
    }
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(isAccepted => {
      if (isAccepted) {
        this.performingAction = true;
        const userIds: string[] = [];
        for (const row of this.selectedUsers) {
          userIds.push(row.id);
        }
        this.sb.deleteUsers(userIds).subscribe(res => {
          if (res) {
            this.dataSource = this.dataSource.filter(tableItem => userIds.indexOf(tableItem.id) === -1);
            this.userTable.checkAllItem(false, "check");
            this.sb.openSnackbar(userConst.BULK_DELETE_USER_SUCCESS_MSG, true);
          }
          this.performingAction = false;
        }, () => {
          this.sb.openSnackbar(userConst.BULK_DELETE_USER_FAILURE_MSG, false);
          this.performingAction = false;
        });
      }
    });
  }

  openDeleteUserDialog(userToDelete: UserDetailedModel): void {
    const config = new DialogConfig();
    const data = this.confirmConstants.find(constant => constant.type === 'delete');
    config.title = data.title;
    config.submitButton = "Устгах";
    config.declineButton = "Болих"
    config.data = {
      info: data.info
    }
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(isAccepted => {
      if (isAccepted) {
        this.performingAction = true;
        this.sb.deleteUser(userToDelete.id).subscribe(res => {
          if (res) {
            this.dataSource = this.dataSource.filter(user => user.id !== userToDelete.id);
            this.sb.openSnackbar(userConst.DELETE_USER_SUCCESS_MSG, true);
          }
          this.performingAction = false;
        }, () => {
          this.sb.openSnackbar(userConst.DELETE_USER_FAILURE_MSG, false);
          this.performingAction = false;
        });
      }
    });
  }

  openBulkArchiveUsersDialog(archive: boolean): void {
    let type = 'bulk_archive';
    if (!archive) {
      type = 'bulk_unarchive';
    }
    const config = new DialogConfig();
    const data = this.confirmConstants.find(constant => constant.type === type)
    config.title = data.title;
    config.data = {info: data.info}
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(isAccepted => {
      if (isAccepted) {
        this.performingAction = true;
        const userIds: string[] = [];
        for (const user of this.selectedUsers) {
          userIds.push(user.id);
        }

        this.sb.archiveUsers({userIds, archived: !archive}).subscribe(res => {
          if (res) {
            for (const user of this.selectedUsers) {
              user.status = archive ? UserStatus.ARCHIVED : UserStatus.ACTIVE;
            }
            this.userTable.checkAllItem(false, "check");
            this.refreshTable();
            this.sb.openSnackbar(archive ? userConst.BULK_ARCHIVE_USER_SUCCESS_MSG : userConst.BULK_UNARCHIVE_USER_SUCCESS_MSG, true);
          }
          this.performingAction = false;
        }, () => {
          this.sb.openSnackbar(archive ? userConst.BULK_ARCHIVE_USER_FAILURE_MSG : userConst.BULK_UNARCHIVE_USER_FAILURE_MSG, false);
          this.performingAction = false;
        });
      }
    });
  }

  openArchiveUserDialog(user: UserDetailedModel, isUserArchived: boolean): void {
    let type = 'archive';
    if (isUserArchived) {
      type = 'unarchive';
    }

    const config = new DialogConfig();
    const data = this.confirmConstants.find(constant => constant.type === type)
    config.title = data.title;
    config.data = {info: data.info}
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(isAccepted => {
      if (isAccepted) {
        this.sb.archiveUser(user.id, isUserArchived).subscribe(res => {
          if (res) {
            user.status = isUserArchived ? UserStatus.ACTIVE : UserStatus.ARCHIVED;
            this.replaceUserInTable(user);
          }
          this.sb.openSnackbar(isUserArchived ? userConst.UNARCHIVE_USER_SUCCESS_MSG : userConst.ARCHIVE_USER_SUCCESS_MSG, true);
        }, () => {
          this.sb.openSnackbar(isUserArchived ? userConst.UNARCHIVE_USER_FAILURE_MSG : userConst.ARCHIVE_USER_FAILURE_MSG, false);
        });
      }
    });
  }

  openImportDialog(): void {
    const config = new DialogConfig();
    config.title = "Хэрэглэгч импортлох";
    config.submitButton = "Импорт"
    config.data = {
      info: "Та хэрэглэгч импортлох гэж байна.",
      fileTypes: ".csv",
      allowedRegexp: /(\.csv)$/i
    }
    this.sb.openDialog(FileUploadDialogComponent, config).afterClosed.subscribe(result => {
      if (result) {
        this.importing = true
        this.sb.importUsers(result.file).subscribe(res => {
          this.importing = false;
          if (res.duplicatedUsers.length > 0 || res.failedToRegisterUsers.length > 0) {
            let title = userConst.DUPLICATE_USER_TITLE;
            let alternativeMessage = UserManagementMapper.mapToDuplicatedUsersModel(res.duplicatedUsers) + userConst.DUPLICATE_USER_ERROR_TEXT;
            if (res.failedToRegisterUsers.length > 0) {
              title = userConst.WRONG_FORMAT_USER_ERROR_TEXT;
              alternativeMessage = UserManagementMapper.mapToDuplicatedUsersModel(res.failedToRegisterUsers) + userConst.USER_IMPORT_ERROR_TEXT;
            }
            const warningConfig = new DialogConfig();
            warningConfig.width = '400px';
            warningConfig.title = title;
            warningConfig.decline = false;
            warningConfig.submitButton = "Ойлголоо"
            warningConfig.data = {info: alternativeMessage}
            this.sb.openDialog(ConfirmDialogComponent, warningConfig);
          }
          if (res.registeredUserCount > 0) {
            this.sb.openSnackbar(userConst.IMPORT_USERS_SUCCESS_MSG, true);
          }
          if (res.registeredUserCount != 0) {
            this.refreshTable();
          }
        }, () => {
          this.importing = false;
          this.sb.openSnackbar(userConst.IMPORT_USERS_FAILURE_MSG, false);
        });
      }
    })
  }

  export(): void {
    this.downloading = true;
    this.sb.exportUsers().subscribe(() => {
      this.downloading = false;
      this.sb.openSnackbar(userConst.USER_DOWNLOAD_SUCCESS_MSG, true);
    }, () => {
      this.downloading = false;
      this.sb.openSnackbar(userConst.USER_DOWNLOAD_FAILURE_MSG, false);
    });
  }

  collectData(selectedUsers: UserDetailedModel[]): void {
    this.selectedUsers = selectedUsers;
    this.totalSelected = selectedUsers.length;
    this.setHeaderActions();
  }

  getContextActions(user: UserDetailedModel): void {
    this.currentSelectedUser = user;
    if (user.status == UserStatus.ACTIVE) {
      this.currentActions = userConst.USER_MANAGEMENT_CONTEXT_ACTIONS;
    } else {
      this.currentActions = userConst.USER_MANAGEMENT_CONTEXT_ACTIONS_ARCHIVED;
    }
  }

  actionSelected(event): void {
    switch (event.action) {
      case userConst.USER_MANAGEMENT_EDIT_USER:
        this.openUpdateUserDialog(this.currentSelectedUser);
        break;
      case userConst.USER_MANAGEMENT_ARCHIVE_USER:
        this.openArchiveUserDialog(this.currentSelectedUser, false);
        break;
      case userConst.USER_MANAGEMENT_UNARCHIVE_USER:
        this.openArchiveUserDialog(this.currentSelectedUser, true);
        break;
      case userConst.USER_MANAGEMENT_DELETE_USER:
        this.openDeleteUserDialog(this.currentSelectedUser);
        break;
      default:
        break;
    }
  }

  headerActionSelected(action): void {
    switch (action) {
      case userConst.USER_MANAGEMENT_ARCHIVE_USER:
        this.openBulkArchiveUsersDialog(true);
        break;
      case userConst.USER_MANAGEMENT_UNARCHIVE_USER:
        this.openBulkArchiveUsersDialog(false);
        break;
      case userConst.USER_MANAGEMENT_DELETE_USER:
        this.openBulkDeleteUsersDialog();
        break;
      default:
        break;
    }
  }

  private refreshTable(): void {
    this.loading = true;
    this.sb.getUsers().subscribe(res => {
      this.dataSource = res;
      this.loading = false;
    }, () => {
      this.sb.openSnackbar(userConst.GET_USERS_FAILURE_MSG, false);
      this.loading = false;
    });
  }

  private setHeaderActions(): void {
    if (this.bulkDeletable()) {
      this.currentActions = userConst.USER_BULK_CONTEXT_ACTIONS;
    } else {
      this.currentActions = userConst.USER_BULK_CONTEXT_ACTIONS_INDELIBLE
    }
  }

  private replaceUserInTable(res: UserDetailedModel) {
    const index = this.dataSource.findIndex(user => user.id == res.id);
    if (index != null) {
      const copy = [];
      Object.assign(copy, this.dataSource);
      copy.splice(index, 1, res);
      this.dataSource = copy;
    }
  }
}
