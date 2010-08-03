package andy.test.network;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import android.test.ApplicationTestCase;
import andy.commons.network.ApplicationTaggedHttpMessageBuilder;
import andy.sample.SampleApplication;

public class TestApplicationTaggedHttpMessageBuilder extends ApplicationTestCase<SampleApplication> {

	public TestApplicationTaggedHttpMessageBuilder() {
		super(SampleApplication.class);
	}

	private ApplicationTaggedHttpMessageBuilder messageBuilder;
	
	public void setUp() throws Exception {
		super.setUp();
		
		messageBuilder = new ApplicationTaggedHttpMessageBuilder(getContext());
	}

	public void testGetRequest() {
		compareRequest(HttpGet.class, messageBuilder.createGetRequest());
	}
	
	public void testPostRequest() {
		compareRequest(HttpPost.class, messageBuilder.createPostRequest());
	}
	
	public void testPutRequest() {
		compareRequest(HttpPut.class, messageBuilder.createPutRequest());
	}
	
	public void testDeleteRequest() {
		compareRequest(HttpDelete.class, messageBuilder.createDeleteRequest());
	}
	
	private void compareRequest(Class<?> requestClass, HttpMessage request) {
		assertEquals(requestClass, request.getClass());
		
		Header[] headers = request.getAllHeaders();
		for(Header header : headers) {
			System.out.println(header.getName() + " " + header.getValue());
		}
	}
}
