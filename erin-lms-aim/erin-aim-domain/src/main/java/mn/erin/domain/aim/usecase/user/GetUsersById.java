package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.UserProfileRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.usecase.UseCase;

/**
 * @author Erdenetulga
 */
public class GetUsersById implements UseCase<List<String>, List<GetUserOutput>>
{
  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;

  public GetUsersById(UserRepository userRepository, UserProfileRepository userProfileRepository)
  {
    this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null!");
    this.userProfileRepository = userProfileRepository;
  }

  @Override
  public List<GetUserOutput> execute(List<String> input)
  {
    List<GetUserOutput> result = new ArrayList<>();
    for (String userId : input)
    {
      User user = userRepository.findById(UserId.valueOf(userId));
      if (user != null)
      {
        result.add(convert(user));
      }
    }
    return result;
  }

  public GetUserOutput convert(User user)
  {
    UserContact contact = userProfileRepository.findById(user.getUserId()).getUserContact();
    return new GetUserOutput.Builder(user.getUserId().getId())
      .withTenant(user.getTenantId().getId())
      .withEmail(contact.getEmail())
      .withPhoneNumber(contact.getPhoneNumber())
      .build();
  }
}
