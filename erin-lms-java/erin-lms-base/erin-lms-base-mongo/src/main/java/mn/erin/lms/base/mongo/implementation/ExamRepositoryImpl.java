package mn.erin.lms.base.mongo.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.ExamPublishConfig;
import mn.erin.lms.base.domain.model.exam.ExamPublishStatus;
import mn.erin.lms.base.domain.model.exam.ExamStatus;
import mn.erin.lms.base.domain.model.exam.ExamType;
import mn.erin.lms.base.domain.model.exam.HistoryOfModification;
import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;
import mn.erin.lms.base.domain.model.exam.ShowAnswerResult;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.exam.MongoExam;
import mn.erin.lms.base.mongo.document.exam.MongoExamConfig;
import mn.erin.lms.base.mongo.document.exam.MongoExamPublishConfig;
import mn.erin.lms.base.mongo.document.exam.MongoHistoryOfModification;
import mn.erin.lms.base.mongo.document.exam.MongoRandomQuestionConfig;
import mn.erin.lms.base.mongo.repository.MongoExamRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Galsan Bayart.
 */
public class ExamRepositoryImpl implements ExamRepository
{
  private static final String EXAM_NOT_FOUND = "Exam ID with: [%s] not found!";

  private final MongoExamRepository mongoExamRepository;

  public ExamRepositoryImpl(MongoExamRepository mongoExamRepository)
  {
    this.mongoExamRepository = mongoExamRepository;
  }

  @Override
  public Exam create(String name, String description,
      ExamCategoryId categoryId, ExamGroupId groupId,
      ExamType examType, List<HistoryOfModification> historyOfModification,
      ExamPublishConfig publishConfig, ExamConfig configure)
  {
    String currentUser = historyOfModification.get(0).getModifiedUser();
    Date modifiedDate = historyOfModification.get(0).getModifiedDate();
    MongoExam mongoExam = new MongoExam();
    mongoExam.setId(IdGenerator.generateId());
    mongoExam.setAuthor(currentUser);
    mongoExam.setCreatedDate(modifiedDate);
    mongoExam.setName(name);
    mongoExam.setExamStatus(ExamStatus.NEW.name());
    mongoExam.setExamPublishStatus(ExamPublishStatus.UNPUBLISHED.name());
    mongoExam.setDescription(description);
    mongoExam.setExamCategoryId(categoryId.getId());
    mongoExam.setExamGroupId(groupId.getId());
    mongoExam.setExamType(examType.name());
    mongoExam.setModifiedDate(modifiedDate);
    mongoExam.setModifiedUser(currentUser);
    mongoExam.setHistoryOfModifications(historyOfModification.stream().map(this::mapToMongoHistoryOfModification).collect(Collectors.toList()));
    mongoExam.setPublishConfig(mapToMongoExamPublishConfigure(publishConfig));
    mongoExam.setMongoExamConfig(mapToMongoExamConfigure(configure));

    return mapToExam(mongoExamRepository.save(mongoExam));
  }

  @Override
  public String update(Exam exam) throws LmsRepositoryException
  {
    Optional<MongoExam> optional = mongoExamRepository.findById(exam.getId().getId());
    MongoExam mongoExam = optional.orElseThrow(() -> new LmsRepositoryException(String.format(EXAM_NOT_FOUND, exam.getId())));

    setMongoExamValues(mongoExam, exam);

    return mongoExamRepository.save(mongoExam).getId();
  }

  @Override
  public void updateExamStatus(String examId, ExamStatus newStatus) throws LmsRepositoryException
  {
    Optional<MongoExam> optional = mongoExamRepository.findById(examId);
    MongoExam mongoExam = optional.orElseThrow(() -> new LmsRepositoryException(String.format(EXAM_NOT_FOUND, examId)));
    mongoExam.setExamStatus(newStatus.name());
    mongoExamRepository.save(mongoExam);
  }

  @Override
  public void updateExamPublishStatus(String examId, ExamPublishStatus newStatus) throws LmsRepositoryException
  {
    Optional<MongoExam> optional = mongoExamRepository.findById(examId);
    MongoExam mongoExam = optional.orElseThrow(() -> new LmsRepositoryException(String.format(EXAM_NOT_FOUND, examId)));
    mongoExam.setExamPublishStatus(newStatus.name());
    mongoExamRepository.save(mongoExam);
  }

