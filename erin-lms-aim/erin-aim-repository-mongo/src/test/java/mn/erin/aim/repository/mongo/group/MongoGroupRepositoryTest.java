package mn.erin.aim.repository.mongo.group;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import mn.erin.aim.repository.mongo.MongoGroupRepository;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Mockito.doReturn;

/**
 * @author Zorig
 */
public class MongoGroupRepositoryTest
{
  MongoCollection<Document> groupCollection;
  MongoGroupRepository mongoGroupRepository;

  @Before
  public void setUp()
  {
    groupCollection = Mockito.mock(MongoCollection.class);
    MongoTemplate template = Mockito.mock(MongoTemplate.class);
    doReturn(groupCollection).when(template).getCollection("groups");
    mongoGroupRepository = new MongoGroupRepository(template);
  }

  // TODO: 6/16/2021 TODO: fix test when necessary
  //  public void createGroupTestForRootGroup()
  //  {
  //    String tenantId = "123";
  //    String groupId = "00897";
  //    String name = "Itgel";
  //    Group testGroup = new Group(new GroupId(groupId), null, new TenantId(tenantId), name);
  //    mongoGroupRepository.createGroup("name", "parentId", "Description", new TenantId(tenantId));
  //    ArgumentCaptor<Document> groupArgumentCaptor = ArgumentCaptor.forClass(Document.class);
  //    Mockito.verify(groupCollection).insertOne(groupArgumentCaptor.capture());
  //    Assert.assertNull(groupArgumentCaptor.getValue().get("parentId"));
  //  }

  @Test(expected = IllegalArgumentException.class)
  public void forDoesParentExist()
  {
    String parentId = "8723";
    String tenantId = "123";
    String groupId = "00897";
    String name = "Itgel";
    Group testGroup = new Group(new GroupId(groupId), new GroupId(parentId), new TenantId(tenantId), name);

    Document filter = new Document("parentId", new ObjectId(parentId));

    MongoCursor<Document> mockIterator = Mockito.mock(MongoCursor.class);

    FindIterable<Document> mockIterable = Mockito.mock(FindIterable.class);

    Mockito.when(mockIterable.iterator()).thenReturn(mockIterator);

    Mockito.when(mockIterator.hasNext()).thenReturn(false);

    Mockito.when(groupCollection.find(filter)).thenReturn(mockIterable);

    //mongoGroupRepository.createGroup(testGroup);
  }

  // TODO: 6/16/2021 TODO: fix test when necessary
  //  @Test
  //  public void UpdateChildrenWhenParentExists()
  //  {
  //    String parentId = "5e425c9445c567174879c611";
  //    String tenantId = "123";
  //    String groupId = "5e425cab45c567174879c612";
  //    String name = "Itgel";
  //    Group testGroup = new Group(new GroupId(groupId), new GroupId(parentId), new TenantId(tenantId), name);
  //    MongoCursor<Document> mockIterator = Mockito.mock(MongoCursor.class);
  //    Mockito.when(mockIterator.hasNext()).thenReturn(true);
  //    FindIterable<Document> mockIterable = Mockito.mock(FindIterable.class);
  //    Mockito.when(mockIterable.iterator()).thenReturn(mockIterator);
  //    Mockito.when(groupCollection.find(Mockito.any(Document.class))).thenReturn(mockIterable);
  //    Document arrayAddDocument = new Document("children", groupId);
  //    Document updateDocument = new Document("$push", arrayAddDocument);
  //    Document fieldIdFilter = new Document("_id", new ObjectId(parentId));
  //    System.out.println(fieldIdFilter);
  //    System.out.println(updateDocument);
  //    List<String> childrenList = new ArrayList<>();
  //    childrenList.add(groupId);
  //    Document updatedDocument = new Document();
  //    updatedDocument.put("children", childrenList);
  //    Mockito.when(groupCollection.findOneAndUpdate(Mockito.any(Document.class), Mockito.any(Document.class))).thenReturn(updatedDocument);
  //    mongoGroupRepository.createGroup(testGroup);
  //  }
}
