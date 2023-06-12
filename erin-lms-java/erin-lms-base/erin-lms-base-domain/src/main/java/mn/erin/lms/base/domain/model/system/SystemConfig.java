package mn.erin.lms.base.domain.model.system;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.lms.base.aim.organization.OrganizationId;

/**
 * @author Temuulen Naranbold
 */
public class SystemConfig implements Entity<SystemConfig>
{
  private static final String ROOT_URL = "/alfresco/System-Config/";

  private final SystemConfigId id;
  private FolderId folderId;
  private String folderName;
  private DocumentId documentId;
  private String documentName;
  private OrganizationId organizationId;
  private boolean isLogo;

  public SystemConfig(SystemConfigId id, FolderId folderId, String folderName, DocumentId documentId, String documentName,
      OrganizationId organizationId, boolean isLogo)
  {
    this.id = id;
    this.folderId = folderId;
    this.folderName = folderName;
    this.documentId = documentId;
    this.documentName = documentName;
    this.organizationId = organizationId;
    this.isLogo = isLogo;
  }

  public SystemConfigId getId()
  {
    return id;
  }

  public FolderId getFolderId()
  {
    return folderId;
  }

  public void setFolderId(FolderId folderId)
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

  public DocumentId getDocumentId()
  {
    return documentId;
  }

  public void setDocumentId(DocumentId documentId)
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

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public void setOrganizationId(OrganizationId organizationId)
  {
    this.organizationId = organizationId;
  }

  public boolean isLogo()
  {
    return isLogo;
  }

  public void setLogo(boolean logo)
  {
    isLogo = logo;
  }

  public String getImageURL()
  {
    return ROOT_URL + folderName + "/" + documentName;
  }

  @Override
  public boolean sameIdentityAs(SystemConfig other)
  {
    return this.id.equals(other.id);
  }
}
