import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyCreatePageComponent } from './survey-create-page.component';

describe('SurveyCreatePageComponent', () => {
  let component: SurveyCreatePageComponent;
  let fixture: ComponentFixture<SurveyCreatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyCreatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyCreatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
