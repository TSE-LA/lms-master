import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class HistoryService {
  constructor(private httpClient: HttpClient) {
  }

  public getHistory(name: string, year: string): Observable<any> {
    let params = new HttpParams()
    params = params.set("year", year);
    params = params.set("username", name);
    return this.httpClient.get('/lms/course-history/get-learner-history-by-year', {params}).pipe(map((res: any) => {
      return res.entity;
    }), catchError(error => {
      return throwError(error);
    }));
  }

  public getHistoryCredits(name: string, year: string): Observable<any> {
    let params = new HttpParams()
    params = params.set("year", year);
    params = params.set("username", name);
    return this.httpClient.get('/lms/course-history/get-learner-credit-by-year', {params}).pipe(map((res: any) => {
      return res.entity;
    }), catchError(error => {
      return throwError(error);
    }));
  }
}
