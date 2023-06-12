package mn.erin.lms.base.domain.service.impl;

import java.util.UUID;

import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.QuestionType;
import mn.erin.lms.base.domain.model.assessment.Questionnaire;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.service.QuestionnaireService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionQuestionnaireService implements QuestionnaireService
{
  @Override
  public Questionnaire newInstance()
  {
    QuizId quizId = QuizId.valueOf(UUID.randomUUID().toString());
    Questionnaire questionnaire = new Questionnaire(quizId, "Асуулга");
    Question question = new Question("Танд сургалттай холбоотой асууж тодруулах зүйл байвал доорх нүдэнд бичээд ИЛГЭЭХ товчийг дарна уу.",
        QuestionType.FILL_IN_BLANK, false);
    questionnaire.addQuestion(question);
    return questionnaire;
  }
}
