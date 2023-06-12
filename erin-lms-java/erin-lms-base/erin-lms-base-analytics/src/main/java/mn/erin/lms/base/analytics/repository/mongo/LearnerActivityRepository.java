/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.analytics.repository.mongo;

import java.util.List;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.learner.LearnerProgress;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;

/**
 * @author Munkh
 */
public interface LearnerActivityRepository
{
  Analytic getAll(GroupId groupId, CourseCategoryId parentCategoryId);

  List<LearnerProgress> getLearnerProgress(GroupId groupId, CourseCategoryId parentCategoryId);
}
