package mn.erin.lms.base.mongo.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.repository.ExamRuntimeDataRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeStatus;
import mn.erin.lms.base.mongo.document.exam.MongoExamRuntimeData;
import mn.erin.lms.base.mongo.repository.MongoExamRuntimeDataRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Byambajav
 */
public class ExamRuntimeDataRepositoryImpl implements ExamRuntimeDataRepository
{
  private final MongoExamRuntimeDataRepository mongoExamRuntimeDataRepository;

  public ExamRuntimeDataRepositoryImpl(MongoExamRuntimeDataRepository mongoExamRuntimeDataRepository)
  {
    this.mongoExamRuntimeDataRepository = mongoExamRuntimeDataRepository;
  }

  @Override
  public List<ExamRuntimeData> listExamRuntimeData(String examId) throws LmsRepositoryException
  {
    Iterator<MongoExamRuntimeData> runtimeDataIterator = mongoExamRuntimeDataRepository.findAllByExamId(examId).listIterator();
    List<ExamRuntimeData> runtimeDataList = new ArrayList<>();
    while (runtimeDataIterator.hasNext())
    {
      runtimeDataList.add(mapToRuntimeData(runtimeDataIterator.next()));
    }
    return runtimeDataList;
  }

  @Override
  public ExamRuntimeData findRuntimeDataWithOngoingInteraction(String learnerId)
  {
    return mongoExamRuntimeDataRepository.findAllByLearnerId(learnerId)
        .stream()
        .filter(runtimeData -> runtimeData.getExamInteraction().stream().anyMatch(ExamInteractionDto::isOngoing))
        .findFirst()
        .map(ExamRuntimeDataRepositoryImpl::mapToRuntimeData)
        .orElse(null);
  }

  @Override
  public ExamRuntimeData getRuntimeData(String examId, String learnerId) throws LmsRepositoryException
  {
    return mapToRuntimeData(mongoExamRuntimeDataRepository.findByLearnerIdAndExamId(learnerId, examId));
  }

  @Override
  public boolean checkIfExists(String examId, String learnerId) throws LmsRepositoryException
  {
    Optional<MongoExamRuntimeData> examRuntimeData = mongoExamRuntimeDataRepository.findByExamIdAndLearnerId(examId, learnerId);
    return examRuntimeData.isPresent();
  }

  @Override
  public ExamRuntimeData create(String learnerId, String examId, double maxScore, int maxAttempt, int duration, double threshold)
      throws LmsRepositoryException
  {
    String id = IdGenerator.generateId();
    MongoExamRuntimeData mongoExamRuntimeData = new MongoExamRuntimeData(id, examId, learnerId, maxScore, duration, maxAttempt, threshold);
    mongoExamRuntimeData.setStatus(ExamRuntimeStatus.UNSET);
    return mapToRuntimeData(mongoExamRuntimeDataRepository.save(mongoExamRuntimeData));
  }

  @Override
  public ExamRuntimeData update(String examId, String learnerId, List<ExamInteractionDto> interactions) throws LmsRepositoryException
  {
    MongoExamRuntimeData examRuntimeData = mongoExamRuntimeDataRepository.findByLearnerIdAndExamId(learnerId, examId);
    examRuntimeData.setExamInteraction(interactions);
    return mapToRuntimeData(mongoExamRuntimeDataRepository.save(examRuntimeData));
  }

  @Override
  public void update(String examId, String learnerId, ExamRuntimeStatus status) throws LmsRepositoryException
  {
    MongoExamRuntimeData examRuntimeData = mongoExamRuntimeDataRepository.findByLearnerIdAndExamId(learnerId, examId);
    examRuntimeData.setStatus(status);
    mongoExamRuntimeDataRepository.save(examRuntimeData);
  }

  @Override
  public boolean delete(String examId, String learnerId) throws LmsRepositoryException
  {
    return false;
  }

  @Override
  public void updateMaxScore(String id, int score) throws LmsRepositoryException
  {
    Optional<MongoExamRuntimeData> optional = mongoExamRuntimeDataRepository.findById(id);
    MongoExamRuntimeData mongoExamRuntimeData = optional.orElseThrow(() -> new LmsRepositoryException(String.format("Could not find runtime data", id)));
    mongoExamRuntimeData.setMaxScore(score);
    this.mongoExamRuntimeDataRepository.save(mongoExamRuntimeData);
  }

  @Override
  public List<ExamRuntimeData> getRuntimeData(String learnerId)
  {
    return mongoExamRuntimeDataRepository.findAllByLearnerId(learnerId)
        .stream()
        .map(ExamRuntimeDataRepositoryImpl::mapToRuntimeData).collect(Collectors.toList());
  }

  private static ExamRuntimeData mapToRuntimeData(MongoExamRuntimeData mongoExamRuntimeData)
  {
    ExamRuntimeData examRuntimeData = new ExamRuntimeData(
        mongoExamRuntimeData.getId(),
        mongoExamRuntimeData.getExamId(),
        mongoExamRuntimeData.getLearnerId(),
        mongoExamRuntimeData.getMaxScore(),
        mongoExamRuntimeData.getDuration(),
        mongoExamRuntimeData.getMaxAttempt(),
        mongoExamRuntimeData.getThresholdScore()
    );
    examRuntimeData.setInteractions(mongoExamRuntimeData.getExamInteraction());
    return examRuntimeData;
  }
}
