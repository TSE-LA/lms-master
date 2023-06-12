import { ComponentFactoryResolver, ComponentRef, Injectable, Injector, Type, ViewContainerRef} from '@angular/core';
import {DialogComponent} from "./dialog.component";
import {DialogConfig} from "./dialog-config";
import {DialogInjector} from "./dialog-injector";
import {DialogRef} from "./dialog-ref";

@Injectable({
  providedIn: 'root'
})
export class DialogService {
  dialogId = 0;
  private dialogComponentRef: ComponentRef<DialogComponent>;
  private parentViewContainerRef: ViewContainerRef;

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver,
    private injector: Injector) {
  }

  open(componentType: Type<any>, config: DialogConfig): DialogRef {
    const dialogRef = this.appendDialogToBody(config);
    this.dialogComponentRef.instance.childComponentType = componentType;
    return dialogRef;
  }

  setViewContainerRef(viewContainerRef: ViewContainerRef): void {
    this.parentViewContainerRef = viewContainerRef;
  }

  private appendDialogToBody(config: DialogConfig): DialogRef {
    const map = new WeakMap();
    map.set(DialogConfig, config)

    const dialogRef = new DialogRef();
    map.set(DialogRef, dialogRef);

    const sub = dialogRef.afterClosed.subscribe(() => {
      this.removeDialogFromBody();
      sub.unsubscribe();
    });

    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(DialogComponent);
    // ++this.dialogId

    this.dialogComponentRef  = this.parentViewContainerRef.createComponent(componentFactory, this.dialogId, new DialogInjector(this.injector, map));

    return dialogRef;
  }

  private removeDialogFromBody(): void {
    if (this.parentViewContainerRef.length < 1) {
      return;
    }

    const vcrIndex: number = this.parentViewContainerRef.indexOf(this.dialogComponentRef.hostView);

    this.parentViewContainerRef.remove(vcrIndex);
  }
}
