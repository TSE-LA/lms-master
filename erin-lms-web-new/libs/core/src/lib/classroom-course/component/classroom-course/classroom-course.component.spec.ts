import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCourseComponent } from './classroom-course.component';

describe('ClassroomCourseComponent', () => {
  let component: ClassroomCourseComponent;
  let fixture: ComponentFixture<ClassroomCourseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCourseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