  @Override
  public List<Exam> getAllExam()
  {
    return mongoExamRepository.findAll().stream().map(this::mapToExam).collect(Collectors.toList());
  }

  @Override
  public List<Exam> listAllByCategory(ExamCategoryId categoryId)
  {
    List<MongoExam> mongoExams = mongoExamRepository
        .findAllByExamCategoryId(categoryId.getId());
    return this.mapToExamList(mongoExams);
  }

  @Override
  public List<Exam> getAllByIds(Set<String> examIds)
  {
    Iterator<MongoExam> examIterator = mongoExamRepository.findAllById(examIds).iterator();
    List<Exam> exams = new ArrayList<>();
    while (examIterator.hasNext())
    {
      exams.add(mapToExam(examIterator.next()));
    }
    return exams;
  }

  @Override
  public List<Exam> listAllByGroup(ExamGroupId examGroupId)
  {
    return mapToExamList(mongoExamRepository.findAllByExamGroupId(examGroupId.getId()));
  }

  @Override
  public List<Exam> listAllByCategoryAndGroup(ExamCategoryId categoryId, ExamGroupId groupId)
  {
    return mapToExamList(mongoExamRepository.findAllByExamCategoryIdAndExamGroupId(categoryId.getId(), groupId.getId()));
  }

  @Override
  public Exam findById(ExamId id) throws LmsRepositoryException
  {
    Optional<MongoExam> mongoExam = mongoExamRepository.findById(id.getId());

    if (mongoExam.isPresent())
    {
      return mapToExam(mongoExam.get());
    }
    else
    {
      throw new LmsRepositoryException(String.format(EXAM_NOT_FOUND, id.getId()));
    }
  }

  @Override
  public boolean delete(String id) throws LmsRepositoryException
  {
    Optional<MongoExam> optional = mongoExamRepository.findById(id);

    if (!optional.isPresent())
    {
      throw new LmsRepositoryException(String.format(EXAM_NOT_FOUND, id));
    }
    mongoExamRepository.deleteById(id);
    return true;
  }

  @Override
  public boolean exists(ExamId examId)
  {
    return mongoExamRepository.existsById(examId.getId());
  }

  @Override
  public void updateExamScore(String id, int score) throws LmsRepositoryException
  {
    Optional<MongoExam> optional = mongoExamRepository.findById(id);
    MongoExam mongoExam = optional.orElseThrow(() -> new LmsRepositoryException(String.format(EXAM_NOT_FOUND, id)));
    mongoExam.getMongoExamConfig().setMaxScore(score);
    mongoExamRepository.save(mongoExam);
  }

  @Override
  public Exam updateEnrollments(String id, Set<String> enrolledLearners, Set<String> enrolledGroups) throws LmsRepositoryException
  {
    Optional<MongoExam> optional = mongoExamRepository.findById(id);
    MongoExam mongoExam = optional.orElseThrow(() -> new LmsRepositoryException(String.format(EXAM_NOT_FOUND, id)));
    mongoExam.setEnrolledLearners(enrolledLearners);
    mongoExam.setEnrolledGroups(enrolledGroups);

    return mapToExam(mongoExamRepository.save(mongoExam));
  }

  private Exam mapToExam(MongoExam mongoExam)

