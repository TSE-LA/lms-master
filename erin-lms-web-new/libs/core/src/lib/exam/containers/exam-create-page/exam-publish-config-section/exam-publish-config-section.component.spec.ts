import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamPublishConfigSectionComponent } from './exam-publish-config-section.component';

describe('ExamPublishConfigSectionComponent', () => {
  let component: ExamPublishConfigSectionComponent;
  let fixture: ComponentFixture<ExamPublishConfigSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamPublishConfigSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamPublishConfigSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
