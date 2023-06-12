package mn.erin.lms.base.domain.usecase.category;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.QuestionCategory;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.category.dto.QuestionCategoryInput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class CreateQuestionCategoryTest
{
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String PARENT_ID = "parentId";
  private static final String NAME = "name";
  private static final String DESCRIPTION = "description";
  private static final String ID = "id";
  private static final int INDEX = 1;

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private QuestionCategoryRepository questionCategoryRepository;
  private OrganizationIdProvider organizationIdProvider;

  private CreateQuestionCategory createQuestionCategory;

  @Before
  public void setUp()
  {
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);

    when(lmsServiceRegistry.getOrganizationIdProvider()).thenReturn(organizationIdProvider);
    when(organizationIdProvider.getOrganizationId()).thenReturn(ORGANIZATION_ID);
  }

  @Test(expected = UseCaseException.class)
  public void inputIsNull() throws UseCaseException
  {
    mockRepositories(false);
    createQuestionCategory.executeImpl(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenParentId_isNull() throws UseCaseException
  {
    mockRepositories(false);
    QuestionCategoryInput input = new QuestionCategoryInput(null, INDEX, NAME);
    createQuestionCategory.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenName_isNull() throws UseCaseException
  {
    mockRepositories(false);
    QuestionCategoryInput input = new QuestionCategoryInput(PARENT_ID, INDEX, null);
    when(input.getName()).thenReturn(null);
    createQuestionCategory.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenOrganizationId_isNull() throws UseCaseException
  {
    mockRepositories(false);
    when(organizationIdProvider.getOrganizationId()).thenReturn(null);
    createQuestionCategory.executeImpl(createInput());
  }

  @Test(expected = UseCaseException.class)
  public void whenRepository_returnsNull() throws UseCaseException
  {
    mockRepositories(true);
    createQuestionCategory.executeImpl(createInput());
  }

  @Test
  public void whenSuccess() throws UseCaseException, LmsRepositoryException
  {
    mockRepositories(false);
    QuestionCategoryInput input = createInput();
    input.setDescription(DESCRIPTION);
    QuestionCategoryId questionCategoryId = QuestionCategoryId.valueOf(ID);
    QuestionCategory questionCategory = new QuestionCategory(
        questionCategoryId,
        QuestionCategoryId.valueOf(PARENT_ID),
        OrganizationId.valueOf(ORGANIZATION_ID),
        INDEX,
        NAME,
        DESCRIPTION
    );
    when(questionCategoryRepository.create(
        QuestionCategoryId.valueOf(PARENT_ID),
        OrganizationId.valueOf(ORGANIZATION_ID),
        INDEX,
        NAME,
        DESCRIPTION
    )).thenReturn(questionCategory);
    String id = createQuestionCategory.executeImpl(input);
    Assert.assertEquals(ID, id);
  }

  private QuestionCategoryInput createInput()
  {
    QuestionCategoryInput input = new QuestionCategoryInput(PARENT_ID, INDEX, NAME);
    input.setDescription(DESCRIPTION);
    return input;
  }

  private void mockRepositories(boolean isNull)
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    questionCategoryRepository = mock(QuestionCategoryRepository.class);

    if (isNull)
    {
      when(lmsRepositoryRegistry.getQuestionCategoryRepository()).thenReturn(null);
    }
    else
    {
      when(lmsRepositoryRegistry.getQuestionCategoryRepository()).thenReturn(questionCategoryRepository);
    }

    createQuestionCategory = new CreateQuestionCategory(lmsRepositoryRegistry, lmsServiceRegistry);
  }
}
