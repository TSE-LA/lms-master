package mn.erin.lms.base.domain.usecase.assessment;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.dto.AssessmentDto;
import mn.erin.lms.base.domain.usecase.assessment.dto.CloneAssessmentInput;
import mn.erin.lms.base.domain.usecase.assessment.dto.CreateAssessmentInput;

@Authorized(users = {Instructor.class})
public class CloneAssessment extends AssessmentUseCase<CloneAssessmentInput, AssessmentDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloneAssessment.class);

    private final QuizRepository quizRepository;

    public CloneAssessment(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry) {
        super(lmsRepositoryRegistry, lmsServiceRegistry);
        this.assessmentRepository = lmsRepositoryRegistry.getAssessmentRepository();
        this.quizRepository = lmsRepositoryRegistry.getQuizRepository();
    }

    @Override
    protected AssessmentDto executeImpl(CloneAssessmentInput input) throws UseCaseException {
        Validate.notNull(input);
        AssessmentId existingAssessmentId = AssessmentId.valueOf(input.getAssessmentId());
        return clone(existingAssessmentId, input.getCreateAssessmentInput());
    }

    private AssessmentDto clone(AssessmentId existingAssessmentId, CreateAssessmentInput createAssessmentInput) throws UseCaseException {
        Assessment existingAssessment = getAssessment(existingAssessmentId);
        Assessment clonedAssessment;
        Quiz quiz;
        try {
            quiz = quizRepository.fetchById(existingAssessment.getQuizId());
            Quiz clonedQuiz = quizRepository.create(quiz.getQuestions(), quiz.getName(), quiz.isGraded(), quiz.getTimeLimit(), quiz.getDueDate(), quiz.getMaxAttempts(), quiz.getThresholdScore());
            clonedAssessment = assessmentRepository.createAssessment(clonedQuiz.getQuizId(),
                    createAssessmentInput.getName(), existingAssessment.getAuthorId(), createAssessmentInput.getDescription());
            return convert(clonedAssessment, clonedQuiz);
        } catch (LmsRepositoryException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UseCaseException("Failed to clone online course assessment");
        }
    }
}
