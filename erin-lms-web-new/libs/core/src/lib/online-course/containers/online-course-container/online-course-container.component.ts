import {Component, OnInit} from '@angular/core';
import {OnlineCourseSandboxService} from "../../online-course-sandbox.service";
import {EnrollmentState, OnlineCourseModel} from "../../models/online-course.model";
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import {COURSE_PUBLISH_STATUS, UserRoleProperties} from "../../../common/common.model";
import {
  DEFAULT_THUMBNAIL_URL,
  ONLINE_COURSE_STATISTICS,
  ONLINE_COURSE_CARD_BUTTON_TEXTS,
  ONLINE_COURSE_DELETE,
  ONLINE_COURSE_EDIT,
  ONLINE_COURSE_HIDE,
  ONLINE_COURSE_LAUNCH,
  ONLINE_COURSE_PUBLISHED_ACTION,
  ONLINE_COURSE_UNPUBLISHED_ACTION,
  PUBLISH_STATUS
} from "../../models/online-course.constant";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'jrs-online-course-container',
  template: `
    <div class="online-course-header">
      <div *canAccess="CREATE_BUTTON" class="margin-top align-right">
        <jrs-button
          [title]="!isTabletMode ? 'Сургалт нэмэх': 'Нэмэх'"
          [size]="'medium'"
          [width]="isTabletMode ? '100%' : 'auto'"
          (clicked)="navigateToCreatePage()"
          [attr.data-test-id]="'course-create-button'">
        </jrs-button>
      </div>

      <div>
        <jrs-dropdown-view
          [defaultValue]="selectedCategoryName"
          [values]="categoryItems"
          (selectedValue)="changeCategory($event)"
          [label]="'Сургалтын ангилал'"
          [color]="'light'"
          [outlined]="true"
          [load]="categoryLoading"
          [tooltip]="true"
          [size]="'medium'">
        </jrs-dropdown-view>
      </div>

      <div *canAccess="PUBLISH_STATUS_FILTER">
        <jrs-dropdown-view
          [label]="'Нийтэлсэн төлөв'"
          [chooseFirst]="true"
          [values]="publishStatus"
          (selectedValue)="changeStatus($event)"
          [color]="'light'"
          [outlined]="true"
          [size]="'medium'">
        </jrs-dropdown-view>
      </div>

      <div *canAccess="TYPE_FILTER">
        <jrs-dropdown-view
          [chooseFirst]="true"
          [label]="'Хамрах хүрээ'"
          [values]="getFilteredTypes()"
          [color]="'light'"
          [outlined]="true"
          (selectedValue)="changeType($event)"
          [size]="'medium'">
        </jrs-dropdown-view>
      </div>
    </div>
    <div *ngIf="loading">
      <jrs-grid-container>
        <div *ngFor="let i of [].constructor(row * column)">
          <jrs-card-skeleton-loader></jrs-card-skeleton-loader>
        </div>
      </jrs-grid-container>
    </div>
    <div *ngIf="!loading">

      <jrs-grid-container [column]="column" [row]="row">
        <div *ngFor="let onlineCourse of splitOnlineCourse">
          <jrs-card
            [course]="onlineCourse"
            [defaultThumbnailUrl]="defaultThumbnailUrl"
            [buttonText]="getCardButtonText(onlineCourse.publishStatus)"
            [state]="'Шинэ'"
            [per]="CREATE_BUTTON"
            [contextValues]="filteredActions"
            [hasMenu]="hasMenu"
            [hasProgress]="hasProgress"
            [hasNotification]="hasNotification"
            (contextMenuClicked)="actionTriggered($event)"
            (contextMenuTriggered)="contextMenuTriggered($event)"
            (clicked)="clickCardButton(onlineCourse)"
            [attr.data-test-id]="'course-namíe-' + onlineCourse.name">
          </jrs-card>
        </div>
      </jrs-grid-container>
    </div>
    <jrs-not-found-page
      [text]="selectedCategoryName+ ' ангилалд сургалт олдсонгүй'"
      [show]="!loading && splitOnlineCourse.length === 0">
    </jrs-not-found-page>
    <div class="flex justify-end margin-top">
      <jrs-paginator [perPageNumber]="row * column" [contents]="onlineCourses"
                     (pageClick)="splitCourse($event)"></jrs-paginator>
    </div>
  `,
  styleUrls: ['./online-course-container.component.scss']
})
export class OnlineCourseContainerComponent implements OnInit {
  TYPE_FILTER = 'user.onlineCourse.typeFilter';
  PUBLISH_STATUS_FILTER = 'user.onlineCourse.publishStatusFilter';
  CREATE_BUTTON = 'user.onlineCourse.addButton';
  MENU = 'user.onlineCourse.contextMenu';
  PROGRESS = 'user.onlineCourse.progress';
  NOTIFICATION = 'user.onlineCourse.notification';
  hasMenu: boolean;
  hasProgress: boolean;
  hasNotification: boolean;
  defaultThumbnailUrl = DEFAULT_THUMBNAIL_URL;
  loading: boolean;
  categoryLoading: boolean;
  selectedCategoryName: string;
  categoryItems: DropdownModel[];
  courseTypes: any[] = [];
  selectedType: string;
  publishStatus = COURSE_PUBLISH_STATUS;
  selectedPublishStatus: string;
  selectedCourse: OnlineCourseModel;
  temp: OnlineCourseModel[];
  maxCourseNumber: number;
  index = 0;
  splitOnlineCourse: OnlineCourseModel[] = [];
  onlineCourses: OnlineCourseModel[] = [];
  isTabletMode: boolean;
  UNPUBLISHED_ACTIONS: DropdownModel[] = ONLINE_COURSE_UNPUBLISHED_ACTION;
  PUBLISHED_ACTIONS: DropdownModel[] = ONLINE_COURSE_PUBLISHED_ACTION;
  filteredActions: DropdownModel[];
  row = 2;
  column = 4;
  private authRole = this.sb.authRole$;
  private role: string;
  private selectedCategory: DropdownModel;

