import {authReducer} from './reducers/auth/auth.reducer';
import {notificationReducer} from './reducers/notification/notification.reducer';

export let rootReducer = {
  auth: authReducer,
  notification: notificationReducer
};
