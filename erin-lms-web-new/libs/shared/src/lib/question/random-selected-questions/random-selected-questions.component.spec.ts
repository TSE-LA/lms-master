import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RandomSelectedQuestionsComponent } from './random-selected-questions.component';

describe('RandomChosenQuestionsComponent', () => {
  let component: RandomSelectedQuestionsComponent;
  let fixture: ComponentFixture<RandomSelectedQuestionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RandomSelectedQuestionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RandomSelectedQuestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
