/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.alfresco.connector.config.AlfrescoConnectorBeanConfig;
import mn.erin.lms.legacy.infrastructure.lms.repository.mongo.LmsMongoBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ LmsMongoBeanConfig.class, AlfrescoConnectorBeanConfig.class })
public class ErinLmsRepositoryBeanConfig
{
}
