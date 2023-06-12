/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.config;

import mn.erin.alfresco.connector.config.AlfrescoConnectorBeanConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Temuulen 2021
 */
@Configuration
@Import({ AlfrescoConnectorBeanConfig.class })
public class AimBeanConfig
{
}
