/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.mongo;

import java.util.Date;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.model.assessment.Question;
import mn.erin.lms.legacy.domain.lms.model.assessment.QuestionType;
import mn.erin.lms.legacy.domain.lms.model.assessment.Test;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseQuestionnaireRepository;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class MongoCourseQuestionnaireRepository implements CourseQuestionnaireRepository
{

  public MongoCourseQuestionnaireRepository(MongoTemplate mongoTemplate)
  {
    Objects.requireNonNull(mongoTemplate, "Mongo template cannot be null!");
  }

  @Override
  public Test get(CourseId courseId)
  {
    // TODO implement real mongo save and get questionnaire repository
    TestId id = new TestId(new ObjectId(new Date()).toHexString());
    Test questionnaire = new Test(id, "АСУУЛГА");
    Question question = new Question("Танд урамшуулалтай холбоотой асууж тодруулах зүйл байвал доорх нүдэнд бичээд ИЛГЭЭХ товчийг дарна уу.",
        QuestionType.FILL_IN_BLANK);
    questionnaire.addQuestion(question);
    return questionnaire;
  }
}
