package mn.erin.lms.jarvis.mongo.implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.QuestionType;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.assessment.MongoQuestionType;
import mn.erin.lms.jarvis.domain.report.model.AssessmentReport;
import mn.erin.lms.jarvis.domain.report.model.AssessmentReportItem;
import mn.erin.lms.jarvis.domain.report.repository.AssessmentReportRepository;
import mn.erin.lms.jarvis.mongo.document.report.MongoAssessmentAnswer;
import mn.erin.lms.jarvis.mongo.document.report.MongoAssessmentReport;
import mn.erin.lms.jarvis.mongo.document.report.MongoAssessmentReportItem;
import mn.erin.lms.jarvis.mongo.repository.MongoAssessmentReportRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AssessmentReportRepositoryImpl implements AssessmentReportRepository
{
  private final MongoAssessmentReportRepository mongoAssessmentReportRepository;

  public AssessmentReportRepositoryImpl(MongoAssessmentReportRepository mongoAssessmentReportRepository)
  {
    this.mongoAssessmentReportRepository = mongoAssessmentReportRepository;
  }

  @Override
  public void save(AssessmentReport report)
  {
    MongoAssessmentReport mongoAssessmentReport = convert(report);
    mongoAssessmentReportRepository.save(mongoAssessmentReport);
  }

  @Override
  public AssessmentReport fetchById(String id) throws LmsRepositoryException
  {
    Optional<MongoAssessmentReport> mongoAssessmentReport = mongoAssessmentReportRepository.findById(id);

    if (!mongoAssessmentReport.isPresent())
    {
      throw new LmsRepositoryException("Assessment report was not found for the assessment with the ID: [" + id + "]");
    }

    return convert(mongoAssessmentReport.get());
  }

  @Override
  public List<AssessmentReport> find(AssessmentId assessmentId, LocalDate startDate, LocalDate endDate)
  {
    List<MongoAssessmentReport> assessmentReports = mongoAssessmentReportRepository.findByAssessmentIdAndDateBetween(assessmentId.getId(),
        startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));

    List<AssessmentReport> result = new ArrayList<>();

    for (MongoAssessmentReport report : assessmentReports)
    {
      result.add(convert(report));
    }
    return result;
  }

  private AssessmentReport convert(MongoAssessmentReport mongoAssessmentReport)
  {
    List<AssessmentReportItem> items = new ArrayList<>();

    for (MongoAssessmentReportItem item : mongoAssessmentReport.getItems())
    {
      List<AssessmentReportItem.Answer> answers = new ArrayList<>();

      for (MongoAssessmentAnswer answer : item.getAnswers())
      {
        if (item.getQuestionType() == MongoQuestionType.FILL_IN_BLANK)
        {
          answers.add(new AssessmentReportItem.Answer(answer.getValue()));
        }
        else
        {
          answers.add(new AssessmentReportItem.Answer(answer.getValue(), answer.getCount()));
        }
      }

      items.add(new AssessmentReportItem(item.getIndex(), item.getQuestion(),
          QuestionType.valueOf(item.getQuestionType().name()), answers));
    }

    return new AssessmentReport(mongoAssessmentReport.getId(), AssessmentId.valueOf(mongoAssessmentReport.getAssessmentId()), items,
        mongoAssessmentReport.getDate());
  }

  private MongoAssessmentReport convert(AssessmentReport assessmentReport)
  {
    List<MongoAssessmentReportItem> items = new ArrayList<>();

    for (AssessmentReportItem item : assessmentReport.getItems())
    {
      List<MongoAssessmentAnswer> answers = new ArrayList<>();

      for (AssessmentReportItem.Answer answer : item.getAnswers())
      {
        if (item.getQuestionType() == QuestionType.FILL_IN_BLANK)
        {
          answers.add(new MongoAssessmentAnswer(answer.getValue()));
        }
        else
        {
          answers.add(new MongoAssessmentAnswer(answer.getValue(), answer.getCount()));
        }
      }

      MongoAssessmentReportItem assessmentReportItem = new MongoAssessmentReportItem(item.getIndex(), item.getQuestion(),
          MongoQuestionType.valueOf(item.getQuestionType().name()), answers);
      items.add(assessmentReportItem);
    }

    return new MongoAssessmentReport(assessmentReport.getReportId(), assessmentReport.getAssessmentId().getId(), assessmentReport.getDate(),
        items);
  }
}
