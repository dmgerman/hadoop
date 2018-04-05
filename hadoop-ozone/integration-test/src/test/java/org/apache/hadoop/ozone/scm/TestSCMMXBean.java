begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|scm
operator|.
name|StorageContainerManager
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
name|io
operator|.
name|IOUtils
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
name|MiniOzoneClassicCluster
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
name|MiniOzoneCluster
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
name|conf
operator|.
name|OzoneConfiguration
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
name|OzoneConsts
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
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
operator|.
name|ContainerStat
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
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|AfterClass
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_comment
comment|/**  *  * This class is to test JMX management interface for scm information.  */
end_comment

begin_class
DECL|class|TestSCMMXBean
specifier|public
class|class
name|TestSCMMXBean
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestSCMMXBean
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|numOfDatanodes
specifier|private
specifier|static
name|int
name|numOfDatanodes
init|=
literal|1
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|scm
specifier|private
specifier|static
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|mbs
specifier|private
specifier|static
name|MBeanServer
name|mbs
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numOfDatanodes
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitOzoneReady
argument_list|()
expr_stmt|;
name|scm
operator|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
expr_stmt|;
name|mbs
operator|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSCMMXBean ()
specifier|public
name|void
name|testSCMMXBean
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectName
name|bean
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=StorageContainerManager,"
operator|+
literal|"name=StorageContainerManagerInfo,"
operator|+
literal|"component=ServerRuntime"
argument_list|)
decl_stmt|;
name|String
name|dnRpcPort
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|bean
argument_list|,
literal|"DatanodeRpcPort"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|scm
operator|.
name|getDatanodeRpcPort
argument_list|()
argument_list|,
name|dnRpcPort
argument_list|)
expr_stmt|;
name|String
name|clientRpcPort
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|bean
argument_list|,
literal|"ClientRpcPort"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|scm
operator|.
name|getClientRpcPort
argument_list|()
argument_list|,
name|clientRpcPort
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ContainerStat
argument_list|>
name|map
init|=
name|scm
operator|.
name|getContainerReportCache
argument_list|()
decl_stmt|;
name|ContainerStat
name|stat
init|=
operator|new
name|ContainerStat
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"nodeID"
argument_list|,
name|stat
argument_list|)
expr_stmt|;
name|TabularData
name|data
init|=
operator|(
name|TabularData
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|bean
argument_list|,
literal|"ContainerReport"
argument_list|)
decl_stmt|;
comment|// verify report info
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|data
operator|.
name|values
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|obj
operator|instanceof
name|CompositeData
argument_list|)
expr_stmt|;
name|CompositeData
name|d
init|=
operator|(
name|CompositeData
operator|)
name|obj
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|it
init|=
name|d
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"nodeID"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stat
operator|.
name|toJsonString
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSCMNodeManagerMXBean ()
specifier|public
name|void
name|testSCMNodeManagerMXBean
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NodeManager
name|scmNm
init|=
name|scm
operator|.
name|getScmNodeManager
argument_list|()
decl_stmt|;
name|ObjectName
name|bean
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=SCMNodeManager,name=SCMNodeManagerInfo"
argument_list|)
decl_stmt|;
name|Integer
name|minChillNodes
init|=
operator|(
name|Integer
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|bean
argument_list|,
literal|"MinimumChillModeNodes"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|scmNm
operator|.
name|getMinimumChillModeNodes
argument_list|()
argument_list|,
name|minChillNodes
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|isOutOfChillMode
init|=
operator|(
name|boolean
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|bean
argument_list|,
literal|"OutOfChillMode"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|scmNm
operator|.
name|isOutOfChillMode
argument_list|()
argument_list|,
name|isOutOfChillMode
argument_list|)
expr_stmt|;
name|String
name|chillStatus
init|=
operator|(
name|String
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|bean
argument_list|,
literal|"ChillModeStatus"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|scmNm
operator|.
name|getChillModeStatus
argument_list|()
argument_list|,
name|chillStatus
argument_list|)
expr_stmt|;
name|TabularData
name|nodeCountObj
init|=
operator|(
name|TabularData
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|bean
argument_list|,
literal|"NodeCount"
argument_list|)
decl_stmt|;
name|verifyEquals
argument_list|(
name|nodeCountObj
argument_list|,
name|scm
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|getNodeCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * An internal function used to compare a TabularData returned    * by JMX with the expected data in a Map.    */
DECL|method|verifyEquals (TabularData data1, Map<String, Integer> data2)
specifier|private
name|void
name|verifyEquals
parameter_list|(
name|TabularData
name|data1
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|data2
parameter_list|)
block|{
if|if
condition|(
name|data1
operator|==
literal|null
operator|||
name|data2
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Data should not be null."
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Object
name|obj
range|:
name|data1
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Each TabularData is a set of CompositeData
name|assertTrue
argument_list|(
name|obj
operator|instanceof
name|CompositeData
argument_list|)
expr_stmt|;
name|CompositeData
name|cds
init|=
operator|(
name|CompositeData
operator|)
name|obj
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cds
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|it
init|=
name|cds
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|data2
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data2
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

