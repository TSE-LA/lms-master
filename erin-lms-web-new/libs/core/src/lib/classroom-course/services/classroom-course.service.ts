import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, of, throwError} from "rxjs";
import {catchError, map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {
  ClassroomCourseAttendanceModel,
  ClassroomCourseModel,
  ClassroomCourseUpdateModel,
  CreateClassroomCourseModel,
  CreateClassroomCourseTableModel,
  InstructorDropdownModel
} from "../model/classroom-course.model";
import {CategoryItem} from "../../../../../shared/src/lib/shared-model";
import {FileDownloadUtil} from "../../common/utilities/file-download-util";
import {ClassroomCourseMapper} from "../model/classroom-course.mapper";
import {CourseTypeByRoleUtil} from "../../../../../shared/src/lib/utilities/course-type-by-role.util";


@Injectable({
  providedIn: 'root'
})
export class ClassroomCourseService {

  constructor(private httpClient: HttpClient) {
  }

  CLASSROOM_URL = '/lms/classroom-courses/';

  getCourseById(id: string): Observable<ClassroomCourseModel> {
    return this.httpClient.get('/lms/courses/' + id).pipe(map((res: any) => {
      return ClassroomCourseMapper.mapToClassroomCourseModel(res.entity);
    }));
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

  getClassroomCourseCategories(): Observable<CategoryItem[]> {
    let params = new HttpParams();
    params = params.append('parentCategoryId', 'classroom-course');
    return this.httpClient.get('/lms/course-categories', {params})
      .pipe(map((res: any) => {
        const categories: CategoryItem[] = [];
        const entity = res.entity;
        entity.sort((a, b) => a.categoryName > b.categoryName ? 1 : a.categoryName < b.categoryName ? -1 : 0);
        if (entity.length > 0) {
          for (const item of entity) {
            const category = {categoryName: item.categoryName, categoryId: item.categoryId, newTotal: 0, total: 0};
            categories.push(ClassroomCourseMapper.mapToCategoryModel(category));
          }
        }
        return categories;
      }));
  }

  createClassroomCourse(model: CreateClassroomCourseModel, attachmentMimeType: string): Observable<any> {
    for (const date of model.dateItems) {
      this.httpClient.post('/lms/courses', ClassroomCourseMapper.mapToCreateRestModel(model, date, attachmentMimeType))
        .toPromise().then((res: any) => {
        this.httpClient.post('/lms/courses/course-content/persist-attachment', {
          courseId: res.entity.courseId,
          attachmentId: model.attachedFileId
        }).subscribe();
      });
    }

    return of({});
  }

  getInstructors(): Observable<InstructorDropdownModel[]> {
    return this.httpClient.get('/lms/admins').pipe(map((res: any) => {
      return ClassroomCourseMapper.mapToInstructorsModel(res.entity);
    }));
  }

  uploadAttachment(file: any): Observable<string> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);

    return this.httpClient.post('/lms/courses/course-content/attachment', formData, {headers})
      .pipe(map((res: any) => {
        return res.entity;
      }))
  }

  downloadAttachment(courseId: string, fileName: string, nameToShow: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('fileName', fileName);
    return this.httpClient.get('/lms/courses/' + courseId + '/course-content/download-attachment', {
      responseType: 'blob',
      observe: 'response',
      params
    }).pipe(map(response => {
      FileDownloadUtil.downloadFile(response, nameToShow);
      return response;
    }))
  }

  deleteClassroomCourse(id: string): Observable<any> {
    return this.httpClient.delete('/lms/courses/' + id).pipe(map((res: any) => {
      this.httpClient.delete(this.CLASSROOM_URL + id + '/attendance');
      return res.entity;
    }));
  }

  getClassroomCourseUsers(id: string): Observable<string[]> {
    return this.httpClient.get(this.CLASSROOM_URL + id + '/get-suggested-users').pipe(map((res: any) => {
      return res.entity.users ? res.entity.users : [];
    }))
  }

  updateSuggestedUsers(checkedUsers: string[], id: string): Observable<any> {
    return this.httpClient.post(this.CLASSROOM_URL + id + '/update-suggested-users', checkedUsers);
  }

  getAttendanceByCourseIdAndLearnerId(courseId: string, learnerId: string): Observable<any> {
    return this.httpClient.get(this.CLASSROOM_URL + courseId + '/attendance/' + learnerId).pipe(map((res: any) => {
      return ClassroomCourseMapper.mapToGradeTable(res.entity);
    }));
  }

  updateClassroomCourseState(id: string, state: string, rollback?: boolean, reason?: string): Observable<any> {
    const body = {state: state, rollback: rollback != null && rollback, reason: reason != null ? reason : null};
    return this.httpClient.put(this.CLASSROOM_URL + id + '/update-state', body);
  }


  updateClassroomCourse(classroomCourse: ClassroomCourseUpdateModel): Observable<any> {
    return this.httpClient.put('/lms/courses/' + classroomCourse.id, ClassroomCourseMapper.mapToUpdateModel(classroomCourse));
  }

  updateAttachment(id: string, attachmentId: string): Observable<any> {
    return this.httpClient.post('/lms/courses/course-content/persist-attachment', {
      courseId: id,
      attachmentId: attachmentId
    });
  }

  saveCourseLearners(learners: string[], courseId: string): Observable<any> {
    return this.httpClient.post(this.CLASSROOM_URL + courseId + '/update-suggested-users-confirmation', learners);
  }

  publishCourse(courseId: string): Observable<any> {
    return this.httpClient.put(this.CLASSROOM_URL + courseId + '/confirm', {});
  }

  enrollSuperLearners(departments: string[], newSuperLearners: string[], courseId: string, sendNotification: boolean, type: string): Observable<any> {
    return this.httpClient.put('/lms/courses/' + courseId + '/enrollment-coursetype', {
      departments: departments, learners: newSuperLearners, courseType: type,
      sendNotification
    });
  }

  updateClassroomSurveyStatus(courseId: string, currentUser: string): Observable<any> {
    return this.httpClient.post(this.CLASSROOM_URL + courseId + '/survey-status/' + currentUser, {});
  }

  downloadAttendance(id: string, attendances: ClassroomCourseAttendanceModel[]): Observable<any> {
    return this.httpClient.post(this.CLASSROOM_URL + id + '/attendance/download', ClassroomCourseMapper.mapToRestAttendancesModel(attendances),
      {
        responseType: 'blob',
        observe: 'response'
      })
      .pipe(map(res => {
        return FileDownloadUtil.downloadFile(res);
      }), catchError((error) => {
        return throwError(error);
      }));
  }

  getAttendance(id: string): Observable<ClassroomCourseAttendanceModel[]> {
    return this.httpClient.get(this.CLASSROOM_URL + id + '/attendance').pipe(map((res: any) => {
      return ClassroomCourseMapper.mapToAttendanceModel(res.entity);
    }));
  }

  closeClassroomCourse(courseId: string, attendances: ClassroomCourseAttendanceModel[]): Observable<any> {
    return this.httpClient.put(this.CLASSROOM_URL + courseId + '/close', ClassroomCourseMapper.mapToCloseClassroomModel(attendances)).pipe(map(() => {
    }));
  }

  updateAttendance(id: string, attendances: ClassroomCourseAttendanceModel[]): Observable<ClassroomCourseAttendanceModel[]> {
    return this.httpClient.put(this.CLASSROOM_URL + id + '/attendance', ClassroomCourseMapper.mapToCloseClassroomModel(attendances)).pipe(map((res: any) => {
      return ClassroomCourseMapper.mapToAttendanceModel(res.entity);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  removeUser(courseId: string, username: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('learnerId', username);
    return this.httpClient.delete(this.CLASSROOM_URL + `${courseId}/remove-attendance`, {params}).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      return throwError(err);
    }));
  }

  getClassroomCourses(categoryId: string, type: string, state: string): Observable<CreateClassroomCourseTableModel[]> {
    let params = new HttpParams();
    params = params.append('categoryId', categoryId);

    if (type !== 'all') {
      params = params.append('courseType', type);
    }
    if (state !== 'all') {
      params = params.append('publishState', state);
    }
    return this.httpClient.get('/lms/courses', {params})
      .pipe(map((res: any) => {
        return ClassroomCourseMapper.mapToClassroomCourseActivity(res.entity);
      }));
  }
}
