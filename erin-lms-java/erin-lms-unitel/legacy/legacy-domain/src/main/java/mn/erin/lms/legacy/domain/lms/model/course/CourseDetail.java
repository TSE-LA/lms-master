/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.course;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.base.model.ValueObject;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CourseDetail implements ValueObject<CourseDetail>, Serializable
{
  private static final long serialVersionUID = -182857433484157488L;

  private static final String CREATED_DATE_ERR_MSG = "Created date cannot be greater than modified date";

  private PublishStatus publishStatus = PublishStatus.UNPUBLISHED;

  private String title;

  private Map<String, Serializable> properties = new TreeMap<>();
  private String description;
  private List<CourseNote> notes = new ArrayList<>();
  private Date createdDate;
  private Date modifiedDate;

  public CourseDetail(String title)
  {
    this.title = Validate.notBlank(title, "Title cannot be null or blank!");
  }

  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }
  public void addNote(CourseNote courseNote)
  {
    if (courseNote != null)
    {
      this.notes.add(courseNote);
    }
  }

  public String getLatestNote()
  {
    if (this.notes.isEmpty())
    {
      return "";
    }

    CourseNote courseNote = notes.get(notes.size() - 1);
    return courseNote.getNote();
  }

  public void addProperty(String key, Serializable value)
  {
    if (!StringUtils.isBlank(key) && value != null)
    {
      properties.put(key, value);
    }
  }

  public void changePublishStatus(PublishStatus publishStatus)
  {
    if (publishStatus != null)
    {
      this.publishStatus = publishStatus;
    }
  }

  public String getTitle()
  {
    return title;
  }

  public String getDescription()
  {
    return description;
  }

  public List<CourseNote> getNotes()
  {
    return notes;
  }

  public void setNotes(List<CourseNote> notes)
  {
    this.notes = notes;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public PublishStatus getPublishStatus()
  {
    return publishStatus;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate)
  {
    if (this.modifiedDate != null && DateTimeUtils.compare(this.modifiedDate, createdDate) < -1)
    {
      throw new IllegalStateException(CREATED_DATE_ERR_MSG);
    }

    this.createdDate = createdDate;
  }

  public void changeCourseTitle(String newTitle)
  {
    if (!StringUtils.isBlank(newTitle))
    {
      this.title = newTitle;
    }
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    if (this.createdDate != null && DateTimeUtils.compare(modifiedDate, this.createdDate) < -1)
    {
      throw new IllegalStateException(CREATED_DATE_ERR_MSG);
    }
    this.modifiedDate = modifiedDate;
  }

  @Override
  public boolean sameValueAs(CourseDetail other)
  {
    return other != null
        && (this.properties.equals(other.properties)
        && this.description.equals(other.description)
        && this.publishStatus.equals(other.publishStatus))
        && this.properties.equals(other.properties);
  }
}
