import * as actions from '../../actions/notification/notification';
import * as notification from './notification.reducer';
import {CourseNotif, NotificationModel} from '../../../common.model';

describe('notification  reducer', () => {
  let testModel: NotificationModel;
  beforeEach(() => {
    const subCategory: Map<string, CourseNotif> = new Map();
    subCategory.set('LookTV', {total: 100, userNewTotal: 78, subCategory: undefined});
    subCategory.set('Мобайл', {total: 10, userNewTotal: 3, subCategory: undefined});
    const notifList: Map<string, CourseNotif> = new Map();
    notifList.set('Урамшуулал', {total: 20, userNewTotal: 2, subCategory});
    testModel = {categories: notifList};
  });

  it('should set new model state', () => {
    const state = notification.notificationReducer(undefined, new actions.SetNotification(testModel));
    expect(state).not.toBeUndefined();
    const resultData = state.categories.get('Урамшуулал');
    expect(resultData.total).toEqual(20);
    expect(resultData.userNewTotal).toEqual(2);
    expect(resultData.subCategory.size).toEqual(2);
  });

  it('should clear state', () => {
    const state = notification.notificationReducer(testModel, new actions.ClearNotification());
    expect(state).not.toBeUndefined();
    expect(state.categories.size).toEqual(0);
  });

  it('should clear state', () => {
    const test: Map<string, CourseNotif> = new Map();
    test.set('Emo', {total: 11, userNewTotal: 0, subCategory: undefined});
    const state = notification.notificationReducer(testModel, new actions.UpdateNotification({name: 'Emo', value: {total: 11, userNewTotal: 0, subCategory: undefined}}));
    expect(state).not.toBeUndefined();
    const resultData = state.categories.get('Emo');
    expect(resultData.total).toEqual(11);
    expect(resultData.userNewTotal).toEqual(0);
  });
});
