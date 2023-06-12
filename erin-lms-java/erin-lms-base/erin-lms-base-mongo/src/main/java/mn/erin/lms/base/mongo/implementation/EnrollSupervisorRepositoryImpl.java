package mn.erin.lms.base.mongo.implementation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.enrollment.EnrollSupervisor;
import mn.erin.lms.base.domain.repository.EnrollSupervisorRepository;
import mn.erin.lms.base.mongo.document.enrollment.MongoEnrollSupervisor;
import mn.erin.lms.base.mongo.repository.MongoEnrollSupervisorRepository;

/**
 * @author Galsan Bayart.
 */
public class EnrollSupervisorRepositoryImpl implements EnrollSupervisorRepository
{
  private final MongoEnrollSupervisorRepository mongoEnrollSupervisorRepository;

  public EnrollSupervisorRepositoryImpl(MongoEnrollSupervisorRepository mongoEnrollSupervisorRepository)
  {
    this.mongoEnrollSupervisorRepository = mongoEnrollSupervisorRepository;
  }

  @Override
  public boolean save(EnrollSupervisor enrollSupervisor)
  {
    mongoEnrollSupervisorRepository.save(mapToMongoEnrollSupervisor(enrollSupervisor));
    return true;
  }

  @Override
  public Set<String> getAllExamBySupervisorId(String supervisorId)
  {
    return mongoEnrollSupervisorRepository.getAllBySupervisorIdsContains(supervisorId).stream().map(MongoEnrollSupervisor::getExamId).collect(Collectors.toSet());
  }

  @Override
  public Set<EnrollSupervisor> getAllByExamIds(List<String> examIds)
  {
    return mongoEnrollSupervisorRepository.getAllByExamIdIn(examIds).stream().map(this::mapToEnrollSupervisor).collect(Collectors.toSet());
  }

  private MongoEnrollSupervisor mapToMongoEnrollSupervisor(EnrollSupervisor enrollSupervisor)
  {
    return new MongoEnrollSupervisor(enrollSupervisor.getExamId(), enrollSupervisor.getSupervisorIds(), enrollSupervisor.getAdminAddedUser());
  }
  private EnrollSupervisor mapToEnrollSupervisor(MongoEnrollSupervisor mongoEnrollSupervisor)
  {
    return new EnrollSupervisor(mongoEnrollSupervisor.getExamId(), mongoEnrollSupervisor.getSupervisorIds(), mongoEnrollSupervisor.getAdminAddedUser());
  }

}
