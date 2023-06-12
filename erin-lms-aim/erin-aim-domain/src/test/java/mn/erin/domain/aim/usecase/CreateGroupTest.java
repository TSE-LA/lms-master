package mn.erin.domain.aim.usecase;

import mn.erin.domain.aim.BaseUseCaseTestAbstract;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.usecase.group.CreateGroup;
import mn.erin.domain.aim.usecase.group.CreateGroupInput;
import mn.erin.domain.aim.usecase.group.CreateGroupOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Zorig
 */
// TODO : fix failing tests
public class CreateGroupTest extends BaseUseCaseTestAbstract
{
  private static final String DESCRIPTION = "-TEST_DESCRIPTION-";

  @Mock(name = "groupRepository")
  private GroupRepository groupRepository;

  private CreateGroup createGroup;

  @Before
  public void setUpGroup()
  {
    createGroup = new CreateGroup(authenticationService, authorizationService, groupRepository);
    mockRequiredAuthServices();
  }

  @Test(expected = UseCaseException.class)
  public void throwsExceptionWhenParentIdIsBlank() throws UseCaseException
  {
    String parentId = "";
    String name = "12";
    String id = "1";
    String description = "alibaba";

    CreateGroupInput input = new CreateGroupInput(parentId, id, name, description);
    createGroup.execute(input);
  }

  @Ignore
  @Test(expected = UseCaseException.class)
  public void throwsExceptionWhenNameIsBlank() throws UseCaseException
  {
    String parentId = "12";
    String name = "";
    String id = "1";

    CreateGroupInput input = new CreateGroupInput(parentId, id, name, DESCRIPTION);

    createGroup.execute(input);
  }

  @Test
  @Ignore
  public void createsRootGroupWhenParentIdIsNull() throws UseCaseException
  {
    String parentId = null;
    String tenantId = "-erin-";
    String name = "12";
    String id = "1";
    CreateGroupInput input = new CreateGroupInput(parentId, id, name, DESCRIPTION);

    CreateGroupOutput output = createGroup.execute(input);

    Assert.assertEquals("G1", output.getId());
  }

  @Test
  @Ignore
  public void createsGroupByInputParameter() throws AimRepositoryException, UseCaseException
  {
    String parentId = "12";
    String tenantId = "-erin-";
    String name = "12";
    String id = "1";

    CreateGroupInput input = new CreateGroupInput(parentId, id, name, DESCRIPTION);

    createGroup.execute(input);

    Mockito.verify(groupRepository).createGroup(null, parentId, name, TenantId.valueOf(tenantId));
  }

  @Ignore
  @Test
  public void getsNthSiblingTest() throws AimRepositoryException, UseCaseException
  {
    String number = "GR1";
    String parentId = "12";
    String name = "12";
    String id = "1";

    TenantId tenantId = TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId());

    Mockito.when(groupRepository.getNthSibling(parentId)).thenReturn(3);
    Mockito.when(groupRepository.createGroup(name, parentId, number, tenantId))
      .thenReturn(new Group(GroupId.valueOf(number), GroupId.valueOf(parentId), tenantId, name));

    CreateGroupInput input = new CreateGroupInput(parentId, id, name, number);

    Assert.assertEquals(number, createGroup.execute(input).getId());
  }
}

