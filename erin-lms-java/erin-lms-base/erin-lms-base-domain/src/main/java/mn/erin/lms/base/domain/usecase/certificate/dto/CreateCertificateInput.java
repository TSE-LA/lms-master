package mn.erin.lms.base.domain.usecase.certificate.dto;

import java.io.File;

/**
 * @author Erdenetulga
 */
public class CreateCertificateInput {
    private final String name;
    private final String id;
    private File file;

    public CreateCertificateInput(String name, String id, File file) {
        this.name = name;
        this.id = id;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getId() {
        return id;
    }
}
