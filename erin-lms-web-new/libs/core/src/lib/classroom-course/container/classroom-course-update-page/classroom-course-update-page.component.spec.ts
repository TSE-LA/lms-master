import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCourseUpdatePageComponent } from './classroom-course-update-page.component';

describe('ClassroomCourseUpdatePageComponent', () => {
  let component: ClassroomCourseUpdatePageComponent;
  let fixture: ComponentFixture<ClassroomCourseUpdatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCourseUpdatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCourseUpdatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
