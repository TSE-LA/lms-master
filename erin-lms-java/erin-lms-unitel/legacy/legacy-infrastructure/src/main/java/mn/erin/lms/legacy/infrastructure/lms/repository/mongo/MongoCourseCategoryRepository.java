/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.model.course.CompanyId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoCourseCategoryRepository implements CourseCategoryRepository
{
  private static final String COLLECTION_NAME = "Course Categories";

  private static final String FIELD_ID = "_id";
  private static final String FIELD_COMPANY_ID = "companyId";
  private static final String FIELD_PARENT_CATEGORY_ID = "parentCategoryId";
  private static final String FIELD_SUB_CATEGORIES = "subCategories";
  private static final String FIELD_CATEGORY_NAME = "categoryName";
  private static final String FIELD_DESCRIPTION = "description";

  private static final String ERR_MSG_COMPANY_ID = "Company ID is required!";
  private static final String ERR_MSG_PARENT_CATEGORY = "Parent category ID is required!";
  private static final String ERR_MSG_CATEGORY_NAME = "Category name is required!";

  private final MongoCollection<Document> courseCategoryCollection;

  public MongoCourseCategoryRepository(MongoTemplate mongoTemplate)
  {
    this.courseCategoryCollection = mongoTemplate.getCollection(COLLECTION_NAME);
  }

  @Override
  public CourseCategory create(CompanyId companyId, CourseCategoryId parentCategoryId, String categoryName, String description)
  {
    Validate.notNull(companyId, ERR_MSG_COMPANY_ID);
    Validate.notNull(parentCategoryId, ERR_MSG_PARENT_CATEGORY);
    Validate.notBlank(categoryName, ERR_MSG_CATEGORY_NAME);

    ObjectId objectId = new ObjectId(new Date());

    Document courseCategoryAsDocument = new Document();

    courseCategoryAsDocument.put(FIELD_ID, objectId);
    courseCategoryAsDocument.put(FIELD_COMPANY_ID, companyId);
    courseCategoryAsDocument.put(FIELD_PARENT_CATEGORY_ID, parentCategoryId);
    courseCategoryAsDocument.put(FIELD_CATEGORY_NAME, categoryName);
    courseCategoryAsDocument.put(FIELD_DESCRIPTION, description);
    courseCategoryAsDocument.put(FIELD_SUB_CATEGORIES, new ArrayList<CourseCategoryId>());

    courseCategoryCollection.insertOne(courseCategoryAsDocument);

    CourseCategoryId courseCategoryId = new CourseCategoryId(objectId.toHexString());
    CourseCategory courseCategory = new CourseCategory(companyId, parentCategoryId, courseCategoryId, categoryName);
    courseCategory.setDescription(description);

    return courseCategory;
  }

  @Override
  public CourseCategory addSubCategory(CompanyId companyId, CourseCategoryId categoryId, CourseCategoryId subCategoryId)
  {
    Validate.notNull(companyId, ERR_MSG_COMPANY_ID);
    Validate.notNull(categoryId, ERR_MSG_PARENT_CATEGORY);
    Validate.notNull(subCategoryId, "Subcategory is required");

    Bson filter = and(eq(FIELD_COMPANY_ID, companyId), eq(FIELD_ID, new ObjectId(categoryId.getId())));

    Document subCategoriesAsDocument = new Document(FIELD_SUB_CATEGORIES, subCategoryId);
    Document update = new Document("$push", subCategoriesAsDocument);

    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
    options.upsert(false);
    options.returnDocument(ReturnDocument.AFTER);

    Document updated = courseCategoryCollection.findOneAndUpdate(filter, update, options);
    return mapToCourseCategory(updated);
  }

  @Override
  public Collection<CourseCategory> listAll(CompanyId companyId)
  {
    Validate.notNull(companyId, ERR_MSG_COMPANY_ID);
    Bson filter = eq(FIELD_COMPANY_ID, companyId);

    return listCourseCategories(filter);
  }

  @Override
  public Collection<CourseCategory> listAll(CourseCategoryId parentCategoryId)
  {
    Validate.notNull(parentCategoryId, ERR_MSG_PARENT_CATEGORY);
    Bson filter = eq(FIELD_PARENT_CATEGORY_ID, parentCategoryId);

    return listCourseCategories(filter);
  }

  @Override
  public Collection<CourseCategory> listAll(CompanyId companyId, CourseCategoryId parentCategoryId)
  {
    Validate.notNull(companyId, ERR_MSG_COMPANY_ID);
    Validate.notNull(parentCategoryId, ERR_MSG_PARENT_CATEGORY);

    Bson filter = and(eq(FIELD_COMPANY_ID, companyId), eq(FIELD_PARENT_CATEGORY_ID, parentCategoryId));

    return listCourseCategories(filter);
  }

  @Override
  public CourseCategory getCourseCategory(CompanyId companyId, CourseCategoryId parentCategoryId, String categoryName) throws LMSRepositoryException
  {
    Validate.notNull(companyId, ERR_MSG_COMPANY_ID);
    Validate.notNull(parentCategoryId, ERR_MSG_PARENT_CATEGORY);
    Validate.notBlank(categoryName, ERR_MSG_CATEGORY_NAME);

    Bson filter = and(eq(FIELD_COMPANY_ID, companyId), eq(FIELD_PARENT_CATEGORY_ID, parentCategoryId),
        eq(FIELD_CATEGORY_NAME, categoryName));

    FindIterable<Document> result = courseCategoryCollection.find(filter);

    if (result == null)
    {
      throw new LMSRepositoryException("The course category [name: " + categoryName +
          ", company ID: " + companyId.getId() + "] was not found!");
    }

    Document document = result.iterator().next();

    return mapToCourseCategory(document);
  }

  @Override
  public CourseCategory getCourseCategory(CourseCategoryId courseCategoryId) throws LMSRepositoryException
  {
    Bson filter = eq(FIELD_ID, new ObjectId(courseCategoryId.getId()));

    FindIterable<Document> result = courseCategoryCollection.find(filter);

    if (result == null)
    {
      throw new LMSRepositoryException("The course category with the ID: [" + courseCategoryId.getId() + "] was not found!");
    }

    Document document = result.iterator().next();

    return mapToCourseCategory(document);
  }

  @Override
  public void delete(CourseCategoryId courseCategoryId)
  {
    Bson filter = eq(FIELD_ID, new ObjectId(courseCategoryId.getId()));
    courseCategoryCollection.findOneAndDelete(filter);
  }

  @Override
  public void delete(CompanyId companyId, CourseCategoryId parentCategoryId, String categoryName)
  {
    Bson filter = and(eq(FIELD_COMPANY_ID, companyId), eq(FIELD_PARENT_CATEGORY_ID, parentCategoryId),
        eq(FIELD_CATEGORY_NAME, categoryName));
    courseCategoryCollection.findOneAndDelete(filter);
  }

  private Collection<CourseCategory> listCourseCategories(Bson filter)
  {
    FindIterable<Document> documents = courseCategoryCollection.find(filter);

    if (documents == null)
    {
      return Collections.emptyList();
    }

    Iterator<Document> iterator = documents.iterator();

    Set<CourseCategory> courseCategories = new HashSet<>();
    while(iterator.hasNext())
    {
      Document document = iterator.next();
      courseCategories.add(mapToCourseCategory(document));
    }

    return courseCategories;
  }

  private CourseCategory mapToCourseCategory(Document document)
  {
    String courseCategoryName = (String) document.get(FIELD_CATEGORY_NAME);
    String description = (String) document.get(FIELD_DESCRIPTION);

    ObjectId objectId = document.get(FIELD_ID, ObjectId.class);
    Document companyIdAsDocument = (Document) document.get(FIELD_COMPANY_ID);
    Document parentCategoryIdAsDocument = (Document) document.get(FIELD_PARENT_CATEGORY_ID);

    CompanyId companyId = new CompanyId((String) companyIdAsDocument.get(FIELD_ID));
    CourseCategoryId parentCategoryId = new CourseCategoryId((String) parentCategoryIdAsDocument.get(FIELD_ID));
    CourseCategoryId courseCategoryId = new CourseCategoryId(objectId.toHexString());

    CourseCategory courseCategory = new CourseCategory(companyId, parentCategoryId, courseCategoryId, courseCategoryName);
    courseCategory.setDescription(description);

    return courseCategory;
  }
}
