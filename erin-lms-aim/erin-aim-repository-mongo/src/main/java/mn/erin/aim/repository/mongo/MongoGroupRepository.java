package mn.erin.aim.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.model.EntityId;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

/**
 * @author Zorig
 */
@Repository("groupRepository")
public class MongoGroupRepository implements GroupRepository
{
  private static final String FIELD_ID = "_id";
  private static final String FIELD_PARENT_ID = "parentId";
  private static final String FIELD_TENANT_ID = "tenantId";
  private static final String FIELD_CHILDREN = "children";
  private static final String FIELD_NTH_SIBLING = "nthSibling";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_DESCRIPTION = "description";

  private static final String ERR_MSG_PARENT_NONEXISTENT = "Parent does not exist.";

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoGroupRepository.class);

  protected final MongoCollection<Document> groupCollection;

  public MongoGroupRepository(MongoTemplate aimMongoTemplate)
  {
    this.groupCollection = aimMongoTemplate.getCollection("groups");
  }

  @Override
  public Group findByNumberAndTenantId(String number, TenantId tenantId)
  {
    Bson filter = and(
      eq(FIELD_DESCRIPTION, number),
      eq(FIELD_TENANT_ID, tenantId.getId())
    );
    Document groupDocument = groupCollection.find(filter).iterator().next();
    return convertToGroupObject(groupDocument);
  }

  @Override
  public Group findByName(String name)
  {
    Bson filter = eq(FIELD_NAME, name);
    MongoCursor<Document> iterator = groupCollection.find(filter).iterator();
    if (!iterator.hasNext())
    {
      return null;
    }
    Document groupDocument = iterator.next();
    return convertToGroupObject(groupDocument);
  }

  @Override
  public Group createGroup(String name, String parentId, String description, TenantId tenantId)
  {
    ObjectId objectId = new ObjectId(new Date());
    String id = objectId.toHexString();

    GroupId parentGroupId = null;
    //if parentid is not null then that parentid needs to set/add childid
    if (!StringUtils.isBlank(parentId))
    {
      setChild(parentId, id);
      parentGroupId = GroupId.valueOf(parentId);
    }

    Group group = new Group(GroupId.valueOf(id), parentGroupId, tenantId, name);
    group.setDescription(description);

    //always run this after child has been set
    int nThSibling = getNthSibling(parentId);
    group.setNthSibling(nThSibling);

    //Define a constant instead of duplicating literal multiple times
    Document documentToInsert = new Document();
    documentToInsert.put(FIELD_ID, objectId);
    documentToInsert.put(FIELD_PARENT_ID, parentId);
    documentToInsert.put(FIELD_TENANT_ID, tenantId.getId());
    documentToInsert.put(FIELD_CHILDREN, new ArrayList<String>());
    documentToInsert.put(FIELD_NTH_SIBLING, nThSibling);
    documentToInsert.put(FIELD_NAME, name);

    groupCollection.insertOne(documentToInsert);
    return group;
  }

  @Override
  public boolean doesGroupExist(GroupId id)
  {
    return groupCollection.countDocuments(Filters.eq(FIELD_ID, new ObjectId(id.getId()))) > 0;
  }

  @Override
  public boolean doesGroupExistByName(String name)
  {
    return groupCollection.countDocuments(Filters.eq(FIELD_NAME, name)) > 0;
  }

  @Override
  public Collection<Group> findAll()
  {
    List<Group> groupsListToReturn = new ArrayList<>();
    for (Document document : groupCollection.find())
    {
      groupsListToReturn.add(convertToGroupObject(document));
    }
    return groupsListToReturn;
  }

  @Override
  public boolean deleteGroup(String groupId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(groupId));
    String parentId = (String) groupCollection.find(filter).iterator().next().get(FIELD_PARENT_ID);

    if (parentId != null)
    {
      deleteSiblingShift(groupId, parentId);
      removeChild(parentId, groupId);
    }

    Queue<String> groupsToTraverse = new PriorityQueue<>();
    groupsToTraverse.add(groupId); //root

    Iterator<Document> documentIterator;
    Iterator<String> children;
    while (!groupsToTraverse.isEmpty())
    {
      documentIterator = groupCollection.find(filter).iterator();
      children = ((ArrayList<String>) documentIterator.next().get(FIELD_CHILDREN)).iterator();

      String currentPeak = groupsToTraverse.peek();

      while (children.hasNext())
      {
        String nextChild = children.next();
        groupsToTraverse.add(nextChild);
      }

      //delete and check if deletion is successful
      if (groupCollection.deleteOne(filter).getDeletedCount() == 0)
      {
        LOGGER.warn("Group not deleted, group id: {}", currentPeak);
      }

      groupsToTraverse.remove(currentPeak);
      String newPeak = groupsToTraverse.peek();

      //check if it has peek or empty
      //set new filter for next iteration
      if (!groupsToTraverse.isEmpty())
      {
        filter = new Document(FIELD_ID, new ObjectId(newPeak));
      }
    }

    //do a check if delete was successful and set return accordingly
    return true;
  }

  @Override
  public Set<GroupId> getAllSubGroups(String number)
  {
    // find group by number
    Document groupDocument = groupCollection.find(eq(FIELD_DESCRIPTION, number)).iterator().next();
    String groupId = groupDocument.getObjectId(FIELD_ID).toHexString();
    return getSubGroups(groupId).stream().map(GroupId::valueOf).collect(Collectors.toSet());
  }

  public Set<String> getSubGroups(String groupId)
  {
    Bson filter = eq(FIELD_PARENT_ID, groupId);
    FindIterable<Document> documents = groupCollection.find(filter);
    Iterator<Document> iterator = documents.iterator();

    Set<String> subGroups = new HashSet<>();
    subGroups.add(groupId);
    while (iterator.hasNext())
    {
      String id = iterator.next().getObjectId(FIELD_ID).toHexString();
      subGroups.add(id);
    }
    return subGroups;
  }

  @Override
  public Group moveGroupParent(String groupId, String parentId)
  {
    Document currentParentFilter = new Document(FIELD_ID, new ObjectId(groupId));
    String currentParentIdOfGroup = (String) groupCollection.find(currentParentFilter).iterator().next().get(FIELD_PARENT_ID);

    deleteSiblingShift(groupId, currentParentIdOfGroup);
    if (removeChild(currentParentIdOfGroup, groupId))
    {
      throw new IllegalArgumentException("Unsuccessful child removal!");
    }
    if (!setParent(parentId, groupId))
    {
      throw new IllegalArgumentException("Unsuccessful parent update!");
    }
    if (!setNthSibling(groupId, getNthSibling(parentId)))
    {
      throw new IllegalArgumentException("Unsuccessful set nthSibling");
    }
    if (!setChild(parentId, groupId))
    {
      throw new IllegalArgumentException("Unsuccessful child update!");
    }

    return mapToGroupObject(groupId);
  }

  @Override
  public Group moveGroupSibling(String groupId, int siblingNumber)
  {
    Document groupFilter = new Document(FIELD_ID, new ObjectId(groupId));
    Document groupDocument = groupCollection.find(groupFilter).iterator().next();
    String parentId = (String) groupDocument.get(FIELD_PARENT_ID);
    Document parentFilter = new Document(FIELD_ID, new ObjectId(parentId));
    Document parentGroupDocument = groupCollection.find(parentFilter).iterator().next();

    ArrayList<String> childrenList = (ArrayList<String>) parentGroupDocument.get(FIELD_CHILDREN);
    if (siblingNumber > childrenList.size() - 1)
    {
      throw new IllegalArgumentException("Sibling number should not exceed limit the max index of sibling!");
    }

    childrenList.remove(groupId);
    childrenList.add(siblingNumber, groupId);

    Document childrenFilter = new Document(FIELD_CHILDREN, childrenList);
    Document setChildrenFilter = new Document("$set", childrenFilter);
    groupCollection.findOneAndUpdate(parentFilter, setChildrenFilter);

    for (String currentGroupFilterString : childrenList)
    {
      int nthSibling = childrenList.indexOf(currentGroupFilterString) + 1;
      setNthSibling(currentGroupFilterString, nthSibling);//wrap this in try catch?
    }
    return mapToGroupObject(groupId);
  }

  @Override
  public Group renameGroup(String groupId, String newName)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(groupId));
    Document nameDocument = new Document(FIELD_NAME, newName);
    Document setFilter = new Document("$set", nameDocument);

    groupCollection.findOneAndUpdate(filter, setFilter);
    String name = (String) groupCollection.find(filter).iterator().next().get(FIELD_NAME);
    if (!newName.equals(name))
    {
      throw new IllegalArgumentException("Unsuccessful name change!");
    }
    return mapToGroupObject(groupId);
  }

  @Override
  public Set<String> getParentGroupIds(String groupId)
  {
    Bson filter = eq(FIELD_CHILDREN, groupId);
    FindIterable<Document> documents = groupCollection.find(filter);
    Iterator<Document> iterator = documents.iterator();

    Set<String> parentGroups = new HashSet<>();
    while (iterator.hasNext())
    {
      String id = iterator.next().getObjectId(FIELD_ID).toHexString();
      parentGroups.add(id);
      filter = eq(FIELD_CHILDREN, id);
      documents = groupCollection.find(filter);
      iterator = documents.iterator();
    }

    return parentGroups;
  }

  private boolean removeChild(String parentId, String childId)
  {
    Document parentFilter = new Document(FIELD_ID, new ObjectId(parentId));
    if (!doesGroupExist(GroupId.valueOf(parentId)))
    {
      throw new IllegalArgumentException(ERR_MSG_PARENT_NONEXISTENT);
    }
    if (!doesChildExist(parentId, childId))
    {
      throw new IllegalArgumentException("Child does not exist. Can't delete child that does not exist.");
    }
    Document removeElementDocument = new Document(FIELD_CHILDREN, childId);
    Document updateDocument = new Document("$pull", removeElementDocument);
    Document documentThatWasUpdated = groupCollection.findOneAndUpdate(parentFilter, updateDocument);
    assert documentThatWasUpdated != null;
    return !((Collection<String>) documentThatWasUpdated.get(FIELD_CHILDREN)).contains(childId);
  }

  @Override
  public int getNthSibling(String parentId)
  {
    //if we decide to start counting sibling from 0, then we can set root as a -negative number, and start counting from 0
    if (parentId != null)
    {
      return getChildrenCount(parentId) + 1;
    }
    //means root
    return 0;
  }

  @Override
  public List<Group> getAllRootGroups(TenantId tenantId)
  {
    Document tenantIdFilter = new Document(FIELD_TENANT_ID, tenantId.getId());
    Document rootFilter = new Document(FIELD_PARENT_ID, null);
    Bson andFilter = and(tenantIdFilter, rootFilter);
    List<Group> groupsToReturn = new ArrayList<>();
    for (Document document : groupCollection.find(andFilter))
    {
      groupsToReturn.add(convertToGroupObject(document));
    }
    return groupsToReturn;
  }

  @Override
  public List<Group> getAllGroups(Set<String> groupIds)
  {
    Set<ObjectId> groupsToFind = groupIds.stream().map(ObjectId::new).collect(Collectors.toSet());
    List<Group> groups = new ArrayList<>();
    for (Document document : groupCollection.find(in(FIELD_ID, groupsToFind)))
    {
      groups.add(convertToGroupObject(document));
    }
    return groups;
  }

  @Override
  public Map<String, String> getAllGroupNamesAndId()
  {
    Map<String, String> groupIdAndNames = new HashMap<>();
    for (Document document : groupCollection.find())
    {
      String name = (String) document.get(FIELD_NAME);
      String id = ((ObjectId) document.get(FIELD_ID)).toHexString();
      groupIdAndNames.put(name, id);
    }
    return groupIdAndNames;
  }

  @Override
  public Group findById(EntityId entityId)
  {
    String filterId = entityId.getId();
    return mapToGroupObject(filterId);
  }

  private boolean setChild(String parentId, String childId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(parentId));

    //check if the parent exists, implement a private method
    if (!doesGroupExist(GroupId.valueOf(parentId)))
    {
      throw new IllegalArgumentException(ERR_MSG_PARENT_NONEXISTENT);
    }

    Document arrayAddDocument = new Document(FIELD_CHILDREN, childId);
    Document updateDocument = new Document("$push", arrayAddDocument);
    groupCollection.findOneAndUpdate(filter, updateDocument);

    Document updatedDocument = groupCollection.find(filter).iterator().next();

    //check the returned document if the child has been added
    return ((ArrayList<String>) updatedDocument.get(FIELD_CHILDREN)).contains(childId);
  }

  private boolean doesChildExist(String parentId, String childIdSearch)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(parentId));

    Bson childFilter = Filters.eq(FIELD_CHILDREN, childIdSearch);
    Bson combinationFilter = and(filter, childFilter);

    return groupCollection.find(combinationFilter).iterator().hasNext();
  }

  private boolean setParent(String parentId, String childId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(childId));
    Document parentUpdate = new Document(FIELD_PARENT_ID, parentId);
    Document updateDocument = new Document("$set", parentUpdate);
    groupCollection.findOneAndUpdate(filter, updateDocument);

    Document updatedDocument = groupCollection.find(filter).iterator().next();
    return updatedDocument.get(FIELD_PARENT_ID).equals(parentId);
  }

  private void deleteSiblingShift(String groupId, String parentId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(groupId));
    Document parentFilter = new Document(FIELD_ID, new ObjectId(parentId));

    int index = ((int) groupCollection.find(filter).iterator().next().get(FIELD_NTH_SIBLING)) - 1;

    List<String> siblings = (ArrayList<String>) groupCollection.find(parentFilter).iterator().next().get(FIELD_CHILDREN);
    List<String> siblingsToShiftList = new ArrayList<>();

    for (int i = index; i <= siblings.size() - 1; i++)
    {
      siblingsToShiftList.add(siblings.get(i));
    }

    Iterator<String> siblingsToShiftIterator = siblingsToShiftList.iterator();
    int nthSibling;
    while (siblingsToShiftIterator.hasNext())
    {
      Document siblingFilter = new Document(FIELD_ID, new ObjectId(siblingsToShiftIterator.next()));
      nthSibling = (int) groupCollection.find(siblingFilter).iterator().next().get(FIELD_NTH_SIBLING);
      Document siblingNumberDocument = new Document(FIELD_NTH_SIBLING, nthSibling - 1);
      Document setFilter = new Document("$set", siblingNumberDocument);
      groupCollection.findOneAndUpdate(siblingFilter, setFilter);
    }
  }

  private int getChildrenCount(String parentId)
  {
    if (!doesGroupExist(GroupId.valueOf(parentId)))
    {
      throw new IllegalArgumentException(ERR_MSG_PARENT_NONEXISTENT);
    }
    Document filter = new Document(FIELD_ID, new ObjectId(parentId));
    return ((ArrayList<String>) groupCollection.find(filter).iterator().next().get(FIELD_CHILDREN)).size();
  }

  private Group mapToGroupObject(String filterId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(filterId));
    MongoCursor<Document> iterator = groupCollection.find(filter).iterator();
    if (!iterator.hasNext())
    {
      return null;
    }
    Document returnedGroupDocument = iterator.next();
    return convertToGroupObject(returnedGroupDocument);
  }

  private Group convertToGroupObject(Document document)
  {
    GroupId parentIdObject = null;

    String id = ((ObjectId) document.get(FIELD_ID)).toHexString();
    String parentId = (String) document.get(FIELD_PARENT_ID);
    String tenantId = (String) document.get(FIELD_TENANT_ID);
    String name = (String) document.get(FIELD_NAME);
    int nthSibling = (int) document.get(FIELD_NTH_SIBLING);
    String description = (String) document.get(FIELD_DESCRIPTION);
    List<String> children = (ArrayList<String>) document.get(FIELD_CHILDREN);

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

  private boolean setNthSibling(String groupId, int nthSibling)
  {
    Document groupFilter = new Document(FIELD_ID, new ObjectId(groupId));
    Document nthSiblingFilter = new Document(FIELD_NTH_SIBLING, nthSibling);
    Document setFilter = new Document("$set", nthSiblingFilter);
    groupCollection.findOneAndUpdate(groupFilter, setFilter);

    int updatedNthSibling = (int) groupCollection.find(groupFilter).iterator().next().get(FIELD_NTH_SIBLING);
    return nthSibling == updatedNthSibling;
  }

  @Override
  public Set<Group> getAllByIds(Set<String> groupIds)
  {
    Validate.notNull(groupIds);
    Bson filter = in(FIELD_ID, groupIds.stream().map(ObjectId::new).collect(Collectors.toSet()));
    return groupCollection.find(filter).map(this::convertToGroupObject).into(new HashSet<>());
  }
}
