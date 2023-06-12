import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCoursePublishConfigComponent } from './classroom-course-publish-config.component';

describe('ClassroomCoursePublishConfigComponent', () => {
  let component: ClassroomCoursePublishConfigComponent;
  let fixture: ComponentFixture<ClassroomCoursePublishConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCoursePublishConfigComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCoursePublishConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
