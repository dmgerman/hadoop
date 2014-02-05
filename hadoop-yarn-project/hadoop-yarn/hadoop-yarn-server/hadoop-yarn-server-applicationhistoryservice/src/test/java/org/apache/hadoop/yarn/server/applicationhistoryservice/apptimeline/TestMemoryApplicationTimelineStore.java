begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.apptimeline
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
name|applicationhistoryservice
operator|.
name|apptimeline
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_class
DECL|class|TestMemoryApplicationTimelineStore
specifier|public
class|class
name|TestMemoryApplicationTimelineStore
extends|extends
name|ApplicationTimelineStoreTestUtils
block|{
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|=
operator|new
name|MemoryApplicationTimelineStore
argument_list|()
expr_stmt|;
name|store
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
name|loadTestData
argument_list|()
expr_stmt|;
name|loadVerificationData
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getApplicationTimelineStore ()
specifier|public
name|ApplicationTimelineStore
name|getApplicationTimelineStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
annotation|@
name|Test
DECL|method|testGetSingleEntity ()
specifier|public
name|void
name|testGetSingleEntity
parameter_list|()
block|{
name|super
operator|.
name|testGetSingleEntity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetEntities ()
specifier|public
name|void
name|testGetEntities
parameter_list|()
block|{
name|super
operator|.
name|testGetEntities
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetEntitiesWithPrimaryFilters ()
specifier|public
name|void
name|testGetEntitiesWithPrimaryFilters
parameter_list|()
block|{
name|super
operator|.
name|testGetEntitiesWithPrimaryFilters
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetEntitiesWithSecondaryFilters ()
specifier|public
name|void
name|testGetEntitiesWithSecondaryFilters
parameter_list|()
block|{
name|super
operator|.
name|testGetEntitiesWithSecondaryFilters
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetEvents ()
specifier|public
name|void
name|testGetEvents
parameter_list|()
block|{
name|super
operator|.
name|testGetEvents
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

