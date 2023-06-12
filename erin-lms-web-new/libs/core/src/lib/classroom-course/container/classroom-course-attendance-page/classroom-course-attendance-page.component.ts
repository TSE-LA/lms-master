import {Component, OnInit} from '@angular/core';
import {GroupNode, TableColumn} from "../../../../../../shared/src/lib/shared-model";
import {CLASSROOM_COURSE_ATTENDANCE_COLUMN_DONE, CLASSROOM_COURSE_ATTENDANCE_COLUMN_INPROGRESS} from "../../model/classroom-course.constants";
import {ClassroomCourseSandboxService} from "../../classroom-course-sandbox.service";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ClassroomCourseAttendanceModel, ClassroomCourseModel} from "../../model/classroom-course.model";
import {ActivatedRoute} from "@angular/router";
import {DetailedUserInfo} from "../../../common/common.model";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {forkJoin, Observable} from "rxjs";
import {map} from "rxjs/operators";
import {ClassroomCourseAddLearnerDialogComponent} from "../../component/classroom-course-add-learner-dialog/classroom-course-add-learner-dialog.component";

@Component({
  selector: 'jrs-classroom-course-attendance-page',
  template: `
    <jrs-button
      [iconName]="'arrow_back_ios'"
      [iconColor]="'secondary'"
      [noOutline]="true"
      [isMaterial]="true"
      [size]="'medium'"
      [bold]="true"
      [textColor]="'text-link'"
      (clicked)="back()">БУЦАХ
    </jrs-button>
    <jrs-section class="margin-bottom-large" [width]="'75vw'" [minWidth]="'fit-content'">
      <div class="flex">
        <jrs-header-text>{{courseTitle}}</jrs-header-text>
        <jrs-header-text [bold]="false">{{' / Нийт ' + getTotalUsers() + ' - Ирц ' + totalAttendedUsers + ' /'}}</jrs-header-text>
        <span class="spacer"></span>
        <jrs-button
          *ngIf="!isDone"
          [title]="'Төлөв буцаах'"
          [size]="'medium'"
          [load]="loading"
          (clicked)="openBackPrevStateDialog()">
        </jrs-button>
        <jrs-button
          *ngIf="!isDone"
          class="margin-left"
          [title]="'Суралцагч нэмэх'"
          [size]="'medium'"
          [load]="loading"
          (clicked)="openAddDialog()">
        </jrs-button>
        <jrs-button
          class="margin-left"
          [iconName]="'file_download'"
          [isMaterial]="true"
          [iconColor]="'light'"
          [size]=" 'icon-medium-responsive'"
          [load]="loading"
          (clicked)="attendanceDownload()">
        </jrs-button>
      </div>

      <jrs-dynamic-table
        [minHeight]="'unset'"
        [loading]="loading"
        [dataPerPage]="100"
        [minWidth]="'unset'"
        [maxWidth]="'unset'"
        [tableMinHeight]="'51vh'"
        [dataSource]="currentEnrolledLearners"
        [tableColumns]="tableColumns"
        (rowAction)="removeUser($event)"
        (checkboxAction)="collectData($event)">
      </jrs-dynamic-table>
      <div class="flex-center">
        <jrs-button
          *ngIf="!isDone"
          [title]="'ХАДГАЛАХ'"
          [size]="'medium'"
          [load]="loading"
          (clicked)="save()">
        </jrs-button>
        <jrs-button
          *ngIf="!isDone"
          class="margin-left"
          [title]="'СУРГАЛТ ХААХ'"
          [size]="'medium'"
          [load]="loading"
          (clicked)="openCloseDialog()">
        </jrs-button>
      </div>
    </jrs-section>
    <jrs-page-loader [show]="saving"></jrs-page-loader>
  `
})
export class ClassroomCourseAttendancePageComponent implements OnInit {
  loading: boolean;
  saving: boolean;
  classroomCourse: ClassroomCourseModel;
  currentEnrolledLearners: ClassroomCourseAttendanceModel[] = [];
  tableColumns: TableColumn[] = CLASSROOM_COURSE_ATTENDANCE_COLUMN_INPROGRESS;
  allUsers: Map<string, DetailedUserInfo> = new Map<string, DetailedUserInfo>();
  classroomCourseId: string;
  courseState: string;
  courseTitle: string;
  isDone = false;
  totalAttendedUsers = 0;
  BACK_TO_PREVIOUS_STATE_TITLE = 'Өмнөх төлөвт буцаах.'
  BACK_TO_PREVIOUS_STATE_MSG = 'Төлөв буцаах дарахад Баталгаажуулах төлөвд орно. Өмнөх хадгалсан ирц болон дүн устана. Та буцаахдаа итгэлтэй байна уу ? '


