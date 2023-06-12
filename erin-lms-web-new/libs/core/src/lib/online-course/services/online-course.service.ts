import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable, Subject, throwError} from "rxjs";
import {
  AttachmentModel,
  OnlineCourseModel,
  OnlineCourseStatistic,
  OnlineCourseStatisticBundleModel,
  PublishingModel,
  TestModel
} from "../models/online-course.model";
import {catchError, map} from "rxjs/operators";
import {CourseTypeByRoleUtil} from "../../../../../shared/src/lib/utilities/course-type-by-role.util";
import {FileType, FileUtil} from "../../../../../shared/src/lib/utilities/file-util";
import {TranslateService} from "@ngx-translate/core";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";
import {
  CourseStructure, FileAttachment,
  StructureModule
} from "../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {OnlineCourseMapper} from "../models/online-course.mapper";
import {CategoryItem} from "../../../../../shared/src/lib/shared-model";
import {FileDownloadUtil} from "../../common/utilities/file-download-util";
import {DatePipe} from "@angular/common";
import { GroupUtil } from '../../group-management/model/group-util';

@Injectable({
  providedIn: 'root'
})
export class OnlineCourseService {

  private pdfSplitPercentage = new Subject<number>();
  private serverSentEvent = new Subject<EventSource>();
  private codecConvertingPercentage = new Subject<number>();

  constructor(private httpClient: HttpClient,
    private translate: TranslateService,
    private snackbar: SnackbarService,
    @Inject('constants') private constants,
    @Inject('baseUrl') private baseUrl: string) {
  }

  createOnlineCourse(onlineCourse: OnlineCourseModel): Observable<string> {
    return this.httpClient.post('/lms/courses', OnlineCourseMapper.mapToCreateRestModel(onlineCourse)).pipe(map((res: any) => {
      return res.entity.courseId;
    }));
  }

  getOnlineCourseCategories(): Observable<CategoryItem[]> {
    let params = new HttpParams();
    params = params.append('parentCategoryId', 'online-course');
    return this.httpClient.get('/lms/course-categories', {params})
      .pipe(map((res: any) => {
        let categories: CategoryItem[] = [];
        const entity = res.entity;
        entity.sort((a, b) => a.categoryName > b.categoryName ? 1 : a.categoryName < b.categoryName ? -1 : 0);
        const categoryOrder = this.constants.COURSE_CATEGORY_ORDER;

        if (entity.length > 0) {
          for (const item of entity) {
            categories.push(OnlineCourseMapper.mapToDropdownModel(item));
          }
        }

        if (categoryOrder) {
          categories = categories.sort(function (a, b) {
            return categoryOrder.indexOf(a.name) - categoryOrder.indexOf(b.name);
          })
        }
        return categories;
      }));
  }

  getOnlineCourseStructure(onlineCourseId: any): Observable<CourseStructure> {
    return this.httpClient.get(this.baseUrl + '/lms/courses/' + onlineCourseId + '/course-content').pipe(map((res: any) => {
      return OnlineCourseMapper.mapStructure(res.entity);
    }));
  }

  getAttachment(onlineCourseId: string): Observable<AttachmentModel[]> {
    return this.httpClient.get(this.baseUrl + '/lms/courses/' + onlineCourseId + '/course-content').pipe(map((res: any) => {
      return OnlineCourseMapper.mapAttachment(res.entity);
    }));
  }

  getOnlineCourses(categoryId: string, type: string, publishStatus: string, userRole: string): Observable<OnlineCourseModel[]> {
    let params = new HttpParams();
    params = params.append('categoryId', categoryId);
    if (type !== 'all') {
      params = params.append('courseType', type);
    }
    if (publishStatus !== 'all') {
      params = params.append('publishStatus', publishStatus);
    }
    if (userRole !== undefined) {
      if (userRole !== 'LMS_ADMIN') {
        params = params.append('userRole', CourseTypeByRoleUtil.getRole(userRole));
      }
    }
    return this.httpClient.get('/lms/courses', {params})
      .pipe(map((res: any) => {
        const sorted = res.entity.sort((firstModifiedDate, secondModifiedDate) => {
          const firstDate = new Date(firstModifiedDate.createdDate);
          const secondDate = new Date(secondModifiedDate.createdDate);
          return (secondDate as any) - (firstDate as any);
        });
        return OnlineCourseMapper.mapToOnlineCourses(sorted);
      }));
  }

