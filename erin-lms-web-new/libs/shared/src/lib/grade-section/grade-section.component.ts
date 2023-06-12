import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-grade-section',
  template: `
    <div class="grade-container">
      <div class="title">Дүнгийн мэдээлэл</div>
      <div class="keys">
        <div *ngFor="let key of keys" class="key">
          {{key}}
        </div>
      </div>

      <div *ngIf="!gradesUndefined" class="values">
        <div *ngFor="let value of values" class="value">
          {{value}}
        </div>
      </div>
      <div *ngIf="gradesUndefined" class="no-grades">
        <span>Дүнгийн мэдээлэл оруулаагүй байна.</span>
      </div>
    </div>`,
  styleUrls: ['./grade-section.component.scss']
})
export class GradeSectionComponent {
  @Input() values = [];
  @Input() keys = [];
  @Input() gradesUndefined: boolean;
}
