import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {GroupNode, LearnerInfo} from "../../shared-model";

@Component({
  selector: 'jrs-group-enrollment-section',
  template: `
    <div class="group-members">
      <div *ngIf="title" class="group-enrollment-title margin-top">{{title}}</div>
      <jrs-scroll *ngIf="users.length > 0 && !load" [color]="'primary'" [height]="'40vh'">
        <div class="group-enrollment-member-info-container">
          <div *ngFor="let user of users" class="group-member" [id]="user.name" [class.highlight]="user === selectedUser">
            <jrs-checkbox
              *ngIf="checkbox"
              class="checkbox"
              [check]="user.selected"
              [padding]="false"
              (checked)="this.selectUser($event,user)">
            </jrs-checkbox>
            <div *ngIf="user.cannotRemove">
            </div>

            <div class="member-info">
              <span class="username">{{user.name}}</span>
              <span class="user-lastname">{{user.lastname}}</span>
              <span class="user-firstname">{{user.firstname}}</span>
              <span class="user-role">{{user.role}}</span>
            </div>
          </div>
        </div>
      </jrs-scroll>
      <jrs-not-found-page
        *ngIf="users.length <= 0 && !load"
        [text]="notFoundText"
        [size]="'small'"
        [show]="users.length === 0">
      </jrs-not-found-page>
      <jrs-skeleton-loader [amount]="6" [load]="load"></jrs-skeleton-loader>
    </div>`,
  styleUrls: ['./group-enrollment-section.component.scss']
})
export class GroupEnrollmentSectionComponent implements OnChanges {
  @Input() allUsers: LearnerInfo[];
  @Input() users: LearnerInfo[];
  @Input() selectedUser: LearnerInfo;
  @Input() group: GroupNode;
  @Input() roles = [];
  @Input() title: string;
  @Input() load: boolean;
  @Input() checkbox = false;
  @Output() removeEnrolledUser = new EventEmitter<any>();
  @Output() filterByName = new EventEmitter<LearnerInfo>();
  @Output() filterByRole = new EventEmitter<string>();
  notFoundText = "Илэрц олдсонгүй.";

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "selectedUser") {
        this.scrollToUser();
      }
    }
  }

  getCheckedEnrolledUsers(): string[] {
    const enrolledUsers = [];
    this.users.forEach(user => {
      if (user.selected) {
        enrolledUsers.push(user.name);
      }
    })
    return enrolledUsers
  }

  selectUser(selected: boolean, user: LearnerInfo): void {
    user.selected = selected;
    this.removeEnrolledUser.emit(user);
  }

  onFilterByName(user: LearnerInfo): void {
    this.filterByName.emit(user);
  }

  filterUsersByRole($event: any, b: boolean): void {
    this.filterByRole.emit($event.name);
  }

  private scrollToUser(): void {
    if (this.selectedUser) {
      const element = document.getElementById(this.selectedUser.name)
      if (element) {
        element.scrollIntoView({behavior: 'smooth', block: 'nearest', inline: 'center'});
      }
    }
  }
}
