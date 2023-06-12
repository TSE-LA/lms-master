import {Component, OnInit, ViewChild} from '@angular/core';
import {GroupNode, LearnerInfo} from "../../../../../../shared/src/lib/shared-model";
import {ClassroomCourseModel, ClassroomCourseState} from "../../model/classroom-course.model";

import {ClassroomCourseSandboxService} from "../../classroom-course-sandbox.service";
import {forkJoin, Observable, Subject, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {ActivatedRoute} from "@angular/router";
import {ClassroomCourseInfoComponent} from "../classroom-course-sections/classroom-course-info/classroom-course-info.component";
import {ClassroomCourseSuperInvitationComponent} from "../classroom-course-sections/classroom-course-super-invitation/classroom-course-super-invitation.component";
import {ClassroomCoursePublishConfigComponent} from "../classroom-course-sections/classroom-course-publish-config/classroom-course-publish-config.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'jrs-classroom-course-update-page',
  template: `
    <jrs-button class="create-page-sidenav-content"
                [iconName]="'arrow_back_ios'"
                [iconColor]="'secondary'"
                [noOutline]="true"
                [isMaterial]="true"
                [size]="'medium'"
                [bold]="true"
                [textColor]="'text-link'"
                (clicked)="goBack()">БУЦАХ
    </jrs-button>
    <ng-container *ngIf="!load">
      <jrs-classroom-course-info
        #infoSection
        [course]="classroomCourse"
        [published]="published"
        (openInvitation)="openInvitation($event)">
      </jrs-classroom-course-info>
      <jrs-classroom-course-super-invitation
        #invitationSection
        *ngIf="!published"
        [course]="classroomCourse"
        [openSuperInvitation]="openSuperInvitation"
        [allLearnersSubject]="allLearnersSubject">
      </jrs-classroom-course-super-invitation>
      <jrs-classroom-course-publish-config
        #enrollmentSection
        *ngIf="!published"
        [course]="classroomCourse"
        [allLearnersSubject]="allLearnersSubject">
      </jrs-classroom-course-publish-config>
      <div class="flex-center margin-top margin-bottom-large">
        <jrs-button
          class="side-margin margin-left"
          [title]="'ХАДГАЛАХ'"
          [color]="'primary'"
          [size]="'medium'"
          [float]="'center'"
          [load]="saving || publishing"
          (clicked)="save()">
        </jrs-button>
        <jrs-button
          *ngIf="!published"
          class="side-margin margin-left"
          [title]="'НИЙТЛЭХ'"
          [color]="'primary'"
          [size]="'medium'"
          [float]="'center'"
          [load]="saving || publishing"
          (clicked)="publish()">
        </jrs-button>
      </div>
    </ng-container>
    <jrs-section *ngIf="load">
      <jrs-skeleton-loader [amount]="8" [load]="load"></jrs-skeleton-loader>
    </jrs-section>
    <jrs-page-loader [show]="saving || publishing"></jrs-page-loader>
  `,
  styles: []
})
export class ClassroomCourseUpdatePageComponent implements OnInit{
  @ViewChild('infoSection') infoSection: ClassroomCourseInfoComponent;
  @ViewChild('invitationSection') invitationSection: ClassroomCourseSuperInvitationComponent;
  @ViewChild('enrollmentSection') enrollmentSection: ClassroomCoursePublishConfigComponent;
  courseId: string;
  classroomCourse: ClassroomCourseModel;
  allGroups: GroupNode[] = [{id: 'БҮГД', name: "БҮГД", parent: '', nthSibling: 0, children: []}];
  allLearnersSubject = new Subject<any>();
  GET_GROUPS_ERROR = "Групп ачаалахад алдаа гарлаа!";
  GET_USERS_ERROR = "Хэрэгчийн жагсаалт ачаалахад алдаа гарлаа!";
  openSuperInvitation = false;
  allLearners: LearnerInfo[] = [];
  load = false;
  saving: boolean;
  publishing: boolean;
  published: boolean;

