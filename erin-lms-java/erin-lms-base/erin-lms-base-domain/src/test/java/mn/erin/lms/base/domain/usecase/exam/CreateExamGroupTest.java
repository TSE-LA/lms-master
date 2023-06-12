package mn.erin.lms.base.domain.usecase.exam;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.repository.ExamGroupRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamGroupInput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class CreateExamGroupTest
{
  private static final String ORGANIZATION_ID = "jarvis";
  private static final String PARENT_ID = "id";
  private static final String NAME = "test group";

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private OrganizationIdProvider organizationIdProvider;
  private ExamGroupRepository examGroupRepository;

  private CreateExamGroup createExamGroup;

  @Before
  public void setUp()
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);
    examGroupRepository = mock(ExamGroupRepository.class);
    when(lmsServiceRegistry.getOrganizationIdProvider()).thenReturn(organizationIdProvider);
    when(organizationIdProvider.getOrganizationId()).thenReturn(ORGANIZATION_ID);
    when(lmsRepositoryRegistry.getExamGroupRepository()).thenReturn(examGroupRepository);

    createExamGroup = new CreateExamGroup(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void whenInput_isNull() throws UseCaseException
  {
    createExamGroup.executeImpl(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenName_isNull() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(PARENT_ID, null, "");
    createExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenName_isBlank() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(PARENT_ID, "", "");
    createExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenOrganizationId_isNull() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(PARENT_ID, NAME, "");
    when(organizationIdProvider.getOrganizationId()).thenReturn(null);
    createExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenGroupRepository_isNull() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(PARENT_ID, NAME, "");
    when(lmsRepositoryRegistry.getExamGroupRepository()).thenReturn(null);
    createExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenCreate_returnsNull() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(PARENT_ID, NAME, "");
    when(examGroupRepository.create(
        PARENT_ID, NAME, OrganizationId.valueOf("jarvis"), ""
    )).thenReturn(null);

    createExamGroup.executeImpl(input);
  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(null, NAME, "");
    String id = "groupId";
    ExamGroup group = new ExamGroup(ExamGroupId.valueOf(id), null, NAME, OrganizationId.valueOf("jarvis"), "");

    when(examGroupRepository.create(
        null, NAME, OrganizationId.valueOf("jarvis"), ""
    )).thenReturn(group);

    Assert.assertEquals(createExamGroup.executeImpl(input), id);
  }

  @Test
  public void whenSuccess_withParentId() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(PARENT_ID, NAME, "");
    String id = "groupId";
    ExamGroup group = new ExamGroup(ExamGroupId.valueOf(id), PARENT_ID, NAME, OrganizationId.valueOf("jarvis"), "");

    when(examGroupRepository.create(
        PARENT_ID, NAME, OrganizationId.valueOf("jarvis"), ""
    )).thenReturn(group);

    Assert.assertEquals(createExamGroup.executeImpl(input), id);
  }
}
