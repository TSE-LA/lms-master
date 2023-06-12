package mn.erin.lms.base.domain.usecase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentPath;
import mn.erin.lms.base.domain.util.DepartmentPathUtil;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.search.SearchInput;
import mn.erin.lms.base.domain.model.search.UsersWithGroup;
import mn.erin.lms.base.domain.model.search.UserSearchOutput;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.user.GetAllMyUsers;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesOutput;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { Instructor.class, Supervisor.class, Manager.class })
public class SearchUser extends LmsUseCase<SearchInput, UserSearchOutput>
{
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final GroupRepository groupRepository;
  private final LmsDepartmentService departmentService;

  public SearchUser(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry, AimRepositoryRegistry aimRepositoryRegistry)
  {
    super(Validate.notNull(lmsRepositoryRegistry), Validate.notNull(lmsServiceRegistry));
    this.aimRepositoryRegistry = Validate.notNull(aimRepositoryRegistry);
    this.groupRepository = aimRepositoryRegistry.getGroupRepository();
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  protected UserSearchOutput executeImpl(SearchInput input) throws UseCaseException
  {
    Validate.notNull(input);

    String currentDepartmentId = departmentService.getCurrentDepartmentId();
    Set<String> departments = departmentService.getSubDepartments(currentDepartmentId);
    Set<Group> groups = groupRepository.getAllByIds(departments);
    Map<String, DepartmentPath> groupIdPathMap = DepartmentPathUtil.getPath(currentDepartmentId, groups);

    GetAllMyUsers getAllMyUsers = new GetAllMyUsers(lmsRepositoryRegistry, lmsServiceRegistry, aimRepositoryRegistry);
    List<GetUserByRolesOutput> allUsers = getAllMyUsers.execute(false);

    departments.remove(currentDepartmentId);
    String searchString = input.getSearchKey().toLowerCase();

    List<UsersWithGroup> usersWithGroups = new ArrayList<>();
    Set<String> usersWithoutGroup = new HashSet<>();
    String username;
    for (GetUserByRolesOutput user : allUsers)
    {
      username = user.getUser().getUsername();
      if (username.toLowerCase().contains(searchString))
      {
        if (user.getMembership() != null && departments.contains(user.getMembership().getGroupId().getId()))
        {
          DepartmentPath departmentPath = groupIdPathMap.get(user.getMembership().getGroupId().getId());
          usersWithGroups
              .add(new UsersWithGroup(username, departmentPath.getPath(), departmentPath.getLevel()));
        }
        else if (user.getMembership() == null)
        {
          usersWithoutGroup.add(username);
        }
      }
    }
    input.getSelectedUsers().forEach(usersWithoutGroup::remove);

    return new UserSearchOutput(usersWithoutGroup, usersWithGroups);
  }
}