  getOnlineCourseStatistics(courseId: string, startDate: string, endDate: string, groupId: string): Observable<OnlineCourseStatisticBundleModel> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    if (groupId !== undefined) {
      params = params.append('groupId', groupId);
    }
    return this.httpClient.get(this.baseUrl + '/lms/analytics/statistics/' + courseId, {params})
      .pipe(map((res: any) => {
        return OnlineCourseMapper.mapToOnlineCourseStatistics(res.entity);
      }));
  }


  deleteOnlineCourse(onlineCourseId: string): Observable<any> {
    return this.httpClient.delete('/lms/courses/' + onlineCourseId).pipe(map(() => {
    }), catchError((error: any) => {
      return throwError(error);
    }));
  }

  hideOnlineCourse(courseId: string): Observable<any> {
    return this.httpClient.patch('/lms/courses/' + courseId + '/hide', null);
  }

  getOnlineCourseById(courseId: string): Observable<OnlineCourseModel> {
    return this.httpClient.get(this.baseUrl + '/lms/courses/' + courseId).pipe(map((res: any) => {
      return OnlineCourseMapper.mapToOnlineCourseModel(res);
    }));
  }

  getSuggestedOnlineCourses(categoryId: string, type: string, publishStatus: string, courseCount: string, userRole: string): Observable<OnlineCourseModel[]> {
    let params = new HttpParams();
    params = params.append('categoryId', categoryId);
    if (type !== 'all') {
      params = params.append('courseType', type);
    }
    if (publishStatus !== 'all') {
      params = params.append('publishStatus', publishStatus);
    }
    params = params.append('courseCount', courseCount);
    if (userRole !== undefined) {
      if (userRole !== 'LMS_ADMIN') {
        params = params.append('userRole', OnlineCourseMapper.getRole(userRole))
      }
    }
    return this.httpClient.get(this.baseUrl + '/lms/courses/suggestions', {params})
      .pipe(map((res: any) => {
        const sorted = res.entity.sort((firstModifiedDate, secondModifiedDate) => {
          const firstDate = new Date(firstModifiedDate.modifiedDate);
          const secondDate = new Date(secondModifiedDate.modifiedDate);
          return (secondDate as any) - (firstDate as any);
        });
        return OnlineCourseMapper.mapToOnlineCourses(sorted);
      }));
  }


  updateOnlineCourse(course: OnlineCourseModel, courseId: string, sendNotification): Observable<any> {
    return this.httpClient.put('/lms/courses/' + courseId + '/update-and-notify', OnlineCourseMapper.mapToUpdateRestModel(course, sendNotification));
  }


  updateThumbnail(thumbnailId: string, courseId: string): Observable<any> {
    return this.httpClient.post('/lms/courses/course-content/persist-thumbnail', {
      courseId: courseId,
      attachmentId: thumbnailId
    })
  }

  updateProperty(course: OnlineCourseModel, id: string): Observable<any> {
    return this.httpClient.put('/lms/courses/' + id, OnlineCourseMapper.courseRestMap(course));
  }

  uploadFile(file: File, name: string, courseId: string) {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);
    if (FileUtil.getFileType(file.type) === FileType.VIDEO) {
      return this.httpClient.post('/lms/courses/course-content/video', formData, {headers})
        .pipe(map((res: any) => {
          return OnlineCourseMapper.mapToUploadedModel(file, res.entity, name);
        }));
    } else if (FileUtil.getFileType(file.type) === FileType.IMAGE) {
      return this.httpClient.post('/lms/courses/course-content/image', formData, {headers})
        .pipe(map((res: any) => {
          return OnlineCourseMapper.mapToUploadedModel(file, res.entity, name);
        }));
    } else {
      formData.append('courseId', courseId);
      formData.append('courseType', 'onlineCourse');
      return this.httpClient.post(this.baseUrl + '/lms/courses/course-content/split-pdf-to-images', formData, {headers})
        .pipe(map((res: any) => {
          const sse = new EventSource(this.baseUrl + '/lms/courses/course-content/get-pdf-percentage/' + courseId);
          sse.onmessage = (evt) => {
            this.pdfSplitPercentage.next(evt.data);
            if (evt.data == 100) {
              sse.close();
            }
          };
          res.codecSupported = false;
          return res;
        }), catchError(err => {
          if (!err.ok && err.status === 400) {
            this.translate.get(err.error.message).subscribe(res => this.snackbar.open(res));
          }
          return throwError(err);
        }));
    }
  }

  uploadThumbnail(file: File): Observable<string> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);

    return this.httpClient.post('/lms/courses/course-content/attachment', formData, {headers})
      .pipe(map((res: any) => {
        return res.entity;
      }))
  }

  getPdfSplitPercentage(): Subject<number> {
    this.pdfSplitPercentage.next(0);
    return this.pdfSplitPercentage;
  }

  getSection(content: File, sectionName: string, courseId: string): Observable<StructureModule> {
    let params = new HttpParams();
    params = params.append('courseId', courseId);
    return this.httpClient.get(this.baseUrl + '/lms/courses/course-content/get-pdf-section', {params})
      .pipe(map((response: any) => {
        return OnlineCourseMapper.mapToUploadedModel(content, response.entity, sectionName);
      }))
  }

  getServerSentEvent(): Subject<EventSource> {
    return this.serverSentEvent;
  }

  convertVideo(filePath: string, fileId: string, sectionName: string, file: File): Observable<StructureModule> {
    let params = new HttpParams();
    params = params.append('filePath', filePath);
    params = params.append('fileId', fileId);
    return this.httpClient.get('/lms/courses/course-content/convert', {params}).pipe(map((res: any) => {
      if (res) {
        const sse = new EventSource('/lms/courses/course-content/get-percentage/' + fileId);
        this.serverSentEvent.next(sse);
        sse.onmessage = (evt) => {
          this.codecConvertingPercentage.next(evt.data);
          if (evt.data == 100) {
            sse.close();
          }
        };
      }
      return OnlineCourseMapper.mapToUploadedModel(file, res.entity, sectionName);
    }));
  }

  getCodecConvertingPercentage(): Subject<number> {
    return this.codecConvertingPercentage;
  }

  saveStructure(attachments: FileAttachment[], structure: StructureModule[], courseId: string): Observable<CourseStructure> {
    const body = OnlineCourseMapper.mapStructureToRest(attachments, structure);
    return this.httpClient.post('/lms/courses/' + courseId + '/course-content', body)
      .pipe(map((res: any) => {
        return OnlineCourseMapper.mapStructure(res.entity);
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  updateStructure(attachments: FileAttachment[], currentStructure: StructureModule[], courseId: string): Observable<CourseStructure> {
    const body = OnlineCourseMapper.mapStructureToRest(attachments, currentStructure);
    return this.httpClient.put('/lms/courses/' + courseId + '/course-content', body)
      .pipe(map((res: any) => {
        return OnlineCourseMapper.mapStructure(res.entity);
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }


  getOnlineCourseTest(courseId: string): Observable<TestModel> {
    return this.httpClient.get('/lms/courses/' + courseId + '/assessment').pipe(map((res: any) => {
      return OnlineCourseMapper.mapToTestModel(res.entity);
    }, catchError(error => {
      throw error;
    })));
  }

  saveCourseTest(onlineCourseId: string, tests: TestModel): Observable<TestModel> {
    return this.httpClient.post('/lms/courses/' + onlineCourseId + '/assessment', OnlineCourseMapper.getTestRestBody(tests)).pipe(map((res: any) => {
      return OnlineCourseMapper.mapToTestModel(res.entity);
    }), catchError((error: any) => {
      return throwError(error);
    }));
  }

  updateCourseTest(courseId: string, testModel: TestModel): Observable<TestModel> {
    return this.httpClient.put('/lms/courses/' + courseId + '/assessment',
      OnlineCourseMapper.getTestRestBody(testModel)).pipe(map((res: any) => {
      return OnlineCourseMapper.mapToTestModel(res.entity);
    }), catchError((error: any) => {
      return throwError(error);
    }));
  }

  downloadSurvey(onlineCourseId: string, startDate: string, endDate: string, groupId?: string): Observable<any> {
    let params = new HttpParams();
    const datePipe = new DatePipe('en-US');
    params = params.append('startDate', datePipe.transform(startDate, 'yyyyMMdd'));
    params = params.append('endDate', datePipe.transform(endDate, 'yyyyMMdd'));

    if (groupId !== undefined) {
      params = params.append('departmentId', groupId);
    }
    return this.httpClient.get('/lms/courses/' + onlineCourseId + '/survey/download', {
      responseType: 'blob',
      observe: 'response',
      params
    })
      .pipe(map(res => {
        return FileDownloadUtil.downloadFile(res);
      }), catchError((error) => {
        return throwError(error);
      }));
  }

  downloadStatistics(downloadData: OnlineCourseStatistic[]): Observable<any> {
    return this.httpClient.post('/lms/courses/analytics/download', OnlineCourseMapper.mapToStatisticDownload(downloadData), {
      responseType: 'blob',
      observe: 'response',
    })
      .pipe(map(res => {
        return FileDownloadUtil.downloadFile(res);
      }), catchError((error) => {
        return throwError(error);
      }));
  }

  downloadAttachment(attachmentFolderId: string, attachmentId: string, fileName: string): Observable<any> {
    const url = '/lms/courses/course-content/download-attachment/' + attachmentFolderId;
    const headers = new HttpHeaders().append('Accept', 'application/octet-stream');
    let params = new HttpParams();
    params = params.append('attachmentId', attachmentId);
    params = params.append('fileName', fileName);
    return this.httpClient.get(url, {headers, params, observe: 'response', responseType: 'blob'})
      .pipe(map(res => {
        return FileDownloadUtil.downloadAttachment(res, fileName);
      }), catchError((error) => {
        return throwError(error);
      }));
  }


  downloadSurveyOneRowExcelFile(onlineCourseId: string, startDate: string, endDate: string, groupId: string, userId: string): Observable<any> {
    let params = new HttpParams();
    const datePipe = new DatePipe('en-US');
    params = params.append('startDate', datePipe.transform(startDate, 'yyyyMMdd'));
    params = params.append('endDate', datePipe.transform(endDate, 'yyyyMMdd'));
    if (groupId !== undefined) {
      params = params.append('departmentId', groupId);
    }

    if (userId !== undefined) {
      params = params.append('userId', userId);
    }

    return this.httpClient.get('/lms/courses/' + onlineCourseId + '/survey/download-one-row', {
      responseType: 'blob',
      observe: 'response',
      params
    })
      .pipe(map(res => {
        return FileDownloadUtil.downloadFile(res);
      }), catchError((error) => {
        return throwError(error);
      }));
  }

  publishCourse(courseId: string, publishingModel: PublishingModel): Observable<any> {
    const mapped = {
      assignedDepartments: publishingModel.assignedGroups,
      assignedLearners: publishingModel.assignedLearners,
      sendEmail: publishingModel.sendEmail,
      sendSms: publishingModel.sendSms,
      note: publishingModel.memo,
      publishDate: publishingModel.publishDate ? publishingModel.publishDate : null,
      //    TODO: Remove this field when version is 3.0.0
      autoChildDepartmentEnroll: false
    };
    return this.httpClient.post('/lms/courses/' + courseId + '/publish', mapped);
  }

  saveEnrollment(courseId: string, learners: string[], groups?: string[]): Observable<any> {
    return this.httpClient.put('/lms/courses/' + courseId + '/enrollment', {
      departments: groups ? groups : [], learners: learners,
      sendNotification: true,
      autoChildDepartmentEnroll: false
    });
  }

  deleteCourseRelations(groupId: string): Observable<any> {
    return this.httpClient.delete('/lms/courses/course-relations/' + groupId);
  }

  getCourseEnrollmentGroup(courseId: string): Observable<any> {
    return this.httpClient.get("/lms/courses/group-enrollment/" + courseId).pipe(map((res: any) => {
      const groups = []
      groups.push(res.entity);
      return GroupUtil.mapToGroupNode(groups);
    }));
  }
}
