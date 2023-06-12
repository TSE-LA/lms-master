import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class SystemSettingsService {

  constructor(private http: HttpClient) {
  }

  getSystemImageUrl(isLogo: boolean): Observable<string> {
    let params = new HttpParams();
    params = params.set("isLogo", isLogo);
    return this.http.get(`/lms/system-configs/system-image-url`, {params})
      .pipe(map(response => response['entity']));
  }

  uploadImage(file: File, isLogo: boolean): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('isLogo', String(isLogo));
    const headers = new HttpHeaders();
    headers.append('Content-Type', undefined);
    return this.http.post('/lms/system-configs/system-image', formData, {headers});
  }


}
