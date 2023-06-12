import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable, Subject, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {ContentModule} from "../../common/common.model";
import {FileType, FileUtil} from "../../../../../shared/src/lib/utilities/file-util";
import {TimedCourseStatisticBundleModel, TimedCourseStatistics, TimedCourseStructure} from "../models/timed-course.model";
import {PublishingModel, TestModel} from "../../online-course/models/online-course.model";
import {RoleValueUtil} from "../../../../../shared/src/lib/utilities/role-value.util";
import {DatePipe} from "@angular/common";
import {FileDownloadUtil} from "../../common/utilities/file-download-util";
import {CategoryItem, GroupNode, SmallDashletModel, TimedCourseModel} from "../../../../../shared/src/lib/shared-model";
import {FileAttachment, StructureModule} from "../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {TimedCourseMapper} from "../models/timed-course.mapper";
import {SnackbarService} from "../../../../../shared/src/lib/snackbar/snackbar.service";

@Injectable({
  providedIn: 'root'
})
export class TimedCourseService {
  private barChartGreen = 'assets/images/bar-chart-green.png';
  private barChartPurple = 'assets/images/bar-chart-purple.png';
  private ENROLLMENT_TO_READERSHIPS_SUCCESS_MSG = 'Урамшууллууд амжилттай унших төлөвт шилжлээ.';
  private ENROLLMENT_TO_READERSHIPS_FAILURE_MSG = 'Урамшууллууд унших төлөвт шилжихэд алдаа гарлаа.';
  private pdfSplitPercentage: Subject<number>;

  constructor(private httpClient: HttpClient, private snackbar: SnackbarService) {
    this.pdfSplitPercentage = new Subject();
  }

  updateTimedCourse(course: TimedCourseModel): Observable<any> {
    return this.httpClient.put('/legacy/courses/fully/' + course.id, TimedCourseMapper.mapToCreateRestModel(course, 'create'));
  }

  createTimedCourse(timedCourse: TimedCourseModel): Observable<any> {
    return this.httpClient.post('/legacy/courses', TimedCourseMapper.mapToCreateRestModel(timedCourse, 'create'))
  }

