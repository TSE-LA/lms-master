import {Component, Input, OnChanges, OnDestroy, SimpleChanges} from '@angular/core';
import {ClassroomCourseModel, ClassroomCourseState} from "../../../model/classroom-course.model";
import {GroupNode, LearnerInfo} from "../../../../../../../shared/src/lib/shared-model";
import {FILTER_ROLES, UserRoleProperties} from "../../../../common/common.model";
import {Subject} from "rxjs";
import {ClassroomCourseSandboxService} from "../../../classroom-course-sandbox.service";
import {DialogConfig} from "../../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'jrs-classroom-course-super-invitation',
  template: `
    <ng-container *ngIf="openSuperInvitation">
      <jrs-section>
        <div class="row gx-1 margin-left-double margin-bottom">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-enrollment-section
              [title]="'АХЛАХ, МЕНЕЖЕРТ УРИЛГА ИЛГЭЭХ'"
              [notEnrolledUsers]="supersAvailableToEnroll"
              [roles]="filterRoles"
              [groups]="allGroups"
              [enrolledUsers]="initialEnrolledSupers"
              [load]="load"
              (usersToEnroll)="invitationChange($event)">
            </jrs-enrollment-section>
          </div>
        </div>
        <jrs-button
          *ngIf="!published"
          [title]="'УРИЛГА ИЛГЭЭХ'"
          [color]="'primary'"
          [size]="'medium'"
          [float]="'center'"
          [load]="sending"
          (clicked)="send()">
        </jrs-button>
      </jrs-section>
    </ng-container>
  `,
  styles: []
})
export class ClassroomCourseSuperInvitationComponent implements OnChanges, OnDestroy {
  @Input() course: ClassroomCourseModel;
  @Input() openSuperInvitation = false;
  @Input() allLearnersSubject: Subject<LearnerInfo[]>;
  courseId: string;
  load: boolean;
  sending: boolean;
  published: boolean;
  supersAvailableToEnroll: LearnerInfo[] = [];
  filterRoles = FILTER_ROLES;
  allGroups: GroupNode[] = [{id: 'БҮГД', name: "БҮГД", parent: '', nthSibling: 0, children: []}];
  initialEnrolledSupers: LearnerInfo[] = [];
  courseInvitedSupers: LearnerInfo[] = [];

  private notInGroupUsers: string[] = [];

  constructor(private sb: ClassroomCourseSandboxService) {
  }

  ngOnDestroy(): void {
    if (this.allLearnersSubject) {
      this.allLearnersSubject.unsubscribe();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "course" && this.course) {
        this.courseId = this.course.id;
        this.loadPage();
      }
    }
  }

  send(): void {
    const config = new DialogConfig();
    config.title = "Ахлах, менежерүүдэд урилга илгээх үү?";
    config.data = {info: "Хэрэв 'Тийм' гэж сонговол ахлах, менежерүүдэд мэдэгдэл илгээгдэнэ. 'Үгүй' гэж сонговол хадгалагдана."};
    this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
      const sendNotification = !!res;
      this.sb.enrollSuperLearners(
        this.course.enrollment.assignedDepartments,
        this.getNewSuperLearners(),
        this.courseId,
        sendNotification,
        this.course.typeId).subscribe(() => {
        if (this.course.state == ClassroomCourseState.NEW) {
          this.sb.changeStateToSent(this.course.id).subscribe(() => {
              this.initialEnrolledSupers = [...this.courseInvitedSupers];
              this.sb.openSnackbar("Ахлах, Менежерт урилга илгээгдлээ.", true);
              this.sb.goBack();
            },
            () => this.sb.openSnackbar("Сургалтын төлөв илгээсэн болгоход алдаа гарлаа!"));
        } else {
          this.initialEnrolledSupers = [...this.courseInvitedSupers];
          this.sb.openSnackbar("Ахлах, Менежерт урилга илгээгдлээ.", true);
          this.sb.goBack();
        }
      }, () => {
        this.sb.openSnackbar("Ахлах, Менежерт урилга илгээхэд алдаа гарлаа!");
      });
    })
  }

  invitationChange($event: LearnerInfo[]): void {
    this.courseInvitedSupers = $event;
  }

  isDataChanged(): boolean {
    return JSON.stringify(this.initialEnrolledSupers) !== JSON.stringify(this.courseInvitedSupers);
  }

  private loadPage(): void {
    this.notInGroupUsers = [];
    this.load = true;
    this.allLearnersSubject.subscribe((res: any) => {
      this.assignSupers(res);
      this.load = false;
    }, () => {
      this.sb.openSnackbar("Ахлах, Менежер ачаалахад алдаа гарлаа!");
      this.load = false;
    })
  }

  private assignSupers(res: any): void {
    this.allGroups = res.groups;
    const learnersAvailable = [];
    const initialLearners = [];
    res.learners.forEach(learner => {
      if (this.course.enrollment.assignedLearners.includes(learner.name)) {
        initialLearners.push(learner);
      } else {
        if (learner.role == UserRoleProperties.managerRole.name || learner.role == UserRoleProperties.supervisorRole.name) {
          learnersAvailable.push(learner);
        }
      }
    });

    const allMyLearners = res.learners.map(learner => learner.name);
    this.course.enrollment.assignedLearners.forEach(assignedLearner => {
      if (!allMyLearners.includes(assignedLearner)) {
        this.notInGroupUsers.push(assignedLearner);
      }
    })
    this.courseInvitedSupers = [...initialLearners];
    this.initialEnrolledSupers = initialLearners;
    this.supersAvailableToEnroll = learnersAvailable;
  }

  private getNewSuperLearners(): string[] {
    let superLearners = [...this.notInGroupUsers];
    superLearners = superLearners.concat(this.courseInvitedSupers.map(learner => learner.name));
    return superLearners;
  }
}
