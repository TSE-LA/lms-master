import {Component, ViewChild} from '@angular/core';
import {SingleChoiceQuestionComponent} from "../../../../../../shared/src/lib/question/single-choice-question/single-choice-question.component";
import {MultiChoiceQuestionComponent} from "../../../../../../shared/src/lib/question/multi-choice-question/multi-choice-question.component";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {CategoryItem, GroupNode, Question, QuestionTypes} from "../../../../../../shared/src/lib/shared-model";
import {DEFAULT_QUESTION} from "../../model/question.constants";
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {FormUtil} from "../../../../../../shared/src/lib/utilities/form.util";
import {ActivatedRoute} from "@angular/router";
import {map} from "rxjs/operators";
import {forkJoin} from "rxjs";
import {SELECT_QUESTION_TYPE} from "../../../../../../shared/src/lib/shared-constants";

@Component({
  selector: 'jrs-question-update-page',
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
      <form [formGroup]="questionFormGroup">
        <jrs-section
          [color]="'primary'"
          [outline]="false">
          <div class="margin-top margin-bottom-large">
            <jrs-header-text [size]="'medium'" [margin]="false">АСУУЛТЫН МЭДЭЭЛЭЛ</jrs-header-text>
          </div>

          <div class="container" *ngIf="!loading">
            <div class="row gx-1">

              <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
                <jrs-dropdown-view
                  [placeholder]="'Асуултын ангилал'"
                  [formGroup]="questionFormGroup"
                  [formType]="'categoryId'"
                  [icon]="'expand_more'"
                  [outlined]="true"
                  [errorText]="'Заавал сонгоно уу'"
                  [padding]="true"
                  [values]="questionCategories"
                  [required]="true"
                  (selectedValue)="selectionChange($event)">
                </jrs-dropdown-view>
              </div>

              <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
                <jrs-dropdown-view
                  [placeholder]="'Асуултын групп'"
                  [formGroup]="questionFormGroup"
                  [formType]="'groupId'"
                  [icon]="'expand_more'"
                  [outlined]="true"
                  [errorText]="'Заавал сонгоно уу'"
                  [padding]="true"
                  [values]="questionGroups"
                  [required]="true">
                </jrs-dropdown-view>
              </div>

              <div class="col-media_s-12 col-media_sm-12 col-media_md-4 col-media_xl-4">
                <jrs-dropdown-view
                  [placeholder]="'Асуултын төрөл'"
                  [formGroup]="questionFormGroup"
                  [formType]="'type'"
                  [icon]="'expand_more'"
                  [errorText]="'Заавал сонгоно уу'"
                  [outlined]="true"
                  [padding]="true"
                  [values]="questionTypes"
                  [required]="true"
                  (selectedValue)="changeType($event)">
                </jrs-dropdown-view>
              </div>
            </div>

            <div class="row gx-1">
              <div class="col-media_s-12 col-media_xl-12 col-media_md-12 col-media_sm-12">
                <jrs-text-area
                  [placeholder]="'Асуулт'"
                  [formGroup]="questionFormGroup"
                  [value]="question.value"
                  [errorText]="'Асуултаа оруулна уу'"
                  [formType]="'question'">
                </jrs-text-area>
              </div>
            </div>
            <div class="row gx-1">
              <div class="col-media_s-12 col-media_xl-12 col-media_md-12 col-media_sm-12">
                <jrs-label [text]="'Зураг хавсаргах'"></jrs-label>
                <div class="row gx-1">
                  <jrs-image-attach-button
                    [uploadedFileName]="question.contentName"
                    [load]="fileLoad"
                    (selectedFile)="attachFile($event)">
                  </jrs-image-attach-button>
                </div>
              </div>
            </div>

            <jrs-single-choice-question
              #singleChoice
              *ngIf="question.type.value === SINGLE_CHOICE"
              [totalScore]="question.totalScore"
              (totalScoreChange)="question.totalScore = $event"
              [answers]="question.answers">
            </jrs-single-choice-question>

            <jrs-multi-choice-question
              #multiChoice
              *ngIf="question.type.value === MULTI_CHOICE"
              [totalScore]="question.totalScore"
              (totalScoreChange)="question.totalScore = $event"
              [answers]="question.answers">
            </jrs-multi-choice-question>

            <div class="row gx-1">
              <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                <jrs-input-field
                  [placeholder]="'Зөв хариулт сонгоход гарах текст'"
                  [value]="question.correctText"
                  [formGroup]="questionFormGroup"
                  [formType]="'correctText'"
                  [movePlaceholder]="true"
                  [selectedType]="'text'">
                </jrs-input-field>
              </div>
            </div>
            <div class="row gx-1">
              <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                <jrs-input-field
                  [placeholder]="'Буруу хариулт сонгоход гарах текст'"
                  [value]="question.wrongText"
                  [formGroup]="questionFormGroup"
                  [formType]="'wrongText'"
                  [movePlaceholder]="true"
                  [selectedType]="'text'">
                </jrs-input-field>
              </div>
            </div>
          </div>
          <jrs-skeleton-loader [load]="loading" [amount]="10"></jrs-skeleton-loader>
        </jrs-section>
      </form>
      <div class="flex-center">
        <jrs-button
          class="margin-bottom-large"
          [title]="'ХАДГАЛАХ'"
          [load]="loading || fileLoad || saving"
          [color]="'primary'"
          [size]="'medium'"
          [float]="'center'"
          (clicked)="submitQuestion()">
        </jrs-button>
      </div>
    </div>
    <jrs-page-loader [show]="saving"></jrs-page-loader>
  `,
  styles: []
})
export class QuestionUpdatePageComponent {
  @ViewChild('singleChoice') singleChoice: SingleChoiceQuestionComponent;
  @ViewChild('multiChoice') multiChoice: MultiChoiceQuestionComponent;

  loading: boolean;
  saving: boolean;
  questionFormGroup: FormGroup;
  questionCategories: CategoryItem[];
  questionGroups: GroupNode[];
  fileLoad: boolean;
  MULTI_CHOICE = QuestionTypes.MULTIPLE_CHOICE;
  SINGLE_CHOICE = QuestionTypes.SINGLE_CHOICE;
  question: Question = DEFAULT_QUESTION;
  questionTypes = SELECT_QUESTION_TYPE;
  private questionId: string;

  constructor(private sb: ExamSandboxService, private route: ActivatedRoute) {
    this.setupForm();
    this.route.paramMap.subscribe(params => {
      this.questionId = params.get('id');
      this.setupPage();
    })
  }

  back(): void {
    this.sb.navigateByUrl('/exam/container/question-list');
  }

  submitQuestion(): void {
    this.saving = true;
    let isValid;
    if (this.question.type.value == QuestionTypes.SINGLE_CHOICE) {
      isValid = this.singleChoice.isValid();
    } else {
      isValid = this.multiChoice.isValid();
    }

    if (FormUtil.isFormValid(this.questionFormGroup) && isValid) {
      this.mapToQuestion();
      this.sb.updateQuestion(this.question).subscribe(() => {
        this.saving = false;
        this.sb.snackbarOpen("Асуулт амжилттай хадгалагдлаа", true);
        this.getQuestion();
      }, () => {
        this.sb.snackbarOpen("Асуулт хадгалахад алдаа гарлаа");
        this.saving = false;
      })
    } else {
      this.saving = false;
    }

  }

  private getQuestion() {
    this.sb.getQuestion(this.questionId).subscribe(res => {
      this.question = res;
      this.loading = false
      this.setupForm()
    }, () => {
      this.sb.snackbarOpen("Асуулт ачаалахад алдаа гарлаа");
      this.loading = false;
    });
  }

  selectionChange(category): void {
    this.sb.setQuestionCategory(category);
  }

  changeType(type): void {
    this.question.type = type;
  }

  attachFile(file): void {
    if (file) {
      this.fileLoad = true;
      this.sb.uploadFile(file).subscribe((res) => {
        this.question.contentId = res;
        this.question.contentName = file.name;
        this.question.hasContent = true;
        this.fileLoad = false;
      }, () => {
        this.fileLoad = false;
      })
    } else {
      this.question.contentId = null;
      this.question.hasContent = false;
    }
  }

  private setupForm(): void {
    this.questionFormGroup = new FormGroup({
      question: new FormControl(this.question.value, [Validators.required]),
      file: new FormControl(""),
      fileName: new FormControl(this.question.contentName),
      categoryId: new FormControl(this.question.category, [Validators.required]),
      groupId: new FormControl(this.question.group, [Validators.required]),
      type: new FormControl(this.question.type, [Validators.required]),
      correctText: new FormControl(this.question.correctText),
      wrongText: new FormControl(this.question.wrongText),
    })
  }

  private mapToQuestion(): void {
    this.question = {
      id: this.question.id,
      value: this.questionFormGroup.controls.question.value,
      type: this.questionFormGroup.controls.type.value,
      answers: this.question.answers,
      totalScore: this.question.totalScore,
      category: this.questionFormGroup.controls.categoryId.value,
      group: this.questionFormGroup.controls.groupId.value,
      correctText: this.questionFormGroup.controls.correctText.value,
      wrongText: this.questionFormGroup.controls.wrongText.value,
      contentId: this.question.contentId,
      hasContent: this.question.hasContent,
      contentName: this.question.contentName
    };
  }

  private setupPage(): void {
    this.loading = true;
    const getCategory = this.sb.getQuestionCategories().pipe(map(res => {
      this.questionCategories = res;
    }));

    const getgroup = this.sb.getQuestionGroups().pipe(map(res => {
      this.questionGroups = res;
    }));

    const getQuestion = this.sb.getQuestion(this.questionId).pipe(map(res => {
      this.question = res;
      this.setupForm()
    }));

    forkJoin([getCategory, getgroup, getQuestion]).subscribe(() => {
      this.loading = false;
    }, () => {
      this.sb.snackbarOpen("Асуулт ачаалахад алдаа гарлаа");
      this.loading = false;
    })
  }
}
