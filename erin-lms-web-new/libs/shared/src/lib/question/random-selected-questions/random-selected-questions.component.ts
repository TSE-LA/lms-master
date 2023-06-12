import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {CategoryItem, GroupNode, RandomQuestion} from "../../shared-model";

@Component({
  selector: 'jrs-random-selected-questions',
  template: `
    <div class="margin-bottom-large">
      <div class="flex">
        <jrs-header-text
          [size]="'medium'"
          [margin]="false"
          class="margin-bottom-medium">
          Санамсаргүй сонгогдох асуултууд ({{selectedRandomQuestions}}/{{totalRandomQuestions}})
        </jrs-header-text>
        <span class="spacer"></span>
      </div>

      <div class="flex">
        <div id="group-name">Групп</div>
        <div id="category-name">Ангилал</div>
        <span class="spacer"></span>
        <div id="score-name">Оноо</div>
      </div>

      <div class="flex">
        <div class="flex questions-to-choose">
          <div>
            <div class="select-group">
              <jrs-dropdown-view
                class="margin-left"
                [defaultValue]="selectedGroupName"
                [size]="'medium'"
                [width]="'170px'"
                [icon]="'expand_more'"
                [outlined]="true"
                [load]="groupLoad"
                [padding]="true"
                [values]="groups"
                (selectedValue)="selectGroup($event)">
              </jrs-dropdown-view>
            </div>

          </div>
          <span class="margin-left">
            <jrs-dropdown-view
              class="margin-left"
              [defaultValue]="selectedCategoryName"
              [size]="'medium'"
              [width]="'170px'"
              [icon]="'expand_more'"
              [outlined]="true"
              [load]="categoriesLoad"
              [padding]="true"
              [values]="categories"
              (selectedValue)="selectCategory($event)">
            </jrs-dropdown-view>
        </span>
          <span class="spacer"></span>
          <span class="question-score margin-left">
              <jrs-input-field
                class="answer-input"
                [size]="'small'"
                [noEdit]="true"
                [value]="selectedScore"
                [underline]="false"
                [noOutline]="false"
                [movePlaceholder]="false"
                [placeholder]="''"
                [selectedType]="'text'"
                [padding]="false"
                (inputChanged)="selectedScore = $event; getAvailable()">
          </jrs-input-field>
        </span>
          <span class="question-amount margin-left">
             <jrs-input-field
               class="answer-input"
               [size]="'small'"
               [value]="currentSelectionAmount"
               [underline]="false"
               [noOutline]="false"
               [movePlaceholder]="false"
               [selectedType]="'text'"
               [padding]="false"
               (inputChanged)="currentSelectionAmount = $event">
          </jrs-input-field>
        </span>
          <span class="question-available">
           /{{currentAvailable}}
        </span>

        </div>
        <jrs-button class="padding-left margin-left"
                    (clicked)="addToRandom()"
                    [isMaterial]="true"
                    [iconName]="'add'"
                    [color]="'primary'"
                    [size]="'icon-medium'">
        </jrs-button>
      </div>

      <div *ngFor="let question of questions;let index = index" class="flex">
        <div class="flex chosen-questions">
          <span class="margin-right">{{index + 1}}.</span>
          <div class="question-group"
               [jrsTooltip]="question.group.name">
            {{question.group.name}}
          </div>
          <span class="question-category margin-left"
                [jrsTooltip]="question.category.name">
            {{question.category.name}}
        </span>
          <span class="spacer"></span>
          <div class="chosen-question-score margin-left">
            {{question.score}}
          </div>
          <span class="question-amount margin-left">
             <jrs-input-field
               class="answer-input"
               [size]="'small'"
               [value]="question.totalAmount"
               [underline]="false"
               [noOutline]="false"
               [placeholder]="''"
               [movePlaceholder]="false"
               [selectedType]="'text'"
               [padding]="false"
               (inputChanged)="question.totalAmount = $event; calculateTotals()">
          </jrs-input-field>
        </span>
          <span class="question-available">
           /{{question.available}}
        </span>

        </div>
        <div class="delete-button">
          <jrs-button class="padding-left margin-left"
                      (clicked)="removeRandomQuestions(index)"
                      [isMaterial]="false"
                      [iconName]="'jrs-trash-can'"
                      [color]="'warn'"
                      [size]="'icon-medium'">
          </jrs-button>
        </div>

      </div>

      <div class="flex" *ngIf="questions.length > 0">
        <span class="spacer"></span>
        <div class="grand-total-score">{{totalScore}} оноо</div>
      </div>
    </div>
  `,
  styleUrls: ['./random-selected-questions.component.scss']
})
export class RandomSelectedQuestionsComponent implements OnChanges {
  @Input() questions: RandomQuestion[] = [];
  @Input() groups: GroupNode[] = [];
  @Input() categories: CategoryItem[] = [];
  @Input() currentAvailable = 0;
  @Output() totalChange = new EventEmitter<number>();
  @Output() getTotalAvailable = new EventEmitter<any>();
  selectedRandomQuestions = 0;
  totalRandomQuestions = 0;
  totalScore = 0;
  categoriesLoad: boolean;
  selectedGroupName = "";
  groupLoad: boolean;
  selectedCategoryName = "";
  currentSelectionAmount = 1;
  selectedScore = 1;
  private selectedCategory: CategoryItem;
  private selectedGroup: GroupNode;

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == "questions" && this.questions.length > 0) {
        this.calculateTotals();
      }
      if (prop == "groups") {
        this.selectedGroup = this.groups[0];
        this.selectedGroupName = this.selectedGroup ? this.selectedGroup.name : '';
        this.getAvailable();
      }
      if (prop == "categories") {
        this.selectedCategory = this.categories[0];
        this.selectedCategoryName = this.selectedCategory ? this.selectedCategory.name : '';
        this.getAvailable();
      }
    }
  }

  removeRandomQuestions(index: number): void {
    this.questions.splice(index, 1);
    this.calculateTotals();
  }

  selectGroup(group): void {
    this.selectedGroup = group;
    this.selectedGroupName = this.selectedGroup.name;
    this.getAvailable();
  }

  selectCategory(category): void {
    this.selectedCategory = category;
    this.getAvailable();
  }

  addToRandom(): void {
    this.questions.push({
      index: this.questions.length,
      category: this.selectedCategory,
      group: this.selectedGroup,
      score: this.selectedScore,
      totalAmount: this.currentSelectionAmount,
      available: this.currentAvailable
    });
    this.calculateTotals();
  }

  getAvailable(): void {
    if (this.selectedCategory && this.selectedGroup) {
      this.getTotalAvailable.emit({
        category: this.selectedCategory.id,
        group: this.selectedGroup.id,
        score: this.selectedScore
      });
    }
  }

  private calculateTotals(): void {
    this.selectedRandomQuestions = 0;
    this.totalRandomQuestions = 0;
    this.totalScore = 0;
    for (const question of this.questions) {
      this.selectedRandomQuestions += Number(question.totalAmount);
      this.totalRandomQuestions += question.available;
      this.totalScore += question.totalAmount * question.score;
    }
    this.totalChange.emit(this.totalScore);
  }
}
