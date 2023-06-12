import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Notif, NotificationModel} from "../common.model";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class CourseNotificationService {

  constructor(private http: HttpClient) {
  }

  getNotification(): Observable<Map<string, Notif>> {
    return this.http.get('/legacy/course-notifications').pipe(map((res: any) => {
      return this.mapToModel(res.entity);
    }));
  }

  updateNotification(courseId: string): Observable<NotificationModel> {
    return this.http.post('/legacy/course-notifications', courseId).pipe(map((res: any) => {
      return new NotificationModel(this.mapToModel(res.entity));
    }));
  }

  private mapToModel(res: any): Map<string, Notif> {
    const notifList: Map<string, Notif> = new Map();
    const courseNotif = new Notif(res.total, res.newTotal);
    if (res.subCategory && res.subCategory.length > 0) {
      for (const subCategory of res.subCategory) {
        courseNotif.subCategory.set(subCategory.categoryId,
          new Notif(subCategory.total, subCategory.newTotal));
      }
    }
    notifList.set(res.categoryId, courseNotif);
    return notifList;
  }
}
