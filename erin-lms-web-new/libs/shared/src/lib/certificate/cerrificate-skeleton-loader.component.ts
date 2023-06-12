import {Component} from '@angular/core';

@Component({
  selector: 'jrs-certificate-skeleton-loader',
  template: `
    <div class="certificate certificate-loader">
      <div class="certificate-image"></div>
      <div class="regular"></div>
      <div class="smaller"></div>
    </div>`,
  styleUrls: ['./certificate.component.scss']
})
export class CerrificateSkeletonLoaderComponent {

}
