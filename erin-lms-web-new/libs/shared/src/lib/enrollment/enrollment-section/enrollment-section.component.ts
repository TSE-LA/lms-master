import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {BreakPointObserverService} from "../../theme/services/break-point-observer.service";
import {GroupNode, LearnerInfo} from "../../shared-model";

@Component({
  selector: 'jrs-enrollment-section',
  template: `
    <div *ngIf="!load" class="container">
      <div class="users-to-enroll">
        <div class="title-container">{{title}}</div>
        <div class="users-count">Сонгосон /{{selectedUsersToEnroll.length}}/</div>
        <div class="filters">
          <jrs-checkbox
            class="checkbox"
            [padding]="false"
            (click)="this.selectAllAvailableUsers()"
            [check]="allAvailableUsersSelected">
          </jrs-checkbox>
          <div class="fields">
            <jrs-input-field
              class="name-filter"
              [iconName]="'search'"
              [iconColor]="'gray'"
              [isMaterial]="true"
              [placeholder]="'Хайх'"
              [selectedType]="'text'"
              [style]="'icon-input-config'"
              [prefixIcon]="true"
              [padding]="false"
              (inputChanged)="filterUsersByName($event, true)">
            </jrs-input-field>
            <jrs-drop-down-tree-view
              class="group-dropdown"
              [placeholder]="availableUserGroupName"
              [allGroups]="groups"
              (selectedNode)="filterUsersByGroup($event, true)">
            </jrs-drop-down-tree-view>
            <jrs-dropdown-view
              class="role-dropdown"
              [defaultValue]="'Эрх'"
              [outlined]="true"
              [size]="'medium'"
              [icon]="'expand_more'"
              [values]="roles"
              (selectedValue)="filterUsersByRole($event, true)">
            </jrs-dropdown-view>
          </div>
        </div>
        <div class="group-members">
          <div *ngFor="let user of notEnrolledUsersView" class="group-member">
            <jrs-checkbox
              class="checkbox"
              [check]="user.selected"
              [padding]="false"
              (checked)="this.addUsersToEnroll(user)">
            </jrs-checkbox>
            <div class="member-info">
              <span class="username">{{user.name}}</span>
              <span class="group-path">{{user.groupPath}}</span>
              <span class="user-role">{{user.role}}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="action">
        <div class="add-enroll">
          <jrs-circle-button
            [disabled]="selectedUsersToEnroll.length < 1"
            [size]="'medium'"
            [isMaterial]="true"
            [iconName]="addEnrollmentIcon"
            [color]="'primary'"
            [iconColor]="'light'"
            (clicked)="this.addEnroll()">
          </jrs-circle-button>
        </div>
        <div class="remove-enroll">
          <jrs-circle-button
            [disabled]="selectedUsersFromEnroll.length < 1"
            [size]="'medium'"
            [isMaterial]="true"
            [iconName]="removeEnrollmentIcon"
            [color]="'primary'"
            [iconColor]="'light'"
            (clicked)="this.removeEnroll()">
          </jrs-circle-button>
        </div>
      </div>
      <div class="enrolled-users">
        <div class="users-count"> Сонгосон /{{selectedUsersFromEnroll.length}}/</div>
        <div class="users-count">Нийт хамрагдах суралцагчид /{{enrolledUsersView.length}}/</div>
        <div *ngIf="openInButton" class="open-icon">
          <jrs-button
            [iconName]="'open_in_new'"
            [iconSize]="'small'"
            [noOutline]="true"
            [textColor]="'text-dark'"
            [iconColor]="'dark'"
            (clicked)="onOpenButtonClick()">
          </jrs-button>
        </div>
        <div class="filters">
          <jrs-checkbox
            class="checkbox"
            [padding]="false"
            [check]="allEnrolledUsersSelected"
            (click)="this.selectAllEnrolledUsers()">
          </jrs-checkbox>
          <div class="fields">
            <jrs-input-field
              class="name-filter"
              [iconName]="'search'"
              [iconColor]="'gray'"
              [isMaterial]="true"
              [placeholder]="'Хайх'"
              [selectedType]="'text'"
              [style]="'icon-input-config'"
              [prefixIcon]="true"
              [padding]="false"
              [required]="false"
              (inputChanged)="filterUsersByName($event, false)">
            </jrs-input-field>
            <jrs-drop-down-tree-view
              class="group-dropdown"
              [placeholder]="enrolledUserGroupName"
              [allGroups]="groups"
              (selectedNode)="filterUsersByGroup($event, false)">
            </jrs-drop-down-tree-view>
            <jrs-dropdown-view
              class="role-dropdown"
              [values]="roles"
              [size]="'medium'"
              [defaultValue]="'Эрх'"
              [outlined]="true"
              [icon]="'expand_more'"
              (selectedValue)="filterUsersByRole($event, false)">
            </jrs-dropdown-view>
          </div>
        </div>
        <div class="group-members">
          <div *ngFor="let user of enrolledUsersView" class="group-member">
            <jrs-checkbox
              *ngIf="!user.cannotRemove"
              class="checkbox"
              [check]="user.selected"
              [padding]="false"
              (checked)="this.removeUsersFromEnroll(user)">
            </jrs-checkbox>
            <div *ngIf="user.cannotRemove">
            </div>

            <div class="member-info ">
              <span class="username">{{user.name}}</span>
              <span class="group-path">{{user.groupPath}}</span>
              <span class="user-role">{{user.role}}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <jrs-skeleton-loader [amount]="9" [load]="load"></jrs-skeleton-loader>
  `,
  styleUrls: ['./enrollment-section.component.scss']
})
export class EnrollmentSectionComponent implements OnChanges {
  @Input() title: string;
  @Input() notEnrolledUsers: LearnerInfo[] = [];
  @Input() enrolledUsers: LearnerInfo[] = [];
  @Input() selectedGroupName: string;
  @Input() groups: GroupNode[];
  @Input() load: boolean;
  @Input() roles = [];
  @Input() openInButton = false;
  @Output() usersToEnroll = new EventEmitter<LearnerInfo[]>();
  @Output() openInButtonClicked = new EventEmitter();
  notEnrolledUsersView: LearnerInfo[] = [];
  enrolledUsersView: LearnerInfo[] = [];
  availableUserGroupName = 'Алба хэлтэс сонгох';
  enrolledUserGroupName = 'Алба хэлтэс сонгох';
  selectedUsersToEnroll = [];
  selectedUsersFromEnroll = [];
  show = false;
  addEnrollmentIcon = 'arrow_forward_ios';
  removeEnrollmentIcon = 'arrow_back_ios';
  allEnrolledUsersSelected = false;
  allAvailableUsersSelected = false;
  nameFilterToEnroll: string;
  roleFilterToEnroll: string;
  groupFilterToEnroll: string;
  nameFilterFromEnroll: string;
  roleFilterFromEnroll: string;
  groupFilterFromEnroll: string;

