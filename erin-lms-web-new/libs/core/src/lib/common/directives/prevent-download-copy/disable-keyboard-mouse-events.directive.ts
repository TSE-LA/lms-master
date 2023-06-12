import {Directive, HostListener} from "@angular/core";

@Directive({
  selector: '[disableKeyboardMouseEvents]'
})
export class DisableKeyboardMouseEventsDirective {
  @HostListener('document:contextmenu', ['$event'])
  onDocumentRightClick(): boolean {
    return false;
  }

  @HostListener('document:-webkit-touch-callout', ['$event'])
  onLongPress(): boolean {
    return false;
  }

  @HostListener('window:keydown', ['$event'])
  onKeyPress($event: KeyboardEvent): boolean {
    const keyEvents = ($event.ctrlKey || $event.metaKey);
    if (keyEvents) {
      if($event.key === 'p' || $event.key === 's' || $event.key === 'c') {
        return false
      }
    }
    return $event.key !== 'insert';
  }
}
