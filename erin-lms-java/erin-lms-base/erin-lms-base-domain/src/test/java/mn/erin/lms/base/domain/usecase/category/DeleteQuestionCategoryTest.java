package mn.erin.lms.base.domain.usecase.category;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class DeleteQuestionCategoryTest
{
  private static final String CATEGORY_ID = "categoryId";

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private QuestionCategoryRepository questionCategoryRepository;
  private QuestionRepository questionRepository;
  private DeleteQuestionCategory deleteQuestionCategory;

  @Before
  public void setUp()
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    questionCategoryRepository = mock(QuestionCategoryRepository.class);
    questionRepository = mock(QuestionRepository.class);

    when(lmsRepositoryRegistry.getQuestionCategoryRepository()).thenReturn(questionCategoryRepository);
    when(lmsRepositoryRegistry.getQuestionRepository()).thenReturn(questionRepository);
    when(questionCategoryRepository.exists(any(QuestionCategoryId.class))).thenReturn(true);

    deleteQuestionCategory = new DeleteQuestionCategory(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void when_Input_Is_Null() throws UseCaseException
  {
    deleteQuestionCategory.executeImpl(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void when_Input_Category_Id_Blank() throws UseCaseException
  {
    deleteQuestionCategory.executeImpl("");
  }

  @Test
  public void whenCategory_doesNotExists() throws UseCaseException
  {
    when(questionCategoryRepository.exists(any(QuestionCategoryId.class))).thenReturn(false);
    Assert.assertFalse(deleteQuestionCategory.executeImpl(CATEGORY_ID));
  }

//  @Test(expected = UseCaseException.class)
//  public void whenQuestions_underCategory_notNull() throws UseCaseException
//  {
//    QuestionCategoryId id = new QuestionCategoryId(CATEGORY_ID);
//    Question question = new Question();
//    List<Question> questions = new ArrayList<>();
//    questions.add(question);
//    when(questionRepository.listAll(id)).thenReturn(questions);
//    deleteQuestionCategory.executeImpl(CATEGORY_ID);
//  }

  @Test
  public void whenQuestions_UnderCategory_null() throws UseCaseException
  {
    QuestionCategoryId id = new QuestionCategoryId(CATEGORY_ID);
    when(questionRepository.listAll(id)).thenReturn(null);
    deleteQuestionCategory.executeImpl(CATEGORY_ID);
    Assert.assertTrue(deleteQuestionCategory.executeImpl(CATEGORY_ID));
  }

  @Test
  public void whenQuestions_UnderCategory_empty() throws UseCaseException
  {
    QuestionCategoryId id = new QuestionCategoryId(CATEGORY_ID);
    List<Question> emptyList = Collections.emptyList();
    when(questionRepository.listAll(id)).thenReturn(emptyList);
    deleteQuestionCategory.executeImpl(CATEGORY_ID);
    Assert.assertTrue(deleteQuestionCategory.executeImpl(CATEGORY_ID));
  }
}