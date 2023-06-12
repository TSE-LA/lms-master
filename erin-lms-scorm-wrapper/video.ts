/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

export class Video {
  videoName: string;
  currentTime: number;
  progressMeasure: number;
  progressWeight: number;
  isCompleted: boolean;

  duration: number;

  constructor(videoName: string, currentTime: number, progressMeasure: number, progressWeight: number,
    isCompleted: boolean) {
    this.videoName = videoName;
    this.currentTime = currentTime;
    this.progressMeasure = progressMeasure;
    this.progressWeight = progressWeight;
    this.isCompleted = isCompleted;
  }

  setDuration(duration: number): void {
    this.duration = duration;
  }
}