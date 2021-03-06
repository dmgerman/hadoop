begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|documentstore
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntities
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|collector
operator|.
name|TimelineCollectorContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|documentstore
operator|.
name|lib
operator|.
name|DocumentStoreFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
operator|.
name|TimelineDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|documentstore
operator|.
name|writer
operator|.
name|DocumentStoreWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|documentstore
operator|.
name|writer
operator|.
name|DummyDocumentStoreWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|powermock
operator|.
name|api
operator|.
name|mockito
operator|.
name|PowerMockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|powermock
operator|.
name|core
operator|.
name|classloader
operator|.
name|annotations
operator|.
name|PrepareForTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|powermock
operator|.
name|modules
operator|.
name|junit4
operator|.
name|PowerMockRunner
import|;
end_import

begin_comment
comment|/**  * Test case for {@link DocumentStoreTimelineWriterImpl}.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|PowerMockRunner
operator|.
name|class
argument_list|)
annotation|@
name|PrepareForTest
argument_list|(
name|DocumentStoreFactory
operator|.
name|class
argument_list|)
DECL|class|TestDocumentStoreTimelineWriterImpl
specifier|public
class|class
name|TestDocumentStoreTimelineWriterImpl
block|{
DECL|field|documentStoreWriter
specifier|private
specifier|final
name|DocumentStoreWriter
argument_list|<
name|TimelineDocument
argument_list|>
name|documentStoreWriter
init|=
operator|new
name|DummyDocumentStoreWriter
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|YarnException
block|{
name|conf
operator|.
name|set
argument_list|(
name|DocumentStoreUtils
operator|.
name|TIMELINE_SERVICE_DOCUMENTSTORE_DATABASE_NAME
argument_list|,
literal|"TestDB"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DocumentStoreUtils
operator|.
name|TIMELINE_SERVICE_COSMOSDB_ENDPOINT
argument_list|,
literal|"https://localhost:443"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DocumentStoreUtils
operator|.
name|TIMELINE_SERVICE_COSMOSDB_MASTER_KEY
argument_list|,
literal|"1234567"
argument_list|)
expr_stmt|;
name|PowerMockito
operator|.
name|mockStatic
argument_list|(
name|DocumentStoreFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|PowerMockito
operator|.
name|when
argument_list|(
name|DocumentStoreFactory
operator|.
name|createDocumentStoreWriter
argument_list|(
name|ArgumentMatchers
operator|.
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|documentStoreWriter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|YarnException
operator|.
name|class
argument_list|)
DECL|method|testFailOnNoCosmosDBConfigs ()
specifier|public
name|void
name|testFailOnNoCosmosDBConfigs
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStoreUtils
operator|.
name|validateCosmosDBConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWritingToCosmosDB ()
specifier|public
name|void
name|testWritingToCosmosDB
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStoreTimelineWriterImpl
name|timelineWriter
init|=
operator|new
name|DocumentStoreTimelineWriterImpl
argument_list|()
decl_stmt|;
name|timelineWriter
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|TimelineEntities
name|entities
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|entities
operator|.
name|addEntities
argument_list|(
name|DocumentStoreTestUtils
operator|.
name|bakeTimelineEntities
argument_list|()
argument_list|)
expr_stmt|;
name|entities
operator|.
name|addEntity
argument_list|(
name|DocumentStoreTestUtils
operator|.
name|bakeTimelineEntityDoc
argument_list|()
operator|.
name|fetchTimelineEntity
argument_list|()
argument_list|)
expr_stmt|;
name|PowerMockito
operator|.
name|verifyStatic
argument_list|(
name|DocumentStoreFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|TimelineCollectorContext
name|context
init|=
operator|new
name|TimelineCollectorContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setFlowName
argument_list|(
literal|"TestFlow"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setAppId
argument_list|(
literal|"DUMMY_APP_ID"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClusterId
argument_list|(
literal|"yarn_cluster"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setUserId
argument_list|(
literal|"test_user"
argument_list|)
expr_stmt|;
name|timelineWriter
operator|.
name|write
argument_list|(
name|context
argument_list|,
name|entities
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test_user"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

