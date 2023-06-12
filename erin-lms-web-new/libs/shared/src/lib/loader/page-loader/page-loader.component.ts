import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-page-loader',
  template: `
    <jrs-overlay [show]="show" [transparent]="false" [zIndex]="'1000'">
      <div class="container">
        <div class="wrapper">
          <div class="loader"></div>
          <span class="text">{{text}}</span>
        </div>
      </div>
    </jrs-overlay>
  `,
  styleUrls: ['./page-loader.component.scss']
})
export class PageLoaderComponent {
  @Input() show: boolean;
  @Input() text = "Түр хүлээнэ үү ...";
}
