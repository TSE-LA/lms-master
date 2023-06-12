import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  ComponentRef,
  OnDestroy,
  Type,
  ViewChild,
} from "@angular/core";
import { DialogConfig } from "./dialog-config";
import { Subject } from "rxjs";
import { InsertionDirective } from "./insertion.directive";
import { DialogRef } from "./dialog-ref";

@Component({
  selector: "jrs-dialog",
  template: ` <div
      class="jrs-dialog"
      [class.background-none]="!this.config.background"
      [ngStyle]="{ width: config.width, 'min-height': config.minHeight }"
    >
      <div class="dialog-header" *ngIf="config.title">
        <jrs-header-text [margin]="false">
          <div>{{ config.title }}</div>
          <ng-content select="[header]"></ng-content>
        </jrs-header-text>
      </div>
      <div class="dialog-body">
        <ng-template jrsInsertion></ng-template>
      </div>
    </div>
    <jrs-overlay
      [show]="true"
      [blur]="config.blur"
      (clicked)="outsideClick()"
    ></jrs-overlay>`,
  styleUrls: ["./dialog.component.scss"],
})
export class DialogComponent implements OnDestroy, AfterViewInit {
  @ViewChild(InsertionDirective) insertionPoint: InsertionDirective;
  private childComponentRef: ComponentRef<any>;
  public childComponentType: Type<any>;
  private readonly _onClose = new Subject<any>();
  close = this._onClose.asObservable();

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver,
    private cd: ChangeDetectorRef,
    public config: DialogConfig,
    public dialog: DialogRef
  ) {}

  ngAfterViewInit(): void {
    this.loadChildComponent(this.childComponentType);
    this.cd.detectChanges();
  }

  ngOnDestroy(): void {
    if (this.childComponentRef) {
      this.childComponentRef.destroy();
    }
  }

  outsideClick(): void {
    if (this.config.outsideClick) {
      this._onClose.next(true);
    }
  }

  loadChildComponent(componentType: Type<any>): void {
    const componentFactory =
      this.componentFactoryResolver.resolveComponentFactory(componentType);
    const viewContainerRef = this.insertionPoint.viewContainerRef;
    viewContainerRef.clear();
    this.childComponentRef = viewContainerRef.createComponent(componentFactory);
  }
}
