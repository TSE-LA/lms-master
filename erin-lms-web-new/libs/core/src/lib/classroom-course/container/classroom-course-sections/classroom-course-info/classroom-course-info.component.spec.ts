import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCourseInfoComponent } from './classroom-course-info.component';

describe('ClassroomCourseInfoComponent', () => {
  let component: ClassroomCourseInfoComponent;
  let fixture: ComponentFixture<ClassroomCourseInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCourseInfoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCourseInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
