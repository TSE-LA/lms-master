package mn.erin.lms.legacy.infrastructure.lms.repository.service;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.service.CourseService;

import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;

/**
 * author Naranbaatar Avir.
 */
public class MongoCourseService implements CourseService
{
  private static final String EMBEDDED_FIELD_DESCRIPTION = "courseDetail.description";
  private static final String EMBEDDED_FIELD_TITLE = "courseDetail.title";

  private static final String COURSES_COLLECTION_NAME = "Courses";
  private static final String FIELD_ID = "_id";

  private CourseRepository courseRepository;
  private final MongoCollection<Document> courseCollection;

  private static final String ERROR_MSG_BLANK = "Search text cannot be blank!";

  @Inject
  public void setCourseRepository(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  public MongoCourseService(MongoTemplate mongoTemplate)
  {
    this.courseCollection = mongoTemplate.getCollection(COURSES_COLLECTION_NAME);
  }

  @Override
  public List<Course> search(String text, boolean searchByName, boolean searchByDescription)
      throws LMSRepositoryException
  {
    Validate.notBlank(text, ERROR_MSG_BLANK);
    List<Course> courseList = new ArrayList<>();

    Bson filter = getFilter(text, searchByName, searchByDescription);

    FindIterable<Document> iterable = (courseCollection.find(filter));

    if (iterable != null)
    {
      MongoCursor<Document> cursor = iterable.iterator();

      while (cursor.hasNext())
      {
        Document course = cursor.next();
        CourseId courseId = new CourseId(course.get(FIELD_ID).toString());
        courseList.add(courseRepository.getCourse(courseId));
      }

      cursor.close();
    }

    return courseList;
  }

  private Bson getFilter(String text, boolean searchByName, boolean searchByDescription)
  {
    if (searchByName && !searchByDescription)
    {
      return regex(EMBEDDED_FIELD_TITLE, text, "/i");
    }
    else if (searchByDescription && !searchByName)
    {
      return regex(EMBEDDED_FIELD_DESCRIPTION, text, "/i");
    }
    else
    {
      return or(regex(EMBEDDED_FIELD_TITLE, text, "/i"),
          regex(EMBEDDED_FIELD_DESCRIPTION, text, "/i"));
    }
  }

  private Bson getSearchByNameFilter(String text, boolean searchByName)
  {
    return searchByName ? regex(EMBEDDED_FIELD_TITLE, text, "/i") : null;
  }

  private Bson getSearchByDescriptionFilter(String text, boolean searchByDescripton)
  {
    return searchByDescripton ? regex(EMBEDDED_FIELD_DESCRIPTION, text, "/i") : null;
  }
}

