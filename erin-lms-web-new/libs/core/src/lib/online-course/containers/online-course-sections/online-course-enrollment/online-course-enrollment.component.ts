import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {GroupNode, LearnerInfo, PublishStatus} from "../../../../../../../shared/src/lib/shared-model";
import {FILTER_ROLES} from "../../../../common/common.model";
import {Subject, throwError} from "rxjs";
import {OnlineCourseSandboxService} from "../../../online-course-sandbox.service";
import {OnlineCourseModel} from "../../../models/online-course.model";
import {catchError, map} from "rxjs/operators";

@Component({
  selector: 'jrs-online-course-enrollment',
  template: `
    <jrs-enrollment-section
      [title]="'СУРГАЛТАД ХАМРАГДАХ СУРАЛЦАГЧИД'"
      [notEnrolledUsers]="learnersAvailableToEnroll"
      [roles]="filterRoles"
      [groups]="allUserGroups"
      [enrolledUsers]="initialEnrolledLearners"
      [load]="load"
      [openInButton]="true"
      (usersToEnroll)="enrollmentChange($event)"
      (openInButtonClicked)="onOpenButtonClick()"
    >
    </jrs-enrollment-section>
  `,
  styles: []
})
export class OnlineCourseEnrollmentComponent implements OnInit, OnChanges {
  @Input() course: OnlineCourseModel;
  @Output() openInClicked = new EventEmitter()
  learnersAvailableToEnroll: LearnerInfo[] = [];
  filterRoles = FILTER_ROLES;
  allUserGroups: GroupNode[] = [{id: 'БҮГД', name: "БҮГД", parent: '', nthSibling: 0, children: []}];
  initialEnrolledLearners: LearnerInfo[] = [];
  courseEnrolledLearners: LearnerInfo[] = [];
  GET_USERS_ERROR = "Хэрэгчийн жагсаалт ачаалахад алдаа гарлаа!";
  allLearnersSubject = new Subject<LearnerInfo[]>();
  load = true;
  private published: boolean;

  constructor(private sb: OnlineCourseSandboxService) {
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
    saveOrder.push(this.sb.saveEnrollment(this.course.id, courseLearnerIds,this.course.assignedDepartments).pipe(map(() => {
      this.courseEnrolledLearners.forEach(learner => learner.cannotRemove = true);
      this.initialEnrolledLearners = [...this.courseEnrolledLearners];

    }), catchError((err) => {
      this.sb.openSnackbar('Цахим сургалтын элсэлт хадгалахад алдаа гарлаа!');
      return throwError(err)
    })));
    return saveOrder;
  }

  onOpenButtonClick(): void {
    this.openInClicked.emit();
  }

  private assignEnrollmentSection(): void {
    this.allLearnersSubject.subscribe(res => {
      const learnersAvailable = [];
      const initialLearners = [];
      res.forEach(learner => {
        if (this.course.assignedLearners.includes(learner.name)) {
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
}