  {
    return new Exam.Builder(
        ExamId.valueOf(mongoExam.getId()),
        mongoExam.getAuthor(),
        mongoExam.getCreatedDate(),
        mongoExam.getName())
        .withDescription(mongoExam.getDescription())
        .withExamCategoryId(ExamCategoryId.valueOf(mongoExam.getExamCategoryId()))
        .withExamGroupId(ExamGroupId.valueOf(mongoExam.getExamGroupId()))
        .withExamType(ExamType.valueOf(mongoExam.getExamType().toUpperCase()))
        .withExamStatus(ExamStatus.valueOf(mongoExam.getExamStatus()))
        .withExamPublishStatus(
            mongoExam.getExamPublishStatus() != null ? ExamPublishStatus.valueOf(mongoExam.getExamPublishStatus()) : ExamPublishStatus.UNPUBLISHED)
        .withModifiedDate(mongoExam.getModifiedDate())
        .withModifiedUser(mongoExam.getModifiedUser())
        .withHistoryOfModifications(mongoExam.getHistoryOfModifications().stream().map(this::mapToHistoryOfModification).collect(Collectors.toList()))
        .withPublishConfig(mapToExamPublishConfig(mongoExam.getPublishConfig()))
        .withExamConfig(mapToExamConfig(mongoExam.getMongoExamConfig()))
        .withEnrolledLearners(mongoExam.getEnrolledLearners())
        .withEnrolledGroups(mongoExam.getEnrolledGroups())
        .build();
  }

  private void setMongoExamValues(MongoExam mongoExam, Exam exam)
  {
    mongoExam.setName(exam.getName());
    mongoExam.setDescription(exam.getDescription());
    mongoExam.setExamCategoryId(exam.getExamCategoryId().getId());
    mongoExam.setExamType(exam.getExamType().name());
    mongoExam.setModifiedDate(exam.getModifiedDate());
    mongoExam.setModifiedUser(exam.getModifiedUser());
    mongoExam.addModifyInfo(new MongoHistoryOfModification(exam.getModifiedUser(), exam.getModifiedDate()));
    mongoExam.setPublishConfig(mapToMongoExamPublishConfigure(exam.getExamPublishConfig()));
    mongoExam.setMongoExamConfig(mapToMongoExamConfigure(exam.getExamConfig()));
    mongoExam.setEnrolledGroups(exam.getEnrolledGroups());
    mongoExam.setEnrolledLearners(exam.getEnrolledLearners());
    mongoExam.setExamGroupId(exam.getExamGroupId().getId());
  }

  private ExamConfig mapToExamConfig(MongoExamConfig mongoExamConfig)
  {

    ExamConfig examConfig = new ExamConfig(
        mongoExamConfig.getMongoRandomQuestionConfigs().stream().map(this::mapToRandomQuestionConfig).collect(Collectors.toSet()),
        mongoExamConfig.getTotalQuestion(),
        ShowAnswerResult.valueOf(mongoExamConfig.getAnswerResult()),
        mongoExamConfig.isShuffleQuestion(),
        mongoExamConfig.isShuffleAnswer(),
        mongoExamConfig.getQuestionsPerPage(),
        mongoExamConfig.isAutoStart(),
        mongoExamConfig.getThresholdScore()
    );
    examConfig.setQuestionIds(mongoExamConfig.getQuestionIds() != null ? mongoExamConfig.getQuestionIds() : Collections.emptySet());
    examConfig.setAttempt(mongoExamConfig.getAttempt());
    examConfig.setCertificateId(mongoExamConfig.getCertificateId() != null ? CertificateId.valueOf(mongoExamConfig.getCertificateId()) : null);
    examConfig.setStartDate(mongoExamConfig.getStartDate());
    examConfig.setEndDate(mongoExamConfig.getEndDate());
    examConfig.setStartTime(mongoExamConfig.getStartTime());
    examConfig.setEndTime(mongoExamConfig.getEndTime());
    examConfig.setDuration(mongoExamConfig.getDuration());
    examConfig.setMaxScore(mongoExamConfig.getMaxScore());
    return examConfig;
  }

