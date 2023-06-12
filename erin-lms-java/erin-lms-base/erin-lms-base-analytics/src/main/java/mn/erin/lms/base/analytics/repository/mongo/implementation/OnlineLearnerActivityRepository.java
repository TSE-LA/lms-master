/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.datetime.TimeUtils;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.lms.base.aim.organization.DepartmentPath;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.learner.LearnerActivity;
import mn.erin.lms.base.analytics.model.learner.LearnerProgress;
import mn.erin.lms.base.analytics.repository.mongo.LearnerActivityRepository;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoActivityQueries;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.repository.mongo.utils.QueryBuilder;
import mn.erin.lms.base.analytics.service.UserService;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.util.DepartmentPathUtil;
import mn.erin.lms.base.scorm.model.SCORMTime;

import static mn.erin.lms.base.analytics.repository.mongo.utils.ArrayStringConverter.toArrayString;

/**
 * @author Munkh
 */
public class OnlineLearnerActivityRepository extends AnalyticRepository implements LearnerActivityRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineLearnerActivityRepository.class);

  private final MongoCollection<Document> scormCollection;

  public OnlineLearnerActivityRepository(MongoCollectionProvider mongoCollectionProvider,
      UserService userService)
  {
    super(mongoCollectionProvider, userService);
    this.scormCollection = mongoCollectionProvider.getSCORMCollection();
  }

  @Override
  public Analytic getAll(GroupId groupId, CourseCategoryId parentCategoryId)
  {
    Set<String> learners = getLearners(getSubGroupIds(groupId.getId()));
    return toAnalytic(scormCollection.aggregate(queryFromSCORM(groupId, parentCategoryId)).into(new ArrayList<>()), learners.size());
  }

  @Override
  public List<LearnerProgress> getLearnerProgress(GroupId groupId, CourseCategoryId parentCategoryId)
  {
    List<Group> subGroups = getSubGroups(groupId.getId());
    Map<String, String> mappedGroups = new HashMap<>();
    for (Group group: subGroups)
    {
      mappedGroups.put(group.getId().getId(), group.getName());
    }
    List<Membership> memberships = getMemberships(mappedGroups.keySet());
    List<LearnerProgress> progresses = getProgress(scormCollection.aggregate(queryFromSCORM(groupId, parentCategoryId)).into(new ArrayList<>()));
    List<LearnerProgress> result = new ArrayList<>();
    for (Membership membership : memberships)
    {
      if (!membership.getRoleId().getId().equals(LmsRole.LMS_ADMIN.name()))
      {
        Optional<LearnerProgress> learnerProgress = progresses.stream().filter(progress -> progress.getUsername().equals(membership.getUsername())).findFirst();
        Map<String, DepartmentPath> departmentPathMap = DepartmentPathUtil.getPath(groupId.getId(), new HashSet<>(subGroups));
        Optional<DepartmentPath> departmentPath = Optional.ofNullable(departmentPathMap.get(membership.getGroupId().getId()));
        String path = departmentPath.isPresent() ? departmentPath.get().getPath() : mappedGroups.get(membership.getGroupId().getId());
        if (learnerProgress.isPresent())
        {
          LearnerProgress progress = learnerProgress.get();
          progress.setGroupPath(path);
          progress.setRole(membership.getRoleId().getId());
          result.add(progress);
        }
        else
        {
          result.add(new LearnerProgress(membership.getUsername(), path, membership.getRoleId().getId(), 0));
        }
      }
    }

    return result;
  }

  private List<Document> queryFromSCORM(GroupId groupId, CourseCategoryId parentCategoryId)
  {
    Set<String> subGroups = getSubGroupIds(groupId.getId());
    Set<String> learners = getLearners(subGroups);
    Set<String> categories = getSubCategories(parentCategoryId.getId());
    Set<String> contents = getExistingCourseContentIds(categories);

    Map<String, String> replacement = new HashMap<>();
    replacement.put("learners", toArrayString(learners));
    replacement.put("contents", toArrayString(contents));

    return Arrays.asList(
        QueryBuilder.buildStage(MongoActivityQueries.STAGE_MATCH_ACTIVITY, replacement),
        QueryBuilder.parseStage(MongoActivityQueries.STAGE_GROUP_ACTIVITY),
        QueryBuilder.parseStage(MongoActivityQueries.STAGE_GROUP_BY_LEARNER_ACTIVITY)
    );
  }

  @NotNull
  private Analytic toAnalytic(List<Document> documents, int learnerCount)
  {
    int perfectViewsCount = 0;
    double totalProgress = 0;
    List<Long> spentTimes = new ArrayList<>();
    for (Document document: documents)
    {
      List<Long> totalTime = new ArrayList<>();
      double progress = document.getDouble(MongoFields.FIELD_LEARNER_PROGRESS);
      if (Double.isNaN(progress))
      {
        progress = 0.0;
        LOGGER.error("{} learner progress is NaN.", document.getString(MongoFields.FIELD_ID));
      }
      perfectViewsCount += progress >= 90.0 ? 1 : 0;
      totalProgress += progress;
      List<List<String>> spentTime = (List<List<String>>) document.get(MongoFields.FIELD_LEARNER_SPENT_TIME);
      for (List<String> courseSpentTime: spentTime)
      {
        Long time = 0L;
        for (String chapterSpentTime: courseSpentTime)
        {
          time += TimeUtils.convertToLongRepresentation(SCORMTime.convertToHumanReadableTime(chapterSpentTime));
        }
        totalTime.add(time);
      }
      spentTimes.add(getMedianTime(totalTime));
    }

    Optional<Long> spentTimeSum = spentTimes.stream().reduce(Long::sum);

    long avgTime = spentTimeSum.map(aLong -> aLong / learnerCount).orElse(0L);

    double avgProgress = totalProgress != 0.0 ? totalProgress / learnerCount : 0.0;

    return new LearnerActivity(TimeUtils.convertToStringRepresentation(avgTime), learnerCount, perfectViewsCount, avgProgress);
  }

  @NotNull
  private List<LearnerProgress> getProgress(List<Document> documents)
  {
    List<LearnerProgress> progresses = new ArrayList<>();
    for (Document document : documents)
    {
      progresses.add(new LearnerProgress(document.getString(MongoFields.FIELD_ID), document.getDouble(MongoFields.FIELD_LEARNER_PROGRESS)));
    }
    return progresses;
  }
}
