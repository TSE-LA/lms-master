import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IconLabelComponent } from './icon-label.component';

describe('LocationTimeComponent', () => {
  let component: IconLabelComponent;
  let fixture: ComponentFixture<IconLabelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IconLabelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IconLabelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
