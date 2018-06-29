package com.topaiebiz.goods.repair.exception;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 15:33 2018/4/4
 * @Modified by:
 */
public class GoodsRuntimeException extends RuntimeException {

		private static final long serialVersionUID = -5121723716435881378L;

		public GoodsRuntimeException() {
			super();
		}

		public GoodsRuntimeException(String message) {
			super(message);
		}

		public GoodsRuntimeException(String message, Throwable throwable) {
			super(message,throwable);
		}

		public GoodsRuntimeException(Throwable throwable) {
			super(throwable);
		}
}
