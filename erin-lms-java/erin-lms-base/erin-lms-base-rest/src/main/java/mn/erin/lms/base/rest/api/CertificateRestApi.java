package mn.erin.lms.base.rest.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.certificate.CreateCertificate;
import mn.erin.lms.base.domain.usecase.certificate.DeleteCertificate;
import mn.erin.lms.base.domain.usecase.certificate.GenerateCertificate;
import mn.erin.lms.base.domain.usecase.certificate.GetCertificateById;
import mn.erin.lms.base.domain.usecase.certificate.GetCertificates;
import mn.erin.lms.base.domain.usecase.certificate.GetReceivedCertificates;
import mn.erin.lms.base.domain.usecase.certificate.ReceiveCertificate;
import mn.erin.lms.base.domain.usecase.certificate.dto.CertificateDto;
import mn.erin.lms.base.domain.usecase.certificate.dto.CreateCertificateInput;
import mn.erin.lms.base.domain.usecase.certificate.dto.GenerateCertificateInput;
import mn.erin.lms.base.domain.usecase.certificate.dto.ReceiveCertificateInput;
import mn.erin.lms.base.rest.model.RestCertificate;

import static mn.erin.common.pdf.PdfUtil.getUUIdString;

/**
 * @author Erdenetulga
 */
@Api("Certificate REST API")
@RequestMapping(value = "/lms/certificate", name = "Provides LMS certificate features")
@RestController
public class CertificateRestApi extends BaseLmsRestApi
{

  public CertificateRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Uploads a certificate files")
  @PostMapping
  public ResponseEntity createCertificate(@RequestParam("file") MultipartFile multipartFile) throws IOException
  {
    // files are uploaded to tomcat "temp" directory per default, we can use it as it is
    File file = lmsServiceRegistry.getTemporaryFileApi().store(multipartFile.getOriginalFilename(), multipartFile.getBytes());
    CreateCertificateInput input = new CreateCertificateInput(multipartFile.getOriginalFilename(), getUUIdString(), file);
    CreateCertificate createCertificate = new CreateCertificate(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(createCertificate.executeImpl(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Failed to read the certificate!");
    }
  }

  @ApiOperation("Fetches all certificates")
  @GetMapping
  public ResponseEntity<RestResult> readAll()
  {
    GetCertificates getCertificates = new GetCertificates(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      List<CertificateDto> certificate = getCertificates.execute(null);
      return RestResponse.success(certificate);
    }
    catch (UseCaseException e)
    {
      return RestResponse.success(e.getMessage());
    }
  }

  @ApiOperation("Fetches certificate by ID")
  @GetMapping("/{certificateId}")
  public ResponseEntity<RestResult> readById(@PathVariable String certificateId)
  {
    GetCertificateById getCertificateById = new GetCertificateById(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      CertificateDto dto = getCertificateById.execute(certificateId);
      return RestResponse.success(dto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes a certificate file")
  @DeleteMapping("/{certificateId}")
  public ResponseEntity<RestResult> delete(@PathVariable String certificateId)
  {
    DeleteCertificate deleteCertificate = new DeleteCertificate(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      boolean isDeleted = deleteCertificate.execute(certificateId);
      return RestResponse.success(isDeleted);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generate a certificate")
  @PostMapping("/generate")
  public ResponseEntity<RestResult> generateCertificate(@RequestBody RestCertificate body)
  {
    GenerateCertificateInput input = new GenerateCertificateInput(
        body.getCertificateId(),
        body.getCertificateName(),
        body.getRecipient(),
        body.getCourseId(),
        body.getCourseName(),
        body.getTargetPath(),
        body.getFileName());

    GenerateCertificate generateCertificate = new GenerateCertificate(lmsServiceRegistry, lmsRepositoryRegistry);
    String output;
    try
    {
      output = generateCertificate.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Receive certificate")
  @GetMapping("/receive")
  public ResponseEntity<RestResult> receiveCertificate(
      @RequestParam String certificateId,
      @RequestParam String learnerId,
      @RequestParam String courseId,
      @RequestParam String documentId)
  {
    ReceiveCertificateInput input = new ReceiveCertificateInput(learnerId, courseId, certificateId, documentId);
    ReceiveCertificate receiveCertificate = new ReceiveCertificate(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      Boolean result = receiveCertificate.execute(input);
      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Fetches received certificates")
  @GetMapping("/{learnerId:.+}/received-certificates")
  public ResponseEntity<RestResult> getReceivedCertificates(@PathVariable String learnerId)
  {
    GetReceivedCertificates getReceivedCertificates = new GetReceivedCertificates(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getReceivedCertificates.execute(learnerId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
