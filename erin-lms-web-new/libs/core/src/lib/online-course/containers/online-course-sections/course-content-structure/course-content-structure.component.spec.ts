import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseContentStructureComponent } from './course-content-structure.component';

describe('CourseContentStructureComponent', () => {
  let component: CourseContentStructureComponent;
  let fixture: ComponentFixture<CourseContentStructureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CourseContentStructureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseContentStructureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
