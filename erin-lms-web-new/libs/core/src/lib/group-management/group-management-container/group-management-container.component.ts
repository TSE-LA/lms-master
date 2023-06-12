import {Component, OnInit} from '@angular/core';
import {GroupNode} from "../../../../../shared/src/lib/shared-model";
import {GROUP_ACTIONS, GROUP_ADD, GROUP_DELETE, GROUP_EDIT, User, USER_COLUMNS, USER_COLUMNS_NO_DELETE, USER_REMOVE} from "../model/group.model";
import {GroupManagementSandboxService} from "../services/group-management-sandbox.service";
import {RoleValueUtil} from "../../../../../shared/src/lib/utilities/role-value.util";
import {Membership, Role} from "../../common/common.model";
import {AddUserDialogComponent} from "../containers/add-user-dialog/add-user-dialog.component";
import {DialogConfig} from "../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {forkJoin, Observable, throwError} from "rxjs";
import {EditGroupDialogComponent} from "../containers/edit-group-dialog/edit-group-dialog.component";
import {AddGroupDialogComponent} from "../containers/add-group-dialog/add-group-dialog.component";
import {catchError, map} from "rxjs/operators";

@Component({
  selector: 'jrs-group-management-container',
  template: `
    <div class="container">
      <jrs-section [height]="'72vh'" [width]="'200px'" [minWidth]="'fit-content'" [background]="'section-background-secondary'">
        <div class="flex">
          <jrs-button
            [color]="'light'"
            [outline]="true"
            [size]="'icon-medium'"
            [width]="'150px'"
            [iconColor]="'dark'"
            [textColor]="'text-dark'"
            [title]="rootGroup.name"
            (clicked)="changeGroup(rootGroup)">
          </jrs-button>
          <span class="margin-left"></span>
          <jrs-button
            [color]="'primary'"
            [size]="'icon-medium'"
            [width]="'unset'"
            [iconName]="'add'"
            [iconColor]="'light'"
            (clicked)="openAddGroupDialog(rootGroup)">
          </jrs-button>
        </div>
        <jrs-scroll [height]="'calc(100% - 13px)'" [color]="'primary'" [horizontalScrollEnabled]="true">
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
          <jrs-label [size]="'medium'" [text]="currentGroupName + ' /' + currentGroupMembers.length + '/'"></jrs-label>
          <span class="spacer"></span>
          <div>
            <jrs-button
              [size]="'long'"
              [title]="'Хэрэглэгч нэмэх'"
              (clicked)="openAddUserDialog()">
            </jrs-button>
          </div>
          <div class="margin-left">
          </div>
        </div>
        <jrs-dynamic-table
          [notFoundText]="notFountText"
          [loading]="tableLoading"
          [minWidth]="'unset'"
          [maxWidth]="'unset'"
          [tableMinHeight]="'60vh'"
          [dataPerPage]="20"
          [dataSource]="currentGroupMembers"
          [contextActions]="userActions"
          (rowAction)="removeUser($event)"
          [tableColumns]="columns">
        </jrs-dynamic-table>
      </jrs-section>
    </div>
    <jrs-page-loader [show]="saving || deletingUser || deletingGroup || updatingGroup || creatingGroup"></jrs-page-loader>
  `,
  styleUrls: ['./group-management-container.component.scss']
})
export class GroupManagementContainerComponent implements OnInit {
  rootGroup: GroupNode = {
    parent: "",
    id: "",
    name: "",
    nthSibling: 0,
    children: []
  };
  groups: GroupNode[] = [];
  groupLoading = false;
  selectedGroup: GroupNode = {
    parent: "",
    id: "",
    name: "",
    nthSibling: 0,
    children: []
  };
  notFountText = "Группэд хэрэглэгч оноогоогүй байна"
  tableLoading = false;
  currentGroupMembers: User[] = [];
  userActions = [USER_REMOVE];
  columns = USER_COLUMNS;
  currentGroupName = "";
  roles: Role[] = [];
  saving = false;
  groupActions = [];
  deletingUser = false;
  deletingGroup = false;
  updatingGroup = false;
  creatingGroup = false;

  private allUsersMap = new Map<string, User>();
  private allNewUsers: User[] = [];
  private CREATE_READERSHIP = "user.promotion.createReadership";
  private DELETE_READERSHIP = "user.promotion.deleteReadership";
  private REMOVE_USER = "user.membership.delete";
  private createReadershipPermission = false;
  private deleteReadershipPermission = false;


