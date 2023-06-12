package mn.erin.lms.base.domain.usecase.certificate;

import mn.erin.lms.base.domain.model.certificate.Certificate;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.certificate.dto.CertificateDto;

/**
 * @author Erdenetulga
 */
public abstract class CertificateUseCase <I, O> extends LmsUseCase<I, O>
{
    public CertificateUseCase(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry) {
        super(lmsRepositoryRegistry, lmsServiceRegistry);

    }

    protected CertificateDto convert(Certificate certificate)
    {
        CertificateDto dto = new CertificateDto();

        dto.setName(certificate.getName());
        dto.setId(certificate.getCertificateId().getId());
        dto.setAuthorId(certificate.getAuthorId().getId());
        dto.setUsed(certificate.isUsed());
        return dto;
    }
}
