/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.model;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMTime implements ValueObject<SCORMTime>
{
  private static final String SCORM_TIME_PATTERN = "PT?(([0-9]+)H)?(([0-9]+)M)?(([0-9]+)(\\.[0-9]+)?S)?";

  private String value;

  public SCORMTime(String value)
  {
    this.value = validate(value);
  }

  @Override
  public boolean sameValueAs(SCORMTime other)
  {
    return this.value.equals(other.value);
  }

  public void add(SCORMTime other)
  {
    try
    {
      this.value = convertMillisecondsToSCORMTime(
          convertSCORMTimeToMilliseconds(this.value) + convertSCORMTimeToMilliseconds(other.value)
      );
    }
    catch (NumberFormatException e)
    {
      e.printStackTrace();
    }
  }

  public String getValue()
  {
    return value;
  }

  public static String convertToHumanReadableTime(String scormTime)
  {
    String hours = "00";
    String minutes = "00";
    String seconds;

    int indexOfT = scormTime.indexOf('T');

    if (scormTime.contains("H"))
    {
      int indexOfH = scormTime.indexOf('H');
      hours = scormTime.substring(indexOfT + 1, indexOfH);
      scormTime = scormTime.substring(0, indexOfT + 1) + scormTime.substring(indexOfH + 1);
    }
    if (scormTime.contains("M"))
    {
      int indexOfM = scormTime.indexOf('M');
      minutes = scormTime.substring(indexOfT + 1, indexOfM);
      scormTime = scormTime.substring(0, indexOfT + 1) + scormTime.substring(indexOfM + 1);
    }
    int indexOfS = scormTime.indexOf('S');
    seconds = scormTime.substring(indexOfT + 1, indexOfS);
    seconds = seconds.indexOf('.') > -1 ? seconds.substring(0, seconds.indexOf('.')) : seconds;

    hours = hours.length() == 1 ? "0" + hours : hours;
    minutes = minutes.length() == 1 ? "0" + minutes : minutes;
    seconds = seconds.length() == 1 ? "0" + seconds : seconds;

    return hours + ":" + minutes + ":" + seconds;
  }

  private static String validate(String scormTime)
  {
    Validate.notNull(scormTime, "SCORM Time cannot be null or blank!");
    if (!scormTime.matches(SCORM_TIME_PATTERN))
    {
      throw new IllegalArgumentException("Invalid SCORM Time Format!");
    }

    return scormTime;
  }

  private static long convertSCORMTimeToMilliseconds(String scormTime)
  {
    String humanReadableTimeRepresentation = convertToHumanReadableTime(scormTime);

    String[] parts = humanReadableTimeRepresentation.split(":");

    int hours = Integer.parseInt(parts[0]);
    int minutes = Integer.parseInt(parts[1]);
    float seconds = Float.parseFloat(parts[2]);

    return (long) ((hours * 60 * 60) + (minutes * 60) + seconds) * 1000;
  }

  private static String convertMillisecondsToSCORMTime(long milliseconds)
  {
    int seconds = (int) milliseconds / 1000;
    int hundrethsOfASecond = ((int) milliseconds) % 1000;

    int hours = seconds / 3600;
    seconds -= hours * 3600;
    int minutes = seconds / 60;
    seconds -= minutes * 60;

    String scormTime = "PT";

    if (hours > 0)
    {
      scormTime += hours + "H";
    }

    if (minutes > 0)
    {
      scormTime += minutes + "M";
    }

    scormTime += seconds + "." + hundrethsOfASecond + "S";
    return scormTime;
  }
}
