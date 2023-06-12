import {Component, Input, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {FormUtil} from "../../../../../../../shared/src/lib/utilities/form.util";
import {ExamModel} from "../../../model/exam.model";
import {DEFAULT_EXAM} from "../../../model/exam.constants";
import {TimeUtil} from "../../../../../../../shared/src/lib/utilities/time.util";
import {DateFormatter} from "../../../../../../../shared/src/lib/utilities/date-formatter.util";

@Component({
  selector: 'jrs-exam-publish-config-section',
  template: `
    <jrs-section [color]="'primary'"
                 [outline]="false">
      <jrs-header-text [size]="'medium'" [margin]="false">НИЙТЛЭХ ТОХИРГОО</jrs-header-text>
      <div class="row gx-1">
        <div class="col-media_s-12 col-media_sm-12 col-media_md-3 col-media_xl-3">
          <jrs-date-picker
            [formGroup]="examFormGroup"
            [formType]="'publishDate'"
            [datePickerSize]="'standard'"
            [defaultDate]="examFormGroup.controls.publishDate.value"
            [label]="'Нийтлэх өдөр'"
            [width]="'100%'">
          </jrs-date-picker>
        </div>
        <div class="col-media_s-12 col-media_sm-12 col-media_md-3 col-media_xl-3">
          <jrs-time-picker
            [formGroup]="examFormGroup"
            [hourFormType]="'startHour'"
            [minuteFormType]="'startMin'"
            [label]="'Нийтлэх цаг'"
            [initialValue]="{name:'00 : 00', id: '00'}"
            [size]="'long'">
          </jrs-time-picker>
        </div>
      </div>
    </jrs-section>
  `
})
export class ExamPublishConfigSectionComponent {
  @Input() exam: ExamModel = DEFAULT_EXAM;
  @Input() examFormGroup: FormGroup;

  constructor() {
    this.setupPublishForm()
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'exam' && this.exam) {
        this.setupFormWithInitialValue()
      }
    }
  }

  isFormValid(): boolean {
    return FormUtil.isFormValid(this.examFormGroup);
  }

  private setupPublishForm(): void {
    this.examFormGroup = new FormGroup({
      publishDate: new FormControl(DateFormatter.dateFormat(new Date, '.'), [Validators.required]),
      startHour: new FormControl('', [Validators.required]),
      startMin: new FormControl('', [Validators.required])
    })
  }

  private setupFormWithInitialValue(): void {
    this.examFormGroup.controls.publishDate.setValue(DateFormatter.isoToDatePickerString(this.exam.publishDate));
    this.examFormGroup.controls.startHour.setValue(TimeUtil.getHour(this.exam.publishTime));
    this.examFormGroup.controls.startMin.setValue(TimeUtil.getMinute(this.exam.publishTime));
  }
}
