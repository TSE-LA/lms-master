import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {GroupNode, LearnerInfo, PublishStatus, TimedCourseModel} from "../../../../../../../shared/src/lib/shared-model";
import {FILTER_ROLES} from "../../../../common/common.model";
import {Subject, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {TimedCourseSandboxService} from "../../../services/timed-course-sandbox.service";

@Component({
  selector: 'jrs-timed-course-enrollment',
  template: `
    <jrs-enrollment-section
      [title]="'УРАМШУУЛАЛТАЙ ТАНИЛЦАХ СУРАЛЦАГЧИД'"
      [notEnrolledUsers]="learnersAvailableToEnroll"
      [roles]="filterRoles"
      [groups]="allUserGroups"
      [enrolledUsers]="initialEnrolledLearners"
      [load]="load"
      [openInButton]="true"
      (usersToEnroll)="enrollmentChange($event)"
      (openInButtonClicked)="onOpenButtonClick()">
    </jrs-enrollment-section>
  `,
  styles: []
})
export class TimedCourseEnrollmentComponent implements OnInit {
  @Input() course: TimedCourseModel;
  @Output() openInClicked = new EventEmitter()
  learnersAvailableToEnroll: LearnerInfo[] = [];
  filterRoles = FILTER_ROLES;
  allUserGroups: GroupNode[] = [{id: 'БҮГД', name: "БҮГД", parent: '', nthSibling: 0, children: []}];
  initialEnrolledLearners: LearnerInfo[] = [];
  courseEnrolledLearners: LearnerInfo[] = [];
  enrolledUserGroups: GroupNode[];
  GET_USERS_ERROR = "Хэрэгчийн жагсаалт ачаалахад алдаа гарлаа!";
  allLearnersSubject = new Subject<LearnerInfo[]>();
  load = true;
  private published: boolean;

  constructor(private sb: TimedCourseSandboxService) {
  }

  ngOnInit(): void {
    this.load = true;
    this.sb.getAllGroups(false).subscribe(res => {
      this.allUserGroups = [...this.allUserGroups.concat(res)];
    });

    this.sb.getAllUsers().subscribe(res => {
      this.learnersAvailableToEnroll = res;
      this.allLearnersSubject.next(res);
    }, () => {
      this.allLearnersSubject.next([]);
      this.sb.openSnackbar(this.GET_USERS_ERROR);
    })
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'course' && this.course) {
        if (this.course.id) {
          this.published = this.course.publishStatus == PublishStatus.PUBLISHED;
          this.assignEnrollmentSection();
        }
      }
    }
  }

  getLearnerIds(): string[] {
    return this.courseEnrolledLearners.map(learner => learner.name);
  }

  isDataChanged(): boolean {
    return JSON.stringify(this.initialEnrolledLearners) !== JSON.stringify(this.courseEnrolledLearners);
  }

  saveEnrollment(saveOrder: any[]): any[] {
    const courseLearnerIds = this.getLearnerIds();
    saveOrder.push(this.sb.saveEnrollment(courseLearnerIds, this.course.enrollments.groups, this.course.id).pipe(map(() => {
      this.courseEnrolledLearners.forEach(learner => learner.cannotRemove = true);
      this.initialEnrolledLearners = [...this.initialEnrolledLearners];
    }), catchError((err) => {
      this.sb.openSnackbar('Урамшууллын элсэлт хадгалахад алдаа гарлаа!');
      return throwError(err)
    })));
    return saveOrder;
  }

  private assignEnrollmentSection(): void {
    this.allLearnersSubject.subscribe(res => {
      const learnersAvailable = [];
      const initialLearners = [];
      res.forEach(learner => {
        if (this.course.enrollments.users.includes(learner.name)) {
          if (this.published) {
            learner.cannotRemove = true;
          }
          initialLearners.push(learner);
        } else {
          learnersAvailable.push(learner);
        }
      });
      this.courseEnrolledLearners = [...initialLearners];
      this.initialEnrolledLearners = initialLearners;
      this.learnersAvailableToEnroll = learnersAvailable;
      this.load = false;
    })
  }

  enrollmentChange($event: LearnerInfo[]): void {
    this.courseEnrolledLearners = $event;
  }

  onOpenButtonClick(): void {
    this.openInClicked.emit();
  }
}
