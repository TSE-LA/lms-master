import {Component, Input, OnChanges, OnDestroy, SimpleChanges} from '@angular/core';
import {ClassroomCourseModel} from "../../../model/classroom-course.model";
import {GroupNode, LearnerInfo} from "../../../../../../../shared/src/lib/shared-model";
import {FILTER_ROLES} from "../../../../common/common.model";
import {Observable, Subject, throwError} from "rxjs";
import {ClassroomCourseSandboxService} from "../../../classroom-course-sandbox.service";
import {catchError, map} from "rxjs/operators";

@Component({
  selector: 'jrs-classroom-course-publish-config',
  template: `
    <jrs-section>
      <div class="row gx-1 margin-left-double margin-right-double">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
          <jrs-enrollment-section
            [title]="'СУРГАЛТАД ХАМРАГДАХ СУРАЛЦАГЧИД'"
            [notEnrolledUsers]="learnersAvailableToEnroll"
            [roles]="filterRoles"
            [groups]="allGroups"
            [enrolledUsers]="initialEnrolledLearners"
            [load]="load"
            (usersToEnroll)="enrollmentChange($event)">
          </jrs-enrollment-section>
        </div>
      </div>
    </jrs-section>
  `,
  styles: []
})
export class ClassroomCoursePublishConfigComponent implements OnChanges, OnDestroy {
  @Input() course: ClassroomCourseModel;
  @Input() allLearnersSubject: Subject<LearnerInfo[]>;
  courseId: string;
  load: boolean;
  published: boolean;
  filterRoles = FILTER_ROLES;
  allGroups: GroupNode[] = [{id: 'БҮГД', name: "БҮГД", parent: '', nthSibling: 0, children: []}];
  learnersAvailableToEnroll: LearnerInfo[] = [];
  initialEnrolledLearners: LearnerInfo[] = [];
  courseEnrolledLearners: LearnerInfo[] = [];
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

  saveLearners(saveOrder: any[]): Observable<any>[] {
    saveOrder.push(this.sb.saveCourseLearners(this.getLearners(), this.courseId)
      .pipe(map(() => {
        this.sb.openSnackbar("Танхимын сургалтад суралцагчдыг амжилттай хадгаллаа!", true);
      }), catchError((error) => {
        this.sb.openSnackbar("Танхимын сургалтын суралцагчид!");
        return throwError(error);
      })));
    return saveOrder;
  }

  enrollmentChange($event: LearnerInfo[]): void {
    this.courseEnrolledLearners = $event;
  }

  isDataChanged(): boolean {
    return JSON.stringify(this.initialEnrolledLearners) !== JSON.stringify(this.courseEnrolledLearners);
  }

  private getLearners(): string[] {
    let saveLearners: string[] = [];
    saveLearners = saveLearners.concat(this.courseEnrolledLearners.map(learner => learner.name))
    saveLearners = saveLearners.concat(this.notInGroupUsers);
    return saveLearners;
  }

  private loadPage(): void {
    this.notInGroupUsers = [];
    this.allLearnersSubject.subscribe((res: any) => {
      this.allGroups = res.groups;
      this.load = true;
      this.sb.getClassroomCourseUsers(this.courseId).subscribe((learners) => {
        this.assignLearners(res, learners);
        this.load = false;
      }, () => {
        this.sb.openSnackbar("Танхимын сургалтад суух суралцагчдыг харуулахад алдаа гарлаа!");
        this.load = false;
      });
    })
  }

  private assignLearners(res: any, learners: string[]) {
    const learnersAvailable = [];
    const initialLearners = [];
    res.learners.forEach(learner => {
      if (learners.includes(learner.name)) {
        initialLearners.push(learner);
      } else {
        learnersAvailable.push(learner);
      }
    });
    const allMyLearners = res.learners.map(learner => learner.name);
    learners.forEach(assignedlearner => {
      if (!allMyLearners.includes(assignedlearner)) {
        this.notInGroupUsers.push(assignedlearner);
      }
    })
    this.courseEnrolledLearners = [...initialLearners];
    this.initialEnrolledLearners = initialLearners;
    this.learnersAvailableToEnroll = learnersAvailable;
  }
}
