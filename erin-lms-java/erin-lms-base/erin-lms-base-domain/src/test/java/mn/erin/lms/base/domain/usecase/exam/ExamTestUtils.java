package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.ExamPublishStatus;
import mn.erin.lms.base.domain.model.exam.ExamStatus;
import mn.erin.lms.base.domain.model.exam.ExamType;
import mn.erin.lms.base.domain.model.exam.HistoryOfModification;
import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;
import mn.erin.lms.base.domain.model.exam.ShowAnswerResult;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionDetail;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.model.exam.question.QuestionId;
import mn.erin.lms.base.domain.model.exam.question.QuestionStatus;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInput;

/**
 * @author Temuulen Naranbold
 */
public class ExamTestUtils
{
  public static final String EXAM_ID = "examId";
  public static final String NAME = "examName";
  public static final String DESCRIPTION = "description";
  public static final String START_TIME = "15:00";
  public static final String END_TIME = "15:30";
  public static final String CERTIFICATE_ID = "certificate_1";
  public static final String SHOW_ANSWER_RESULT = "AFTER_EXAM";
  public static final String EXAM_TYPE = "SELF_TRAINING";
  public static final String GROUP_ID = "groupId_1";
  public static final String CATEGORY_ID = "categoryId_1";
  public static final boolean SET_FALSE = false;
  public static final String EXAM_CATEGORY_ID = "examCategoryId";
  public static final String PUBLISH_TIME = "13:00";
  public static final Date CURRENT_DATE = new Date();
  public static final String AUTHOR = "admin";
  public static final String QUESTION_CATEGORY_ID = "questionCategoryId";
  public static final String QUESTION_GROUP_ID = "questionGroupId";

  public static ExamInput createInput()
  {
    Set<String> questionIds = new HashSet<>();
    questionIds.add("q1");
    questionIds.add("q2");
    questionIds.add("q3");
    questionIds.add("q4");
    questionIds.add("q5");

    Set<RandomQuestionConfig> randomQuestionConfigs = new HashSet<>();
    randomQuestionConfigs.add(new RandomQuestionConfig(GROUP_ID, CATEGORY_ID, 5, 3));
    randomQuestionConfigs.add(new RandomQuestionConfig(GROUP_ID, CATEGORY_ID, 5, 3));
    randomQuestionConfigs.add(new RandomQuestionConfig(GROUP_ID, CATEGORY_ID, 5, 3));
    randomQuestionConfigs.add(new RandomQuestionConfig(GROUP_ID, CATEGORY_ID, 5, 3));
    randomQuestionConfigs.add(new RandomQuestionConfig(GROUP_ID, CATEGORY_ID, 5, 3));

    Set<String> enrolledLearners = new HashSet<>();
    enrolledLearners.add("user1");
    enrolledLearners.add("user2");
    enrolledLearners.add("user3");

    Set<String> enrolledGroups = new HashSet<>();
    enrolledGroups.add("group1");
    enrolledGroups.add("group2");
    enrolledGroups.add("group3");

    ExamInput examInput = new ExamInput();
    examInput.setName(NAME);
    examInput.setDescription(DESCRIPTION);
    examInput.setQuestionIds(questionIds);
    examInput.setRandomQuestionConfigs(randomQuestionConfigs);
    examInput.setPublishDate(CURRENT_DATE);
    examInput.setPublishTime(PUBLISH_TIME);
    examInput.setStartDate(CURRENT_DATE);
    examInput.setEndDate(CURRENT_DATE);
    examInput.setStartTime(START_TIME);
    examInput.setEndTime(END_TIME);
    examInput.setDuration(30);
    examInput.setThresholdScore(8);
    examInput.setAttempt(1);
    examInput.setCertificateId(CERTIFICATE_ID);
    examInput.setSendEmail(SET_FALSE);
    examInput.setSendSMS(SET_FALSE);
    examInput.setQuestionsPerPage(true);
    examInput.setAutoStart(true);
    examInput.setMaxScore(13);
    examInput.setAnswerResult(SHOW_ANSWER_RESULT);
    examInput.setExamType(EXAM_TYPE);
    examInput.setShuffleQuestion(SET_FALSE);
    examInput.setShuffleAnswer(SET_FALSE);
    examInput.setExamCategoryId(EXAM_CATEGORY_ID);
    examInput.setGroupId(GROUP_ID);
    examInput.setEnrolledLearners(enrolledLearners);
    examInput.setEnrolledGroups(enrolledGroups);
    return examInput;
  }

  public static List<Exam> getExams(int count)
  {
    List<Exam> exams = new ArrayList<>();
    ExamConfig examConfig = new ExamConfig(Collections.emptySet(), 0, ShowAnswerResult.AFTER_EXAM, false, false, false, true, 1);
    examConfig.setCertificateId(CertificateId.valueOf("certificateId"));

    for (int i = 0; i < count; i++)
    {
      exams.add(new Exam.Builder(ExamId.valueOf("examId" + i), "author", new Date(), "exam" + i)
          .withExamStatus(ExamStatus.NEW)
          .withExamCategoryId(ExamCategoryId.valueOf("category"))
          .withExamConfig(examConfig)
          .withExamType(ExamType.OFFICIAL)
          .withExamStatus(ExamStatus.NEW)
          .withExamPublishStatus(ExamPublishStatus.UNPUBLISHED)
          .build());
    }
    return exams;
  }

  public static List<Question> generateQuestions(int numberOfQuestion)
  {
    List<Question> questions = new ArrayList<>();
    List<Answer> answers = new ArrayList<>();
    String questionValue = "Question-";
    String id = "id-";

    Answer answer = new Answer("answer-1", 0, 0, 0, true, 0);
    Answer answer1 = new Answer("answer-2", 1, 0, 0, false, 0);
    Answer answer2 = new Answer("answer-3", 2, 0, 0, false, 0);
    answers.add(answer);
    answers.add(answer1);
    answers.add(answer2);

    HistoryOfModification historyOfModification = new HistoryOfModification(AUTHOR, CURRENT_DATE);
    List<HistoryOfModification> historyOfModifications = new ArrayList<>();
    historyOfModifications.add(historyOfModification);

    QuestionDetail questionDetail = new QuestionDetail(
        QuestionCategoryId.valueOf(QUESTION_CATEGORY_ID), QuestionGroupId.valueOf(QUESTION_GROUP_ID),
        historyOfModifications, "", false, null, null, null
        );

    for (int i = 0; i < numberOfQuestion; i++)
    {
      Question question = new Question.Builder(QuestionId.valueOf(id + i), questionValue + i, AUTHOR, CURRENT_DATE).build();
      question.setDetail(questionDetail);
      question.setAnswers(answers);
      question.setStatus(QuestionStatus.USED);
      questions.add(question);
    }

    return questions;
  }
}
