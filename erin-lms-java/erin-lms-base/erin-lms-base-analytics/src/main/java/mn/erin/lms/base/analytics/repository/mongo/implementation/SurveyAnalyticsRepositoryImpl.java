package mn.erin.lms.base.analytics.repository.mongo.implementation;

import mn.erin.lms.base.analytics.model.survey.SurveyAnalytic;
import mn.erin.lms.base.analytics.model.survey.SurveyAnswer;
import mn.erin.lms.base.analytics.repository.mongo.SurveyAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.utils.ArrayStringConverter;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoSurveyQueries;
import mn.erin.lms.base.analytics.service.UserService;
import mn.erin.lms.base.domain.model.assessment.Answer;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.QuestionType;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.model.course.CourseId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mongodb.client.MongoCollection;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.*;

/**
 * @author Munkh
 */
public class SurveyAnalyticsRepositoryImpl extends AnalyticRepository implements SurveyAnalyticsRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SurveyAnalyticsRepositoryImpl.class);
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public SurveyAnalyticsRepositoryImpl(MongoCollectionProvider mongoCollectionProvider, UserService userService)
  {

    super(mongoCollectionProvider, userService);
  }

  @Override
  public List<SurveyAnalytic> getAllBySurveyId(AssessmentId assessmentId, LocalDate startDate, LocalDate endDate)
  {
    MongoCollection<Document> courseCollection = mongoCollectionProvider.getCourseCollection();
    MongoCollection<Document> scormCollection = mongoCollectionProvider.getSCORMCollection();

    List<SurveyAnalytic> result = getQuestions(assessmentId);

    Bson filter = eq(MongoFields.FIELD_SURVEY_ID, assessmentId.getId());
    Iterable<Document> iterable = courseCollection.find(filter);
    Iterator<Document> iterator = iterable.iterator();

    if (!iterator.hasNext())
    {
      return result;
    }

    List<String> contents = new ArrayList<>();
    while (iterator.hasNext())
    {
      Document courseDocument = iterator.next();
      String contentId = courseDocument.getString(MongoFields.FIELD_COURSE_COURSE_CONTENT_ID);

      if (contentId != null)
      {
        contents.add(contentId);
      }
    }

    List<String> answers = scormCollection.aggregate(Arrays.asList(
        Document.parse(MongoSurveyQueries.STAGE_PROJECT),
        Document.parse(String.format(
            MongoSurveyQueries.STAGE_MATCH,
            ArrayStringConverter.toArrayString(contents),
            startDate.atStartOfDay().format(DATE_FORMATTER),
            endDate.atTime(LocalTime.MAX).format(DATE_FORMATTER)
        ))
    )).map(document -> document.getString("answer")).into(new ArrayList<>());


    addAnswers(result, answers);

    return result;
  }

  @Override
  public List<SurveyAnalytic> getAllBySurveyAndCourseId(AssessmentId assessmentId, CourseId courseId, LocalDate startDate, LocalDate endDate)
  {
    MongoCollection<Document> courseCollection = mongoCollectionProvider.getCourseCollection();
    MongoCollection<Document> scormCollection = mongoCollectionProvider.getSCORMCollection();

    Bson filter = eq(MongoFields.FIELD_ID, new ObjectId(courseId.getId()));
    Iterable<Document> iterable = courseCollection.find(filter);
    Iterator<Document> iterator = iterable.iterator();

    if (!iterator.hasNext())
    {
      return Collections.emptyList();
    }

    Document courseDocument = iterator.next();

    List<SurveyAnalytic> result = getQuestions(assessmentId);

    String contentId = courseDocument.getString(MongoFields.FIELD_COURSE_COURSE_CONTENT_ID);

    List<String> answers = scormCollection.aggregate(Arrays.asList(
        Document.parse(MongoSurveyQueries.STAGE_PROJECT),
        Document.parse(String.format(
            MongoSurveyQueries.STAGE_MATCH_WITH_CONTENT_ID,
            contentId,
            startDate.atStartOfDay().format(DATE_FORMATTER),
            endDate.atTime(LocalTime.MAX).format(DATE_FORMATTER)
        ))
    )).map(document -> document.getString("answer")).into(new ArrayList<>());

    addAnswers(result, answers);

    return result;
  }

  private List<SurveyAnalytic> getQuestions(AssessmentId surveyId)
  {
    String quizId = getQuizId(surveyId);

    if (StringUtils.isBlank(quizId))
    {
      LOGGER.error("Survey not found!");
      return Collections.emptyList();
    }

    List<Question> questions = getQuestions(QuizId.valueOf(quizId));

    if (questions == null)
    {
      LOGGER.error("Quiz not found!");
      return Collections.emptyList();
    }

    List<SurveyAnalytic> result = new ArrayList<>();

    for (Question question : questions)
    {
      List<SurveyAnswer> answers = question.getAnswers().stream().map(answer ->
          question.getType().equals(QuestionType.FILL_IN_BLANK) ?
              new SurveyAnswer(answer.getValue()) :
              new SurveyAnswer(answer.getValue(), 0)
      ).collect(Collectors.toList());
      result.add(new SurveyAnalytic(questions.indexOf(question), question.getTitle(), question.getType(), answers));
    }

    return result;
  }

  private String getQuizId(AssessmentId assessmentId)
  {
    MongoCollection<Document> surveyCollection = mongoCollectionProvider.getSurveyCollection();

    Bson filter = eq(MongoFields.FIELD_ID, new ObjectId(assessmentId.getId()));
    Iterable<Document> iterable = surveyCollection.find(filter);
    Iterator<Document> iterator = iterable.iterator();

    if (!iterator.hasNext())
    {
      return null;
    }

    Document surveyDocument = iterator.next();

    return surveyDocument.getString("quizId");
  }

  private List<Question> getQuestions(QuizId quizId)
  {
    MongoCollection<Document> quizCollection = mongoCollectionProvider.getQuizCollection();

    Bson filter = eq(MongoFields.FIELD_ID, new ObjectId(quizId.getId()));
    Iterable<Document> iterable = quizCollection.find(filter);
    Iterator<Document> iterator = iterable.iterator();

    if (!iterator.hasNext())
    {
      return Collections.emptyList();
    }

    Document quizDocument = iterator.next();

    List<Document> questionDocuments = (List<Document>) quizDocument.get(MongoFields.FIELD_QUIZ_QUESTIONS);

    List<Question> result = new ArrayList<>();

    for (Document questionDocument : questionDocuments)
    {
      String type = questionDocument.getString(MongoFields.FIELD_QUESTION_TYPE);
      String title = questionDocument.getString(MongoFields.FIELD_QUESTION_TITLE);
      boolean isRequired = questionDocument.getBoolean(MongoFields.FIELD_QUESTION_REQUIRED);
      List<Document> answerDocuments = (List<Document>) questionDocument.get(MongoFields.FIELD_QUESTION_ANSWERS);

      Question question = new Question(title, QuestionType.valueOf(type), isRequired);

      for (Document answerDocument : answerDocuments)
      {
        String value = answerDocument.getString(MongoFields.FIELD_ANSWER_VALUE);
        boolean isCorrect = answerDocument.getBoolean(MongoFields.FIELD_ANSWER_CORRECT);
        double score = answerDocument.getDouble(MongoFields.FIELD_ANSWER_SCORE);

        question.addAnswer(new Answer(value, isCorrect, score));
      }

      result.add(question);
    }

    return result;
  }

  private void addAnswers(List<SurveyAnalytic> analytics, List<String> allAnswers)
  {
    for (String answer: allAnswers)
    {
      if (answer == null || answer.equals("unknown"))
      {
        continue;
      }

      String[] answers = answer.split("Å");

      if (answers.length != 0 && answers[0].startsWith("singleChoice"))
      {
        addSingleChoice(analytics, answers[0]);
      }

      if (answers.length > 1 && answers[1].startsWith("multiChoice"))
      {
        addMultiChoice(analytics, answers[1]);
      }

      if (answers.length > 2 && answers[2].startsWith("fillInBlank"))
      {
        addFillInBlank(analytics, answers[2]);
      }
    }
  }

  private void addSingleChoice(List<SurveyAnalytic> analytics, String answer)
  {
    String[] split = answer.split("Â");

    for (int index = 1; index < split.length; index++)
    {
      String[] pair = split[index].split("À");

      int key = Integer.parseInt(pair[0]);
      String value = pair[1];

      for (SurveyAnalytic analytic: analytics)
      {
        if (analytic.getIndex() == key - 1)
        {
          Optional<SurveyAnswer> optional = analytic.getAnswers().stream()
              .filter(surveyAnswer -> surveyAnswer.getValue().equals(value)).findFirst();
          optional.ifPresent(SurveyAnswer::increment);
        }
      }
    }
  }

  private void addMultiChoice(List<SurveyAnalytic> analytics, String answer)
  {
    String[] split = answer.split("Â");

    for (int index = 1; index < split.length; index++)
    {
      String[] pair = split[index].split("À");
      int key = Integer.parseInt(pair[0]);
      String value = pair[1];

      if (value.contains(","))
      {
        String[] values = value.split(",");

        for (SurveyAnalytic analytic: analytics)
        {
          if (analytic.getIndex() == key - 1)
          {
            for (String v : values)
            {
              Optional<SurveyAnswer> optional = analytic.getAnswers().stream()
                  .filter(surveyAnswer -> surveyAnswer.getValue().equals(v)).findFirst();
              optional.ifPresent(SurveyAnswer::increment);
            }
          }
        }
      }
      else
      {
        for (SurveyAnalytic analytic: analytics)
        {
          if (analytic.getIndex() == key - 1)
          {
            Optional<SurveyAnswer> optional = analytic.getAnswers().stream()
                .filter(surveyAnswer -> surveyAnswer.getValue().equals(value)).findFirst();
            optional.ifPresent(SurveyAnswer::increment);
          }
        }
      }
    }
  }

  private void addFillInBlank(List<SurveyAnalytic> analytics, String answer)
  {
    String[] split = answer.split("Â");

    for (int index = 1; index < split.length; index++)
    {
      String[] pair = split[index].split("À");
      int key = Integer.parseInt(pair[0]);
      String value = pair[1];

      for (SurveyAnalytic analytic: analytics)
      {
        if (analytic.getIndex() == key - 1)
        {
          analytic.getAnswers().add(new SurveyAnswer(value));
        }
      }
    }
  }
}