  private MongoExamConfig mapToMongoExamConfigure(ExamConfig examConfig)
  {
    MongoExamConfig mongoExamConfig = new MongoExamConfig();
    mongoExamConfig.setQuestionIds(examConfig.getQuestionIds());
    mongoExamConfig.setRandomQuestionConfigs(
        examConfig.getRandomQuestionConfigs().stream().map(this::mapToMongoRandomQuestionConfig).collect(Collectors.toSet()));
    mongoExamConfig.setTotalQuestion(examConfig.getTotalQuestions());
    mongoExamConfig.setAnswerResult(examConfig.getAnswerResult().toString());
    mongoExamConfig.setShuffleQuestion(examConfig.isShuffleQuestion());
    mongoExamConfig.setShuffleAnswer(examConfig.isShuffleAnswer());
    mongoExamConfig.setQuestionsPerPage(examConfig.getQuestionsPerPage());
    mongoExamConfig.setMaxScore(examConfig.getMaxScore());
    mongoExamConfig.setThresholdScore(examConfig.getThresholdScore());
    mongoExamConfig.setAttempt(examConfig.getAttempt());
    mongoExamConfig.setCertificateId(examConfig.getCertificateId() != null ? examConfig.getCertificateId().getId() : null);
    mongoExamConfig.setStartDate(examConfig.getStartDate());
    mongoExamConfig.setEndDate(examConfig.getEndDate());
    mongoExamConfig.setStartTime(examConfig.getStartTime());
    mongoExamConfig.setEndTime(examConfig.getEndTime());
    mongoExamConfig.setDuration(examConfig.getDuration());
    mongoExamConfig.setAutoStart(examConfig.isAutoStart());

    return mongoExamConfig;
  }

  private ExamPublishConfig mapToExamPublishConfig(MongoExamPublishConfig mongoPublishConfigure)
  {
    ExamPublishConfig publishConfigure = new ExamPublishConfig(
        mongoPublishConfigure.getPublishDate(),
        mongoPublishConfigure.getPublishTime(),
        mongoPublishConfigure.isSendEmail(),
        mongoPublishConfigure.isSendSMS()
    );
    if (mongoPublishConfigure.isSendEmail())
    {
      publishConfigure.setMailText(mongoPublishConfigure.getMailText());
    }
    if (mongoPublishConfigure.isSendSMS())
    {
      publishConfigure.setSmsText(mongoPublishConfigure.getSmsText());
    }
    return publishConfigure;
  }

  private MongoExamPublishConfig mapToMongoExamPublishConfigure(ExamPublishConfig publishConfigure)
  {
    MongoExamPublishConfig mongoPublishConfigure = new MongoExamPublishConfig();
    mongoPublishConfigure.setPublishDate(publishConfigure.getPublishDate());
    mongoPublishConfigure.setPublishTime(publishConfigure.getPublishTime());
    mongoPublishConfigure.setSendEmail(publishConfigure.isSendEmail());
    mongoPublishConfigure.setSendSMS(publishConfigure.isSendSMS());
    if (publishConfigure.isSendEmail())
    {
      mongoPublishConfigure.setMailText(publishConfigure.getMailText());
    }
    if (publishConfigure.isSendSMS())
    {
      mongoPublishConfigure.setSmsText(publishConfigure.getSmsText());
    }
    return mongoPublishConfigure;
  }

  private RandomQuestionConfig mapToRandomQuestionConfig(MongoRandomQuestionConfig mongoRandomQuestionConfig)
  {
    return new RandomQuestionConfig(
        mongoRandomQuestionConfig.getGroupId(),
        mongoRandomQuestionConfig.getCategoryId(),
        mongoRandomQuestionConfig.getScore(),
        mongoRandomQuestionConfig.getAmount()
    );
  }

  private MongoRandomQuestionConfig mapToMongoRandomQuestionConfig(RandomQuestionConfig randomQuestionConfig)
  {
    return new MongoRandomQuestionConfig(
        randomQuestionConfig.getGroupId(),
        randomQuestionConfig.getCategoryId(),
        randomQuestionConfig.getScore(),
        randomQuestionConfig.getAmount()
    );
  }

  private HistoryOfModification mapToHistoryOfModification(MongoHistoryOfModification historyOfModification)
  {
    return new HistoryOfModification(historyOfModification.getModifiedUser(), historyOfModification.getModifiedDate());
  }

  private MongoHistoryOfModification mapToMongoHistoryOfModification(HistoryOfModification historyOfModification)
  {
    return new MongoHistoryOfModification(historyOfModification.getModifiedUser(), historyOfModification.getModifiedDate());
  }

  private List<Exam> mapToExamList(List<MongoExam> mongoExams)
  {
    Set<Exam> exams = new HashSet<>();

    for (MongoExam mongoExam : mongoExams)
    {
      exams.add(this.mapToExam(mongoExam));
    }
    return new ArrayList<>(exams);
  }
}
