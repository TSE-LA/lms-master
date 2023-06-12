import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {CreateClassroomCourseTableModel, EventAction} from "../../model/classroom-course.model";
import {ClassroomCourseSandboxService} from "../../classroom-course-sandbox.service";
import {CoursePropertyUtil} from "../../../../../../shared/src/lib/utilities/course-property.util";
import {
  ACTION_CANCEL,
  ACTION_DELETE,
  ACTION_EDIT,
  ACTION_EDIT_ATTENDANCE,
  ACTION_LAUNCH,
  ACTION_POSTPONE,
  ACTION_START,
  CLASSROOM_COURSE_TABLE_COLUMNS,
  COURSE_STATES,
  STATE_ITEMS,
} from "../../model/classroom-course.constants";
import {DropdownModel} from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {NotesDialogComponent} from "../../../../../../shared/src/lib/dialog/notes-dialog/notes-dialog.component";
import { UserRoleProperties } from '../../../common/common.model';


@Component({
  selector: 'jrs-classroom-course-new',
  template: `
    <jrs-section [width]="'78vw'" [height]="'unset'" [minWidth]="'unset'">
      <div class="header">
        <div *canAccess="STATE_FILTER">
          <jrs-dropdown-view
            [label]="'Сургалтын төлөв'"
            [chooseFirst]="true"
            [values]="state"
            (selectedValue)="changeState($event)"
            [color]="'light'"
            [outlined]="true"
            [size]="'medium'">
          </jrs-dropdown-view>
        </div>
        <div *canAccess="TYPE_FILTER">
          <jrs-dropdown-view
            [chooseFirst]="true"
            [label]="'Хамрах хүрээ'"
            [values]="types"
            [color]="'light'"
            [outlined]="true"
            (selectedValue)="changeType($event)"
            [size]="'medium'">
          </jrs-dropdown-view>
        </div>
        <div>
          <jrs-dropdown-view
            [values]="categoryItems"
            [defaultValue]="selectedCategory.name"
            (selectedValue)="changeCategory($event)"
            [label]="'Сургалтын ангилал'"
            [color]="'light'"
            [outlined]="true"
            [load]="categoryLoading"
            [tooltip]="true"
            [size]="'medium'">
          </jrs-dropdown-view>
        </div>
        <div *canAccess="CREATE_BUTTON" class=" margin-top">
          <jrs-button
            [iconName]="addIconName"
            [isMaterial]="true"
            [iconColor]="'light'"
            [size]=" 'medium'"
            [title]="addButtonName"
            (clicked)="createClassroomCourse()">
          </jrs-button>
        </div>
      </div>
      <jrs-dynamic-table
        [loading]="loading"
        [minWidth]="'unset'"
        [maxWidth]="'unset'"
        [height]="'unset'"
        [tableMinHeight]="'65vh'"
        [freezeFirstColumn]="true"
        [noCircle]="true"
        [dataPerPage]="100"
        [dataSource]="dataSource"
        [contextActions]="actions"
        [hasAction]="isAdmin()"
        [role]="role"
        (rowSelected)="rowSelected($event)"
        (doubleClickOnRow)="dblClickOnRow($event)"
        (selectedAction)="actionTriggered($event)"
        [tableColumns]="tableColumns">
      </jrs-dynamic-table>
    </jrs-section>
    <jrs-calendar-sidenav *ngIf="doubleClicked"
      [drawerToggle]='showDrawer'
      [data]="selectedCourse">
    </jrs-calendar-sidenav>
  `,
  styleUrls: ['./classroom-course-table.component.scss']
})
export class ClassroomCourseTableComponent implements OnInit {
  @Output() selectedAction = new EventEmitter<any>();
  TYPE_FILTER = 'user.classroomCourse.typeFilter';
  STATE_FILTER = 'user.classroomCourse.stateFilter';
  loading = false;
  tableColumns = CLASSROOM_COURSE_TABLE_COLUMNS;
  state = COURSE_STATES;
  types: any[] = [];
  selectedState: string;
  selectedType: string;
  dataSource: CreateClassroomCourseTableModel[] = [];
  categoryItems: DropdownModel[];
  role: string;
  CREATE_BUTTON = 'user.classroomCourse.addButton';
  addButtonName = 'Сургалт нэмэх';
  addIconName = '';
  isTablet: boolean;
  mobile: boolean;
  categoryLoading: boolean;
  actions: EventAction[];
  selectedCategory: DropdownModel;
  showDrawer = false;
  selectedCourse: any;
  doubleClicked = false;

  constructor(private sb: ClassroomCourseSandboxService) {
    this.sb.userRole$.subscribe(res => this.role = res)
    this.sb.getMediaBreakPointChange().subscribe(media => {
      if (media == 'media_md' || media == 'media_s' || media == 'media_sm' || media == 'media_xs') {
        this.addButtonName = '';
        this.addIconName = 'add';
        this.isTablet = true;
      } else {
        this.addButtonName = 'Сургалт нэмэх';
        this.addIconName = '';
        this.isTablet = false;
      }
      this.mobile = media == 'media_s' || media == 'media_sm' || media == 'media_xs';
    });
  }

