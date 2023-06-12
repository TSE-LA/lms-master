package mn.erin.lms.base.mongo.implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.AssessmentStatus;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.assessment.MongoAssessment;
import mn.erin.lms.base.mongo.document.assessment.MongoAssessmentStatus;
import mn.erin.lms.base.mongo.repository.MongoAssessmentRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AssessmentRepositoryImpl implements AssessmentRepository
{
  private final MongoAssessmentRepository mongoAssessmentRepository;

  public AssessmentRepositoryImpl(MongoAssessmentRepository mongoAssessmentRepository)
  {
    this.mongoAssessmentRepository = mongoAssessmentRepository;
  }

  @Override
  public Assessment createAssessment(QuizId quizId, String name, AuthorId authorId)
  {
    String id = IdGenerator.generateId();
    MongoAssessment mongoAssessment = new MongoAssessment(
        id,
        name,
        quizId.getId(),
        authorId.getId(),
        LocalDateTime.now(),
        LocalDateTime.now()
    );

    mongoAssessment.setStatus(MongoAssessmentStatus.INACTIVE);
    mongoAssessmentRepository.save(mongoAssessment);
    return new Assessment(AssessmentId.valueOf(id), name, authorId);
  }

  @Override
  public Assessment createAssessment(QuizId quizId, String name, AuthorId authorId, String description)
  {
    String id = IdGenerator.generateId();
    MongoAssessment mongoAssessment = new MongoAssessment(
        id,
        name,
        quizId.getId(),
        authorId.getId(),
        LocalDateTime.now(),
        LocalDateTime.now()
    );

    mongoAssessment.setDescription(description);
    mongoAssessment.setStatus(MongoAssessmentStatus.INACTIVE);
    mongoAssessmentRepository.save(mongoAssessment);
    Assessment assessment = new Assessment(AssessmentId.valueOf(id), name, authorId);
    assessment.setQuizId(quizId);
    assessment.setDescription(description);
    assessment.setCreatedDate(mongoAssessment.getCreatedDate());
    assessment.setModifiedDate(mongoAssessment.getModifiedDate());

    return assessment;
  }

  @Override
  public List<Assessment> listAll()
  {
    List<MongoAssessment> mongoAssessments = mongoAssessmentRepository.findAll();
    return mongoAssessments.stream().map(this::convert).collect(Collectors.toList());
  }

  @Override
  public List<Assessment> listAll(LocalDate startDate, LocalDate endDate)
  {
    List<MongoAssessment> mongoAssessments = mongoAssessmentRepository
        .findByCreatedDateBetween(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    return mongoAssessments.stream().map(this::convert).collect(Collectors.toList());
  }

  @Override
  public Assessment findById(AssessmentId assessmentId) throws LmsRepositoryException
  {
    Optional<MongoAssessment> mongoAssessment = mongoAssessmentRepository.findById(assessmentId.getId());
    if (!mongoAssessment.isPresent())
    {
      throw new LmsRepositoryException("Failed find an assessment with the ID: [" + assessmentId.getId() + "]");
    }

    return convert(mongoAssessment.get());
  }

  @Override
  public Assessment update(AssessmentId assessmentId, String name, String description) throws LmsRepositoryException
  {
    Assessment assessment = findById(assessmentId);
    MongoAssessment mongoAssessment = new MongoAssessment(
        assessment.getAssessmentId().getId(),
        name,
        assessment.getQuizId().getId(),
        assessment.getAuthorId().getId(),
        assessment.getCreatedDate(),
        LocalDateTime.now()
    );
    mongoAssessment.setDescription(description);
    if (assessment.getStatus() == AssessmentStatus.ACTIVE)
    {
      mongoAssessment.setStatus(MongoAssessmentStatus.ACTIVE);
    }
    else
    {
      mongoAssessment.setStatus(MongoAssessmentStatus.INACTIVE);
    }
    mongoAssessmentRepository.save(mongoAssessment);
    return convert(mongoAssessment);
  }

  @Override
  public Assessment updateModifiedDate(AssessmentId assessmentId) throws LmsRepositoryException
  {
    if (assessmentId == null)
    {
      throw new LmsRepositoryException("Assessment id cannot be null");
    }
    Assessment assessment = findById(assessmentId);

    MongoAssessment mongoAssessment = new MongoAssessment(
        assessment.getAssessmentId().getId(),
        assessment.getName(),
        assessment.getQuizId().getId(),
        assessment.getAuthorId().getId(),
        assessment.getCreatedDate(),
        LocalDateTime.now()
    );
    mongoAssessment.setDescription(assessment.getDescription());

    mongoAssessmentRepository.save(mongoAssessment);
    return convert(mongoAssessment);
  }

  @Override
  public boolean delete(AssessmentId assessmentId)
  {
    mongoAssessmentRepository.deleteById(assessmentId.getId());
    return true;
  }

  @Override
  public void updateStatus(AssessmentId assessmentId, boolean activate) throws LmsRepositoryException
  {
    Assessment assessment = findById(assessmentId);

    MongoAssessment mongoAssessment = new MongoAssessment(
        assessment.getAssessmentId().getId(),
        assessment.getName(),
        assessment.getQuizId().getId(),
        assessment.getAuthorId().getId(),
        assessment.getCreatedDate(),
        LocalDateTime.now()
    );
    mongoAssessment.setDescription(assessment.getDescription());
    mongoAssessment.setStatus(activate ? MongoAssessmentStatus.ACTIVE : MongoAssessmentStatus.INACTIVE);

    mongoAssessmentRepository.save(mongoAssessment);
  }

  private Assessment convert(MongoAssessment mongoAssessment)
  {
    AssessmentId assessmentId = AssessmentId.valueOf(mongoAssessment.getId());
    AuthorId authorId = AuthorId.valueOf(mongoAssessment.getAuthorId());
    Assessment assessment = new Assessment(assessmentId, mongoAssessment.getName(), authorId);
    assessment.setCreatedDate(mongoAssessment.getCreatedDate());
    assessment.setModifiedDate(mongoAssessment.getModifiedDate());
    assessment.setQuizId(StringUtils.isBlank(mongoAssessment.getQuizId()) ? null : QuizId.valueOf(mongoAssessment.getQuizId()));
    assessment.setDescription(mongoAssessment.getDescription());

    if (mongoAssessment.getStatus() == MongoAssessmentStatus.ACTIVE)
    {
      assessment.activate();
    }

    return assessment;
  }
}
