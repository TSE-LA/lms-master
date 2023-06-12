import {Component, OnInit, ViewChild} from '@angular/core';
import {OnlineCourseModel} from "../../../online-course/models/online-course.model";
import {TreeViewCheckboxComponent} from "../../../../../../shared/src/lib/tree-view-checkbox/tree-view-checkbox.component";
import {GroupNode, LearnerInfo, PublishStatus} from "../../../../../../shared/src/lib/shared-model";
import {FILTER_ROLES} from "../../../common/common.model";
import {ActivatedRoute} from "@angular/router";
import {TimedCourseSandboxService} from "../../services/timed-course-sandbox.service";
import {GroupUtil} from "../../../util/group-util";

@Component({
  selector: 'jrs-timed-course-group-enrollment-page',
  template: `
    <jrs-button
      [iconName]="'arrow_back_ios'"
      [iconColor]="'secondary'"
      [noOutline]="true"
      [isMaterial]="true"
      [size]="'medium'"
      [bold]="true"
      [textColor]="'text-link'"
      (clicked)="goBackToUpdatePage()">БУЦАХ
    </jrs-button>
    <jrs-section [minWidth]="'75vw'" [width]="'75vw'">
      <jrs-header-with-filter *ngIf="!loading" [roles]="filterRoles"
                              [searchValues]="allUsers"
                              [filterBy]="'name'"
                              [title]="'ХАМРАГДАХ СУРАЛЦАГЧИД'"
                              [subtitle]="getEnrolledGroupAndUsersCount()"
                              (filterByName)="filterByName($event)"
                              (filterByRole)="filterByRole($event)"></jrs-header-with-filter>
      <jrs-skeleton-loader [load]="loading" [amount]="1"></jrs-skeleton-loader>
      <div class="row gx-5">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
          <jrs-scroll *ngIf="!loading" [height]="SCROLL_HEIGHT" [color]="'primary'">
            <jrs-tree-view-checkbox #groupEnrollment
                                    [nodes]="groups"
                                    [allItems]="allUsers"
                                    [selectedItems]="getUniqueUsers()"
                                    [showSelectedItemCount]="true"
                                    (bulkChecked)="bulkChecked($event)"
                                    (groupSelected)="groupSelected($event, true)"
                                    (groupChecked)="groupChecked($event)"></jrs-tree-view-checkbox>
          </jrs-scroll>
          <jrs-skeleton-loader [load]="loading" [amount]="5"></jrs-skeleton-loader>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-8 col-media_xl-8">
          <jrs-group-enrollment-section
            [allUsers]="allUsers"
            [users]="filteredUsers"
            [checkbox]="true"
            [group]="selectedGroup"
            [selectedUser]="this.searchedUser"
            [title]="this.selectedGroup.name"
            [roles]="filterRoles"
            (filterByName)="filterByName($event)"
            (filterByRole)="filterByRole($event)"
            (removeEnrolledUser)="removeUser($event)"></jrs-group-enrollment-section>
          <jrs-skeleton-loader [load]="userLoading" [amount]="6"></jrs-skeleton-loader>
        </div>
      </div>
      <div class="flex justify-end margin-top">
        <jrs-button [title]="'Хадгалах'" [size]="'medium'" (clicked)="saveEnrollment()"></jrs-button>
      </div>
    </jrs-section>
    <jrs-section [minWidth]="'75vw'" [width]="'75vw'">
      <div class="row gx-5">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-4">
          <jrs-column-list [dataSource]="selectedGroups"
                           [columns]="1"
                           [height]="'45vh'"
                           [rows]="selectedGroups.length"
                           [notFoundText]="'Сонгогдсон групп олдсонгүй'">
          </jrs-column-list>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-8">
          <jrs-group-enrollment-section
            [load]="loading"
            [checkbox]="false"
            [users]="detailedSelectedUsers">
          </jrs-group-enrollment-section>
        </div>
      </div>
    </jrs-section>
    <jrs-page-loader [show]="saving" [text]="'Хадгалж байна...'"></jrs-page-loader>`,
})
export class TimedCourseGroupEnrollmentPageComponent implements OnInit {
  @ViewChild('groupEnrollment') groupEnrollment: TreeViewCheckboxComponent;
  onlineCourse: OnlineCourseModel;
  published = false;
  groups: GroupNode[];
  courseId: string;
  loading: boolean;
  detailedSelectedUsers: any[] = [];
  selectedGroups: string[] = [];
  userLoading: boolean;
  saving: boolean;
  groupCount = 0;
  filterRoles = FILTER_ROLES;
  selectedRole = 'БҮГД';
  SCROLL_HEIGHT = '40vh'
  allUsers: LearnerInfo[] = []
  filteredUsers: LearnerInfo[] = [];
  staticFilteredUsers: LearnerInfo[] = [];
  selectedUsers: string[] = [];
  searchedUser: LearnerInfo;
  selectedGroup: GroupNode = {
    parent: "",
    id: "",
    name: "",
    nthSibling: 0
  };

