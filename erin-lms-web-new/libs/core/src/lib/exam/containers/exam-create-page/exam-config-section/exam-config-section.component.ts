import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ExamSandboxService} from "../../../services/exam-sandbox.service";
import {CertificateModel} from "../../../../certificate/model/certificate.model";
import {DEFAULT_EXAM} from "../../../model/exam.constants";
import {FormUtil} from "../../../../../../../shared/src/lib/utilities/form.util";
import {ExamModel} from "../../../model/exam.model";
import {TimeUtil} from "../../../../../../../shared/src/lib/utilities/time.util";
import {DateFormatter} from "../../../../../../../shared/src/lib/utilities/date-formatter.util";

@Component({
  selector: 'jrs-exam-config-section',
  template: `
    <form [formGroup]="examFormGroup">
      <jrs-section
        [color]="'primary'"
        [outline]="false">
        <div class="margin-top">
          <jrs-header-text [size]="'medium'" [margin]="false">ШАЛГАЛТЫН ТОХИРГОО</jrs-header-text>
        </div>
        <div class="row gx-1 margin-top-large">
          <div class="col-media_s-12 col-media_sm-6 col-media_md-6 col-media_xl-6">
            <div class="row gx-1">
              <jrs-checkbox
                [formGroup]="examFormGroup"
                [formType]="'shuffleQuestion'"
                [text]="'Асуултын дараалал холих'">
              </jrs-checkbox>
            </div>
            <div class="row gx-1">
              <jrs-checkbox
                [formGroup]="examFormGroup"
                [formType]="'shuffleAnswer'"
                [text]="'Хариултын дараалал холих'">
              </jrs-checkbox>
            </div>
          </div>
          <div class="col-media_s-12 col-media_sm-6 col-media_md-6 col-media_xl-6">
            <div class="row gx-1">
              <div class="col-media_s-12 col-media_sm-6 col-media_md-6 col-media_xl-6">
                <jrs-input-field
                  [formGroup]="examFormGroup"
                  [formType]="'threshold'"
                  [placeholder]="'Тэнцэх оноо'"
                  [movePlaceholder]="true"
                  [errorText]="'0-с их тоо оруулна уу'"
                  [selectedType]="'number'"
                  [required]="true">
                </jrs-input-field>
              </div>
              <div class="col-media_s-12 col-media_sm-6 col-media_md-6 col-media_xl-6">
                <jrs-input-field
                  [formGroup]="examFormGroup"
                  [formType]="'attempt'"
                  [placeholder]="'Оролдлого'"
                  [errorText]="'0-с их тоо оруулна уу'"
                  [movePlaceholder]="true"
                  [selectedType]="'number'"
                  [required]="true">
                </jrs-input-field>
              </div>
            </div>
            <div class="row gx-1">
              <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                <div *ngFor="let config of this.startExamConfigs">
                  <jrs-radio-button
                    [check]="config.selected"
                    [label]="config.name"
                    (clicked)="selectState(config.state)">
                  </jrs-radio-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <jrs-divider></jrs-divider>
        <div class="margin-bottom">
          <jrs-header-text [size]="'medium'" [margin]="false">ШАЛГАЛТЫН ХУГАЦАА</jrs-header-text>
        </div>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
            <jrs-input-field
              [formGroup]="examFormGroup"
              [formType]="'duration'"
              [errorText]="'0-c их минут оруулна уу'"
              [movePlaceholder]="true"
              [placeholder]="'Үргэлжлэх хугацаа'"
              [selectedType]="'number'"
              [required]="true">
            </jrs-input-field>
          </div>
        </div>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-3 col-media_xl-3">
            <jrs-date-picker
              [formGroup]="examFormGroup"
              [formType]="'startDate'"
              [datePickerSize]="'standard'"
              [label]="'Эхлэх өдөр'"
              [width]="'100%'">
            </jrs-date-picker>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-3 col-media_xl-3">
            <jrs-time-picker
              [formGroup]="examFormGroup"
              [hourFormType]="'startHour'"
              [minuteFormType]="'startMin'"
              [label]="'Эхлэх цаг'"
              [size]="'long'">
            </jrs-time-picker>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-3 col-media_xl-3">
            <jrs-date-picker
              [formGroup]="examFormGroup"
              [formType]="'endDate'"
              [datePickerSize]="'standard'"
              [label]="'Дуусах өдөр'"
              [width]="'100%'">
            </jrs-date-picker>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-3 col-media_xl-3">
            <jrs-time-picker
              [formGroup]="examFormGroup"
              [hourFormType]="'endHour'"
              [minuteFormType]="'endMin'"
              [label]="'Дуусах цаг'"
              [size]="'long'">
            </jrs-time-picker>
          </div>
        </div>
      </jrs-section>
    </form>
  `,
  styles: []
})
export class ExamConfigSectionComponent implements OnInit, OnChanges {
  @Input() exam: ExamModel = DEFAULT_EXAM;
  examFormGroup: FormGroup;
  certificates: CertificateModel[] = [];
  certificateLoading: boolean;
  startExamConfigs = this.sb.constants.START_EXAM_CONFIG;
  moreThanZeroPattern = "^[1-9][0-9]*$";

