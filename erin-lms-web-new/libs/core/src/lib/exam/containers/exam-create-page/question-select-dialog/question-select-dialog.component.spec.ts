import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionSelectDialogComponent } from './question-select-dialog.component';

describe('QuestionSelectDialogComponent', () => {
  let component: QuestionSelectDialogComponent;
  let fixture: ComponentFixture<QuestionSelectDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuestionSelectDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionSelectDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
