import {ComponentFactoryResolver, ComponentRef, Injectable, ViewContainerRef} from '@angular/core';
import {SnackbarComponent} from "./snackbar.component";

@Injectable({providedIn: 'root'})
export class SnackbarService {
  parentViewContainerRef: ViewContainerRef;
  snackbarId = 0;
  componentsReferences = Array<ComponentRef<SnackbarComponent>>()
  private snackbars: ComponentRef<SnackbarComponent>[] = [];

  constructor(private componentFactoryResolver: ComponentFactoryResolver) {
  }

  open(text: string, success?: boolean, size?: string): void {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(SnackbarComponent);
    ++this.snackbarId;

    const snackbarComponentRef = this.parentViewContainerRef.createComponent(componentFactory);
    snackbarComponentRef.instance.open(this.snackbarId, text, success, size );
    this.snackbars.push(snackbarComponentRef);
  }

  remove(id: number): void {
    if (this.snackbars.length < 1) {
      return;
    }

    const snackbar = this.snackbars.find(x => x.instance.id === id);
    const vcrIndex: number = this.parentViewContainerRef.indexOf(snackbar.hostView);

    this.parentViewContainerRef.remove(vcrIndex);
  }

  setViewContainerRef(viewContainerRef: ViewContainerRef): void {
    this.parentViewContainerRef = viewContainerRef;
  }
}
