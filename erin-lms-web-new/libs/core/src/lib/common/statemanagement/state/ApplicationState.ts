import {AuthModel, NotificationModel} from '../../common.model';

export type ApplicationState = Readonly<{
  auth: AuthModel;
  notification: NotificationModel;
}>;
