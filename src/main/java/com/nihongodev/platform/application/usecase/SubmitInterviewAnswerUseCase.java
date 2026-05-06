package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.SubmitInterviewAnswerCommand;
import com.nihongodev.platform.application.dto.InterviewAnswerResultDto;
import com.nihongodev.platform.application.dto.InterviewFeedbackDto;
import com.nihongodev.platform.application.port.in.SubmitInterviewAnswerPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.InterviewAnswerRepositoryPort;
import com.nihongodev.platform.application.port.out.InterviewQuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.InterviewSessionRepositoryPort;
import com.nihongodev.platform.application.service.AnswerEvaluator;
import com.nihongodev.platform.application.service.AnswerEvaluatorFactory;
import com.nihongodev.platform.domain.event.InterviewAnswerEvaluatedEvent;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.InterviewAnswer;
import com.nihongodev.platform.domain.model.InterviewFeedback;
import com.nihongodev.platform.domain.model.InterviewQuestion;
import com.nihongodev.platform.domain.model.InterviewSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubmitInterviewAnswerUseCase implements SubmitInterviewAnswerPort {

    private final InterviewSessionRepositoryPort sessionRepository;
    private final InterviewQuestionRepositoryPort questionRepository;
    private final InterviewAnswerRepositoryPort answerRepository;
    private final EventPublisherPort eventPublisher;

    public SubmitInterviewAnswerUseCase(InterviewSessionRepositoryPort sessionRepository,
                                        InterviewQuestionRepositoryPort questionRepository,
                                        InterviewAnswerRepositoryPort answerRepository,
                                        EventPublisherPort eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public InterviewAnswerResultDto submit(UUID userId, SubmitInterviewAnswerCommand command) {
        InterviewSession session = sessionRepository.findById(command.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException("InterviewSession", "id", command.sessionId()));

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized: this session belongs to another user");
        }

        if (!session.isInProgress()) {
            throw new BusinessException("Interview session is no longer in progress");
        }

        InterviewQuestion question = questionRepository.findById(command.questionId())
                .orElseThrow(() -> new ResourceNotFoundException("InterviewQuestion", "id", command.questionId()));

        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(session.getInterviewType());
        InterviewFeedback feedback = evaluator.evaluate(question, command.answerText(), command.timeSpentSeconds());

        InterviewAnswer answer = InterviewAnswer.submit(
                command.sessionId(), command.questionId(),
                command.answerText(), command.timeSpentSeconds()
        );
        answer.applyScores(
                feedback.getLanguageScore(), feedback.getTechnicalScore(),
                feedback.getCommunicationScore(), feedback.getCulturalScore()
        );

        InterviewAnswer savedAnswer = answerRepository.save(answer);
        feedback.setAnswerId(savedAnswer.getId());

        session.advanceQuestion();
        session.addScore(
                feedback.getLanguageScore(), feedback.getTechnicalScore(),
                feedback.getCommunicationScore(), feedback.getCulturalScore()
        );
        session.addTimeSpent(command.timeSpentSeconds());
        sessionRepository.save(session);

        eventPublisher.publish("interview-events", InterviewAnswerEvaluatedEvent.of(
                userId, session.getId(), command.questionId(),
                feedback.getOverallScore(), session.getCurrentPhase().name()
        ));

        boolean sessionComplete = !session.hasMoreQuestions();

        InterviewFeedbackDto feedbackDto = new InterviewFeedbackDto(
                savedAnswer.getId(), feedback.getOverallScore(),
                feedback.getLanguageScore(), feedback.getTechnicalScore(),
                feedback.getCommunicationScore(), feedback.getCulturalScore(),
                feedback.getStrengths(), feedback.getImprovements(),
                feedback.getModelAnswer(), feedback.getGrammarNotes(),
                feedback.getVocabularySuggestions()
        );

        return new InterviewAnswerResultDto(
                savedAnswer.getId(), command.questionId(),
                feedback.getOverallScore(), feedbackDto, sessionComplete
        );
    }
}
