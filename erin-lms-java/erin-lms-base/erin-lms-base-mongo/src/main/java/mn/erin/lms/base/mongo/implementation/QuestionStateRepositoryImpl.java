package mn.erin.lms.base.mongo.implementation;

import java.util.List;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.exam.question.QuestionState;
import mn.erin.lms.base.domain.model.exam.question.QuestionStateId;
import mn.erin.lms.base.domain.repository.QuestionStateRepository;
import mn.erin.lms.base.mongo.document.exam.question.MongoQuestionState;
import mn.erin.lms.base.mongo.repository.MongoQuestionStateRepository;

/**
 * @author Galsan Bayart
 */
public class QuestionStateRepositoryImpl implements QuestionStateRepository
{
  private final MongoQuestionStateRepository mongoQuestionStateRepository;

  public QuestionStateRepositoryImpl(MongoQuestionStateRepository mongoQuestionStateRepository)
  {
    this.mongoQuestionStateRepository = mongoQuestionStateRepository;
  }

  @Override
  public QuestionState create(QuestionState questionState)
  {
    MongoQuestionState mongoQuestionCategory = new MongoQuestionState(questionState.getName(), questionState.getTenantId());
    return mapToQuestionState(mongoQuestionStateRepository.save(mongoQuestionCategory));
  }

  @Override
  public QuestionState update(QuestionState questionState)
  {
    MongoQuestionState mongoQuestionCategory = new MongoQuestionState(questionState.getQuestionStateId().getId(), questionState.getName(),
        questionState.getTenantId());
    return mapToQuestionState(mongoQuestionStateRepository.save(mongoQuestionCategory));
  }

  @Override
  public List<QuestionState> getAllByTenantId(String tenantId)
  {
    return mongoQuestionStateRepository.getAllByTenantId(tenantId).stream().map(this::mapToQuestionState).collect(Collectors.toList());
  }

  @Override
  public boolean delete(String questionStateId)
  {
    mongoQuestionStateRepository.deleteById(questionStateId);
    return true;
  }

  public QuestionState mapToQuestionState(MongoQuestionState mongoQuestionState)
  {
    return new QuestionState(QuestionStateId.valueOf(mongoQuestionState.getTenantId()), mongoQuestionState.getName(), mongoQuestionState.getTenantId());
  }
}
