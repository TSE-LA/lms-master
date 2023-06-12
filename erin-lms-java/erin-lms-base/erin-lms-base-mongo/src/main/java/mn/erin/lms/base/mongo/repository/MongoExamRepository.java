package mn.erin.lms.base.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.base.mongo.document.exam.MongoExam;

/**
 * @author Galsan Bayart.
 */
public interface MongoExamRepository extends MongoRepository<MongoExam, String>
{
    List<MongoExam> findAllByExamCategoryId(String categoryId);

    List<MongoExam> findAllByExamGroupId(String groupId);

    List<MongoExam> findAllByExamCategoryIdAndExamGroupId(String categoryId, String groupId);
}
