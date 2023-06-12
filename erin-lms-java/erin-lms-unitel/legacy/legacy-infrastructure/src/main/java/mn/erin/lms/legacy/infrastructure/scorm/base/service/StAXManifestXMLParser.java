/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.base.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.xml.XmlFormatter;
import mn.erin.lms.legacy.domain.scorm.constants.ManifestConstants;
import mn.erin.lms.legacy.domain.scorm.model.ContentAggregation;
import mn.erin.lms.legacy.domain.scorm.model.ManifestXMLFile;
import mn.erin.lms.legacy.domain.scorm.model.SCO;
import mn.erin.lms.legacy.domain.scorm.service.ManifestParserException;
import mn.erin.lms.legacy.domain.scorm.service.ManifestXMLParser;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class StAXManifestXMLParser implements ManifestXMLParser
{
  private static final Logger LOGGER = LoggerFactory.getLogger(StAXManifestXMLParser.class);

  private static final String PATH_DELIMITER = "/";
  private static final String COMMON_FILES = "common_files";
  private static final String WEB_CONTENT = "webcontent";

  private XMLEventReader reader;
  private XMLEventWriter writer;
  private XMLEventFactory eventFactory;

  @Override
  public byte[] generateManifestXMLFile(ContentAggregation contentAggregation, List<String> commonFiles, String wrapperPathComponent)
      throws ManifestParserException
  {
    Validate.notNull(contentAggregation, "Content Aggregation cannot be null!");
    Validate.notEmpty(commonFiles, "Common files are required!");
    Validate.notBlank(wrapperPathComponent, "Wrapper path component is required!");

    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");


    URL manifestTemplate = getClass().getClassLoader().getResource(ManifestConstants.MANIFEST_TEMPLATE);

    if (manifestTemplate == null)
    {
      throw new ManifestParserException("Manifest template XML not found!");
    }

    ByteArrayOutputStream manifestXMLBytArrayOutputStream = new ByteArrayOutputStream();

    try
    {
      reader = inputFactory.createXMLEventReader(manifestTemplate.openStream());
      writer = outputFactory.createXMLEventWriter(manifestXMLBytArrayOutputStream, "UTF-8");

      eventFactory = XMLEventFactory.newInstance();

      while (reader.hasNext())
      {
        XMLEvent event = reader.nextEvent();
        writer.add(event);

        if (event.isStartElement())
        {
          StartElement startElement = (StartElement) event;

          if (ManifestConstants.ELEMENT_ORGANIZATION.equalsIgnoreCase(startElement.getName().getLocalPart()))
          {
            writeItemData(contentAggregation.getScormContentName(), contentAggregation.getOrganizations());
          }

          if (ManifestConstants.ELEMENT_RESOURCES.equalsIgnoreCase(startElement.getName().getLocalPart()))
          {
            writeResourceData(wrapperPathComponent, contentAggregation.getOrganizations());
            writeCommonFilesData(commonFiles);
          }
        }
      }

      writer.flush();

      // Close the writer and the reader.
      writer.close();
      reader.close();
    }
    catch (XMLStreamException | IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new ManifestParserException("Failed to write the manifest XML file", e);
    }

    // A new file for writing properly formatted and indented manifest file
    try
    {
      // Format the XML
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      XmlFormatter.prettify(new ByteArrayInputStream(manifestXMLBytArrayOutputStream.toByteArray()), os);
      return os.toByteArray();
    }
    catch (TransformerException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new ManifestParserException("Failed to format the manifest XML", e);
    }
  }

  @Override
  public Set<SCO> readManifestXMLFile(ManifestXMLFile manifestXMLFile) throws ManifestParserException
  {
    Validate.notNull(manifestXMLFile, "Manifest XML file cannot be null!");

    XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");


    Set<ItemData> itemData = new LinkedHashSet<>();
    Map<String, String> resourceData = new HashMap<>();

    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(manifestXMLFile.getAssetContent().getContent()))
    {
      reader = inputFactory.createXMLEventReader(byteArrayInputStream);

      String defaultOrganization;

      while (reader.hasNext())
      {
        XMLEvent event = reader.nextEvent();

        if (event.isStartElement())
        {
          StartElement startElement = (StartElement) event;

          /*
          The element "organizations" is at  the root of the manifest XML file. It has a "default" attribute which references to
          the default organization used for the SCORM content.
           */
          if (ManifestConstants.ELEMENT_ORGANIZATIONS.equalsIgnoreCase(startElement.getName().getLocalPart()))
          {
            Attribute defaultOrganizationIdentifier = startElement.getAttributeByName(ManifestConstants.QNAME_DEFAULT_ATTR);
            defaultOrganization = defaultOrganizationIdentifier.getValue();
            // Read the item data of the default organization.
            readItemData(defaultOrganization, itemData);
          }

          if (ManifestConstants.ELEMENT_RESOURCE.equalsIgnoreCase(startElement.getName().getLocalPart()))
          {
            Attribute scormType = startElement.getAttributeByName(ManifestConstants.QNAME_SCORM_TYPE_ATTR);

            if ("sco".equalsIgnoreCase(scormType.getValue()))
            {
              Attribute identifier = startElement.getAttributeByName(ManifestConstants.QNAME_IDENTIFIER_ATTR);
              Attribute href = startElement.getAttributeByName(ManifestConstants.QNAME_HREF_ATTR);

              resourceData.put(identifier.getValue(), href.getValue());
            }
          }
        }
      }

      // Close the reader.
      reader.close();
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new ManifestParserException("Failed to read the byte content of the manifest XML file", e);
    }
    catch (XMLStreamException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new ManifestParserException("Failed to parse the manifest XML file", e);
    }

    Set<SCO> scos = new LinkedHashSet<>();

    itemData.forEach(datum -> {
      String resourceIdentifierRef = datum.getResourceIdentifierRef();
      String path = resourceData.get(resourceIdentifierRef);
      scos.add(new SCO(manifestXMLFile.getRootEntity(), datum.getTitle(), path));
    });

    return scos;
  }

  /**
   * Writes the "item" data of the manifest XML file.
   * The element "item" represents the logical groupings of the parts of a SCORM content in the manifest file.
   * It has a title, its own reference and a reference to a resource.
   *
   * @param scormContentName The name of the SCORM content, which will be the title of the manifest XML file
   * @param organizations    The organizations of the content aggregation
   * @throws XMLStreamException if failed to write XML data
   * @see #writeResourceData(String, Set)
   */
  private void writeItemData(String scormContentName, Set<ContentAggregation.Organization> organizations) throws XMLStreamException
  {
    createTitleElement(scormContentName);

    int counter = 0;
    for (ContentAggregation.Organization organization : organizations)
    {
      Validate.notNull(organization, "Null organization"
          + " detected!");

      createStartElement(ManifestConstants.ELEMENT_ITEM);
      createAttribute(ManifestConstants.ATTR_IDENTIFIER, "i" + counter);
      createAttribute(ManifestConstants.ATTR_IDENTIFIER_REF, "r" + counter);
      counter++;

      createTitleElement(organization.getTitle());

      createEndElement(ManifestConstants.ELEMENT_ITEM);
    }
  }

  /**
   * Reads the "item" data of the "Default" organization of the manifest XML file.
   *
   * @param defaultOrganization The identifier of the default organization
   * @param itemData            The set in which the "item" data will be stored.
   * @throws XMLStreamException if failed to read the item data.
   */
  private void readItemData(String defaultOrganization, Set<ItemData> itemData) throws XMLStreamException
  {
    while (reader.hasNext())
    {
      XMLEvent event = reader.nextEvent();

      if (event.isStartElement())
      {
        StartElement startElement = (StartElement) event;

        if (ManifestConstants.ELEMENT_ORGANIZATION.equalsIgnoreCase(startElement.getName().getLocalPart()))
        {
          Attribute identifier = startElement.getAttributeByName(ManifestConstants.QNAME_IDENTIFIER_ATTR);

          if (defaultOrganization.equals(identifier.getValue()))
          {
            // Once the default organization is found, read its "item" data.
            readItemData(itemData);
          }
        }
      }

      if (event.isEndElement())
      {
        EndElement endElement = (EndElement) event;

        if (ManifestConstants.ELEMENT_ORGANIZATIONS.equalsIgnoreCase(endElement.getName().getLocalPart()))
        {
          break;
        }
      }
    }
  }

  /**
   * Reads the "item" data and stores it into a set.
   *
   * @param itemData The set in which the item data will be stored.
   * @throws XMLStreamException if failed to read item data.
   */
  private void readItemData(Set<ItemData> itemData) throws XMLStreamException
  {
    String itemTitle = "";
    String resourceIdentifierRefValue = "";

    while (reader.hasNext())
    {
      XMLEvent event = reader.nextEvent();

      if (event.isStartElement())
      {
        StartElement startElement = (StartElement) event;

        if (ManifestConstants.ELEMENT_ITEM.equalsIgnoreCase(startElement.getName().getLocalPart()))
        {
          Attribute resourceIdentifierRef = startElement.getAttributeByName(ManifestConstants.QNAME_IDENTIFIER_REF_ATTR);
          resourceIdentifierRefValue = resourceIdentifierRef.getValue();
        }

        if (ManifestConstants.ELEMENT_TITLE.equalsIgnoreCase(startElement.getName().getLocalPart()))
        {
          Characters titleText = (Characters) reader.nextEvent();
          itemTitle = titleText.getData();
        }
      }

      if (event.isEndElement())
      {
        EndElement endElement = (EndElement) event;

        if (ManifestConstants.ELEMENT_ITEM.equalsIgnoreCase(endElement.getName().getLocalPart()) &&
            !StringUtils.isBlank(itemTitle) &&
            !StringUtils.isBlank(resourceIdentifierRefValue))
        {
          itemData.add(new ItemData(itemTitle, resourceIdentifierRefValue));
          itemTitle = "";
          resourceIdentifierRefValue = "";
        }

        if (ManifestConstants.ELEMENT_ORGANIZATION.equalsIgnoreCase(endElement.getName().getLocalPart()))
        {
          break;
        }
      }
    }
  }

  /**
   * Writes "resource" data of the manifest file.
   *
   * @param wrapperPathComponent The wrapper path component will be used as an identifier for
   *                             a "resource" element, which in effect will be used to launch
   *                             the resource or SCO {@link mn.erin.domain.scorm.model.SCO}
   * @param organizations        The organizations of the content aggregation
   * @throws XMLStreamException if failed to write XML data
   */
  private void writeResourceData(String wrapperPathComponent, Set<ContentAggregation.Organization> organizations) throws XMLStreamException
  {
    int counter = 0;
    for (ContentAggregation.Organization organization : organizations)
    {
      createStartElement(ManifestConstants.ELEMENT_RESOURCE);
      createAttribute(ManifestConstants.ATTR_IDENTIFIER, "r" + counter);
      createAttribute(ManifestConstants.ATTR_TYPE, WEB_CONTENT);
      createScormTypeAttribute("sco");
      createAttribute(ManifestConstants.ATTR_HREF, wrapperPathComponent + organization.getShortID());

      for (ContentAggregation.Resource resource : organization.getResources())
      {
        Validate.notNull(resource, "Null resource detected!");

        createStartElement(ManifestConstants.ELEMENT_FILE);
        createAttribute(ManifestConstants.ATTR_HREF, organization.getShortID() + PATH_DELIMITER + resource.getAssetName());
        createEndElement(ManifestConstants.ELEMENT_FILE);

        // write resource dependencies recursively
        writeDependencyData(organization.getShortID(), resource);
      }
      createStartElement(ManifestConstants.ELEMENT_DEPENDENCY);
      createAttribute(ManifestConstants.ATTR_IDENTIFIER_REF, COMMON_FILES);
      createEndElement(ManifestConstants.ELEMENT_DEPENDENCY);

      createEndElement(ManifestConstants.ELEMENT_RESOURCE);

      counter++;
    }
  }

  /**
   * Writes the "resource" data of the common files used across every other resources.
   *
   * @param commonFiles The list of common files.
   * @throws XMLStreamException if failed to write XML data
   */
  private void writeCommonFilesData(List<String> commonFiles)
      throws XMLStreamException
  {
    createStartElement(ManifestConstants.ELEMENT_RESOURCE);
    createAttribute(ManifestConstants.ATTR_IDENTIFIER, COMMON_FILES);
    createAttribute(ManifestConstants.ATTR_TYPE, WEB_CONTENT);
    createScormTypeAttribute("asset");

    for (String file : commonFiles)
    {
      createFileElement(file);
    }

    createEndElement(ManifestConstants.ELEMENT_RESOURCE);
  }

  private void writeDependencyData(String organizationShortID, ContentAggregation.Resource resource) throws XMLStreamException
  {
    if (resource.getDependencies() == null || resource.getDependencies().isEmpty())
    {
      return;
    }

    for (ContentAggregation.Resource dependency : resource.getDependencies())
    {
      createFileElement(organizationShortID + PATH_DELIMITER + dependency.getAssetName());
      writeDependencyData(organizationShortID, dependency);
    }
  }

  private void createStartElement(String element) throws XMLStreamException
  {
    writer.add(eventFactory.createStartElement("", "", element));
  }

  private void createEndElement(String element) throws XMLStreamException
  {
    writer.add(eventFactory.createEndElement("", "", element));
  }

  private void createAttribute(String localName, String value) throws XMLStreamException
  {
    writer.add(eventFactory.createAttribute(localName, value));
  }

  private void createContent(String content) throws XMLStreamException
  {
    String value = new String(content.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    writer.add(eventFactory.createCharacters(value));
  }

  private void createTitleElement(String title) throws XMLStreamException
  {
    createStartElement(ManifestConstants.ELEMENT_TITLE);
    createContent(title);
    createEndElement(ManifestConstants.ELEMENT_TITLE);
  }

  private void createScormTypeAttribute(String value) throws XMLStreamException
  {
    writer.add(eventFactory.createAttribute(ManifestConstants.PREFIX_ADLCP, ManifestConstants.URI_ADL,
        ManifestConstants.ATTR_SCORM_TYPE, value));
  }

  private void createFileElement(String path) throws XMLStreamException
  {
    createStartElement(ManifestConstants.ELEMENT_FILE);
    createAttribute(ManifestConstants.ATTR_HREF, path);
    createEndElement(ManifestConstants.ELEMENT_FILE);
  }

  private static class ItemData
  {
    private final String title;
    private final String resourceIdentifierRef;

    ItemData(String title, String resourceIdentifierRef)
    {
      this.title = title;
      this.resourceIdentifierRef = resourceIdentifierRef;
    }

    String getTitle()
    {
      return title;
    }

    String getResourceIdentifierRef()
    {
      return resourceIdentifierRef;
    }
  }
}