  constructor(private sb: OnlineCourseSandboxService) {
    this.authRole.subscribe(res => this.role = res);
    this.calculateGrid();
  }

  ngOnInit(): void {
    this.selectedType = this.sb.constants.COURSE_FILTER_TYPES[0].id;
    this.selectedPublishStatus = COURSE_PUBLISH_STATUS[0].id;
    this.courseTypes = this.sb.constants.COURSE_FILTER_TYPES;
    this.sb.getMediaBreakPointChange().subscribe(res => {
      const currentMode = (res == "media_sm" || res == "media_s" || res == "media_xs" || res == "media_md");
      if (currentMode != this.isTabletMode) {
        this.isTabletMode = currentMode;
      }
    });

    this.loading = true;
    this.categoryLoading = true;
    this.sb.getOnlineCourseCategories()
      .subscribe((categories) => {
        this.stopLoading();
        this.categoryItems = categories;
        this.selectedCategory = this.sb.getSelectedCategory() ? this.sb.getSelectedCategory() : this.categoryItems[0];
        this.selectedCategoryName = this.selectedCategory.name;
        this.categoryLoading = false;
        this.updateOnlineCourses();
      }, () => {
        this.loading = false;
        this.categoryLoading = false;
      });
    this.hasMenu = this.sb.getPermission(this.MENU);
    this.hasProgress = this.sb.getPermission(this.PROGRESS);
    this.hasNotification = this.sb.getPermission(this.NOTIFICATION)
    this.calculateGrid();
  }

  clickCardButton(course): void {
    this.selectedCourse = course;
    if (course.publishStatus === 'PUBLISHED') {
      this.actionTriggered(ONLINE_COURSE_LAUNCH);
    } else {
      this.actionTriggered(ONLINE_COURSE_EDIT);
    }
  }

  updateOnlineCourses(): void {
    this.temp = [];
    this.loading = true;
    this.sb.getOnlineCourses(this.selectedCategory.id, this.selectedType, this.selectedPublishStatus)
      .subscribe((onlineCourses: OnlineCourseModel[]) => {
        if (onlineCourses.length > this.maxCourseNumber) {
          this.index = 0;
          this.temp = onlineCourses;
          this.onlineCourses = [];
          for (let i = this.index; i < this.index + this.maxCourseNumber; i++) {
            this.onlineCourses.push(this.temp[i]);
          }
          this.index += this.maxCourseNumber;
          this.loading = false;
        } else {
          this.onlineCourses = onlineCourses;
        }
        this.calculateGrid();
        this.loading = false;
      }, () => {
        this.loading = false;
      });
  }

  changeType(courseType): void {
    this.selectedType = courseType.id;
    this.updateOnlineCourses();
  }

  getFilteredTypes(): any {
    let courseTypes = this.courseTypes
    if (this.courseTypes) {
      if (this.role === UserRoleProperties.supervisorRole.id) {
        courseTypes = this.courseTypes.filter(type => type.name !== UserRoleProperties.managerRole.name.toUpperCase())
      }
    }
    return courseTypes
  }

  splitCourse(courses: OnlineCourseModel[]): void {
    this.splitOnlineCourse = courses;
  }

