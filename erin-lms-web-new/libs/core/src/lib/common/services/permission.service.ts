import {Injectable} from '@angular/core';
import {select, Store} from "@ngrx/store";
import {PermissionItem} from "../common.model";
import {ApplicationState} from "../statemanagement/state/ApplicationState";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private permissions: PermissionItem[] = [];

  constructor(private store: Store<ApplicationState>) {
    this.store.pipe(select(state => {
      if (state.auth) {
        this.permissions = state.auth.permissions;
      }
    })).subscribe();
  }

  getPermissionAccess(permId: string): boolean {
    const permissionItem = this.permissions.find(item => item.id === permId);
    return !!permissionItem;
  }

  filterWithPermission(actions: any[]): any[] {
    const permittedActions: any[] = [];
    for (const action of actions) {
      if (this.getPermissionAccess(action.id)) {
        permittedActions.push(action);
      }
    }
    return permittedActions;
  }
}

