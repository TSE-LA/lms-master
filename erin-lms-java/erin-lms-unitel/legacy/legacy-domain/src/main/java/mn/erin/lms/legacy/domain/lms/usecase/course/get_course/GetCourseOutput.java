/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mn.erin.lms.legacy.domain.lms.model.course.CourseNote;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetCourseOutput
{
  private String id;
  private String courseCategoryId;
  private String courseContentId;
  private String authorId;
  private String title;
  private String description;
  private String note;
  private String publishStatus;
  private Date createdDate;
  private Date modifiedDate;
  private UserGroupOutput userGroups;
  private Map<String, Object> properties;
  private List<CourseNote> courseNotes;
  private String isReadyToPublish;

  private String enrollmentState;

  public String getId()
  {
    return id;
  }

  public String getCourseCategory()
  {
    return courseCategoryId;
  }

  public String getCourseContentId()
  {
    return courseContentId;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public String getTitle()
  {
    return title;
  }

  public List<CourseNote> getCourseNotes()
  {
    return courseNotes;
  }

  public String getDescription()
  {
    return description;
  }

  public String getNote()
  {
    return note;
  }

  public String getPublishStatus()
  {
    return publishStatus;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public String getIsReadyToPublish()
  {
    return isReadyToPublish;
  }

  public void setIsReadyToPublish(String isReadyToPublish)
  {
    this.isReadyToPublish = isReadyToPublish;
  }

  public void setEnrollmentState(String enrollmentState)
  {
    this.enrollmentState = enrollmentState;
  }

  public String getEnrollmentState()
  {
    return enrollmentState;
  }

  public void setCourseCategory(String courseCategoryId) {
    this.courseCategoryId = courseCategoryId;
  }

  private GetCourseOutput()
  {
  }

  public UserGroupOutput getUserGroupOutput()
  {
    return userGroups;
  }

  public void setUserGroupOutput(UserGroupOutput userGroupOutput)
  {
    this.userGroups = userGroupOutput;
  }

  public static class Builder
  {
    private String id;
    private String courseCategoryId;
    private String courseContentId;
    private String authorId;
    private String title;
    private String description;
    private String note;
    private String publishStatus;
    private Date createdDate;
    private Date modifiedDate;
    private UserGroupOutput userGroups = new UserGroupOutput();
    private Map<String, Object> properties;
    private List<CourseNote> courseNotes;
    private String isReadyToPublish;

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder withCategory(String courseCategoryId)
    {
      this.courseCategoryId = courseCategoryId;
      return this;
    }

    public Builder withContent(String courseContentId)
    {
      this.courseContentId = courseContentId;
      return this;
    }

    public Builder withAuthor(String authorId)
    {
      this.authorId = authorId;
      return this;
    }

    public Builder withTitle(String title)
    {
      this.title = title;
      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;
      return this;
    }

    public Builder withNote(String note)
    {
      this.note = note;
      return this;
    }

    public Builder havingNotes(List<CourseNote> courseNotes)
    {
      this.courseNotes = courseNotes;
      return this;
    }

    public Builder withPublishStatus(String publishStatus)
    {
      this.publishStatus = publishStatus;
      return this;
    }

    public Builder withProperties(Map<String, Object> properties)
    {
      this.properties = properties;
      return this;
    }

    public Builder withUserGroups(Set<String> users, Set<String> groups)
    {
      this.userGroups.setUsers(users);
      this.userGroups.setGroups(groups);
      return this;
    }

    public Builder createdAt(Date createdDate)
    {
      this.createdDate = createdDate;
      return this;
    }

    public Builder modifiedAt(Date modifiedDate)
    {
      this.modifiedDate = modifiedDate;
      return this;
    }

    public Builder hasReadyToPublish(String isReadyToPublish)
    {
      this.isReadyToPublish = isReadyToPublish;
      return this;
    }

    public GetCourseOutput build()
    {
      GetCourseOutput output = new GetCourseOutput();
      output.id = this.id;
      output.courseCategoryId = this.courseCategoryId;
      output.courseContentId = this.courseContentId;
      output.authorId = this.authorId;
      output.title = this.title;
      output.description = this.description;
      output.note = this.note;
      output.publishStatus = this.publishStatus;
      output.properties = properties;
      output.userGroups = this.userGroups;
      output.createdDate = this.createdDate;
      output.modifiedDate = this.modifiedDate;
      output.courseNotes = this.courseNotes;
      output.isReadyToPublish = this.isReadyToPublish;

      return output;
    }
  }
}
