package mn.erin.lms.base.domain.service.exam;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.aim.UserProvider;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;

/**
 * @author mLkhagvasuren
 */
public class ExamUserServiceImpl implements ExamInteractionService
{
  private static final Logger LOG = LoggerFactory.getLogger(ExamUserServiceImpl.class);

  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final UserProvider userProvider;

  public ExamUserServiceImpl(LmsRepositoryRegistry lmsRepositoryRegistry, UserProvider userProvider)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.userProvider = userProvider;
  }

  @Override
  public Optional<Exam> getCurrentUserOngoingExam()
  {
    String learnerId = userProvider.getCurrentUsername();
    ExamRuntimeData ongoingExamRuntimeData = lmsRepositoryRegistry.getExamRuntimeDataRepository().findRuntimeDataWithOngoingInteraction(learnerId);
    if (ongoingExamRuntimeData != null)
    {
      try
      {
        Exam exam = lmsRepositoryRegistry.getExamRepository().findById(ExamId.valueOf(ongoingExamRuntimeData.getExamId()));
        return Optional.of(exam);
      }
      catch (LmsRepositoryException e)
      {
        LOG.error("Failed to get ongoing exam for user [{}]", learnerId);
      }
    }
    return Optional.empty();
  }
}
