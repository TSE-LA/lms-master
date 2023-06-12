package mn.erin.lms.base.domain.model.certificate;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.aim.user.AuthorId;

/**
 * @author Erdenetulga
 */
public class Certificate implements Entity<Certificate>
{
  private final CertificateId certificateId;
  private final String name;
  private final AuthorId authorId;
  private String certificateFolderId;
  private boolean isUsed;

  public Certificate(CertificateId certificateId, String name, AuthorId authorId, String certificateFolderId, boolean isUsed)
  {
    this.certificateId = certificateId;
    this.name = name;
    this.authorId = authorId;
    this.certificateFolderId = certificateFolderId;
    this.isUsed = isUsed;
  }

  public CertificateId getCertificateId()
  {
    return certificateId;
  }

  public String getName()
  {
    return name;
  }

  public AuthorId getAuthorId()
  {
    return authorId;
  }

  @Override
  public boolean sameIdentityAs(Certificate certificate)
  {
    return this.certificateId.sameValueAs(certificate.certificateId);
  }

  public String getCertificateFolderId()
  {
    return certificateFolderId;
  }

  public void setCertificateFolderId(String certificateFolderId)
  {
    this.certificateFolderId = certificateFolderId;
  }

  public boolean isUsed()
  {
    return isUsed;
  }

  public void setUsed(boolean used)
  {
    isUsed = used;
  }
}
