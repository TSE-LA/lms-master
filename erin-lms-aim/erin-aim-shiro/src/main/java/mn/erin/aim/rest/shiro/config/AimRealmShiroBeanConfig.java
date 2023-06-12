/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import javax.inject.Inject;

import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.realm.Realm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author EBazarragchaa
 */
@Configuration
@Import({ ShiroBaseBeanConfig.class })
public class AimRealmShiroBeanConfig
{
  @Bean
  @Inject
  public Realm realm(MembershipRepository membershipRepository, RoleRepository roleRepository, UserIdentityRepository userIdentityRepository,
      PasswordService passwordService)
  {
    return new AimRealm(membershipRepository, roleRepository, userIdentityRepository, passwordService);
  }
}
