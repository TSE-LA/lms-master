package mn.erin.lms.unitel.domain.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.util.DateUtil;
import mn.erin.lms.unitel.domain.model.report.AssessmentReport;
import mn.erin.lms.unitel.domain.model.report.AssessmentReportItem;
import mn.erin.lms.unitel.domain.repository.AssessmentReportRepository;
import mn.erin.lms.unitel.domain.repository.UnitelLmsRepositoryRegistry;
import mn.erin.lms.unitel.domain.service.UnitelLmsServiceRegistry;
import mn.erin.lms.unitel.domain.usecase.dto.GetAssessmentReportInput;

import static mn.erin.lms.base.domain.model.assessment.QuestionType.FILL_IN_BLANK;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class, Supervisor.class, Manager.class })
public class GetAssessmentReport extends LmsUseCase<GetAssessmentReportInput, List<AssessmentReportItem>>
{
  private final AssessmentReportRepository assessmentReportRepository;
  private final AssessmentRepository assessmentRepository;
  private final CourseRepository courseRepository;

  public GetAssessmentReport(UnitelLmsRepositoryRegistry lmsRepositoryRegistry,
      UnitelLmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.assessmentReportRepository = lmsRepositoryRegistry.getAssessmentReportRepository();
    this.assessmentRepository = lmsRepositoryRegistry.getAssessmentRepository();
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
  }

  @Override
  protected List<AssessmentReportItem> executeImpl(GetAssessmentReportInput input) throws UseCaseException
  {
    List<AssessmentReport> reports;
    if (input.getRequestType().equals("all"))
    {
      reports = assessmentReportRepository
          .find(AssessmentId.valueOf(input.getAssessmentId()), input.getStartDate(), input.getEndDate());
    }
    else
    {
      Assessment assessment;
      try
      {
        Course course = courseRepository.fetchById(CourseId.valueOf(input.getCourseId()));
        assessment = assessmentRepository.findById(AssessmentId.valueOf(course.getAssessmentId()));
      }
      catch (LmsRepositoryException e)
      {
        throw new UseCaseException(e.getMessage(), e);
      }

      Set<String> ids = new HashSet<>();
      for (LocalDate date = input.getStartDate(); date.isBefore(input.getEndDate()) || date.isEqual(input.getEndDate()); date = date.plusDays(1))
      {
        ids.add(assessment.getAssessmentId().getId() + input.getCourseId() + DateUtil.convertDate(date));
      }

      reports = assessmentReportRepository.findAll(ids);
    }

    if (reports.isEmpty())
    {
      return new ArrayList<>();
    }

    AssessmentReport report = reduce(reports);
    return report.getItems();
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
