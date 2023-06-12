import {AfterViewInit, Component, OnInit} from '@angular/core';
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import {
  CLASSROOM_COURSE,
  CLASSROOM_COURSE_ACTIVITY_TABLE_COLUMN,
  ONLINE_COURSE,
  ONLINE_COURSE_ACTIVITY_TABLE_COLUMN,
  TIMED_COURSE,
  TIMED_COURSE_ACTIVITY_TABLE_COLUMNS
} from "../../model/report.constants";
import {CourseActivityReportModel} from "../../model/report.model";
import {Subscription} from "rxjs";
import {ReportSandboxService} from "../../services/report-sandbox.service";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DetailedUserInfo, UserRoleProperties} from "../../../common/common.model";
import {SmallDashletModel} from "../../../../../../shared/src/lib/shared-model";
import {RoleValueUtil} from "../../../../../../shared/src/lib/utilities/role-value.util";

@Component({
  selector: 'jrs-course-activity-report',
  template: `
    <jrs-small-dashlets-bundle
      [loading]="loading"
      [dashlets]="dashlets"
      [role]="selectedUserRole"
      [selectedUserInfo]="selectedUserInfo"
      [showEmptyUser]="showEmptyUser"
      [totalScore]="totalScore">
    </jrs-small-dashlets-bundle>
    <jrs-section class="margin-bottom-large" [maxWidth]="loading ? '98%' : '87vw'" [width]="'100%'">
      <div class="flex">
        <jrs-autocomplete-dropdown
          class="margin-left width-full"
          [label]="'Суралцагчийн нэр'"
          [defaultValue]="selectedUser"
          [filterBy]="'name'"
          [load]="loadingUsers || gettingUsers"
          [values]="selectedGroupUsers"
          (selectedValue)="userNameSelect($event)">
        </jrs-autocomplete-dropdown>
        <span class="margin-left"></span>
        <jrs-dropdown-view
          class="margin-left"
          [label]="'Сургалтын төрөл'"
          [chooseFirst]="true"
          [outlined]="true"
          [size]="'medium'"
          [width]="'180px'"
          [icon]="'expand_more'"
          [values]="courseTypes"
          [hasSuffix]="true"
          (selectedValue)="courseTypeSelect($event)">
        </jrs-dropdown-view>
        <jrs-button
          jrsTooltip="{{reportDownloadTooltip}}"
          placement="{{placement}}"
          delay="0"
          class="margin-left"
          style="margin-top: 15px"
          [iconName]="'file_download'"
          [isMaterial]="true"
          [iconColor]="'light'"
          [size]=" 'icon-medium-responsive'"
          (clicked)="reportDownload()">
        </jrs-button>
      </div>
      <div class="margin-top">
        <jrs-dynamic-table
          [notFoundText]="notFoundText"
          [loading]="loading"
          [minWidth]="'unset'"
          [maxWidth]="'unset'"
          [dataPerPage]="100"
          [height]="'unset'"
          [freezeFirstColumn]="true"
          [tableMinHeight]="'46vh'"
          [dataSource]="dataSource"
          [tableColumns]="tableColumns">
        </jrs-dynamic-table>
      </div>
    </jrs-section>
    <jrs-page-loader [show]="downloading"></jrs-page-loader>
  `,
  styles: []
})
export class CourseActivityReportComponent implements OnInit, AfterViewInit {
  startDate: string;
  endDate: string;
  placement = 'bottom';
  reportDownloadTooltip = 'Тайлан татах';
  courseTypes: DropdownModel[] = [];
  selectedType: DropdownModel;
  selectedGroupId: string;
  notFoundText = 'Мэдээлэл олдсонгүй';
  tableColumns = ONLINE_COURSE_ACTIVITY_TABLE_COLUMN;
  loading = true;
  downloading: boolean;
  loadingUsers: boolean;
  gettingUsers: boolean;
  dataSource: any[] = [];
  onlineCourseData: any[] = [];
  timedCourseData: any[] = [];
  classroomCourseData: any[] = [];
  dashlets: SmallDashletModel[] = [{}, {}];
  groupSubscription: Subscription;
  startDateSubscription: Subscription;
  endDateSubscription: Subscription
  selectedGroupUsers: DropdownModel[] = [];
  selectedUser: DropdownModel = {
    name: ""
  };
  selectedUserInfo: DetailedUserInfo = {
    userId: "",
    username: "",
    displayName: "",
    email: "",
    phoneNumber: "",
    membership: null
  }
  allUsers: DetailedUserInfo[] = [];
  totalScore: number;
  selectedUserRole: string;
  showEmptyUser = false;


