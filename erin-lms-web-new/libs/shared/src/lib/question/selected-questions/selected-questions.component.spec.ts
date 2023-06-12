import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectedQuestionsComponent } from './selected-questions.component';

describe('ChosenQuestionsComponent', () => {
  let component: SelectedQuestionsComponent;
  let fixture: ComponentFixture<SelectedQuestionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectedQuestionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectedQuestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
