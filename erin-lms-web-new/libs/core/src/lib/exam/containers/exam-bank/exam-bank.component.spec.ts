import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamBankComponent } from './exam-bank.component';

describe('ExamBankComponent', () => {
  let component: ExamBankComponent;
  let fixture: ComponentFixture<ExamBankComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamBankComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamBankComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
