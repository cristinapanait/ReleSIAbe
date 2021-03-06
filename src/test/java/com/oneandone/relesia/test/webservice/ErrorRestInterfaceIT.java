/*
 * 
 * Copyright (c) 2016 1&1 Internet SE.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 *        
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.oneandone.relesia.test.webservice;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.oneandone.relesia.database.model.entity.ErrorEntity;

import java.io.IOException;

/**
 * Created by octavian on 09.09.2016.
 *
 */
public class ErrorRestInterfaceIT {
	private final String RESOURCE_PATH = "errors";
	private final String NEW_OBJECT_JSON = "{\n"
			+ "  \"errorCode\": 122,\n"
			+ "  \"description\": \"some random description\",\n"
			+ "  \"errorType\" : \"COMPILE\",\n"
			+ "  \"severity\" : \"BLOCKING\"\n"
			+ "}";

	private String getFullResourcePath() {
		return RestTestUtil.getBaseURL() + "/" + RESOURCE_PATH;
	}

	@Test
	public void requestNewObjectCreation_thenCreationIsSuccesfull() throws ClientProtocolException, IOException {
		// Given
		HttpPost postRequest = new HttpPost(getFullResourcePath());

		postRequest.addHeader("Content-Type", "application/json");
		postRequest.addHeader("Accept", "application/json");
		postRequest.setEntity(new StringEntity(NEW_OBJECT_JSON));

		// When
		HttpResponse response = HttpClientBuilder.create().build().execute(postRequest);

		// Then
		ErrorEntity resource = RestTestUtil.retrieveResourceFromResponse(response,
				ErrorEntity.class);

		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_CREATED);
		Assert.assertNotNull(resource);
		Assert.assertNotNull(resource.getId());
		RestTestUtil.saveWorkId("" + resource.getId());
	}

	@Test(dependsOnMethods = { "requestNewObjectCreation_thenCreationIsSuccesfull" })
	public void requestWithNoAcceptHeader_thenDefaultResponseContentTypeIsJson()
			throws ClientProtocolException, IOException {
		// Given
		String jsonMimeType = "application/json";
		HttpUriRequest request = new HttpGet(getFullResourcePath() + "/" + RestTestUtil.getWorkId());

		// When
		HttpResponse response = HttpClientBuilder.create().build().execute(request);

		// Then
		String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
		Assert.assertEquals(mimeType, jsonMimeType);
	}

	@Test(dependsOnMethods = { "requestNewObjectCreation_thenCreationIsSuccesfull" })
	public void requestExistingId_thenRetrievedResourceIsCorrect() throws ClientProtocolException, IOException {
		// Given
		HttpUriRequest request = new HttpGet(getFullResourcePath() + "/" + RestTestUtil.getWorkId());

		// When
		HttpResponse response = HttpClientBuilder.create().build().execute(request);

		// Then
		ErrorEntity resource = RestTestUtil.retrieveResourceFromResponse(response,
				ErrorEntity.class);
		Assert.assertNotNull(resource);
		Assert.assertEquals("" + resource.getId(), RestTestUtil.getWorkId());
	}

	@Test(dependsOnMethods = { "requestNewObjectCreation_thenCreationIsSuccesfull" })
	public void requestObjectUpdate_thenUpdateIsSuccesfull() throws ClientProtocolException, IOException {
		// Get existing object from DB
		HttpUriRequest getRequest = new HttpGet(getFullResourcePath() + "/" + RestTestUtil.getWorkId());

		HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);

		ErrorEntity resource = RestTestUtil.retrieveResourceFromResponse(response,
				ErrorEntity.class);

		Assert.assertNotNull(resource);
		String newName = resource.getDescription() + "_UPDATED_DURING_TEST";
		resource.setDescription(newName);

		// Update with new value
		HttpPut putRequest = new HttpPut(getFullResourcePath());
		putRequest.addHeader("Content-Type", "application/json");
		putRequest.addHeader("Accept", "application/json");
		putRequest.setEntity(new StringEntity(RestTestUtil.getJSONFormat(resource)));
		response = HttpClientBuilder.create().build().execute(putRequest);

		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);

		// Get again the object and check updated value
		response = HttpClientBuilder.create().build().execute(getRequest);

		resource = RestTestUtil.retrieveResourceFromResponse(response, ErrorEntity.class);
		Assert.assertNotNull(resource);
		Assert.assertEquals(resource.getDescription(), newName);
	}

	@Test(dependsOnMethods = { "requestObjectUpdate_thenUpdateIsSuccesfull" })
	public void requestExistingObjectDelete_thenResponseIsOK() throws ClientProtocolException, IOException {
		// Given
		HttpUriRequest request = new HttpDelete(getFullResourcePath() + "/" + RestTestUtil.getWorkId());

		// When
		HttpResponse response = HttpClientBuilder.create().build().execute(request);

		// Then
		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_NO_CONTENT);
	}

	@Test(dependsOnMethods = { "requestExistingObjectDelete_thenResponseIsOK" })
	public void requestInexistantID_then404IsReceived() throws ClientProtocolException, IOException {
		System.out.println("connecting to : " + getFullResourcePath() + "/" + RestTestUtil.getWorkId());
		// Given
		HttpUriRequest request = new HttpGet(getFullResourcePath() + "/" + RestTestUtil.getWorkId());

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
	}
}
