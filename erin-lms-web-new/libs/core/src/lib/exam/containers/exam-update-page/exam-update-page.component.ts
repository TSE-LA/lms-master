import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {AnswerResultType, ExamModel, ExamType} from "../../model/exam.model";
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {ExamConfigSectionComponent} from "../exam-create-page/exam-config-section/exam-config-section.component";
import {ExamQuestionsSectionComponent} from "../exam-create-page/exam-questions-section/exam-questions-section.component";
import {ExamPublishConfigSectionComponent} from "../exam-create-page/exam-publish-config-section/exam-publish-config-section.component";
import {ExamEnrollSectionComponent} from "../exam-create-page/exam-enroll-section/exam-enroll-section.component";
import {ExamBasicInfoSectionComponent} from "../exam-create-page/exam-basic-info-section/exam-basic-info-section.component";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";

@Component({
  selector: 'jrs-exam-update-page',
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
      <ng-container *ngIf="editable">
        <jrs-exam-basic-info-section #infoSection [exam]="exam"></jrs-exam-basic-info-section>

        <jrs-exam-questions-section #questionSection [exam]="exam"></jrs-exam-questions-section>

        <jrs-exam-config-section #configSection [exam]="exam"></jrs-exam-config-section>

        <jrs-exam-enroll-section #enrollSection [exam]="exam"></jrs-exam-enroll-section>

        <jrs-exam-publish-config-section #publishSection [exam]="exam"></jrs-exam-publish-config-section>

        <div class="flex-center">
          <jrs-button
            class="margin-bottom-large"
            [title]="'ХАДГАЛАХ'"
            [load]="loading || updating"
            [color]="'primary'"
            [size]="'icon-medium'"
            [float]="'center'"
            (clicked)="submitExam()">
          </jrs-button>

          <jrs-button
            *ngIf="notPublished && editable"
            class="margin-bottom-large margin-left"
            [title]="'НИЙТЛЭХ'"
            [load]="loading|| updating || publishing"
            [color]="'primary'"
            [size]="'icon-medium'"
            [float]="'center'"
            (clicked)="publishExam()">
          </jrs-button>
        </div>
      </ng-container>
    </div>
    <jrs-page-loader [show]="publishing || updating"></jrs-page-loader>
  `,
  styles: []
})
export class ExamUpdatePageComponent implements OnInit {
  @ViewChild('infoSection') infoSection: ExamBasicInfoSectionComponent;
  @ViewChild('configSection') configSection: ExamConfigSectionComponent;
  @ViewChild('questionSection') questionSection: ExamQuestionsSectionComponent;
  @ViewChild('publishSection') publishSection: ExamPublishConfigSectionComponent;
  @ViewChild('enrollSection') enrollSection: ExamEnrollSectionComponent;
  loading: boolean;
  updating: boolean;
  publishing: boolean;
  examId: string;
  exam: ExamModel;
  notPublished: boolean;
  editable = true;


  constructor(
    private sb: ExamSandboxService,
    private route: ActivatedRoute,
    private cd: ChangeDetectorRef) {
  }


  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.examId = params.get('id');
      this.setupPage();
    })
  }

  back(): void {
    this.sb.goBack()
  }

  submitExam(): void {
    this.updating = true;
    if (!this.isFormValid()) {
      this.updating = false;
      return;
    }
    this.loading = true;
    const mappedExam = this.mapFormToExam();
    this.sb.updateExam(mappedExam).subscribe(() => {
      this.updating = false;
      this.loading = false;
      this.sb.snackbarOpen("Шалгалт амжилттай хадгалагдлаа", true);
      this.sb.navigateByUrl('/exam/container/list');
    }, () => {
      this.updating = false;
      this.loading = false;
      this.sb.snackbarOpen("Шалгалт хадгалахад алдаа гарлаа");
    });
  }

  publishExam(): void {
    if (!this.isFormValid()) {
      return;
    }
    this.loading = true;
    const mappedExam = this.mapFormToExam();
    this.sb.updateExam(mappedExam).subscribe(() => {
      this.loading = false;
      this.sb.snackbarOpen("Шалгалт амжилттай хадгалагдлаа", true);
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
      this.sb.snackbarOpen("Шалгалт хадгалахад алдаа гарлаа");
    });
  }

  private isFormValid(): boolean {
    const isInfoSectionValid = this.infoSection.isFormValid();
    const isConfigSectionValid = this.configSection.isFormValid();
    const publishFormValid = this.publishSection.isFormValid();
    return isInfoSectionValid && isConfigSectionValid && publishFormValid;
  }

  private mapFormToExam(): ExamModel {
    const infoFormGroup = this.infoSection.examFormGroup;
    const configFormGroup = this.configSection.examFormGroup;
    const publishFormGroup = this.publishSection.examFormGroup;
    const questionIds = new Set(this.questionSection.selectedQuestions.map(question => question.id));
    const randomQuestions = this.questionSection.randomQuestion;
    const maxScore = this.questionSection.totalScore;
    return {
      id: this.exam.id,
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
      isEditable: this.exam.isEditable,
      notPublished: this.exam.notPublished,
      config: {
        questionIds: questionIds,
        randomQuestions: randomQuestions,
        showAnswerResult: AnswerResultType.AFTER_EXAM,
        shuffleQuestion: configFormGroup.controls.shuffleQuestion.value,
        shuffleAnswer: configFormGroup.controls.shuffleAnswer.value,
        autoStart: configFormGroup.controls.autoStart.value,
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
    };
  }

  private setupPage(): void {
    this.loading = true;
    this.sb.getExamDetailedById(this.examId).subscribe(res => {
      this.loading = false;
      this.exam = Object.assign(res);
      this.notPublished = this.exam.notPublished;
      this.editable = this.exam.isEditable;
      if (!this.editable) {
        const config = new DialogConfig();
        config.decline = false;
        config.outsideClick = false;
        config.submitButton = "Ойлголоо";
        config.data = {info: "Эхэлсэн, дууссан шалгалтыг засах боломжгүйг анхаарна уу."}
        config.title = "Анхаар";
        this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
          if (res) {
            this.sb.navigateByUrl('/exam/container/list');
          }
        });
      }
      this.cd.detectChanges();
    }, () => {
      this.loading = false;
    });
  }
}
