package mn.erin.lms.base.mongo.document.content;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Temuulen Naranbold
 */
@Document
public class MongoSystemConfig
{
  private String id;
  private String organizationId;
  private String folderId;
  private String folderName;
  private String documentId;
  private String documentName;
  private boolean isLogo;

  public MongoSystemConfig()
  {
    /*Need an empty constructor*/
  }

  public MongoSystemConfig(String id, String organizationId, String folderId, String folderName, String documentId, String documentName, boolean isLogo)
  {
    this.id = id;
    this.organizationId = organizationId;
    this.folderId = folderId;
    this.folderName = folderName;
    this.documentId = documentId;
    this.documentName = documentName;
    this.isLogo = isLogo;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getOrganizationId()
  {
    return organizationId;
  }

  public void setOrganizationId(String organizationId)
  {
    this.organizationId = organizationId;
  }

  public String getFolderId()
  {
    return folderId;
  }

  public void setFolderId(String folderId)
  {
    this.folderId = folderId;
  }

  public String getFolderName()
  {
    return folderName;
  }

  public void setFolderName(String folderName)
  {
    this.folderName = folderName;
  }

  public String getDocumentId()
  {
    return documentId;
  }

  public void setDocumentId(String documentId)
  {
    this.documentId = documentId;
  }

  public String getDocumentName()
  {
    return documentName;
  }

  public void setDocumentName(String documentName)
  {
    this.documentName = documentName;
  }

  public boolean isLogo()
  {
    return isLogo;
  }

  public void setLogo(boolean logo)
  {
    isLogo = logo;
  }
}
