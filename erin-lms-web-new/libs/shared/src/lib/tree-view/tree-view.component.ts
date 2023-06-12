import {Component, EventEmitter, Input, Output} from '@angular/core';
import {GroupNode} from "../shared-model";

@Component({
  selector: 'jrs-tree-view',
  template: `
    <div *ngIf="!load">
      <div *ngFor="let node of nodes" class="node">
        <div class="info"
             [class.selected]="node === selectedNode"
             [class.dropdown]="isDropDown"
             [class.table]="!isDropDown">
          <jrs-icon
            *ngIf="node.children.length > 0"
            [class.open]="node.showChildren"
            [mat]="true"
            [size]="'medium'"
            class="click"
            (click)="toggle(node.id)">chevron_right
          </jrs-icon>
          <div *ngIf="node.children.length <= 0" class="circle"></div>
          <div class="tree-value" (click)="selectNode(node)" jrsTooltip="{{tooltip? node.name: null}}">
            {{node.name}}
          </div>
          <jrs-menu class="context-button"
                    *ngIf="hasMenu"
                    [contextActions]="contextActions"
                    [noCircle]="true"
                    (selectedAction)="selectAction(node, $event)">
          </jrs-menu>
        </div>
        <div *ngIf="node.showChildren">
          <jrs-tree-view
            [nodes]="node.children"
            [isDropDown]="isDropDown"
            [hasMenu]="hasMenu"
            [contextActions]="contextActions"
            (nodeSelect)="selectNode($event)"
            [selectedNode]="selectedNode"
            (actionSelected)="selectAction($event.node, $event.action)">
          </jrs-tree-view>
        </div>
      </div>
    </div>
    <jrs-skeleton-loader [load]="load" [amount]="10"></jrs-skeleton-loader>
  `,
  styleUrls: ['./tree-view.component.scss']
})
export class TreeViewComponent {
  @Input() nodes: GroupNode[] = [];
  @Input() isDropDown = false;
  @Input() tooltip = false;
  @Input() load: boolean;
  @Input() hasMenu = false;
  @Input() contextActions: any[] = [];
  @Input() selectedNode: GroupNode;
  @Output() nodeSelect = new EventEmitter<GroupNode>();
  @Output() actionSelected = new EventEmitter<any>();

  toggle(id: string): void {
    this.nodes.forEach(node => {
      if (node.id == id && node.children.length > 0) {
        node.showChildren = !node.showChildren;
      }
    })
  }

  selectNode(node: GroupNode): void {
    this.selectedNode = node;
    this.nodeSelect.emit(node);
  }

  selectAction(node: GroupNode, action): void {
    this.actionSelected.emit({node, action});
  }
}
