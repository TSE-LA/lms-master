package mn.erin.lms.base.domain.usecase.enrollment;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollment;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuestionRepository;

/**
 * @author Galsan Bayart.
 */
public class UpdateExamEnrollment implements UseCase<ExamEnrollment, Void>
{
  private final QuestionRepository questionRepository;
  private final ExamRepository examRepository;
  private final ExamEnrollmentRepository examEnrollmentRepository;

  private final Random random = new Random();


  public UpdateExamEnrollment(QuestionRepository questionRepository, ExamRepository examRepository,
      ExamEnrollmentRepository examEnrollmentRepository)
  {
    this.questionRepository = questionRepository;
    this.examRepository = examRepository;
    this.examEnrollmentRepository = examEnrollmentRepository;
  }

  @Override
  public Void execute(ExamEnrollment input) throws UseCaseException
  {
    Exam exam;
    try
    {
      exam = examRepository.findById(ExamId.valueOf(input.getExamId()));
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }

    Set<String> enrollQuestion = new HashSet<>(exam.getExamConfig().getQuestionIds());
    for (RandomQuestionConfig randomQuestionConfig : exam.getExamConfig().getRandomQuestionConfigs())
    {
      List<Question> questions = questionRepository.findByGroupIdAndCategoryAndScore(randomQuestionConfig.getGroupId(), randomQuestionConfig.getCategoryId(), randomQuestionConfig.getScore());
      int questionSize = questions.size();
      Set<Integer> questionIndex = new HashSet<>();
      while (questionIndex.size() != randomQuestionConfig.getAmount()){
        questionIndex.add(random.nextInt(questionSize));
      }
      enrollQuestion.addAll(questionIndex.stream().map(index-> questions.get(index).getId().getId()).collect(Collectors.toList()));
    }

    examEnrollmentRepository.update(input);
    return null;
  }
}
