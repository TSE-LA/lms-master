package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroup;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;

/**
 * @author Galsan Bayart
 */
public interface QuestionGroupRepository
{
  QuestionGroup create(String parentId, String name, OrganizationId organizationId, String description);

  List<QuestionGroup> getAll(String parentId, String organizationId);

  List<QuestionGroup> getAllByOrganizationId(OrganizationId organizationId);

  String update(QuestionGroup questionGroup) throws LmsRepositoryException;

  QuestionGroup findById(QuestionGroupId id) throws LmsRepositoryException;

  boolean delete(String questionGroupId);
}
