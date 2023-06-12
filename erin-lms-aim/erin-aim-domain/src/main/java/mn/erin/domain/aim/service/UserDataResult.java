package mn.erin.domain.aim.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserDataResult
{
  public final boolean affirmative;
  private final Collection<String> courses = new HashSet<>();
  private final Collection<String> allRuntimeData = new HashSet<>();

  public UserDataResult(boolean affirmative)
  {
    this.affirmative = affirmative;
  }

  public void addCourse(String courseId)
  {
    courses.add(courseId);
  }

  public void addRuntimeData(Collection<String> runtimeData)
  {
    allRuntimeData.addAll(runtimeData);
  }

  public Collection<String> getCourses()
  {
    return Collections.unmodifiableCollection(courses);
  }

  public Collection<String> getAllRuntimeData()
  {
    return Collections.unmodifiableCollection(allRuntimeData);
  }

  public String getMessage()
  {
    return "User has courses: " + this.courses.size() + " runtimeData: " + this.allRuntimeData.size();
  }

  public void addCourses(Set<String> courseEnrollments)
  {
    courses.addAll(courseEnrollments);
  }
}
