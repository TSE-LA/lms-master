import {Inject, Injectable} from "@angular/core";
import {Observable, Subject, throwError} from "rxjs";
import {RuntimeDataModel, ScoModel} from "../model/runtime-data.model";
import {HttpClient} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class ScormRuntimeService {
  private saveDataSubject = new Subject();
  private notificationSubject = new Subject();

  constructor(private httpClient: HttpClient) {
  }

  saveCourseRuntimeData(courseId: string, data: ScoModel): Observable<any> {
    return this.httpClient.put('/lms/courses/' + courseId + '/progress',
      this.mapToUpdateOnlineCourseRuntimeData(data))
      .pipe(map((res: any) => {

        return this.mapToRuntimeDataModel(res.entity);
      }));
  }

  saveRuntimeData(scormContentId: string, data: ScoModel): Observable<any> {
    return this.httpClient.put('/legacy/scorm-contents/' + scormContentId + '/runtime-data', this.mapToUpdateRuntimeData(data)).pipe(map((res: any) => {
      return this.mapToRuntimeDataModel(res.entity);
    }), catchError(error => {
      return throwError(error);
    }))
  }

  getCourseScoData(courseId: string) {
    return this.httpClient.get('/lms/courses/' + courseId + '/launch')
      .pipe(map((res: any) => {
        const items = res.entity;
        const scos: ScoModel[] = [];
        for (const item of items) {
          scos.push(this.mapToScoModel(item));
        }
        scos.sort((a, b) => a.index > b.index ? 1 : a.index < b.index ? -1 : 0);
        return scos;
      }, catchError(err => throwError(err))));
  }

  getScoData(id: string): Observable<ScoModel[]> {
    return this.httpClient.get('/legacy/courses/' + id + '/scorm-content/').pipe(map((res: any) => {
      const items = res.entity;
      const scos: ScoModel[] = [];
      for (const item of items) {
        scos.push(this.mapToScoModel(item));
      }
      return scos;
    }))
  }

  onSaveDataCalled(): Observable<any> {
    return this.saveDataSubject.asObservable();
  }

  notifyDataSent(): void {
    this.notificationSubject.next();
  }

  mapToUpdateOnlineCourseRuntimeData(data: ScoModel): any {
    return {
      data: this.mapRuntimeData(data.runtimeData),
      moduleName: data.scoName
    };
  }

  mapToScoModel(data: any): ScoModel {
    const tempIndex = data.path.slice(data.path.indexOf('content=sco'), data.path.length);
    const index = tempIndex.slice(11, tempIndex.length);
    const indexNumber: number = +index;
    return {
      scoName: data.name,
      path: data.path,
      runtimeData: this.mapToRuntimeDataModel(data.runtimeData),
      isTest: data.name.toLowerCase() === 'тест',
      isQuestionnaire: data.name.toLowerCase() === 'асуулга',
      isSurvey: data.name.toLowerCase() === 'үнэлгээний хуудас',
      index: indexNumber === undefined ? 0 : indexNumber
    };
  }

  private mapRuntimeData(runtimeData: Map<string, RuntimeDataModel>): any {
    const result = {};

    runtimeData.forEach((value: RuntimeDataModel, key: string) => {
      result[key] = value.data;
    });

    return result;
  }

  private mapToRuntimeDataModel(runtimeData: any): Map<string, RuntimeDataModel> {
    const result = new Map<string, RuntimeDataModel>();

    Object.keys(runtimeData).forEach(key => {
      result.set(key, runtimeData[key]);
    });

    return result;
  }

  private mapToUpdateRuntimeData(data: ScoModel): any {
    return {
      data: this.mapRuntimeData(data.runtimeData),
      scoName: data.scoName
    }
  }
}