  constructor(private breakPointService: BreakPointObserverService) {
    this.breakPointService.getMediaBreakPointChange().subscribe(res => {
      const onMobileDevice = (res == "media_sm" || res == "media_s" || res == "media_xs");
      if (onMobileDevice) {
        this.removeEnrollmentIcon = 'keyboard_arrow_up';
        this.addEnrollmentIcon = 'keyboard_arrow_down';
      } else {
        this.addEnrollmentIcon = 'arrow_forward_ios';
        this.removeEnrollmentIcon = 'arrow_back_ios';
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "notEnrolledUsers" && this.notEnrolledUsersView.length < 1) {
        this.notEnrolledUsersView = [...this.notEnrolledUsers];
      }
      if (prop == "enrolledUsers" && this.enrolledUsersView.length < 1) {
        this.enrolledUsersView = [...this.enrolledUsers];
      }
    }
  }

  addEnroll(): void {
    this.enrolledUsers = this.enrolledUsers.concat(this.selectedUsersToEnroll);
    this.enrolledUsers.forEach(user => user.selected = false);
    this.notEnrolledUsers = this.notEnrolledUsers.filter(
      user => !this.selectedUsersToEnroll.includes(user));

    this.enrolledUsersView = this.enrolledUsersView.concat(this.selectedUsersToEnroll);
    this.enrolledUsersView.forEach(user => user.selected = false);
    this.notEnrolledUsersView = [...this.notEnrolledUsers];

    this.complete();
  }

  removeEnroll(): void {
    this.notEnrolledUsers = this.notEnrolledUsers.concat(this.selectedUsersFromEnroll);
    this.notEnrolledUsers.forEach(user => user.selected = false);
    this.enrolledUsers = this.enrolledUsers.filter(
      user => !this.selectedUsersFromEnroll.includes(user));

    this.notEnrolledUsersView = this.notEnrolledUsersView.concat(this.selectedUsersFromEnroll);
    this.notEnrolledUsersView.forEach(user => user.selected = false);
    this.enrolledUsersView = [...this.enrolledUsers];
    this.complete();
  }

  complete(): void {
    this.selectedUsersToEnroll = [];
    this.selectedUsersFromEnroll = [];
    this.filterEnrolledUsers();
    this.filterAvailableUsers();
    this.allEnrolledUsersSelected = false;
    this.allAvailableUsersSelected = false;
    this.usersToEnroll.emit(this.enrolledUsers);
  }

  addUsersToEnroll(user): void {
    if (this.selectedUsersToEnroll.includes(user)) {
      user.selected = false;
      const index = this.selectedUsersToEnroll.indexOf(user);
      this.selectedUsersToEnroll.splice(index, 1);
    } else {
      user.selected = true;
      this.selectedUsersToEnroll.push(user);
    }
    this.allAvailableUsersSelected = (this.selectedUsersToEnroll.length == this.notEnrolledUsers.length);
  }

  removeUsersFromEnroll(user): void {
    if (this.selectedUsersFromEnroll.includes(user)) {
      user.selected = false;
      const index = this.selectedUsersFromEnroll.indexOf(user);
      this.selectedUsersFromEnroll.splice(index, 1);
    } else {
      user.selected = true;
      this.selectedUsersFromEnroll.push(user);
    }
    this.allEnrolledUsersSelected = this.selectedUsersFromEnroll.length == this.enrolledUsers.length;
  }

  selectAllAvailableUsers(): void {
    this.allAvailableUsersSelected = !this.allAvailableUsersSelected;
    this.selectedUsersToEnroll.forEach(user => user.selected = false);
    this.selectedUsersToEnroll = [];
    if (this.allAvailableUsersSelected) {
      this.selectedUsersToEnroll = this.selectedUsersToEnroll.concat(this.notEnrolledUsersView);
      this.allAvailableUsersSelected = true;
      this.selectedUsersToEnroll.forEach(user => user.selected = true);
    }
  }

  selectAllEnrolledUsers(): void {
    this.allEnrolledUsersSelected = !this.allEnrolledUsersSelected;
    this.selectedUsersFromEnroll.forEach(user => user.selected = false);
    this.selectedUsersFromEnroll = [];
    if (this.allEnrolledUsersSelected) {
      const enrolledView = this.enrolledUsersView.filter(user => !user.cannotRemove);
      this.selectedUsersFromEnroll = this.selectedUsersFromEnroll.concat(enrolledView);
      this.allEnrolledUsersSelected = true;
      this.selectedUsersFromEnroll.forEach(user => user.selected = true);
    }
  }


  filterUsersByName(name: string, toEnroll: boolean): void {
    if (toEnroll) {
      this.nameFilterToEnroll = name;
      this.filterAvailableUsers();
    } else {
      this.nameFilterFromEnroll = name;
      this.filterEnrolledUsers();
    }
  }

  filterUsersByGroup(group, toEnroll: boolean): void {
    if (toEnroll) {
      this.groupFilterToEnroll = group.id;
      this.availableUserGroupName = group.name;
      this.filterAvailableUsers();
    } else {
      this.groupFilterFromEnroll = group.id;
      this.enrolledUserGroupName = group.name;
      this.filterEnrolledUsers();
    }
  }

  filterUsersByRole(role, toEnroll: boolean): void {
    if (toEnroll) {
      this.roleFilterToEnroll = role.name;
      this.filterAvailableUsers();
    } else {
      this.roleFilterFromEnroll = role.name;
      this.filterEnrolledUsers();
    }
  }


  filterAvailableUsers(): void {
    let filteredAvailableUsers = [...this.notEnrolledUsers];
    if (this.nameFilterToEnroll != null && this.nameFilterToEnroll !== '') {
      filteredAvailableUsers = filteredAvailableUsers.filter(user => user.name.includes(this.nameFilterToEnroll));
    }
    if (this.groupFilterToEnroll != null && this.groupFilterToEnroll !== 'БҮГД') {
      filteredAvailableUsers = filteredAvailableUsers.filter(user => user.groupId == this.groupFilterToEnroll);
    }
    if (this.roleFilterToEnroll != null && this.roleFilterToEnroll !== 'БҮГД') {
      filteredAvailableUsers = filteredAvailableUsers.filter(user => user.role == this.roleFilterToEnroll);
    }
    this.notEnrolledUsersView = filteredAvailableUsers;
  }

  filterEnrolledUsers(): void {
    let filteredEnrolledUsers = [...this.enrolledUsers];
    if (this.nameFilterFromEnroll != null && this.nameFilterFromEnroll !== '') {
      filteredEnrolledUsers = filteredEnrolledUsers.filter(user => user.name.includes(this.nameFilterFromEnroll));
    }
    if (this.groupFilterFromEnroll != null && this.groupFilterFromEnroll !== 'БҮГД') {
      filteredEnrolledUsers = filteredEnrolledUsers.filter(user => user.groupId == this.groupFilterFromEnroll);
    }
    if (this.roleFilterFromEnroll != null && this.roleFilterFromEnroll !== 'БҮГД') {
      filteredEnrolledUsers = filteredEnrolledUsers.filter(user => user.role == this.roleFilterFromEnroll);
    }
    this.enrolledUsersView = filteredEnrolledUsers;

  }

  onOpenButtonClick(): void {
    this.openInButtonClicked.emit()
  }
}
