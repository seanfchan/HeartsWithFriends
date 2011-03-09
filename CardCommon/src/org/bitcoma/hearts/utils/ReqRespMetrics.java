package org.bitcoma.hearts.utils;

public class ReqRespMetrics {

	private Data data = new Data();

	
	public synchronized void updateMetrics(long respTime) {
		data.totalRespTime += respTime;
		data.totalNumReqResps++;
	}
	
	
	public synchronized Data getAndResetInfo() {
		Data ret = data;
		
		data = new Data();
		
		return ret;
	}

	public class Data {
		public long totalRespTime = 0;
		public long totalNumReqResps = 0;
	}
	
}
