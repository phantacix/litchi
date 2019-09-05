//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dbqueue;

import java.util.Collection;

import litchi.core.components.Component;
import litchi.core.components.Component;
import litchi.core.jdbc.table.Table;

/**
 * 入库队列接口
 * @author 0x737263
 *
 */
public interface DBQueue extends Component {
	
	/**
	 * 数据更新队列
	 * @param table
	 */
	void updateQueue(Table<?>... table);
	
	/**
	 * 数据更新队列
	 * @param tables
	 */
	void updateQueue(Collection<Table<?>> tables);
	
	/**
	 * 获取任务队列任务数
	 * @return
	 */
	int getTaskSize();
	
	/**
	 * 获取table实体数量
	 * @return
	 */
	int getTableSize();

	/**
	 * 修改Table提交数量
	 * @param newSubmitNum
	 */
	void changeSubmitNum(int newSubmitNum);

	/**
	 * 修改Table提交频率
	 * @param newSubmitFrequency
	 */
	void changeSubmitFrequency(int newSubmitFrequency); 

	/**
	 * 实体是否存在队列中
	 * @param table
	 * @return
	 */
	boolean inQueue(Table<?> table);
	
}
