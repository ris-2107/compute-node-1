package com.compute_process.middleware.service;

import com.compute_process.middleware.dtos.ComputeRequest;
import com.compute_process.middleware.model.TaskResult;
import com.compute_process.middleware.repository.TaskResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskProcessorService {

  private static final Logger log = LoggerFactory.getLogger(TaskProcessorService.class);
  private final TaskResultRepository taskResultRepository;
  private final String QUEUE_NAME = "taskQueue_TYPE_2";
  private boolean isProcessing = false;

  @Autowired private RabbitTemplate rabbitTemplate;
  @Autowired private ObjectMapper objectMapper;

  // If there are multiple compute services running, they can listen to different queues each
  @RabbitListener(queues = QUEUE_NAME)
  public synchronized void receiveMessage(String request) throws JsonProcessingException {
    log.info("received a Message !");
    if (isProcessing) {
      // Requeue it back
      rabbitTemplate.convertAndSend(QUEUE_NAME, request);
      return;
    }
    isProcessing = true;

    try {
      ComputeRequest computeRequest = objectMapper.readValue(request, ComputeRequest.class);
      createInitialTaskResult(computeRequest);
      TaskResult finalResult = processRequest(computeRequest);
      updateTaskResult(finalResult);

    } finally {
      isProcessing = false;
    }
  }

  private TaskResult createInitialTaskResult(ComputeRequest request) {
    log.info("Creating Initial TaskResult");
    String requestId = request.getComputeType() + "_" + request.getUserId();
    TaskResult result = new TaskResult();
    result.setRequestId(requestId);
    result.setStatus("IN_PROGRESS");

    return taskResultRepository.save(result);
  }

  private TaskResult processRequest(ComputeRequest request) {
    String response = simulateProcessing(request);

    String requestId = request.getComputeType() + "_" + request.getUserId();
    TaskResult result = new TaskResult();
    result.setRequestId(requestId);
    result.setStatus("COMPLETED");
    result.setResultData(response);

    return result;
  }

  private String simulateProcessing(ComputeRequest computeRequest) {
    try {
      Thread.sleep(180_000);
      return computeRequest.getData().toString();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error(
          "Processing failed for userId : {}, and computeType : {}",
          computeRequest.getUserId(),
          computeRequest.getComputeType());
      return null;
    }
  }

  private void updateTaskResult(TaskResult result) {
    log.info("Processing Done, saving to DB");
    taskResultRepository.save(result);
  }
}
