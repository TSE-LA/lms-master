import {Component} from '@angular/core';
import {GroupNode, LearnerInfo} from "../../../../../../shared/src/lib/shared-model";
import {DetailedUserInfo, FILTER_ROLES} from "../../../common/common.model";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../../../shared/src/lib/dialog/dialog-ref";
import {ClassroomCourseAttendanceModel} from "../../model/classroom-course.model";

@Component({
  selector: 'jrs-classroom-course-add-learner-dialog',
  template: `
    <jrs-enrollment-section
      [title]="'СУРГАЛТАД ХАМРАГДАХ СУРАЛЦАГЧ'"
      [notEnrolledUsers]="learnersAvailableToEnroll"
      [roles]="filterRoles"
      [groups]="allGroups"
      [enrolledUsers]="initialEnrolledLearners"
      [load]="load"
      (usersToEnroll)="enrollmentChange($event)">
    </jrs-enrollment-section>
    <jrs-action-buttons [submitButton]="'Нэмэх'" [declineButton]="'Хаах'"
                        (submitted)="addUsers()" (declined)="closeDialog()"></jrs-action-buttons>
  `,
  styles: []
})
export class ClassroomCourseAddLearnerDialogComponent {
  courseId: string;
  load: boolean;
  published: boolean;
  filterRoles = FILTER_ROLES;
  allGroups: GroupNode[] = [{id: 'БҮГД', name: "БҮГД", parent: '', nthSibling: 0, children: []}];
  learnersAvailableToEnroll: LearnerInfo[] = [];
  initialEnrolledLearners: LearnerInfo[] = [];
  addedUsers: LearnerInfo[] = [];
  allUsers: Map<string, DetailedUserInfo>;

  constructor(private config: DialogConfig, public dialog: DialogRef) {
    const attendanceModels: ClassroomCourseAttendanceModel[] = config.data.enrolledLearners;
    const enrolledUsernames = attendanceModels.map(user => user.username);
    this.allUsers = config.data.allUsers;
    this.allGroups.concat(config.data.groups);
    const available: LearnerInfo[] = []
    this.allUsers.forEach((value: DetailedUserInfo, key: string) => {
      if (!enrolledUsernames.includes(key)) {
        available.push(this.mapToLearnerInfo(value, false))
      }
    });
    this.learnersAvailableToEnroll = available;
  }

  enrollmentChange(learners: LearnerInfo[]): void {
    this.addedUsers = learners;
  }

  addUsers(): void {
    const addedDetailedUsers: DetailedUserInfo[] = [];
    this.addedUsers.forEach(learnerInfo => {
      addedDetailedUsers.push(this.allUsers.get(learnerInfo.username));
    })
    this.dialog.close(addedDetailedUsers);
  }

  closeDialog(): void {
    this.dialog.close();
  }

  private mapToLearnerInfo(value: DetailedUserInfo, cannotRemove: boolean) {
    return {
      name: value.displayName,
      username: value.username,
      role: value.membership.roleId,
      groupPath: value.membership.groupPath,
      selected: false,
      groupName: "",
      groupId: value.membership.groupId,
      cannotRemove: cannotRemove
    };
  }
}
