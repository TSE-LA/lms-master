package mn.erin.lms.base.mongo.implementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.model.exam.HistoryOfModification;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionDetail;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.model.exam.question.QuestionId;
import mn.erin.lms.base.domain.model.exam.question.QuestionStatus;
import mn.erin.lms.base.domain.model.exam.question.QuestionType;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.mongo.document.exam.MongoHistoryOfModification;
import mn.erin.lms.base.mongo.document.exam.question.MongoAnswerEntity;
import mn.erin.lms.base.mongo.document.exam.question.MongoQuestion;
import mn.erin.lms.base.mongo.document.exam.question.MongoQuestionDetail;
import mn.erin.lms.base.mongo.document.exam.question.MongoQuestionStatus;
import mn.erin.lms.base.mongo.document.exam.question.MongoQuestionType;
import mn.erin.lms.base.mongo.repository.MongoQuestionRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Galsan Bayart
 */
public class QuestionRepositoryImpl implements QuestionRepository
{
  private final MongoQuestionRepository mongoQuestionRepository;

  public QuestionRepositoryImpl(MongoQuestionRepository mongoQuestionRepository)
  {
    this.mongoQuestionRepository = mongoQuestionRepository;
  }

  @Override
  public Question create(String value, QuestionDetail detail, QuestionType type,
      List<Answer> answers, int score)
  {
    String id = IdGenerator.generateId();

    String user = detail.getHistoryOfModifications().get(0).getModifiedUser();
    Date date = detail.getHistoryOfModifications().get(0).getModifiedDate();

    MongoQuestion mongoQuestion = new MongoQuestion();
    mongoQuestion.setId(id);
    mongoQuestion.setValue(value);
    mongoQuestion.setAuthor(user);
    mongoQuestion.setCreatedDate(date);
    mongoQuestion.setCategoryId(detail.getCategoryId().getId());
    mongoQuestion.setGroupId(detail.getGroupId().getId());
    mongoQuestion.setDetail(mapToMongoQuestionDetail(detail));
    mongoQuestion.setType(MongoQuestionType.valueOf(type.name()));
    mongoQuestion.setAnswers(answers.stream().map(this::mapToMongoAnswerEntity).collect(Collectors.toList()));
    mongoQuestion.setStatus(MongoQuestionStatus.NEW);
    mongoQuestion.setModifiedUser(user);
    mongoQuestion.setModifiedDate(date);
    mongoQuestion.setScore(score);

    return mapToQuestion(mongoQuestionRepository.save(mongoQuestion));
  }

  @Override
  public Question update(Question question) throws LmsRepositoryException
  {
    Optional<MongoQuestion> mongoQuestions = mongoQuestionRepository.findById(question.getId().getId());

    if (mongoQuestions.isPresent())
    {
      MongoQuestion mongoQuestion = mongoQuestions.get();
      if (mongoQuestion.getStatus().name().equals(MongoQuestionStatus.NEW.name()))
      {
        mongoQuestionRepository.save(mapToMongoQuestion(question, mongoQuestion));
        return mapToQuestion(mongoQuestion);
      }
      else if (mongoQuestion.getStatus().name().equals(MongoQuestionStatus.USED.name()))
      {
        mongoQuestion.setStatus(MongoQuestionStatus.ARCHIVED);
        mongoQuestionRepository.save(mongoQuestion);
        MongoQuestion mongoQuestionNew = new MongoQuestion();
        mongoQuestionNew.setStatus(MongoQuestionStatus.NEW);
        return mapToQuestion(mongoQuestionRepository.save(mapToMongoQuestion(question, mongoQuestionNew)));
      }
      else
        return null;
    }
    else
    {
      throw new LmsRepositoryException("Question was not found with the ID: [" + question.getId().getId() + "]");
    }
  }

  @Override
  public List<Question> getAll()
  {
    return mongoQuestionRepository.findAll().stream().map(this::mapToQuestion).collect(Collectors.toList());
  }

  @Override
  public List<Question> getAllActive()
  {
    List<Question> questions = new ArrayList<>();

    questions.addAll(mongoQuestionRepository.findAllByStatus(MongoQuestionStatus.NEW).stream().map(this::mapToQuestion).collect(Collectors.toList()));
    questions.addAll(mongoQuestionRepository.findAllByStatus(MongoQuestionStatus.USED).stream().map(this::mapToQuestion).collect(Collectors.toList()));

    return questions;
  }

  @Override
  public List<Question> getAllActive(QuestionCategoryId categoryId)
  {
    List<Question> questions = new ArrayList<>();

    questions.addAll(mongoQuestionRepository.findAllByCategoryIdAndStatus(categoryId.getId(), MongoQuestionStatus.NEW).stream().map(this::mapToQuestion)
        .collect(Collectors.toList()));
    questions.addAll(mongoQuestionRepository.findAllByCategoryIdAndStatus(categoryId.getId(), MongoQuestionStatus.USED).stream().map(this::mapToQuestion)
        .collect(Collectors.toList()));

    return questions;
  }

