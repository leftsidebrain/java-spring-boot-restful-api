package JavaProgrammerZamanNow.Restfulapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAddressRequest {

	@JsonIgnore
	@NotBlank
	private String contactId;

	@JsonIgnore
	@NotBlank
	private String addressId;

	@Size(max = 200)
	private String street;

	@NotBlank
	@Size(max = 100)
	private String country;

	@Size(max = 200)
	private String city;

	@Size(max = 200)
	private String province;

	@Size(max = 200)
	private String postalCode;
}
