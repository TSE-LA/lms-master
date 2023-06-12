package mn.erin.lms.base.domain.usecase.assessment.dto;

import org.apache.commons.lang3.Validate;

public class CloneAssessmentInput
{
    private final String assessmentId;
    private final CreateAssessmentInput createAssessmentInput;

    public CloneAssessmentInput(String assessmentId, CreateAssessmentInput createAssessmentInput)
    {
        this.assessmentId = Validate.notBlank(assessmentId);
        this.createAssessmentInput = Validate.notNull(createAssessmentInput);
    }

    public String getAssessmentId() {return assessmentId;}

    public CreateAssessmentInput getCreateAssessmentInput() { return createAssessmentInput; }
}
