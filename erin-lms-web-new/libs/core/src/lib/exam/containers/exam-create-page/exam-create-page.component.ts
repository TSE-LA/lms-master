import {Component, ViewChild} from '@angular/core';
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {AnswerResultType, ExamModel, ExamType} from "../../model/exam.model";
import {ExamConfigSectionComponent} from "./exam-config-section/exam-config-section.component";
import {ExamQuestionsSectionComponent} from "./exam-questions-section/exam-questions-section.component";
import {ExamPublishConfigSectionComponent} from "./exam-publish-config-section/exam-publish-config-section.component";
import {ExamEnrollSectionComponent} from "./exam-enroll-section/exam-enroll-section.component";
import {ExamBasicInfoSectionComponent} from "./exam-basic-info-section/exam-basic-info-section.component";

@Component({
  selector: 'jrs-exam-create-page',
  template: `
    <div class="margin-bottom-large">
      <jrs-button
        [iconName]="'arrow_back_ios'"
        [iconColor]="'secondary'"
        [noOutline]="true"
        [isMaterial]="true"
        [size]="'medium'"
        [bold]="true"
        [textColor]="'text-link'"
        (clicked)="back()">БУЦАХ
      </jrs-button>

      <jrs-exam-basic-info-section #infoSection></jrs-exam-basic-info-section>

      <jrs-exam-questions-section #questionSection></jrs-exam-questions-section>

      <jrs-exam-config-section #configSection></jrs-exam-config-section>

      <jrs-exam-enroll-section #enrollSection></jrs-exam-enroll-section>

      <jrs-exam-publish-config-section #publishSection></jrs-exam-publish-config-section>

      <div class="flex-center">
        <jrs-button
          class="margin-bottom-large"
          [title]="'ҮҮСГЭХ'"
          [load]="loading || creating"
          [color]="'primary'"
          [size]="'icon-medium'"
          [float]="'center'"
          (clicked)="submitExam()">
        </jrs-button>

        <jrs-button
          class="margin-bottom-large margin-left"
          [title]="'НИЙТЛЭХ'"
          [load]="loading|| creating || publishing"
          [color]="'primary'"
          [size]="'icon-medium'"
          [float]="'center'"
          (clicked)="publishExam()">
        </jrs-button>
      </div>
    </div>

    <jrs-page-loader [show]="publishing || creating"></jrs-page-loader>
  `
})
export class ExamCreatePageComponent {
  @ViewChild('infoSection') infoSection: ExamBasicInfoSectionComponent;
  @ViewChild('configSection') configSection: ExamConfigSectionComponent;
  @ViewChild('questionSection') questionSection: ExamQuestionsSectionComponent;
  @ViewChild('publishSection') publishSection: ExamPublishConfigSectionComponent;
  @ViewChild('enrollSection') enrollSection: ExamEnrollSectionComponent;
  loading: boolean;
  creating: boolean;
  publishing: boolean;
  private exam: ExamModel;


  constructor(private sb: ExamSandboxService) {
  }

  back(): void {
    this.sb.goBack()
  }

  submitExam(): void {
    this.creating = true;
    if (!this.isFormValid()) {
      this.creating = false;
      return;
    }
    this.loading = true;
    this.mapFormToExam();
    this.sb.createExam(this.exam).subscribe(() => {
      this.creating = false;
      this.loading = false;
      this.sb.snackbarOpen("Шалгалт амжилттай үүсгэлээ", true);
      this.sb.navigateByUrl('/exam/container/list');
    }, () => {
      this.creating = false;
      this.loading = false;
      this.sb.snackbarOpen("Шалгалт үүсгэхэд алдаа гарлаа");
    });
  }

  publishExam(): void {
    if (!this.isFormValid()) {
      return;
    }
    this.loading = true;
    this.mapFormToExam();
    this.sb.createExam(this.exam).subscribe((res) => {
      this.exam.id = res;
      this.loading = false;
      this.sb.snackbarOpen("Шалгалт амжилттай үүсгэлээ", true);
      this.publishing = true;
      this.sb.publishExam(this.exam.id).subscribe(() => {
        this.publishing = false;
        this.sb.snackbarOpen("Шалгалт амжилттай нийтэллээ", true);
        this.sb.navigateByUrl('/exam/container/list');
      }, () => {
        this.publishing = false;
        this.loading = false;
        this.sb.snackbarOpen("Шалгалт нийтлэхэд алдаа гарлаа");
      });

    }, () => {
      this.loading = false;
      this.sb.snackbarOpen("Шалгалт үүсгэхэд алдаа гарлаа");
    });
  }

  private isFormValid(): boolean {
    const isInfoSectionValid = this.infoSection.isFormValid();
    const isConfigSectionValid = this.configSection.isFormValid();
    const publishFormValid = this.publishSection.isFormValid();
    return isInfoSectionValid && isConfigSectionValid && publishFormValid;
  }

  private mapFormToExam(): void {
    const infoFormGroup = this.infoSection.examFormGroup;
    const configFormGroup = this.configSection.examFormGroup;
    const publishFormGroup = this.publishSection.examFormGroup;
    const questionIds = new Set(this.questionSection.selectedQuestions.map(question => question.id));
    const randomQuestions = this.questionSection.randomQuestion;
    const maxScore = this.questionSection.totalScore;
    this.exam = {
      id: null,
      name: infoFormGroup.controls.name.value,
      description: infoFormGroup.controls.summary.value,
      categoryId: infoFormGroup.controls.category.value.id,
      groupId: infoFormGroup.controls.group.value.id,
      type: ExamType.OFFICIAL,
      publishDate: publishFormGroup.controls.publishDate.value,
      publishTime: publishFormGroup.controls.startHour.value + ':' + publishFormGroup.controls.startMin.value,
      sendEmail: false,
      sendSms: false,
      mailText: '',
      smsText: '',
      enrolledLearners: this.enrollSection.getLearnerIds(),
      enrolledGroups: [],
      notPublished: true,
      isEditable: true,
      config: {
        questionIds: questionIds,
        randomQuestions: randomQuestions,
        showAnswerResult: AnswerResultType.AFTER_EXAM,
        autoStart: configFormGroup.controls.autoStart.value,
        shuffleQuestion: configFormGroup.controls.shuffleQuestion.value,
        shuffleAnswer: configFormGroup.controls.shuffleAnswer.value,
        questionsPerPage: false,
        threshold: configFormGroup.controls.threshold.value,
        attempt: configFormGroup.controls.attempt.value,
        certificateId: null,
        maxScore: maxScore,
        startDate: configFormGroup.controls.startDate.value,
        endDate: configFormGroup.controls.endDate.value,
        endTime: configFormGroup.controls.endHour.value + ':' + configFormGroup.controls.endMin.value,
        startTime: configFormGroup.controls.startHour.value + ':' + configFormGroup.controls.startMin.value,
        duration: configFormGroup.controls.duration.value
      }
    }
  }
}
