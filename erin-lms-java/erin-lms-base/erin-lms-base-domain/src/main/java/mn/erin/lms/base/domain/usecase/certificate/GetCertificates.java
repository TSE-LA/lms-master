package mn.erin.lms.base.domain.usecase.certificate;

import java.util.ArrayList;
import java.util.List;

import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.certificate.Certificate;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.CertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.certificate.dto.CertificateDto;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Instructor.class })
public class GetCertificates extends CertificateUseCase<Void, List<CertificateDto>>
{
    private final CertificateRepository certificateRepository;

    public GetCertificates(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
    {
        super(lmsRepositoryRegistry, lmsServiceRegistry);
        this.certificateRepository = lmsRepositoryRegistry.getCertificateRepository();
    }

    @Override
    protected List<CertificateDto> executeImpl(Void input)
    {
        List<Certificate> certificates = certificateRepository.listAll();
        List<CertificateDto> result = new ArrayList<>();
        for (Certificate certificate : certificates) {
            result.add(convert(certificate));
        }
        return result;
    }
}
