import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DropdownViewComponent } from './dropdown-view.component';

describe('DropdownComplexComponent', () => {
  let component: DropdownViewComponent;
  let fixture: ComponentFixture<DropdownViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DropdownViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DropdownViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
