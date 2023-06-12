import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyUpdatePageComponent } from './survey-update-page.component';

describe('SurveyUpdatePageComponent', () => {
  let component: SurveyUpdatePageComponent;
  let fixture: ComponentFixture<SurveyUpdatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyUpdatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyUpdatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
