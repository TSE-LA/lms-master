package mn.erin.aim.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.AimRole;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.base.model.EntityId;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Zorig
 */
@Repository("roleRepository")
public class MongoRoleRepository implements RoleRepository
{
  protected final MongoCollection<Document> roleCollection;

  private static final String USER_SELF_UPDATE_PERMISSION = "admin.aim.UpdateUserSelf";

  private static final String FIELD_ID = "_id";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_TENANT_ID = "tenantId";
  private static final String FIELD_PERMISSIONS = "permissions";
  private static final String FIELD_DESCRIPTION = "description";

  public MongoRoleRepository(MongoTemplate aimMongoTemplate, AimConfigProvider config) throws AimRepositoryException
  {
    this.roleCollection = aimMongoTemplate.getCollection("roles");

    // TODO: remove after role management
    if (!doesExistById(AimRole.ADMIN.getRoleId()))
    {
      create(
          config.getDefaultTenantId(),
          AimRole.ADMIN.getRoleId().getId(),
          config.getDefaultTenantId().getId(),
          Collections.singletonList(Permission.valueOf("*", "*", "*"))
      );
    }

    if (!doesExistById(AimRole.USER.getRoleId()))
    {
      create(
          config.getDefaultTenantId(),
          AimRole.USER.getRoleId().getId(),
          config.getDefaultTenantId().getId(),
          Collections.singletonList(Permission.valueOf(USER_SELF_UPDATE_PERMISSION))
      );
    }
  }

  @Override
  public Role create(TenantId tenantId, String id, String name, Collection<Permission> permissions) throws AimRepositoryException
  {
    String tenantIdToStore = tenantId.getId();

    Document roleDocumentToStore = new Document();
    roleDocumentToStore.put(FIELD_ID, id);
    roleDocumentToStore.put(FIELD_NAME, name);
    roleDocumentToStore.put(FIELD_TENANT_ID, tenantIdToStore);
    roleDocumentToStore.put(FIELD_PERMISSIONS, permissions.stream().map(Permission::getPermissionString).collect(Collectors.toList()));

    try
    {
      roleCollection.insertOne(roleDocumentToStore);
    }
    catch (MongoException e)
    {
      throw new AimRepositoryException(e.getMessage());
    }

    Document savedRoleDocument = roleCollection.find(new Document(FIELD_ID, id)).iterator().next();

    return convertToRole(savedRoleDocument);
  }

  @Override
  public Role create(TenantId tenantId, String id, String name) throws AimRepositoryException
  {
    String tenantIdToStore = tenantId.getId();

    Document roleDocumentToStore = new Document();
    roleDocumentToStore.put(FIELD_ID, id);
    roleDocumentToStore.put(FIELD_NAME, name);
    roleDocumentToStore.put(FIELD_TENANT_ID, tenantIdToStore);

    try
    {
      roleCollection.insertOne(roleDocumentToStore);
    }
    catch (MongoException e)
    {
      throw new AimRepositoryException(e.getMessage());
    }

    Document savedRoleDocument = roleCollection.find(new Document(FIELD_ID, id)).iterator().next();

    return convertToRole(savedRoleDocument);
  }

  @Override
  public List<Role> listAll(TenantId tenantId)
  {
    List<Role> rolesToReturn = new ArrayList<>();

    for (Document document : roleCollection.find())
    {
      rolesToReturn.add(convertToRole(document));
    }

    return rolesToReturn;
  }

  @Override
  public boolean doesExistById(RoleId roleId)
  {
    Document roleIdFilter = new Document(FIELD_ID, roleId.getId());
    return roleCollection.find(roleIdFilter).iterator().hasNext();
  }

  @Override
  public Role findById(EntityId entityId)
  {
    Document roleIdFilter = new Document(FIELD_ID, entityId.getId());
    Iterator<Document> returnedRoleDocumentIterator = roleCollection.find(roleIdFilter).iterator();

    if (returnedRoleDocumentIterator.hasNext())
    {
      return convertToRole(returnedRoleDocumentIterator.next());
    }
    return null;
  }

  @Override
  public Collection<Role> findAll()
  {
    List<Role> rolesListToReturn = new ArrayList<>();

    for (Document document : roleCollection.find())
    {
      rolesListToReturn.add(convertToRole(document));
    }
    return rolesListToReturn;
  }

  private Role convertToRole(Document roleDocument)
  {
    String roleIdString = roleDocument.getString(FIELD_ID);
    RoleId roleId = new RoleId(roleIdString);
    TenantId tenantId = new TenantId(roleDocument.getString(FIELD_TENANT_ID));
    String name = roleDocument.getString(FIELD_NAME);
    String description = roleDocument.getString(FIELD_DESCRIPTION);

    Role roleToReturn = new Role(roleId, tenantId, name);
    roleToReturn.setDescription(description);

    try
    {
      List<String> permissionStrings = (List<String>) roleDocument.get(FIELD_PERMISSIONS);

      if (permissionStrings != null && !permissionStrings.isEmpty())
      {
        for (String permissionString : permissionStrings)
        {
          roleToReturn.addPermission(Permission.valueOf(permissionString));
        }
      }
    }
    catch (ClassCastException ignored)
    {
      // The role has no permission
    }
    return roleToReturn;
  }
}
