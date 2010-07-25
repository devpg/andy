package andy.commons.network;


import org.apache.http.HttpMessage;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestApplicationTaggedHttpMessageBuilder {

	private ApplicationTaggedHttpMessageBuilder messageBuilder;
	
	@Before
	public void setUp() throws Exception {
		messageBuilder = new ApplicationTaggedHttpMessageBuilder(null);
	}

	@Test
	public void testGetRequest() {
		testRequestMethod(HttpGet.class, messageBuilder.createGetRequest());
	}
	
	public void testPostRequest() {
		testRequestMethod(HttpPost.class, messageBuilder.createPostRequest());
	}
	
	public void testPutRequest() {
		testRequestMethod(HttpPut.class, messageBuilder.createPutRequest());
	}
	
	public void testDeleteRequest() {
		testRequestMethod(HttpDelete.class, messageBuilder.createGetRequest());
	}
	
	private void testRequestMethod(Class<?> requestClass, HttpMessage request) {
		Assert.assertEquals(requestClass, request.getClass());
	}
}
