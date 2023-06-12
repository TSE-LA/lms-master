import {Component, EventEmitter, OnInit, Output} from "@angular/core";

@Component({
  selector: 'jrs-achievement',
  template: `
    <div class="background-style">
      <div class="achievement-wrapper">
        <div class="achievement-super">
          <div class="achievement-body">
            <p class="achievement-text">
              Та сургалтыг амжилттай дүүргэлээ
            </p>
          </div>
          <div class="achievement-title">
            <img src="assets/images/trophy.png" alt="Achievement logo">
          </div>
        </div>
      </div>
    </div>

  `,
  styleUrls: ['achievement.component.scss']
})
export class AchievementComponent implements OnInit {
  @Output() finished = new EventEmitter();

  constructor() {
  }

  ngOnInit(): void {
    if (this.finished) {
      this.animateElements();
    }
  }

  private animateElements(): void {
    const achievementWrapper = document.querySelector(".achievement-wrapper");
    achievementWrapper.classList.remove("animation");
    achievementWrapper.classList.add("animation")
    this.finished.emit();
  }
}
