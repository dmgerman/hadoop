begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Time
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
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * This class extends the primary identifier of a Datanode with ephemeral  * state, eg last reported time, usage information etc.  */
end_comment

begin_class
DECL|class|DatanodeInfo
specifier|public
class|class
name|DatanodeInfo
extends|extends
name|DatanodeDetails
block|{
DECL|field|lock
specifier|private
specifier|final
name|ReadWriteLock
name|lock
decl_stmt|;
DECL|field|lastHeartbeatTime
specifier|private
specifier|volatile
name|long
name|lastHeartbeatTime
decl_stmt|;
DECL|field|lastStatsUpdatedTime
specifier|private
name|long
name|lastStatsUpdatedTime
decl_stmt|;
comment|// If required we can dissect StorageReportProto and store the raw data
DECL|field|storageReports
specifier|private
name|List
argument_list|<
name|StorageReportProto
argument_list|>
name|storageReports
decl_stmt|;
comment|/**    * Constructs DatanodeInfo from DatanodeDetails.    *    * @param datanodeDetails Details about the datanode    */
DECL|method|DatanodeInfo (DatanodeDetails datanodeDetails)
specifier|public
name|DatanodeInfo
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
block|{
name|super
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|lastHeartbeatTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
comment|/**    * Updates the last heartbeat time with current time.    */
DECL|method|updateLastHeartbeatTime ()
specifier|public
name|void
name|updateLastHeartbeatTime
parameter_list|()
block|{
try|try
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|lastHeartbeatTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the last heartbeat time.    *    * @return last heartbeat time.    */
DECL|method|getLastHeartbeatTime ()
specifier|public
name|long
name|getLastHeartbeatTime
parameter_list|()
block|{
try|try
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|lastHeartbeatTime
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Updates the datanode storage reports.    *    * @param reports list of storage report    */
DECL|method|updateStorageReports (List<StorageReportProto> reports)
specifier|public
name|void
name|updateStorageReports
parameter_list|(
name|List
argument_list|<
name|StorageReportProto
argument_list|>
name|reports
parameter_list|)
block|{
try|try
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|lastStatsUpdatedTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|storageReports
operator|=
name|reports
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the storage reports associated with this datanode.    *    * @return list of storage report    */
DECL|method|getStorageReports ()
specifier|public
name|List
argument_list|<
name|StorageReportProto
argument_list|>
name|getStorageReports
parameter_list|()
block|{
try|try
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|storageReports
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

