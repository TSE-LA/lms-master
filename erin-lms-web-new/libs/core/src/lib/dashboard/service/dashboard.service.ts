import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {Observable, throwError} from "rxjs";
import {
  ActivityDashletData,
  DetailedLearnerActivity,
  LearnerSuccessDashletModel,
  LearnerSuccessesMonthlyData,
  PublishedCourse,
  PublishedCourseCountData
} from "../dashboard-model/dashboard-model";
import {UserRoleProperties} from "../../common/common.model";
import {RoleValueUtil} from "../../../../../shared/src/lib/utilities/role-value.util";
import {SmallDashletModel} from "../../../../../shared/src/lib/shared-model";
import {MonthNameByNumberUtil} from "../../../../../shared/src/lib/utilities/month-name-by-number.util";
import {ClassroomCourseModel} from "../../classroom-course/model/classroom-course.model";
import {ClassroomCourseMapper} from "../../classroom-course/model/classroom-course.mapper";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {Store} from "@ngrx/store";
import {ApplicationState} from "../../common/statemanagement/state/ApplicationState";
import {BreakPointObserverService} from "../../../../../shared/src/lib/theme/services/break-point-observer.service";

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  userRole$ = this.store.select(state => state.auth.role)

  barChartGreen = 'assets/images/bar-chart-green.png';
  barChartPurple = 'assets/images/bar-chart-purple.png';
  certificateBadge = 'assets/images/badge.png';

  constructor(private httpClient: HttpClient,
              private store: Store<ApplicationState>,
              private snackbarService: SnackbarService,
              private breakPointService: BreakPointObserverService,) {
  }

  getMediaBreakPointChange(): Observable<string> {
    return this.breakPointService.getMediaBreakPointChange().pipe(map((res: any) => {
      return res;
    }))
  }
  getTimedCourseData(startDate: string, endDate: string, role): Observable<any> {
    let params = new HttpParams();
    params = this.getDateParamsFormatted(params, startDate, endDate);
    if (role == UserRoleProperties.adminRole.id) {
      return this.httpClient.get('/legacy/lms/promotion-statistics/created-promotion-count', {params})
        .pipe(map((res: any) => {
          return this.mapPromotionToCourseCountData(res.entity);
        }));
    } else {
      return this.httpClient.get('/legacy/lms/promotion-statistics/enrolled-promotion-count', {params})
        .pipe(map((res: any) => {
          return this.mapPromotionToCourseCountData(res.entity);
        }));
    }
  }

  getOnlineCourseData(startDate: string, endDate: string, role: string): Observable<any> {
    let params = new HttpParams();
    params = this.getDateParamsFormatted(params, startDate, endDate);
    params = params.append('parentCategoryId', 'online-course');
    if (role == UserRoleProperties.adminRole.id) {
      params = params.append('courseTypes', 'MANAGER');
      params = params.append('courseTypes', 'EMPLOYEE');
      params = params.append('courseTypes', 'SUPERVISOR');
      return this.httpClient.get('/lms/courses/created-course-count', {params}).pipe(map((res: any) => {
        return this.mapOnlineCourseToCourseCountData(res.entity);
      }));
    } else {
      return this.httpClient.get('/lms/courses/enrolled-course-count', {params}).pipe(map((res: any) => {
        return this.mapOnlineCourseToCourseCountData(res.entity);
      }));
    }
  }

  getOnlineCourseUserActivityData(groupId: string): Observable<SmallDashletModel[]> {
    return this.httpClient.get('/lms/analytics/activity/' + groupId).pipe(map((res: any) => {
      return this.mapToSmallDashlets(this.mapOnlineCourseActivity(res.entity), true);
    }));
  }

  getOnlineCourseLearnerActivityData(groupId: string): Observable<DetailedLearnerActivity[]> {
    return this.httpClient.get('/lms/analytics/activity/progress/' + groupId).pipe(map((res: any) => {
      return this.mapToDetailedLearnerActivity(res.entity, true)
    }));
  }


  getTimedCourseUserActivityData(groupId: string, asDashletData: boolean): Observable<SmallDashletModel[] | DetailedLearnerActivity[]> {
    return this.httpClient.get('/lms/analytics/promotion/activities?groupId=' + groupId).pipe(map((res: any) => {
      if (asDashletData) {
        return this.mapToSmallDashlets(this.mapTimedCourseActivity(res.entity), false);
      } else {
        return this.mapToDetailedLearnerActivity(res.entity.activities, false)
      }
    }));
  }

  getOnlineLearnerSuccessData(year: string, halfYear: string, group: string): Observable<LearnerSuccessDashletModel> {
    let params = new HttpParams();
    params = params.append('year', year);
    params = params.append('dateType', halfYear);
    params = params.append('groupType', group);
    return this.httpClient.get('/lms/learner-success/online', {params}).pipe(map((res: any) => {
      return this.mapLearnerSuccessData(res.entity);
    }))
  }

  getTimedLearnerSuccessData(year: string, halfYear: string, group: string) {
    let params = new HttpParams();
    params = params.append('year', year);
    params = params.append('dateType', halfYear);
    params = params.append('groupType', group);
    return this.httpClient.get('/lms/learner-success/promotion', {params}).pipe(map((res: any) => {
      return this.mapLearnerSuccessData(res.entity);
    }))
  }

  openSnackbar(text: string, success?: boolean): void {
    this.snackbarService.open(text, success)
  }

  getCalendarData(date: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('date', date);
    return this.httpClient.get('/lms/courses/courses-for-calendar', {params})
      .pipe(map((res: any) => {
        return res.entity.calendarDays
      }), catchError(err => {
        return throwError(err);
      }));
  }

  getCourseById(id: string): Observable<ClassroomCourseModel> {
    return this.httpClient.get('/lms/courses/' + id).pipe(map((res: any) => {
      return ClassroomCourseMapper.mapToClassroomCourseModel(res.entity);
    }));
  }


  mapLearnerSuccessData(data): LearnerSuccessDashletModel {
    const mapped: LearnerSuccessesMonthlyData[] = [];
    let totalScore = 0;
    let groupTotalScore = 0;
    const unique = this.removeDuplicates(data.learnerSuccesses, it => it.month);
    if (unique) {
      for (const data of unique) {
        totalScore += data.score;
        groupTotalScore += data.groupAvg;
        mapped.push({
          learnerScore: Math.floor(data.score),
          groupScore: Math.floor(data.groupAvg),
          month: MonthNameByNumberUtil.getMonth(data.month)
        })
      }
    }
    return {
      overallPercentage: data.overallScore + '/' + data.overallMaxScore + ' (' + this.getOverallDifference(data.overallScore, data.overallMaxScore) + ')',
      difference: Math.floor(data.difference) + '%',
      up: data.difference > 0,
      totalScore: totalScore,
      groupTotalScore: groupTotalScore,
      learnerSuccesses: mapped
    }
  }

  removeDuplicates(data, key): any[] {
    return [
      ...new Map(
        data.map(x => [key(x), x])
      ).values()
    ];
  }

  getOverallDifference(overallScore, overallMaxScore): string {
    if (overallScore > 0) {
      return ((overallScore * 100) / overallMaxScore).toFixed(0) + '%';
    } else {
      return 0 + '%';
    }
  }

  private mapOnlineCourseActivity(res: any): ActivityDashletData {
    return {
      learnerCount: res.learnerCount,
      completedLearnerCount: res.perfectViewersCount,
      averageSpentTime: res.averageTime,
      averageViewPercentage: res.averageProgress.toFixed(1) + '%'
    }
  }

  private mapTimedCourseActivity(res: any): ActivityDashletData {
    return {
      learnerCount: res.learners,
      averageSpentTime: res.averageTime,
      completedLearnerCount: res.perfectSupersCount + res.perfectEmployeeCount,
      completedEmployeeCount: res.perfectEmployeeCount,
      completedManagerCount: res.perfectSupersCount,
      averageViewPercentage: res.averageProgress.toFixed(1) + '%'
    }
  }


  private mapToDetailedLearnerActivity(res: any, isOnline: boolean): DetailedLearnerActivity[] {
    const result: DetailedLearnerActivity[] = [];
    for (let data of res) {
      result.push({
        username: data.username,
        role: RoleValueUtil.getRoleDisplayName(data.role),
        progress: isOnline ? Math.round(data.progress) : Math.round(data.status),
        groupPath: data.groupPath,
        userHistory: 'Дэлгэрэнгүй'
      })
    }
    return result;
  }

  private mapToSmallDashlets(res: ActivityDashletData, isOnlineCourse: boolean): SmallDashletModel[] {
    const result: SmallDashletModel[] = [];
    result.push(
      {
        title: 'Нийт суралцагчид',
        info: res.learnerCount,
        navigateLink: isOnlineCourse ? '/online-course/learner-activity' : '/timed-course/learner-activity',
        imageSrc: null,
        hasDropDown: false
      })
    result.push(
      {
        title: '90% танилцсан',
        info: res.completedLearnerCount,
        imageSrc: this.barChartPurple,
        hasDropDown: !isOnlineCourse,
        navigateLink: null,
        extraData: isOnlineCourse ? null : {employee: res.completedEmployeeCount, manager: res.completedManagerCount}
      })
    result.push(
      {
        title: 'Танилцсан дундаж хувь',
        info: res.averageViewPercentage,
        imageSrc: this.barChartGreen,
        hasDropDown: false,
        navigateLink: null
      })
    result.push(
      {
        title: isOnlineCourse ? 'Цахим сургалттай танилцсан дундаж хугацаа' : 'Урамшуулалтай танилцсан дундаж хугацаа',
        info: res.averageSpentTime,
        imageSrc: this.barChartGreen,
        hasDropDown: false,
        navigateLink: null
      })
    return result;
  }

  private getDateParamsFormatted(params, startDate: string, endDate: string) {
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    return params;
  }

  private mapOnlineCourseToCourseCountData(res: any): PublishedCourseCountData[] {
    const result: PublishedCourseCountData[] = []
    for (let data of res) {
      const courseCount: PublishedCourse[] = [];
      let sum = 0;
      for (let datum of data.courseCountByCategory) {
        courseCount.push({categoryName: datum.categoryName, count: datum.publishedCount, categoryId: datum.categoryId})
        sum = sum + datum.publishedCount;
      }
      courseCount.sort((a, b) => {
        return a.categoryName.localeCompare(b.categoryName);
      })
      result.push({type: data.type, courseCountByCategory: courseCount, totalCount: sum});
    }

    return result;
  }

  private mapPromotionToCourseCountData(res: any): PublishedCourseCountData[] {
    const result: PublishedCourseCountData[] = []
    for (let data of res) {
      let courseCount: PublishedCourse[] = [];
      let sum = 0;
      for (let datum of data.promotionCountByCategories) {
        courseCount.push({categoryName: datum.categoryName, count: datum.publishedPromotionCount, categoryId: datum.categoryId})
        sum = sum + datum.publishedPromotionCount;
      }
      courseCount = this.sortByPromotionCategoryOrder(courseCount);
      result.push({type: data.promotionState, courseCountByCategory: courseCount, totalCount: sum});
    }
    return result;
  }

  private sortByPromotionCategoryOrder(courseCount: PublishedCourse[]): PublishedCourse[] {
    const others: PublishedCourse[] = [];
    const topOnes: PublishedCourse[] = [];
    courseCount.sort((a, b) => {
      return a.categoryName.localeCompare(b.categoryName);
    })
    for (const item of courseCount) {
      if (item.categoryName.toLowerCase() === 'мобайл' || item.categoryName.toLowerCase() === 'юнивишн') {
        topOnes.push(item);
      } else {
        others.push(item);
      }
    }
    return topOnes.concat(others);
  }
}
