package mn.erin.lms.base.domain.usecase.exam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.LearnerAnswerEntity;
import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.LearnerExamResultInput;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class GetLearnerExcelExamResult extends ExamUseCase<LearnerExamResultInput, byte[]>
{
  private static final String[] HEADERS = { "№", "Шалгалтын асуулт", "Оноо", "Зөв хариулт", "Сонгосон хариулт", "Авсан оноо" };

  public GetLearnerExcelExamResult(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected byte[] executeImpl(LearnerExamResultInput input) throws UseCaseException
  {
    try
    {
      Exam exam = examRepository.findById(ExamId.valueOf(input.getExamId()));

      ExamInteractionDto interaction = lmsRepositoryRegistry.getExamRuntimeDataRepository().getRuntimeData(input.getExamId(), input.getLearnerId())
          .getInteractions()
          .stream().max(Comparator.comparing(ExamInteractionDto::getScore)).orElse(null);

      if (interaction == null)
      {
        throw new UseCaseException("Could not find interaction!");
      }

      List<Question> questions = lmsRepositoryRegistry.getQuestionRepository().getByIds(interaction.getGivenQuestions().stream()
          .map(LearnerQuestionDto::getId).collect(Collectors.toSet()));

      if (questions.size() < interaction.getGivenQuestions().size())
      {
        Set<String> existingQuestionIds = questions.stream().map(question -> question.getId().getId()).collect(Collectors.toSet());
        for (LearnerQuestionDto question : interaction.getGivenQuestions())
        {
          if (!existingQuestionIds.contains(question.getId()))
          {
            Question newQuestion = new Question.Builder(QuestionId.valueOf(question.getId()), question.getValue(), "unknown", new Date())
                .withAnswers(question.getAnswers().stream().map(this::mapToAnswer).collect(Collectors.toList()))
                .withScore(0).build();
            questions.add(newQuestion);
          }
        }
      }

      if (questions.isEmpty())
      {
        throw new UseCaseException("Questions not found!");
      }

      Object[][] excelData = convertToExcel(interaction.getGivenQuestions(), questions);

      String learner = "Суралцагч: " + input.getLearnerId();
      String examDate = "Шалгалтын огноо: " + new SimpleDateFormat("dd-MM-yyyy").format(exam.getExamConfig().getStartDate());
      String examName = "Шалгалтын нэр: " + exam.getName();
      String examResult = "Авсан оноо: " + interaction.getScore() + "/" + exam.getExamConfig().getMaxScore()
          + " (" + new DecimalFormat("0").format(interaction.getScore() != 0 ? interaction.getScore() / exam.getExamConfig().getMaxScore() * 100 : 0) + "%)";
      String thresholdScore = "Тэнцэх оноо: " + exam.getExamConfig().getThresholdScore();
      String sheetTitle = learner + "\n" + examDate + "\n" + examName + "\n" + examResult + "\n" + thresholdScore;

      try (ByteArrayOutputStream result = new ByteArrayOutputStream())
      {
        ExcelWriterUtil.write(true, sheetTitle, HEADERS, excelData, result);
        return result.toByteArray();
      }
    }
    catch (LmsRepositoryException | IOException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Answer mapToAnswer(LearnerAnswerEntity learnerAnswerEntity)
  {
    return new Answer(learnerAnswerEntity.getValue(), learnerAnswerEntity.getIndex(), learnerAnswerEntity.getIndex(), learnerAnswerEntity.getIndex(),
        false, 1);
  }

  private Object[][] convertToExcel(List<LearnerQuestionDto> learnerQuestionDtos, List<Question> questions)
  {
    int dataSize = questions.size();
    Object[][] data = new Object[dataSize][];
    int index = 0;

    for (Question question : questions)
    {
      String value = question.getValue();

      int questionScore = question.getScore();

      LearnerAnswerEntity learnerAnswerEntity = learnerQuestionDtos.stream().filter(e -> e.getId().equals(question.getId().getId())).findFirst()
          .orElseThrow(NullPointerException::new)
          .getAnswers().stream().filter(LearnerAnswerEntity::isSelected).findFirst().orElse(null);

      Answer answer = question.getAnswers().stream().filter(Answer::isCorrect).findFirst().orElse(null);

      String chosenAnswer = learnerAnswerEntity != null ? learnerAnswerEntity.getValue() : "";
      String correctAnswer = answer != null ? answer.getValue() : "unknown";
      int learnerScore = chosenAnswer.equals(correctAnswer) ? questionScore : 0;
      Object[] row = { 0, value, questionScore, correctAnswer, chosenAnswer, learnerScore };
      data[index] = row;
      index++;
    }
    return data;
  }
}