  constructor(private sb: GroupManagementSandboxService) {
    this.createReadershipPermission = this.sb.isPermitted(this.CREATE_READERSHIP);
    this.deleteReadershipPermission = this.sb.isPermitted(this.DELETE_READERSHIP);
    this.columns = this.sb.isPermitted(this.REMOVE_USER) ? USER_COLUMNS : USER_COLUMNS_NO_DELETE;
    this.groupActions = this.sb.filterPermission(GROUP_ACTIONS);
  }

  ngOnInit(): void {
    const tasks = [];
    this.groupLoading = true;
    tasks.push(this.getGroups());
    tasks.push(this.getAllUsers());
    tasks.push(this.getAllRoles());
    forkJoin(tasks).subscribe(() => {
      this.changeGroup(this.rootGroup);
      this.groupLoading = false;
    }, () => {
      this.groupLoading = false;
    })
  }

  changeGroup(group: GroupNode): void {
    this.selectedGroup = group;
    this.currentGroupName = this.selectedGroup.name;
    this.tableLoading = true;
    this.updateMembers();
  }

  openAddUserDialog(): void {
    const config = new DialogConfig();
    config.width = "80vw";
    config.title = this.selectedGroup.name.toUpperCase() + " группэд хэрэглэгч нэмэх"
    config.data = {users: this.allNewUsers, roles: this.roles, groupId: this.selectedGroup.id, readership: this.createReadershipPermission};
    this.sb.openDialog(AddUserDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
          this.allNewUsers = [...res.newUsers];
          if (this.createReadershipPermission) {
            const tasks = [];
            let failedUsers = "Дараах ";
            this.saving = true;
            res.users.forEach(user => {
              tasks.push(this.sb.createPromotionReadership(this.selectedGroup.id, user.name).pipe(map(() => {
              }), catchError(err => {
                failedUsers += ", " + user.name;
                return throwError(err)
              })));
            });
            forkJoin(tasks).subscribe(() => {
              this.saving = false;
              this.updateMembers();
            }, () => {
              this.sb.openSnackbar(failedUsers + " хэрэглэгчид унших төлөвтэй урамшуулал үүсгэхэд алдаа гарлаа!");
              this.saving = false;
            })
          } else {
            this.saving = false;
            this.updateMembers();
          }
      }
    });
  }

  removeUser(event): void {
    const user: User = event.data;
    const config = new DialogConfig();
    config.title = "Суралцагчийг группээс хасах уу?";
    config.data = {
      info: 'Та ' + user.name + ' -г хасахдаа итгэлтэй байна уу?' +
        '\nХэрэглэгчийг группэд нэмэх хүртэл түр хугацаанд системд нэвтрэх боломжгүй болохыг анхаарна уу! ' +
        (this.deleteReadershipPermission ? 'Урамшуулалтай танилцсан прогресс устахгүй харин унших төлөвтэй урамшууллууд байхгүй болохыг анхаарна уу!' :
          '')
    }
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
        this.deletingUser = true;
        this.sb.deleteMember(user.membershipId).subscribe(() => {
          if (this.deleteReadershipPermission) {
            this.sb.deleteTimedCourseReaderships(user.name).subscribe(() => {
              this.deletingUser = false;
              this.updateMembers();
              this.sb.openSnackbar("Амжилттай группээс хаслаа.", true);
            }, () => {
              this.sb.openSnackbar("Унших урамшууллууд устгахад алдаа гарлаа!")
              this.deletingUser = false;
            });
          } else {
            this.deletingUser = false;
            this.updateMembers();
            this.sb.openSnackbar("Амжилттай группээс хаслаа.", true);
          }
        }, () => {
          this.sb.openSnackbar("Группээс хасахад алдаа гарлаа!")
          this.deletingUser = false;
        });
      }
    });
  }

  actionTriggered($event): void {
    const node: GroupNode = $event.node;
    switch ($event.action) {
      case GROUP_EDIT:
        this.openEditGroupDialog($event.node);
        break;
      case GROUP_ADD:
        this.openAddGroupDialog($event.node);
        break;
      case GROUP_DELETE:
        this.deleteGroup(node);
        break;
    }
  }

  openAddGroupDialog(node: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Групп нэмэх";
    config.data = {
      parent: node
    }
    this.sb.openDialog(AddGroupDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
        this.creatingGroup = true;
        const groupName = res.groupName.trim();
        this.sb.addGroup(res.parent, groupName).subscribe(() => {
          this.creatingGroup = false;
          this.sb.openSnackbar("Групп амжилттай үүсгэлээ.", true);
          this.getGroups().subscribe();
        }, () => {
          this.creatingGroup = false;
          this.sb.openSnackbar("Групп үүсгэхэд алдаа гарлаа!");
        })
      }
    })
  }

  private updateMembers(): void {
    this.sb.getGroupMembers(this.selectedGroup.id).subscribe((res: Membership[]) => {
      const members: User[] = [];
      for (const user of res) {
        const foundUser = this.allUsersMap.get(user.userId);
        members.push({
          name: user.userId,
          role: RoleValueUtil.getRoleDisplayName(user.roleId),
          groupId: user.groupId,
          firstname: foundUser ? foundUser.firstname : "-",
          lastname: foundUser ? foundUser.lastname : "-",
          membershipId: user.membershipId,
          notInGroup: !foundUser,
          selected: false
        })
      }
      this.currentGroupMembers = members;
      this.tableLoading = false;
    }, () => {
      this.sb.openSnackbar("Группийн хэрэглэгчид ачаалахад алдаа гарлаа!");
      this.tableLoading = false;
    })
  }

  private getGroups(): Observable<any> {
    return this.sb.getAllGroups(false).pipe(map((groups: GroupNode[]) => {
      this.rootGroup = groups[0];
      this.groups = this.rootGroup.children;
    }), catchError(err => {
      this.sb.openSnackbar("Групп ачааллахад алдаа гарлаа!")
      return throwError(err)
    }));
  }

  private getAllUsers(): Observable<any> {
    this.allNewUsers = [];
    return this.sb.getAllUsers().pipe(map(res => {
      this.allUsersMap = res;
      this.allUsersMap.forEach(user => {
        if (!user.groupId) {
          this.allNewUsers.push(user);
        }
      })
    }), catchError(err => {
      this.sb.openSnackbar("Суралцагчдыг харуулахад алдаа гарлаа!")
      return throwError(err)
    }));
  }

  private getAllRoles(): Observable<any> {
    return this.sb.getAllRoles().pipe(map(res => {
      this.roles = res;
    }), catchError(err => {
      this.sb.openSnackbar("Роль ачаалахад алдаа гарлаа");
      return throwError(err)
    }));
  }

  private deleteGroup(node: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Групп устгах уу?";
    config.data = {
      info: `Та ${node.name} группийг устгахдаа итгэлтэй байна уу?` +
        `\nГруппэд бүртгэлтэй бүх хэрэглэгчид устаж системд нэвтрэх эрхгүй \nболохыг анхаарна уу. ` +
        `Устгасан группийг дахин сэргээх боломжгүй.`
    }
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
        this.deletingGroup = true;
        const tasks = [];

        tasks.push(this.sb.deleteCourseRelations(node.id));
        if (this.deleteReadershipPermission) {
          tasks.push(this.sb.deleteTimedCourseRelations(node.id));
        }
        forkJoin(tasks).subscribe(() => {
          this.sb.deleteGroup(node.id).subscribe(() => {
            this.sb.openSnackbar("Групп амжилттай устгалаа!", true);
            this.getGroups().subscribe();
            this.deletingGroup = false;
          }, () => {
            this.deletingGroup = false;
            this.sb.openSnackbar("Групп устгахад алдаа гарлаа!");
          });
        }, () => {
          this.deletingGroup = false;
          this.sb.openSnackbar("Группийн холбоос устгахад алдаа гарлаа!");
        })
      }
    });
  }

  private openEditGroupDialog(node: GroupNode): void {
    const config = new DialogConfig();
    config.title = "Групп засах";
    config.data = {
      name: node.name
    }
    this.sb.openDialog(EditGroupDialogComponent, config).afterClosed.subscribe(res => {
      if (res) {
        this.updatingGroup = true;
        const groupName = res.trim();
        this.sb.updateGroupName(node.id, groupName).subscribe(() => {
          this.updatingGroup = false;
          node.name = groupName;
          this.sb.openSnackbar("Групп амжилттай шинэчлэгдлээ.", true);
        }, () => {
          this.updatingGroup = false;
          this.sb.openSnackbar("Групп засварлахад алдаа гарлаа!");
        });
      }
    });
  }

  private getUserIds(users: User[]): string[] {
    return users.map(user => user.name);
  }
}
