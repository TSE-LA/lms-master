package mn.erin.lms.legacy.domain.lms.usecase.course_group;

public class GetGroupCourseInput
{
  private final String groupId;

  public GetGroupCourseInput(String groupId)
  {
    this.groupId = groupId;
  }

  public String getGroupId()
  {
    return groupId;
  }
}