  private saveOrder: any[] = [];


  constructor(private sb: ClassroomCourseSandboxService, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      this.courseId = params.get('id');
      this.loadCourse();
    })
  }

  ngOnInit(): void {
    const observables = [];
    observables.push(this.sb.getAllGroups(false).pipe(map(res => {
      this.allGroups = [...this.allGroups.concat(res)];
    }), catchError(err => {
      this.sb.openSnackbar(this.GET_GROUPS_ERROR);
      return throwError(err);
    })));
    observables.push(this.sb.getAllUsersWithRole().pipe(map(res => {
      this.allLearners = res;
    }), catchError((err) => {
      this.sb.openSnackbar(this.GET_USERS_ERROR);
      return throwError(err);
    })));
    forkJoin(...observables).subscribe(() => {
      this.allLearnersSubject.next({groups: this.allGroups, learners: this.allLearners});
    }, () => {
      this.allLearnersSubject.next({groups: this.allGroups, learners: this.allLearners});
    });
  }

  goBack(): void {
    const infoChanged = this.infoSection.isDataChanged();
    const invitationSection = this.invitationSection && this.invitationSection.isDataChanged();
    const enrollmentSection = this.enrollmentSection && this.enrollmentSection.isDataChanged();
    if (infoChanged || invitationSection || enrollmentSection) {
      const config = new DialogConfig();
      config.outsideClick = false;
      config.title = "Мэдээллийг хадгалахгүй шууд гарах уу?"
      config.data = {
        info: "'Тийм' гэж дарвал мэдээлэл хадгалагдахгүй болохыг анхаарна уу."
      }
      this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
        if (res) {
          this.navigateToCalendar();
        }
      });
    } else {
      this.navigateToCalendar();
    }
  }

  save(): void {
    const save = this.saveCourse();
    if (this.saveOrder.length > 0) {
      this.saving = true;
      save.subscribe(() => {
        this.saving = false;
        this.navigateToCalendar();
      }, () => {
        this.saving = false;
      });
    }
  };

  navigateToCalendar(): void {
    this.sb.goBack();
  }

  openInvitation($event: boolean): void {
    this.openSuperInvitation = $event;
  }

  publish(): void {
    const save = this.saveCourse();
    if (this.saveOrder.length > 0) {
      this.saving = true;
      save.subscribe(() => {
        this.saving = false;
        this.publishCourse();
      }, () => {
        this.saving = false;
      });
    } else {
      this.publishCourse();
    }
  }

  private publishCourse(): void {
    this.publishing = true;
    this.sb.publishCourse(this.courseId).subscribe(() => {
      this.publishing = false;
      this.sb.openSnackbar("Танхимын сургалт амжилттай нийтэллээ.", true);
      this.navigateToCalendar();
    }, () => {
      this.publishing = false;
      this.sb.openSnackbar("Танхимын сургалтыг нийтлэхэд алдаа гарлаа!");
    });
  }

  private saveCourse(): Observable<any> {
    this.saveOrder = [];
    if (this.infoSection.isDataChanged()) {
      this.saveOrder = this.infoSection.save(this.saveOrder);
    }
    if (this.enrollmentSection && this.enrollmentSection.isDataChanged()) {
      this.saveOrder = this.enrollmentSection.saveLearners(this.saveOrder);
    }
    return forkJoin(this.saveOrder);
  }

  private loadCourse(): void {
    this.load = true;
    this.sb.getCourseById(this.courseId).subscribe(res => {
      this.classroomCourse = res;
      if (this.classroomCourse.state == ClassroomCourseState.DONE || this.classroomCourse.state == ClassroomCourseState.STARTED) {
        this.published = true;
      }
      this.load = false;
    }, catchError(err => {
      this.sb.openSnackbar("Танхимын сургалт ачаалахад алдаа гарлаа!");
      this.load = false;
      return throwError(err);
    }));
  }
}
