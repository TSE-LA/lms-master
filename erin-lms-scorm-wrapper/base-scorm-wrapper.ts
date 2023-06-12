/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import {Asset} from './asset.model';
import {AssetUtil} from './util/asset-util';

export abstract class BaseScormWrapper {
  private readonly maxAttempts = 500;
  protected WINDOW: Window;
  protected API: any;
  protected initialized = false;
  protected terminated = false;

  abstract init(): void;

  abstract end(): void;

  /**
   * Begins the process of searching for the API_1484_11 object of the LMS.
   */
  protected getApi(): void {
    if ((window.parent != null) && (window.parent !== window)) {
      this.API = this.scanApi(window.parent);
    }
    if (this.API == null && window.opener != null) {
      this.API = this.scanApi(window.opener);
    }
  }

  /**
   * Searches for the API_1484_11 object from the current window's parent.
   * If the current window's parent does not have the API_1484_11, it'll try to
   * find the parent's parent window until it finds it. The maximum attempts that can be made
   * is 500 times.
   *
   * @param win The current window
   * @returns the API_1484_11 object provided by the LMS
   */
  protected scanApi(win: any): void {
    let attempts = 0;

    while ((win.API_1484_11 == null) && (win.parent != null)
    && (win.parent !== win)) {
      attempts++;
      if (attempts > this.maxAttempts) {
        return null;
      }
      win = win.parent;
    }
    this.WINDOW = win;
    return win.API_1484_11;
  }

  /**
   * Gets a run-time data by calling the API_1484_11 object's getter method
   *
   * @param element The run-time data model
   * @returns {string} The value of the data model
   */
  protected getValue(element: string): string {
    if (this.initialized === false || this.terminated === true) {
      this.fail("Connection is not established with the LMS");
    }

    return this.API.GetValue(element);
  }

  /**
   * Sets a run-time data by calling the API_1484_11 object's setter method
   *
   * @param element The run-time data model
   * @param value The value to set on the data model
   */
  protected setValue(element: string, value: string): void {
    if (this.initialized === false || this.terminated === true) {
      this.fail("Connection is not established with the LMS");
    }

    this.API.SetValue(element, value);
  }

  protected fail(errorMsg: string): void {
    const errorNumber = this.API.GetLastError();
    const errorString = this.API.GetErrorString(errorNumber);
    const diagnostic = this.API.GetDiagnostic(errorNumber);

    const errorDescription = "Error number: " + errorNumber + "\nDescription: " + errorString + "\nDiagnostic: " + diagnostic;
    alert(errorMsg + "\n" + errorDescription);
  }

  /**
   * Loads the order of the pages from a local JSON file using AJAX.
   *
   * @param resource The file name or resource location
   * @param callback The callback function to be called for the response
   */
  protected loadAssetData(resource: string, callback: (response: string) => any): void {
    const xhr = new XMLHttpRequest();
    xhr.overrideMimeType("application/json");
    xhr.open('GET', resource, false);
    xhr.onreadystatechange = () => {
      if (xhr.readyState === 4 && xhr.status === 200) {
        callback(xhr.responseText);
      } else if (xhr.status === 400) {
        alert("No asset data written on the json");
      }
    };

    xhr.send(null);
  };

  /**
   * Formats the milliseconds to SCORM time.
   *
   * @return {string}
   */
  protected static convertToSCORMTime(milliseconds: number): string {
    let ScormTime = "";

    let HundredthsOfASecond;	//decrementing counter - work at the hundreths of a second level because that is all the precision that is required

    let Seconds;	// 100 hundreths of a seconds
    let Minutes;	// 60 seconds
    let Hours;		// 60 minutes
    let Days;		// 24 hours
    let Months;		// assumed to be an "average" month (figures a leap year every 4 years) = ((365*4) + 1) / 48 days - 30.4375 days per month
    let Years;		// assumed to be 12 "average" months

    const HUNDREDTHS_PER_SECOND = 100;
    const HUNDREDTHS_PER_MINUTE = HUNDREDTHS_PER_SECOND * 60;
    const HUNDREDTHS_PER_HOUR = HUNDREDTHS_PER_MINUTE * 60;
    const HUNDREDTHS_PER_DAY = HUNDREDTHS_PER_HOUR * 24;
    const HUNDREDTHS_PER_MONTH = HUNDREDTHS_PER_DAY * (((365 * 4) + 1) / 48);
    const HUNDREDTHS_PER_YEAR = HUNDREDTHS_PER_MONTH * 12;

    HundredthsOfASecond = Math.floor(milliseconds / 10);

    Years = Math.floor(HundredthsOfASecond / HUNDREDTHS_PER_YEAR);
    HundredthsOfASecond -= (Years * HUNDREDTHS_PER_YEAR);

    Months = Math.floor(HundredthsOfASecond / HUNDREDTHS_PER_MONTH);
    HundredthsOfASecond -= (Months * HUNDREDTHS_PER_MONTH);

    Days = Math.floor(HundredthsOfASecond / HUNDREDTHS_PER_DAY);
    HundredthsOfASecond -= (Days * HUNDREDTHS_PER_DAY);

    Hours = Math.floor(HundredthsOfASecond / HUNDREDTHS_PER_HOUR);
    HundredthsOfASecond -= (Hours * HUNDREDTHS_PER_HOUR);

    Minutes = Math.floor(HundredthsOfASecond / HUNDREDTHS_PER_MINUTE);
    HundredthsOfASecond -= (Minutes * HUNDREDTHS_PER_MINUTE);

    Seconds = Math.floor(HundredthsOfASecond / HUNDREDTHS_PER_SECOND);
    HundredthsOfASecond -= (Seconds * HUNDREDTHS_PER_SECOND);

    if (Years > 0) {
      ScormTime += Years + "Y";
    }
    if (Months > 0) {
      ScormTime += Months + "M";
    }
    if (Days > 0) {
      ScormTime += Days + "D";
    }

    //check to see if we have any time before adding the "T"
    if ((HundredthsOfASecond + Seconds + Minutes + Hours) > 0) {

      ScormTime += "T";

      if (Hours > 0) {
        ScormTime += Hours + "H";
      }

      if (Minutes > 0) {
        ScormTime += Minutes + "M";
      }

      if ((HundredthsOfASecond + Seconds) > 0) {
        ScormTime += Seconds;

        if (HundredthsOfASecond > 0) {
          ScormTime += "." + HundredthsOfASecond;
        }

        ScormTime += "S";
      }

    }

    if (ScormTime === "") {
      ScormTime = "0S";
    }

    ScormTime = "P" + ScormTime;

    return ScormTime;
  }

  protected collectAssets(assetData: any[]): Asset[] {
    if (assetData == null) {
      return [];
    }

    const result: Asset[] = [];

    for (let assetDatum of assetData) {
      const mimeType = assetDatum.substr(assetDatum.lastIndexOf('.') + 1, assetDatum.length);

      const assetName = assetDatum;
      const type = AssetUtil.getAssetType(mimeType.toUpperCase());
      result.push({assetName, type});
    }

    return result;
  }
}