  getTimedCourseCategories(): Observable<CategoryItem[]> {
    return this.httpClient.get('/legacy/promotion-category').pipe(map((res: any) => {
      const categories: CategoryItem[] = [];
      const topOnes: CategoryItem[] = [];
      const entity = res.entity;
      entity.sort((a, b) => a.categoryName > b.categoryName ? 1 : a.categoryName < b.categoryName ? -1 : 0);
      if (entity.length > 0) {
        for (const item of entity) {
          const category: CategoryItem = {name: item.categoryName, id: item.categoryId, newCount: 0, count: 0};
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

  getTimedCourseData(categoryId: string, categoryName: string, stateId: string, publishStatusId: string): Observable<TimedCourseModel[]> {
    let params = new HttpParams();
    params = params.append('courseCategoryId', categoryId);
    params = params.append('state', stateId);
    if (publishStatusId !== 'all') {
      params = params.append('publishStatus', publishStatusId);
    }
    return this.httpClient.get('/legacy/courses', {params}).pipe(map((res: any) => {
      const sorted = res.entity.sort((firstModifiedDate, secondModifiedDate) => {

        // date checker start here
        const fPublished: number = TimedCourseMapper.getPublishedDate(firstModifiedDate);
        const fModified: number = (firstModifiedDate.modifiedDate !== undefined) ? firstModifiedDate.modifiedDate : 0;

        const sPublished: number = TimedCourseMapper.getPublishedDate(secondModifiedDate);
        const sModified: number = (secondModifiedDate.modifiedDate !== undefined) ? secondModifiedDate.modifiedDate : 0;

        const firstModified = (fPublished > fModified) ? fPublished : fModified;
        const secondModified = (sPublished > sModified) ? sPublished : sModified;
        // date checker end here

        const firstDate = new Date(firstModified);
        const secondDate = new Date(secondModified);
        return (secondDate as any) - (firstDate as any);
      });
      return TimedCourseMapper.mapToTableViewModel(sorted, categoryName);
    }));
  }

  getTimeCourseStatistics(courseId: string, startDate: string, endDate: string, groupId?: string): Observable<TimedCourseStatisticBundleModel> {
    let params = new HttpParams();
    params = params.append('startDate', startDate);
    params = params.append('endDate', endDate);
    if (groupId !== undefined) {
      params = params.append('groupId', groupId);
    }
    return this.httpClient.get('/legacy/lms/promotion-statistics/' + courseId, {params})
      .pipe(map((res: any) => {
        return this.mapToTimedCourseStatistics(res.entity);
      }));
  }

  deleteTimedCourse(id: string): Observable<any> {
    return this.httpClient.delete('/legacy/courses/' + id);
  }

  hideTimedCourse(id: string): Observable<any> {
    return this.httpClient.put('/legacy/courses/hide/' + id, null);
  }

  getTimedCourseContent(timedCourseId: string): Observable<ContentModule[]> {
    return this.httpClient.get('/legacy/course-contents/' + timedCourseId + '/file-attachment/').pipe(map((res: any) => {
      return TimedCourseMapper.mapContent(res.entity)
    }))
  }

  downloadStatistics(promotionId: string, startDate: string, endDate: string, groupId?): Observable<any> {
    let params = new HttpParams();
    const datePipe = new DatePipe('en-US');
    params = params.append('startDate', datePipe.transform(startDate, 'yyyyMMdd'));
    params = params.append('endDate', datePipe.transform(endDate, 'yyyyMMdd'));

    if (groupId !== undefined) {
      params = params.append('groupId', groupId);
    }
    return this.httpClient.get('/legacy/lms/promotion-statistics/excel/' + promotionId, {
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

  getTimedCourseById(id: string): Observable<TimedCourseModel> {
    return this.httpClient.get('/legacy/courses/' + id).pipe(map((res: any) => {
      return TimedCourseMapper.mapToTimedCourseModel(res.entity);
    }))
  }

  getTimedCourseStructure(id: string): Observable<TimedCourseStructure> {
    return this.httpClient.get('/legacy/course-contents/' + id).pipe(map((res: any) => {
      return TimedCourseMapper.mapStructure(res.entity)
    }))
  }

  cloneTimedCourse(id: string, timedCourse: TimedCourseModel) {
    return this.httpClient.post('/legacy/courses/' + id + '/clone', TimedCourseMapper.mapToCreateRestModel(timedCourse, "clone"));
  }

  getPdfSplitPercentage(): Subject<number> {
    return this.pdfSplitPercentage;
  }

  getSection(file: File, name: string, id: string): Observable<StructureModule> {
    let params = new HttpParams();
    params = params.append('courseId', id);
    return this.httpClient.get('/legacy/course-contents/get-pdf-section', {params})
      .pipe(map((response: any) => {
        return TimedCourseMapper.mapToUploadedModel(file, response.entity, name);
      }))
  }

  convertEnrollmentToReadership(startDate: Date, endDate: Date, state: string): Observable<any> {
    const body = {startDate: startDate, endDate: endDate, state: state}
    return this.httpClient.post('/legacy/lms/readerships/enrollment-to-readership', body).pipe(map(res => {
      this.snackbar.open(this.ENROLLMENT_TO_READERSHIPS_SUCCESS_MSG, true);
      return res;
    }), catchError((error: any) => {
      this.snackbar.open(this.ENROLLMENT_TO_READERSHIPS_FAILURE_MSG, false);
      return throwError(error);
    }))
  }

  deleteTimedCourseRelations(groupId: string): Observable<any> {
    return this.httpClient.delete('/legacy/lms/promotion-relations/' + groupId);
  }

  saveStructure(contents: FileAttachment[], additional: FileAttachment[], structure: StructureModule[], id: string): Observable<TimedCourseStructure> {
    const body = TimedCourseMapper.mapToStructureRest(contents, additional, structure, id, true);
    return this.httpClient.post('/legacy/course-contents', body)
      .pipe(map((res: any) => {
        return TimedCourseMapper.mapStructure(res.entity);
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  updateStructure(contents: FileAttachment[], additional: FileAttachment[], structure: StructureModule[], id: string): Observable<TimedCourseStructure> {
    const body = TimedCourseMapper.mapToStructureRest(contents, additional, structure, id, false);
    return this.httpClient.put('/legacy/course-contents', body)
      .pipe(map((res: any) => {
        return TimedCourseMapper.mapStructure(res.entity);
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  uploadFile(content: File, name: string, id: string): Observable<any> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', content);
    if (FileUtil.getFileType(content.type) === FileType.VIDEO) {
      return this.httpClient.post('/legacy/course-contents/upload-video', formData, {headers})
        .pipe(map((res: any) => {
          return TimedCourseMapper.mapToUploadedModel(content, res.entity, name);
        }));
    } else {
      formData.append('courseId', id);
      formData.append('courseType', 'promotion');
      return this.httpClient.post('/legacy/course-contents/split-pdf-to-images', formData, {headers})
        .pipe(map((res: any) => {
          const sse = new EventSource('/lms/courses/course-content/get-pdf-percentage/' + id);
          sse.onmessage = (evt) => {
            this.pdfSplitPercentage.next(evt.data);
            if (evt.data == 100) {
              sse.close();
            }
          };
          res.codecSupported = false;
          return res;
        }), catchError(err => {
          return throwError(err);
        }));
    }
  }

  getTimedCourseTest(id: string) {
    return this.httpClient.get('/legacy/course-assessments/' + id).pipe(map((res: any) => {
      return TimedCourseMapper.mapToTestModel(res.entity);
    }));
  }

  saveCourseTest(id: string, test: TestModel): Observable<TestModel> {
    return this.httpClient.post('/legacy/course-assessments', TimedCourseMapper.getTestBody(id, test)).pipe(map((res: any) => {
      return TimedCourseMapper.mapToTestModel(res.entity);
    }), catchError((error: any) => {
      return throwError(error);
    }));
  }

  updateCourseTest(id: string, test: TestModel): Observable<TestModel> {
    return this.httpClient.put('/legacy/course-assessments', TimedCourseMapper.getTestBody(id, test)).pipe(map((res: any) => {
      return TimedCourseMapper.mapToTestModel(res.entity);
    }), catchError((error: any) => {
      return throwError(error);
    }));
  }

  uploadAttachment(file: File): Observable<FileAttachment> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post('/legacy/course-contents/upload-attachments', formData, {headers})
      .pipe(map((res: any) => {
        return TimedCourseMapper.mapToAttachment(file, res.entity);
      }));
  }


  deleteAttachments(deletedAttId: string, attachmentType: string, courseId: string, deleteAll: boolean): Observable<any> {
    return this.httpClient.delete('/legacy/course-contents/' + courseId + '/'
      + deletedAttId + '/' + attachmentType + '/' + deleteAll + '/delete-additional-files');
  }

  saveEnrollments(addedUsers: string[], addedGroupIds: string[], id: string): Observable<any> {
    const body = {deleteProgress: false, userGroup: {users: addedUsers, groups: addedGroupIds, groupEnroll: true}};
    return this.httpClient.patch('/legacy/courses/' + id + '/enrollment', body)
  }

  publishCourse(promotionId: string, publish: PublishingModel): Observable<any> {
    const mapped = {
      userGroup: {groups: publish.assignedGroups, users: publish.assignedLearners, groupEnroll: true},
      notificationRequestModel: {
        sendEmail: publish.sendEmail,
        sendSms: publish.sendSms,
        memo: publish.memo
      },
      publishDate: publish.publishDate ? publish.publishDate : new Date()
    };
    return this.httpClient.post('/legacy/courses/' + promotionId + '/publish', mapped);
  }

  sendUpdateNotification(previousCode: string, previousName: string, id: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('previousCode', previousCode);
    params = params.append('previousName', previousName);
    params = params.append('promotionId', id);

    return this.httpClient.get('/legacy/course-notifications/send-notification', {params}).pipe(map((res: any) => {
      return res;
    }));
  }

  private mapToTimedCourseStatistics(entity): TimedCourseStatisticBundleModel {
    const statistics: TimedCourseStatistics[] = [];
    const dashlets: SmallDashletModel[] = [];
    let launchCount = 0;
    let inTimeLaunchCount = 0;
    let completionCount = 0;
    for (const statistic of entity.analyticData) {
      if (statistic.status > 0) {
        launchCount++;
      }

      if (statistic.isLate == false) {
        inTimeLaunchCount++;
      }

      if (statistic.status >= 90) {
        completionCount++;
      }

      statistics.push({
        username: statistic.userName,
        groupName: statistic.group,
        feedback: statistic.feedback != null ? statistic.feedback : 'Бөглөөгүй',
        hasFeedback: statistic.feedback != null,
        firstLaunchDate: statistic.initialLaunchDate != null ? statistic.initialLaunchDate : '00:00:00',
        lastLaunchDate: statistic.lastLaunchDate != null ? statistic.lastLaunchDate : '00:00:00',
        totalEnrollment: statistic.totalEnrollment,
        spentTime: statistic.totalTime != null ? statistic.totalTime : '00:00:00',
        role: RoleValueUtil.getRoleDisplayName(statistic.role),
        progress: statistic.status != null ? statistic.status.toFixed(1) : '0',
        score: statistic.score != null ? statistic.score : '0',
        isLate: statistic.isLate
      })
    }

    dashlets.push({
      title: 'Урамшуулалтай танилцсан',
      info: launchCount,
      imageSrc: this.barChartPurple,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: '90% танилцсан',
      info: completionCount,
      imageSrc: this.barChartPurple,
      hasDropDown: false,
      navigateLink: null
    });
    dashlets.push({
      title: 'Хугацаандаа танилцсан',
      info: inTimeLaunchCount,
      imageSrc: this.barChartGreen,
      hasDropDown: false,
      navigateLink: null
    });

    statistics.sort((a, b) => {
      return b.progress - a.progress;
    })

    return {statistics: statistics, dashlets: dashlets};
  }

  deleteReaderships(userId: string): Observable<any> {
    return this.httpClient.delete('/legacy/lms/readerships/' + userId).pipe(map((res: any) => res));
  }

  getTimedCourseEnrollmentGroup(timedCourseId: string): Observable<GroupNode[]> {
    return this.httpClient.get(`/legacy/courses/group-enrollment/${timedCourseId}`).pipe(map((res: any) => {
      const groups = []
      res.entity !== null && groups.push(res.entity);
      return TimedCourseMapper.mapToGroupNode(groups);
    }));
  }
}