  @Override
  public List<Question> getAllActive(QuestionGroupId groupId)
  {
    List<Question> questions = new ArrayList<>();

    questions.addAll(mongoQuestionRepository.findAllByGroupIdAndStatus(groupId.getId(), MongoQuestionStatus.NEW).stream().map(this::mapToQuestion)
        .collect(Collectors.toList()));
    questions.addAll(mongoQuestionRepository.findAllByGroupIdAndStatus(groupId.getId(), MongoQuestionStatus.USED).stream().map(this::mapToQuestion)
        .collect(Collectors.toList()));

    return questions;
  }

  @Override
  public List<Question> getAllActive(QuestionCategoryId categoryId, QuestionGroupId groupId)
  {
    List<Question> questions = new ArrayList<>();

    questions.addAll(mongoQuestionRepository.findAllByGroupIdAndCategoryIdAndStatus(groupId.getId(), categoryId.getId(), MongoQuestionStatus.NEW).stream()
        .map(this::mapToQuestion).collect(Collectors.toList()));
    questions.addAll(mongoQuestionRepository.findAllByGroupIdAndCategoryIdAndStatus(groupId.getId(), categoryId.getId(), MongoQuestionStatus.USED).stream()
        .map(this::mapToQuestion).collect(Collectors.toList()));

    return questions;
  }

  @Override
  public int getQuestionCount(int score)
  {
    List<Question> questions = mongoQuestionRepository.findAllByScoreAndStatus(score, MongoQuestionStatus.NEW).stream().map(this::mapToQuestion)
        .collect(Collectors.toList());
    List<Question> usedQuestions = mongoQuestionRepository.findAllByScoreAndStatus(score, MongoQuestionStatus.USED).stream().map(this::mapToQuestion)
        .collect(Collectors.toList());
    questions.addAll(usedQuestions);
    return questions.size();
  }

  @Override
  public int getQuestionCount(QuestionCategoryId categoryId, int score)
  {
    List<Question> questions = mongoQuestionRepository.findAllByCategoryIdAndScoreAndStatus(categoryId.getId(), score, MongoQuestionStatus.NEW).stream()
        .map(this::mapToQuestion)
        .collect(Collectors.toList());
    List<Question> usedQuestions = mongoQuestionRepository.findAllByCategoryIdAndScoreAndStatus(categoryId.getId(), score, MongoQuestionStatus.USED).stream()
        .map(this::mapToQuestion)
        .collect(Collectors.toList());
    questions.addAll(usedQuestions);
    return questions.size();
  }

  @Override
  public int getQuestionCount(QuestionGroupId groupId, int score)
  {
    List<Question> questions = mongoQuestionRepository.findAllByGroupIdAndScoreAndStatus(groupId.getId(), score, MongoQuestionStatus.NEW).stream()
        .map(this::mapToQuestion)
        .collect(Collectors.toList());
    List<Question> usedQuestions = mongoQuestionRepository.findAllByGroupIdAndScoreAndStatus(groupId.getId(), score, MongoQuestionStatus.USED).stream()
        .map(this::mapToQuestion)
        .collect(Collectors.toList());
    questions.addAll(usedQuestions);
    return questions.size();
  }

  @Override
  public int getQuestionCount(QuestionCategoryId categoryId, QuestionGroupId groupId, int score)
  {
    return findByGroupIdAndCategoryAndScore(groupId.getId(), categoryId.getId(), score).size();
  }

  @Override
  public List<Question> getByIds(Set<String> questionIds)
  {
    return mongoQuestionRepository.findAllByIdIn(questionIds).stream().map(this::mapToQuestion).collect(Collectors.toList());
  }

  @Override
  public void updateStatus(Set<String> questionIds, String updatingStatus)
  {
    List<MongoQuestion> mongoQuestions = mongoQuestionRepository.findAllByIdIn(questionIds);
    for (MongoQuestion mongoQuestion : mongoQuestions)
    {
      mongoQuestion.setStatus(MongoQuestionStatus.valueOf(updatingStatus));
      mongoQuestionRepository.save(mongoQuestion);
    }
  }

  @Override
  public void archiveQuestion(String questionId)
  {
    Optional<MongoQuestion> optional = mongoQuestionRepository.findById(questionId);
    if (optional.isPresent())
    {
      MongoQuestion question = optional.get();
      question.setStatus(MongoQuestionStatus.ARCHIVED);
      mongoQuestionRepository.save(question);
    }
  }

  @Override
  public List<Question> findByGroupIdAndCategoryAndScore(String questionGroupId, String questionCategoryId, int score)
  {
    List<Question> newQuestions = mongoQuestionRepository.findAllByGroupIdAndCategoryIdAndScoreAndStatus(questionGroupId, questionCategoryId, score,
            MongoQuestionStatus.NEW).stream().map(this::mapToQuestion)
        .collect(Collectors.toList());
    List<Question> usedQuestions = mongoQuestionRepository.findAllByGroupIdAndCategoryIdAndScoreAndStatus(questionGroupId, questionCategoryId, score,
            MongoQuestionStatus.USED).stream().map(this::mapToQuestion)
        .collect(Collectors.toList());
    newQuestions.addAll(usedQuestions);

    return newQuestions;
  }

