/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.aim.usecase.user;

import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zorig
 */
public class GetUserEmailAddressByUserName extends AbstractUseCase<String, String>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetUserEmailAddressByUserName.class);

  private final AimRepositoryRegistry aimRepositoryRegistry;

  public GetUserEmailAddressByUserName(AimRepositoryRegistry aimRepositoryRegistry)
  {
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @Override
  public String execute(String username) throws UseCaseException
  {
    LOGGER.info("########## Invoked GET EMAIL USER BY USER NAME use case.");
    if (StringUtils.isBlank(username))
    {
      throw new UseCaseException("Email input name must not be blank!");
    }
    return getRecipientEmailAddress(username);
  }

  private String getRecipientEmailAddress(String username) throws UseCaseException
  {
    String usernameTrimmed = getCorrectUserId(username);
    UserIdentity identity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(usernameTrimmed);
    if (identity == null)
    {
      throw new UseCaseException("User doesnt have an identity");
    }
    UserProfile profile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(identity.getUserId());
    if (profile == null)
    {
      throw new UseCaseException("User doesnt have a profile");
    }
    if (profile.getUserContact() == null)
    {
      return null;
    }
    return profile.getUserContact().getEmail();
  }

  private String getCorrectUserId(String username)
  {
    return !username.contains(".") ? username : username.substring(2);
  }
}
