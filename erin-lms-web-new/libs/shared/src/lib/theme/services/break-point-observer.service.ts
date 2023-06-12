/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class BreakPointObserverService {
  //breakpoint unit is in px
  defaultBreakPoints: Map<string, number> = new Map([
    ["media_xxl", 1600],
    ["media_xl", 1280],
    ["media_lg", 1024],
    ["media_ml", 920],
    ["media_md", 800],
    ["media_sm", 600],
    ["media_s", 414]
  ]);

  currentBreakPoints: Map<string, number> = this.defaultBreakPoints;
  currentMediaState = "media_xl";

  private $breakPointSubject = new ReplaySubject<string>();

  public getMediaBreakPointChange(): Observable<string> {
    this.listenMediaChanges();
    return this.$breakPointSubject.asObservable();
  }

  private listenMediaChanges(): void {
    this.checkMediaSizeUpdateState();
    window.addEventListener('resize', () => {
      this.checkMediaSizeUpdateState();
    });
  }

  private checkMediaSizeUpdateState(): void {
    if (window.innerWidth > this.currentBreakPoints.get("media_xxl")) {
      this.updateMediaState("media_xxl");
    } else if (window.innerWidth > this.currentBreakPoints.get("media_xl")) {
      this.updateMediaState("media_xl");
    } else if (window.innerWidth > this.currentBreakPoints.get("media_lg") && window.innerWidth <= this.currentBreakPoints.get("media_xl")) {
      this.updateMediaState("media_lg");
    } else if (window.innerWidth > this.currentBreakPoints.get("media_ml") && window.innerWidth <= this.currentBreakPoints.get("media_lg")) {
      this.updateMediaState("media_ml");
    } else if (window.innerWidth > this.currentBreakPoints.get("media_md") && window.innerWidth <= this.currentBreakPoints.get("media_ml")) {
      this.updateMediaState("media_md");
    } else if (window.innerWidth > this.currentBreakPoints.get("media_sm") && window.innerWidth <= this.currentBreakPoints.get("media_md")) {
      this.updateMediaState("media_sm");
    } else if (window.innerWidth > this.currentBreakPoints.get("media_s") && window.innerWidth <= this.currentBreakPoints.get("media_sm")) {
      this.updateMediaState("media_s");
    } else if (window.innerWidth <= this.currentBreakPoints.get("media_s")) {
      this.updateMediaState("media_xs");
    }
  }

  private updateMediaState(changedMedia: string): void {
    if (this.currentMediaState != changedMedia) {
      this.currentMediaState = changedMedia;
      this.$breakPointSubject.next(this.currentMediaState);
    }
  }


  /*
  * Set break points from style variable
  * @param {HTMLElement} referenceElement - reference element that contain theme css
  * */
  public getMediaBreakPointsFromCSS(referenceElement: HTMLElement): void {
    if (referenceElement) {
      for (let key of this.defaultBreakPoints.keys()) {
        const temp = getComputedStyle(referenceElement).getPropertyValue(`--${key}`);
        if (temp) {
          this.currentBreakPoints.set(key, parseInt(temp));
        }
      }
    }
  }
}
