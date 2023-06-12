/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import {SuspendData} from "../suspend_data";

describe('SuspendData', () => {
  let suspend_data: SuspendData;
  const suspend_data_str = "textProgressWeight=0.6,video1=24.1823|80.24|0.4|false,endPage=1";

  beforeEach(() => {
    suspend_data = new SuspendData(suspend_data_str);
  });

  it('should convert string suspend_data into an SuspendData object', () => {
    expect(suspend_data).toBeTruthy();
    expect(suspend_data.videos.length).toEqual(1);
    expect(suspend_data.textProgressWeight).toEqual(0.6);
    const video = suspend_data.videos[0];
    expect(video.videoName).toEqual('video1');
    expect(video.progressMeasure).toEqual(80.24);
    expect(video.progressWeight).toEqual(0.4);
  });

  it('should convert SuspendData object to suspend_data string', () => {
    expect(suspend_data.toString()).toEqual(suspend_data_str);
  });

  it('should update current time of the video', () => {
    suspend_data.updateVideoCurrentTime('video1', 26.20);
    expect(suspend_data.getVideoCurrentTime('video1')).toEqual(26.20);
  });

  it('should update the progress of a video', () => {
    suspend_data.updateVideoProgressMeasure('video1', 100);
    expect(suspend_data.getVideoProgressMeasure('video1')).toEqual(100);
  });

  it('should return the progress weight of a video', () => {
    expect(suspend_data.getVideoProgressWeight('video1')).toEqual(0.4);
  });

  it('should set the status of a video complete', () => {
    suspend_data.completeVideo('video1');
    expect(suspend_data.isVideoCompleted('video1')).toBeTruthy();
  });
});