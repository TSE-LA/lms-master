package mn.erin.lms.jarvis.mongo.document.report;


import java.util.HashSet;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class MongoCourseSuggestedUsers
{
  private String courseId;
  private Set<String> suggestedUsers = new HashSet<>();
  private Set<String> suggestedGroups = new HashSet<>();

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

  public Set<String> getSuggestedGroups()
  {
    return suggestedGroups;
  }

  public void removeSuggestedUsers(Set<String> suggestedUsers)
  {
    this.suggestedUsers.removeAll(suggestedUsers);
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

