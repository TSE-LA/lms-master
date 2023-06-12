package mn.erin.lms.base.mongo.implementation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.certificate.Certificate;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.repository.CertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.certificate.MongoCertificate;
import mn.erin.lms.base.mongo.repository.MongoCertificateRepository;
import mn.erin.lms.base.mongo.repository.MongoCourseRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Erdenetulga
 */
public class CertificateRepositoryImpl implements CertificateRepository
{
  private final MongoCertificateRepository mongoCertificateRepository;
  private final MongoCourseRepository mongoCourseRepository;

  public CertificateRepositoryImpl(MongoCertificateRepository mongoCertificateRepository, MongoCourseRepository mongoCourseRepository)
  {
    this.mongoCertificateRepository = mongoCertificateRepository;
    this.mongoCourseRepository = mongoCourseRepository;
  }

  @Override
  public Certificate createCertificate(String name, AuthorId authorId)
  {
    String id = IdGenerator.generateId();
    MongoCertificate mongoCertificate = new MongoCertificate(id, name, authorId.getId());

    mongoCertificate.setAuthorId(authorId.getId());
    mongoCertificateRepository.save(mongoCertificate);
    return new Certificate(CertificateId.valueOf(id), name, authorId, null, false);
  }

  @Override
  public void updateCertificate(String name, AuthorId authorId, CertificateId certificateId,
      String certificateFolderId, boolean isUsed) throws LmsRepositoryException
  {
    MongoCertificate mongoCertificate = getMongoCertificate(certificateId.getId());
    mongoCertificate.setName(name);
    mongoCertificate.setAuthorId(authorId.getId());
    mongoCertificate.setCertificateFolderId(certificateFolderId);
    mongoCertificate.setUsed(isUsed);
    mongoCertificate.setId(certificateId.getId());
    mongoCertificateRepository.save(mongoCertificate);
    mapToCertificate(mongoCertificate);
  }

  @Override
  public List<Certificate> listAll()
  {
    List<MongoCertificate> mongoCertificates = mongoCertificateRepository.findAll();
    return mongoCertificates.stream().map(this::mapToCertificate).collect(Collectors.toList());
  }

  private Certificate mapToCertificate(MongoCertificate mongoCertificate)
  {
    return new Certificate(
        CertificateId.valueOf(mongoCertificate.getId()),
        mongoCertificate.getName(),
        AuthorId.valueOf(mongoCertificate.getAuthorId()),
        mongoCertificate.getCertificateFolderId(),
        mongoCourseRepository.existsByCertificateId(mongoCertificate.getId()));
  }

  @Override
  public Certificate fetchById(CertificateId certificateId) throws LmsRepositoryException
  {
    return getCertificate(certificateId.getId());
  }

  private Certificate getCertificate(String certificateId) throws LmsRepositoryException
  {
    MongoCertificate mongoCertificate = getMongoCertificate(certificateId);
    return mapToCertificate(mongoCertificate);
  }

  private MongoCertificate getMongoCertificate(String courseId) throws LmsRepositoryException
  {
    Optional<MongoCertificate> mongoCertificate = mongoCertificateRepository.findById(courseId);

    if (!mongoCertificate.isPresent())
    {
      throw new LmsRepositoryException("The course with the ID: [" + courseId + "] does not exist!");
    }

    return mongoCertificate.get();
  }

  @Override
  public boolean delete(CertificateId certificateId)
  {
    mongoCertificateRepository.deleteById(certificateId.getId());
    return true;
  }
}