  private allMyUsers: Map<string, DetailedUserInfo>;
  private root: GroupNode;

  constructor(private sb: ClassroomCourseSandboxService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    if (this.route.paramMap) {
      this.route.paramMap.subscribe(params => this.classroomCourseId = params.get('id'));
      this.setupPage();
    }
  }

  back(): void {
    this.sb.goBack();
  }

  openAddDialog(): void {
    const config = new DialogConfig();
    config.width = "80vw";
    config.data = {
      groups: this.root,
      enrolledLearners: this.currentEnrolledLearners,
      allUsers: this.allMyUsers
    };
    this.sb.openDialog(ClassroomCourseAddLearnerDialogComponent, config).afterClosed.subscribe((modifiedSuggestedUsers: DetailedUserInfo []) => {
      if (modifiedSuggestedUsers) {
        const data = this.currentEnrolledLearners;
        /*re-sync users*/
        for (const user of modifiedSuggestedUsers) {
          const foundUser = this.allMyUsers.get(user.username);
          if (foundUser) {
            foundUser.clicked = true;
          }

          /*update on table*/
          const foundInList = data.find(learner => learner.username == user.username);

          if (!foundInList) {
            data.push({
              username: user.username,
              displayName: user.displayName,
              groups: user.path,
              supervisor: null,
              invitation: 1,
              attendance: true,
              score1: null,
              score2: null,
              score3: null
            })
          }
        }
        /*sort it or it will get scrambled*/
        modifiedSuggestedUsers.sort((a, b) => a.displayName.localeCompare(b.displayName))
        this.currentEnrolledLearners = [...data];
      }
    });
  }

  attendanceDownload(): void {
    this.loading = true;
    this.sb.downloadAttendance(this.classroomCourseId, this.currentEnrolledLearners).subscribe(() => {
      this.loading = false;
    }, () => {
      this.loading = false;
      this.sb.openSnackbar("Сургалтын ирц татахад алдаа гарлаа.", false);
    })
  }

  updateClassroomCourse(response): void {
    this.classroomCourse = response;
    if (this.classroomCourse.state === 'DONE') {
      this.isDone = true;
      this.tableColumns = CLASSROOM_COURSE_ATTENDANCE_COLUMN_DONE
    }
  }

  getTotalUsers(): number {
    return this.currentEnrolledLearners ? this.currentEnrolledLearners.length : 0;
  }

  save(): void {
    this.saving = true;
    this.sb.updateAttendance(this.classroomCourseId, this.currentEnrolledLearners).subscribe((res) => {
      this.setDisplayNameAndUpdate(res);
      this.saving = false;
      this.sb.openSnackbar('Амжилттай хадгаллаа.', true);
    }, () => {
      this.saving = false;
      this.sb.openSnackbar('Хадгалахад алдаа гарлаа.');
    });
  }

  openCloseDialog(): void {
    const config = new DialogConfig()
    config.title = "Сургалт хаах уу?";
    config.submitButton = 'Тийм';
    config.data = {
      info: `'Та ${this.courseTitle}' сургалтыг хаасан бол дахин нээх боломжгүй болохыг анхаарна уу.`
    }
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(res => {
      if (res) {
        this.close();
      }
    })
  }

