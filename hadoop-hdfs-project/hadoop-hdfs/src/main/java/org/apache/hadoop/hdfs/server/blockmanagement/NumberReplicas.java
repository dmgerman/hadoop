begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|hdfs
operator|.
name|util
operator|.
name|EnumCounters
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|CORRUPT
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|DECOMMISSIONED
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|DECOMMISSIONING
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|EXCESS
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|LIVE
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|MAINTENANCE_FOR_READ
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|MAINTENANCE_NOT_FOR_READ
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|READONLY
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|REDUNDANT
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|NumberReplicas
operator|.
name|StoredReplicaState
operator|.
name|STALESTORAGE
import|;
end_import

begin_comment
comment|/**  * A immutable object that stores the number of live replicas and  * the number of decommissioned Replicas.  */
end_comment

begin_class
DECL|class|NumberReplicas
specifier|public
class|class
name|NumberReplicas
extends|extends
name|EnumCounters
argument_list|<
name|NumberReplicas
operator|.
name|StoredReplicaState
argument_list|>
block|{
DECL|enum|StoredReplicaState
specifier|public
enum|enum
name|StoredReplicaState
block|{
comment|// live replicas. for a striped block, this value excludes redundant
comment|// replicas for the same internal block
DECL|enumConstant|LIVE
name|LIVE
block|,
DECL|enumConstant|READONLY
name|READONLY
block|,
DECL|enumConstant|DECOMMISSIONING
name|DECOMMISSIONING
block|,
DECL|enumConstant|DECOMMISSIONED
name|DECOMMISSIONED
block|,
comment|// We need live ENTERING_MAINTENANCE nodes to continue
comment|// to serve read request while it is being transitioned to live
comment|// IN_MAINTENANCE if these are the only replicas left.
comment|// MAINTENANCE_NOT_FOR_READ == maintenanceReplicas -
comment|// Live ENTERING_MAINTENANCE.
DECL|enumConstant|MAINTENANCE_NOT_FOR_READ
name|MAINTENANCE_NOT_FOR_READ
block|,
comment|// Live ENTERING_MAINTENANCE nodes to serve read requests.
DECL|enumConstant|MAINTENANCE_FOR_READ
name|MAINTENANCE_FOR_READ
block|,
DECL|enumConstant|CORRUPT
name|CORRUPT
block|,
comment|// excess replicas already tracked by blockmanager's excess map
DECL|enumConstant|EXCESS
name|EXCESS
block|,
DECL|enumConstant|STALESTORAGE
name|STALESTORAGE
block|,
comment|// for striped blocks only. number of redundant internal block replicas
comment|// that have not been tracked by blockmanager yet (i.e., not in excess)
DECL|enumConstant|REDUNDANT
name|REDUNDANT
block|}
DECL|method|NumberReplicas ()
specifier|public
name|NumberReplicas
parameter_list|()
block|{
name|super
argument_list|(
name|StoredReplicaState
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|liveReplicas ()
specifier|public
name|int
name|liveReplicas
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|LIVE
argument_list|)
return|;
block|}
DECL|method|readOnlyReplicas ()
specifier|public
name|int
name|readOnlyReplicas
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|READONLY
argument_list|)
return|;
block|}
comment|/**    *    * @return decommissioned and decommissioning replicas    */
DECL|method|decommissionedAndDecommissioning ()
specifier|public
name|int
name|decommissionedAndDecommissioning
parameter_list|()
block|{
return|return
name|decommissioned
argument_list|()
operator|+
name|decommissioning
argument_list|()
return|;
block|}
comment|/**    *    * @return decommissioned replicas only    */
DECL|method|decommissioned ()
specifier|public
name|int
name|decommissioned
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|DECOMMISSIONED
argument_list|)
return|;
block|}
comment|/**    *    * @return decommissioning replicas only    */
DECL|method|decommissioning ()
specifier|public
name|int
name|decommissioning
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|DECOMMISSIONING
argument_list|)
return|;
block|}
DECL|method|corruptReplicas ()
specifier|public
name|int
name|corruptReplicas
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|CORRUPT
argument_list|)
return|;
block|}
DECL|method|excessReplicas ()
specifier|public
name|int
name|excessReplicas
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|EXCESS
argument_list|)
return|;
block|}
comment|/**    * @return the number of replicas which are on stale nodes.    * This is not mutually exclusive with the other counts -- ie a    * replica may count as both "live" and "stale".    */
DECL|method|replicasOnStaleNodes ()
specifier|public
name|int
name|replicasOnStaleNodes
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|STALESTORAGE
argument_list|)
return|;
block|}
DECL|method|redundantInternalBlocks ()
specifier|public
name|int
name|redundantInternalBlocks
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|REDUNDANT
argument_list|)
return|;
block|}
DECL|method|maintenanceNotForReadReplicas ()
specifier|public
name|int
name|maintenanceNotForReadReplicas
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|MAINTENANCE_NOT_FOR_READ
argument_list|)
return|;
block|}
DECL|method|maintenanceReplicas ()
specifier|public
name|int
name|maintenanceReplicas
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|get
argument_list|(
name|MAINTENANCE_NOT_FOR_READ
argument_list|)
operator|+
name|get
argument_list|(
name|MAINTENANCE_FOR_READ
argument_list|)
argument_list|)
return|;
block|}
DECL|method|outOfServiceReplicas ()
specifier|public
name|int
name|outOfServiceReplicas
parameter_list|()
block|{
return|return
name|maintenanceReplicas
argument_list|()
operator|+
name|decommissionedAndDecommissioning
argument_list|()
return|;
block|}
DECL|method|liveEnteringMaintenanceReplicas ()
specifier|public
name|int
name|liveEnteringMaintenanceReplicas
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|get
argument_list|(
name|MAINTENANCE_FOR_READ
argument_list|)
return|;
block|}
block|}
end_class

end_unit

