import {Component, OnInit, ViewChild} from '@angular/core';
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {CategoryItem, GroupNode, Question, QuestionTypes} from "../../../../../../shared/src/lib/shared-model";
import {SingleChoiceQuestionComponent} from "../../../../../../shared/src/lib/question/single-choice-question/single-choice-question.component";
import {MultiChoiceQuestionComponent} from "../../../../../../shared/src/lib/question/multi-choice-question/multi-choice-question.component";
import {FormUtil} from "../../../../../../shared/src/lib/utilities/form.util";
import {QUESTION_TYPES, SELECT_QUESTION_TYPE} from "../../../../../../shared/src/lib/shared-constants";


@Component({
  selector: 'jrs-question-create-page',
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
                [load]="categoriesLoading"
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
                [required]="true"
                [load]="groupsLoading">
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
                [value]="''"
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
                [value]="''"
                [formGroup]="questionFormGroup"
                [formType]="'wrongText'"
                [movePlaceholder]="true"
                [selectedType]="'text'">
              </jrs-input-field>
            </div>
          </div>
        </jrs-section>
      </form>
      <div class="flex-center">
        <jrs-button
          class="margin-bottom-large"
          [title]="'ҮҮСГЭХ'"
          [load]="loading || fileLoad || creating"
          [color]="'primary'"
          [size]="'medium'"
          [float]="'center'"
          (clicked)="submitQuestion()">
        </jrs-button>
        <jrs-button
          class="margin-bottom-large margin-left"
          [title]="'ДАХИН ҮҮСГЭХ'"
          [load]="loading || fileLoad || creating"
          [color]="'primary'"
          [size]="'medium'"
          [float]="'center'"
          (clicked)="submitQuestion(true)">
        </jrs-button>
      </div>
    </div>
    <jrs-page-loader [show]="creating || loading || fileLoad"></jrs-page-loader>
  `,
  styles: []
})
export class QuestionCreatePageComponent implements OnInit {
  @ViewChild('singleChoice') singleChoice: SingleChoiceQuestionComponent;
  @ViewChild('multiChoice') multiChoice: MultiChoiceQuestionComponent;

  loading: boolean;
  creating: boolean;
  questionFormGroup: FormGroup;
  questionCategories: CategoryItem[];
  categoriesLoading: boolean;
  questionGroups: GroupNode[];
  groupsLoading: boolean;
  fileLoad: boolean;
  MULTI_CHOICE = QuestionTypes.MULTIPLE_CHOICE;
  SINGLE_CHOICE = QuestionTypes.SINGLE_CHOICE;
  question: Question;
  questionTypes = SELECT_QUESTION_TYPE;
  private uploadedFileId = "";

  constructor(private sb: ExamSandboxService) {
    this.setDefaultAnswers();
    this.setupForm();
  }

  ngOnInit(): void {
    this.categoriesLoading = true;
    this.sb.getQuestionCategories().subscribe(res => {
      this.questionCategories = res;
      this.categoriesLoading = false;
    }, () => {
      this.sb.snackbarOpen("Асуултын ангилал ачаалахад алдаа гарлаа");
      this.categoriesLoading = false;
    });
    this.groupsLoading = true;
    this.sb.getQuestionGroups().subscribe(res => {
      this.questionGroups = res;
      this.groupsLoading = false;
    }, () => {
      this.sb.snackbarOpen("Асуултын групп ачаалахад алдаа гарлаа");
      this.groupsLoading = false;
    });
  }

  back(): void {
    this.sb.navigateByUrl('/exam/container/question-list');
  }

  submitQuestion(another?: boolean): void {
    this.creating = true;
    let isValid;
    if (this.question.type.value == QuestionTypes.SINGLE_CHOICE) {
      isValid = this.singleChoice.isValid();
    } else {
      isValid = this.multiChoice.isValid();
    }

    if (FormUtil.isFormValid(this.questionFormGroup) && isValid) {
      this.mapToQuestion();
      this.sb.createQuestion(this.question).subscribe(() => {
        this.sb.snackbarOpen("Асуулт амжилттай үүсгэлээ", true);
        if (another) {
          this.sb.reloadPage();
          this.creating = false;
        } else {
          this.sb.navigateByUrl('exam/container/question-list');
          this.creating = false;
        }

      }, () => {
        this.sb.snackbarOpen("Асуулт үүсгэхэд алдаа гарлаа");
        this.creating = false;
      })
    } else {
      this.creating = false;
    }
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
        this.uploadedFileId = res;
        this.fileLoad = false;
      }, () => {
        this.fileLoad = false;
      })
    } else {
      this.uploadedFileId = null;
    }
  }

  private setupForm(): void {
    this.questionFormGroup = new FormGroup({
      question: new FormControl('', [Validators.required]),
      file: new FormControl(''),
      fileName: new FormControl(''),
      categoryId: new FormControl('', [Validators.required]),
      groupId: new FormControl('', [Validators.required]),
      type: new FormControl(this.questionTypes[1], [Validators.required]),
      correctText: new FormControl(""),
      wrongText: new FormControl(""),
    })
  }

  private mapToQuestion(): void {
    this.question = {
      id: "",
      value: this.questionFormGroup.controls.question.value,
      type: this.questionFormGroup.controls.type.value,
      answers: this.question.answers,
      totalScore: Number(this.question.totalScore),
      category: this.questionFormGroup.controls.categoryId.value,
      group: this.questionFormGroup.controls.groupId.value,
      correctText: this.questionFormGroup.controls.correctText.value,
      wrongText: this.questionFormGroup.controls.wrongText.value,
      contentId: this.uploadedFileId,
      hasContent: !!this.uploadedFileId,
      contentName: ""
    };
  }

  private setDefaultAnswers() {
    this.question = {
      value: "", type: QUESTION_TYPES[1], id: "",
      category: {name: "", id: ""},
      group: {name: "", id: "", parent: "", nthSibling: 0, children: []},
      correctText: "",
      wrongText: "",
      totalScore: 1,
      contentId: "",
      hasContent: false,
      contentName: "",
      answers: [{value: null, correct: true, weight: 1, column: 1, formName: '0'},
        {value: null, correct: true, weight: 0, column: 1, formName: '1'},
        {value: null, correct: true, weight: 0, column: 1, formName: '2'}]
    }
  }
}
