import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {GroupNode, LearnerInfo} from "../../../../../../../shared/src/lib/shared-model";
import {FILTER_ROLES} from "../../../../common/common.model";
import {ExamSandboxService} from "../../../services/exam-sandbox.service";
import {ExamModel} from "../../../model/exam.model";
import {Subject} from "rxjs";

@Component({
  selector: 'jrs-exam-enroll-section',
  template: `
    <jrs-section [color]="'primary'" [outline]="false">
      <jrs-enrollment-section
        [title]="'ШАЛГАЛТАД ХАМРАГДАХ СУРАЛЦАГЧИД'"
        [notEnrolledUsers]="learnersAvailableToEnroll"
        [roles]="filterRoles"
        [groups]="allUserGroups"
        [enrolledUsers]="initialEnrolledUsers"
        (usersToEnroll)="enrollmentChange($event)">
      </jrs-enrollment-section>
    </jrs-section>
  `
})
export class ExamEnrollSectionComponent implements OnInit, OnChanges {
  @Input() exam: ExamModel;
  learnersAvailableToEnroll: LearnerInfo[] = [];
  filterRoles = FILTER_ROLES;
  allUserGroups: GroupNode[] = [{id: 'БҮГД', name: "БҮГД", parent: '', nthSibling: 0, children: []}];
  initialEnrolledUsers: LearnerInfo[] = [];
  examEnrolledUsers: LearnerInfo[] = [];
  enrolledUserGroups: GroupNode[];
  GET_USERS_ERROR = "Хэрэгчийн жагсаалт ачаалахад алдаа гарлаа!";
  allLearnersSubject = new Subject<LearnerInfo[]>();

  constructor(private sb: ExamSandboxService) {
  }

  ngOnInit(): void {
    this.sb.getAllGroups(false).subscribe(res => {
      this.allUserGroups = [...this.allUserGroups.concat(res)];
    });

    this.sb.getAllUsers().subscribe(res => {
      this.learnersAvailableToEnroll = res;
      this.allLearnersSubject.next(res);
    }, () => {
      this.sb.snackbarOpen(this.GET_USERS_ERROR);
    })
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'exam' && this.exam) {
        if (this.exam.id) {
          this.assignEnrollmentSection();
        }
      }
    }
  }

  getLearnerIds(): string[] {
    return this.examEnrolledUsers.map(learner => learner.name);
  }

  enrollmentChange($event: LearnerInfo[]): void {
    this.examEnrolledUsers = $event;
  }

  private assignEnrollmentSection(): void {
    this.allLearnersSubject.subscribe(res => {
      const learnersAvailable = [];
      const initialLearners = [];
      res.forEach(learner => {
        if (this.exam.enrolledLearners.includes(learner.name)) {
          initialLearners.push(learner);
        } else {
          learnersAvailable.push(learner);
        }
      });
      this.examEnrolledUsers = [...initialLearners];
      this.initialEnrolledUsers = initialLearners;
      this.learnersAvailableToEnroll = learnersAvailable;
    })
  }
}
