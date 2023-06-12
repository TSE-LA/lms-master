package mn.erin.lms.base.mongo.document.certificate;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Erdenetulga
 */
@Document
public class MongoCertificate {
    @Id
    private String id;
    @Indexed
    private String name;
    @Indexed
    private String authorId;

    private String certificateFolderId;

    private boolean isUsed;

    public MongoCertificate() {
    }

    public MongoCertificate(String id, String name, String authorId) {
        this.id = Validate.notBlank(id);
        this.name = Validate.notBlank(name);
        this.authorId = authorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCertificateFolderId() {
        return certificateFolderId;
    }

    public void setCertificateFolderId(String certificateFolderId) {
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
