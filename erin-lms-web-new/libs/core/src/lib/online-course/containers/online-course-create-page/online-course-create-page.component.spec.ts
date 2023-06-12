import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseCreatePageComponent } from './online-course-create-page.component';

describe('OnlineCourseCreatePageComponent', () => {
  let component: OnlineCourseCreatePageComponent;
  let fixture: ComponentFixture<OnlineCourseCreatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseCreatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseCreatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
