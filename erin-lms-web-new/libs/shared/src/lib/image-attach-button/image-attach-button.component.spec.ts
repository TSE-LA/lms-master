import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageAttachButtonComponent } from './image-attach-button.component';

describe('ImageAttachButtonComponent', () => {
  let component: ImageAttachButtonComponent;
  let fixture: ComponentFixture<ImageAttachButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImageAttachButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImageAttachButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
