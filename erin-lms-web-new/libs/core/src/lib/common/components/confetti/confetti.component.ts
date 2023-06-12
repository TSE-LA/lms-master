import {Component, OnInit} from "@angular/core";
import {interval} from "rxjs";
import {pausable, PausableObservable} from "rxjs-pausable";

@Component({
  selector: 'jr-confetti',
  template: ``
})
export class ConfettiComponent implements OnInit {
  pausable: PausableObservable<number>;

  static random(min: number, max: number) {
    return Math.random() * (max - min) + min;
  }

  ngOnInit(): void {
    this.shoot();
    this.pausable = interval(800)
      .pipe(pausable()) as PausableObservable<number>;
    this.pausable.subscribe(this.shoot.bind(this));
    this.pausable.resume();

    setTimeout(() => {
      this.pausable.pause();
    }, 5000)
  }

  shoot() {
    try {
      this.confetti({
        angle: ConfettiComponent.random(60, 120),
        spread: ConfettiComponent.random(10, 50),
        particleCount: ConfettiComponent.random(40, 50),
        origin: {
          y: 0.6
        }
      });
    }
    catch (e) {
      console.error(e);
    }
  }

  confetti(args: any) {
    if(window['confetti']){
      return window['confetti'].apply(this, arguments);
    }
  }
}
