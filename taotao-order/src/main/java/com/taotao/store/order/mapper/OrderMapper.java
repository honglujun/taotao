package com.taotao.store.order.mapper;

import java.util.Date;





import org.apache.ibatis.annotations.Param;

import com.taotao.store.order.pojo.Order;
import com.taotao.store.order.pojo.OrderItem;

public interface OrderMapper extends IMapper<Order>{
	
	public void paymentOrderScan(@Param("date") Date date);

        public OrderItem queryByUserIdAndItemId(@Param("order") String orderId, @Param("itemId") String itemId);


}
