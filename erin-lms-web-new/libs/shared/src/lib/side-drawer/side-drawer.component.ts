import { Component, EventEmitter, Input, OnChanges, Output, ViewChild } from "@angular/core";
import { MatDrawer } from "@angular/material/sidenav";

@Component({
  selector: "jrs-side-drawer",
  template: `
    <mat-drawer-container
      [class.drawer-opened]="drawer.opened"
      [class.overlay-style]="drawer.opened"
      class="example-container"
    >
      <mat-drawer #drawer class="drawer" mode="over" [opened]="open">
        <div class="close">
          
          <mat-icon class="close" (click)="close()"> close </mat-icon>
        </div>
        <ng-content></ng-content>
      </mat-drawer>
    </mat-drawer-container>
  `,
  styleUrls: ["./side-drawer.component.scss"],
})
export class SideDrawerComponent implements OnChanges {
  @ViewChild("drawer") public drawer: MatDrawer;
  @Input() open: boolean;
  @Output() closeDrawer = new EventEmitter<boolean>();

  ngOnChanges() {
    if (this.open) {
      this.drawer.toggle();
    }
  }

  close() {
    this.closeDrawer.emit(false);
    this.drawer.close();
  }
}
