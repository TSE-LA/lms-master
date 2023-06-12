import {Component, OnDestroy} from '@angular/core';
import {CategoryItem} from "../../../../../../shared/src/lib/shared-model";
import {CommonSandboxService} from "../../common-sandbox.service";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {CLASSROOM_COURSE_SEARCH, ONLINE_COURSE_SEARCH, TIMED_COURSE_SEARCH} from "../../../../../../shared/src/lib/shared-constants";
import {UserRoleProperties} from "../../common.model";
import {Subscription} from "rxjs";

@Component({
  selector: 'jrs-search-result',
  template: `
    <jrs-section [width]="'85vw'" [minWidth]="'fit-content'">
      <div class="flex">
        <jrs-header-text [margin]="false" [load]="loading">{{headerText}}</jrs-header-text>
        <span class="spacer"></span>
        <span *ngIf="!loading">Хайлтаар олдсон: {{data.length}} үр дүн</span>
      </div>
      <jrs-dynamic-table
        [maxWidth]="'unset'"
        [tableColumns]="columns"
        [dataSource]="data"
        [loading]="loading"
        (clickOnRow)="launch($event)">
      </jrs-dynamic-table>
    </jrs-section>

  `,
  styleUrls: ['./search-result.component.scss']
})
export class SearchResultComponent implements OnDestroy {
  loading: boolean;
  columns = this.sb.constants.SEARCH_RESULT_TABLE_COLUMNS;
  courseType: string;
  byName: boolean;
  byDescription: boolean;
  byCategory: boolean;
  searchValue: string;
  data: any[] = [];
  headerText: string;
  role = this.sb.role;
  routerSubscription: Subscription;
  categories: CategoryItem[] = [];

  constructor(private sb: CommonSandboxService, private route: ActivatedRoute, private router: Router) {
    this.routerSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        if (this.router.url.includes('/search-result')) {
          this.getSearchResult();
        }
      }
    })
  }

  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
  }


  getSearchResult(): void {
    const queryParam = this.route.snapshot.queryParamMap;
    this.byCategory = queryParam.get('byCategory') == 'true';
    this.byName = queryParam.get('byName') == 'true';
    this.byDescription = queryParam.get('byDescription') == 'true';
    this.searchValue = queryParam.get('searchValue');
    this.courseType = queryParam.get('courseType');
    if (this.courseType == TIMED_COURSE_SEARCH) {
      this.columns = this.sb.constants.TIMED_COURSE_SEARCH_TABLE_COLUMNS;
    } else {
      this.columns = this.sb.constants.OTHER_COURSE_SEARCH_TABLE_COLUMNS;
    }
    this.getHeaderText();
    if (this.byCategory) {
      this.getCategories();
    } else {
      this.searchByCourse();
    }
  }

  getCategories(): void {
    this.loading = true;
    switch (this.courseType) {
      case ONLINE_COURSE_SEARCH:
        this.sb.getOnlineCourseCategories().subscribe(res => {
          this.searchByCategories(this.filterCategories(res));
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Цахим сургалтын ангилал авахад алдаа гарлаа.", false);
        })
        break;
      case CLASSROOM_COURSE_SEARCH:
        this.sb.getClassroomCourseCategories().subscribe(res => {
          this.searchByCategories(this.filterCategories(res));
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Танхимын сургалтын ангилал авахад алдаа гарлаа.", false);
        })
        break;
      case TIMED_COURSE_SEARCH:
        this.sb.getTimedCourseCategories().subscribe(res => {
          this.searchByCategories(this.filterCategories(res));
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Урамшууллын ангилал авахад алдаа гарлаа.", false);
        })
        break;
      default:
        break;
    }
  }

  searchByCourse(): void {
    this.loading = true;
    let result = [];
    const isTimedCourse = this.courseType == TIMED_COURSE_SEARCH;
    this.sb.searchCourses(this.searchValue, this.byName, this.byDescription, isTimedCourse).subscribe(res => {
      result = result.concat(res);
      if (isTimedCourse == true) {
        this.data = result;
      } else {
        this.separateData(result);
      }
      this.loading = false;
    }, () => {
      this.loading = false;
      this.sb.openSnackbar("Сургалт хайхад алдаа гарлаа.", false);
    });
  }

  separateData(datas: any): void {
    const result = [];
    if (this.courseType == ONLINE_COURSE_SEARCH) {
      this.sb.getOnlineCourseCategories().subscribe(categories => {
        for (let data of datas) {
          for (let category of categories) {
            if (data.categoryId == category.id) {
              result.push(data);
            }
          }
        }
        this.data = result;
      });
    } else {
      this.sb.getClassroomCourseCategories().subscribe(categories => {
        for (let data of datas) {
          for (let category of categories) {
            if (data.categoryId == category.id) {
              result.push(data);
            }
          }
        }
        this.data = result;
      });
    }
  }

  filterCategories(categories: CategoryItem[]): CategoryItem[] {
    return categories.filter(category => {
      return category.name.toLowerCase().includes(this.searchValue.toLowerCase());
    })
  }

  searchByCategories(categories: CategoryItem[]): void {
    const isTimedCourse = this.courseType == TIMED_COURSE_SEARCH;
    this.data = [];
    let result = [];
    for (let category of categories) {
      this.sb.searchByCourseCategory(category.id, isTimedCourse, category.name).subscribe(res => {
        result = result.concat(res);
        this.data = result.sort((first, second) => {
          const firstDate = new Date(first.date);
          const secondDate = new Date(second.date);
          return (secondDate as any) - (firstDate as any);
        })
        this.loading = false;
      }, () => {
        this.loading = false;
        this.sb.openSnackbar("Сургалт хайхад алдаа гарлаа.", false);
      });
    }
  }


  launch(course: any): void {
    const isLearner = this.role == UserRoleProperties.employeeRole.id;
    switch (this.courseType) {
      case ONLINE_COURSE_SEARCH:
        if (course.published) {
          this.sb.navigateByUrl('/online-course/launch/' + course.id + '/' + isLearner);
        } else {
          this.sb.navigateByUrl('/online-course/update/' + course.id);
        }
        break;
      case CLASSROOM_COURSE_SEARCH:
        this.sb.navigateByUrl('/classroom-course/launch/' + course.id);
        break;
      case TIMED_COURSE_SEARCH:
        if (course.published) {
          this.sb.navigateByUrl('/timed-course/launch/' + course.id + '/' + isLearner);
        } else {
          this.sb.navigateByUrl('/timed-course/update/' + course.id);
        }
        break;
      default:
        break;
    }
  }

  getHeaderText(): void {
    switch (this.courseType) {
      case ONLINE_COURSE_SEARCH:
        this.headerText = this.sb.constants.COURSE_NAMES[0].name;
        break;
      case CLASSROOM_COURSE_SEARCH:
        this.headerText = this.sb.constants.COURSE_NAMES[1].name;
        break;
      case TIMED_COURSE_SEARCH:
        this.headerText = this.sb.constants.COURSE_NAMES[2].name;
        break;
      default:
        break;
    }
  }


}
