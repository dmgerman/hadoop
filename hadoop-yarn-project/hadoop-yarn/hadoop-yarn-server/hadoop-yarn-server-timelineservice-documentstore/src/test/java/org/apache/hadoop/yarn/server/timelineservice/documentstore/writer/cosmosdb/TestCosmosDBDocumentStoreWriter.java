begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore.writer.cosmosdb
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
operator|.
name|writer
operator|.
name|cosmosdb
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|cosmosdb
operator|.
name|rx
operator|.
name|AsyncDocumentClient
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|documentstore
operator|.
name|DocumentStoreTestUtils
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
name|DocumentStoreUtils
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
name|CollectionType
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
name|entity
operator|.
name|TimelineEntityDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|mockito
operator|.
name|Mockito
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Test case for {@link CosmosDBDocumentStoreWriter}.  */
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
name|DocumentStoreUtils
operator|.
name|class
argument_list|)
DECL|class|TestCosmosDBDocumentStoreWriter
specifier|public
class|class
name|TestCosmosDBDocumentStoreWriter
block|{
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|AsyncDocumentClient
name|asyncDocumentClient
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AsyncDocumentClient
operator|.
name|class
argument_list|)
decl_stmt|;
name|PowerMockito
operator|.
name|mockStatic
argument_list|(
name|DocumentStoreUtils
operator|.
name|class
argument_list|)
expr_stmt|;
name|PowerMockito
operator|.
name|when
argument_list|(
name|DocumentStoreUtils
operator|.
name|getCosmosDBDatabaseName
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
literal|"FooBar"
argument_list|)
expr_stmt|;
name|PowerMockito
operator|.
name|when
argument_list|(
name|DocumentStoreUtils
operator|.
name|createCosmosDBAsyncClient
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
name|asyncDocumentClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|applyingUpdatesOnPrevDocTest ()
specifier|public
name|void
name|applyingUpdatesOnPrevDocTest
parameter_list|()
throws|throws
name|IOException
block|{
name|MockedCosmosDBDocumentStoreWriter
name|documentStoreWriter
init|=
operator|new
name|MockedCosmosDBDocumentStoreWriter
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|TimelineEntityDocument
name|actualEntityDoc
init|=
operator|new
name|TimelineEntityDocument
argument_list|()
decl_stmt|;
name|TimelineEntityDocument
name|expectedEntityDoc
init|=
name|DocumentStoreTestUtils
operator|.
name|bakeTimelineEntityDoc
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|actualEntityDoc
operator|.
name|getInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getIsRelatedToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|actualEntityDoc
operator|=
operator|(
name|TimelineEntityDocument
operator|)
name|documentStoreWriter
operator|.
name|applyUpdatesOnPrevDoc
argument_list|(
name|CollectionType
operator|.
name|ENTITY
argument_list|,
name|actualEntityDoc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getIsRelatedToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

