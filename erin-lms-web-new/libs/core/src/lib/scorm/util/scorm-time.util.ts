export class ScormTimeUtil {
  /**
   * Formats the milliseconds to SCORM time.
   *
   * @return {string}
   */
  public static convertToSCORMTime(milliseconds: number): string {
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
      ScormTime = "T0S";
    }

    ScormTime = "P" + ScormTime;

    return ScormTime;
  };
}
