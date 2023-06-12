import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-image-viewer',
  template: `
    <div>
      <img
        class="margin-top-medium"
        src="{{imageSrc}}"
        [alt]="'Зураг олдсонгүй'"
        [ngStyle]="{width: '100%'}"
        (load)="hasFinishedLoading()">
    </div>
    <div class="loader" *ngIf="load"></div>
  `,
  styleUrls: ['./image-viewer.component.scss']
})
export class ImageViewerComponent {
  @Input() imageSrc: string;
  @Input() load = true;

  hasFinishedLoading() {
    this.load = false;
  }
}