  @Override
  public void deleteById(String id)
  {
    mongoQuestionRepository.deleteById(id);
  }

  @Override
  public Question findById(String id) throws LmsRepositoryException
  {
    MongoQuestion mongoQuestion = mongoQuestionRepository.findById(id).orElse(null);

    if (mongoQuestion != null)
    {
      return mapToQuestion(mongoQuestion);
    }
    else
    {
      throw new LmsRepositoryException("Question was not found with the ID: [" + id + "]");
    }
  }

  @Override
  public List<Question> listAll(QuestionCategoryId categoryId)
  {
    return mongoQuestionRepository.findAllByCategoryId(categoryId.getId()).stream().map(this::mapToQuestion).collect(Collectors.toList());
  }

  private MongoQuestion mapToMongoQuestion(Question question, MongoQuestion mongoQuestion)
  {
    mongoQuestion.setValue(question.getValue());
    mongoQuestion.setAuthor(question.getAuthor());
    mongoQuestion.setCreatedDate(question.getCreatedDate());
    mongoQuestion.setGroupId(question.getDetail().getGroupId().getId());
    mongoQuestion.setCategoryId(question.getDetail().getCategoryId().getId());
    mongoQuestion.setDetail(mapToMongoQuestionDetail(question.getDetail()));
    mongoQuestion.setType(MongoQuestionType.valueOf(question.getType().name()));
    mongoQuestion.setAnswers(question.getAnswers().stream().map(this::mapToMongoAnswerEntity).collect(Collectors.toList()));
    mongoQuestion.setModifiedUser(question.getModifiedUser());
    mongoQuestion.setModifiedDate(question.getModifiedDate());
    mongoQuestion.setScore(question.getScore());
    return mongoQuestion;
  }

  private Question mapToQuestion(MongoQuestion mongoQuestion)
  {
    return new Question.Builder(QuestionId.valueOf(mongoQuestion.getId()), mongoQuestion.getValue(), mongoQuestion.getAuthor(), mongoQuestion.getCreatedDate())
        .withDetail(mapToQuestionDetail(mongoQuestion))
        .withType(QuestionType.valueOf(mongoQuestion.getType().name()))
        .withAnswers(mongoQuestion.getAnswers().stream().map(this::mapToAnswer).collect(Collectors.toList()))
        .withStatus(QuestionStatus.valueOf(mongoQuestion.getStatus().name()))
        .withModifiedUser(mongoQuestion.getModifiedUser())
        .withModifiedDate(mongoQuestion.getModifiedDate())
        .withScore(mongoQuestion.getScore()).build();
  }

  private MongoQuestionDetail mapToMongoQuestionDetail(QuestionDetail detail)
  {
    List<MongoHistoryOfModification> modifications = detail.getHistoryOfModifications().stream().map(this::mapToMongoHistoryOfModification)
        .collect(Collectors.toList());
    return new MongoQuestionDetail(modifications,
        detail.getContentFolderId(), detail.isHasImage(), detail.getImageName(), detail.getCorrectAnswerText(), detail.getWrongAnswerText());
  }

  private QuestionDetail mapToQuestionDetail(MongoQuestion question)
  {
    MongoQuestionDetail detail = question.getDetail();
    List<HistoryOfModification> modifications = detail.getHistoryOfModifications().stream().map(this::mapToHistoryOfModification).collect(Collectors.toList());
    return new QuestionDetail(QuestionCategoryId.valueOf(question.getCategoryId()), QuestionGroupId.valueOf(question.getGroupId()), modifications,
        detail.getContentFolderId(), detail.isHasImage(), detail.getImageName(), detail.getCorrectAnswerText(), detail.getWrongAnswerText());
  }

  private MongoAnswerEntity mapToMongoAnswerEntity(Answer answer)
  {
    return new MongoAnswerEntity(answer.getValue(), answer.getIndex(), answer.getMatchIndex(), answer.getColumn(), answer.isCorrect(), answer.getWeight());
  }

  private Answer mapToAnswer(MongoAnswerEntity answer)
  {
    return new Answer(answer.getValue(), answer.getIndex(), answer.getMatchIndex(), answer.getColumn(), answer.isCorrect(), answer.getWeight());
  }

  private MongoHistoryOfModification mapToMongoHistoryOfModification(HistoryOfModification historyOfModification)
  {
    return new MongoHistoryOfModification(historyOfModification.getModifiedUser(), historyOfModification.getModifiedDate());
  }

  private HistoryOfModification mapToHistoryOfModification(MongoHistoryOfModification historyOfModification)
  {
    return new HistoryOfModification(historyOfModification.getModifiedUser(), historyOfModification.getModifiedDate());
  }
}
