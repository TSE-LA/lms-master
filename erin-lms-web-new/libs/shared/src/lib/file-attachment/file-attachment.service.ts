import {Injectable, Type} from "@angular/core";
import {Observable, throwError} from "rxjs";
import {FileAttachment} from "../structures/content-structure/content-structure.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {DialogConfig} from "../dialog/dialog-config";
import {DialogRef} from "../dialog/dialog-ref";
import {DialogService} from "../dialog/dialog.service";
import {SnackbarService} from "../snackbar/snackbar.service";
import {FormUtil} from "../utilities/form.util";

@Injectable({
  providedIn: 'root'
})
export class FileAttachmentService {
  constructor(private httpClient: HttpClient, private dialogService: DialogService, private snackbarService: SnackbarService,) {
  }

  mapToAttachment(content: any, entity: any): FileAttachment {
    return {name: content.name, id: entity.fileId, type: content.type};
  }


  uploadAttachment(file: File): Observable<FileAttachment> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post('lms/courses/course-content/attachment', formData, {headers})
      .pipe(map((res: any) => {
        return this.mapToAttachment(file, res.entity);
      }))
  }

  deleteAttachment(deletedAttId: string, courseId: string): Observable<any> {
    let params = new HttpParams();
    params = params.append('attachmentId', deletedAttId);
    params = params.append('courseId', courseId);
    return this.httpClient.delete('lms/courses/course-content/delete-attachment', {params});
  }

  updateAttachment(courseId: string, attachmentIds: any): Observable<any> {
    const body = {
      courseId: courseId,
      attachmentIds: attachmentIds
    }
    return this.httpClient.put('lms/courses/update-attachments', body)
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  openSnackbar(text: string, success?: boolean) {
    this.snackbarService.open(text, success)
  }
}
