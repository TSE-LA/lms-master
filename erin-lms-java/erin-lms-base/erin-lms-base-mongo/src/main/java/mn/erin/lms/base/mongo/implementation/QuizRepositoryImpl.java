package mn.erin.lms.base.mongo.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.assessment.Answer;
import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.QuestionType;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.mongo.document.assessment.MongoAnswer;
import mn.erin.lms.base.mongo.document.assessment.MongoQuestion;
import mn.erin.lms.base.mongo.document.assessment.MongoQuestionType;
import mn.erin.lms.base.mongo.document.assessment.MongoQuiz;
import mn.erin.lms.base.mongo.repository.MongoQuizRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class QuizRepositoryImpl implements QuizRepository
{
  private final MongoQuizRepository mongoQuizRepository;

  public QuizRepositoryImpl(MongoQuizRepository mongoQuizRepository)
  {
    this.mongoQuizRepository = mongoQuizRepository;
  }

  @Override
  public Quiz create(String name)
  {
    String id = IdGenerator.generateId();
    MongoQuiz mongoQuiz = new MongoQuiz(id, name);
    mongoQuizRepository.save(mongoQuiz);
    return new Quiz(QuizId.valueOf(id), name);
  }

  @Override
  public Quiz create(List<Question> questions, String name, boolean graded, Long timeLimit, LocalDateTime dueDate)
  {
    String id = IdGenerator.generateId();
    MongoQuiz mongoQuiz = new MongoQuiz(id, name, graded, timeLimit, dueDate, mapQuestions(questions));
    mongoQuizRepository.save(mongoQuiz);

    Quiz quiz = new Quiz(QuizId.valueOf(id), name, graded, timeLimit, dueDate);
    questions.forEach(quiz::addQuestion);
    return quiz;
  }

  @Override
  public Quiz create(List<Question> questions, String name, boolean graded, Long timeLimit, LocalDateTime dueDate, Integer maxAttempts, Double thresholdScore)
  {
    String id = IdGenerator.generateId();
    MongoQuiz mongoQuiz = new MongoQuiz(id, name, graded, timeLimit, dueDate, maxAttempts, thresholdScore, mapQuestions(questions));
    mongoQuizRepository.save(mongoQuiz);

    return map(mongoQuiz, questions);
  }

  @Override
  public Quiz update(QuizId quizId, List<Question> questions) throws LmsRepositoryException
  {
    MongoQuiz mongoQuiz = getMongoQuiz(quizId);
    mongoQuiz.setQuestions(mapQuestions(questions));

    mongoQuizRepository.save(mongoQuiz);

    return map(mongoQuiz, questions);
  }

  @Override
  public Quiz update(QuizId quizId, List<Question> questions, Integer maxAttempts, Double thresholdScore) throws LmsRepositoryException
  {
    MongoQuiz mongoQuiz = getMongoQuiz(quizId);

    mongoQuiz.setQuestions(mapQuestions(questions));
    mongoQuiz.setMaxAttempts(maxAttempts);
    mongoQuiz.setThresholdScore(thresholdScore);

    mongoQuizRepository.save(mongoQuiz);

    return map(mongoQuiz, questions);
  }

  @Override
  public Quiz fetchById(QuizId quizId) throws LmsRepositoryException
  {
    MongoQuiz mongoQuiz = getMongoQuiz(quizId);
    return map(mongoQuiz);
  }

  @Override
  public boolean delete(QuizId quizId)
  {
    mongoQuizRepository.deleteById(quizId.getId());

    return true;
  }

  private Quiz map(MongoQuiz mongoQuiz)
  {
    List<Question> questions = new ArrayList<>();

    if (mongoQuiz.getQuestions() == null)
    {
      return new Quiz(QuizId.valueOf(mongoQuiz.getId()), mongoQuiz.getName());
    }

    for (MongoQuestion mongoQuestion : mongoQuiz.getQuestions())
    {
      List<MongoAnswer> mongoAnswers = mongoQuestion.getAnswers();
      Question question = new Question(mongoQuestion.getTitle(), QuestionType.valueOf(mongoQuestion.getType().name()), mongoQuestion.isRequired());
      mongoAnswers.forEach(mongoAnswer -> question.addAnswer(new Answer(mongoAnswer.getValue(), mongoAnswer.isCorrect(), mongoAnswer.getScore())));
      questions.add(question);
    }

    return map(mongoQuiz, questions);
  }

  private Quiz map(MongoQuiz mongoQuiz, List<Question> questions)
  {
    Quiz quiz = new Quiz(QuizId.valueOf(mongoQuiz.getId()), mongoQuiz.getName(), mongoQuiz.isGraded(), mongoQuiz.getTimeLimit(), mongoQuiz.getDueDate());
    questions.forEach(quiz::addQuestion);
    quiz.setMaxAttempts(mongoQuiz.getMaxAttempts());
    quiz.setThresholdScore(mongoQuiz.getThresholdScore());
    return quiz;
  }

  private MongoQuiz getMongoQuiz(QuizId quizId) throws LmsRepositoryException
  {
    Optional<MongoQuiz> optionalQuiz = mongoQuizRepository.findById(quizId.getId());

    if (!optionalQuiz.isPresent())
    {
      throw new LmsRepositoryException("Quiz with the ID: [" + quizId.getId() + "] was not found!");
    }

    return optionalQuiz.get();
  }

  private List<MongoQuestion> mapQuestions(List<Question> questions)
  {
    return questions.stream().map(question -> new MongoQuestion(MongoQuestionType.valueOf(question.getType().name()), question.getTitle(),
        mapAnswers(question.getAnswers()), question.isRequired())).collect(Collectors.toList());
  }

  private List<MongoAnswer> mapAnswers(List<Answer> answers)
  {
    return answers.stream().map(answer -> new MongoAnswer(answer.getValue(), answer.isCorrect(), answer.getScore()))
        .collect(Collectors.toList());
  }
}
