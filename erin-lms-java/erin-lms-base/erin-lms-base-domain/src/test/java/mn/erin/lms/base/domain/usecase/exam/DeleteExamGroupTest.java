package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.ExamGroupRepository;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 * @author Temuulen Naranbold
 */
public class DeleteExamGroupTest
{
  private static final String ID = "id";
  private static final String ORGANIZATION_ID = "jarvis";
  private static final String PARENT_ID = "id";
  private static final String NAME = "test group";

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private ExamGroupRepository examGroupRepository;
  private ExamRepository examRepository;
  private ExamGroup examGroup;
  private OrganizationIdProvider organizationIdProvider;
  private DeleteExamGroup deleteExamGroup;

  @Before
  public void setUp() throws LmsRepositoryException
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    examGroupRepository = mock(ExamGroupRepository.class);
    examRepository = mock(ExamRepository.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);

    when(lmsRepositoryRegistry.getExamGroupRepository()).thenReturn(examGroupRepository);
    when(lmsRepositoryRegistry.getExamRepository()).thenReturn(examRepository);
    when(examRepository.listAllByGroup(any(ExamGroupId.class))).thenReturn(null);
    when(examGroupRepository.findByParentIdAndOrganizationId(any(), any())).thenReturn(null);
    when(lmsServiceRegistry.getOrganizationIdProvider()).thenReturn(organizationIdProvider);
    when(organizationIdProvider.getOrganizationId()).thenReturn(ORGANIZATION_ID);

    examGroup = new ExamGroup(ExamGroupId.valueOf(ID), PARENT_ID, NAME, OrganizationId.valueOf(ORGANIZATION_ID), "");
    when(examGroupRepository.findById(any())).thenReturn(examGroup);

    deleteExamGroup = new DeleteExamGroup(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void whenInput_isNull() throws UseCaseException
  {
    deleteExamGroup.executeImpl(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenId_isBlank() throws UseCaseException
  {
    deleteExamGroup.executeImpl("");
  }

  @Test(expected = UseCaseException.class)
  public void whenGroup_doesNotExists() throws UseCaseException, LmsRepositoryException
  {
    when(examGroupRepository.findById(any())).thenReturn(null);
    deleteExamGroup.executeImpl(ID);
  }

  @Test(expected = UseCaseException.class)
  public void whenGroup_hasExams() throws UseCaseException
  {
    List<Exam> exams = new ArrayList<>();
    Exam exam = new Exam.Builder(ExamId.valueOf("id"), "admin", new Date(), "Test").build();
    exams.add(exam);
    when(examRepository.listAllByGroup(any(ExamGroupId.class))).thenReturn(exams);
    deleteExamGroup.executeImpl(ID);
  }

  @Test(expected = UseCaseException.class)
  public void whenGroup_hasChildGroups() throws UseCaseException
  {
    List<ExamGroup> groups = new ArrayList<>();
    groups.add(examGroup);
    when(examGroupRepository.findByParentIdAndOrganizationId(any(), any())).thenReturn(groups);
    deleteExamGroup.executeImpl(ID);
  }

  @Test(expected = UseCaseException.class)
  public void whenRepository_isNull() throws UseCaseException
  {
    when(lmsRepositoryRegistry.getExamGroupRepository()).thenReturn(null);
    deleteExamGroup.executeImpl(ID);
  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    when(examGroupRepository.delete(any())).thenReturn(true);
    Assert.assertTrue(deleteExamGroup.executeImpl(ID));
  }
}
