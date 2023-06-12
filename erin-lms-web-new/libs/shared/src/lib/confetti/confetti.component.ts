import {Component, ElementRef, OnInit, Renderer2} from '@angular/core';
import * as confetti from 'canvas-confetti';

@Component({
  selector: 'jrs-confetti',
  template: ``,
  styleUrls: ['./confetti.component.scss']
})
export class ConfettiComponent implements OnInit {
  public clicked = false;

  constructor(
    private renderer2: Renderer2,
    private elementRef: ElementRef
  ) {
  }

  ngOnInit() {
    this.igniteConfetti();
  }

  igniteConfetti(): void {
    const canvas = this.renderer2.createElement('canvas');

    this.renderer2.appendChild(this.elementRef.nativeElement, canvas);

    const myConfetti = confetti.create(canvas, {
      resize: true // will fit all screen sizes
    });

    myConfetti();

    this.clicked = true;
    setTimeout(() => {
      this.igniteConfetti();
    }, 1500);
  }
}
