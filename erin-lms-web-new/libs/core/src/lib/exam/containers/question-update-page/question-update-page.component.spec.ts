import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionUpdatePageComponent } from './question-update-page.component';

describe('QuestionUpdatePageComponent', () => {
  let component: QuestionUpdatePageComponent;
  let fixture: ComponentFixture<QuestionUpdatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuestionUpdatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionUpdatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
