package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Car {

  @ApiModelProperty(value = "is car for private use", required = true, example = "true")
  private boolean isPrivateUse;
  @ApiModelProperty(value = "car registration number", required = true, example = "WWW 4642")
  private String carRegistrationNumber;
}
