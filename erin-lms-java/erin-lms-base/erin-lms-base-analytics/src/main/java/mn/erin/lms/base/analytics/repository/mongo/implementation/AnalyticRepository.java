package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.service.UserService;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

/**
 * @author Munkh
 */
public abstract class AnalyticRepository
{
  protected final MongoCollectionProvider mongoCollectionProvider;
  protected final UserService userService;
  protected final MongoCollection<Document> groupsCollection;

  protected AnalyticRepository(MongoCollectionProvider mongoCollectionProvider, UserService userService)
  {
    this.mongoCollectionProvider = mongoCollectionProvider;
    this.userService = userService;
    groupsCollection = mongoCollectionProvider.getGroupsCollection();
  }

  public Group getGroup(String groupId)
  {
    Iterator<Document> iterator = findById(groupsCollection, groupId);

    if (!iterator.hasNext())
    {
      return null;
    }

    Document document = iterator.next();

    String parentId = document.getString(MongoFields.FIELD_GROUP_PARENT_ID);
    String tenantId = document.getString(MongoFields.FIELD_GROUP_TENANT_ID);

    return new Group(
        GroupId.valueOf(groupId),
        parentId != null ? GroupId.valueOf(parentId) : null,
        tenantId != null ? TenantId.valueOf(tenantId) : null,
        document.getString(MongoFields.FIELD_GROUP_NAME)
    );
  }

  public String getGroupName(String groupId)
  {
      Iterator<Document> iterator = findById(groupsCollection, groupId);
      if (!iterator.hasNext())
      {
        return null;
      }
      Document document = iterator.next();
      return document.getString(MongoFields.FIELD_GROUP_NAME);
  }

  public Set<String> getChildGroups(String groupId)
  {
    Set<String> children = Sets.newHashSet(groupId);

    Iterator<Document> iterator = findById(groupsCollection, groupId);

    if (!iterator.hasNext())
    {
      return children;
    }

    Document document = iterator.next();

    children.addAll((List<String>) document.get(MongoFields.FIELD_GROUP_CHILDREN));

    return children;
  }

  protected Set<String> getSubGroupIds(String groupId)
  {
    Set<String> children = Sets.newHashSet(groupId);

    Iterator<Document> iterator = findById(groupsCollection, groupId);

    if (!iterator.hasNext())
    {
      return children;
    }

    Document document = iterator.next();

    List<String> subGroupIds = (List<String>) document.get(MongoFields.FIELD_GROUP_CHILDREN);

    for (String subGroupId: subGroupIds)
    {
      children.addAll(getSubGroupIds(subGroupId));
    }

    return children;
  }

  protected List<Group> getSubGroups(String groupId)
  {
    List<Group> children = new ArrayList<>();

    Iterator<Document> iterator = findById(groupsCollection, groupId);

    if (!iterator.hasNext())
    {
      return children;
    }

    Document document = iterator.next();

    String parentId = document.getString(MongoFields.FIELD_GROUP_PARENT_ID);
    String tenantId = document.getString(MongoFields.FIELD_GROUP_TENANT_ID);
    List<String> childrenIds = (List<String>) document.get(MongoFields.FIELD_GROUP_CHILDREN);

    Group group = new Group(
        GroupId.valueOf(groupId),
        parentId != null ? GroupId.valueOf(parentId) : null,
        tenantId != null ? TenantId.valueOf(tenantId) : null,
        document.getString(MongoFields.FIELD_GROUP_NAME)
    );
    for (String childId : childrenIds)
    {
      group.addChild(GroupId.valueOf(childId));
    }

    children.add(group);

    List<String> subGroupIds = (List<String>) document.get(MongoFields.FIELD_GROUP_CHILDREN);

    for (String subGroupId: subGroupIds)
    {
      children.addAll(getSubGroups(subGroupId));
    }

    return children;
  }

  protected Set<String> getParentGroups(String groupId)
  {
    Set<String> parents = Sets.newHashSet(groupId);

    Iterator<Document> iterator = findById(groupsCollection, groupId);

    if (!iterator.hasNext())
    {
      return parents;
    }

    Document document = iterator.next();

    String parent = document.getString(MongoFields.FIELD_GROUP_PARENT_ID);

    if (StringUtils.isBlank(parent))
    {
      return parents;
    }

    parents.addAll(getParentGroups(parent));
    return parents;
  }

  protected Set<String> getSubCategories(String categoryId)
  {
    Set<String> subCategories = new HashSet<>();

    MongoCollection<Document> categoryCollection = mongoCollectionProvider.getCategoryCollection();

    Bson filter = eq(MongoFields.FIELD_CATEGORY_PARENT_CATEGORY_ID, categoryId);
    FindIterable<Document> result = categoryCollection.find(filter);
    Iterator<Document> iterator = result.iterator();

    if (!iterator.hasNext())
    {
      subCategories.add(categoryId);
    }

    while (iterator.hasNext())
    {
      Document document = iterator.next();
      subCategories.add(document.getObjectId(MongoFields.FIELD_ID).toHexString());
    }

    return subCategories;
  }

  public Set<String> getExistingCourseContentIds(Set<String> categories)
  {
    Set<String> contentIds = new HashSet<>();

    MongoCollection<Document> courseCollection = mongoCollectionProvider.getCourseCollection();

    Bson filter = in(MongoFields.FIELD_COURSE_CATEGORY_ID, categories);
    Iterable<Document> result = courseCollection.find(filter);

    for (Document document : result)
    {
      contentIds.add(document.getString(MongoFields.FIELD_COURSE_COURSE_CONTENT_ID));
    }

    return contentIds;
  }

