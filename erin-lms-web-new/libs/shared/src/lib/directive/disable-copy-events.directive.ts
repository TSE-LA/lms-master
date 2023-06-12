import {Directive, HostListener} from '@angular/core';

@Directive({
  selector: '[jrsDisableCopyEvents]'
})
export class DisableCopyEventsDirective {

  @HostListener('document:contextmenu', ['$event'])
  onDocumentRightClick():boolean {
    return false;
  }

  @HostListener('window:keydown', ['$event'])
  onKeyPress($event: KeyboardEvent): boolean {
    const keyEvents = ($event.ctrlKey || $event.metaKey);
    if (keyEvents){
      if ( $event.key === 'p') {
        return false;
      }
      if ($event.key === 's') {
        return false;
      }

      if ( $event.key === 'c') {
        return false;
      }
    }

    if ($event.key === 'insert') {
      return false;
    }

    return true;
  }
}