  changeStatus(publishStatus): void {
    this.selectedPublishStatus = publishStatus.id;
    this.updateOnlineCourses();
  }

  changeCategory(category): void {
    this.loading = true;
    this.selectedCategory = category;
    this.selectedCategoryName = this.selectedCategory.name;
    this.sb.setSelectedCategory(category);
    this.updateOnlineCourses();
  }

  getCardButtonText(publishStatus: string): string {
    return ONLINE_COURSE_CARD_BUTTON_TEXTS[publishStatus];
  }

  contextMenuTriggered(selectedCourse: OnlineCourseModel): void {
    this.selectedCourse = selectedCourse;
    this.filteredActions = this.getActions();
  }

  navigateToCreatePage(): void {
    this.sb.navigateByUrl('/online-course/create');
  }

  actionTriggered(action: DropdownModel): void {
    switch (action) {
      case ONLINE_COURSE_DELETE:
        this.deleteCourse();
        break;
      case ONLINE_COURSE_EDIT:
        this.sb.navigateByUrl('/online-course/update/' + this.selectedCourse.id);
        break;
      case ONLINE_COURSE_LAUNCH:
        this.role != UserRoleProperties.adminRole.id && this.selectedCourse.enrollmentState == EnrollmentState.IN_PROGRESS ?
          this.sb.navigateByUrl('/online-course/launch/' + this.selectedCourse.id + '/' + true) :
          this.sb.navigateByUrl('/online-course/launch/' + this.selectedCourse.id + '/' + false)
        break;
      case ONLINE_COURSE_STATISTICS:
        this.sb.navigateByUrl('/online-course/statistics/' + this.selectedCourse.id);
        break;
      case ONLINE_COURSE_HIDE:
        this.hideCourse();
        break;
      default:
        break;
    }
  }

  private deleteCourse(): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Цахим сургалт устгах уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.name}' нэртэй сургалт устгахдаа итгэлтэй байна уу?\nУстгасан сургалтыг дахин сэргээх боломжгүйг анхаарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.deleteOnlineCourse(this.selectedCourse.id).subscribe(() => {
          this.updateOnlineCourses();
          this.loading = false;
          this.sb.openSnackbar("Цахим сургалт амжилттай устгалаа", true);
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Цахим сургалт устгахад алдаа гарлаа");
        });
      }
    });
  }

  private getActions(): DropdownModel[] {
    if (this.selectedCourse !== undefined) {
      if (this.selectedCourse.publishStatus === PUBLISH_STATUS.PUBLISHED) {
        return this.sb.filterWithPermission(this.PUBLISHED_ACTIONS);
      } else {
        return this.sb.filterWithPermission(this.UNPUBLISHED_ACTIONS);
      }
    }
  }

  private stopLoading(): void {
    this.loading = false;
  }

  private hideCourse() {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Цахим сургалт нуух уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.name}' нэртэй сургалт нуухдаа итгэлтэй байна уу?\n Нуусан сургалтын статистик устана, дахин сэргээх боломжгүйг анхаарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.hideOnlineCourse(this.selectedCourse.id).subscribe(() => {
          this.updateOnlineCourses();
          this.loading = false;
          this.sb.openSnackbar("Цахим сургалтыг амжилттай нуулаа", true);
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Цахим сургалтыг нуухад алдаа гарлаа");
        });
      }
    });
  }

  calculateGrid(): void {
    this.sb.getMediaBreakPointChange().subscribe(res => {
      switch (res) {
        case 'media_xxl' :
          this.row = this.countRows(4);
          this.column = 4;
          break;
        case 'media_xl' :
          this.row = this.countRows(4);
          this.column = 4;
          break;
        case 'media_lg' :
        case 'media_ml' :
          this.row = this.countRows(3);
          this.column = 3;
          break;
        case  'media_md' :
        case  'media_sm' :
          this.row = this.countRows(2);
          this.column = 2;
          break;
        case  'media_s' :
        case  'media_xs' :
          this.row = this.countRows(1);
          this.column = 1;
          break;
        default:
          break;
      }
    })
  }

  private countRows(column: number): number {
    let row;
    switch (column) {
      case 4 :
        row = this.onlineCourses.length < 5 ? 1 : 2;
        break;
      case 3 :
        row = this.onlineCourses.length < 4 ? 1 : this.onlineCourses.length < 7 ? 2 : 3;
        break;
      case 2 :
        row = this.onlineCourses.length > 4 ? 3 : this.onlineCourses.length < 3 ? 1 : 2;
        break;
      case 1 :
        row = this.onlineCourses.length > 5 ? 6 : this.onlineCourses.length;
        break;
    }
    return row;
  }
}
