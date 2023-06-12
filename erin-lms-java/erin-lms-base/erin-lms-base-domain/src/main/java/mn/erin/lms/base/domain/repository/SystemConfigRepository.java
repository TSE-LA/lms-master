package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.system.SystemConfig;
import mn.erin.lms.base.domain.model.system.SystemConfigId;

/**
 * @author Temuulen Naranbold
 */
public interface SystemConfigRepository
{
  void create(FolderId folderId, String folderName, DocumentId documentId, String documentName, OrganizationId organizationId, boolean isLogo);

  void update(SystemConfig systemConfig) throws LmsRepositoryException;

  SystemConfig findByOrganizationId(OrganizationId organizationId);

  List<SystemConfig> findAllByOrganizationId(OrganizationId organizationId);

  boolean exists(SystemConfigId systemConfigId);
}
