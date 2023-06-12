package mn.erin.lms.base.domain.usecase.exam;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.repository.ExamGroupRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamGroupInput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 * @author Temuulen Naranbold
 */
public class UpdateExamGroupTest
{
  private static final String ID = "id";
  private static final String ORGANIZATION_ID = "jarvis";
  private static final String PARENT_ID = "id";
  private static final String NAME = "test group";

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private OrganizationIdProvider organizationIdProvider;
  private ExamGroupRepository examGroupRepository;
  private ExamGroup examGroup;
  private UpdateExamGroup updateExamGroup;

  @Before
  public void setUp() throws LmsRepositoryException
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);
    examGroupRepository = mock(ExamGroupRepository.class);

    when(lmsServiceRegistry.getOrganizationIdProvider()).thenReturn(organizationIdProvider);
    when(organizationIdProvider.getOrganizationId()).thenReturn(ORGANIZATION_ID);
    when(lmsRepositoryRegistry.getExamGroupRepository()).thenReturn(examGroupRepository);

    examGroup = new ExamGroup(ExamGroupId.valueOf(ID), PARENT_ID, NAME, OrganizationId.valueOf("jarvis"), "");
    when(examGroupRepository.findById(any())).thenReturn(examGroup);

    updateExamGroup = new UpdateExamGroup(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void whenInput_isNull() throws UseCaseException
  {
    updateExamGroup.executeImpl(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenId_isNull() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(null, PARENT_ID, NAME, "");
    updateExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenId_isBlank() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput("", PARENT_ID, NAME, "");
    updateExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenGroup_notExists_withId() throws UseCaseException, LmsRepositoryException
  {
    ExamGroupInput input = new ExamGroupInput(ID, PARENT_ID, NAME, "");
    when(examGroupRepository.findById(any())).thenReturn(null);
    updateExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenName_isNull() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(ID, PARENT_ID, null, "");
    updateExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenName_isBlank() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(ID, PARENT_ID, "", "");
    updateExamGroup.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenRepository_isNull() throws UseCaseException
  {
    ExamGroupInput input = new ExamGroupInput(ID, PARENT_ID, NAME, "");
    when(lmsRepositoryRegistry.getExamGroupRepository()).thenReturn(null);
    updateExamGroup.executeImpl(input);
  }

  @Test
  public void whenSuccess() throws UseCaseException, LmsRepositoryException
  {
    ExamGroupInput input = new ExamGroupInput(ID, PARENT_ID, NAME, "");

    when(examGroupRepository.update(examGroup)).thenReturn(ID);

    Assert.assertEquals(ID, updateExamGroup.executeImpl(input));
  }
}
