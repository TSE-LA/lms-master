/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * Content Aggregation is responsible for describing the structure of a SCORM content.
 * It outlines how assets {@link Asset} are organized, and consists of two essential pieces:
 * <p>
 * 1. Organization {@link Organization}: is a logical groupings of resources. Its main purpose is to maintain the order
 * of the resources.
 * <p>
 * 2. Resource {@link Resource}: is more or less information about an asset, consisting of its ID, name as well as its dependencies.
 * <p>
 * Because "Content Aggregation" <i>describes</i> the structure of a SCORM content,
 * it is a value object, and so are its inner classes.
 * <p>
 * It should also be noted that this model is ONLY used for creating a SCORM package.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class ContentAggregation implements ValueObject<ContentAggregation>, Serializable
{
  private static final long serialVersionUID = -5638084091460284394L;

  private final String scormContentName;
  private Set<Organization> organizations = new LinkedHashSet<>();

  public ContentAggregation(String scormContentName)
  {
    this.scormContentName = Validate.notBlank(scormContentName, "SCORM content name cannot be null or blank!");
  }

  @Override
  public boolean sameValueAs(ContentAggregation other)
  {
    return this.organizations.equals(other.organizations);
  }

  public void addOrganization(Organization organization)
  {
    if (organization != null)
    {
      this.organizations.add(organization);
    }
  }

  public String getScormContentName()
  {
    return scormContentName;
  }

  public Set<Organization> getOrganizations()
  {
    return Collections.unmodifiableSet(organizations);
  }

  public static class Organization implements ValueObject<Organization>, Serializable
  {
    private static final long serialVersionUID = -2903109783168716387L;

    private final String title;

    private Set<Resource> resources = new LinkedHashSet<>();

    // Short ID is primarily used as an alternative to a title with a long sequence of characters
    private String shortID;

    public Organization(String title)
    {
      this.title = Validate.notBlank(title, "Organization title cannot be null or blank!");
    }

    public void addResource(Resource resource)
    {
      if (resource != null)
      {
        this.resources.add(resource);
      }
    }

    public String getTitle()
    {
      return title;
    }

    public Set<Resource> getResources()
    {
      return Collections.unmodifiableSet(resources);
    }

    public String getShortID()
    {
      return shortID == null ? this.title : shortID;
    }

    public void setShortID(String shortID)
    {
      this.shortID = Validate.notBlank(shortID, "Short ID cannot be null or blank!");
    }

    @Override
    public boolean sameValueAs(Organization other)
    {
      return this.title.equalsIgnoreCase(other.title);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof Organization)
      {
        return sameValueAs((Organization) obj);
      }

      return false;
    }

    @Override
    public int hashCode()
    {
      return super.hashCode();
    }
  }

  public static class Resource implements ValueObject<Resource>, Serializable
  {
    private static final long serialVersionUID = 2672780152208144994L;

    private final AssetId assetId;
    private final String assetName;

    private Set<Resource> dependencies = new HashSet<>();

    public Resource(AssetId assetId, String assetName)
    {
      this.assetId = Objects.requireNonNull(assetId, "Asset ID cannot be null!");
      this.assetName = Validate.notBlank(assetName, "Asset name cannot be null or blank!");
    }

    public AssetId getAssetId()
    {
      return assetId;
    }

    public String getAssetName()
    {
      return assetName;
    }

    @Override
    public boolean sameValueAs(Resource other)
    {
      return this.assetId.equals(other.assetId);
    }

    public void addDependency(Resource dependency)
    {
      if (dependency != null)
      {
        this.dependencies.add(dependency);
      }
    }

    public Set<Resource> getDependencies()
    {
      return Collections.unmodifiableSet(dependencies);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof Resource)
      {
        return sameValueAs((Resource) obj);
      }

      return false;
    }

    @Override
    public int hashCode()
    {
      return super.hashCode();
    }
  }
}
