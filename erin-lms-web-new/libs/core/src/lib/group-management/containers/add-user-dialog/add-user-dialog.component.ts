import {Component} from '@angular/core';
import {DialogRef} from "../../../../../../shared/src/lib/dialog/dialog-ref";
import {User, USER_ADD_COLUMNS, UserCourses} from "../../model/group.model";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {Role, UserRoleProperties} from "../../../common/common.model";
import {GroupManagementSandboxService} from "../../services/group-management-sandbox.service";

@Component({
  selector: 'jrs-add-user-dialog',
  template: `
    <div [style.overflow]="'auto'" [style.height]="'80vh'" *ngIf="!approveRequestOn">
      <div class="row gx-1">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
          <jrs-dynamic-table
            [dataSource]="users"
            [tableColumns]="tableColumns"
            [notFoundText]="'Группгүй суралцагч байхгүй'"
            [minWidth]="'unset'"
            [maxWidth]="'unset'"
            [tableMinHeight]="'67vh'"
            (rowAction)="addToSelected($event)">
          </jrs-dynamic-table>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6 margin-top">
          <jrs-label [size]="'medium'" [text]="'СОНГОСОН ХЭРЭГЛЭГЧИД /' +  selectedUsers.length +'/'"></jrs-label>
          <div class="margin-top">
            <jrs-dropdown-view
              [label]="'Эрх'"
              [icon]="'expand_more'"
              [outlined]="true"
              [errorText]="'Заавал сонгоно уу'"
              [padding]="true"
              [values]="roles"
              [chooseFirst]="true"
              (selectedValue)="selectRole($event)">
            </jrs-dropdown-view>
          </div>

          <jrs-scroll [height]="'52vh'">
            <jrs-list [list]="selectedUsers" [notFoundText]="'Хэрэглэгч нэмнэ үү.'" (delete)="removeUser($event)"></jrs-list>
          </jrs-scroll>
        </div>
      </div>
      <jrs-action-buttons
        [justifyContent]="'end'"
        [declineButton]="'Болих'"
        [submitButton]="'Нэмэх'"
        (declined)="close()"
        (submitted)="addMembers()">
      </jrs-action-buttons>
    </div>
    <div *ngIf="approveRequestOn">
      <jrs-info-panel [text]="'ДАРААХ НӨХЦӨЛҮҮД УНШИХ ТӨЛӨВТЭЙ ОНООГДОНО'" [width]="'94%'"></jrs-info-panel>
      <jrs-scroll [height]="'50vh'">
        <div *ngFor="let userCourse of courseUserList; let order = index" class="margin-left">
          {{order + 1}}. {{userCourse.username}}
          <jrs-label [size]="'medium'" [text]="'нөхцөлийн тоо /' + userCourse.courses.length + '/'"></jrs-label>
          <ul *ngFor="let courseTitles of userCourse.courses">
            <li>{{courseTitles}}</li>
          </ul>
        </div>
      </jrs-scroll>
      <jrs-action-buttons
        [justifyContent]="'end'"
        [decline]="false"
        [submitButton]="'Үргэлжлүүлэх'"
        (submitted)="add()">
      </jrs-action-buttons>
    </div>
    <jrs-page-loader [show]="loading || savingNewMembers"></jrs-page-loader>
  `,
  styles: []
})
export class AddUserDialogComponent {
  users: User[] = [];
  tableColumns = USER_ADD_COLUMNS;
  selectedUsers: User[] = []
  roles: Role[] = [];
  selectedRole: Role;
  approveRequestOn = false;
  loading = false;
  savingNewMembers = false;
  readership = false;
  courseUserList: UserCourses[] = [];
  private groupId: string;

  constructor(private dialog: DialogRef, private config: DialogConfig, private sb: GroupManagementSandboxService) {
    this.users = config.data.users;
    this.roles = config.data.roles;
    this.groupId = config.data.groupId;
    this.readership = config.data.readership;
    this.selectedRole = this.roles[0];
  }

  close(): void {
    this.dialog.close();
  }

  addMembers(): void {
    this.savingNewMembers = true;
    this.sb.createMembers(this.groupId, this.selectedRole.id, this.selectedUsers).subscribe(() => {
      this.sb.openSnackbar("Амжилттай группэд нэмлээ.", true);
      this.savingNewMembers = false;
      if (this.readership && this.selectedRole.name !== UserRoleProperties.adminRole.id) {
        this.showApprovals();
      } else {
        this.add();
      }
    }, () => {
      this.sb.openSnackbar("Группэд нэмэхэд алдаа гарлаа!");
      this.savingNewMembers = false;
    });
  }

  showApprovals(): void {
    this.loading = true;
    this.sb.getLearnersReaderships(this.groupId, this.selectedUsers).subscribe((res: UserCourses[]) => {
      this.loading = false;
      this.approveRequestOn = true;
      this.courseUserList = res;
    }, (res) => {
      this.loading = true;
      // proceed to next step
      this.approveRequestOn = true;
      this.courseUserList = res;
    });
  }

  add(): void {
    this.dialog.close({role: this.selectedRole.id, users: this.selectedUsers, newUsers: this.users});
  }

  addToSelected(item): void {
    this.users = this.users.filter(users => users.name != item.data.name);
    this.selectedUsers.push(item.data);
    this.selectedUsers = [...this.selectedUsers];
  }

  removeUser(user): void {
    this.users.unshift(user);
    this.users = [...this.users];
    this.selectedUsers = this.selectedUsers.filter(selectedUser => selectedUser.name != user.name);
  }

  selectRole(role): void {
    this.selectedRole = role;
  }
}
