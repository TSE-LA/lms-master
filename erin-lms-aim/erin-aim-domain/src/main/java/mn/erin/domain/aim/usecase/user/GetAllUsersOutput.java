package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mn.erin.domain.aim.model.user.UserAggregate;

public class GetAllUsersOutput
{
  private final List<UserAggregate> allUserAggregates;

  public GetAllUsersOutput(Collection<UserAggregate> allUserAggregates)
  {
    this.allUserAggregates = new ArrayList<>(allUserAggregates);
  }

  public List<UserAggregate> getAllUserAggregates()
  {
    return Collections.unmodifiableList(allUserAggregates);
  }
}
