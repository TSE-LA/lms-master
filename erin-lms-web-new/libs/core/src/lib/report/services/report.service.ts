import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {
  ClassroomCourseReportModel,
  CourseActivityReport,
  CourseActivityReportModel,
  DetailedExamReportTableModel,
  ExamReportMappedModel,
  OnlineCourseAnalytics,
  OnlineCourseReport,
  TimedCourseReport,
  TimedCourseReportModel
} from '../model/report.model';
import {catchError, map} from 'rxjs/operators';
import {FileDownloadUtil} from "../../common/utilities/file-download-util";
import {ClassroomCourseState, ClassroomReportModel} from "../../classroom-course/model/classroom-course.model";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {SecondsToTimeConverterUtil} from "../../util/secondsToTimeConverter.util";
import {CategoryItem, CourseShortModel, SmallDashletModel} from "../../../../../shared/src/lib/shared-model";
import {TimeToPercentageConverter} from "../../util/TimeToPercentageConverter";

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  EXAM_REPORT_ERROR = 'Тайлан харуулахад алдаа гарлаа';
  barChartGreen = 'assets/images/bar-chart-green.png';
  barChartPurple = 'assets/images/bar-chart-purple.png';

  constructor(private httpClient: HttpClient,
    private snackbar: SnackbarService,
    @Inject('baseUrl') private baseUrl: string,
    @Inject('constants') private constants) {
  }

  getTimedCourseReport(startDate: string, endDate: string, categoryName: string, type?: string, groupId?: string): Observable<TimedCourseReportModel> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    if (categoryName !== 'БҮГД') {
      params = params.append('categoryName', categoryName);
    }
    if (type !== 'all') {
      params = params.append('type', type);
    }
    params = this.checkUndefinedParam(groupId, 'groupId', params);
    return this.httpClient.get('/lms/analytics/promotion', {params}).pipe(map((res: any) => {
      const sorted = res.entity.analytics.sort((firstModifiedDate, secondModifiedDate) => {
        const firstDate = new Date(firstModifiedDate.courseCreatedDate);
        const secondDate = new Date(secondModifiedDate.courseCreatedDate);
        return (secondDate as any) - (firstDate as any);
      });
      return this.mapToTimedCourseReportModel(sorted);
    }));
  }

  getOnlineCourseReport(startDate: string, endDate: string, categoryId: string, courseType: string, departmentId: string): Observable<OnlineCourseAnalytics> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    if (categoryId !== 'all') {
      params = this.checkUndefinedParam(categoryId, 'categoryId', params);
    }
    if (courseType !== 'all') {
      params = this.checkUndefinedParam(courseType, 'courseType', params);
    }
    return this.httpClient.get(this.baseUrl + '/lms/analytics/online-course', {params})
      .pipe(map((res: any) => this.mapToOnlineCourseReportModel(res.entity)));
  }

  getClassroomCourseReport(startDate: string, endDate: string, categoryId: string, departmentId: string): Observable<ClassroomCourseReportModel> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    if (categoryId !== 'all') {
      params = this.checkUndefinedParam(categoryId, 'categoryId', params);
    }
    return this.httpClient.get(this.baseUrl + '/lms/courses/report/classroom', {params}).pipe(map((res: any) => {
      const sorted = res.entity.sort((firstModifiedDate, secondModifiedDate) => {
        const firstDate = new Date(firstModifiedDate.courseCreatedDate);
        const secondDate = new Date(secondModifiedDate.courseCreatedDate);
        return (secondDate as any) - (firstDate as any);
      });
      return this.mapToClassroomCourseReportModel(sorted);
    }));
  }

  getClassroomCourseActivityReport(startDate: string, endDate: string, departmentId: string, learnerId: string): Observable<CourseActivityReportModel> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    params = params.append('learnerId', learnerId);
    return this.httpClient.get('/lms/course-activity/classroom', {params}).pipe(map((res: any) => {
      const sorted = res.entity.sort((firstModifiedDate, secondModifiedDate) => {
        const firstDate = new Date(firstModifiedDate.courseCreatedDate);
        const secondDate = new Date(secondModifiedDate.courseCreatedDate);
        return (secondDate as any) - (firstDate as any);
      });
      return this.mapToClassroomActivityReportModel(sorted);
    }));
  }

  getOnlineCourseActivityReport(startDate: string, endDate: string, departmentId: string, learnerId: string): Observable<CourseActivityReportModel> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    params = this.checkUndefinedParam(learnerId, 'learnerId', params);
    params = params.append('categoryId', 'online-course');
    return this.httpClient.get('/lms/analytics/learner', {params}).pipe(map((res: any) => {
      return this.mapToOnlineCourseLearnerAnalytics(res.entity.analytics);
    }));
  }

  getTimedCourseActivityReport(startDate: string, endDate: string, departmentId: string, username: string): Observable<CourseActivityReportModel> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    params = params.append('userName', username);
    return this.httpClient.get('/legacy/lms/course-activity', {params}).pipe(map((res: any) => {
      return this.mapToTimedCourseActivityTable(res.entity);
    }));
  }

  getExamReport(startDate: string, endDate: string, filterItems: ExamReportMappedModel): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('categoryId', filterItems.category);
    params = params.append('status', filterItems.status);
    params = params.append('type', filterItems.type);
    params = params.append('groupId', filterItems.group);
    return this.httpClient.get('/lms/analytics/exam/', {params}).pipe(map((item: any) => {
      return this.mapToExamReport(item.entity.analytics);
    }), catchError((error: any) => {
      if (error.status != 404) {
        this.snackbar.open(this.EXAM_REPORT_ERROR, false);
      }

      return throwError(error);
    }));
  }

  downloadClassroomCourseActivityReport(startDate: string, endDate: string, departmentId: string, userName: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    params = params.append('userName', userName);
    return this.httpClient.get('/lms/course-activity/download-excel-classroom', {
      params,
      responseType: 'blob',
      observe: 'response'
    }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  downloadTimedCourseActivityReport(startDate: string, endDate: string, departmentId: string, userName: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    params = params.append('userName', userName);
    return this.httpClient.get('/legacy/lms/course-activity/download-excel', {
      params,
      responseType: 'blob',
      observe: 'response'
    }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  downloadOnlineCourseActivityReport(report: CourseActivityReport[]): Observable<any> {
    return this.httpClient.post('/lms/course-activity/download-excel', this.mapToCourseReportDtoFromOnlineCourseActivity(report),
      {
        responseType: 'blob',
        observe: 'response'
      }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  checkUndefinedParam(item: string, name: string, params: HttpParams) {
    // TODO: test me
    if (item !== undefined) {
      params = params.append(name, item);
    }
    return params;
  }

  getformattedDateParams(params, startDate: Date, endDate: Date) {
    params = params.append('startDate', DateFormatter.dateFormat(startDate, '.'));
    params = params.append('endDate', DateFormatter.dateFormat(endDate, '.'));
    return params;
  }

  downloadPromoExcelReport(startDate: string, endDate: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);

    return this.httpClient.get(this.baseUrl + '/legacy/lms/promo-report/promo-excel', {
      responseType: 'blob',
      observe: 'response',
      params
    }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(error => {
      return throwError(error);
    }));
  }

  downloadTimedCourseExcelReport(promotionReports: TimedCourseReport[]): Observable<any> {
    return this.httpClient.post(this.baseUrl + '/legacy/lms/promo-report/download-promotion-report', this.mapToCourseReportResult(promotionReports), {
      responseType: 'blob',
      observe: 'response'
    }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  downloadOnlineCourseExcelReport(onlineCourses: OnlineCourseReport[]): Observable<any> {
    return this.httpClient.post(this.baseUrl + '/lms/courses/report/download-online-course-report', this.mapToCourseReportDto(onlineCourses),
      {
        responseType: 'blob',
        observe: 'response'
      }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  downloadAllOnlineCourseExcelReport(startDate: string, endDate: string, categoryId: string, courseType: string, departmentId: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    if (categoryId !== 'all') {
      params = this.checkUndefinedParam(categoryId, 'categoryId', params);
    }
    if (courseType !== 'all') {
      params = this.checkUndefinedParam(courseType, 'courseType', params);
    }
    return this.httpClient.get(this.baseUrl + '/lms/analytics/download-all-online-course-reports', {
      params,
      responseType: 'blob',
      observe: 'response'
    }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  downloadClassroomCourseExcelReport(startDate: string, endDate: string, categoryId: string, departmentId: string, duration: string[]): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    params = params.append('departmentId', departmentId);
    if (categoryId !== 'all') {
      params = this.checkUndefinedParam(categoryId, 'categoryId', params);
    }
    params = params.append('durationList', duration.join(', '));
    return this.httpClient.get(this.baseUrl + '/lms/courses/report/classroom-course-report-excel', {
      params,
      responseType: 'blob',
      observe: 'response'
    }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  timedCourseTotalScore(learnerId: string): Observable<number> {
    return this.httpClient.get(this.baseUrl + '/legacy/lms/employee-analytics/employee-score/' + learnerId,).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  getTotalScore(learnerId: string, courseType: string): Observable<number> {
    return this.httpClient.get(this.baseUrl + '/lms/users/' + learnerId + '/total-score/' + courseType).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  getPromotionCategories(): Observable<CategoryItem[]> {
    return this.httpClient.get(this.baseUrl + '/legacy/promotion-category').pipe(map((res: any) => {
      const categories: CategoryItem[] = [];
      const topOnes: CategoryItem[] = [];
      const entity = res.entity;
      entity.sort((a, b) => a.categoryName > b.categoryName ? 1 : a.categoryName < b.categoryName ? -1 : 0);
      if (entity.length > 0) {
        for (const item of entity) {
          const category = {name: item.categoryName, id: item.categoryId, newTotal: 0, total: 0};
          if (item.categoryName.toLowerCase() === 'мобайл' || item.categoryName.toLowerCase() === 'юнивишн') {
            topOnes.push(category);
          } else {
            categories.push(category);
          }
        }
      }
      return topOnes.concat(categories);
    }));
  }

  getClassroomCourseCategories(): Observable<CategoryItem[]> {
    let params = new HttpParams();
    params = params.append('parentCategoryId', 'classroom-course');
    return this.getCourseCategories(params);
  }

  getOnlineCourseCategories(): Observable<CategoryItem[]> {
    let params = new HttpParams();
    params = params.append('parentCategoryId', 'online-course');
    return this.getCourseCategories(params);
  }

  getCoursesWithSurvey(surveyId: string): Observable<CourseShortModel[]> {
    return this.httpClient.get('/lms/courses/with-survey/' + surveyId).pipe(map((res: any) => {
      const courses: any[] = [];
      for (const item of res.entity) {
        courses.push({id: item.id, name: item.title});
      }
      return courses;
    }));
  }

  private mapToAttendances(entity: any) {
    const courses = [];
    for (const course of entity.entity) {
      courses.push({
        courseId: course.courseId,
        present: course.attendances[0].present,
        grades: course.attendances[0].grades
      });
    }
    return courses;
  }

  private mapToExamReport(data: any[]): DetailedExamReportTableModel[] {
    const result = [];
    for (const item of data) {
      result.push({
        id: item.id,
        title: item.title,
        category: item.categoryName,
        status: this.getStatusByName(item.status),
        duration: item.duration != null ? SecondsToTimeConverterUtil.convertWithoutUnit(item.duration * 60) : '00:00:00',
        questionCount: item.questionCount ? item.questionCount : 0,
        totalRuntime: item.totalRuntime && item.enrollmentCount ? item.totalRuntime + '/' + item.enrollmentCount : 0,
        passedCount: item.passedCount && item.totalRuntime ? item.passedCount + '/' + item.totalRuntime + '(' + (item.passedCount * 100 / item.totalRuntime).toFixed(0) + '%)' : 0,
        averageScore: item.averageScore && item.maxScore ? item.averageScore + "/" + item.maxScore + '(' + (item.averageScore * 100 / item.maxScore).toFixed(0) + '%)' : 0,
        averageSpentTime: item.averageSpentTime != null ? SecondsToTimeConverterUtil.convertWithoutUnit(item.averageSpentTime) : '00:00:00'
      })
    }
    return result;
  }

  private mapToTimedCourseActivityTable(data: any[]): CourseActivityReportModel {
    const result: CourseActivityReport[] = [];
    let status = 0;
    for (const item of data) {
      status += item.reportData.status;
      result.push({
        category: item.reportData.category,
        name: item.courseName,
        testScore: item.reportData.score,
        progress: item.reportData.status,
        certification: item.reportData.certification === '""' ? item.reportData.certification : 'Байхгүй',
        spentTime: item.reportData.spentTime,
        views: item.reportData.views,
        firstViewDate: item.reportData.firstViewDate ? item.reportData.firstViewDate : '00:00:00',
        lastViewDate: item.reportData.lastViewDate ? item.reportData.lastViewDate : '00:00:00',
        feedBack: item.reportData.feedback === 0 ? 'Өгөөгүй' : 'Өгсөн',
        courseType: this.getCourseState(item.reportData.state),
        assessment: item.reportData.survey === 0 ? 'Өгөөгүй' : 'Өгсөн',
        promoCategory: item.categoryName
      })
    }
    const dashlet: SmallDashletModel = {
      title: 'Урамшуулалтай танилцсан дундаж хувь',
      info: data.length > 0 ? (status / data.length).toFixed(0) + '%' : '0%',
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    }
    return {dashlet: dashlet, reports: result};
  }

  private mapToOnlineCourseLearnerAnalytics(data: any[]): CourseActivityReportModel {
    const result: CourseActivityReport[] = [];
    let status = 0;
    for (const item of data) {
      status += item.status;
      result.push({
        name: item.title,
        category: item.category,
        testScore: item.score,
        progress: item.status,
        certification: item.certificate != null ? item.certificate : 'Байхгүй',
        spentTime: item.spentTime,
        spentTimeRatio: {
          name: TimeToPercentageConverter.getPercentage(item.spentTimeRatio),
          toolTip: item.spentTimeRatio,
        },
        views: item.views,
        firstViewDate: item.firstViewDate ? item.firstViewDate : '00:00:00',
        lastViewDate: item.lastViewDate ? item.lastViewDate : '00:00:00',
        courseType: this.getCourseState(item.courseType),
        assessment: item.survey ? 'Өгсөн' : 'Өгөөгүй',
        spentTimeOnTest: item.spentTimeOnTest
      })
    }
    const dashlet: SmallDashletModel = {
      title: 'Цахим сургалттай танилцсан дундаж хувь',
      info: data.length > 0 ? (status / data.length).toFixed(0) + '%' : '0%',
      imageSrc: this.barChartPurple,
      hasDropDown: false,
      navigateLink: null
    }
    return {dashlet: dashlet, reports: result};
  }

  private getCourseCategories(params: HttpParams): Observable<CategoryItem[]> {
    return this.httpClient.get(this.baseUrl + '/lms/course-categories', {params})
      .pipe(map((res: any) => {
        const categories: CategoryItem[] = [];
        const entity = res.entity;
        entity.sort((a, b) => a.categoryName > b.categoryName ? 1 : a.categoryName < b.categoryName ? -1 : 0);
        if (entity.length > 0) {
          for (const item of entity) {
            const category = {name: item.categoryName, id: item.categoryId, newTotal: 0, total: 0};
            categories.push(category);
          }
        }
        return categories;
      }));
  }

  private mapToTimedCourseReportModel(data: any): TimedCourseReportModel {
    const result: TimedCourseReport[] = [];
    let perfectViewCount = 0;
    let totalViewPercentage = 0;
    let testScore = 0;
    let courseWithTest = 0;
    let viewCount = 0;
    const dashlets = [];
    for (const item of data) {
      if (item.status >= 90) {
        perfectViewCount++;
      }
      if (item.score != null) {
        courseWithTest++;
      }
      if (item.status > 0) {
        viewCount++;
        totalViewPercentage += item.status;
      }
      testScore += item.score;
      result.push({
        id: item.id,
        code: item.code,
        name: item.name,
        totalEnrollment: item.totalEnrollment,
        createdDate: item.createdDate,
        date: item.createdDate,
        author: item.author,
        progress: parseFloat(item.status.toFixed(1)),
        views: item.views,
        hasTest: item.hasTest ? 'Тийм' : 'Үгүй',
        questions: item.questionCount,
        score: item.score.toFixed(1),
        feedbackCount: item.feedback
      });
    }
    dashlets.push({
      title: 'Нийтэлсэн урамшуулал',
      info: data.length,
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: '90%-с дээш хувь танилцсан',
      info: perfectViewCount,
      imageSrc: this.barChartPurple,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: 'Танилцсан дундаж',
      info: viewCount > 0 ? (totalViewPercentage / viewCount).toFixed() + '%' : '0%',
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: 'Сорилын дундаж оноо',
      info: courseWithTest > 0 ? (testScore / courseWithTest).toFixed(1) : 0,
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    return {reports: result, dashlets: dashlets};
  }

  private mapToOnlineCourseReportModel(data: any): OnlineCourseAnalytics {
    const result: OnlineCourseAnalytics = {report: [], groupEnrollmentCount: 0, dashlets: []};
    const dashlets = [];
    result.groupEnrollmentCount = data.analyticData['groupEnrollmentCount'];
    let percentageAboveNinety = 0;
    let totalViewers = 0;
    let totalScore = 0;
    let courseWithTest = 0;
    let totalEnrollment = 0;
    let viewerPercentage;
    for (const item of data.analytics) {
      viewerPercentage = ((item.viewersCount * 100) / item.learners.length).toFixed(1);
      if (viewerPercentage > 90) {
        percentageAboveNinety++;
      }
      if (item.maxScore > 0) {
        courseWithTest++;
        totalScore += item.averageScore;
      }
      totalViewers += item.viewersCount;
      totalEnrollment += item.learners.length;
      result.report.push({
        id: item.id,
        name: item.title,
        state: this.getCourseState(item.courseType),
        type: item.categoryName,
        hasCertificate: item.hasCertificate ? 'Тийм' : 'Үгүй',
        enrollmentCount: item.learnersCount,
        repeatedViewersCount: item.repeatedViewersCount,
        totalViewers: item.viewersCount,
        receivedViewers: item.receivedCertificateCount,
        completedViewers: item.completedViewersCount,
        progress: (item.totalProgress + item.learnersCount === 0) ? 0 : parseInt((item.totalProgress / item.learnersCount).toFixed()),
        enrolledGroupIds: [],
        enrolledLearners: item.learners,
        averageSpentTimeOnTest: item.spentTimeOnTest,
        testScore: item.averageScore + "/" + item.maxScore
      });
    }
    dashlets.push({
      title: 'Нийтэлсэн цахим сургалт',
      info: data.analytics.length,
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: '90%-с дээш танилцсан',
      info: percentageAboveNinety,
      imageSrc: this.barChartPurple,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: 'Танилцсан дундаж',
      info: totalEnrollment <= 0 ? '0%' : ((totalViewers * 100) / totalEnrollment).toFixed(0) + '%',
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: 'Сорилын дундаж оноо',
      info: courseWithTest <= 0 ? 0 : (totalScore / courseWithTest).toFixed(1),
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    result.dashlets = dashlets;
    return result;
  }

  private getCourseState(stateId: string) {
    for (const i of this.constants.COURSE_TYPES) {
      if (i.id === stateId) {
        return i.name;
      }
    }
  }

  private mapToClassroomCourseReportModel(data: any): ClassroomCourseReportModel {
    const classroomCourses: ClassroomReportModel[] = [];
    const dashlets: SmallDashletModel[] = [];
    let completedCourses = 0;
    let totalAttendance = 0;
    let totalEnrollment = 0;
    let coursesWithCertificate = 0;
    const durations = [];
    for (const classroomCourse of data) {
      if (classroomCourse.properties.state === ClassroomCourseState.DONE) {
        completedCourses++;
      }
      if (classroomCourse.hasCertificate) {
        coursesWithCertificate++;
      }
      durations.push(this.getDuration(classroomCourse.properties.startTime, classroomCourse.properties.endTime, true))
      totalEnrollment += parseInt(classroomCourse.properties.maxEnrollmentCount);

      if (classroomCourse.properties.enrollmentCount != null) {
        totalAttendance += parseInt(classroomCourse.properties.enrollmentCount) > parseInt(classroomCourse.properties.maxEnrollmentCount) ?
          parseInt(classroomCourse.properties.maxEnrollmentCount) : parseInt(classroomCourse.properties.enrollmentCount);
      }
      classroomCourses.push(this.mapToClassroomReportModel(classroomCourse));
    }

    dashlets.push({
      title: 'Зохион байгуулагдсан',
      info: completedCourses,
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: 'Хамрагдсан идэвх',
      info: totalEnrollment > 0 ? ((totalAttendance * 100) / totalEnrollment).toFixed() + '%' : '0%',
      imageSrc: this.barChartPurple,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: 'Сертификаттай сургалт',
      info: coursesWithCertificate,
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: 'Сургалтын нийт хугацаа',
      info: this.getTotalDurationForClassroomDashlet(durations),
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });
    return {reports: classroomCourses, dashlets: dashlets};
  }

  private mapToClassroomReportModel(entity: any): ClassroomReportModel {
    return {
      id: entity.id,
      categoryId: entity.courseCategoryId,
      name: entity.title,
      typeId: entity.type,
      description: entity.description,
      author: entity.authorId,
      hasCertificate: entity.hasCertificate,
      hasSurvey: entity.hasAssessment,
      createdDate: DateFormatter.toISODateString(entity.createdDate),
      address: entity.properties.address ? entity.properties.address : '',
      instructor: entity.properties.teacher,
      instructorNumber: entity.properties.teacherNumber,
      dateString: DateFormatter.toISODateString(entity.properties.date),
      startTime: entity.properties.startTime,
      endTime: entity.properties.endTime,
      maxEnrollmentCount: Math.floor(entity.properties.maxEnrollmentCount),
      enrollmentCount: entity.properties.enrollmentCount ? Math.floor(entity.properties.enrollmentCount) : 0,
      isClassroomFull: entity.enrollmentCount >= entity.properties.maxEnrollmentCount,
      hasEnrolled: false,
      state: entity.properties.state,
      previousState: entity.properties.previousState,
      groups: entity.belongingDepartmentName,
      assignedDepartments: entity.assignedDepartments,
      assignedLearners: entity.assignedLearners,
      surveyId: entity.assessmentId,
      certificateId: entity.certificateId,
      attachmentName: entity.properties.attachmentName,
      attachmentType: entity.properties.attachmentMimeType,
      reason: entity.properties.reason,
      date: new Date(),
      departmentNames: "",
      categoryName: entity.courseCategoryName,
      duration: this.getDuration(entity.properties.startTime, entity.properties.endTime),
    }
  }

  private mapToClassroomActivityReportModel(data: any): CourseActivityReportModel {
    const result: CourseActivityReport[] = [];
    for (const item of data) {
      result.push({
        name: item.name,
        category: item.courseType,
        courseType: item.courseType,
        attendance: item.present === false ? 'Хамрагдаагүй' : 'Хамрагдсан',
        testScore: item.testScore,
        certification: item.certificate === null ? 'Сертификатгүй' : 'Сертификаттай',
        teacher: item.teacher,
        startTime: item.startTime,
        endTime: item.endTime,
        date: item.date,
      })
    }
    const dashlet: SmallDashletModel = {
      title: 'Танхимын сургалтын тоо',
      info: data.length,
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    }
    return {dashlet: dashlet, reports: result};
  }

  private getDuration(startTime: string, endTime: string, forDashlet?: boolean): any {
    const s = startTime.split(':');
    const e = endTime.split(':');
    const endHour: number = +e[0];
    const endMinute: number = +e[1];
    const startHour: number = +s[0];
    const startMinute: number = +s[1];
    const endDate: Date = new Date(2020, 1, 1, endHour, endMinute, 0);
    const startDate: Date = new Date(2020, 1, 1, startHour, startMinute, 0);
    let diff = (endDate.getTime() - startDate.getTime()) / 1000;
    diff /= 60;
    const duration = Math.abs(Math.round(diff));
    const hours = (duration / 60);
    const remainingHours = Math.floor(hours);
    const minutes = (hours - remainingHours) * 60;
    const remainingMinutes = Math.round(minutes);
    if (forDashlet == true) {
      return {remainingHours: remainingHours, remainingMinutes: remainingMinutes};
    } else {
      return remainingHours + ' : ' + remainingMinutes;
    }
  }

  private getTotalDurationForClassroomDashlet(durations: any[]): string {
    let hours = 0;
    let minutes = 0;
    let hoursInString: string;
    let minutesInString: string;
    for (const duration of durations) {
      hours += duration.remainingHours;
      minutes += duration.remainingMinutes;
    }
    hours += Math.floor(minutes / 60);
    minutes = minutes % 60;
    if (hours.toString(10).length === 1) {
      hoursInString = '0' + hours;
    } else {
      hoursInString = hours.toString(10);
    }
    if (minutes.toString(10).length === 1) {
      minutesInString = '0' + minutes;
    } else {
      minutesInString = minutes.toString(10)
    }
    return hoursInString + ':' + minutesInString;
  }

  private mapToCourseReportDtoFromOnlineCourseActivity(courseActivityReport: CourseActivityReport[]) {
    const mapped = [];
    for (const course of courseActivityReport) {
      mapped.push({
        courseId: null,
        courseName: course.name,
        hasQuiz: true,
        authorId: null,
        courseCreatedDate: null,
        courseProperties: null,
        reportData: {
          score: Math.floor(course.testScore),
          category: course.category,
          state: course.courseType,
          survey: course.assessment,
          status: Math.floor(course.progress),
          certification: course.certification,
          views: Math.floor(course.views),
          spentTime: course.spentTime,
          firstViewDate: course.firstViewDate,
          lastViewDate: course.lastViewDate,
          spentTimeOnTest: course.spentTimeOnTest,
          spentTimeRatio: course.spentTimeRatio.toolTip,
        },
        enrolledLearners: null
      })
    }
    return mapped;
  }

  private mapToCourseReportDto(onlineCourses: OnlineCourseReport[]) {
    const mapped = [];
    for (const course of onlineCourses) {
      mapped.push({
        courseId: course.id,
        courseName: course.name,
        hasQuiz: true,
        authorId: course.author,
        courseCreatedDate: null,
        courseProperties: null,
        reportData: {
          category: course.type,
          state: course.state,
          enrollmentCount: course.enrollmentCount,
          viewersCount: course.totalViewers,
          status: course.progress,
          completedViewers: course.completedViewers,
          hasCertificate: course.hasCertificate == 'Тийм',
          receivedCertificateCount: course.receivedViewers,
          repeatedViewersCount: course.repeatedViewersCount,
          score: course.testScore,
          spentTime: course.averageSpentTimeOnTest,
        },
        enrolledLearners: null
      })
    }
    return mapped;
  }

  private mapToCourseReportResult(timedCourseReports: TimedCourseReport[]) {
    const mapped = []
    for (const timedCourseReport of timedCourseReports) {
      mapped.push({
        courseId: timedCourseReport.id,
        courseName: timedCourseReport.name,
        authorName: timedCourseReport.author,
        categoryName: timedCourseReport.category,
        courseCreatedDate: timedCourseReport.date,
        courseProperties: {
          code: timedCourseReport.code,
          keyword: null,
          startDate: null,
          endDate: null,
          type: null,
          state: timedCourseReport.state,
          modifierUserID: null,
          target: null,
          hasTest: timedCourseReport.hasTest == 'Тийм',
          createdDate: timedCourseReport.date
        },
        reportData: {
          status: timedCourseReport.progress.toString(),
          title: timedCourseReport.name,
          views: timedCourseReport.views,
          questionsCount: timedCourseReport.questions,
          score: timedCourseReport.score,
          totalEnrollment: timedCourseReport.totalEnrollment,
          feedback: timedCourseReport.feedbackCount,
          lastViewDate: null,
          firstViewDate: null,
          spentTime: null,
          category: timedCourseReport.category
        },
        enrolledLearners: null
      })
    }
    return mapped;
  }

  getStatusByName(status: string): string {
    switch (status) {
      case 'NEW':
        return 'Шинэ';
      case 'PUBLISHED':
        return 'Нийтлэгдсэн';
      case 'STARTED':
        return 'Эхэлсэн';
      case 'FINISHED':
        return 'Дууссан';
      case 'PENDING':
        return 'Хүлээгдэж буй';
    }

  }
}
