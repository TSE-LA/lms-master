package mn.erin.lms.base.domain.usecase.exam;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.LearnerAnswerEntity;
import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionStatus;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.StartExamDto;

/**
 * @author Galsan Bayart.
 */

@Authorized(users = { LmsUser.class })
public class StartExam extends ExamUseCase<String, StartExamDto>
{

  public StartExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  private final Random random = new Random();

  @Override
  protected StartExamDto executeImpl(String examId) throws UseCaseException
  {
    try
    {
      Validate.notBlank(examId);
      ensureSingleExamInstance(examId);
      Exam exam = examRepository.findById(ExamId.valueOf(examId));
      String learnerId = lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();
      ExamRuntimeData runtimeData = !examRuntimeDataRepository.checkIfExists(examId, learnerId) ?
          examRuntimeDataRepository.create(learnerId, examId, exam.getExamConfig().getMaxScore(),
              exam.getExamConfig().getAttempt(), exam.getExamConfig().getDuration(), exam.getExamConfig().getThresholdScore()) :
          examRuntimeDataRepository.getRuntimeData(examId, learnerId);
      List<ExamInteractionDto> interactions = runtimeData.getInteractions();
      Date now = new Date();
      ExamInteractionDto currentInteraction = interactions.stream().filter(ExamInteractionDto::isOngoing).findFirst().orElse(null);
      // create an ongoing interaction if there's none
      if (currentInteraction == null)
      {
        currentInteraction = ExamInteractionDto.newOngoingInteraction(now);
        currentInteraction.setGivenQuestions(getQuestions(examId));
        interactions.add(currentInteraction);
        examRuntimeDataRepository.update(examId, learnerId, interactions);
      }
      return new StartExamDto(exam.getName(), getDurationInSeconds(runtimeData.getDuration()),
          getRemainingTimeAsSeconds(exam.getExamConfig().isAutoStart() ? exam.getActualStartDate() : currentInteraction.getInitialLaunch(),
              now, runtimeData.getDuration()), currentInteraction.getGivenQuestions());
    }
    catch (UseCaseException | NullPointerException | IllegalArgumentException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private void ensureSingleExamInstance(String examId) throws UseCaseException
  {
    Optional<Exam> ongoingExam = lmsServiceRegistry.getExamInteractionService().getCurrentUserOngoingExam();
    if (ongoingExam.isPresent() && !Objects.equals(ongoingExam.get().getId(), ExamId.valueOf(examId)))
    {
      throw new UseCaseException(MessageFormat.format("User already has an ongoing exam [{0}]", ongoingExam.get().getName()));
    }
  }

  private long getRemainingTimeAsSeconds(Date examStartDate, Date currentDate, int duration)
  {
    long differenceInSeconds = (currentDate.getTime() - examStartDate.getTime()) / 1000;
    long remainingTime = (duration * 60L) - differenceInSeconds;
    return remainingTime > 0 ? remainingTime : 0;
  }

  private long getDurationInSeconds(int duration)
  {
    return duration * 60L;
  }

  private List<LearnerQuestionDto> getQuestions(String examId) throws UseCaseException
  {
    Exam exam = getExam(examId);
    Set<String> questionIds = exam.getExamConfig().getQuestionIds();

    if (!exam.getExamConfig().getRandomQuestionConfigs().isEmpty())
    {
      getRandomQuestions(questionIds, exam.getExamConfig().getRandomQuestionConfigs());
    }

    if (!questionIds.isEmpty())
    {
      List<Question> questions = lmsRepositoryRegistry.getQuestionRepository().getByIds(questionIds);
      updateQuestionStatus(questions);

      if (exam.getExamConfig().isShuffleAnswer())
      {
        ShuffleAnswer shuffleAnswer = new ShuffleAnswer();
        shuffleAnswer.execute(questions);
      }

      if (exam.getExamConfig().isShuffleQuestion())
      {
        ShuffleQuestions shuffleQuestions = new ShuffleQuestions();
        shuffleQuestions.execute(questions);
      }

      return mapToLearnerQuestions(questions);
    }
    throw new UseCaseException("Question configuration error, exam requested question count is 0");
  }

  private void getRandomQuestions(Set<String> questionIds, Set<RandomQuestionConfig> randomQuestionConfigs) throws UseCaseException
  {
    List<RandomQuestionConfig> reducedQuestionConfig = new ArrayList<>();

    for (RandomQuestionConfig randomQuestionConfig : randomQuestionConfigs)
    {
      if (randomQuestionConfig.getAmount() > 0)
      {
        Optional<RandomQuestionConfig> optional = reducedQuestionConfig.stream().filter(
            existing ->
                existing.getCategoryId().equals(randomQuestionConfig.getCategoryId()) &&
                    existing.getGroupId().equals(randomQuestionConfig.getGroupId()) &&
                    existing.getScore() == randomQuestionConfig.getScore()
        ).findFirst();

        if (optional.isPresent())
        {
          RandomQuestionConfig foundQuestionConfig = optional.get();
          foundQuestionConfig.setAmount(foundQuestionConfig.getAmount() + randomQuestionConfig.getAmount());
        }
        else
        {
          reducedQuestionConfig.add(randomQuestionConfig);
        }
      }
    }
    randomizeQuestions(questionIds, reducedQuestionConfig);
  }

  private void randomizeQuestions(Set<String> questionIds, List<RandomQuestionConfig> reducedQuestionConfig) throws UseCaseException
  {
    for (RandomQuestionConfig randomQuestionConfig : reducedQuestionConfig)
    {
      List<Question> tempQuestions = questionRepository.findByGroupIdAndCategoryAndScore(randomQuestionConfig.getGroupId(), randomQuestionConfig.getCategoryId(), randomQuestionConfig.getScore());

      int questionSize = tempQuestions.size();
      if (randomQuestionConfig.getAmount() <= questionSize)
      {
        for (int i = 0; i < randomQuestionConfig.getAmount() && questionSize != 0; i++, questionSize--)
        {
          int index = random.nextInt(questionSize);
          questionIds.add(tempQuestions.remove(index).getId().getId());
        }
      }
      else
      {
        throw new UseCaseException("Random question amount is greater than existing questions!");
      }
    }
  }

  private List<LearnerQuestionDto> mapToLearnerQuestions(List<Question> questions)
  {
    List<LearnerQuestionDto> learnerQuestionDto = new ArrayList<>();
    for (Question question : questions)
    {
      learnerQuestionDto.add(mapToLearnerQuestion(question));
    }
    return learnerQuestionDto;
  }

  private LearnerQuestionDto mapToLearnerQuestion(Question question)
  {
    return new LearnerQuestionDto(
        question.getId().getId(),
        question.getValue(),
        question.getType(),
        mapToLearnerAnswer(question.getAnswers()),
        getImagePath(question));
  }

  private List<LearnerAnswerEntity> mapToLearnerAnswer(List<Answer> answers)
  {
    List<LearnerAnswerEntity> learnerAnswerEntities = new ArrayList<>();
    for (Answer answer : answers)
    {
      learnerAnswerEntities.add(new LearnerAnswerEntity(answer.getValue(), answer.getIndex(), false));
    }
    return learnerAnswerEntities;
  }

  private String getImagePath(Question question)
  {
    if (question.getDetail().getImageName() != null)
    {
      return "/alfresco/Exams/" + question.getId().getId() + "/" + question.getDetail().getImageName();
    }
    else
    {
      return null;
    }
  }

  private void updateQuestionStatus(List<Question> questions)
  {
    Set<String> newQuestions = questions.stream().filter(question -> question.getStatus().equals(QuestionStatus.NEW))
        .map(Question::getId).map(EntityId::getId).collect(Collectors.toSet());
    if (!newQuestions.isEmpty())
    {
      questionRepository.updateStatus(newQuestions, QuestionStatus.USED.name());
    }
  }
}
