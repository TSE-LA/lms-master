package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.domain.model.certificate.Certificate;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.aim.user.AuthorId;

/**
 * @author Erdenetulga
 */
public interface CertificateRepository
{
    Certificate createCertificate(String name, AuthorId authorId) throws LmsRepositoryException;

    void updateCertificate(String name, AuthorId authorId, CertificateId certificateId, String certificateFolderId, boolean isUsed) throws LmsRepositoryException;

    List<Certificate> listAll();

    boolean delete(CertificateId certificateId);

    Certificate fetchById(CertificateId certificateId) throws LmsRepositoryException;

}
