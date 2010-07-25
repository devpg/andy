package andy.commons.network;

import org.apache.http.HttpMessage;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 * The <code>ApplicationTaggedHttpMessageBuilder</code> class annotates
 * implementations of <code>HttpMessage</code> and <code>HttpClient</code>
 * (Apache Commons HttpComponents) with an application specific user agent.
 * 
 * Supported HTTP request methods are GET, POST, PUT and DELETE.
 * 
 * Samples: <code>
 * 			// Execute a tagged GET-request
 * 			ApplicationTaggedHttpMessageBuilder messageBuilder = new ApplicationTaggedHttpMessageBuilder(context);
 * 			HttpGet request = messageBuilder.createGetRequest();
 * 			request.setURI(new URI("http://www.devpg.com"));
 * 			HttpResponse response = new DefaultHttpClient().execute(request);
 * 
 * 			// Execute a GET-request with a tagged HTTP client
 * 			ApplicationTaggedHttpMessageBuilder messageBuilder = new ApplicationTaggedHttpMessageBuilder(context);
 * 			HttpGet request = new HttpGet("http://www.devpg.com");
 * 			HttpClient client= messageBuilder.createHttpClient();
 * 			HttpResponse response = client.execute(request);
 * </code>
 * 
 * @author devpg
 * @version 1.0
 * 
 */

public class ApplicationTaggedHttpMessageBuilder {

	protected final String userAgent;

	public ApplicationTaggedHttpMessageBuilder(Context context) throws NameNotFoundException {
		if(context == null) {
			throw new IllegalArgumentException();
		}
		
		final StringBuilder builder = new StringBuilder();
		final ApplicationInfo appInfo = context.getApplicationInfo();
		final PackageManager packageManager = context.getPackageManager();

		// label and version of the application (e.g. "MyApp/1.0")
		builder.append(packageManager.getApplicationLabel(appInfo).toString()).append("/");
		builder.append(packageManager.getPackageInfo(context.getPackageName(), 0).versionCode);

		// release and API-level of the underlying platform
		builder.append(" (").append(Build.VERSION.CODENAME).append("; ");
		builder.append(Build.VERSION.SDK_INT).append(")");

		userAgent = builder.toString();
	}

	/**
	 * Creates a tagged GET-request
	 * 
	 * @see org.apache.http.client.methods.HttpGet
	 * @return GET-request with application specific user agent
	 */
	public HttpGet createGetRequest() {
		final HttpGet request = new HttpGet();
		setUserAgent(request);
		return request;
	}

	/**
	 * Creates a tagged POST-request
	 * 
	 * @see org.apache.http.client.methods.HttpPost
	 * @return POST-request with application specific user agent
	 */
	public HttpPost createPostRequest() {
		final HttpPost request = new HttpPost();
		setUserAgent(request);
		return request;
	}

	/**
	 * Creates a tagged PUT-request
	 * 
	 * @see org.apache.http.client.methods.HttpPut
	 * @return PUT-request with application specific user agent
	 */
	public HttpPut createPutRequest() {
		final HttpPut request = new HttpPut();
		setUserAgent(request);
		return request;
	}

	/**
	 * Creates a tagged DELETE-request
	 * 
	 * @see org.apache.http.client.methods.HttpDelete
	 * @return DELETE-request with application specific user agent
	 */
	public HttpDelete createDeleteRequest() {
		final HttpDelete request = new HttpDelete();
		setUserAgent(request);
		return request;
	}

	/**
	 * Creates a tagged HTTP client
	 * 
	 * @see org.apache.http.client.HttpClient
	 * @see org.apache.http.impl.client.DefaultHttpClient
	 * @return Default implementation of a HTTP client with application specific
	 *         user agent
	 */
	public HttpClient createHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpProtocolParams.setUserAgent(client.getParams(), userAgent);
		return client;
	}

	private void setUserAgent(HttpMessage message) {
		message.setHeader("User-Agent", userAgent);
	}
}
