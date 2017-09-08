begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
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
name|metrics
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
name|util
operator|.
name|Time
operator|.
name|now
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotCompliantMBeanException
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
name|javax
operator|.
name|management
operator|.
name|StandardMBean
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|DFSUtilClient
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
name|protocol
operator|.
name|DatanodeInfo
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|DatanodeReportType
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
name|protocol
operator|.
name|RollingUpgradeInfo
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|NamenodeRole
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
name|resolver
operator|.
name|FederationNamespaceInfo
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
name|router
operator|.
name|Router
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
name|router
operator|.
name|RouterRpcServer
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
name|MembershipStore
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
name|StateStoreService
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
name|protocol
operator|.
name|GetNamespaceInfoRequest
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
name|protocol
operator|.
name|GetNamespaceInfoResponse
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
name|namenode
operator|.
name|NameNodeMXBean
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
name|namenode
operator|.
name|NameNodeStatusMXBean
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
name|namenode
operator|.
name|metrics
operator|.
name|FSNamesystemMBean
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
name|ipc
operator|.
name|StandbyException
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
name|net
operator|.
name|NetUtils
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
name|util
operator|.
name|VersionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Expose the Namenode metrics as the Router was one.  */
end_comment

begin_class
DECL|class|NamenodeBeanMetrics
specifier|public
class|class
name|NamenodeBeanMetrics
implements|implements
name|FSNamesystemMBean
implements|,
name|NameNodeMXBean
implements|,
name|NameNodeStatusMXBean
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NamenodeBeanMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
comment|/** FSNamesystem bean. */
DECL|field|fsBeanName
specifier|private
name|ObjectName
name|fsBeanName
decl_stmt|;
comment|/** FSNamesystemState bean. */
DECL|field|fsStateBeanName
specifier|private
name|ObjectName
name|fsStateBeanName
decl_stmt|;
comment|/** NameNodeInfo bean. */
DECL|field|nnInfoBeanName
specifier|private
name|ObjectName
name|nnInfoBeanName
decl_stmt|;
comment|/** NameNodeStatus bean. */
DECL|field|nnStatusBeanName
specifier|private
name|ObjectName
name|nnStatusBeanName
decl_stmt|;
DECL|method|NamenodeBeanMetrics (Router router)
specifier|public
name|NamenodeBeanMetrics
parameter_list|(
name|Router
name|router
parameter_list|)
block|{
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
try|try
block|{
comment|// TODO this needs to be done with the Metrics from FSNamesystem
name|StandardMBean
name|bean
init|=
operator|new
name|StandardMBean
argument_list|(
name|this
argument_list|,
name|FSNamesystemMBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|fsBeanName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"NameNode"
argument_list|,
literal|"FSNamesystem"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered FSNamesystem MBean: {}"
argument_list|,
name|this
operator|.
name|fsBeanName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotCompliantMBeanException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad FSNamesystem MBean setup"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|StandardMBean
name|bean
init|=
operator|new
name|StandardMBean
argument_list|(
name|this
argument_list|,
name|FSNamesystemMBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|fsStateBeanName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"NameNode"
argument_list|,
literal|"FSNamesystemState"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered FSNamesystemState MBean: {}"
argument_list|,
name|this
operator|.
name|fsStateBeanName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotCompliantMBeanException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad FSNamesystemState MBean setup"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|StandardMBean
name|bean
init|=
operator|new
name|StandardMBean
argument_list|(
name|this
argument_list|,
name|NameNodeMXBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|nnInfoBeanName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"NameNode"
argument_list|,
literal|"NameNodeInfo"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered NameNodeInfo MBean: {}"
argument_list|,
name|this
operator|.
name|nnInfoBeanName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotCompliantMBeanException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad NameNodeInfo MBean setup"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|StandardMBean
name|bean
init|=
operator|new
name|StandardMBean
argument_list|(
name|this
argument_list|,
name|NameNodeStatusMXBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|nnStatusBeanName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"NameNode"
argument_list|,
literal|"NameNodeStatus"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered NameNodeStatus MBean: {}"
argument_list|,
name|this
operator|.
name|nnStatusBeanName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotCompliantMBeanException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad NameNodeStatus MBean setup"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * De-register the JMX interfaces.    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|fsStateBeanName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|fsStateBeanName
argument_list|)
expr_stmt|;
name|fsStateBeanName
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|nnInfoBeanName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|nnInfoBeanName
argument_list|)
expr_stmt|;
name|nnInfoBeanName
operator|=
literal|null
expr_stmt|;
block|}
comment|// Remove the NameNode status bean
if|if
condition|(
name|nnStatusBeanName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|nnStatusBeanName
argument_list|)
expr_stmt|;
name|nnStatusBeanName
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getFederationMetrics ()
specifier|private
name|FederationMetrics
name|getFederationMetrics
parameter_list|()
block|{
return|return
name|this
operator|.
name|router
operator|.
name|getMetrics
argument_list|()
return|;
block|}
comment|/////////////////////////////////////////////////////////
comment|// NameNodeMXBean
comment|/////////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|VersionInfo
operator|.
name|getVersion
argument_list|()
operator|+
literal|", r"
operator|+
name|VersionInfo
operator|.
name|getRevision
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSoftwareVersion ()
specifier|public
name|String
name|getSoftwareVersion
parameter_list|()
block|{
return|return
name|VersionInfo
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUsed ()
specifier|public
name|long
name|getUsed
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getUsedCapacity
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFree ()
specifier|public
name|long
name|getFree
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getRemainingCapacity
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotal ()
specifier|public
name|long
name|getTotal
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getTotalCapacity
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSafemode ()
specifier|public
name|String
name|getSafemode
parameter_list|()
block|{
comment|// We assume that the global federated view is never in safe mode
return|return
literal|""
return|;
block|}
annotation|@
name|Override
DECL|method|isUpgradeFinalized ()
specifier|public
name|boolean
name|isUpgradeFinalized
parameter_list|()
block|{
comment|// We assume the upgrade is always finalized in a federated biew
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getRollingUpgradeStatus ()
specifier|public
name|RollingUpgradeInfo
operator|.
name|Bean
name|getRollingUpgradeStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getNonDfsUsedSpace ()
specifier|public
name|long
name|getNonDfsUsedSpace
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getPercentUsed ()
specifier|public
name|float
name|getPercentUsed
parameter_list|()
block|{
return|return
name|DFSUtilClient
operator|.
name|getPercentUsed
argument_list|(
name|getCapacityUsed
argument_list|()
argument_list|,
name|getCapacityTotal
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPercentRemaining ()
specifier|public
name|float
name|getPercentRemaining
parameter_list|()
block|{
return|return
name|DFSUtilClient
operator|.
name|getPercentUsed
argument_list|(
name|getCapacityRemaining
argument_list|()
argument_list|,
name|getCapacityTotal
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheUsed ()
specifier|public
name|long
name|getCacheUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheCapacity ()
specifier|public
name|long
name|getCacheCapacity
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockPoolUsedSpace ()
specifier|public
name|long
name|getBlockPoolUsedSpace
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getPercentBlockPoolUsed ()
specifier|public
name|float
name|getPercentBlockPoolUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalBlocks ()
specifier|public
name|long
name|getTotalBlocks
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getNumBlocks
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfMissingBlocks ()
specifier|public
name|long
name|getNumberOfMissingBlocks
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getNumOfMissingBlocks
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|getPendingReplicationBlocks ()
specifier|public
name|long
name|getPendingReplicationBlocks
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getNumOfBlocksPendingReplication
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPendingReconstructionBlocks ()
specifier|public
name|long
name|getPendingReconstructionBlocks
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getNumOfBlocksPendingReplication
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|getUnderReplicatedBlocks ()
specifier|public
name|long
name|getUnderReplicatedBlocks
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getNumOfBlocksUnderReplicated
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLowRedundancyBlocks ()
specifier|public
name|long
name|getLowRedundancyBlocks
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getNumOfBlocksUnderReplicated
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPendingDeletionBlocks ()
specifier|public
name|long
name|getPendingDeletionBlocks
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getNumOfBlocksPendingDeletion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getScheduledReplicationBlocks ()
specifier|public
name|long
name|getScheduledReplicationBlocks
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfMissingBlocksWithReplicationFactorOne ()
specifier|public
name|long
name|getNumberOfMissingBlocksWithReplicationFactorOne
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCorruptFiles ()
specifier|public
name|String
name|getCorruptFiles
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getThreads ()
specifier|public
name|int
name|getThreads
parameter_list|()
block|{
return|return
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
operator|.
name|getThreadCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveNodes ()
specifier|public
name|String
name|getLiveNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|getNodes
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDeadNodes ()
specifier|public
name|String
name|getDeadNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|getNodes
argument_list|(
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDecomNodes ()
specifier|public
name|String
name|getDecomNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|getNodes
argument_list|(
name|DatanodeReportType
operator|.
name|DECOMMISSIONING
argument_list|)
return|;
block|}
comment|/**    * Get all the nodes in the federation from a particular type.    * TODO this is expensive, we may want to cache it.    * @param type Type of the datanodes to check.    * @return JSON with the nodes.    */
DECL|method|getNodes (DatanodeReportType type)
specifier|private
name|String
name|getNodes
parameter_list|(
name|DatanodeReportType
name|type
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|info
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|RouterRpcServer
name|rpcServer
init|=
name|this
operator|.
name|router
operator|.
name|getRpcServer
argument_list|()
decl_stmt|;
name|DatanodeInfo
index|[]
name|datanodes
init|=
name|rpcServer
operator|.
name|getDatanodeReport
argument_list|(
name|type
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|node
range|:
name|datanodes
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|innerinfo
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"infoAddr"
argument_list|,
name|node
operator|.
name|getInfoAddr
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"infoSecureAddr"
argument_list|,
name|node
operator|.
name|getInfoSecureAddr
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"xferaddr"
argument_list|,
name|node
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"location"
argument_list|,
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"lastContact"
argument_list|,
name|getLastContact
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"usedSpace"
argument_list|,
name|node
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"adminState"
argument_list|,
name|node
operator|.
name|getAdminState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"nonDfsUsedSpace"
argument_list|,
name|node
operator|.
name|getNonDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"capacity"
argument_list|,
name|node
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"numBlocks"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// node.numBlocks()
name|innerinfo
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
operator|(
name|node
operator|.
name|getSoftwareVersion
argument_list|()
operator|==
literal|null
condition|?
literal|"UNKNOWN"
else|:
name|node
operator|.
name|getSoftwareVersion
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"used"
argument_list|,
name|node
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"remaining"
argument_list|,
name|node
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"blockScheduled"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// node.getBlocksScheduled()
name|innerinfo
operator|.
name|put
argument_list|(
literal|"blockPoolUsed"
argument_list|,
name|node
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"blockPoolUsedPercent"
argument_list|,
name|node
operator|.
name|getBlockPoolUsedPercent
argument_list|()
argument_list|)
expr_stmt|;
name|innerinfo
operator|.
name|put
argument_list|(
literal|"volfails"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// node.getVolumeFailures()
name|info
operator|.
name|put
argument_list|(
name|node
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|node
operator|.
name|getXferPort
argument_list|()
argument_list|,
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|innerinfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StandbyException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get {} nodes, Router in safe mode"
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get "
operator|+
name|type
operator|+
literal|" nodes"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|JSON
operator|.
name|toString
argument_list|(
name|info
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
block|{
try|try
block|{
return|return
name|getNamespaceInfo
argument_list|(
name|FederationNamespaceInfo
operator|::
name|getClusterId
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot fetch cluster ID metrics {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|""
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
try|try
block|{
return|return
name|getNamespaceInfo
argument_list|(
name|FederationNamespaceInfo
operator|::
name|getBlockPoolId
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot fetch block pool ID metrics {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|""
return|;
block|}
block|}
comment|/**    * Build a set of unique values found in all namespaces.    *    * @param f Method reference of the appropriate FederationNamespaceInfo    *          getter function    * @return Set of unique string values found in all discovered namespaces.    * @throws IOException if the query could not be executed.    */
DECL|method|getNamespaceInfo ( Function<FederationNamespaceInfo, String> f)
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|getNamespaceInfo
parameter_list|(
name|Function
argument_list|<
name|FederationNamespaceInfo
argument_list|,
name|String
argument_list|>
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|StateStoreService
name|stateStore
init|=
name|router
operator|.
name|getStateStore
argument_list|()
decl_stmt|;
name|MembershipStore
name|membershipStore
init|=
name|stateStore
operator|.
name|getRegisteredRecordStore
argument_list|(
name|MembershipStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetNamespaceInfoRequest
name|request
init|=
name|GetNamespaceInfoRequest
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|GetNamespaceInfoResponse
name|response
init|=
name|membershipStore
operator|.
name|getNamespaceInfo
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|response
operator|.
name|getNamespaceInfo
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|f
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNameDirStatuses ()
specifier|public
name|String
name|getNameDirStatuses
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeUsage ()
specifier|public
name|String
name|getNodeUsage
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getNameJournalStatus ()
specifier|public
name|String
name|getNameJournalStatus
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getJournalTransactionInfo ()
specifier|public
name|String
name|getJournalTransactionInfo
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getNNStartedTimeInMillis ()
specifier|public
name|long
name|getNNStartedTimeInMillis
parameter_list|()
block|{
return|return
name|this
operator|.
name|router
operator|.
name|getStartTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCompileInfo ()
specifier|public
name|String
name|getCompileInfo
parameter_list|()
block|{
return|return
name|VersionInfo
operator|.
name|getDate
argument_list|()
operator|+
literal|" by "
operator|+
name|VersionInfo
operator|.
name|getUser
argument_list|()
operator|+
literal|" from "
operator|+
name|VersionInfo
operator|.
name|getBranch
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDistinctVersionCount ()
specifier|public
name|int
name|getDistinctVersionCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getDistinctVersions ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getDistinctVersions
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/////////////////////////////////////////////////////////
comment|// FSNamesystemMBean
comment|/////////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getFSState ()
specifier|public
name|String
name|getFSState
parameter_list|()
block|{
comment|// We assume is not in safe mode
return|return
literal|"Operational"
return|;
block|}
annotation|@
name|Override
DECL|method|getBlocksTotal ()
specifier|public
name|long
name|getBlocksTotal
parameter_list|()
block|{
return|return
name|this
operator|.
name|getTotalBlocks
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityTotal ()
specifier|public
name|long
name|getCapacityTotal
parameter_list|()
block|{
return|return
name|this
operator|.
name|getTotal
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityRemaining ()
specifier|public
name|long
name|getCapacityRemaining
parameter_list|()
block|{
return|return
name|this
operator|.
name|getFree
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacityUsed ()
specifier|public
name|long
name|getCapacityUsed
parameter_list|()
block|{
return|return
name|this
operator|.
name|getUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFilesTotal ()
specifier|public
name|long
name|getFilesTotal
parameter_list|()
block|{
return|return
name|getFederationMetrics
argument_list|()
operator|.
name|getNumFiles
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalLoad ()
specifier|public
name|int
name|getTotalLoad
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getNumLiveDataNodes ()
specifier|public
name|int
name|getNumLiveDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|router
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumLiveNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDeadDataNodes ()
specifier|public
name|int
name|getNumDeadDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|router
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDeadNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumStaleDataNodes ()
specifier|public
name|int
name|getNumStaleDataNodes
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDecomLiveDataNodes ()
specifier|public
name|int
name|getNumDecomLiveDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|router
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDecomLiveNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDecomDeadDataNodes ()
specifier|public
name|int
name|getNumDecomDeadDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|router
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDecomDeadNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDecommissioningDataNodes ()
specifier|public
name|int
name|getNumDecommissioningDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|router
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDecommissioningNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumInMaintenanceLiveDataNodes ()
specifier|public
name|int
name|getNumInMaintenanceLiveDataNodes
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumInMaintenanceDeadDataNodes ()
specifier|public
name|int
name|getNumInMaintenanceDeadDataNodes
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumEnteringMaintenanceDataNodes ()
specifier|public
name|int
name|getNumEnteringMaintenanceDataNodes
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getVolumeFailuresTotal ()
specifier|public
name|int
name|getVolumeFailuresTotal
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getEstimatedCapacityLostTotal ()
specifier|public
name|long
name|getEstimatedCapacityLostTotal
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getSnapshotStats ()
specifier|public
name|String
name|getSnapshotStats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxObjects ()
specifier|public
name|long
name|getMaxObjects
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockDeletionStartTime ()
specifier|public
name|long
name|getBlockDeletionStartTime
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getNumStaleStorages ()
specifier|public
name|int
name|getNumStaleStorages
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getTopUserOpCounts ()
specifier|public
name|String
name|getTopUserOpCounts
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getFsLockQueueLength ()
specifier|public
name|int
name|getFsLockQueueLength
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalSyncCount ()
specifier|public
name|long
name|getTotalSyncCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalSyncTimes ()
specifier|public
name|String
name|getTotalSyncTimes
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
DECL|method|getLastContact (DatanodeInfo node)
specifier|private
name|long
name|getLastContact
parameter_list|(
name|DatanodeInfo
name|node
parameter_list|)
block|{
return|return
operator|(
name|now
argument_list|()
operator|-
name|node
operator|.
name|getLastUpdate
argument_list|()
operator|)
operator|/
literal|1000
return|;
block|}
comment|/////////////////////////////////////////////////////////
comment|// NameNodeStatusMXBean
comment|/////////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getNNRole ()
specifier|public
name|String
name|getNNRole
parameter_list|()
block|{
return|return
name|NamenodeRole
operator|.
name|NAMENODE
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|HAServiceState
operator|.
name|ACTIVE
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getHostAndPort ()
specifier|public
name|String
name|getHostAndPort
parameter_list|()
block|{
return|return
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|router
operator|.
name|getRpcServerAddress
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isSecurityEnabled ()
specifier|public
name|boolean
name|isSecurityEnabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getLastHATransitionTime ()
specifier|public
name|long
name|getLastHATransitionTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesWithFutureGenerationStamps ()
specifier|public
name|long
name|getBytesWithFutureGenerationStamps
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getSlowPeersReport ()
specifier|public
name|String
name|getSlowPeersReport
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getSlowDisksReport ()
specifier|public
name|String
name|getSlowDisksReport
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfSnapshottableDirs ()
specifier|public
name|long
name|getNumberOfSnapshottableDirs
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getEnteringMaintenanceNodes ()
specifier|public
name|String
name|getEnteringMaintenanceNodes
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getNameDirSize ()
specifier|public
name|String
name|getNameDirSize
parameter_list|()
block|{
return|return
literal|"N/A"
return|;
block|}
annotation|@
name|Override
DECL|method|getNumEncryptionZones ()
specifier|public
name|int
name|getNumEncryptionZones
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

