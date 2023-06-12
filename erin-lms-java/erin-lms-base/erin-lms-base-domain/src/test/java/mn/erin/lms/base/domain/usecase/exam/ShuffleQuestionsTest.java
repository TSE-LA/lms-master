//package mn.erin.lms.base.domain.usecase.exam;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import mn.erin.domain.base.usecase.UseCaseException;
//import mn.erin.lms.base.domain.model.exam.question.Question;
//import mn.erin.lms.base.domain.model.exam.question.QuestionId;
//
///**
// * @author Galsan Bayart.
// */
//public class ShuffleQuestionsTest
//{
//  @Test
//  public void ShuffleTest() throws UseCaseException
//  {
//    ShuffleQuestions shuffleQuestions = new ShuffleQuestions();
//    Question question1 = new Question();
//    question1.setId(QuestionId.valueOf("1"));
//    Question question2 = new Question();
//    question2.setId(QuestionId.valueOf("2"));
//    Question question3 = new Question();
//    question3.setId(QuestionId.valueOf("3"));
//    List<Question> questions = Arrays.asList(question1, question2, question3);
//    shuffleQuestions.execute(questions);
//    Assert.assertEquals(3, questions.size());
//  }
//}