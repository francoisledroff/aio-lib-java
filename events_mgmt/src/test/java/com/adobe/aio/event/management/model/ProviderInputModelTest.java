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
package com.adobe.aio.event.management.model;

import static com.adobe.aio.util.Constants.CUSTOM_EVENTS_PROVIDER_METADATA_ID;

import org.junit.Assert;
import org.junit.Test;

public class ProviderInputModelTest {

  @Test(expected = IllegalArgumentException.class)
  public void invalid() {
    ProviderInputModel.builder().build();
  }

  @Test
  public void valid() {
    String label = "com.adobe.aio.event.management.test";
    String description = label + " description";
    ProviderInputModel providerInputModel = ProviderInputModel.builder()
        .label(label)
        .description(description)
        .docsUrl("https://github.com/adobe/aio-lib-java")
        .build();
    Assert.assertEquals(description, providerInputModel.getDescription());
    Assert.assertEquals(label, providerInputModel.getLabel());
    Assert.assertEquals(CUSTOM_EVENTS_PROVIDER_METADATA_ID,
        providerInputModel.getProviderMetadataId());
  }
}
