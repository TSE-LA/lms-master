import { Component, OnInit } from '@angular/core';
import { CategoryItem, CourseState, PublishStatus, Status, TimedCourseModel } from "../../../../../../shared/src/lib/shared-model";
import { COURSE_PUBLISH_STATUS, UserRoleProperties } from "../../../common/common.model";
import {
  EXPIRED_PUBLISHED_TIMED_COURSE_ACTIONS,
  TIMED_COURSE_ACTION_CLONE,
  TIMED_COURSE_ACTION_DELETE,
  TIMED_COURSE_ACTION_EDIT,
  TIMED_COURSE_ACTION_HIDE,
  TIMED_COURSE_ACTION_LAUNCH,
  TIMED_COURSE_ACTION_STATISTIC,
  TIMED_COURSE_NOT_PUBLISHED_ACTIONS,
  TIMED_COURSE_PENDING_ACTIONS,
  TIMED_COURSE_PUBLISHED_ACTIONS
} from "../../models/timed-course.constant";
import { DropdownModel } from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import { TimedCourseSandboxService } from "../../services/timed-course-sandbox.service";
import { DialogConfig } from "../../../../../../shared/src/lib/dialog/dialog-config";
import { ConfirmDialogComponent } from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import { TIMED_COURSE_COLUMN } from "../../../../../../shared/src/lib/table/model/table-constants";
import { DateIntervalDialogComponent } from "../../../common/components/date-interval-dialog/date-interval-dialog.component";


@Component({
  selector: 'jrs-timed-course-container',
  template: `
    <div class="main-wrapper">
      <div class="header">
        <div class="header-text">
          <jrs-header-text [size]="'medium'">УРАМШУУЛАЛ</jrs-header-text>
        </div>
        <span class="spacer"></span>
        <div class="action-toolbar-dropdowns">
          <div class="category-dropdown margin-left">
            <jrs-dropdown-view
              [width]="'190px'"
              [label]="'Ангилал сонгох'"
              [defaultValue]="selectedCategory? selectedCategory.name: ''"
              [outlined]="true"
              [size]="'medium'"
              [icon]="'expand_more'"
              [values]="this.categories"
              [hasSuffix]="true"
              (selectedValue)="categorySelect($event)">
            </jrs-dropdown-view>
          </div>
          <div *canAccess="PUBLISH_STATUS_FILTER" class="margin-left">
            <jrs-dropdown-view
              [width]="'190px'"
              [label]="'Нийтэлсэн төлөв'"
              [chooseFirst]="true"
              [values]="publishStatus"
              [color]="'light'"
              [outlined]="true"
              [tooltip]="true"
              [size]="'medium'"
              (selectedValue)="changeStatus($event)">
            </jrs-dropdown-view>
          </div>
        </div>
        <div class="create-course">
        <jrs-button
          *canAccess="CREATE_BUTTON"
          [title]="!isTabletMode ? 'Урамшуулал нэмэх': ''"
          [size]="isTabletMode ? 'medium':'long'"
          [iconName]="isTabletMode && 'add'"
          (clicked)="navigateToCreatePage()">
        </jrs-button>
      </div>
        <div *canAccess="READERSHIP_BUTTON" class="readership-btn-style">
          <jrs-icon class="icon material-icons"
                    [size]="'chapters-medium'"
                    [mat]="true"
                    [color]="'primary'"
                    (click)="openDateInterval()">
            settings
          </jrs-icon>
        </div>
      </div>
      <div class="state-picker">
        <div *ngFor="let state of this.states" class="state">
          <jrs-radio-button
            [group]="'state'"
            [label]="state.name"
            [size]="'large'"
            [margin]="true"
            [labelSize]="'label-large'"
            [check]="state.selected"
            (clicked)="stateSelect(state)">
          </jrs-radio-button>
        </div>
      </div>
      <jrs-timed-course-table
        [dataSource]="data"
        [tableColumns]="columns"
        [hasMenu]="hasMenu()"
        [contextActions]="contextActions"
        [notFoundText]="(selectedCategory? selectedCategory.name: '') +' ангилалд урамшуулал олдсонгүй'"
        [loading]="loading"
        (rowSelection)="selectCourse($event)"
        (rowClicked)="navigateToLaunchPage($event)"
        (getContextActions)="getContextActions($event)"
        (selectedAction)="actionSelected($event)">
      </jrs-timed-course-table>
    </div>
  `,
  styleUrls: ['./timed-course-container.component.scss']
})
export class TimedCourseContainerComponent implements OnInit {
  data: TimedCourseModel[] = [];
  role: string;
  columns = TIMED_COURSE_COLUMN;
  publishedActions = TIMED_COURSE_PUBLISHED_ACTIONS;
  notPublishedActions = TIMED_COURSE_NOT_PUBLISHED_ACTIONS;
  pendingActions = TIMED_COURSE_PENDING_ACTIONS;
  expiredPublishedActions = EXPIRED_PUBLISHED_TIMED_COURSE_ACTIONS;
  PUBLISH_STATUS_FILTER = 'user.promotion.publishFilter';
  CREATE_BUTTON = 'user.promotion.addButton';
  MENU_PERMISSION = 'user.promotion.contextMenu';
  READERSHIP_BUTTON = 'user.promotion.enrollmentToReadershipBtnId';
  publishStatus = COURSE_PUBLISH_STATUS;
  contextActions: DropdownModel[];
  categories: CategoryItem[] = [];
  states = this.sb.constants.TIMED_COURSE_STATE ? this.sb.constants.TIMED_COURSE_STATE : [];
  selectedState: string;
  selectedCategory: CategoryItem;
  selectedCourse: TimedCourseModel;
  loading = false;
  isTabletMode: boolean;
  private selectedPublishStatus = COURSE_PUBLISH_STATUS[0].id;

