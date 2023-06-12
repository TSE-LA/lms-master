package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.LearnerScoreForExam;

/**
 * @author Erdene-Ochir Bazarragchaa
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class  })
public class GetLearnerScoresForExam extends ExamUseCase<String, List<LearnerScoreForExam>>
{

  public GetLearnerScoresForExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected List<LearnerScoreForExam> executeImpl(String examId) throws UseCaseException, LmsRepositoryException
  {
    try
    {
      Validate.notBlank(examId);
    }
    catch (NullPointerException e)
    {
      throw new UseCaseException("Get learner exam: exam id cannot be null");
    }
    Exam exam = lmsRepositoryRegistry.getExamRepository().findById(ExamId.valueOf(examId));
    Set<String> learnersSorted = new TreeSet<>(lmsRepositoryRegistry.getExamEnrollmentRepository().getAllReadLearnerByExamId(examId));
    Map<String, ExamRuntimeData> runtimeDataByLearner = getExamRuntimeDataAsMap(examId);

    List<LearnerScoreForExam> learnerScores = new ArrayList<>();
    for (String learnerName : learnersSorted)
    {
      ExamRuntimeData learnerRuntimeData = runtimeDataByLearner.get(learnerName);
      if (learnerRuntimeData == null) // enrolled but never opened the exam
      {
        learnerScores.add(LearnerScoreForExam.empty(learnerName, exam.getExamConfig()));
      }
      else
      {
        ExamInteractionDto interaction = learnerRuntimeData.getInteractionWithMaxScore();
        if (interaction == null) // opened the exam but no attempt has been made
        {
          learnerScores.add(LearnerScoreForExam.empty(learnerName, exam.getExamConfig()));
        }
        else // has at least one attempt
        {
          learnerScores.add(LearnerScoreForExam.from(learnerName, exam.getExamConfig(), interaction));
        }
      }
    }

    return Collections.unmodifiableList(learnerScores);
  }

  private Map<String, ExamRuntimeData> getExamRuntimeDataAsMap(String examId) throws LmsRepositoryException
  {
    List<ExamRuntimeData> runtimeData = lmsRepositoryRegistry.getExamRuntimeDataRepository().listExamRuntimeData(examId);
    Map<String, ExamRuntimeData> runtimeDataByLearnerName = new HashMap<>();
    for (ExamRuntimeData learnerRuntimeData : runtimeData)
    {
      runtimeDataByLearnerName.put(learnerRuntimeData.getLearnerId(), learnerRuntimeData);
    }
    return runtimeDataByLearnerName;
  }
}
