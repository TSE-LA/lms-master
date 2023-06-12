import {GroupNode} from "../../../../shared/src/lib/shared-model";

export class GroupUtil {
  public static findGroup(groups: GroupNode[], searchId: string): GroupNode {
    let result = null;
    groups.forEach(group => {
      if (group.id === searchId) {
        result = group
      }
      if (!result) {
        result = this.findGroup(group.children, searchId);
      }
    })
    return result;
  }

  
}
