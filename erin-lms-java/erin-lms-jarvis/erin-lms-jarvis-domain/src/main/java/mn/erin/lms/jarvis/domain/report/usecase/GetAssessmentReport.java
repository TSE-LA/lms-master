package mn.erin.lms.jarvis.domain.report.usecase;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.jarvis.domain.repository.JarvisLmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.jarvis.domain.service.JarvisLmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.jarvis.domain.report.model.AssessmentReport;
import mn.erin.lms.jarvis.domain.report.model.AssessmentReportItem;
import mn.erin.lms.jarvis.domain.report.repository.AssessmentReportRepository;
import mn.erin.lms.jarvis.domain.report.service.CourseAnalyticsService;
import mn.erin.lms.jarvis.domain.report.usecase.dto.GetAssessmentReportInput;
import mn.erin.lms.base.domain.util.DateUtil;

import static mn.erin.lms.base.domain.model.assessment.QuestionType.FILL_IN_BLANK;
import static mn.erin.lms.base.domain.model.assessment.QuestionType.MULTI_CHOICE;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class, Manager.class })
public class GetAssessmentReport extends LmsUseCase<GetAssessmentReportInput, List<AssessmentReportItem>>
{
  private final AssessmentReportRepository assessmentReportRepository;
  private AssessmentRepository assessmentRepository;
  private QuizRepository quizRepository;
  private CourseRepository courseRepository;
  private final CourseAnalyticsService courseAnalyticsService;

  public GetAssessmentReport(JarvisLmsRepositoryRegistry lmsRepositoryRegistry,
      JarvisLmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.assessmentReportRepository = lmsRepositoryRegistry.getAssessmentReportRepository();
    this.assessmentRepository = lmsRepositoryRegistry.getAssessmentRepository();
    this.quizRepository = lmsRepositoryRegistry.getQuizRepository();
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
    this.courseAnalyticsService = lmsServiceRegistry.getCourseAnalyticsService();
  }

  @Override
  protected List<AssessmentReportItem> executeImpl(GetAssessmentReportInput input) throws UseCaseException
  {
    List<AssessmentReport> reports = new ArrayList<>();
    if (input.getRequestType().equals("all"))
    {
      reports = assessmentReportRepository
          .find(AssessmentId.valueOf(input.getAssessmentId()), input.getStartDate(), input.getEndDate());
    }
    else
    {
      try
      {
        String currentDepartmentId = lmsServiceRegistry.getDepartmentService().getCurrentDepartmentId();
          List<Map<Integer, String>> learnersAssessmentData = courseAnalyticsService
              .getLearnersAssessmentDatas(input.getCourseId(), DepartmentId.valueOf(currentDepartmentId));
          Course course = courseRepository.fetchById(CourseId.valueOf(input.getCourseId()));
          Assessment assessment = assessmentRepository.findById(AssessmentId.valueOf(course.getAssessmentId()));
          Quiz quiz = quizRepository.fetchById(assessment.getQuizId());

          String assessmentReportId = assessment.getAssessmentId().getId() + input.getCourseId() + DateUtil.generateIdBasedOnDate();
          AssessmentReport assessmentReport = new AssessmentReport(assessmentReportId, assessment.getAssessmentId());

          getQuiz(quiz, assessmentReport);

          LocalDateTime modifiedDate = course.getCourseDetail().getDateInfo().getModifiedDate();

          if (input.getStartDate().atStartOfDay().compareTo(modifiedDate) * modifiedDate
              .compareTo(LocalDateTime.of(input.getEndDate(), LocalTime.MAX)) > 0)
          {
            for (Map<Integer, String> learnersAssessmentDatum : learnersAssessmentData)
            {
              getReportAnswers(assessmentReport.getItems(), learnersAssessmentDatum);
            }
          }
          reports.add(assessmentReport);
        }
      catch (LmsRepositoryException e)
      {
        throw new UseCaseException("failed to get assessment report", e);
      }
    }
    if (reports.isEmpty())
    {
      return new ArrayList<>();
    }

    AssessmentReport report = reduce(reports);
    return report.getItems();
  }

