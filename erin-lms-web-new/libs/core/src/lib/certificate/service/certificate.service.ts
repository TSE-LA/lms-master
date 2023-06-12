import {Inject, Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {CertificateModel, GenerateCertificateModel, ReceivedCertificateModel} from "../model/certificate.model";
import {catchError, map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  constructor(private httpClient: HttpClient, @Inject('baseUrl') private baseUrl: string) {
  }

  getCertificates(): Observable<CertificateModel[]> {
    return this.httpClient.get('/lms/certificate').pipe(map((res: any) => {
      let sorted = res.entity.sort((firstModifiedDate, secondModifiedDate) => {
        const firstDate = new Date(firstModifiedDate.modifiedDate);
        const secondDate = new Date(secondModifiedDate.modifiedDate);
        return (secondDate as any) - (firstDate as any);
      });
      sorted = sorted.reverse();
      return this.mapToCertificates(sorted);
    }));
  }

  uploadCertificate(file: any): Observable<any> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post(this.baseUrl + '/lms/certificate', formData, {headers})
      .pipe(map((res: any) => {
        return res;
      }), catchError(err => {
        return throwError(err);
      }));
  }

  deleteCertificate(certificateId: string): Observable<any> {
    return this.httpClient.delete(this.baseUrl + '/lms/certificate/' + certificateId).pipe(map(() => {
    }), catchError((error: any) => {
      return throwError(error);
    }));
  }


  getCertificateById(certificateId: string): Observable<CertificateModel> {
    if (certificateId) {
    }
    return this.httpClient.get(this.baseUrl + '/lms/certificate/' + certificateId).pipe(map((res: any) => {
      return res.entity;
    }));
  }

  receiveCertificate(courseId: string, certificateId: string, learnerId: string, documentId: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('learnerId', learnerId);
    params = params.append('courseId', courseId);
    params = params.append('documentId', documentId);
    params = params.append('certificateId', certificateId);
    return this.httpClient.get(this.baseUrl + '/lms/certificate/receive', {params})
      .pipe(map((res: any) => {
        return res;
      }));
  }

  // TODO: Add courseId and certifiedNumber(get from courseDetail) fields on body
  public generateCertificate(certificate: CertificateModel, recipient: string, courseName: string, target: string, fileName: string, courseId?: string):
    Observable<GenerateCertificateModel> {
    const body = {
      'certificateId': certificate.id,
      "certificateName": certificate.name,
      "targetPath": target,
      "recipient": recipient,
      "courseName": courseName,
      "fileName": fileName,
      "courseId": courseId
    }
    return this.httpClient.post(this.baseUrl + '/lms/certificate/generate', body).pipe(map((res: any) => {
      return {
        url: target + '/' + 'fileName',
        documentId: res.entity
      };
    }), catchError(err => {
      return throwError(err);
    }));
  }

  getReceivedCertificates(learnerId: string): Observable<ReceivedCertificateModel[]> {
    return this.httpClient.get(this.baseUrl + '/lms/certificate/' + learnerId + '/received-certificates').pipe(map((res: any) => {
      const receivedCertificates: ReceivedCertificateModel[] = [];
      for (const item of res.entity) {
        receivedCertificates.push({
          learnerId: item.learnerId,
          courseId: item.courseId,
          certificateId: item.certificateId,
          courseName: item.courseName,
          type: item.courseType === 'OnlineCourse' ? 'Цахим сургалт' : 'Танхимын сургалт',
          date: item.date
        });
      }
      return receivedCertificates;
    }))
  }

  mapToCertificates(items: any): CertificateModel[] {
    const certificates: CertificateModel[] = [];
    for (const certificate of items) {
      certificates.push(certificate);
    }
    return certificates;
  }
}
