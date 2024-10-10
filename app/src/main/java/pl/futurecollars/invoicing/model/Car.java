package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Car {

  @Id
  @JsonIgnore
  @ApiModelProperty(value = "Car id (genearated by applicaiotn)")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ApiModelProperty(value = "is car for private use", required = true, example = "true")
  private boolean isPrivateUse;
  @ApiModelProperty(value = "car registration number", required = true, example = "WWW 4642")
  private String carRegistrationNumber;
}
