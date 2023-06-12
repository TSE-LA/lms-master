import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[jrsInsertion]'
})
export class InsertionDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }
}
