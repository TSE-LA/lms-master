import {Component, EventEmitter, Input, Output} from '@angular/core';
import {GroupNode} from "../shared-model";

@Component({
  selector: 'jrs-tree-view-checkbox',
  template: `
    <div *ngFor="let node of nodes" class="container">
      <div
        [class.selected]="node === selectedNode"
        class="node">
        <jrs-icon
          *ngIf="node.children.length > 0"
          [class.open]="node.showChildren"
          [mat]="true"
          [size]="'medium'"
          (click)="toggle(node.id)">chevron_right
        </jrs-icon>
        <div *ngIf="node.children.length <= 0" class="space"></div>
        <div class="flex">
          <jrs-checkbox [padding]="false"
                        [color]="'light'"
                        [disabled]="node.children.length < 1"
                        [indeterminate]="node.indeterminate"
                        [check]="node.allChildChecked"
                        (checked)="changeChildState($event, node)"
                        jrsTooltip="{{node.name}}-д хамаарах бүх группүүдийг сонгох">
          </jrs-checkbox>
          <jrs-checkbox [padding]="false"
                        [color]="'light'"
                        [check]="node.checked"
                        (checked)="selfCheck($event, node.id, node.parent)"
                        jrsTooltip="{{node.name}} группийг сонгох">
          </jrs-checkbox>
        </div>
        <div class="checkbox-title-container width-full" (click)="this.selectGroup(node)">
          <div class="title margin-left" jrsTooltip="{{node.name}}">{{node.name}}</div>
          <div class="count">{{getSelectedItemCount(node)}}</div>
        </div>

      </div>
      <div *ngIf="node.showChildren">
        <jrs-tree-view-checkbox
          [nodes]="node.children"
          [selectedNode]="selectedNode"
          [allItems]="allItems"
          [selectedItems]="selectedItems"
          [showSelectedItemCount]="true"
          (groupSelected)="selectGroup($event)"
          (groupChecked)="checkGroup($event)"
          (bulkChecked)="bulkCheck($event)"
          (childStateChanged)="changeParentState($event)">
        </jrs-tree-view-checkbox>
      </div>
    </div>`,
  styleUrls: ['./tree-view-checkbox.component.scss']
})
export class TreeViewCheckboxComponent {
  @Input() nodes: GroupNode[] = [];
  @Input() selectedNode: GroupNode;
  @Input() allItems: any[];
  @Input() selectedItems: string[];
  @Input() showSelectedItemCount: boolean;
  @Output() childStateChanged = new EventEmitter<any>();
  @Output() groupSelected = new EventEmitter<GroupNode>();
  @Output() groupChecked = new EventEmitter<GroupNode>();
  @Output() bulkChecked = new EventEmitter<GroupNode>();
  checkedGroups: GroupNode[] = [];

  getCheckedDescendantsCount(nodes: GroupNode[]): any {
    let checkedChildren = 0;
    let childNumber = 0;
    const checkedGroup = [];
    nodes.forEach(node => {
      if (node.checked) {
        checkedChildren++
        checkedGroup.push(node.id);
      }
      childNumber++
      const {counter, childCount, group} = this.getCheckedDescendantsCount(node.children);
      checkedChildren += counter
      childNumber += childCount;
      checkedGroup.push(...group);
    })
    return {counter: checkedChildren, childCount: childNumber, group: checkedGroup};
  }

  getEnrolledGroupIds(): string[] {
    const enrolledGroupIds = [];
    this.nodes.forEach(node => {
      if (node.checked) {
        enrolledGroupIds.push(node.id);
      }
      const {group} = this.getCheckedDescendantsCount(node.children)
      enrolledGroupIds.push(...group)
    })
    return enrolledGroupIds
  }

  getSelectedItemCount(node: GroupNode): string {
    let count = '';
    if (this.allItems && this.selectedItems) {
      this.nodes.forEach(nd => {
        if (node.id === nd.id) {
          const usersInGroup = this.allItems.filter(item => item.groupId === node.id)
          let counter = 0;
          usersInGroup.forEach(user => {
            if (this.selectedItems.includes(user.name)) {
              counter++;
            }
          })
          if (counter > 0) {
            count = `${counter}`
          }
        }
      })
    }
    return count
  }

  selectGroup(node: GroupNode): void {
    this.groupSelected.emit(node);
  }

  toggle(id: string): void {
    this.nodes.forEach(node => {
      if (node.id == id && node.children.length > 0) {
        node.showChildren = !node.showChildren;
      }
    })
  }

  changeChildState(check: boolean, node: GroupNode): void {
    node.children.forEach(child => {
      child.checked = check;
      this.childStateChanged.emit({parentId: node.parent, check})
      this.changeChildState(check, child)
      node.allChildChecked = check;
      node.indeterminate = false;
      this.bulkChecked.emit(child);
    })
  }

  selfCheck(check: boolean, id: string, parentId: string): void {
    this.nodes.forEach(node => {
      if (node.id === id) {
        node.checked = check;
        this.childStateChanged.emit({parentId, check});
        this.groupChecked.emit(node);
        this.groupSelected.emit(node);
      }
    })
  }

  changeParentState(object: any): void {
    this.nodes.forEach(node => {
      if (node.id === object.parentId || object.parentId === '') {
        const {counter, childCount, group} = this.getCheckedDescendantsCount(node.children);
        this.checkedGroups = group;
        if (node.checked && node.parent === "") {
          this.checkedGroups.unshift(node);
        }
        if (counter === 0) {
          node.indeterminate = false;
          node.allChildChecked = false;
        } else if (counter > 0 && counter < childCount) {
          node.indeterminate = true;
          node.allChildChecked = false
        } else {
          node.indeterminate = false;
          node.allChildChecked = true;
        }
        this.childStateChanged.emit({parentId: node.parent, check: object.check})
      }
    })
  }

  checkGroup(node: GroupNode): void {
    this.groupChecked.emit(node);
  }

  bulkCheck(node: GroupNode): void {
    this.bulkChecked.emit(node);
  }
}
