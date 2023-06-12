import * as auth from '../../actions/auth/auth';
import {AuthModel} from '../../../common.model';

export function authReducer(state: AuthModel, action: auth.AuthActions) {
  switch (action.type) {
    case auth.AuthActionTypes.AUTH_SET:
      return action.payload.auth;
    case auth.AuthActionTypes.AUTH_CLEAR:
      return  state = new AuthModel();
    default:
      return state;
  }
}
