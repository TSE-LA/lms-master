/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import {AssetType} from '../constants/asset-type';

export class AssetUtil {
  private static images = ['JPEG', 'JPG', 'PNG'];
  private static videos = ['MP4', 'WEBM'];
  private static web = ['HTML'];

  public static getAssetType(mimeType: string) {
    if (this.images.indexOf(mimeType) > -1) {
      return AssetType.IMAGE;
    } else if (this.videos.indexOf(mimeType) > -1) {
      return AssetType.VIDEO;
    } else if (this.web.indexOf(mimeType) > -1) {
      return AssetType.HTML;
    } else {
      return AssetType.UNSUPPORTED;
    }
  }
}
