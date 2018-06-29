package com.topaiebiz.merchant.enter.dto;

import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class StateDto implements Serializable {
	
	/**总状态。*/
	private Integer State;

	private List<MerchantauditLogDto> logList;

	private List<BackendCategorysDTO> backendCategorysDTOList;
}
