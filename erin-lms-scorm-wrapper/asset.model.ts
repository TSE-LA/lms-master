/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import {AssetType} from './constants/asset-type';

export interface Asset {
  assetName: string;
  type: AssetType;
}

export interface ScrollableAsset {
  height: number;
  marginBottom: number;
}
