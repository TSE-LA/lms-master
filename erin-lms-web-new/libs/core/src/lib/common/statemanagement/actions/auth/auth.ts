import {type} from '../../util/util';
import {Action} from '@ngrx/store';
import {AuthModel} from '../../../common.model';


export const AuthActionTypes = {
  AUTH_SET: type<'AUTH_SET'>('AUTH_SET'),
  AUTH_CLEAR: type<'AUTH_CLEAR'>('AUTH_CLEAR'),
};


export class ClearAuth implements Action {
  type = AuthActionTypes.AUTH_CLEAR;
}


export class SetAuth implements Action {
  type = AuthActionTypes.AUTH_SET;
  payload: Readonly<{ auth: AuthModel }>;

  constructor(auth: AuthModel) {
    this.payload = {auth};
  }
}

export type AuthActions = ClearAuth | SetAuth;