  public Set<String> getLearners(Set<String> groups)
  {
    Set<String> learners = new HashSet<>();

    MongoCollection<Document> membershipsCollection = mongoCollectionProvider.getMembershipsCollection();

    for (String groupId : groups)
    {
      Bson filter = eq(MongoFields.FIELD_MEMBERSHIP_GROUP_ID, eq(MongoFields.FIELD_ID, groupId));
      FindIterable<Document> result = membershipsCollection.find(filter);

      for (Document document : result)
      {
        String learner = (((Document) document.get(MongoFields.FIELD_MEMBERSHIP_USER_ID)).getString(MongoFields.FIELD_ID));
        String role = ((Document) document.get(MongoFields.FIELD_MEMBERSHIP_ROLE_ID)).getString(MongoFields.FIELD_ID);
        if (userService.exists(learner) && !LmsRole.LMS_ADMIN.name().equals(role))
        {
          learners.add(learner);
        }
      }
    }

    return learners;
  }

  protected List<Group> getAllRootGroups(String tenantId){
    Document tenantIdFilter = new Document(MongoFields.FIELD_GROUP_TENANT_ID, tenantId);
    Document rootFilter = new Document(MongoFields.FIELD_GROUP_PARENT_ID, null);
    Bson bsonFilter = and(tenantIdFilter, rootFilter);
    MongoCollection<Document> groupsCollection = mongoCollectionProvider.getGroupsCollection();
    List<Group> groupsToReturn = new ArrayList<>();
    for (Document document : groupsCollection.find(bsonFilter))
    {
      groupsToReturn.add(convertToGroupObject(document));
    }
    return groupsToReturn;
  }

  protected List<Membership> getMemberships(Set<String> groups)
  {
    List<Membership> memberships = new ArrayList<>();

    MongoCollection<Document> membershipsCollection = mongoCollectionProvider.getMembershipsCollection();

    for (String groupId: groups)
    {
      Bson filter = eq(MongoFields.FIELD_MEMBERSHIP_GROUP_ID, eq(MongoFields.FIELD_ID, groupId));
      FindIterable<Document> result = membershipsCollection.find(filter);

      for (Document document : result)
      {
        String learner = (((Document) document.get(MongoFields.FIELD_MEMBERSHIP_USER_ID)).getString(MongoFields.FIELD_ID));
        if (userService.exists(learner))
        {
          memberships.add(new Membership(
              MembershipId.valueOf(document.getObjectId(MongoFields.FIELD_ID).toHexString()),
              learner,
              GroupId.valueOf(((Document) document.get(MongoFields.FIELD_MEMBERSHIP_GROUP_ID)).getString(MongoFields.FIELD_ID)),
              RoleId.valueOf(((Document) document.get(MongoFields.FIELD_MEMBERSHIP_ROLE_ID)).getString(MongoFields.FIELD_ID))
          ));
        }
      }
    }

    return memberships;
  }

  protected Set<String> getEnrolledCourses(String learnerId)
  {
    Set<String> enrolledCourses = new HashSet<>();

    MongoCollection<Document> enrollmentCollection = mongoCollectionProvider.getCourseEnrollmentCollection();

    Bson filter = eq(MongoFields.FIELD_ENROLLMENT_LEARNER_ID, learnerId);
    FindIterable<Document> result = enrollmentCollection.find(filter);

    for (Document document : result)
    {
      enrolledCourses.add(document.getString(MongoFields.FIELD_ENROLLMENT_COURSE_ID));
    }

    return enrolledCourses;
  }

  protected LocalDateTime getDate(String dateStringRepresentation)
  {
    try
    {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      Date date = formatter.parse(dateStringRepresentation);
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    catch (ParseException | NullPointerException e)
    {
      return null;
    }
  }

  protected Long getMedianTime(List<Long> timeList)
  {
    timeList.sort(Comparator.reverseOrder());
    if (timeList.isEmpty())
    {
      return 0L;
    }

    int size = timeList.size();
    int middle = size / 2;

    if (size % 2 == 1)
    {
      return timeList.get(middle);
    }
    else
    {
      return (timeList.get(middle - 1) + timeList.get(middle)) / 2;
    }
  }

  private Iterator<Document> findById(MongoCollection<Document> collection, String id)
  {
    Bson filter = eq(MongoFields.FIELD_ID, new ObjectId(id));
    FindIterable<Document> result = collection.find(filter);
    return result.iterator();
  }

  protected static String humanReadableFormat(Duration duration) {
    return duration.toString()
        .substring(2)
        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
        .toLowerCase();
  }

  private Group convertToGroupObject(Document document)
  {
    GroupId parentIdObject = null;

    String id = ((ObjectId) document.get(MongoFields.FIELD_ID)).toHexString();
    String parentId = (String) document.get(MongoFields.FIELD_GROUP_PARENT_ID);
    String tenantId = (String) document.get(MongoFields.FIELD_GROUP_TENANT_ID);
    String name = (String) document.get(MongoFields.FIELD_NAME);
    int nthSibling = (int) document.get(MongoFields.FIELD_GROUP_NTH_SIBLING);
    String description = (String) document.get(MongoFields.FIELD_GROUP_DESCRIPTION);
    List<String> children = (ArrayList<String>) document.get(MongoFields.FIELD_GROUP_CHILDREN);

    //needed for constructor of Group
    if (parentId != null)
    {
      parentIdObject = new GroupId(parentId);
    }

    TenantId tenant = new TenantId(tenantId != null ? tenantId : "nan");
    Group groupToReturn = new Group(new GroupId(id), parentIdObject, tenant, name);
    groupToReturn.setDescription(description);
    groupToReturn.setNthSibling(nthSibling);

    //add all children into group object's list one at a time
    for (String child : children)
    {
      groupToReturn.addChild(new GroupId(child));
    }

    return groupToReturn;
  }
}
