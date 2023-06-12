package mn.erin.lms.base.domain.usecase.exam;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.GetExamInput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class GetAllExamInfoTest
{
  private static final String CATEGORY_ID = "categoryId";
  private static final String GROUP_ID = "groupId";
  private ExamRepository examRepository;
  private GetAllExamInfo getAllExamInfo;

  @Before
  public void setUp()
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    examRepository = mock(ExamRepository.class);
    when(lmsRepositoryRegistry.getExamRepository()).thenReturn(examRepository);

    getAllExamInfo = new GetAllExamInfo(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void whenInput_isNull() throws LmsRepositoryException, UseCaseException
  {
    getAllExamInfo.executeImpl(null);
  }

  @Test
  public void whenGroupAndCategory_bothBlank() throws LmsRepositoryException, UseCaseException
  {
    when(examRepository.getAllExam()).thenReturn(ExamTestUtils.getExams(5));
    Assert.assertEquals(5, getAllExamInfo.executeImpl(new GetExamInput("", "")).size());
  }

  @Test
  public void whenCategoryId_isBlank() throws LmsRepositoryException, UseCaseException
  {
    when(examRepository.listAllByGroup(ExamGroupId.valueOf(GROUP_ID))).thenReturn(ExamTestUtils.getExams(4));
    Assert.assertEquals(4, getAllExamInfo.executeImpl(new GetExamInput("", GROUP_ID)).size());
  }

  @Test
  public void whenGroupID_isBlank() throws LmsRepositoryException, UseCaseException
  {
    when(examRepository.listAllByCategory(ExamCategoryId.valueOf(CATEGORY_ID))).thenReturn(ExamTestUtils.getExams(2));
    Assert.assertEquals(2, getAllExamInfo.executeImpl(new GetExamInput(CATEGORY_ID, "")).size());
  }

  @Test
  public void whenGroupId_andCategoryId_bothGiven() throws LmsRepositoryException, UseCaseException
  {
    when(examRepository.listAllByCategoryAndGroup(ExamCategoryId.valueOf(CATEGORY_ID), ExamGroupId.valueOf(GROUP_ID)))
        .thenReturn(ExamTestUtils.getExams(3));
    Assert.assertEquals(3, getAllExamInfo.executeImpl(new GetExamInput(CATEGORY_ID, GROUP_ID)).size());
  }

  @Test
  public void whenCategoryId_isGiven() throws LmsRepositoryException, UseCaseException
  {
    when(examRepository.listAllByCategory(ExamCategoryId.valueOf(CATEGORY_ID))).thenReturn(ExamTestUtils.getExams(3));
    Assert.assertEquals(3, getAllExamInfo.executeImpl(new GetExamInput(CATEGORY_ID, null)).size());
  }

  @Test
  public void whenGroupId_isGiven() throws LmsRepositoryException, UseCaseException
  {
    when(examRepository.listAllByGroup(ExamGroupId.valueOf(GROUP_ID))).thenReturn(ExamTestUtils.getExams(3));
    Assert.assertEquals(3, getAllExamInfo.executeImpl(new GetExamInput(null, GROUP_ID)).size());
  }

  @Test
  public void whenGroupAndCategory_bothNull() throws LmsRepositoryException, UseCaseException
  {
    when(examRepository.getAllExam()).thenReturn(ExamTestUtils.getExams(3));
    Assert.assertEquals(3, getAllExamInfo.executeImpl(new GetExamInput(null, null)).size());
  }
}
