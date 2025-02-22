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

import static com.adobe.aio.event.management.model.ProviderInputModel.DELIVERY_FORMAT_ADOBE_IO;

import com.adobe.aio.event.management.feign.ConflictException;
import com.adobe.aio.event.management.model.EventMetadata;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.util.WorkspaceUtil;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class ProviderServiceIntegrationTest extends ProviderServiceTester {

  public static final String TEST_EVENT_CODE = "com.adobe.aio.event.management.test.event";
  public static final String TEST_EVENT_PROVIDER_LABEL = "com.adobe.aio.event.management.test";

  public ProviderServiceIntegrationTest() {
    super();
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingWorkspace() {
    ProviderService.builder().build();
  }

  @Test
  public void getProvidersWithInvalidConsumerOrgId() {
    ProviderService providerService = ProviderService.builder()
        .workspace(WorkspaceUtil.getSystemWorkspaceBuilder()
            .consumerOrgId("invalid").build())
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
    Assert.assertTrue(providerService.getProviders().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidFindByArg() {
    providerService.findProviderBy("", "");
  }

  @Test
  public void getNotFound() {
    String idNotToBeFound = "this_id_should_not_exist";
    Assert.assertFalse(providerService.findProviderById(idNotToBeFound).isPresent());
    Assert.assertTrue(providerService.getEventMetadata(idNotToBeFound).isEmpty());
    Assert.assertFalse(
        providerService.findCustomEventsProviderByInstanceId(idNotToBeFound).isPresent());
  }

  @Test
  public void createGetUpdateDelete() {
    Provider provider = createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL, TEST_EVENT_CODE);
    String providerId = provider.getId();
    try {
      String instanceId = provider.getInstanceId();

      Assert.assertEquals(1, providerService.getEventMetadata(providerId).size());
      logger.info("Fetched All EventMetadata `{}` of AIO Events Provider `{}`", providerId);

      String updatedEventMetadataDescription = "updated EventMetadata Description";
      Optional<EventMetadata> eventMetadata = providerService.updateEventMetadata(providerId,
          getTestEventMetadataBuilder(TEST_EVENT_CODE).description(updatedEventMetadataDescription)
              .build());
      Assert.assertTrue(eventMetadata.isPresent());
      logger.info("Updated EventMetadata `{}` of AIO Events Provider `{}`", eventMetadata,
          providerId);
      Assert.assertEquals(updatedEventMetadataDescription, eventMetadata.get().getDescription());

      Optional<EventMetadata> eventMetadataFromGet = providerService.getEventMetadata(providerId,
          TEST_EVENT_CODE);
      Assert.assertTrue(eventMetadataFromGet.isPresent());
      Assert.assertEquals(eventMetadata, eventMetadataFromGet);
      logger.info("Fetched EventMetadata `{}` of AIO Events Provider `{}`", eventMetadataFromGet,
          providerId);

      Optional<Provider> providerById = providerService.findProviderById(providerId);
      Assert.assertTrue(providerById.isPresent());
      List<EventMetadata> eventMetadataList = providerService.getEventMetadata(providerId);
      Assert.assertTrue(eventMetadataList.size() == 1);
      Assert.assertEquals(eventMetadata.get(), eventMetadataList.get(0));
      logger.info("Found AIO Events Provider `{}` by Id", providerById);

      Optional<Provider> providerByInstanceId = providerService.findCustomEventsProviderByInstanceId(
          instanceId);
      Assert.assertTrue(providerByInstanceId.isPresent());
      Assert.assertEquals(providerId, providerByInstanceId.get().getId());
      logger.info("Found AIO Events Provider `{}` by InstanceId", providerById);

      providerService.deleteEventMetadata(providerId, TEST_EVENT_CODE);
      Assert.assertFalse(providerService.getEventMetadata(providerId, TEST_EVENT_CODE).isPresent());
      Assert.assertTrue(providerService.getEventMetadata(providerId).isEmpty());
      logger.info("Deleted EventMetadata {} from AIO Events Provider `{}`", TEST_EVENT_CODE,
          providerById);

      try {
        providerService.createProvider(
            getTestProviderInputModelBuilder(TEST_EVENT_PROVIDER_LABEL).instanceId(instanceId)
                .build());
        Assert.fail("We should have had a ConflictException thrown");
      } catch (ConflictException ex) {
        logger.info("Cannot create an AIO Events provider with the same instanceId: {}",
            ex.getMessage());
      }

      String updatedProviderDescription = "updated Provider Description";
      Optional<Provider> updatedProvider = providerService.updateProvider(providerId,
          getTestProviderInputModelBuilder(TEST_EVENT_PROVIDER_LABEL)
              .instanceId(instanceId)
              .description(updatedProviderDescription)
              .eventDeliveryFormat(DELIVERY_FORMAT_ADOBE_IO)
              .build());
      Assert.assertTrue(updatedProvider.isPresent());
      logger.info("Updated AIO Events Provider: {}", provider);
      Assert.assertEquals(providerId, updatedProvider.get().getId());
      Assert.assertEquals(updatedProviderDescription, updatedProvider.get().getDescription());
      Assert.assertEquals(DELIVERY_FORMAT_ADOBE_IO, updatedProvider.get().getEventDeliveryFormat());

      providerService.createEventMetadata(providerId,
          getTestEventMetadataBuilder(TEST_EVENT_CODE).build());
      Assert.assertTrue(eventMetadata.isPresent());
      logger.info("Added EventMetadata `{}` to AIO Events Provider `{}`", eventMetadata,
          providerId);
      providerService.deleteAllEventMetadata(providerId);
      Assert.assertTrue(providerService.getEventMetadata(providerId).isEmpty());
      logger.info("Deleted All EventMetadata from AIO Events Provider `{}`", providerId);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      Assert.fail(e.getMessage());
    } finally {
      deleteProvider(providerId);
    }
  }

}
