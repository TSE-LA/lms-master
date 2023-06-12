import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCourseTableComponent } from './classroom-course-table.component';

describe('ClassroomCourseNewComponent', () => {
  let component: ClassroomCourseTableComponent;
  let fixture: ComponentFixture<ClassroomCourseTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCourseTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCourseTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
