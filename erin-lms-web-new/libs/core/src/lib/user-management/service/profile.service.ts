import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {UserProfile} from "../models/profile.model";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  constructor(private httpClient: HttpClient) {
  }

  public getUserProfile(userName: string): Observable<any> {
    return this.httpClient.get('/aim/users/profile?username=' + userName).pipe(map((res: any) => {
      return res.entity;
    }), catchError(error => {
      return throwError(error);
    }))
  }

  getFields(organizationId: string): Observable<any> {
    return this.httpClient.get('/lms/field/get-fields?organizationId=' + organizationId)
      .pipe(map((res: any) => res.entity));
  }

  public updateUserProfile(profile: UserProfile): Observable<any> {
    return this.httpClient.put('/aim/users/profile', profile).pipe(map((res: any) => {
      return res.entity;
    }), catchError((error: any) => {
      return throwError(error)
    }))
  }

  uploadAvatar(file: File): Observable<any> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('multipartFile', file);

    return this.httpClient.post('/aim/users/image', formData, {headers})
      .pipe(map((res: any) => {
        return res.entity;
      }))
  }

  getAvatarUrl(userName:string): Observable<string> {
    return this.httpClient.get("/aim/users/get-image-url?username="+userName).pipe(map((res: any) => {
      return res.entity;
    }), catchError((error: any) => {
      return throwError(error)
    }));
  }
}
