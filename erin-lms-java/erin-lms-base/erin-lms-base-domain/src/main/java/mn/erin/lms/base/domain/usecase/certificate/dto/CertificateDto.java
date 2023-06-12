package mn.erin.lms.base.domain.usecase.certificate.dto;

/**
 * @author Erdenetulga
 */
public class CertificateDto {
    private String id;
    private String name;
    private String authorId;
    private boolean isUsed;

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

    public boolean isUsed()
    {
        return isUsed;
    }

    public void setUsed(boolean used)
    {
        isUsed = used;
    }
}
