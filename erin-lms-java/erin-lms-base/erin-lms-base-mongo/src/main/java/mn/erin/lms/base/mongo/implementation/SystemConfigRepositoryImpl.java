package mn.erin.lms.base.mongo.implementation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.system.SystemConfig;
import mn.erin.lms.base.domain.model.system.SystemConfigId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.SystemConfigRepository;
import mn.erin.lms.base.mongo.document.content.MongoSystemConfig;
import mn.erin.lms.base.mongo.repository.MongoSystemConfigRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Temuulen Naranbold
 */
public class SystemConfigRepositoryImpl implements SystemConfigRepository
{
  private static final String SYSTEM_CONFIG_NOT_FOUND = "System config ID with: [%s] not found!";

  private final MongoSystemConfigRepository mongoSystemConfigRepository;

  public SystemConfigRepositoryImpl(MongoSystemConfigRepository mongoSystemConfigRepository)
  {
    this.mongoSystemConfigRepository = mongoSystemConfigRepository;
  }

  @Override
  public void create(FolderId folderId, String folderName, DocumentId documentId, String documentName, OrganizationId organizationId, boolean isLogo)
  {
    mongoSystemConfigRepository.save(new MongoSystemConfig(IdGenerator.generateId(), organizationId.getId(),
        folderId.getId(), folderName, documentId.getId(), documentName, isLogo));
  }

  @Override
  public void update(SystemConfig systemConfig) throws LmsRepositoryException
  {
    Optional<MongoSystemConfig> optional = mongoSystemConfigRepository.findById(systemConfig.getId().getId());
    MongoSystemConfig mongoSystemConfig = optional.orElseThrow(() -> new LmsRepositoryException(String.format(SYSTEM_CONFIG_NOT_FOUND, systemConfig.getOrganizationId().getId())));
    setMongoSystemConfigValues(mongoSystemConfig, systemConfig);
    mongoSystemConfigRepository.save(mongoSystemConfig);
  }

  @Override
  public SystemConfig findByOrganizationId(OrganizationId organizationId)
  {
    Optional<MongoSystemConfig> mongoSystemConfig = mongoSystemConfigRepository.findByOrganizationId(organizationId.getId());
    return mongoSystemConfig.map(this::mapToSystemConfig).orElse(null);
  }

  @Override
  public List<SystemConfig> findAllByOrganizationId(OrganizationId organizationId)
  {
    return mongoSystemConfigRepository.findAllByOrganizationId(organizationId.getId()).stream().map(this::mapToSystemConfig).collect(Collectors.toList());
  }

  @Override
  public boolean exists(SystemConfigId systemConfigId)
  {
    return mongoSystemConfigRepository.existsById(systemConfigId.getId());
  }

  private SystemConfig mapToSystemConfig(MongoSystemConfig mongoSystemConfig)
  {
    return new SystemConfig(
        SystemConfigId.valueOf(mongoSystemConfig.getId()),
        FolderId.valueOf(mongoSystemConfig.getFolderId()),
        mongoSystemConfig.getFolderName(),
        DocumentId.valueOf(mongoSystemConfig.getDocumentId()),
        mongoSystemConfig.getDocumentName(),
        OrganizationId.valueOf(mongoSystemConfig.getOrganizationId()),
        mongoSystemConfig.isLogo());
  }

  private void setMongoSystemConfigValues(MongoSystemConfig mongoSystemConfig, SystemConfig systemConfig)
  {
    mongoSystemConfig.setOrganizationId(systemConfig.getOrganizationId().getId());
    mongoSystemConfig.setFolderId(systemConfig.getFolderId().getId());
    mongoSystemConfig.setFolderName(systemConfig.getFolderName());
    mongoSystemConfig.setDocumentId(systemConfig.getDocumentId().getId());
    mongoSystemConfig.setDocumentName(systemConfig.getDocumentName());
    mongoSystemConfig.setLogo(systemConfig.isLogo());
  }
}
