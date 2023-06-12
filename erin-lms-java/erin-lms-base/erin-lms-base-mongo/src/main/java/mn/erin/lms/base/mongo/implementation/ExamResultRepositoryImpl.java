package mn.erin.lms.base.mongo.implementation;

import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.exam.ExamResult;
import mn.erin.lms.base.domain.model.exam.ExamResultEntity;
import mn.erin.lms.base.domain.repository.ExamResultRepository;
import mn.erin.lms.base.mongo.document.exam.MongoExamResult;
import mn.erin.lms.base.mongo.document.exam.MongoExamResultEntity;
import mn.erin.lms.base.mongo.repository.MongoExamResultRepository;

/**
 * @author Galsan Bayart.
 */
public class ExamResultRepositoryImpl implements ExamResultRepository
{
  private final MongoExamResultRepository mongoExamResultRepository;

  public ExamResultRepositoryImpl(MongoExamResultRepository mongoExamResultRepository)
  {
    this.mongoExamResultRepository = mongoExamResultRepository;
  }

  @Override
  public boolean save(ExamResult examResult)
  {
    mongoExamResultRepository.save(new MongoExamResult(examResult.getExamResults().stream().map(this::mapToMongoExamResult).collect(Collectors.toList())));
    return true;
  }

  public MongoExamResultEntity mapToMongoExamResult(ExamResultEntity examResult){
    return new MongoExamResultEntity(examResult.getQuestionId(), examResult.getAnswer());
  }
}
