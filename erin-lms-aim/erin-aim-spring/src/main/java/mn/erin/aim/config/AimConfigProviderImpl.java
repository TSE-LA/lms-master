package mn.erin.aim.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.PostConstruct;

import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.service.AimConfigProvider;

public class AimConfigProviderImpl implements AimConfigProvider
{
  private final Properties properties = new Properties();

  @PostConstruct
  public void loadConfig() throws IOException
  {
    try (InputStream resource = getClass().getClassLoader().getResourceAsStream("aim.config.properties"))
    {
      properties.load(resource);
    }
  }

  @Override
  public TenantId getDefaultTenantId()
  {
    return TenantId.valueOf(properties.getProperty("mn.erin.aim.defaultTenantId"));
  }

  @Override
  public RoleId getAdminRoleId()
  {
    return RoleId.valueOf(properties.getProperty("mn.erin.aim.adminRoleId"));
  }

  @Override
  public String getRootGroupName()
  {
    return properties.getProperty("mn.erin.aim.rootGroupName");
  }

  @Override
  public String getDefaultPassword()
  {
    return properties.getProperty("mn.erin.aim.defaultPassword");
  }
}
