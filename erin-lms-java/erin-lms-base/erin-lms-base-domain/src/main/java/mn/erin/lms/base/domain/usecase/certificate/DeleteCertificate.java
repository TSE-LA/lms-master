package mn.erin.lms.base.domain.usecase.certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.CertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Erdenetulga
 */
@Authorized(users = {Instructor.class})
public class DeleteCertificate extends CertificateUseCase<String, Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCertificate.class);
    private final CertificateRepository certificateRepository;

    public DeleteCertificate(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry) {
        super(lmsRepositoryRegistry, lmsServiceRegistry);
        this.certificateRepository = lmsRepositoryRegistry.getCertificateRepository();

    }

    @Override
    protected Boolean executeImpl(String certificateId) {
        LmsFileSystemService lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
        boolean isDeleted = lmsFileSystemService.deleteFolder(lmsFileSystemService.getCertificateFolderId(certificateId));
        if (isDeleted) {
            return certificateRepository.delete(CertificateId.valueOf(certificateId));
        } else {
            LOGGER.warn("ONLINE COURSE CERTIFICATE OF THE COURSE WITH THE ID: {} WAS NOT DELETED", certificateId);
            return false;
        }
    }
}
