/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.domain.util;

/**
 * @author Munkh
 */
public class GifUtil
{
  private static final String START_TIME = "00:00:00";
  private static final String DURATION_DEFAULT = "00:00:05";
  private static final int FRAME_PER_SECOND = 6;
  private static final int WIDTH_DEFAULT = 512;
  private static final int HEIGHT_DEFAULT = 288;
  private static final int OUTPUT_FRAME = 1;

  private final String name;
  private String startTime;
  private String duration;
  private int fps;
  private int width;
  private int height;
  private int frameQuantity;
  private String output;

  public GifUtil(String name)
  {
    this.name = name;
    this.startTime = START_TIME;
    this.duration = DURATION_DEFAULT;
    this.fps = FRAME_PER_SECOND;
    this.width = WIDTH_DEFAULT;
    this.height = HEIGHT_DEFAULT;
    this.frameQuantity = OUTPUT_FRAME;
    this.output = name;
  }

  public GifUtil(String name, int width, int height)
  {
    this.name = name;
    this.startTime = START_TIME;
    this.duration = DURATION_DEFAULT;
    this.fps = FRAME_PER_SECOND;
    this.width = width;
    this.height = height;
    this.frameQuantity = OUTPUT_FRAME;
    this.output = name;
  }

  public GifUtil(String name, String startTime, String duration, int fps, int width, int height, int frameQuantity, String output)
  {
    this.name = name;
    this.startTime = startTime;
    this.duration = duration;
    this.fps = fps;
    this.width = width;
    this.height = height;
    this.frameQuantity = frameQuantity;
    this.output = output;
  }

  public String getName()
  {
    return name;
  }

  public String getStartTime()
  {
    return startTime;
  }

  public void setStartTime(String startTime)
  {
    this.startTime = startTime;
  }

  public String getDuration()
  {
    return duration;
  }

  public void setDuration(String duration)
  {
    this.duration = duration;
  }

  public int getFps()
  {
    return fps;
  }

  public void setFps(int fps)
  {
    this.fps = fps;
  }

  public int getWidth()
  {
    return width;
  }

  public void setWidth(int width)
  {
    this.width = width;
  }

  public int getHeight()
  {
    return height;
  }

  public void setHeight(int height)
  {
    this.height = height;
  }

  public int getFrameQuantity()
  {
    return frameQuantity;
  }

  public void setFrameQuantity(int frameQuantity)
  {
    this.frameQuantity = frameQuantity;
  }

  public String getOutput()
  {
    return output;
  }

  public void setOutput(String output)
  {
    this.output = output;
  }
}
