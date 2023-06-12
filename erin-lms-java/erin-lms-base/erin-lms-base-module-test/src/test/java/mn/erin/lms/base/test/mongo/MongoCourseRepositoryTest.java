package mn.erin.lms.base.test.mongo;

import java.time.LocalDateTime;
import javax.inject.Inject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.AuthorIdProvider;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.mongo.implementation.CourseRepositoryImpl;
import mn.erin.lms.base.mongo.repository.MongoCourseRepository;
import mn.erin.lms.base.test.DummyCourseType;

import static org.junit.Assert.assertNotNull;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestBeanConfig.class })
public class MongoCourseRepositoryTest
{
  @Inject
  private MongoCourseRepository mongoCourseRepository;

  @Inject
  private AuthorIdProvider authorIdProvider;

  @Inject
  private CourseTypeResolver courseTypeResolver;

  private CourseRepositoryImpl courseRepository;

  MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient(), "Test");

  private final MongoCollection<Document> collection = mongoTemplate.createCollection("test");

  @Before
  public void before()
  {
    mongoCourseRepository.deleteAll();
    this.courseRepository = new CourseRepositoryImpl(collection, mongoCourseRepository);
    courseRepository.setAuthorIdProvider(authorIdProvider);
    courseRepository.setCourseTypeResolver(courseTypeResolver);
  }

  @Test
  public void createCourse()
  {
    CourseCategoryId courseCategoryId = CourseCategoryId.valueOf("category");
    CourseDetail courseDetail = new CourseDetail("Hello, World");
    String courseCategoryName = "Category1";
    DateInfo dateInfo = new DateInfo();
    dateInfo.setModifiedDate(LocalDateTime.now());
    dateInfo.setCreatedDate(LocalDateTime.now());
    courseDetail.setDateInfo(dateInfo);

    CourseDepartmentRelation relation = new CourseDepartmentRelation(DepartmentId.valueOf("department"));

    Course course = courseRepository.create(courseCategoryId, courseCategoryName, courseDetail, relation, new DummyCourseType(), "", "");
    assertNotNull(course);
  }
}
