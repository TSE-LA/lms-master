import * as actions from '../../actions/auth/auth';
import * as auth from './auth.reducer';
import {AuthModel} from '../../../common.model';

describe('auth reducer', () => {
  let testAuth: AuthModel;
  beforeEach(() => {
    testAuth = {
      token: '1',
      role: 'admin',
      userGroups: 'GROUPA',
      userName: 'M.Flower',
      permissions: [{id: 'app.id', properties: {visible: true, disable: true}}]
    };
  });

  it('should set new model state', () => {
    const state = auth.authReducer(undefined, new actions.SetAuth(testAuth));
    expect(state.userName).toBe('M.Flower');
    expect(state.token).toBe('1');
    expect(state.userName).toBe('M.Flower');
    expect(state.userGroups).toBe('GROUPA');
    expect(state.role).toBe('admin');
    expect(state.permissions).toEqual(testAuth.permissions);
  });

  it('should clear state', () => {
    const state = auth.authReducer(testAuth, new actions.ClearAuth());
    expect(state).not.toBeUndefined();
    expect(state.userName).toBeUndefined();
    expect(state.token).toBeUndefined();
    expect(state.userName).toBeUndefined();
    expect(state.userGroups).toBeUndefined();
    expect(state.permissions).toBeUndefined();
  });
});
