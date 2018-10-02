begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
package|package
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
name|base
operator|.
name|Preconditions
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|StorageReportProto
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
name|exceptions
operator|.
name|SCMException
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|StorageLocationReport
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
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|ConcurrentHashMap
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
import|import static
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
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|DUPLICATE_DATANODE
import|;
end_import

begin_import
import|import static
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
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|NO_SUCH_DATANODE
import|;
end_import

begin_comment
comment|/**  * This data structure maintains the disk space capacity, disk usage and free  * space availability per Datanode.  * This information is built from the DN node reports.  */
end_comment

begin_class
DECL|class|SCMNodeStorageStatMap
specifier|public
class|class
name|SCMNodeStorageStatMap
implements|implements
name|SCMNodeStorageStatMXBean
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SCMNodeStorageStatMap
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|warningUtilizationThreshold
specifier|private
specifier|final
name|double
name|warningUtilizationThreshold
decl_stmt|;
DECL|field|criticalUtilizationThreshold
specifier|private
specifier|final
name|double
name|criticalUtilizationThreshold
decl_stmt|;
DECL|field|scmNodeStorageReportMap
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
argument_list|>
name|scmNodeStorageReportMap
decl_stmt|;
comment|// NodeStorageInfo MXBean
DECL|field|scmNodeStorageInfoBean
specifier|private
name|ObjectName
name|scmNodeStorageInfoBean
decl_stmt|;
comment|/**    * constructs the scmNodeStorageReportMap object.    */
DECL|method|SCMNodeStorageStatMap (OzoneConfiguration conf)
specifier|public
name|SCMNodeStorageStatMap
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
comment|// scmNodeStorageReportMap = new ConcurrentHashMap<>();
name|scmNodeStorageReportMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|warningUtilizationThreshold
operator|=
name|conf
operator|.
name|getDouble
argument_list|(
name|OzoneConfigKeys
operator|.
name|HDDS_DATANODE_STORAGE_UTILIZATION_WARNING_THRESHOLD
argument_list|,
name|OzoneConfigKeys
operator|.
name|HDDS_DATANODE_STORAGE_UTILIZATION_WARNING_THRESHOLD_DEFAULT
argument_list|)
expr_stmt|;
name|criticalUtilizationThreshold
operator|=
name|conf
operator|.
name|getDouble
argument_list|(
name|OzoneConfigKeys
operator|.
name|HDDS_DATANODE_STORAGE_UTILIZATION_CRITICAL_THRESHOLD
argument_list|,
name|OzoneConfigKeys
operator|.
name|HDDS_DATANODE_STORAGE_UTILIZATION_CRITICAL_THRESHOLD_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Enum that Describes what we should do at various thresholds.    */
DECL|enum|UtilizationThreshold
specifier|public
enum|enum
name|UtilizationThreshold
block|{
DECL|enumConstant|NORMAL
DECL|enumConstant|WARN
DECL|enumConstant|CRITICAL
name|NORMAL
block|,
name|WARN
block|,
name|CRITICAL
block|;   }
comment|/**    * Returns true if this a datanode that is already tracked by    * scmNodeStorageReportMap.    *    * @param datanodeID - UUID of the Datanode.    * @return True if this is tracked, false if this map does not know about it.    */
DECL|method|isKnownDatanode (UUID datanodeID)
specifier|public
name|boolean
name|isKnownDatanode
parameter_list|(
name|UUID
name|datanodeID
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
return|return
name|scmNodeStorageReportMap
operator|.
name|containsKey
argument_list|(
name|datanodeID
argument_list|)
return|;
block|}
DECL|method|getDatanodeList ( UtilizationThreshold threshold)
specifier|public
name|List
argument_list|<
name|UUID
argument_list|>
name|getDatanodeList
parameter_list|(
name|UtilizationThreshold
name|threshold
parameter_list|)
block|{
return|return
name|scmNodeStorageReportMap
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|entry
lambda|->
operator|(
name|isThresholdReached
argument_list|(
name|threshold
argument_list|,
name|getScmUsedratio
argument_list|(
name|getUsedSpace
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|getCapacity
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|)
argument_list|)
operator|.
name|map
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Insert a new datanode into Node2Container Map.    *    * @param datanodeID -- Datanode UUID    * @param report - set if StorageReports.    */
DECL|method|insertNewDatanode (UUID datanodeID, Set<StorageLocationReport> report)
specifier|public
name|void
name|insertNewDatanode
parameter_list|(
name|UUID
name|datanodeID
parameter_list|,
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|report
parameter_list|)
throws|throws
name|SCMException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|report
operator|.
name|size
argument_list|()
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|scmNodeStorageReportMap
init|)
block|{
if|if
condition|(
name|isKnownDatanode
argument_list|(
name|datanodeID
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Node already exists in the map"
argument_list|,
name|DUPLICATE_DATANODE
argument_list|)
throw|;
block|}
name|scmNodeStorageReportMap
operator|.
name|putIfAbsent
argument_list|(
name|datanodeID
argument_list|,
name|report
argument_list|)
expr_stmt|;
block|}
block|}
comment|//TODO: This should be called once SCMNodeManager gets Started.
DECL|method|registerMXBean ()
specifier|private
name|void
name|registerMXBean
parameter_list|()
block|{
name|this
operator|.
name|scmNodeStorageInfoBean
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"StorageContainerManager"
argument_list|,
literal|"scmNodeStorageInfo"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
comment|//TODO: Unregister call should happen as a part of SCMNodeManager shutdown.
DECL|method|unregisterMXBean ()
specifier|private
name|void
name|unregisterMXBean
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|scmNodeStorageInfoBean
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|this
operator|.
name|scmNodeStorageInfoBean
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmNodeStorageInfoBean
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Updates the Container list of an existing DN.    *    * @param datanodeID - UUID of DN.    * @param report - set of Storage Reports for the Datanode.    * @throws SCMException - if we don't know about this datanode, for new DN    *                        use addDatanodeInContainerMap.    */
DECL|method|updateDatanodeMap (UUID datanodeID, Set<StorageLocationReport> report)
specifier|public
name|void
name|updateDatanodeMap
parameter_list|(
name|UUID
name|datanodeID
parameter_list|,
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|report
parameter_list|)
throws|throws
name|SCMException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|report
operator|.
name|size
argument_list|()
operator|!=
literal|0
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|scmNodeStorageReportMap
init|)
block|{
if|if
condition|(
operator|!
name|scmNodeStorageReportMap
operator|.
name|containsKey
argument_list|(
name|datanodeID
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"No such datanode"
argument_list|,
name|NO_SUCH_DATANODE
argument_list|)
throw|;
block|}
name|scmNodeStorageReportMap
operator|.
name|put
argument_list|(
name|datanodeID
argument_list|,
name|report
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processNodeReport (UUID datanodeID, StorageContainerDatanodeProtocolProtos.NodeReportProto nodeReport)
specifier|public
name|StorageReportResult
name|processNodeReport
parameter_list|(
name|UUID
name|datanodeID
parameter_list|,
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
name|nodeReport
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodeReport
argument_list|)
expr_stmt|;
name|long
name|totalCapacity
init|=
literal|0
decl_stmt|;
name|long
name|totalRemaining
init|=
literal|0
decl_stmt|;
name|long
name|totalScmUsed
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|storagReportSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|fullVolumeSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|failedVolumeSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|StorageReportProto
argument_list|>
name|storageReports
init|=
name|nodeReport
operator|.
name|getStorageReportList
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageReportProto
name|report
range|:
name|storageReports
control|)
block|{
name|StorageLocationReport
name|storageReport
init|=
name|StorageLocationReport
operator|.
name|getFromProtobuf
argument_list|(
name|report
argument_list|)
decl_stmt|;
name|storagReportSet
operator|.
name|add
argument_list|(
name|storageReport
argument_list|)
expr_stmt|;
if|if
condition|(
name|report
operator|.
name|hasFailed
argument_list|()
operator|&&
name|report
operator|.
name|getFailed
argument_list|()
condition|)
block|{
name|failedVolumeSet
operator|.
name|add
argument_list|(
name|storageReport
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isThresholdReached
argument_list|(
name|UtilizationThreshold
operator|.
name|CRITICAL
argument_list|,
name|getScmUsedratio
argument_list|(
name|report
operator|.
name|getScmUsed
argument_list|()
argument_list|,
name|report
operator|.
name|getCapacity
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|fullVolumeSet
operator|.
name|add
argument_list|(
name|storageReport
argument_list|)
expr_stmt|;
block|}
name|totalCapacity
operator|+=
name|report
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|totalRemaining
operator|+=
name|report
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
name|totalScmUsed
operator|+=
name|report
operator|.
name|getScmUsed
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isKnownDatanode
argument_list|(
name|datanodeID
argument_list|)
condition|)
block|{
name|insertNewDatanode
argument_list|(
name|datanodeID
argument_list|,
name|storagReportSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateDatanodeMap
argument_list|(
name|datanodeID
argument_list|,
name|storagReportSet
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isThresholdReached
argument_list|(
name|UtilizationThreshold
operator|.
name|CRITICAL
argument_list|,
name|getScmUsedratio
argument_list|(
name|totalScmUsed
argument_list|,
name|totalCapacity
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Datanode {} is out of storage space. Capacity: {}, Used: {}"
argument_list|,
name|datanodeID
argument_list|,
name|totalCapacity
argument_list|,
name|totalScmUsed
argument_list|)
expr_stmt|;
return|return
name|StorageReportResult
operator|.
name|ReportResultBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStatus
argument_list|(
name|ReportStatus
operator|.
name|DATANODE_OUT_OF_SPACE
argument_list|)
operator|.
name|setFullVolumeSet
argument_list|(
name|fullVolumeSet
argument_list|)
operator|.
name|setFailedVolumeSet
argument_list|(
name|failedVolumeSet
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|isThresholdReached
argument_list|(
name|UtilizationThreshold
operator|.
name|WARN
argument_list|,
name|getScmUsedratio
argument_list|(
name|totalScmUsed
argument_list|,
name|totalCapacity
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Datanode {} is low on storage space. Capacity: {}, Used: {}"
argument_list|,
name|datanodeID
argument_list|,
name|totalCapacity
argument_list|,
name|totalScmUsed
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|failedVolumeSet
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|fullVolumeSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|StorageReportResult
operator|.
name|ReportResultBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStatus
argument_list|(
name|ReportStatus
operator|.
name|STORAGE_OUT_OF_SPACE
argument_list|)
operator|.
name|setFullVolumeSet
argument_list|(
name|fullVolumeSet
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|failedVolumeSet
operator|.
name|isEmpty
argument_list|()
operator|&&
name|fullVolumeSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|StorageReportResult
operator|.
name|ReportResultBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStatus
argument_list|(
name|ReportStatus
operator|.
name|FAILED_STORAGE
argument_list|)
operator|.
name|setFailedVolumeSet
argument_list|(
name|failedVolumeSet
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|failedVolumeSet
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|fullVolumeSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|StorageReportResult
operator|.
name|ReportResultBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStatus
argument_list|(
name|ReportStatus
operator|.
name|FAILED_AND_OUT_OF_SPACE_STORAGE
argument_list|)
operator|.
name|setFailedVolumeSet
argument_list|(
name|failedVolumeSet
argument_list|)
operator|.
name|setFullVolumeSet
argument_list|(
name|fullVolumeSet
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
return|return
name|StorageReportResult
operator|.
name|ReportResultBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStatus
argument_list|(
name|ReportStatus
operator|.
name|ALL_IS_WELL
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|isThresholdReached (UtilizationThreshold threshold, double scmUsedratio)
specifier|private
name|boolean
name|isThresholdReached
parameter_list|(
name|UtilizationThreshold
name|threshold
parameter_list|,
name|double
name|scmUsedratio
parameter_list|)
block|{
switch|switch
condition|(
name|threshold
condition|)
block|{
case|case
name|NORMAL
case|:
return|return
name|scmUsedratio
operator|<
name|warningUtilizationThreshold
return|;
case|case
name|WARN
case|:
return|return
name|scmUsedratio
operator|>=
name|warningUtilizationThreshold
operator|&&
name|scmUsedratio
operator|<
name|criticalUtilizationThreshold
return|;
case|case
name|CRITICAL
case|:
return|return
name|scmUsedratio
operator|>=
name|criticalUtilizationThreshold
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown UtilizationThreshold value"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCapacity (UUID dnId)
specifier|public
name|long
name|getCapacity
parameter_list|(
name|UUID
name|dnId
parameter_list|)
block|{
name|long
name|capacity
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|reportSet
init|=
name|scmNodeStorageReportMap
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageLocationReport
name|report
range|:
name|reportSet
control|)
block|{
name|capacity
operator|+=
name|report
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
block|}
return|return
name|capacity
return|;
block|}
annotation|@
name|Override
DECL|method|getRemainingSpace (UUID dnId)
specifier|public
name|long
name|getRemainingSpace
parameter_list|(
name|UUID
name|dnId
parameter_list|)
block|{
name|long
name|remaining
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|reportSet
init|=
name|scmNodeStorageReportMap
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageLocationReport
name|report
range|:
name|reportSet
control|)
block|{
name|remaining
operator|+=
name|report
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
block|}
return|return
name|remaining
return|;
block|}
annotation|@
name|Override
DECL|method|getUsedSpace (UUID dnId)
specifier|public
name|long
name|getUsedSpace
parameter_list|(
name|UUID
name|dnId
parameter_list|)
block|{
name|long
name|scmUsed
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|reportSet
init|=
name|scmNodeStorageReportMap
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageLocationReport
name|report
range|:
name|reportSet
control|)
block|{
name|scmUsed
operator|+=
name|report
operator|.
name|getScmUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|scmUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalCapacity ()
specifier|public
name|long
name|getTotalCapacity
parameter_list|()
block|{
name|long
name|capacity
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|dnIdSet
init|=
name|scmNodeStorageReportMap
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|UUID
name|id
range|:
name|dnIdSet
control|)
block|{
name|capacity
operator|+=
name|getCapacity
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|capacity
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalSpaceUsed ()
specifier|public
name|long
name|getTotalSpaceUsed
parameter_list|()
block|{
name|long
name|scmUsed
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|dnIdSet
init|=
name|scmNodeStorageReportMap
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|UUID
name|id
range|:
name|dnIdSet
control|)
block|{
name|scmUsed
operator|+=
name|getUsedSpace
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|scmUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalFreeSpace ()
specifier|public
name|long
name|getTotalFreeSpace
parameter_list|()
block|{
name|long
name|remaining
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|dnIdSet
init|=
name|scmNodeStorageReportMap
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|UUID
name|id
range|:
name|dnIdSet
control|)
block|{
name|remaining
operator|+=
name|getRemainingSpace
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|remaining
return|;
block|}
comment|/**    * removes the dataNode from scmNodeStorageReportMap.    * @param datanodeID    * @throws SCMException in case the dataNode is not found in the map.    */
DECL|method|removeDatanode (UUID datanodeID)
specifier|public
name|void
name|removeDatanode
parameter_list|(
name|UUID
name|datanodeID
parameter_list|)
throws|throws
name|SCMException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|scmNodeStorageReportMap
init|)
block|{
if|if
condition|(
operator|!
name|scmNodeStorageReportMap
operator|.
name|containsKey
argument_list|(
name|datanodeID
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"No such datanode"
argument_list|,
name|NO_SUCH_DATANODE
argument_list|)
throw|;
block|}
name|scmNodeStorageReportMap
operator|.
name|remove
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the set of storage volumes for a Datanode.    * @param  datanodeID    * @return set of storage volumes.    */
annotation|@
name|Override
DECL|method|getStorageVolumes (UUID datanodeID)
specifier|public
name|Set
argument_list|<
name|StorageLocationReport
argument_list|>
name|getStorageVolumes
parameter_list|(
name|UUID
name|datanodeID
parameter_list|)
block|{
return|return
name|scmNodeStorageReportMap
operator|.
name|get
argument_list|(
name|datanodeID
argument_list|)
return|;
block|}
comment|/**    * Truncate to 4 digits since uncontrolled precision is some times    * counter intuitive to what users expect.    * @param value - double.    * @return double.    */
DECL|method|truncateDecimals (double value)
specifier|private
name|double
name|truncateDecimals
parameter_list|(
name|double
name|value
parameter_list|)
block|{
specifier|final
name|int
name|multiplier
init|=
literal|10000
decl_stmt|;
return|return
call|(
name|double
call|)
argument_list|(
call|(
name|long
call|)
argument_list|(
name|value
operator|*
name|multiplier
argument_list|)
argument_list|)
operator|/
name|multiplier
return|;
block|}
comment|/**    * get the scmUsed ratio.    */
DECL|method|getScmUsedratio (long scmUsed, long capacity)
specifier|public
name|double
name|getScmUsedratio
parameter_list|(
name|long
name|scmUsed
parameter_list|,
name|long
name|capacity
parameter_list|)
block|{
name|double
name|scmUsedRatio
init|=
name|truncateDecimals
argument_list|(
name|scmUsed
operator|/
operator|(
name|double
operator|)
name|capacity
argument_list|)
decl_stmt|;
return|return
name|scmUsedRatio
return|;
block|}
comment|/**    * Results possible from processing a Node report by    * Node2ContainerMapper.    */
DECL|enum|ReportStatus
specifier|public
enum|enum
name|ReportStatus
block|{
DECL|enumConstant|ALL_IS_WELL
name|ALL_IS_WELL
block|,
DECL|enumConstant|DATANODE_OUT_OF_SPACE
name|DATANODE_OUT_OF_SPACE
block|,
DECL|enumConstant|STORAGE_OUT_OF_SPACE
name|STORAGE_OUT_OF_SPACE
block|,
DECL|enumConstant|FAILED_STORAGE
name|FAILED_STORAGE
block|,
DECL|enumConstant|FAILED_AND_OUT_OF_SPACE_STORAGE
name|FAILED_AND_OUT_OF_SPACE_STORAGE
block|}
block|}
end_class

end_unit

