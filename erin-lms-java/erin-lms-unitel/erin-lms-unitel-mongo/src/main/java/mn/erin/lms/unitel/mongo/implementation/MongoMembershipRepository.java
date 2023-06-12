package mn.erin.lms.unitel.mongo.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoMembershipRepository implements MembershipRepository
{
  private static final String ERR_MSG_USER_ID = "Username cannot be null!";
  private static final String ERR_MSG_GROUP_ID = "GroupID cannot be null!";
  private static final String ERR_MSG_ROLE_ID = "RoleID cannot be null!";
  private static final String ERR_MSG_TENANT_ID = "TenantID cannot be null!";

  private static final String FIELD_ID = "_id";
  private static final String FIELD_USER_ID = "userId"; // persisted value is username
  private static final String FIELD_GROUP_ID = "groupId";
  private static final String FIELD_ROLE_ID = "roleId";
  private static final String FIELD_TENANT_ID = "tenantId";

  private final MongoCollection<Document> collection;

  public MongoMembershipRepository(MongoCollection<Document> collection)
  {
    this.collection = collection;
  }

  @Override
  public Membership create(String username, GroupId groupId, RoleId roleId, TenantId tenantId) throws AimRepositoryException
  {
    Validate.notNull(username, ERR_MSG_USER_ID);
    Validate.notNull(groupId, ERR_MSG_GROUP_ID);
    Validate.notNull(roleId, ERR_MSG_ROLE_ID);
    Validate.notNull(tenantId, ERR_MSG_TENANT_ID);

    if (collection.find(eq(FIELD_USER_ID, eq(FIELD_ID, username))).iterator().hasNext())
    {
      throw new AimRepositoryException("User [" + username + "] already exists for group [" + groupId.getId() + "]");
    }

    Document membershipAsDocument = new Document();
    ObjectId membershipId = new ObjectId(new Date());

    membershipAsDocument.put(FIELD_ID, membershipId);
    membershipAsDocument.put(FIELD_USER_ID, UserId.valueOf(username));
    membershipAsDocument.put(FIELD_GROUP_ID, groupId);
    membershipAsDocument.put(FIELD_ROLE_ID, roleId);
    membershipAsDocument.put(FIELD_TENANT_ID, tenantId);

    collection.insertOne(membershipAsDocument);
    Membership membership = new Membership(MembershipId.valueOf(membershipId.toHexString()), username, groupId, roleId);
    membership.setTenantId(tenantId);
    return membership;
  }

  @Override
  public Membership findByUsername(String username) throws AimRepositoryException
  {
    Validate.notNull(username, ERR_MSG_USER_ID);

    FindIterable<Document> documents = collection.find(eq(FIELD_USER_ID, eq(FIELD_ID, username)));
    Iterator<Document> iterator = documents.iterator();

    if (!iterator.hasNext())
    {
      throw new AimRepositoryException("Membership not found!");
    }
    Document document = iterator.next();
    return mapToMembership(document);
  }

  @Override
  public List<Membership> listAllByGroupId(TenantId tenantId, GroupId groupId) throws AimRepositoryException
  {
    Validate.notNull(groupId, ERR_MSG_GROUP_ID);
    Bson filter = eq(FIELD_GROUP_ID, groupId);

    FindIterable<Document> documents = collection.find(filter);
    Iterator<Document> iterator = documents.iterator();

    List<Membership> memberships = new ArrayList<>();
    while (iterator.hasNext())
    {
      Document document = iterator.next();
      memberships.add(mapToMembership(document));
    }
    return memberships;
  }

  @Override
  public boolean delete(MembershipId membershipId) throws AimRepositoryException
  {
    if (!ObjectId.isValid(membershipId.getId()))
    {
      throw new AimRepositoryException("Invalid Membership Id!");
    }

    Document membershipIdFilter = new Document(FIELD_ID, new ObjectId(membershipId.getId()));

    //check existence
    if (!collection.find(membershipIdFilter).iterator().hasNext())
    {
      throw new AimRepositoryException("Membership does not exist!");
    }

    try
    {
      return collection.deleteOne(membershipIdFilter).getDeletedCount() == 1;
    }
    catch (MongoException e)
    {
      throw new AimRepositoryException(e.getMessage());
    }
  }

  @Override
  public List<Membership> listAllByUsername(TenantId tenantId, String username) throws AimRepositoryException
  {
    return getMembershipsByFilter(eq(FIELD_USER_ID, eq(FIELD_ID, username)));
  }

  @Override
  public Collection<Membership> listAllByRole(RoleId roleId) throws AimRepositoryException
  {
    return getMembershipsByFilter(eq(FIELD_ROLE_ID, roleId));
  }

  @Override
  public Map<String, Membership> getAllForUsers(Collection<String> usernames)
  {
    FindIterable<Document> documents = collection.find(in(FIELD_USER_ID + "." + FIELD_ID/*userId._id*/, usernames));
    Map<String, Membership> memberships = new HashMap<>();
    for (Document doc : documents)
    {
      Membership membership = mapToMembership(doc);
      memberships.put(membership.getUsername(), membership);
    }
    // fill null for users whose membership is not yet created
    for (String username : usernames)
    {
      memberships.putIfAbsent(username, null);
    }
    return Collections.unmodifiableMap(memberships);
  }

  private List<Membership> getMembershipsByFilter(Bson filter)
  {
    FindIterable<Document> documents = collection.find(filter);
    List<Membership> memberships = new ArrayList<>();
    for (Document doc : documents)
    {
      memberships.add(mapToMembership(doc));
    }
    return memberships;
  }

  private Membership mapToMembership(Document document)
  {
    Document userIdAsDocument = (Document) document.get(FIELD_USER_ID);
    Document groupIdAsDocument = (Document) document.get(FIELD_GROUP_ID);
    Document roleIdAsDocument = (Document) document.get(FIELD_ROLE_ID);

    MembershipId membershipId = MembershipId.valueOf(document.getObjectId(FIELD_ID).toHexString());
    String username = userIdAsDocument.getString(FIELD_ID);
    GroupId groupId = GroupId.valueOf((String) groupIdAsDocument.get(FIELD_ID));
    RoleId roleId = RoleId.valueOf((String) roleIdAsDocument.get(FIELD_ID));

    return new Membership(membershipId, username, groupId, roleId);
  }
}
