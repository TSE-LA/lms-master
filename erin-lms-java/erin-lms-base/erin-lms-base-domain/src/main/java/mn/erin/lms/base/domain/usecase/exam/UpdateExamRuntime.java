package mn.erin.lms.base.domain.usecase.exam;

import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.UpdateExamRuntimeInput;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;

/**
 * @author Byambajav
 */
@Authorized(users = { LmsUser.class })
public class UpdateExamRuntime extends ExamUseCase<UpdateExamRuntimeInput, Void>
{
  public UpdateExamRuntime(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected Void executeImpl(UpdateExamRuntimeInput input) throws LmsRepositoryException
  {
    String learnerId = lmsServiceRegistry.getAuthenticationService().getCurrentUsername();
    ExamRuntimeData examRuntimeData = examRuntimeDataRepository.getRuntimeData(input.getExamId(), learnerId);
    for (ExamInteractionDto examInteraction : examRuntimeData.getInteractions())
    {
      if (examInteraction.isOngoing())
      {
        examInteraction.setSpentTime(examInteraction.getSpentTime() + input.getSpentTime());
        examInteraction.setGivenQuestions(input.getLearnerQuestion());
        break;
      }
    }
    examRuntimeDataRepository.update(input.getExamId(), learnerId, examRuntimeData.getInteractions());
    return null;
  }
}
