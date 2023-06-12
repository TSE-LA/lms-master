/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import {Video} from "./video";

export class SuspendData {
  videos: Video[] = [];
  textProgressWeight: number = 1;
  endPage: number = 1;

  constructor(suspend_data: string) {
    if (suspend_data !== undefined && suspend_data !== '0') {
      const split = suspend_data.split(",");

      split.forEach(item => {
        const split = item.split("=");

        if (split[0] === "textProgressWeight") {
          this.textProgressWeight = parseFloat(split[1]);
        } else if (split[0] === "endPage") {
          this.endPage = parseInt(split[1]);
        } else {
          const videoName = split[0];
          const splitByVerticalBar = split[1].split("|");
          const currentTime = parseFloat(splitByVerticalBar[0]);
          const progressMeasure = parseFloat(splitByVerticalBar[1]);
          const progressWeight = parseFloat(splitByVerticalBar[2]);
          const isCompleted = (splitByVerticalBar[3]) === "true";
          const video = new Video(videoName, currentTime, progressMeasure, progressWeight, isCompleted);
          this.videos.push(video);
        }
      });
    }
  }

  toString(): string {
    let str = "textProgressWeight=" + this.textProgressWeight;

    for (let video of this.videos) {
      str += "," + video.videoName + "=" + video.currentTime + "|" + video.progressMeasure + "|" + video.progressWeight
        + "|" + video.isCompleted;
    }

    str += ",endPage=" + this.endPage;
    return str;
  }

  updateVideoCurrentTime(videoName: string, currentTime: number): boolean {
    let correct:boolean = false;
    this.videos.forEach(video => {
      if (video.videoName === videoName) {
        video.currentTime = currentTime;
        correct = true;
      }
    });
    return correct;
  }

  getVideoCurrentTime(videoName: string): number {
    for (let video of this.videos) {
      if (video.videoName === videoName) {
        return video.currentTime;
      }
    }
    return 0;
  }

   getVideoProgressWeight(videoName: string): number {
    for (let video of this.videos) {
      if (video.videoName === videoName) {
        return video.progressWeight;
      }
    }
    return 0;
  }

  getVideoProgressMeasure(videoName: string): number {
    for (let video of this.videos) {
      if (video.videoName === videoName) {
        return video.progressMeasure;
      }
    }
    return 0;
  }

  updateVideoProgressMeasure(videoName: string, progressMeasure: number) {
    this.videos.forEach(video => {
      if (video.videoName === videoName) {
        video.progressMeasure = progressMeasure;
      }
    });
  }

  hasCompletedVideos(): boolean {
    let completed: boolean = true;
    for (let video of this.videos) {
      if (video.progressMeasure < 100) {
        completed = false;
      }
    }
    return completed;
  }

  isVideoCompleted(videoName: string): boolean {
    for (let video of this.videos) {
      if (video.videoName === videoName) {
        return video.isCompleted;
      }
    }
    return false;
  }

  completeVideo(videoName: string): void {
    for (let video of this.videos) {
      if (video.videoName === videoName) {
        video.isCompleted = true;
      }
    }
  }

  setEndPage(endPage: number): void {
    this.endPage = endPage;
  }
}