  constructor(private sb: ExamSandboxService) {
    this.setupForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'exam' && this.exam) {
        this.setupFormWithInitialValue();
        this.setupStartConfig();
      }
    }
  }

  ngOnInit(): void {
    this.certificateLoading = true;
    this.sb.getCertificates().subscribe(res => {
      this.certificates = res;
      this.certificateLoading = false;
    }, () => {
      this.sb.snackbarOpen("Сертификат ачаалахад алдаа гарлаа")
      this.certificateLoading = false;
    });
  }

  selectState(autoStart: boolean): void {
    this.examFormGroup.controls.autoStart.setValue(autoStart);
  }

  isFormValid(): boolean {
    return FormUtil.isFormValid(this.examFormGroup);
  }

  private setupForm(): void {
    this.examFormGroup = new FormGroup({
      shuffleQuestion: new FormControl(false),
      shuffleAnswer: new FormControl(false),
      autoStart: new FormControl(this.startExamConfigs[0].state),
      threshold: new FormControl(null, [Validators.required, Validators.pattern(this.moreThanZeroPattern)]),
      attempt: new FormControl(1, [Validators.required, Validators.pattern(this.moreThanZeroPattern)]),
      startDate: new FormControl(DateFormatter.dateFormat(new Date, '-'), [Validators.required]),
      endDate: new FormControl(DateFormatter.dateFormat(new Date, '-'), [Validators.required]),
      startHour: new FormControl('', [Validators.required]),
      startMin: new FormControl('', [Validators.required]),
      endHour: new FormControl('', [Validators.required]),
      endMin: new FormControl('', [Validators.required]),
      duration: new FormControl(10, [Validators.pattern(this.moreThanZeroPattern)])
    })
    // certificateId: new FormControl(''),
    //   showAnswerResult: new FormControl('', [Validators.required]),
  }

  private setupFormWithInitialValue(): void {
    this.examFormGroup.controls.shuffleQuestion.setValue(this.exam.config.shuffleQuestion);
    this.examFormGroup.controls.shuffleAnswer.setValue(this.exam.config.shuffleAnswer);
    this.examFormGroup.controls.threshold.setValue(this.exam.config.threshold);
    this.examFormGroup.controls.attempt.setValue(this.exam.config.attempt);
    this.examFormGroup.controls.startDate.setValue(new Date(this.exam.config.startDate));
    this.examFormGroup.controls.endDate.setValue(new Date(this.exam.config.endDate));
    this.examFormGroup.controls.duration.setValue(this.exam.config.duration);
    this.examFormGroup.controls.startHour.setValue(TimeUtil.getHour(this.exam.config.startTime));
    this.examFormGroup.controls.startMin.setValue(TimeUtil.getMinute(this.exam.config.startTime));
    this.examFormGroup.controls.endHour.setValue(TimeUtil.getHour(this.exam.config.endTime));
    this.examFormGroup.controls.endMin.setValue(TimeUtil.getMinute(this.exam.config.endTime));
  }

  setupStartConfig(): void {
    if (this.startExamConfigs[0].autoFirst) {
      if (this.exam.config.autoStart) {
        this.startExamConfigs[0].selected = true;
        this.startExamConfigs[1].selected = false;
      } else {
        this.startExamConfigs[0].selected = false;
        this.startExamConfigs[1].selected = true;
      }
    }
    if (!this.startExamConfigs[0].autoFirst) {
      if (this.exam.config.autoStart) {
        this.startExamConfigs[0].selected = false;
        this.startExamConfigs[1].selected = true;
      } else {
        this.startExamConfigs[0].selected = true;
        this.startExamConfigs[1].selected = false;
      }
    }
    this.examFormGroup.controls.autoStart.setValue(this.exam.config.autoStart);
  }
}
