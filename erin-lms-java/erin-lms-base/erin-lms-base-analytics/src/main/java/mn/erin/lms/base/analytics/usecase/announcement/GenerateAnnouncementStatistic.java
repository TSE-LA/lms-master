package mn.erin.lms.base.analytics.usecase.announcement;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMembership;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.analytics.usecase.dto.AnnouncementAnalytic;
import mn.erin.lms.base.analytics.usecase.dto.AnnouncementUserInfo;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntimeStatus;
import mn.erin.lms.base.domain.repository.AnnouncementRuntimeRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

public class GenerateAnnouncementStatistic implements UseCase<String, AnnouncementAnalytic>
{
  private final AnnouncementRuntimeRepository announcementRuntimeRepository;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final MembershipRepository membershipRepository;
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final LmsDepartmentService lmsDepartmentService;

  public GenerateAnnouncementStatistic(
      AnnouncementRuntimeRepository announcementRuntimeRepository, LmsServiceRegistry lmsServiceRegistry, MembershipRepository membershipRepository,
      AimRepositoryRegistry aimRepositoryRegistry, LmsDepartmentService lmsDepartmentService)
  {
    this.announcementRuntimeRepository = Objects.requireNonNull(announcementRuntimeRepository);
    this.lmsServiceRegistry = Objects.requireNonNull(lmsServiceRegistry);
    this.membershipRepository = Objects.requireNonNull(membershipRepository);
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry);
    this.lmsDepartmentService = Objects.requireNonNull(lmsDepartmentService);
  }

  @Override
  public AnnouncementAnalytic execute(String input) throws UseCaseException
  {
    try
    {
      List<AnnouncementRuntime> runtimeData = announcementRuntimeRepository.findByAnnouncementId(input);
      List<AnnouncementUserInfo> users = new ArrayList<>();
      int count = 0;
      for (AnnouncementRuntime runtime : runtimeData)
      {
        String learnerId = runtime.getLearnerId();
        if (runtime.getState() == AnnouncementRuntimeStatus.VIEWED)
        {
          AnnouncementUserInfo userInfo = getViewedUser(learnerId);
          String viewedDate = DateTimeUtils.toShortIsoString(toDate(runtime.getViewedDate()));
          userInfo.setViewedDate(viewedDate);
          users.add(userInfo);
          count++;
        }
      }

      int totalView = runtimeData.size() == 0 ? 0 : count * 100 / runtimeData.size();
      return new AnnouncementAnalytic(users, totalView);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private AnnouncementUserInfo getViewedUser(String userName) throws UseCaseException
  {
    UserInfo user = getUserInfo(userName);
    String firstName = user.getFirstName();
    String lastName = user.getLastName();

    String departmentName = getGroupName(userName);

    return new AnnouncementUserInfo(userName, firstName, lastName, departmentName);
  }

  private UserInfo getUserInfo(String userName) throws UseCaseException
  {
    UserIdentity userIdentity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(userName, UserIdentitySource.OWN);
    if (userIdentity == null)
    {
      throw new UseCaseException("User identity not found!");
    }

    UserProfile userProfile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(userIdentity.getUserId());

    if (userProfile == null)
    {
      throw new UseCaseException("User profile not found!");
    }

    return userProfile.getUserInfo();
  }

  private String getGroupName(String userName) throws UseCaseException
  {
    GetUserMembership getUserMembership = new GetUserMembership(lmsServiceRegistry.getAuthenticationService(), lmsServiceRegistry.getAuthorizationService(),
        membershipRepository);
    GetMembershipOutput membership = getUserMembership.execute(new GetUserMembershipsInput(userName));

    if (membership == null)
    {
      throw new UseCaseException("No membership found for user " + userName);
    }

    String groupId = membership.getGroupId();

    return lmsDepartmentService.getDepartmentName(groupId);
  }

  private Date toDate(LocalDateTime localDateTime)
  {
    return Timestamp.valueOf(localDateTime);
  }
}
