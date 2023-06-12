import { type } from "../../util/util";
import { Action } from "@ngrx/store";
import { Notif, NotificationModel } from "../../../common.model";

export const NotificationActionTypes = {
  NOTIF_SET: type<"NOTIF_SET">("NOTIF_SET"),
  NOTIF_CLEAR: type<"NOTIF_CLEAR">("NOTIF_CLEAR"),
  NOTIF_UPDATE: type<"NOTIF_UPDATE">("NOTIF_UPDATE"),
  NOTIF_MULTIPLE_UPDATE: type<"NOTIF_MULTIPLE_UPDATE">("NOTIF_MULTIPLE_UPDATE"),
};

export class ClearNotification implements Action {
  type = NotificationActionTypes.NOTIF_CLEAR;
}

export class SetNotification implements Action {
  type = NotificationActionTypes.NOTIF_SET;
  payload: Readonly<{ notification: NotificationModel }>;

  constructor(notification: NotificationModel) {
    this.payload = { notification };
  }
}

export class UpdateNotification implements Action {
  type = NotificationActionTypes.NOTIF_UPDATE;
  payload: Readonly<{ notification: { name: string; value: Notif } }>;

  constructor(notification: { name: string; value: Notif }) {
    this.payload = { notification };
  }
}
export class AddMultipleNotifications implements Action {
  type = NotificationActionTypes.NOTIF_MULTIPLE_UPDATE;
  payload: Readonly<{ notification: Map<string, Notif> }>;

  constructor(notification: Map<string, Notif>) {
    this.payload = { notification };
  }
}

export type NotificationActions =
  | ClearNotification
  | SetNotification
  | UpdateNotification
  | AddMultipleNotifications;
