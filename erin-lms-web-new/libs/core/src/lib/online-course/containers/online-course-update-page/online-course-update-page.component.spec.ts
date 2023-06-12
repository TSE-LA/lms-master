import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseUpdatePageComponent } from './online-course-update-page.component';

describe('OnlineCourseUpdatePageComponent', () => {
  let component: OnlineCourseUpdatePageComponent;
  let fixture: ComponentFixture<OnlineCourseUpdatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseUpdatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseUpdatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
