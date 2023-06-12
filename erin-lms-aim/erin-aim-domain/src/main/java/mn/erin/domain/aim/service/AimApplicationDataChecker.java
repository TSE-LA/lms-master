package mn.erin.domain.aim.service;

import java.util.Map;

public interface AimApplicationDataChecker
{
  UserDataResult hasAssociatedData(String username);

  Map<String, UserDataResult> getUserCourseData();

}
