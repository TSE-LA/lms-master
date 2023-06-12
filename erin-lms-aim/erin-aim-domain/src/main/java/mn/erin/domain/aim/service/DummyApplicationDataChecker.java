package mn.erin.domain.aim.service;

import java.util.Collections;
import java.util.Map;

public class DummyApplicationDataChecker implements AimApplicationDataChecker
{
  @Override
  public UserDataResult hasAssociatedData(String username)
  {
    return new UserDataResult(false);
  }

  @Override
  public Map<String, UserDataResult> getUserCourseData()
  {
    return Collections.emptyMap();
  }
}
