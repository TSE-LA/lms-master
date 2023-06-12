import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import {
  Announcement,
  AnnouncementStatistic,
  PublishStatus,
  ViewStatus,
} from "./model/announcement.model";
import { GroupNode } from "../../../../shared/src/lib/shared-model";
import { GroupUtil } from "../group-management/model/group-util";
import { Notif } from "../common/common.model";

@Injectable({
  providedIn: "root",
})
export class AnnouncementService {
  constructor(private http: HttpClient) {}

  createAnnouncement(
    currentUser: string,
    announcement: Announcement
  ): Observable<string> {
    const body = {
      announcementId: "",
      author: currentUser,
      departmentIds: announcement.departmentIds,
      modifiedUser: currentUser,
      content: announcement.content,
      title: announcement.title,
    };
    return this.http.post("/lms/announcements", body).pipe(
      map((res: any) => {
        return res.entity;
      })
    );
  }

  getAnnouncementById(id: string): Observable<Announcement> {
    return this.http.get("/lms/announcements/" + id).pipe(
      map((res: any) => {
        const announcement = res.entity;
        return this.mapToAnnouncement(announcement);
      })
    );
  }

  getAllAnnouncements(
    startDate: string,
    endDate: string
  ): Observable<Announcement[]> {
    let params = new HttpParams();
    params = params.append("startDate", startDate);
    params = params.append("endDate", endDate);
    return this.http.get("/lms/announcements", { params }).pipe(
      map((res: any) => {
        return this.mapToAnnouncements(res.entity);
      })
    );
  }

  getAnnouncements(
    startDate: string,
    endDate: string
  ): Observable<Announcement[]> {
    let params = new HttpParams();
    params = params.append("startDate", startDate);
    params = params.append("endDate", endDate);
    return this.http.get("/lms/announcements/learner", { params }).pipe(
      map((res: any) => {
        return this.mapToAnnouncements(res.entity);
      })
    );
  }

  getStatusIcon(publishStatus: PublishStatus, viewStatus: ViewStatus) {
    let iconName;
    let iconColor;
    if (publishStatus == PublishStatus.PUBLISHED) {
      iconName = viewStatus == ViewStatus.NEW ? "fiber_manual_record" : "";
      iconColor = "secondary";
    } else {
      iconName = "visibility_off";
      iconColor = "gray";
    }
    return { iconName, iconColor };
  }

  updateAnnouncement(announcement: Announcement): Observable<boolean> {
    const body = {
      ...announcement,
      announcementId: announcement.id,
    };
    return this.http.put("/lms/announcements", body).pipe(
      map((res: any) => {
        return res.created;
      })
    );
  }

  publishAnnouncement(id: string): Observable<boolean> {
    return this.http.post(`/lms/announcements/${id}/publish`, {}).pipe(
      map((res: any) => {
        return res.published;
      })
    );
  }

  getAnnouncementNotification(): Observable<Notif> {
    return this.http.get("/lms/announcements/notification").pipe(
      map((res: any) => {
        return new Notif(0, res.entity);
      })
    );
  }

  deleteAnnouncement(id: string): Observable<any> {
    return this.http.delete("/lms/announcements/" + id).pipe(
      map((res: any) => {
        return res.entity;
      })
    );
  }

  getStatistics(
    id: string
  ): Observable<{ totalView: number; statistics: AnnouncementStatistic[] }> {
    return this.http.get("/lms/analytics/announcement/" + id).pipe(
      map((res: any) => {
        const entity = res.entity;
        return { totalView: entity.totalView, statistics: entity.userInfo };
      })
    );
  }

  viewAnnouncement(id: string): Observable<boolean> {
    return this.http.post("/lms/announcements/" + id + "/view", {}).pipe(
      map((res: any) => {
        return res.entity;
      })
    );
  }

  getAnnouncementEnrolledGroups(id: string): Observable<any> {
    return this.http.get("/lms/announcements/group-announcement/" + id).pipe(
      map((res: any) => {
        const groups: GroupNode[] = [];
        groups.push(res.entity);
        return GroupUtil.mapToGroupNode(groups);
      })
    );
  }

  private mapToAnnouncements(results: any): Announcement[] {
    return results.map((result: any) => {
      return this.mapToAnnouncement(result);
    });
  }

  private mapToAnnouncement(result: any): Announcement {
    const publishStatus =
      PublishStatus[result.publishStatus as keyof typeof PublishStatus];
    const viewStatus = ViewStatus[result.viewStatus as keyof typeof ViewStatus];
    return {
      id: result.announcementId,
      title: result.title,
      modifiedDate: result.modifiedDate,
      createdDate: result.createdDate,
      author: result.author,
      publishStatus,
      content: result.content,
      viewStatus,
      statusIcon: this.getStatusIcon(publishStatus, viewStatus),
      upcoming: viewStatus == ViewStatus.NEW,
    };
  }
}
