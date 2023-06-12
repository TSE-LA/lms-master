import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeaderWithFilterComponent } from './header-with-filter.component';

describe('HeaderWithFilterComponent', () => {
  let component: HeaderWithFilterComponent;
  let fixture: ComponentFixture<HeaderWithFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HeaderWithFilterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderWithFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