  constructor(private sb: ReportSandboxService) {
  }

  ngOnInit(): void {
    this.courseTypes = this.courseTypes.concat(this.sb.constants.COURSE_ACTIVITY_REPORT_TYPES);
    this.selectedType = this.courseTypes[0];
    this.getUsers();
    if (this.courseTypes.length > 2) {
      this.dashlets.push({});
    }
  }

  ngAfterViewInit(): void {
    this.groupSubscription = this.sb.getSelectedGroupId().subscribe(res => {
      this.selectedGroupId = res;
      this.loadingUsers = true;
      this.getGroupUsers();
    });
    this.startDateSubscription = this.sb.getStartDate().subscribe(start => {
      this.startDate = start;
      this.updateTable();
    });
    this.endDateSubscription = this.sb.getEndDate().subscribe(end => {
      this.endDate = end;
      this.updateTable();
    });
  }


  ngOnDestroy(): void {
    this.groupSubscription.unsubscribe();
    this.startDateSubscription.unsubscribe();
    this.endDateSubscription.unsubscribe();
  }

  courseTypeSelect(value): void {
    this.selectedType = value;
    this.updateTable();
  }

  userNameSelect(value): void {
    this.showEmptyUser = false;
    this.selectedUser = value;
    this.selectedUserInfo = this.allUsers.find(user => {
      return user.username == this.selectedUser.name;
    })
    this.selectedUserRole = RoleValueUtil.getRoleDisplayName(this.selectedUserInfo.membership.roleId);
    this.updateTable();
  }

  updateTable(): void {
    if (this.startDate && this.endDate && this.selectedUser != null && this.selectedGroupId) {
      this.loading = true;
      let load;
      if (this.courseTypes.length == 3) {
        load = Promise.all([this.getTimedCourseActivityReport(0), this.getOnlineCourseActivityReport(1), this.getClassroomCourseActivityReport(2)]);
      } else {
        load = Promise.all([this.getOnlineCourseActivityReport(0), this.getClassroomCourseActivityReport(1)]);
      }
      load.then(res => {
        if (res) {
          this.loading = false;
          switch (this.selectedType.id) {
            case ONLINE_COURSE.id:
              this.getTotalScore('online-course');
              this.tableColumns = ONLINE_COURSE_ACTIVITY_TABLE_COLUMN;
              this.dataSource = this.onlineCourseData;
              break;
            case TIMED_COURSE.id:
              this.getTimedCourseTotalScore();
              this.tableColumns = TIMED_COURSE_ACTIVITY_TABLE_COLUMNS;
              this.dataSource = this.timedCourseData;
              break;
            case CLASSROOM_COURSE.id:
              this.getTotalScore('classroom-course')
              this.tableColumns = CLASSROOM_COURSE_ACTIVITY_TABLE_COLUMN;
              this.dataSource = this.classroomCourseData
              break;
            default:
              break;
          }
        }
      })
    }
  }

  getClassroomCourseActivityReport(index: number): Promise<boolean> {
    return new Promise(resolve => {
      this.sb.getClassroomCourseActivityReport(this.startDate, this.endDate, this.selectedGroupId, this.selectedUser.name).subscribe((res: CourseActivityReportModel) => {
        this.classroomCourseData = res.reports;
        this.dashlets[index] = res.dashlet;
        resolve(true);
      }, () => {
        resolve(true);
        this.sb.snackbarOpen('Танхимын сургалтын мэдээлэл авахад алдаа гарлаа');
      })
    })
  }

