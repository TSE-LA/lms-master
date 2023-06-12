package mn.erin.lms.base.mongo.document.course;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Erdenetulga
 */
@Document
public class MongoCourseSuggestedUsers
{
  private String courseId;
  private Set<String> suggestedUsers;
  private Set<String> suggestedGroups;

  private MongoCourseSuggestedUsers()
  {
  }

  public MongoCourseSuggestedUsers(String courseId)
  {
    this.courseId = courseId;
  }

  public MongoCourseSuggestedUsers(String courseId, Set<String> suggestedUsers)
  {
    this.courseId = courseId;
    this.suggestedUsers = suggestedUsers;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Set<String> getSuggestedUsers()
  {
    return suggestedUsers;
  }

  public void setSuggestedUsers(Set<String> suggestedUsers)
  {
    this.suggestedUsers = suggestedUsers;
  }

  public void addSuggestedUsers(Set<String> suggestedUsers)
  {
    this.suggestedUsers.addAll(suggestedUsers);
  }

  public void removeSuggestedUsers(Set<String> suggestedUsers)
  {
    this.suggestedUsers.removeAll(suggestedUsers);
  }

  public Set<String> getSuggestedGroups()
  {
    return suggestedGroups;
  }

  public void setSuggestedGroups(Set<String> suggestedGroups)
  {
    this.suggestedGroups = suggestedGroups;
  }

  public void addSuggestedGroups(Set<String> suggestedGroups)
  {
    this.suggestedGroups.addAll(suggestedGroups);
  }
}
