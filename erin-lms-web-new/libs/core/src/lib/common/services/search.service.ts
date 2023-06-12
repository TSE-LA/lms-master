import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {CourseSearchResultModel, TimedCourseSearchResultModel} from "../components/search-result/search-result.model";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {PUBLISH_STATUS} from "../../online-course/models/online-course.constant";

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private httpClient: HttpClient) {
  }

  searchByCourseCategory(categoryId: string, isTimedCourse: boolean, categoryName?: string): Observable<any> {
    let params = new HttpParams();
    if (isTimedCourse == true) {
      params = params.append('courseCategoryId', categoryId);
      return this.httpClient.get('/legacy/courses', {params}).pipe(map((res: any) => {
        return this.mapToTimedCourseResult(this.getSorted(res), categoryName);
      }), catchError((error: any) => {
        return throwError(error);
      }))
    } else {
      params = params.append('categoryId', categoryId);
      return this.httpClient.get('/lms/courses', {params}).pipe(map((res: any) => {
        return this.mapToCourseResult(this.getSorted(res));
      }), catchError((error: any) => {
        return throwError(error);
      }))
    }
  }

  searchCourses(searchValue: string, byName: boolean, byDescription: boolean, isTimedCourse: boolean): Observable<any> {
    if (isTimedCourse == true) {
      return this.httpClient.get(
        '/legacy/courses/search?'
        + 'description=' + byDescription
        + '&name=' + byName
        + '&query=' + encodeURIComponent(searchValue.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'))).pipe(map((res: any) => {
        return this.mapToTimedCourseResult(this.getSorted(res));
      }), catchError((error: any) => {
        return throwError(error);
      }));
    } else {
      return this.httpClient.get(
        '/lms/courses/search?'
        + 'description=' + byDescription
        + '&name=' + byName
        + '&query=' + encodeURIComponent(searchValue.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'))).pipe(map((res: any) => {
        return this.mapToCourseResult(this.getSorted(res));
      }), catchError((error: any) => {
        return throwError(error);
      }));
    }
  }

  private getSorted(res: any): any[] {
    if (res.entity) {
      return res.entity.sort((firstModifiedDate, secondModifiedDate) => {
        const firstDate = new Date(firstModifiedDate.createdDate);
        const secondDate = new Date(secondModifiedDate.createdDate);
        return (secondDate as any) - (firstDate as any);
      });
    }
  }

  private mapToTimedCourseResult(courses: any[], categoryName?: string): TimedCourseSearchResultModel[] {
    const result: TimedCourseSearchResultModel[] = [];
    for (let course of courses) {
      result.push({
        id: course.id,
        title: course.title,
        author: course.authorId,
        code: course.properties.code ? course.properties.code : '',
        keyword: course.properties.keyWord ? course.properties.keyWord : '',
        date: this.getDateForTimedCourseResult(course.properties.startDate, course.properties.endDate),
        category: categoryName != null ? categoryName : course.courseCategory,
        state: course.properties.state ? course.properties.state : '',
        target: course.properties.target ? course.properties.target : '',
        published: course.publishStatus == PUBLISH_STATUS.PUBLISHED,
        enrollmentState: course.enrollmentState ? course.enrollmentState : '',
        createdDate: DateFormatter.toISODateString(course.createdDate)
      })
    }
    return result;
  }

  private getDateForTimedCourseResult(startDate: string, endDate: string): string {
    let result = startDate;
    if (endDate != null) {
      if (endDate.length > 0) {
        return result + ' - ' + endDate;
      } else {
        return result;
      }
    } else {
      return result;
    }
  }

  private mapToCourseResult(courses: any[]): CourseSearchResultModel[] {
    const result: CourseSearchResultModel[] = [];
    for (let course of courses) {
      result.push({
        id: course.id,
        title: course.title,
        author: course.authorId,
        type: course.type,
        date: DateFormatter.toISODateString(course.createdDate),
        category: course.courseCategoryName,
        enrollmentState: course.enrollmentState,
        published: course.publishStatus == "PUBLISHED",
        createdDate: DateFormatter.toISODateString(course.createdDate),
        categoryId: course.courseCategoryId
      })
    }
    return result;
  }
}
