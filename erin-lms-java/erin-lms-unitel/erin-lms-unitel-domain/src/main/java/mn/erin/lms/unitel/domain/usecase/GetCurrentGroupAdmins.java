package mn.erin.lms.unitel.domain.usecase;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.unitel.domain.model.user.UnitelAdmin;
import mn.erin.lms.unitel.domain.usecase.dto.AdminDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { UnitelAdmin.class })
public class GetCurrentGroupAdmins extends LmsUseCase<Void, Set<AdminDto>>
{
  private final LmsDepartmentService departmentService;
  private final LmsUserService userService;

  public GetCurrentGroupAdmins(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
    this.userService = lmsServiceRegistry.getLmsUserService();
  }

  @Override
  protected Set<AdminDto> executeImpl(Void input)
  {
    String currentDepartmentId = departmentService.getCurrentDepartmentId();
    Set<String> allGroups = departmentService.getSubDepartments(currentDepartmentId);
    allGroups.add(currentDepartmentId);

    Set<AdminDto> admins = new HashSet<>();
    for (String group : allGroups)
    {
      admins.addAll(departmentService.getInstructors(group).stream().map(admin -> {
        ContactInfo contact = userService.getContactInfo(admin);
        if(contact == null)
        {
          return new AdminDto(admin, null, null);
        }
        return new AdminDto(admin, contact.getPhone(), contact.getEmail());
      }).collect(Collectors.toSet()));
    }

    return admins;
  }
}
