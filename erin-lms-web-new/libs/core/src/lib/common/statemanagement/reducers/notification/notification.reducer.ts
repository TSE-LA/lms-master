import * as notif from "../../actions/notification/notification";
import { NotificationModel } from "../../../common.model";

export function notificationReducer(
  state: NotificationModel,
  action: notif.NotificationActions
): NotificationModel {
  switch (action.type) {
    case notif.NotificationActionTypes.NOTIF_SET:
      return action.payload.notification;
    case notif.NotificationActionTypes.NOTIF_CLEAR:
      return (state = new NotificationModel());
    case notif.NotificationActionTypes.NOTIF_UPDATE:
      if (!state) {
        const newState = new NotificationModel();
        newState.categories.set(
          action.payload.notification.name,
          action.payload.notification.value
        );
        return newState;
      }
      state.categories.set(
        action.payload.notification.name,
        action.payload.notification.value
      );
      return state;
    case notif.NotificationActionTypes.NOTIF_MULTIPLE_UPDATE:
      if (!state) {
        const newState = new NotificationModel();
        newState.categories = action.payload.notification;
        return newState;
      }
      state.categories = new Map([
        ...state.categories.entries(),
        ...action.payload.notification.entries(),
      ]);

      return state;
    default:
      return state;
  }
}