  removeUser(event): void {
    const user: ClassroomCourseAttendanceModel = event.data;
    const config = new DialogConfig();
    config.title = "Хэрэглэгчийг хасах уу?";
    config.data = {
      info: `'Та ${user.displayName}' хэрэглэгчийг сургалтын ирцээс  хасахдаа итгэлтэй байна уу?`
    }
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(res => {
      if (res) {
        this.sb.removeUser(this.classroomCourseId, user.username).subscribe(res => {
          this.sb.openSnackbar("Хэрэглэгчийг сургалтаас амжилттай устгалаа.", true);
          if (res) {
            this.getAttendance().subscribe(res => {
              this.setDisplayNameAndUpdate(res);
              return res;
            });
          }
        }, () => {
          this.sb.openSnackbar("Хэрэглэгчийг сургалтаас устгахад алдаа гарлаа!");
        });
      }
    })
  }

  openBackPrevStateDialog(): void {
    const config = new DialogConfig();
    config.title = this.BACK_TO_PREVIOUS_STATE_TITLE;
    config.data = {
      info: this.BACK_TO_PREVIOUS_STATE_MSG
    }
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe((result: boolean) => {
      if (result) {
        this.loading = true;
        for (const data of this.currentEnrolledLearners) {
          data.attendance = false;
          data.score1 = null;
          data.score2 = null;
          data.score3 = null;
        }
        this.sb.updateAttendance(this.classroomCourseId, this.currentEnrolledLearners).subscribe(() => {
          this.sb.updateClassroomCourseState(this.classroomCourse.id, 'READY').subscribe(() => {
            this.loading = false;
            this.sb.openSnackbar('Өмнөх төлөвт амжилттай буцлаа.', true);
            this.goBack();
          }, () => {
            this.loading = false;
            this.sb.openSnackbar('Өмнөх төлөвт буцахад алдаа гарлаа.');
          });
        }, () => {
          this.loading = false;
        });
      }
    });
  }

  collectData(event): void {
    this.totalAttendedUsers = event.length;
  }

  private getAttendance(): Observable<any> {
    return this.sb.getAttendance(this.classroomCourseId);
  }

  private setDisplayNameAndUpdate(res: ClassroomCourseAttendanceModel[]) {
    for (const learner of res) {
      const foundUser = this.allUsers.get(learner.username);
      if (foundUser) {
        learner.displayName = foundUser.displayName;
      }
    }
    this.currentEnrolledLearners = [...res];
  }


  private close(): void {
    this.saving = true;
    this.sb.closeClassroomCourse(this.classroomCourseId, this.currentEnrolledLearners).subscribe(() => {
      this.sb.openSnackbar("Сургалтыг амжилттай хаалаа.", true);
      this.saving = false;
      this.goBack();
    }, () => {
      this.sb.openSnackbar("Сургалтыг хаахад алдаа гарлаа");
      this.saving = false;
    });
  }

  private goBack(): void {
    this.sb.navigateByUrl('/classroom-course');
  }

  private setupPage(): void {
    this.loading = true;

    const getClassroomCourseById = this.sb.getClassroomCourseById(this.classroomCourseId).pipe(map(res => {
      this.courseState = res.state;
      this.courseTitle = res.name;
      this.updateClassroomCourse(res);
      return res;
    }));

    const getMyClassroomUsers = this.sb.getMyClassroomUsers(this.classroomCourseId).pipe(map(res => {
      this.allMyUsers = res.usersByGroup.allMyUsers
      this.allUsers = res.usersByGroup.allUsers;
      this.root = res.usersByGroup.groups;
      return res;
    }));

    const getAttendance = this.getAttendance();

    forkJoin([getClassroomCourseById, getMyClassroomUsers, getAttendance]).subscribe((res) => {
      this.setDisplayNameAndUpdate(res[2]);
      this.loading = false
    }, () => {
      this.loading = false
      this.sb.openSnackbar("Ирц хуудасны мэдээлэл ачааллахад алдаа гарлаа!", false);
    })
  }
}
