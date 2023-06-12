/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Aggregate;

/**
 * A SCORM wrapper is a set of assets {@link Asset} that makes it possible for a SCORM content
 * to communicate with the LMS. In other words, it enables a SCORM content to track a learner's
 * progress and other essential data.
 * <p>
 * The root entity of a wrapper will be an Asset. Specifically, it must be an HTML file whose
 * dependencies may include CSS, JavaScript files. It also contains the SCORM API discovery algorithm,
 * which finds the API adapter of a Learning Management System. Once the wrapper finds the API adapter,
 * it'll then trigger run-time calls {@link RuntimeData} through getter and setter functions.
 * <p>
 * For more information on API discovery algorithm, see:
 * https://scorm.com/scorm-explained/technical-scorm/run-time/api-discovery-algorithms/
 *
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMWrapper implements Aggregate<Asset>
{
  /*
  This query string parameter will be used to reference the location of a SCO.
  For example, suppose there is a SCO named "chapter 1" which contains 9 assets or instructional materials.
  In that case, the location of that SCO could be identified as "{LMS_BASE_URL}/index.html?content=chapter1",
  where LMS_BASE_URL is the base url of the LMS and index.html is the actual wrapper. Then, a learner could access
  not only the contents of the "chapter 1", but also his or her run-time data, assuming that the wrapper provides this functionality.
   */
  private static final String QUERY_STRING_PARAMETER = "content";

  private final Asset indexHTML;

  // An LMS can provide a number of wrappers, in which case they will be identified by their type.
  private final String wrapperType;

  // Dependencies of a wrapper may include images, fonts, JavaScript or CSS files.
  private Set<ContentAggregation.Resource> dependencies = new HashSet<>();

  // To be properly formatted, every SCORM content should include XML schema definition files for the manifest file
  private Set<AssetId> xmlSchemaDefinitionFiles = new HashSet<>();

  public SCORMWrapper(Asset indexHTML, String wrapperType)
  {
    this.indexHTML = Objects.requireNonNull(indexHTML, "The wrapper must have an HTML file");
    this.wrapperType = Validate.notBlank(wrapperType, "Wrapper type cannot be null or blank!");
  }

  @Override
  public Asset getRootEntity()
  {
    return this.indexHTML;
  }

  public void addXsd(AssetId xsd)
  {
    if (xsd != null)
    {
      this.xmlSchemaDefinitionFiles.add(xsd);
    }
  }

  public void addDependency(ContentAggregation.Resource dependency)
  {
    if (dependency != null && !this.indexHTML.getAssetId().equals(dependency.getAssetId()))
    {
      this.dependencies.add(dependency);
    }
  }

  public String getWrapperType()
  {
    return wrapperType;
  }

  public Set<ContentAggregation.Resource> getDependencies()
  {
    return Collections.unmodifiableSet(dependencies);
  }

  public Set<AssetId> getXmlSchemaDefinitionFiles()
  {
    return Collections.unmodifiableSet(xmlSchemaDefinitionFiles);
  }

  public String getQueryStringParameter()
  {
    return this.indexHTML.getName() + "?" + QUERY_STRING_PARAMETER + "=";
  }
}