  constructor(private sb: TimedCourseSandboxService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.loading = true;
    this.sb.getAllUsers().subscribe(res => {
      this.allUsers = res;
      this.route.paramMap.subscribe(params => {
        this.courseId = params.get('id');
        this.loading = true;
        this.sb.getTimedCourseById(this.courseId).subscribe(res => {
            this.onlineCourse = res;
            this.selectedUsers = res.enrollments.users;
            this.setSelectedUsers();
            this.sb.getTimedCourseEnrollmentGroup(this.onlineCourse.id).subscribe(res => {
              this.groups = res;
              this.loading = false;
              this.groups[0].showChildren = true;
              this.groupSelected(this.groups[0]);
              this.updateSavedEnrollments();
              this.groupCount = this.getGroupCount(this.groups);
            }, () => {
              this.loading = false;
              this.sb.openSnackbar('Групп ачаалахад алдаа гарлаа');
            })
            if (this.onlineCourse.publishStatus == PublishStatus.PUBLISHED) {
              this.published = true;
            }
          }
          , () => {
            this.loading = false;
            this.sb.openSnackbar('Цахим сургалт ачаалахад алдаа гарлаа');
          }
        )
      })
    }, () => {
      this.loading = false;
      this.sb.openSnackbar('Хэрэглэгчид ачаалахад алдаа гарлаа');
    })
  }


  getUniqueUsers(): string[] {
    return this.selectedUsers.filter((user, index, self) => {
      return index === self.indexOf(user);
    });
  }

  selectedGroupCount(): number {
    return this.groupEnrollment ? this.groupEnrollment.getEnrolledGroupIds().length : 0;
  }

  getEnrolledUsers(): string[] {
    const users = [];
    this.allUsers.forEach(user => {
      if (user.selected) {
        users.push(user.name);
      }
    });
    return users;
  }

  getGroupCount(node: GroupNode[]): number {
    let children = 0;
    node.forEach(node => {
      children++;
      children += this.getGroupCount(node.children);
    })
    return children;
  }

  getEnrolledGroupAndUsersCount(): string {
    return `Групп: ${this.selectedGroupCount()}/${this.getGroupCount(this.groups)} \u00A0\u00A0 Суралцагч ${this.getUniqueUsers().length}/${this.allUsers.length}`
  }

  saveEnrollment(): void {
    const groupIds = this.groupEnrollment.getEnrolledGroupIds();
    this.selectedUsers = this.getEnrolledUsers();
    this.saving = true;
    this.sb.saveEnrollment(this.selectedUsers, groupIds, this.courseId).subscribe(() => {
      this.sb.openSnackbar('Цахим сургалтын элсэлт амжилттай хадгаллаа', true)
      this.saving = false;
    }, () => {
      this.sb.openSnackbar("Цахим сургалтын элсэлт хадгалахад алдаа гарлаа!");
      this.saving = false;
    })
    this.updateSavedEnrollments();
  }

  removeUser(user: LearnerInfo): void {
    const users = this.getUniqueUsers();
    if (!user.selected) {
      this.selectedUsers = users.filter(u => u !== user.name);
    } else {
      this.selectedUsers = this.getUniqueUsers();
    }
  }

  groupChecked(group: GroupNode): void {
    this.selectedGroup = group;
    this.filterUsers();
    this.enrollUsers(group);
    this.selectedUsers = this.getEnrolledUsers();
  }

  enrollUsers(group: GroupNode): void {
    this.allUsers.filter(user => user.groupId === group.id).forEach(user => {
        user.selected = group.checked;
        if (!group.checked) {
          this.removeUser(user);
        }
      }
    )
  }

  goBackToUpdatePage(): void {
    this.sb.navigateByUrl(`/timed-course/update/${this.onlineCourse.id}`);
  }

  filterUsers(): void {
    this.filteredUsers = this.allUsers.filter(user => user.groupId === this.selectedGroup.id);
  }

  groupSelected(group: GroupNode, eraseHighlight?: boolean): void {
    if (this.selectedGroup !== group) {
      this.selectedUsers = this.getEnrolledUsers();
      this.selectedGroup = group;
      this.filterUsers();
      this.staticFilteredUsers = this.filteredUsers;
      this.filterByRole(this.selectedRole);
    }
    if (eraseHighlight) {
      this.searchedUser = undefined;
    }
  }


  bulkChecked(group: GroupNode): void {
    this.enrollUsers(group);
    this.selectedUsers = this.getEnrolledUsers();
  }

  setSelectedUsers(): void {
    this.allUsers.forEach(user => {
      if (this.selectedUsers.includes(user.name)) {
        user.selected = true;
      }
    })
  }


  filterByName(user: LearnerInfo): void {
    const group = GroupUtil.findGroup(this.groups, user.groupId)
    if (group != this.selectedGroup) {
      setTimeout(() => {
        this.searchedUser = user;
      }, 500)
    } else {
      this.searchedUser = user;
    }
    this.groupSelected(group, false);
    this.filterByRole(this.selectedRole);
  }

  filterByRole(role: string): void {
    this.selectedRole = role;
    this.filteredUsers = role !== 'БҮГД' ? this.staticFilteredUsers.filter(user => user.role === role) : this.staticFilteredUsers;
  }

  private updateSavedEnrollments(): void {
    this.detailedSelectedUsers = this.allUsers.filter(user => this.selectedUsers.includes(user.name));
    this.selectedGroups = [];
    this.getEnrolledGroupNames(this.groups);
  }

  private getEnrolledGroupNames(groups: GroupNode[]): void{
    groups.forEach(node => {
      if (node.checked) {
        this.selectedGroups.push(node.name);
      }
      if(node.children.length > 0){
        this.getEnrolledGroupNames(node.children);
      }
    })
  }
}
