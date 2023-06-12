package mn.erin.lms.base.rest.api;

import java.io.File;
import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.system.GetSystemImageURL;
import mn.erin.lms.base.domain.usecase.system.UpdateBackgroundImage;
import mn.erin.lms.base.domain.usecase.content.dto.ImageExtension;
import mn.erin.lms.base.domain.usecase.system.dto.SystemConfigInput;

import static mn.erin.common.pdf.PdfUtil.getUUIdString;

/**
 * @author Temuulen Naranbold
 */
@Api
@RestController
@RequestMapping("lms/system-configs")
public class SystemConfigRestApi extends BaseLmsRestApi
{
  protected SystemConfigRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Update background image")
  @PostMapping("/system-image")
  public ResponseEntity<RestResult> updateImage(@RequestParam("file") MultipartFile multipartFile, @RequestParam boolean isLogo)
  {
    try
    {
      Validate.notNull(multipartFile);

      if (!ImageExtension.isSupported(FilenameUtils.getExtension(multipartFile.getOriginalFilename())))
      {
        throw new IllegalArgumentException("Unsupported image format!");
      }

      File image = saveFile(multipartFile, getUUIdString());
      SystemConfigInput input = new SystemConfigInput();
      input.setFile(image);
      input.setLogo(isLogo);

      UpdateBackgroundImage updateBackgroundImage = new UpdateBackgroundImage(lmsServiceRegistry.getLmsFileSystemService(),
          lmsServiceRegistry.getOrganizationIdProvider(), lmsRepositoryRegistry.getSystemConfigRepository());

      return RestResponse.success(updateBackgroundImage.execute(input));
    }
    catch (UseCaseException | NullPointerException | IllegalArgumentException | IOException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Get system image URL")
  @GetMapping("/system-image-url")
  public ResponseEntity<RestResult> getSystemImageURL(@RequestParam boolean isLogo)
  {
    try
    {
      GetSystemImageURL getSystemImageURL = new GetSystemImageURL(
          lmsRepositoryRegistry.getSystemConfigRepository(), lmsServiceRegistry.getOrganizationIdProvider());
      return RestResponse.success(getSystemImageURL.execute(isLogo));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}