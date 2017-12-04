begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.distributed
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|distributed
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
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|api
operator|.
name|records
operator|.
name|ContainerQueuingLimit
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
name|api
operator|.
name|records
operator|.
name|OpportunisticContainersStatus
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
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|Test
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
name|List
import|;
end_import

begin_comment
comment|/**  * Unit tests for NodeQueueLoadMonitor.  */
end_comment

begin_class
DECL|class|TestNodeQueueLoadMonitor
specifier|public
class|class
name|TestNodeQueueLoadMonitor
block|{
DECL|field|DEFAULT_MAX_QUEUE_LENGTH
specifier|private
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_QUEUE_LENGTH
init|=
literal|200
decl_stmt|;
DECL|class|FakeNodeId
specifier|static
class|class
name|FakeNodeId
extends|extends
name|NodeId
block|{
DECL|field|host
specifier|final
name|String
name|host
decl_stmt|;
DECL|field|port
specifier|final
name|int
name|port
decl_stmt|;
DECL|method|FakeNodeId (String host, int port)
specifier|public
name|FakeNodeId
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHost ()
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|host
return|;
block|}
annotation|@
name|Override
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
annotation|@
name|Override
DECL|method|setHost (String host)
specifier|protected
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|setPort (int port)
specifier|protected
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|build ()
specifier|protected
name|void
name|build
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|host
operator|+
literal|":"
operator|+
name|port
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWaitTimeSort ()
specifier|public
name|void
name|testWaitTimeSort
parameter_list|()
block|{
name|NodeQueueLoadMonitor
name|selector
init|=
operator|new
name|NodeQueueLoadMonitor
argument_list|(
name|NodeQueueLoadMonitor
operator|.
name|LoadComparator
operator|.
name|QUEUE_WAIT_TIME
argument_list|)
decl_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h1"
argument_list|,
literal|1
argument_list|,
literal|15
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h2"
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h3"
argument_list|,
literal|3
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodeIds
init|=
name|selector
operator|.
name|selectNodes
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"1-> "
operator|+
name|nodeIds
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h2:2"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h3:3"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h1:1"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now update node3
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h3"
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|nodeIds
operator|=
name|selector
operator|.
name|selectNodes
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"2-> "
operator|+
name|nodeIds
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h3:3"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h2:2"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h1:1"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now send update with -1 wait time
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h4"
argument_list|,
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|nodeIds
operator|=
name|selector
operator|.
name|selectNodes
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"3-> "
operator|+
name|nodeIds
argument_list|)
expr_stmt|;
comment|// No change
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h3:3"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h2:2"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h1:1"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueueLengthSort ()
specifier|public
name|void
name|testQueueLengthSort
parameter_list|()
block|{
name|NodeQueueLoadMonitor
name|selector
init|=
operator|new
name|NodeQueueLoadMonitor
argument_list|(
name|NodeQueueLoadMonitor
operator|.
name|LoadComparator
operator|.
name|QUEUE_LENGTH
argument_list|)
decl_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h1"
argument_list|,
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h2"
argument_list|,
literal|2
argument_list|,
operator|-
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h3"
argument_list|,
literal|3
argument_list|,
operator|-
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodeIds
init|=
name|selector
operator|.
name|selectNodes
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"1-> "
operator|+
name|nodeIds
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h2:2"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h3:3"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h1:1"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now update node3
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h3"
argument_list|,
literal|3
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|nodeIds
operator|=
name|selector
operator|.
name|selectNodes
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"2-> "
operator|+
name|nodeIds
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h3:3"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h2:2"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h1:1"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now send update with -1 wait time but valid length
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h4"
argument_list|,
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|nodeIds
operator|=
name|selector
operator|.
name|selectNodes
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"3-> "
operator|+
name|nodeIds
argument_list|)
expr_stmt|;
comment|// No change
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h3:3"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h2:2"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h1:1"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h4:4"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now update h3 and fill its queue.
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h3"
argument_list|,
literal|3
argument_list|,
operator|-
literal|1
argument_list|,
name|DEFAULT_MAX_QUEUE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|nodeIds
operator|=
name|selector
operator|.
name|selectNodes
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"4-> "
operator|+
name|nodeIds
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nodeIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h2:2"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h1:1"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h4:4"
argument_list|,
name|nodeIds
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerQueuingLimit ()
specifier|public
name|void
name|testContainerQueuingLimit
parameter_list|()
block|{
name|NodeQueueLoadMonitor
name|selector
init|=
operator|new
name|NodeQueueLoadMonitor
argument_list|(
name|NodeQueueLoadMonitor
operator|.
name|LoadComparator
operator|.
name|QUEUE_LENGTH
argument_list|)
decl_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h1"
argument_list|,
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h2"
argument_list|,
literal|2
argument_list|,
operator|-
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h3"
argument_list|,
literal|3
argument_list|,
operator|-
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test Mean Calculation
name|selector
operator|.
name|initThresholdCalculator
argument_list|(
literal|0
argument_list|,
literal|6
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|QueueLimitCalculator
name|calculator
init|=
name|selector
operator|.
name|getThresholdCalculator
argument_list|()
decl_stmt|;
name|ContainerQueuingLimit
name|containerQueuingLimit
init|=
name|calculator
operator|.
name|createContainerQueuingLimit
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|containerQueuingLimit
operator|.
name|getMaxQueueLength
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|containerQueuingLimit
operator|.
name|getMaxQueueWaitTimeInMs
argument_list|()
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|containerQueuingLimit
operator|=
name|calculator
operator|.
name|createContainerQueuingLimit
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|containerQueuingLimit
operator|.
name|getMaxQueueLength
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|containerQueuingLimit
operator|.
name|getMaxQueueWaitTimeInMs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test Limits do not exceed specified max
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h1"
argument_list|,
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|110
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h2"
argument_list|,
literal|2
argument_list|,
operator|-
literal|1
argument_list|,
literal|120
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h3"
argument_list|,
literal|3
argument_list|,
operator|-
literal|1
argument_list|,
literal|130
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h4"
argument_list|,
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
literal|140
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h5"
argument_list|,
literal|5
argument_list|,
operator|-
literal|1
argument_list|,
literal|150
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h6"
argument_list|,
literal|6
argument_list|,
operator|-
literal|1
argument_list|,
literal|160
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|containerQueuingLimit
operator|=
name|calculator
operator|.
name|createContainerQueuingLimit
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|containerQueuingLimit
operator|.
name|getMaxQueueLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test Limits do not go below specified min
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h1"
argument_list|,
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h2"
argument_list|,
literal|2
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h3"
argument_list|,
literal|3
argument_list|,
operator|-
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h4"
argument_list|,
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h5"
argument_list|,
literal|5
argument_list|,
operator|-
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|updateNode
argument_list|(
name|createRMNode
argument_list|(
literal|"h6"
argument_list|,
literal|6
argument_list|,
operator|-
literal|1
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|selector
operator|.
name|computeTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|containerQueuingLimit
operator|=
name|calculator
operator|.
name|createContainerQueuingLimit
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|containerQueuingLimit
operator|.
name|getMaxQueueLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createRMNode (String host, int port, int waitTime, int queueLength)
specifier|private
name|RMNode
name|createRMNode
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|waitTime
parameter_list|,
name|int
name|queueLength
parameter_list|)
block|{
return|return
name|createRMNode
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|waitTime
argument_list|,
name|queueLength
argument_list|,
name|DEFAULT_MAX_QUEUE_LENGTH
argument_list|)
return|;
block|}
DECL|method|createRMNode (String host, int port, int waitTime, int queueLength, int queueCapacity)
specifier|private
name|RMNode
name|createRMNode
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|waitTime
parameter_list|,
name|int
name|queueLength
parameter_list|,
name|int
name|queueCapacity
parameter_list|)
block|{
name|RMNode
name|node1
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|RMNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeId
name|nID1
init|=
operator|new
name|FakeNodeId
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nID1
argument_list|)
expr_stmt|;
name|OpportunisticContainersStatus
name|status1
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|OpportunisticContainersStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|status1
operator|.
name|getEstimatedQueueWaitTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|status1
operator|.
name|getWaitQueueLength
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|queueLength
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|status1
operator|.
name|getOpportQueueCapacity
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|queueCapacity
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|node1
operator|.
name|getOpportunisticContainersStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|status1
argument_list|)
expr_stmt|;
return|return
name|node1
return|;
block|}
block|}
end_class

end_unit