  private void getQuiz(Quiz quiz, AssessmentReport assessmentReport)
  {
    for (int index = 0; index < quiz.getQuestions().size(); index++)
    {
      Question question = quiz.getQuestions().get(index);
      List<AssessmentReportItem.Answer> answers = question.getAnswers()
          .stream().map(answer -> question.getType() == FILL_IN_BLANK ?
              new AssessmentReportItem.Answer(answer.getValue()) : new AssessmentReportItem.Answer(answer.getValue(), 0))
          .collect(Collectors.toList());
      assessmentReport.add(new AssessmentReportItem(index, question.getTitle(), question.getType(), answers));
    }
  }

  private void getReportAnswers(List<AssessmentReportItem> assessmentReportItems, Map<Integer, String> learnersAssessmentData)
  {

    for (AssessmentReportItem item : assessmentReportItems)
    {
      if (item.getQuestionType().equals(FILL_IN_BLANK))
      {
        item.addAnswer(new AssessmentReportItem.Answer(learnersAssessmentData.get(item.getIndex() + 1)));
      }
      else if (item.getQuestionType().equals(MULTI_CHOICE))
      {
        String learnerAnswer = learnersAssessmentData.get(item.getIndex() + 1);
        if (learnerAnswer.contains(","))
        {
          String[] values = learnerAnswer.split(",");

          for (String v : values)
          {
            Optional<AssessmentReportItem.Answer> answer = item.getAnswers()
                .stream().filter(a -> a.getValue().equals(v))
                .findFirst();
            answer.ifPresent(AssessmentReportItem.Answer::increment);
          }
        }
        else
        {
          Optional<AssessmentReportItem.Answer> answer = item.getAnswers()
              .stream().filter(a -> a.getValue().equals(learnersAssessmentData.get(item.getIndex() + 1)))
              .findFirst();
          answer.ifPresent(AssessmentReportItem.Answer::increment);
        }
      }
      else
      {
        Optional<AssessmentReportItem.Answer> answer = item.getAnswers()
            .stream().filter(a -> a.getValue().equals(learnersAssessmentData.get(item.getIndex() + 1)))
            .findFirst();
        answer.ifPresent(AssessmentReportItem.Answer::increment);
      }
    }
  }

  private AssessmentReport reduce(List<AssessmentReport> reports)
  {
    AssessmentReport report = reports.get(0);
    List<AssessmentReportItem> items = report.getItems();

    for (int index = 1; index < reports.size(); index++)
    {
      AssessmentReport assessmentReport = reports.get(index);
      List<AssessmentReportItem> assessmentReportItems = assessmentReport.getItems();

      for (AssessmentReportItem item : items)
      {
        List<AssessmentReportItem.Answer> answers = getAnswer(assessmentReportItems, item);
        if (item.getQuestionType() == FILL_IN_BLANK)
        {
          item.getAnswers().addAll(answers);
        }
        else
        {
          combine(item.getAnswers(), answers);
        }
      }
    }

    return new AssessmentReport(report.getReportId(), report.getAssessmentId(), items, report.getDate());
  }

  private List<AssessmentReportItem.Answer> getAnswer(List<AssessmentReportItem> items, AssessmentReportItem item)
  {
    for (AssessmentReportItem i : items)
    {
      if (item.getQuestion().equals(i.getQuestion()))
      {
        return i.getAnswers();
      }
    }

    throw new IllegalStateException();
  }

  private void combine(List<AssessmentReportItem.Answer> a1, List<AssessmentReportItem.Answer> a2)
  {
    for (AssessmentReportItem.Answer answer1 : a1)
    {
      for (AssessmentReportItem.Answer answer2 : a2)
      {
        if (answer1.getValue().equals(answer2.getValue()))
        {
          answer1.add(answer2.getCount());
        }
      }
    }
  }
}
