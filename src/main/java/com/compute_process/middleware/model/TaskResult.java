package com.compute_process.middleware.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Accessors(chain = true)
public class TaskResult {

  @Id private String requestId;
  private String status;
  private String resultData;
}
