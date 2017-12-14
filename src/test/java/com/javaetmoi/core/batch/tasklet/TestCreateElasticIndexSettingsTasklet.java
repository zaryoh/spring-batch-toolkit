/**
 * Copyright 2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.javaetmoi.core.batch.tasklet;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Unit test of the {@link CreateElasticIndexSettingsTasklet}
 * 
 * @author Antoine
 * 
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
@RunWith(com.carrotsearch.randomizedtesting.RandomizedRunner.class)
public class TestCreateElasticIndexSettingsTasklet extends ESIntegTestCase {

    private static final String              INDEX     = "bank";

    private Client                           client;

    private CreateElasticIndexSettingsTasklet tasklet;

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.builder().put(super.nodeSettings(nodeOrdinal)).build();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        client = client();
        tasklet = new CreateElasticIndexSettingsTasklet();
        tasklet.setEsClient(client);
    }

    @After
    public void tearDown() throws Exception {
        client.admin().indices().delete(new DeleteIndexRequest(INDEX)).get();
        super.tearDown();
    }

    @Test
    public void executeTasklet() throws Exception {

        IndicesAdminClient admin = client.admin().indices();
        assertFalse(admin.exists(new IndicesExistsRequest(INDEX)).get().isExists());

        Resource resource = new ClassPathResource(
                "com/javaetmoi/core/batch/tasklet/TestCreateElasticIndexSettingsTasklet-settings.json");
        tasklet.setIndexSettings(resource);
        tasklet.setIndexName(INDEX);
        tasklet.setContentType(XContentType.JSON);
        tasklet.execute(null, null);
        
        assertTrue(admin.exists(new IndicesExistsRequest(INDEX)).get().isExists());
    }

}
