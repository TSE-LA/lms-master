package mn.erin.lms.base.domain.usecase.system;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.model.document.Document;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.system.SystemConfig;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.SystemConfigRepository;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.system.dto.SystemConfigInput;

/**
 * @author Temuulen Naranbold
 */
public class UpdateBackgroundImage implements UseCase<SystemConfigInput, Void>
{
  private final LmsFileSystemService lmsFileSystemService;
  private final OrganizationIdProvider organizationIdProvider;
  private final SystemConfigRepository systemConfigRepository;

  public UpdateBackgroundImage(LmsFileSystemService lmsFileSystemService,
      OrganizationIdProvider organizationIdProvider, SystemConfigRepository systemConfigRepository)
  {
    this.lmsFileSystemService = lmsFileSystemService;
    this.organizationIdProvider = organizationIdProvider;
    this.systemConfigRepository = systemConfigRepository;
  }

  @Override
  public Void execute(SystemConfigInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);
      String organizationId = organizationIdProvider.getOrganizationId();
      List<SystemConfig> systemConfigs = systemConfigRepository.findAllByOrganizationId(OrganizationId.valueOf(organizationId));
      Document document;
      if (systemConfigs.isEmpty())
      {
        Folder folder = lmsFileSystemService.createSystemConfigFolder(organizationId);
        document = lmsFileSystemService.createFileInFolder(folder.getFolderId().getId(), input.getFile());
        systemConfigRepository.create(folder.getFolderId(), folder.getFolderName(),
            document.getDocumentId(), document.getDocumentName(), OrganizationId.valueOf(organizationId), input.isLogo());
      }
      else
      {
        SystemConfig updating = null;
        for (SystemConfig systemConfig : systemConfigs)
        {
          if ((systemConfig.isLogo() && input.isLogo()) || (!systemConfig.isLogo() && !input.isLogo()))
          {
            updateImage(systemConfig, input);
            return null;
          }
          else
          {
            updating = systemConfig;
          }
        }
        document = lmsFileSystemService.createFileInFolder(updating.getFolderId().getId(), input.getFile());
        systemConfigRepository.create(updating.getFolderId(), updating.getFolderName(),
            document.getDocumentId(), document.getDocumentName(), OrganizationId.valueOf(organizationId), input.isLogo());
      }
      return null;
    }
    catch (NullPointerException | DMSRepositoryException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void updateImage(SystemConfig systemConfig, SystemConfigInput input) throws DMSRepositoryException, LmsRepositoryException
  {
    lmsFileSystemService.deleteDocument(systemConfig.getDocumentId());
    Document document = lmsFileSystemService.createFileInFolder(systemConfig.getFolderId().getId(), input.getFile());
    systemConfig.setDocumentId(document.getDocumentId());
    systemConfig.setDocumentName(document.getDocumentName());
    systemConfigRepository.update(systemConfig);
  }
}