  constructor(private sb: TimedCourseSandboxService) {
    this.sb.userRole$.subscribe(res => this.role = res)
  }

  ngOnInit(): void {
    this.sb.getMediaBreakPointChange().subscribe(res => {
      const currentMode = (res == 'media_md' || res == "media_sm" || res == "media_s" || res == "media_xs");
      if (currentMode != this.isTabletMode) {
        this.isTabletMode = currentMode;
      }
    });
    this.loading = true;
    this.sb.getTimedCourseCategories().subscribe(res => {
      this.categories = res;
      this.selectState();
      this.categorySelect(this.sb.getSelectedCategory() ? this.sb.getSelectedCategory() : this.categories[0]);
      this.setNotification();
    })
  }

  navigateToCreatePage(): void {
    this.sb.navigateByUrl('/timed-course/create');
  }

  navigateToLaunchPage(course: TimedCourseModel): void {
    if (course.publishStatus === 'PUBLISHED' && course.status !== Status.AUDIT) {
      if (this.role != UserRoleProperties.adminRole.id && course.status == Status.IN_PROGRESS) {
        this.sb.navigateByUrl('/timed-course/launch/' + course.id + '/' + true);
      } else {
        this.sb.navigateByUrl('/timed-course/launch/' + course.id + '/' + false);
      }
    } else if (this.role !== UserRoleProperties.adminRole.id) {
      this.sb.navigateByUrl('/timed-course/read/' + course.id);
    }
  }

  categorySelect(selectedCategory): void {
    this.selectedCategory = selectedCategory;
    this.sb.setSelectedCategory(selectedCategory);
    this.updateTimedCourseTable();
  }

  changeStatus(publishStatus): void {
    this.selectedPublishStatus = publishStatus.id;
    this.updateTimedCourseTable();
  }

  stateSelect(selectedState: DropdownModel): void {
    this.loading = true;
    if (this.selectedCategory.id != null && this.selectedCategory.id) {
      this.selectedState = selectedState.id;
      this.sb.setSelectedType(selectedState);
      this.updateTimedCourseTable();
    }
  }

  actionSelected(action): void {
    switch (action) {
      case TIMED_COURSE_ACTION_LAUNCH:
        this.navigateToLaunchPage(this.selectedCourse);
        if (this.role != UserRoleProperties.adminRole.id) {
          this.sb.updateNotification(this.selectedCourse.id);
        }
        break;
      case TIMED_COURSE_ACTION_EDIT:
        this.sb.navigateByUrl('/timed-course/update/' + this.selectedCourse.id);
        break;
      case TIMED_COURSE_ACTION_STATISTIC:
        this.sb.navigateByUrl('/timed-course/statistics/' + this.selectedCourse.id);
        break;
      case TIMED_COURSE_ACTION_CLONE:
        this.cloneCourse();
        break;
      case TIMED_COURSE_ACTION_HIDE:
        this.hideCourse();
        break;
      case TIMED_COURSE_ACTION_DELETE:
        this.deleteCourse();
        break;
      default:
        break;
    }
  }

