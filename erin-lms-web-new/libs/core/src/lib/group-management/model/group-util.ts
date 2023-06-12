import {GroupNode} from "../../../../../shared/src/lib/shared-model";

export class GroupUtil{

  public static flatten(res: GroupNode[], flatData: GroupNode[]) {
    if (res == null || res.length === 0) {
      return;
    }
    for (const node of res) {
      flatData.push(node);
      this.flatten(node.children, flatData);
    }
  }

  public static pushNode(nodes: any[], node): void {
    const children: GroupNode[] = [];
    if (node.children && node.children.length > 0) {
      for (const child of node.children) {
        this.pushNode(children, child);
      }
    }
    nodes.push({
      parent: node.parentId,
      id: node.id,
      name: node.name,
      nthSibling: node.nthSibling,
      children
    });
  }

  public static getNode(root: GroupNode, id: string): GroupNode {
    if (root != null && root.id === id) {
      return root;
    }
    return GroupUtil.findNode(id, root != null ? root.children : []);

  }

  public static setPath(nodes: GroupNode[], parentPath: string): void {
    if (nodes === undefined) {
      return;
    }
    for (const node of nodes) {
      node.path = parentPath + '/' + node.name;
      this.setPath(node.children, node.path);
    }
  }

  public static  findNode(id: string, nodes: GroupNode[]): GroupNode {
    if (nodes.length === 0) {
      return null;
    }

    let result: GroupNode;
    for (const node of nodes) {
      if (id === node.id) {
        return node;
      }

      result = this.findNode(id, node.children);

      if (result != null) {
        return result;
      }
    }

    return result;
  }

  public static  mapToGroupNode(entity: any[]): GroupNode[] {
    const groupNode = [];
    entity.forEach(group => {
      groupNode.push({
        parent: group.parent,
        id: group.id,
        name: group.name,
        children: this.mapToGroupNode(group.children),
        indeterminate: group.someChildrenSelected,
        checked: group.currentGroupSelected,
        allChildChecked: group.allChildrenSelected,
      })
    })
    return groupNode;
  }
}
