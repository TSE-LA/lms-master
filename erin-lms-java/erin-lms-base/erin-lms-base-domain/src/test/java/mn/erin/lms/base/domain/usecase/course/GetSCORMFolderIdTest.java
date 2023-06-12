package mn.erin.lms.base.domain.usecase.course;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetSCORMFolderIdTest
{
  private final String FOLDER_ID = "folderId";
  private LmsServiceRegistry lmsServiceRegistry;
  private GetSCORMFolderId getSCORMFolderId;
  private LmsFileSystemService lmsFileSystemService;

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
    getSCORMFolderId = new GetSCORMFolderId(lmsRepositoryRegistry, lmsServiceRegistry);
    lmsFileSystemService = Mockito.mock(LmsFileSystemService.class);
  }

  @Test(expected = NullPointerException.class)
  public void inputIsNull_throwsNullPointerException() throws UseCaseException
  {
    getSCORMFolderId.execute(null);
  }

  @Test
  public void assert_whenFolderFound() throws UseCaseException, DMSRepositoryException
  {
    Mockito.when(lmsServiceRegistry.getLmsFileSystemService()).thenReturn(lmsFileSystemService);
    Mockito.when(lmsFileSystemService.getSCORMFolderId(lmsFileSystemService.getCourseFolderId(Mockito.anyString()))).thenReturn(FOLDER_ID);
    String folderId = getSCORMFolderId.execute(FOLDER_ID);
    Assert.assertEquals(FOLDER_ID, folderId);
  }

  @Test
  public void assertNull_whenFolderNotFound() throws UseCaseException
  {
    Mockito.when(lmsServiceRegistry.getLmsFileSystemService()).thenReturn(lmsFileSystemService);
    String folderId = getSCORMFolderId.execute(FOLDER_ID);
    Assert.assertNull(folderId);
  }
}