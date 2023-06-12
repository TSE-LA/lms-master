import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TakeExamContainerComponent } from './take-exam-container.component';

describe('TakeExamContainerComponent', () => {
  let component: TakeExamContainerComponent;
  let fixture: ComponentFixture<TakeExamContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TakeExamContainerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TakeExamContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
