import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseAttachmentsComponent } from './timed-course-attachments.component';

describe('TimedCourseAttachmentsComponent', () => {
  let component: TimedCourseAttachmentsComponent;
  let fixture: ComponentFixture<TimedCourseAttachmentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseAttachmentsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseAttachmentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
