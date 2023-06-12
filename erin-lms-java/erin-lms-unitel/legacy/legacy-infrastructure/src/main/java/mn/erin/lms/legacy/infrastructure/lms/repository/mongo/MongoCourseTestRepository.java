/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.model.assessment.Answer;
import mn.erin.lms.legacy.domain.lms.model.assessment.Question;
import mn.erin.lms.legacy.domain.lms.model.assessment.QuestionType;
import mn.erin.lms.legacy.domain.lms.model.assessment.Test;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class MongoCourseTestRepository implements CourseTestRepository
{
  private static final String COLLECTION_NAME = "CourseTests";
  private final MongoCollection<Document> courseTestCollection;

  private static final String FIELD_ID = "_id";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_GRADED = "graded";
  private static final String FIELD_TIME = "timeLimit";
  private static final String FIELD_DATE = "dueDate";
  private static final String FIELD_QUESTIONS = "questions";
  private static final String FIELD_MAX_ATTEMPTS = "maxAttempts";
  private static final String FIELD_THRESHOLD_SCORE = "thresholdScore";

  private static final String TITLE_PROPERTY = "title";
  private static final String ANSWER_PROPERTY = "answers";
  private static final String TYPE_PROPERTY = "type";

  private static final String VALUE_PROPERTY = "value";
  private static final String CORRECT_PROPERTY = "correct";
  private static final String SCORE_PROPERTY = "score";

  private static final String ERROR_MSG_COURSE_TEST_NAME = "Course test name cannot be null";
  private static final String ERROR_MSG_QUESTIONS = "Course test questions cannot be null or empty!";
  private static final String ERROR_MSG_TEST_ID = "Course test id cannot be null!";

  public MongoCourseTestRepository(MongoTemplate mongoTemplate)
  {
    Objects.requireNonNull(mongoTemplate, "Mongo template cannot be null!");
    this.courseTestCollection = mongoTemplate.getCollection(COLLECTION_NAME);
  }

  @Override
  public Test create(List<Question> questions, String name, boolean graded, Long timeLimit, Date dueDate)
  {
    Document testAsDocument = createTest(questions, name, graded, timeLimit, dueDate);
    courseTestCollection.insertOne(testAsDocument);
    return createCourseTest(((ObjectId) testAsDocument.get(FIELD_ID)).toHexString(), name, questions, graded, timeLimit, dueDate);
  }

  @Override
  public Test create(List<Question> questions, String name, boolean graded, Long timeLimit, Date dueDate, Integer maxAttempts, Double thresholdScore)
  {
    Document testAsDocument = createTest(questions, name, graded, timeLimit, dueDate);
    testAsDocument.put(FIELD_MAX_ATTEMPTS, maxAttempts);
    testAsDocument.put(FIELD_THRESHOLD_SCORE, thresholdScore);
    courseTestCollection.insertOne(testAsDocument);
    return createCourseTest(((ObjectId) testAsDocument.get(FIELD_ID)).toHexString(), name, questions, graded, timeLimit, dueDate, maxAttempts, thresholdScore);
  }

  @Override
  public Test update(TestId id, List<Question> questions)
  {
    validate(id, questions);
    Bson filter = eq(FIELD_ID, new ObjectId(id.getId()));

    Document setDocument = new Document(FIELD_QUESTIONS, questions);
    Document update = new Document("$set", setDocument);

    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
    options.upsert(false);
    options.returnDocument(ReturnDocument.AFTER);

    Document updated = courseTestCollection.findOneAndUpdate(filter, update, options);
    return toCourseTest(updated);
  }

  @Override
  public Test update(TestId id, List<Question> questions, Integer maxAttempts, Double thresholdScore)
  {
    validate(id, questions);

    Bson filter = eq(FIELD_ID, new ObjectId(id.getId()));

    Document setDocument = new Document(FIELD_QUESTIONS, questions);
    setDocument.put(FIELD_MAX_ATTEMPTS, maxAttempts);
    setDocument.put(FIELD_THRESHOLD_SCORE, thresholdScore);

    Document update = new Document("$set", setDocument);

    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
    options.upsert(false);
    options.returnDocument(ReturnDocument.AFTER);

    Document updated = courseTestCollection.findOneAndUpdate(filter, update, options);
    return toCourseTest(updated);
  }

  @Override
  public Test get(TestId id) throws LMSRepositoryException
  {
    Validate.notNull(id, ERROR_MSG_TEST_ID);

    String testId = id.getId();
    Bson filter = eq(FIELD_ID, new ObjectId(testId));
    FindIterable<Document> documents = courseTestCollection.find(filter);

    Iterator<Document> iterator = documents.iterator();

    if (!iterator.hasNext())
    {
      throw new LMSRepositoryException("Could not find test with test ID : [" + testId + "]");
    }

    Document document = iterator.next();
    return toCourseTest(document);
  }

  @Override
  public boolean delete(TestId id) throws LMSRepositoryException
  {
    Validate.notNull(id, ERROR_MSG_TEST_ID);
    Bson filter = eq(FIELD_ID, new ObjectId(id.getId()));

    Document deletedDoc = courseTestCollection.findOneAndDelete(filter);
    // if it returns not null it means delete operation was successful.
    return null != deletedDoc;
  }

  private Document createTest(List<Question> questions, String name, boolean graded, Long timeLimit, Date dueDate)
  {
    validate(name, questions);
    ObjectId objectId = new ObjectId(new Date());
    Document document = new Document();

    document.put(FIELD_ID, objectId);
    document.put(FIELD_NAME, name);
    document.put(FIELD_GRADED, graded);
    document.put(FIELD_TIME, timeLimit);
    document.put(FIELD_DATE, dueDate);
    document.put(FIELD_QUESTIONS, questions);

    return document;
  }

  private Test toCourseTest(Document document)
  {
    String testIdDocument = document.getObjectId(FIELD_ID).toHexString();
    String name = (String) document.get(FIELD_NAME);
    boolean graded = (boolean) document.get(FIELD_GRADED);
    Long timeLimit = (Long) document.get(FIELD_TIME);
    Date date = (Date) document.get(FIELD_DATE);
    Integer maxAttempts = (Integer) document.get(FIELD_MAX_ATTEMPTS);
    Double thresholdScore = (Double) document.get(FIELD_THRESHOLD_SCORE);
    List<Document> questions = (List<Document>) document.get(FIELD_QUESTIONS);
    return createCourseTest(testIdDocument, name, toQuestions(questions), graded, timeLimit, date, maxAttempts, thresholdScore);
  }

  private Test createCourseTest(String id, String name, List<Question> questions, boolean graded, Long timeLimit, Date dueDate)
  {
    Test test = new Test(new TestId(id), name, graded, timeLimit, dueDate);
    for (Question question : questions)
    {
      test.addQuestion(question);
    }
    return test;
  }

  private Test createCourseTest(String id, String name, List<Question> questions, boolean graded, Long timeLimit, Date dueDate,
      Integer maxAttempts, Double thresholdScore)
  {
    Test test = new Test(new TestId(id), name, graded, timeLimit, dueDate);
    for (Question question : questions)
    {
      test.addQuestion(question);
    }
    test.setMaxAttempts(maxAttempts);
    test.setThresholdScore(thresholdScore);
    return test;
  }

  private List<Question> toQuestions(List<Document> questionDocuments)
  {
    List<Question> questionList = new ArrayList<>();
    for (Document questionAsDocument : questionDocuments)
    {
      String title = (String) questionAsDocument.get(TITLE_PROPERTY);
      QuestionType type = QuestionType.valueOf((String) questionAsDocument.get(TYPE_PROPERTY));
      List<Document> documents = (List<Document>) questionAsDocument.get(ANSWER_PROPERTY);
      List<Answer> answers = toAnswer(documents);
      Question question = new Question(title, type);
      for (Answer answer : answers)
      {
        question.addAnswer(answer);
      }

      questionList.add(question);
    }
    return questionList;
  }

  private List<Answer> toAnswer(List<Document> answerDocuments)
  {
    List<Answer> answerList = new ArrayList<>();
    for (Document answerAsDocument : answerDocuments)
    {
      String value = (String) answerAsDocument.get(VALUE_PROPERTY);
      boolean correct = (boolean) answerAsDocument.get(CORRECT_PROPERTY);
      Double score = (Double) answerAsDocument.get(SCORE_PROPERTY);
      Answer answer = new Answer(value, correct, score);
      answerList.add(answer);
    }
    return answerList;
  }

  private void validate(String name, List<Question> questions)
  {
    Validate.notNull(name, ERROR_MSG_COURSE_TEST_NAME);
    Validate.notEmpty(questions, ERROR_MSG_QUESTIONS);
  }

  private void validate(TestId testId, List<Question> questions)
  {
    Validate.notNull(testId, ERROR_MSG_TEST_ID);
    Validate.notEmpty(questions, ERROR_MSG_QUESTIONS);
  }
}
