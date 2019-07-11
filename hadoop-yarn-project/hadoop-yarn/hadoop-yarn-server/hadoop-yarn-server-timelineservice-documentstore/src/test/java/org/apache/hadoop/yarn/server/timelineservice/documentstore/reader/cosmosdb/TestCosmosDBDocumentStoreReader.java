begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore.reader.cosmosdb
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
name|reader
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
name|reader
operator|.
name|TimelineReaderContext
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

begin_comment
comment|/**  * Test case for {@link CosmosDBDocumentStoreReader}.  */
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
DECL|class|TestCosmosDBDocumentStoreReader
specifier|public
class|class
name|TestCosmosDBDocumentStoreReader
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
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testFailureFOnEmptyPredicates ()
specifier|public
name|void
name|testFailureFOnEmptyPredicates
parameter_list|()
block|{
name|PowerMockito
operator|.
name|when
argument_list|(
name|DocumentStoreUtils
operator|.
name|isNullOrEmpty
argument_list|(
name|ArgumentMatchers
operator|.
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|CosmosDBDocumentStoreReader
name|cosmosDBDocumentStoreReader
init|=
operator|new
name|CosmosDBDocumentStoreReader
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|cosmosDBDocumentStoreReader
operator|.
name|addPredicates
argument_list|(
operator|new
name|TimelineReaderContext
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"DummyCollection"
argument_list|,
operator|new
name|StringBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

