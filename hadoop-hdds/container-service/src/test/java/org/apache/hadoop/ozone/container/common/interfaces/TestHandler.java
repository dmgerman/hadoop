begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerMetrics
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerSet
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|HddsDispatcher
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|VolumeSet
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
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueHandler
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
name|Rule
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
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Tests Handler interface.  */
end_comment

begin_class
DECL|class|TestHandler
specifier|public
class|class
name|TestHandler
block|{
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|TestRule
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|dispatcher
specifier|private
name|HddsDispatcher
name|dispatcher
decl_stmt|;
DECL|field|containerSet
specifier|private
name|ContainerSet
name|containerSet
decl_stmt|;
DECL|field|volumeSet
specifier|private
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|handler
specifier|private
name|Handler
name|handler
decl_stmt|;
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
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerSet
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ContainerSet
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|volumeSet
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|VolumeSet
operator|.
name|class
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|datanodeDetails
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeDetails
operator|.
name|class
argument_list|)
decl_stmt|;
name|DatanodeStateMachine
name|stateMachine
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
decl_stmt|;
name|StateContext
name|context
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|stateMachine
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|stateMachine
argument_list|)
expr_stmt|;
name|ContainerMetrics
name|metrics
init|=
name|ContainerMetrics
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ContainerProtos
operator|.
name|ContainerType
argument_list|,
name|Handler
argument_list|>
name|handlers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerProtos
operator|.
name|ContainerType
name|containerType
range|:
name|ContainerProtos
operator|.
name|ContainerType
operator|.
name|values
argument_list|()
control|)
block|{
name|handlers
operator|.
name|put
argument_list|(
name|containerType
argument_list|,
name|Handler
operator|.
name|getHandlerForContainerType
argument_list|(
name|containerType
argument_list|,
name|conf
argument_list|,
name|context
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|,
name|metrics
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|dispatcher
operator|=
operator|new
name|HddsDispatcher
argument_list|(
name|conf
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|,
name|handlers
argument_list|,
literal|null
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetKeyValueHandler ()
specifier|public
name|void
name|testGetKeyValueHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|Handler
name|kvHandler
init|=
name|dispatcher
operator|.
name|getHandler
argument_list|(
name|ContainerProtos
operator|.
name|ContainerType
operator|.
name|KeyValueContainer
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"getHandlerForContainerType returned incorrect handler"
argument_list|,
operator|(
name|kvHandler
operator|instanceof
name|KeyValueHandler
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetHandlerForInvalidContainerType ()
specifier|public
name|void
name|testGetHandlerForInvalidContainerType
parameter_list|()
block|{
comment|// When new ContainerProtos.ContainerType are added, increment the code
comment|// for invalid enum.
name|ContainerProtos
operator|.
name|ContainerType
name|invalidContainerType
init|=
name|ContainerProtos
operator|.
name|ContainerType
operator|.
name|forNumber
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"New ContainerType detected. Not an invalid "
operator|+
literal|"containerType"
argument_list|,
name|invalidContainerType
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Handler
name|handler
init|=
name|dispatcher
operator|.
name|getHandler
argument_list|(
name|invalidContainerType
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Get Handler for Invalid ContainerType should "
operator|+
literal|"return null."
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

