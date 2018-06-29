package com.topaiebiz.elasticsearch.api;

import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 13:18 2018/6/27
 * @Modified by:
 */
public interface ElasticSearchApi {


	/**
	 *@Author: tangx.w
	 *@Description: 全量同步数据到搜索引擎
	 *@param  * @param null
	 *@Date: 2018/6/27 13:36
	 */
	void syncAllItems();

	/**
	 *@Author: tangx.w
	 *@Description: 同步批量条数据到es
	 *@param  itemIds
	 *@Date: 2018/6/29 14:05
	 */
	void syncItems(List<Long> itemIds);

	/**
	 *@Author: tangx.w
	 *@Description: 同步单条数据到es
	 *@param  itemId
	 *@Date: 2018/6/29 14:05
	 */
	void syncItem(Long itemId);

}
