begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.driver
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|driver
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|getStateStoreConfiguration
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|impl
operator|.
name|StateStoreFileImpl
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
name|BeforeClass
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

begin_comment
comment|/**  * Test the FileSystem (e.g., HDFS) implementation of the State Store driver.  */
end_comment

begin_class
DECL|class|TestStateStoreFile
specifier|public
class|class
name|TestStateStoreFile
extends|extends
name|TestStateStoreDriverBase
block|{
annotation|@
name|BeforeClass
DECL|method|setupCluster ()
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getStateStoreConfiguration
argument_list|(
name|StateStoreFileImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|getStateStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|startup ()
specifier|public
name|void
name|startup
parameter_list|()
throws|throws
name|IOException
block|{
name|removeAll
argument_list|(
name|getStateStoreDriver
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInsert ()
specifier|public
name|void
name|testInsert
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|testInsert
argument_list|(
name|getStateStoreDriver
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdate ()
specifier|public
name|void
name|testUpdate
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|ReflectiveOperationException
throws|,
name|IOException
throws|,
name|SecurityException
block|{
name|testPut
argument_list|(
name|getStateStoreDriver
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelete ()
specifier|public
name|void
name|testDelete
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|testRemove
argument_list|(
name|getStateStoreDriver
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFetchErrors ()
specifier|public
name|void
name|testFetchErrors
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|testFetchErrors
argument_list|(
name|getStateStoreDriver
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMetrics ()
specifier|public
name|void
name|testMetrics
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|testMetrics
argument_list|(
name|getStateStoreDriver
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

