import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {GroupNode} from "../shared-model";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'jrs-drop-down-tree-view',
  template: `
    <div>
    <span *ngIf="label" class="label-style">
      {{label}}
    </span>
      <div class="selector exam-group-style" (click)="toggleDropDown()" [style.width]="width" [class.error-label]="getError()">
        <span class="selected-group">{{selectedGroupName ? selectedGroupName : placeholder}}</span>
        <span *ngIf="!selectedGroupName && required" class="required-star-style">*</span>
        <div class="left-side">
          <jrs-icon class="icon" [class.hide-icon]="load" [class.open]="showDropDown" [mat]="true">{{icon}}</jrs-icon>
          <jrs-loader *ngIf="load"></jrs-loader>
        </div>
      </div>
      <span *ngIf="getError() && errorText" class="error-style" aria-live="polite">&#9888; {{errorText}}</span>
    </div>
    <div *ngIf="showDropDown" class="dropdown-wrapper">
      <div class="dropdown" [ngClass]="size">
        <jrs-tree-view [isDropDown]="true" [nodes]="allGroups" (nodeSelect)="selectNode($event)">
        </jrs-tree-view>
      </div>
    </div>
    <jrs-overlay [show]="showDropDown" [transparent]="true" (clicked)="this.showDropDown = !showDropDown"></jrs-overlay>
  `,
  styleUrls: ['./drop-down-tree-view.component.scss']
})
export class DropDownTreeViewComponent implements OnInit {
  @Input() formGroup = new FormGroup({empty: new FormControl('')});
  @Input() formType = "empty";
  @Input() allGroups: GroupNode[] = [];
  @Input() selectedGroupName: string;
  @Input() placeholder: string;
  @Input() label: string;
  @Input() load: boolean;
  @Input() width = '100%';
  @Input() size = 'medium';
  @Input() required: boolean;
  @Input() errorText: string;
  @Output() selectedNode = new EventEmitter<GroupNode>();
  showDropDown = false;
  icon = 'chevron_right';

  ngOnInit(): void {
    this.formGroup.controls[this.formType].valueChanges.subscribe(value => {
      if (value) {
        this.selectedGroupName = value.name;
      }

    });
  }

  toggleDropDown(): void {
    this.showDropDown = !this.showDropDown;
  }

  selectNode(node: GroupNode): void {
    this.showDropDown = !this.showDropDown;
    this.selectedNode.emit(node);
  }

  getError(): boolean {
    return !this.formGroup.controls[this.formType].pristine && this.formGroup.controls[this.formType].invalid;
  }
}