  getContextActions(row: TimedCourseModel): void {
    this.selectedCourse = row;
    if (row.publishStatus == PublishStatus.PUBLISHED) {
      this.contextActions = this.sb.filterWithPermission(this.publishedActions);
    }
    if (row.publishStatus == PublishStatus.UNPUBLISHED) {
      this.contextActions = this.sb.filterWithPermission(this.notPublishedActions);
    } else if (row.publishStatus == PublishStatus.PENDING) {
      this.contextActions = this.sb.filterWithPermission(this.pendingActions);
    } else if (row.state === CourseState.EXPIRED) {
      this.contextActions = this.sb.filterWithPermission(this.expiredPublishedActions);
    } else {
      this.contextActions = this.sb.filterWithPermission(this.publishedActions);
    }
  }

  setNotification(): void {
    this.sb.courseNotification$.subscribe(res => {
      if (res != null) {
        const categories = res.get(this.sb.constants.TIMED_COURSE_NAME).subCategory;
        for (const category of this.categories) {
          const properties = categories.get(category.id);
          if (properties) {
            category.count = properties.userNewTotal;
          }
        }
      }
    })
  }

  selectCourse(course: TimedCourseModel): void {
    this.selectedCourse = course;
  }

  hasMenu(): boolean {
    return this.sb.getPermission(this.MENU_PERMISSION);
  }

  openDateInterval(): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = this.sb.constants.READERSHIP_DIALOG_TITLE
    config.background = true;
    config.blur = false;
    config.width = 'auto'
    config.data = {
      info: 'Үргэлжлүүлэх дарсны дараа сонгосон хугацааны хооронд үүсгэсэн, сонгосон төлвийн бүх урамшууллууд унших төлөвтэй болж, прогресс хадгалдаггүй болно.',
      withState: true
    }
    const dialogRef = this.sb.openDialog(DateIntervalDialogComponent, config);
    dialogRef.afterClosed.subscribe(res => {
      if (res) {
        this.loading = true;
        this.sb.convertEnrollmentToReadership(res.startDate, res.endDate, res.selectedState).subscribe(() => {
          this.loading = false;
        }, () => {
          this.loading = false;
        })
      }
    })
  }

  private updateTimedCourseTable(): void {
    if (this.selectedCategory.id != null && this.selectedCategory.id) {
      this.loading = true;
      this.sb.getNotification();
      this.sb.getTimedCourseData(this.selectedCategory.id, this.selectedCategory.name, this.selectedState, this.selectedPublishStatus).subscribe(
        res => {
          this.data = res;
          this.loading = false;
        }, () => {
          this.loading = false;
        });
    }
  }

  private deleteCourse(): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Урамшуулал устгах уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.name}' нэртэй урамшуулал устгахдаа итгэлтэй байна уу?\nУстгасан урамшууллыг дахин сэргээх боломжгүйг анхаарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.deleteTimedCourse(this.selectedCourse.id).subscribe(() => {
          this.updateTimedCourseTable();
          this.loading = false;
          this.sb.openSnackbar("Урамшуулал амжилттай устгалаа", true);
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Урамшуулал устгахад алдаа гарлаа");
        });
      }
    });
  }

  private cloneCourse() {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Урамшуулал хуулах уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.name}' нэртэй урамшуулал хуулах бол ТИЙМ гэж дарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.cloneTimedCourse(this.selectedCourse.id, this.selectedCourse).subscribe(() => {
          this.updateTimedCourseTable();
          this.loading = false;
          this.sb.openSnackbar("Урамшуулал амжилттай хууллаа", true);
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Урамшуулал хуулахад алдаа гарлаа");
        });
      }
    });
  }

  private hideCourse() {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Урамшуулал нуух уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.name}' нэртэй урамшуулал нуухдаа итгэлтэй байна уу?\n
      Нуусан урамшууллын статистик устана, дахин сэргээх боломжгүйг анхаарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.hideTimedCourse(this.selectedCourse.id).subscribe(() => {
          this.updateTimedCourseTable();
          this.loading = false;
          this.sb.openSnackbar("Урамшуулал амжилттай нуулаа", true);
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Урамшуулал нуухад алдаа гарлаа");
        });
      }
    });
  }

  private selectState(): void {
    this.selectedState = this.sb.getSelectedType() ? this.sb.getSelectedType().id : this.states[0].id;
    this.states.forEach(state => {
      if (state.id == this.selectedState) {
        state.selected = true;
      } else {
        state.selected = false;
      }
    })
  }
}
