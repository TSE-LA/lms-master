import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseTestStructureComponent } from './online-course-test-structure.component';

describe('OnlineCourseTestStructureComponent', () => {
  let component: OnlineCourseTestStructureComponent;
  let fixture: ComponentFixture<OnlineCourseTestStructureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseTestStructureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseTestStructureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
