package mn.erin.lms.base.aim.permission;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import mn.erin.domain.aim.model.permission.Permission;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PermissionProviderImpl implements PermissionProvider
{
  private static final String FILE_NAME = "permission.json";
  private static final String PROPERTIES = "properties";
  private static final String PERMISSION_ID = "id";

  private static final String DISABLE = "disable";
  private static final String VISIBLE = "visible";

  @Override
  public Set<Permission> getPermissionsByRole(String role)
  {
    if (null == role)
    {
      throw new IllegalArgumentException("Group id cannot be null!");
    }

    Map<String, Set<Permission>> permissionMap = getPermissionsMap();

    Set<Permission> permissionSet = permissionMap.get(role);

    if (null == permissionSet)
    {
      return Collections.emptySet();
    }
    return permissionSet;
  }

  private Map<String, Set<Permission>> getPermissionsMap()
  {
    Map<String, Set<Permission>> permissionsMap = new HashMap<>();
    JSONArray permissions = readPermission();

    for (int index = 0; index < permissions.length(); index++)
    {
      JSONObject permission = (JSONObject) permissions.get(index);

      Iterator<String> keys = permission.keys();

      while (keys.hasNext())
      {
        String key = keys.next();

        JSONArray userPermissions = (JSONArray) permission.get(key);

        Set<Permission> permissionsSet = getPermissionsFromJson(userPermissions);
        permissionsMap.put(key, permissionsSet);
      }
    }
    return permissionsMap;
  }

  /*
    Reads permission json from file.
  */
  private JSONArray readPermission()
  {
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME))
    {
      if (null == inputStream)
      {
        throw new IllegalArgumentException("Couldn't find resource file " + FILE_NAME);
      }

      return new JSONArray(new JSONTokener(inputStream));
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException("Couldn't find resource file " + FILE_NAME);
    }
  }

  /**
   * Gets {@link Permission} set from JSONArray.
   *
   * @param permissions Permission from permission.json
   * @return permission set
   */
  private Set<Permission> getPermissionsFromJson(JSONArray permissions)
  {
    Set<Permission> permissionSet = new HashSet<>();

    for (int index = 0; index < permissions.length(); index++)
    {
      JSONObject permissionJson = (JSONObject) permissions.get(index);

      String permissionString = permissionJson.getString(PERMISSION_ID);
      JSONObject propertiesJson = (JSONObject) permissionJson.get(PROPERTIES);

      Map<String, Serializable> properties = getPropertiesFrom(propertiesJson);

      Permission permission = Permission.valueOf(permissionString);
      permission.setProperties(properties);

      permissionSet.add(permission);
    }
    return permissionSet;
  }

  private Map<String, Serializable> getPropertiesFrom(JSONObject propertiesJson)
  {
    Map<String, Serializable> mapProps = new HashMap<>();

    boolean isDisable = propertiesJson.getBoolean(DISABLE);
    boolean isVisible = propertiesJson.getBoolean(VISIBLE);

    mapProps.put(DISABLE, isDisable);
    mapProps.put(VISIBLE, isVisible);

    return mapProps;
  }
}
