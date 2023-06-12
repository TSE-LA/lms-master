package mn.erin.lms.base.aim;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * @author Bat-Erdene Tsogoo.
 */
public class DepartmentServiceImpl extends BaseLmsAimService implements LmsDepartmentService
{
  public DepartmentServiceImpl(AccessIdentityManagement accessIdentityManagement)
  {
    super(accessIdentityManagement);
  }

  @Override
  public String getCurrentDepartmentId()
  {
    return accessIdentityManagement.getCurrentUserDepartmentId();
  }

  @Override
  public String getRole(String userId)
  {
    return accessIdentityManagement.getRole(userId);
  }

  @Override
  public String getDepartmentId(String departmentName)
  {
    return accessIdentityManagement.getDepartmentId(departmentName);
  }

  @Override
  public String getDepartmentName(String departmentId)
  {
    return accessIdentityManagement.getDepartmentName(departmentId);
  }

  @Override
  public Set<String> getSubDepartments(String departmentId)
  {
    try
    {
      return accessIdentityManagement.getSubDepartments(departmentId);
    }
    catch (NoSuchElementException e)
    {
      return Collections.emptySet();
    }
  }

  @Override
  public Set<String> getParentDepartments(String departmentId)
  {
    try
    {
      return accessIdentityManagement.getParentDepartments(departmentId);
    }
    catch (NoSuchElementException e)
    {
      return Collections.emptySet();
    }
  }

  @Override
  public Set<String> getInstructors(String departmentId)
  {
    return accessIdentityManagement.getInstructors(departmentId);
  }

  @Override
  public Set<String> getLearners(String departmentId)
  {
    try
    {
      return accessIdentityManagement.getLearners(departmentId);
    }
    catch (NoSuchElementException e)
    {
      return Collections.emptySet();
    }
  }

  @Override
  public Set<String> getAllLearners(String departmentId)
  {
    try
    {
      return accessIdentityManagement.getAllLearners(departmentId);
    }
    catch (NoSuchElementException e)
    {
      return Collections.emptySet();
    }
  }

  @Override
  public Set<String> getLearnersByRole(String departmentId, String role)
  {
    try
    {
      return accessIdentityManagement.getLearnersByRole(departmentId, role);
    }
    catch (NoSuchElementException e)
    {
      return Collections.emptySet();
    }
  }

  @Override
  public Set<String> getParentLearnersByRole(String departmentId, String role)
  {
    try
    {
      return accessIdentityManagement.getParentGroupLearnersByRole(departmentId, role);
    }
    catch (NoSuchElementException e)
    {
      return Collections.emptySet();
    }
  }
}
