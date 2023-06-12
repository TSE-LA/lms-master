import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamCreatePageComponent } from './exam-create-page.component';

describe('ExamCreatePageComponent', () => {
  let component: ExamCreatePageComponent;
  let fixture: ComponentFixture<ExamCreatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamCreatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamCreatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
