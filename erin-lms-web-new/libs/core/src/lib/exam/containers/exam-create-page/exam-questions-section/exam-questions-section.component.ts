import {ChangeDetectorRef, Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {ExamSandboxService} from "../../../services/exam-sandbox.service";
import {CategoryItem, GroupNode, RandomQuestion} from "../../../../../../../shared/src/lib/shared-model";
import {QuestionSelectDialogComponent} from "../question-select-dialog/question-select-dialog.component";
import {DialogConfig} from "../../../../../../../shared/src/lib/dialog/dialog-config";
import {QuestionBank} from "../../../model/question.model";
import {ExamModel} from "../../../model/exam.model";
import {forkJoin, Observable, Subject, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";

@Component({
  selector: 'jrs-exam-questions-section',
  template: `
    <jrs-section
      [color]="'primary'"
      [outline]="false">
      <div *ngIf="!categoriesLoad && !groupLoad">
        <div class="flex">
          <jrs-header-text
            [size]="'medium'"
            [margin]="false"
            class="margin-bottom-medium">
            НИЙТ ОНОО: {{totalScore}}
          </jrs-header-text>
        </div>

        <jrs-divider></jrs-divider>

        <div class="row gx-1 margin-top">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">

            <jrs-selected-questions
              *ngIf="!loading"
              [questions]="selectedQuestions"
              (totalChange)="setSelectTotal($event)"
              (openDialog)="openSelectDialog()">
            </jrs-selected-questions>

            <jrs-skeleton-loader [load]="loading" [amount]="5"></jrs-skeleton-loader>

            <jrs-random-selected-questions
              [questions]="randomQuestion"
              [categories]="categories"
              [groups]="groups"
              [currentAvailable]="availableRandomQuestionCount"
              (getTotalAvailable)="getTotalAvailable($event)"
              (totalChange)="randomTotal = $event; addTotal()">
            </jrs-random-selected-questions>
          </div>
        </div>
      </div>
      <jrs-skeleton-loader [amount]="6" [load]="categoriesLoad||groupLoad"></jrs-skeleton-loader>
    </jrs-section>
  `
})
export class ExamQuestionsSectionComponent implements OnInit {
  @Input() exam: ExamModel;
  selectedQuestions: QuestionBank[] = [];
  randomQuestion: RandomQuestion[] = [];
  selectedTotal = 0;
  randomTotal = 0;
  totalScore = 0;
  groups: GroupNode[] = [];
  categoriesLoad: boolean;
  categories: CategoryItem[] = [];
  selectedGroupName = "";
  groupLoad: boolean;
  selectedCategoryName = "";
  availableRandomQuestionCount = 0;
  loading: boolean;
  private initialLoadSubject = new Subject();
  private selectedCategory: CategoryItem;
  private selectedGroup: GroupNode;


  constructor(private sb: ExamSandboxService, private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.setupAllData().subscribe(res => {
      this.initialLoadSubject.next(res);
    }, () => {
      this.sb.snackbarOpen("Асуултын мэдээлэл ачаалахад алдаа гарлаа!");
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'exam' && this.exam) {
        this.setupFormWithInitialValue();
      }
    }
  }

  navigateToCreate(): void {
    this.sb.navigateByUrl('exam/question/create');
  }

  openSelectDialog(): void {
    const config = new DialogConfig();
    config.outsideClick = false;
    config.title = "Асуулт сонгох";
    config.width = "800px";
    config.data = {
      selectedIds: new Set(this.selectedQuestions.map(question => question.id)),
      groups: this.groups,
      categories: this.categories,
      selectedCategory: this.selectedCategory,
      selectedGroup: this.selectedGroup
    };
    const dialogRef = this.sb.openDialog(QuestionSelectDialogComponent, config);
    dialogRef.afterClosed.subscribe(res => {
      if (res) {
        this.selectedQuestions = [...this.selectedQuestions.concat(res)];
      }
    })
  }

  getTotalAvailable(event): void {
    this.sb.getTotalAvailable(event.category, event.group, event.score).subscribe(res => {
      this.availableRandomQuestionCount = res;
    });
  }

  addTotal(): void {
    this.totalScore = this.selectedTotal + this.randomTotal;
  }

  setSelectTotal(total: number): void {
    this.selectedTotal = total;
    this.addTotal();
    this.cd.detectChanges();
  }

  private setupAllData(): Observable<any> {
    this.categoriesLoad = true;
    const getCategories = this.sb.getQuestionCategories().pipe(map(res => {
      this.categories = res;
      this.selectedCategory = this.categories[0];
      this.selectedCategoryName = this.selectedCategory.name;
      this.categoriesLoad = false;
    }), catchError(err => {
      this.categoriesLoad = false;
      this.sb.snackbarOpen("Асуултын ангилал ачаалахад алдаа гарлаа");
      return throwError(err);
    }));
    this.groupLoad = true;
    const getGroups = this.sb.getQuestionGroups().pipe(map(res => {
      this.groups = res;
      this.selectedGroup = this.groups[0];
      this.selectedGroupName = this.selectedGroup.name;
      this.groupLoad = false;
    }), catchError(err => {
      this.groupLoad = false;
      this.sb.snackbarOpen("Асуултын групп ачаалахад алдаа гарлаа");
      return throwError(err);
    }))
    return forkJoin([getCategories, getGroups]);
  }

  private setupFormWithInitialValue(): void {
    this.loading = true;
    this.initialLoadSubject.subscribe(() => {
      this.assignSelectedQuestions();
      this.assignRandomQuestions();
      this.loading = false;
    }, () => {
      this.loading = false;
      this.sb.snackbarOpen("Асуулт ачаалахад алдаа гарлаа!");
    });
  }

  private assignSelectedQuestions(): void {
    this.sb.getQuestionsByIds(this.exam.config.questionIds).subscribe(res => {
      this.selectedQuestions = res;
    }, () => {
      this.loading = false;
    });
  }

  private assignRandomQuestions(): void {
    const tasks = [];
    for (const randomQuestion of this.exam.config.randomQuestions) {
      randomQuestion.category = this.sb.getLoadedQuestionCategoryById(randomQuestion.category.id);
      randomQuestion.group = this.sb.getLoadedQuestionGroupById(randomQuestion.group.id);
      tasks.push(this.sb.getTotalAvailable(randomQuestion.category.id, randomQuestion.group.id, randomQuestion.score).pipe(map(res => {
        randomQuestion.available = res;
      })));
    }
    forkJoin(tasks).subscribe(() => this.randomQuestion = this.exam.config.randomQuestions);
  }
}
