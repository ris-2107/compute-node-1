package com.compute_process.middleware.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class ComputeRequest {
  private String computeType;
  private String userId;
  private Map<String, Object> data;
}
