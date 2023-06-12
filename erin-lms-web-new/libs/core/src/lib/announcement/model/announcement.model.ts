export interface Announcement {
  id: string;
  title: string;
  createdDate?: string;
  modifiedDate?: string;
  author?: string;
  content?: string;
  publishStatus?: PublishStatus;
  viewStatus?: ViewStatus;
  status?: ViewStatus;
  statusIcon?: { iconName: string; iconColor: string };
  departmentIds?: [];
  upcoming?: boolean;
}

export enum PublishStatus {
  UNPUBLISHED = "UNPUBLISHED",
  PUBLISHED = "PUBLISHED",
}

export enum ViewStatus {
  NEW = "NEW",
  VIEWED = "VIEWED",
}

export interface AnnouncementStatistic {
  username: string;
  lastName: string;
  firstName: string;
  departmentName: string;
  viewedDate: string;
}
