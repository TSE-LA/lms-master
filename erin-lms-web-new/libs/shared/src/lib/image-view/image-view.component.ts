import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-image-view',
  template: `
    <img [src]="src" alt="profile_picture" class="image" (error)="onError()">
  `,
  styleUrls: ['./image-view.component.scss']
})
export class ImageViewComponent {
  @Input() src = "assets/images/avatar-placeholder.png";
  defaultImage = "assets/images/avatar-placeholder.png";

  onError() {
    if (this.src != this.defaultImage) {
      this.src = this.defaultImage;
    }
  }
}
