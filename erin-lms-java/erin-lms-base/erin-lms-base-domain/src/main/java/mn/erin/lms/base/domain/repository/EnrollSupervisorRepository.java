package mn.erin.lms.base.domain.repository;

import java.util.List;
import java.util.Set;

import mn.erin.lms.base.domain.model.enrollment.EnrollSupervisor;

/**
 * @author Galsan Bayart.
 */
public interface EnrollSupervisorRepository
{
  boolean save(EnrollSupervisor enrollSupervisor);

  Set<String> getAllExamBySupervisorId(String supervisorId);

  Set<EnrollSupervisor> getAllByExamIds(List<String> examIds);
}
