package mn.erin.lms.unitel.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.QuestionType;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.util.DateUtil;
import mn.erin.lms.base.scorm.constants.DataModelConstants;
import mn.erin.lms.base.scorm.service.AssessmentReportException;
import mn.erin.lms.base.scorm.service.AssessmentReportService;
import mn.erin.lms.unitel.domain.model.report.AssessmentReport;
import mn.erin.lms.unitel.domain.model.report.AssessmentReportItem;
import mn.erin.lms.unitel.domain.repository.AssessmentReportRepository;
import mn.erin.lms.unitel.domain.repository.UnitelLmsRepositoryRegistry;

/**
 * @author Erdenetulga
 */
public class AssessmentReportServiceImpl implements AssessmentReportService
{
  private CourseRepository courseRepository;
  private AssessmentRepository assessmentRepository;
  private QuizRepository quizRepository;
  private AssessmentReportRepository assessmentReportRepository;

  public AssessmentReportServiceImpl(UnitelLmsRepositoryRegistry unitelLmsRepositoryRegistry)
  {
    this.courseRepository = unitelLmsRepositoryRegistry.getCourseRepository();
    this.assessmentReportRepository = unitelLmsRepositoryRegistry.getAssessmentReportRepository();
    this.quizRepository = unitelLmsRepositoryRegistry.getQuizRepository();
    this.assessmentRepository = unitelLmsRepositoryRegistry.getAssessmentRepository();
  }

  @Override
  public void save(Map<String, Object> data, String courseId) throws AssessmentReportException
  {

    String suspendData = (String) data.get(DataModelConstants.CMI_SUSPEND_DATA);

    Course course;
    Assessment assessment;
    Quiz quiz;

    try
    {
      course = courseRepository.fetchById(CourseId.valueOf(courseId));
      assessment = assessmentRepository.findById(AssessmentId.valueOf(course.getAssessmentId()));
      quiz = quizRepository.fetchById(assessment.getQuizId());
    }
    catch (LmsRepositoryException e)
    {
      throw new AssessmentReportException(e);
    }

    String assessmentReportId = assessment.getAssessmentId().getId() + courseId + DateUtil.generateIdBasedOnDate();

    AssessmentReport assessmentReport;

    try
    {
      assessmentReport = assessmentReportRepository.fetchById(assessmentReportId);
    }
    catch (LmsRepositoryException e)
    {
      assessmentReport = new AssessmentReport(assessmentReportId, assessment.getAssessmentId());
      for (int index = 0; index < quiz.getQuestions().size(); index++)
      {
        Question question = quiz.getQuestions().get(index);
        List<AssessmentReportItem.Answer> answers = question.getAnswers()
            .stream().map(answer -> question.getType() == QuestionType.FILL_IN_BLANK ? new AssessmentReportItem.Answer(answer.getValue())
                : new AssessmentReportItem.Answer(answer.getValue(), 0))
            .collect(Collectors.toList());
        assessmentReport.add(new AssessmentReportItem(index, question.getTitle(), question.getType(), answers));
      }
      assessmentReport.setDate(LocalDateTime.now());
    }

    if (!StringUtils.isBlank(suspendData) && !suspendData.equals("unknown"))
    {
      this.updateSingleChoice(assessmentReport, suspendData);
      this.updateMultiChoice(assessmentReport, suspendData);
      this.updateFillInBlank(assessmentReport, suspendData);
    }

    assessmentReportRepository.save(assessmentReport);
  }

  private void updateSingleChoice(AssessmentReport assessmentReport, String suspendData)
  {
    String[] split = !(suspendData.split("Å")[0].equals("singleChoice")) ?
        suspendData.split("Å")[0].split("Â") : null;

    if (split != null)
    {
      for (int index = 1; index < split.length; index++)
      {
        String[] pair = split[index].split("À");
        int key = Integer.parseInt(pair[0]);
        String value = pair[1];

        List<AssessmentReportItem> items = assessmentReport.getItems();
        items.forEach(item -> {
          if (item.getIndex().equals(key - 1))
          {
            Optional<AssessmentReportItem.Answer> answer = item.getAnswers().stream().filter(a -> a.getValue().equals(value))
                .findFirst();
            answer.ifPresent(AssessmentReportItem.Answer::increment);
          }
        });
      }
    }
  }

  private void updateMultiChoice(AssessmentReport assessmentReport, String suspendData)
  {
    String[] split = !(suspendData.split("Å")[1].equals("multiChoice")) ?
        suspendData.split("Å")[1].split("Â") : null;

    if (split != null)
    {
      for (int index = 1; index < split.length; index++)
      {
        String[] pair = split[index].split("À");
        int key = Integer.parseInt(pair[0]);
        String value = pair[1];

        List<AssessmentReportItem> items = assessmentReport.getItems();
        if (value.contains(","))
        {
          String[] values = value.split(",");
          items.forEach(item -> {
            if (item.getIndex().equals(key - 1))
            {
              for (String v : values)
              {
                Optional<AssessmentReportItem.Answer> answer = item.getAnswers().stream().filter(a -> a.getValue().equals(v))
                    .findFirst();
                answer.ifPresent(AssessmentReportItem.Answer::increment);
              }
            }
          });
        }
        else
        {
          items.forEach(item -> {
            if (item.getIndex().equals(key - 1))
            {
              Optional<AssessmentReportItem.Answer> answer = item.getAnswers().stream().filter(a -> a.getValue().equals(value))
                  .findFirst();
              answer.ifPresent(AssessmentReportItem.Answer::increment);
            }
          });
        }
      }
    }
  }

  private void updateFillInBlank(AssessmentReport assessmentReport, String suspendData)
  {
    String[] split = !(suspendData.split("Å")[2].equals("fillInBlank")) ?
        suspendData.split("Å")[2].split("Â") : null;

    if (split != null)
    {
      for (int index = 1; index < split.length; index++)
      {
        String[] pair = split[index].split("À");
        int key = Integer.parseInt(pair[0]);
        String value = pair[1];

        List<AssessmentReportItem> items = assessmentReport.getItems();
        items.forEach(item -> {
          if (item.getIndex().equals(key - 1))
          {
            item.addAnswer(new AssessmentReportItem.Answer(value));
          }
        });
      }
    }
  }
}
