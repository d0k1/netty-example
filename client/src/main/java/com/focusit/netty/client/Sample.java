package com.focusit.netty.client;

/**
 * Created by Denis V. Kirpichenkov on 30.12.14.
 */
public class Sample {
	public long appId;
	public String data;

	public Sample(long appId, String data) {
		this.appId = appId;
		this.data = data;
	}

	@Override
	public String toString() {
		return "Sample{" +
			"appId=" + appId +
			", data='" + data + '\'' +
			'}';
	}
}