  getOnlineCourseActivityReport(index: number): Promise<boolean> {
    return new Promise(resolve => {
      this.sb.getOnlineCourseActivityReport(this.startDate, this.endDate, this.selectedGroupId, this.selectedUser.name).subscribe((res: CourseActivityReportModel) => {
        this.onlineCourseData = res.reports;
        this.dashlets[index] = res.dashlet;
        resolve(true);
      }, () => {
        resolve(true);
        this.sb.snackbarOpen('Цахим сургалтын мэдээлэл авахад алдаа гарлаа');
      });
    })
  }

  getTimedCourseActivityReport(index: number): Promise<boolean> {
    return new Promise(resolve => {
      this.sb.getTimedCourseActivityReport(this.startDate, this.endDate, this.selectedGroupId, this.selectedUser.name).subscribe((res: CourseActivityReportModel) => {
        this.timedCourseData = res.reports;
        this.dashlets[index] = res.dashlet;
        resolve(true);
      }, () => {
        resolve(true);
        this.sb.snackbarOpen('Урамшууллын мэдээлэл авахад алдаа гарлаа');
      });
    })
  }

  getTimedCourseTotalScore(): void {
    this.sb.timedCourseTotalScore(this.selectedUser.name).subscribe((res: number) => {
      this.totalScore = res;
    })
  }

  getTotalScore(courseType: string): void {
    this.sb.getTotalScore(this.selectedUser.name, courseType).subscribe((res: number) => {
      this.totalScore = res;
    })
  }


  getUsers(): void {
    this.gettingUsers = true;
    this.sb.getAllUsers(true).subscribe((res: DetailedUserInfo[]) => {
      this.allUsers = res;
      this.gettingUsers = false;
      this.getGroupUsers();
    }, () => {
      this.gettingUsers = false;
      this.sb.snackbarOpen('Хэрэглэгчдийн мэдээлэл авахад алдаа гарлаа');
    });
  }

  getGroupUsers(): void {
    this.loadingUsers = true;
    this.selectedGroupUsers = [];
    const learners: DropdownModel[] = [];
    this.allUsers.forEach(user => {
      if (user.membership != null && user.membership.roleId != UserRoleProperties.adminRole.id && user.membership.groupId == this.selectedGroupId) {
        learners.push({id: user.membership.membershipId, name: user.username});
      }
    })
    this.selectedGroupUsers = this.selectedGroupUsers.concat(learners);
    this.selectedUser = this.selectedGroupUsers[0];
    this.showEmptyUser = false;
    if (this.selectedUser) {
      this.selectedUserInfo = this.allUsers.find(user => {
        return user.username == this.selectedUser.name;
      })
      this.selectedUserRole = RoleValueUtil.getRoleDisplayName(this.selectedUserInfo.membership.roleId);
      this.updateTable();
      this.loadingUsers = false;
    } else {
      this.loadingUsers = false;
      this.showEmptyUser = true;
    }
  }

  reportDownload(): void {
    if (this.dataSource.length === 0) {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.decline = false;
      config.title = 'Анхаар!';
      config.data = {info: (`Тайлангийн мэдээлэл олдсонгүй.`)};
      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
      dialogRef.afterClosed.subscribe(() => {
        return;
      });
    } else {
      this.downloading = true;
      switch (this.selectedType.id) {
        case ONLINE_COURSE.id:
          this.downloadOnlineCourseActivityReport();
          break;
        case TIMED_COURSE.id:
          this.downloadTimedCourseActivityReport();
          break;
        case CLASSROOM_COURSE.id:
          this.downloadClassroomCourseActivityReport();
          break;
        default:
          break;
      }
    }
  }

  downloadClassroomCourseActivityReport(): void {
    this.sb.downloadClassroomCourseActivityReport(this.startDate, this.endDate, this.selectedGroupId, this.selectedUser.name).subscribe(res => {
      this.downloading = false
    }, () => {
      this.downloading = false;
    });
  }

  downloadTimedCourseActivityReport(): void {
    this.sb.downloadTimedCourseActivityReport(this.startDate, this.endDate, this.selectedGroupId, this.selectedUser.name).subscribe(res => {
      this.downloading = false;
    }, () => {
      this.downloading = false;
    });
  }

  downloadOnlineCourseActivityReport(): void {
    this.sb.downloadOnlineCourseActivityReport(this.dataSource).subscribe(res => {
      this.downloading = false;
    }, () => {
      this.downloading = false
    });
  }
}
