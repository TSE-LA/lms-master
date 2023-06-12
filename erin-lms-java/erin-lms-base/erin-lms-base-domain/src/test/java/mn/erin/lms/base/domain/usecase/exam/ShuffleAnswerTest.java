//package mn.erin.lms.base.domain.usecase.exam;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import mn.erin.domain.base.usecase.UseCaseException;
//import mn.erin.lms.base.domain.model.exam.question.MainAnswer;
//import mn.erin.lms.base.domain.model.exam.question.MainAnswerEntity;
//import mn.erin.lms.base.domain.model.exam.question.MatchAnswer;
//import mn.erin.lms.base.domain.model.exam.question.MatchAnswerEntity;
//import mn.erin.lms.base.domain.model.exam.question.Question;
//import mn.erin.lms.base.domain.model.exam.question.QuestionId;
//import mn.erin.lms.base.domain.model.exam.question.QuestionType;
//
//
///**
// * @author Galsan Bayart.
// */
//public class ShuffleAnswerTest
//{
//  @Test
//  public void name() throws UseCaseException
//  {
//    ShuffleAnswer shuffleAnswer = new ShuffleAnswer();
//    Question question1 = new Question();
//    question1.setId(QuestionId.valueOf("1"));
//    question1.setType(QuestionType.MATCH);
//    question1.setAnswer(new MatchAnswer(
//        Arrays.asList(new MatchAnswerEntity("q1", 1, 1), new MatchAnswerEntity("q2", 1, 2)),
//        Arrays.asList(new MatchAnswerEntity("qa", 1, 1), new MatchAnswerEntity("qb", 1, 2))));
//
//    Question question2 = new Question();
//    question2.setId(QuestionId.valueOf("2"));
//    question2.setType(QuestionType.MATCH);
//    question2.setAnswer(new MatchAnswer(
//        Arrays.asList(new MatchAnswerEntity("qq1", 1, 1), new MatchAnswerEntity("qq2", 1, 2)),
//        Arrays.asList(new MatchAnswerEntity("qqa", 1, 1), new MatchAnswerEntity("qqb", 1, 2))));
//
//    Question question3 = new Question();
//    question3.setId(QuestionId.valueOf("3"));
//    question3.setType(QuestionType.SINGLE_CHOICE);
//    question3.setAnswer(new MainAnswer(Arrays.asList(new MainAnswerEntity("qqq1", 1, true), new MainAnswerEntity("qqq2", 1, false))));
//
//    List<Question> questions = Arrays.asList(question1, question2, question3);
//    shuffleAnswer.execute(questions);
//
//    Assert.assertEquals(3, questions.size());
//  }
//}