  ngOnInit(): void {
    this.selectedState = COURSE_STATES[0].id;
    this.types = this.sb.constants.COURSE_FILTER_TYPES;
    this.selectedType = this.sb.constants.COURSE_FILTER_TYPES[0].id;
    this.loading = true;
    this.categoryLoading = true;
    this.sb.getClassroomCourseCategories()
      .subscribe((categories) => {
        this.loading = false;
        this.categoryItems = categories;
        this.selectedCategory = this.categoryItems[0];
        this.categoryLoading = false;
        this.updateClassroomCourse();
      }, () => {
        this.loading = false;
        this.categoryLoading = false;
      });
  }

  isAdmin(): boolean {
    return this.role === UserRoleProperties.adminRole.id;
  }

  createClassroomCourse(): void {
    this.sb.navigateByUrl('/classroom-course/create');
  }

  changeCategory(category): void {
    this.loading = true;
    this.selectedCategory = category;
    this.updateClassroomCourse();
  }

  changeState(state): void {
    this.selectedState = state.id;
    this.updateClassroomCourse();
  }

  changeType(type): void {
    this.selectedType = type.id;
    this.updateClassroomCourse();
  }

  updateClassroomCourse() {
    this.loading = true;
    this.sb.getClassroomCourses(this.selectedCategory.id, this.selectedType, this.selectedState).subscribe(res => {
      this.dataSource = res.sort((a,b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
      this.loading = false;
    }, () => {
      this.loading = false;
    });
  }

  private deleteCourse(event): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Танхимын сургалт устгах уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.courseName}' нэртэй сургалт устгахдаа итгэлтэй байна уу?\nУстгасан сургалтыг дахин сэргээх боломжгүйг анхаарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.deleteClassroomCourse(event.id).subscribe(() => {
          this.updateClassroomCourse();
          this.loading = false;
          this.sb.openSnackbar("Танхимын сургалт амжилттай устгалаа", true);
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Танхимын сургалт устгахад алдаа гарлаа");
        });
      }
    });
  }

  private cancelCourse(event): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Танхимын сургалт цуцлах уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.courseName}' нэртэй сургалт цуцлах шалгаан оруулна уу.`),
      label: "Цуцлах шалтгаан",
      submitButton: "Цуцлах"
    };
    const dialogRef = this.sb.openDialog(NotesDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.sb.cancelClassroomCourse(event.id, result).subscribe(() => {
          this.updateClassroomCourse();
          this.loading = false;
          this.sb.openSnackbar("Сургалтыг амжилттай цуцаллаа", true);
        }, () => {
          this.sb.openSnackbar("Сургалтыг цуцлах шалтгаан оруулна уу.");
          this.loading = false;
        });
      }
    });
  };

  private postponeCourse(event): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Танхимын сургалт хойшлуулах уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.courseName}' нэртэй сургалт хойшлуулах шалгаан оруулна уу.`),
      label: "Хойшлуулах шалтгаан",
      submitButton: "Хойшлуулах"
    };
    const dialogRef = this.sb.openDialog(NotesDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.sb.postponeClassroomCourse(event.id, result).subscribe(() => {
          this.updateClassroomCourse();
          this.loading = false;
          this.sb.openSnackbar("Сургалтыг амжилттай хойшлууллаа", true);
        }, () => {
          this.sb.openSnackbar("Сургалтыг хойшлуулах шалтгаан оруулна уу.");
          this.loading = false;
        });
      }
    })
  };

  private startEvent(event): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Танхимын сургалт эхлүүлэх уу?";
    config.data = {
      info: (`Та '${this.selectedCourse.courseName}' нэртэй сургалт эхлүүлэх бол ТИЙМ гэж дарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.startClassroomCourse(event.id).subscribe(() => {
          this.updateClassroomCourse();
          this.loading = false;
          this.sb.openSnackbar("Танхимын сургалт амжилттай эхэллээ", true);
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Танхимын сургалт эхлүүлэхэд алдаа гарлаа");
        });
      }
    });
  }

  actionTriggered(event): void {
    if (event) {
      switch (event.action) {
        case ACTION_DELETE:
          this.deleteCourse(event);
          break;
        case ACTION_EDIT:
          this.sb.navigateByUrl('/classroom-course/update/' + event.id);
          break;
        case ACTION_CANCEL:
          this.cancelCourse(event);
          break;
        case ACTION_POSTPONE:
          this.postponeCourse(event);
          break;
        case ACTION_START:
          this.startEvent(event);
          break;
        case ACTION_LAUNCH:
          this.sb.navigateByUrl('/classroom-course/launch/' + event.id);
          break;
        case ACTION_EDIT_ATTENDANCE:
          this.sb.navigateByUrl('/classroom-course/attendance/' + event.id);
          break;
        default:
          break;
      }
    }
  }

  rowSelected(event: any) {
    this.selectedCourse = event;
    this.actions = STATE_ITEMS.find(({name, role}) => name === this.selectedCourse.status && role === this.role).actions || [];
  }

  dblClickOnRow(data: any){
    this.doubleClicked = true;
    this.showDrawer = false;
    this.sb.getCourseById(data.id).subscribe(res => {
      if(res) {
        this.selectedCourse = CoursePropertyUtil.mapToCourseProperties(res);
        this.showDrawer = true;
      }
    })
  }

}
