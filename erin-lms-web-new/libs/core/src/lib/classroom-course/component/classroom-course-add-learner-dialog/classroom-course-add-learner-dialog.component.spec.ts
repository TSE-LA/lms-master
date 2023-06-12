import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCourseAddLearnerDialogComponent } from './classroom-course-add-learner-dialog.component';

describe('ClassroomCourseAddLearnerDialogComponent', () => {
  let component: ClassroomCourseAddLearnerDialogComponent;
  let fixture: ComponentFixture<ClassroomCourseAddLearnerDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCourseAddLearnerDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCourseAddLearnerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
