package com.compute_process.middleware.configuraton;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ComputeNode {

  private String type;
  private String url;
  private String queueName;
}
