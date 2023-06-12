package mn.erin.lms.base.domain.usecase.system;

import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.system.SystemConfig;
import mn.erin.lms.base.domain.repository.SystemConfigRepository;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;

/**
 * @author Temuulen Naranbold
 */
public class GetSystemImageURL
{
  private final SystemConfigRepository systemConfigRepository;
  private final OrganizationIdProvider organizationIdProvider;

  public GetSystemImageURL(SystemConfigRepository systemConfigRepository, OrganizationIdProvider organizationIdProvider)
  {
    this.systemConfigRepository = systemConfigRepository;
    this.organizationIdProvider = organizationIdProvider;
  }

  public String execute(boolean isLogo) throws UseCaseException
  {
    String organizationId = organizationIdProvider.getOrganizationId();
    List<SystemConfig> systemConfigs = systemConfigRepository.findAllByOrganizationId(OrganizationId.valueOf(organizationId));
    for (SystemConfig systemConfig : systemConfigs)
    {
      if ((systemConfig.isLogo() && isLogo) || (!systemConfig.isLogo() && !isLogo))
      {
        return systemConfig.getImageURL();
      }
    }

    if (isLogo)
    {
      throw new UseCaseException("Logo not found with organization ID: ["+ organizationId +"] ");
    }
    throw new UseCaseException("Background image not found with organization ID: ["+ organizationId +"] ");
  }
}
