/*
 * Copyright 2017 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.aio.event.management;

import com.adobe.aio.event.management.model.EventsOfInterest;
import com.adobe.aio.event.management.model.EventsOfInterestInputModel;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.RegistrationCreateModel;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationServiceTester {

  public static final String TEST_DESCRIPTION = "Test description";
  public static final String DELIVERY_TYPE_JOURNAL = "journal";
  protected final Logger logger = LoggerFactory.getLogger(this.getClass());
  protected final Workspace workspace;
  protected final RegistrationService registrationService;

  public RegistrationServiceTester() {
    workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
    registrationService = RegistrationService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
  }

  public RegistrationService getRegistrationService(){
    return this.registrationService;
  }

  public static EventsOfInterestInputModel.Builder getTestEventsOfInterestBuilder(String providerId, String eventCode) {
    return EventsOfInterestInputModel.builder()
                    .eventCode(eventCode)
                    .providerId(providerId);
  }

  public Registration createJournalRegistration(String registrationName,
      String providerId, String eventCode){
    return createRegistration(RegistrationCreateModel.builder()
          .name(registrationName)
          .description(TEST_DESCRIPTION)
          .deliveryType(DELIVERY_TYPE_JOURNAL)
          .addEventsOfInterests(getTestEventsOfInterestBuilder(providerId, eventCode).build()));
  }

    public Registration createRegistration(
      RegistrationCreateModel.Builder registrationInputModelBuilder) {
    RegistrationCreateModel registrationInputModel =
        registrationInputModelBuilder.clientId(this.workspace.getApiKey()).build();
    Optional<Registration> registration = registrationService.createRegistration(registrationInputModelBuilder);
    Assert.assertTrue(registration.isPresent());
    Registration registratinCreated = registration.get();
    logger.info("Created AIO Event Registration: {}", registration.get());
    String registrationId = registratinCreated.getRegistrationId();
    Assert.assertNotNull(registrationId);
    Assert.assertEquals(registrationInputModel.getDescription(), registratinCreated.getDescription());
    Assert.assertEquals(registrationInputModel.getName(), registratinCreated.getName());
    Assert.assertEquals(registrationInputModel.getDeliveryType(), registratinCreated.getDeliveryType());

    Set<EventsOfInterest> eventsOfInterestSet = registration.get().getEventsOfInterests();
    Assert.assertEquals(registrationInputModel.getEventsOfInterests().size(),eventsOfInterestSet.size());
    for(EventsOfInterestInputModel eventsOfInterestInput: registrationInputModel.getEventsOfInterests()){
      Assert.assertTrue(eventsOfInterestSet.stream()
                      .anyMatch(eventsOfInterest -> eventsOfInterest.getEventCode()
                                      .equals(eventsOfInterestInput.getEventCode())));
    }

    Assert.assertEquals("verified", registratinCreated.getWebhookStatus());
    Assert.assertEquals(true, registratinCreated.isEnabled());
    Assert.assertNull(registration.get().getWebhookUrl());
    assertUrl(registration.get().getJournalUrl().getHref());
    assertUrl(registration.get().getTraceUrl().getHref());
    Assert.assertNotNull(registration.get().getCreatedDate());
    Assert.assertNotNull(registration.get().getUpdatedDate());
    Assert.assertEquals(registration.get().getUpdatedDate(), registration.get().getCreatedDate());
    return registration.get();
  }

  public void deleteRegistration(String registrationId) {
    registrationService.delete(registrationId);
    Assert.assertFalse(registrationService.findById(registrationId).isPresent());
    logger.info("Deleted AIO Event Registration: {}", registrationId);
  }

  private static void assertUrl(String stringUrl) {
    try {
      Assert.assertNotNull(stringUrl);
      URL url = new URL(stringUrl);
    } catch (MalformedURLException e) {
      Assert.fail("invalid url due to " + e.getMessage());
    }
  }


}
