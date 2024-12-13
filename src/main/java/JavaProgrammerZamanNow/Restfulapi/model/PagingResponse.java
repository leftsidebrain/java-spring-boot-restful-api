package JavaProgrammerZamanNow.Restfulapi.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {

	private Integer currentPage;
	private Integer size;
	private Integer totalPage;

}
