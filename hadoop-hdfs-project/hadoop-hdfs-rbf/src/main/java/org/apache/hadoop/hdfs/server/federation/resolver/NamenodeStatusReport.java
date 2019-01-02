begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver
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
name|resolver
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
name|server
operator|.
name|protocol
operator|.
name|NamespaceInfo
import|;
end_import

begin_comment
comment|/**  * Status of the namenode.  */
end_comment

begin_class
DECL|class|NamenodeStatusReport
specifier|public
class|class
name|NamenodeStatusReport
block|{
comment|/** Namenode information. */
DECL|field|nameserviceId
specifier|private
name|String
name|nameserviceId
init|=
literal|""
decl_stmt|;
DECL|field|namenodeId
specifier|private
name|String
name|namenodeId
init|=
literal|""
decl_stmt|;
DECL|field|clusterId
specifier|private
name|String
name|clusterId
init|=
literal|""
decl_stmt|;
DECL|field|blockPoolId
specifier|private
name|String
name|blockPoolId
init|=
literal|""
decl_stmt|;
DECL|field|rpcAddress
specifier|private
name|String
name|rpcAddress
init|=
literal|""
decl_stmt|;
DECL|field|serviceAddress
specifier|private
name|String
name|serviceAddress
init|=
literal|""
decl_stmt|;
DECL|field|lifelineAddress
specifier|private
name|String
name|lifelineAddress
init|=
literal|""
decl_stmt|;
DECL|field|webAddress
specifier|private
name|String
name|webAddress
init|=
literal|""
decl_stmt|;
comment|/** Namenode state. */
DECL|field|status
specifier|private
name|HAServiceState
name|status
init|=
name|HAServiceState
operator|.
name|STANDBY
decl_stmt|;
DECL|field|safeMode
specifier|private
name|boolean
name|safeMode
init|=
literal|false
decl_stmt|;
comment|/** Datanodes stats. */
DECL|field|liveDatanodes
specifier|private
name|int
name|liveDatanodes
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|deadDatanodes
specifier|private
name|int
name|deadDatanodes
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|staleDatanodes
specifier|private
name|int
name|staleDatanodes
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Decommissioning datanodes. */
DECL|field|decomDatanodes
specifier|private
name|int
name|decomDatanodes
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Live decommissioned datanodes. */
DECL|field|liveDecomDatanodes
specifier|private
name|int
name|liveDecomDatanodes
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Dead decommissioned datanodes. */
DECL|field|deadDecomDatanodes
specifier|private
name|int
name|deadDecomDatanodes
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Space stats. */
DECL|field|availableSpace
specifier|private
name|long
name|availableSpace
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numOfFiles
specifier|private
name|long
name|numOfFiles
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numOfBlocks
specifier|private
name|long
name|numOfBlocks
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numOfBlocksMissing
specifier|private
name|long
name|numOfBlocksMissing
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numOfBlocksPendingReplication
specifier|private
name|long
name|numOfBlocksPendingReplication
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numOfBlocksUnderReplicated
specifier|private
name|long
name|numOfBlocksUnderReplicated
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numOfBlocksPendingDeletion
specifier|private
name|long
name|numOfBlocksPendingDeletion
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|totalSpace
specifier|private
name|long
name|totalSpace
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|providedSpace
specifier|private
name|long
name|providedSpace
init|=
operator|-
literal|1
decl_stmt|;
comment|/** If the fields are valid. */
DECL|field|registrationValid
specifier|private
name|boolean
name|registrationValid
init|=
literal|false
decl_stmt|;
DECL|field|statsValid
specifier|private
name|boolean
name|statsValid
init|=
literal|false
decl_stmt|;
DECL|field|haStateValid
specifier|private
name|boolean
name|haStateValid
init|=
literal|false
decl_stmt|;
DECL|method|NamenodeStatusReport (String ns, String nn, String rpc, String service, String lifeline, String web)
specifier|public
name|NamenodeStatusReport
parameter_list|(
name|String
name|ns
parameter_list|,
name|String
name|nn
parameter_list|,
name|String
name|rpc
parameter_list|,
name|String
name|service
parameter_list|,
name|String
name|lifeline
parameter_list|,
name|String
name|web
parameter_list|)
block|{
name|this
operator|.
name|nameserviceId
operator|=
name|ns
expr_stmt|;
name|this
operator|.
name|namenodeId
operator|=
name|nn
expr_stmt|;
name|this
operator|.
name|rpcAddress
operator|=
name|rpc
expr_stmt|;
name|this
operator|.
name|serviceAddress
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|lifelineAddress
operator|=
name|lifeline
expr_stmt|;
name|this
operator|.
name|webAddress
operator|=
name|web
expr_stmt|;
block|}
comment|/**    * If the statistics are valid.    *    * @return If the statistics are valid.    */
DECL|method|statsValid ()
specifier|public
name|boolean
name|statsValid
parameter_list|()
block|{
return|return
name|this
operator|.
name|statsValid
return|;
block|}
comment|/**    * If the registration is valid.    *    * @return If the registration is valid.    */
DECL|method|registrationValid ()
specifier|public
name|boolean
name|registrationValid
parameter_list|()
block|{
return|return
name|this
operator|.
name|registrationValid
return|;
block|}
comment|/**    * If the HA state is valid.    *    * @return If the HA state is valid.    */
DECL|method|haStateValid ()
specifier|public
name|boolean
name|haStateValid
parameter_list|()
block|{
return|return
name|this
operator|.
name|haStateValid
return|;
block|}
comment|/**    * Get the state of the Namenode being monitored.    *    * @return State of the Namenode.    */
DECL|method|getState ()
specifier|public
name|FederationNamenodeServiceState
name|getState
parameter_list|()
block|{
if|if
condition|(
operator|!
name|registrationValid
condition|)
block|{
return|return
name|FederationNamenodeServiceState
operator|.
name|UNAVAILABLE
return|;
block|}
elseif|else
if|if
condition|(
name|haStateValid
condition|)
block|{
return|return
name|FederationNamenodeServiceState
operator|.
name|getState
argument_list|(
name|status
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|FederationNamenodeServiceState
operator|.
name|ACTIVE
return|;
block|}
block|}
comment|/**    * Get the name service identifier.    *    * @return The name service identifier.    */
DECL|method|getNameserviceId ()
specifier|public
name|String
name|getNameserviceId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nameserviceId
return|;
block|}
comment|/**    * Get the namenode identifier.    *    * @return The namenode identifier.    */
DECL|method|getNamenodeId ()
specifier|public
name|String
name|getNamenodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|namenodeId
return|;
block|}
comment|/**    * Get the cluster identifier.    *    * @return The cluster identifier.    */
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterId
return|;
block|}
comment|/**    * Get the block pool identifier.    *    * @return The block pool identifier.    */
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|this
operator|.
name|blockPoolId
return|;
block|}
comment|/**    * Get the RPC address.    *    * @return The RPC address.    */
DECL|method|getRpcAddress ()
specifier|public
name|String
name|getRpcAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|rpcAddress
return|;
block|}
comment|/**    * Get the Service RPC address.    *    * @return The Service RPC address.    */
DECL|method|getServiceAddress ()
specifier|public
name|String
name|getServiceAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|serviceAddress
return|;
block|}
comment|/**    * Get the Lifeline RPC address.    *    * @return The Lifeline RPC address.    */
DECL|method|getLifelineAddress ()
specifier|public
name|String
name|getLifelineAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|lifelineAddress
return|;
block|}
comment|/**    * Get the web address.    *    * @return The web address.    */
DECL|method|getWebAddress ()
specifier|public
name|String
name|getWebAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|webAddress
return|;
block|}
comment|/**    * Set the HA service state.    *    * @param state The HA service state to set.    */
DECL|method|setHAServiceState (HAServiceState state)
specifier|public
name|void
name|setHAServiceState
parameter_list|(
name|HAServiceState
name|state
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|haStateValid
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Set the namespace information.    *    * @param info Namespace information.    */
DECL|method|setNamespaceInfo (NamespaceInfo info)
specifier|public
name|void
name|setNamespaceInfo
parameter_list|(
name|NamespaceInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|info
operator|.
name|getClusterID
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockPoolId
operator|=
name|info
operator|.
name|getBlockPoolID
argument_list|()
expr_stmt|;
name|this
operator|.
name|registrationValid
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setSafeMode (boolean safemode)
specifier|public
name|void
name|setSafeMode
parameter_list|(
name|boolean
name|safemode
parameter_list|)
block|{
name|this
operator|.
name|safeMode
operator|=
name|safemode
expr_stmt|;
block|}
DECL|method|getSafemode ()
specifier|public
name|boolean
name|getSafemode
parameter_list|()
block|{
return|return
name|this
operator|.
name|safeMode
return|;
block|}
comment|/**    * Set the datanode information.    *    * @param numLive Number of live nodes.    * @param numDead Number of dead nodes.    * @param numStale Number of stale nodes.    * @param numDecom Number of decommissioning nodes.    * @param numLiveDecom Number of decommissioned live nodes.    * @param numDeadDecom Number of decommissioned dead nodes.    */
DECL|method|setDatanodeInfo (int numLive, int numDead, int numStale, int numDecom, int numLiveDecom, int numDeadDecom)
specifier|public
name|void
name|setDatanodeInfo
parameter_list|(
name|int
name|numLive
parameter_list|,
name|int
name|numDead
parameter_list|,
name|int
name|numStale
parameter_list|,
name|int
name|numDecom
parameter_list|,
name|int
name|numLiveDecom
parameter_list|,
name|int
name|numDeadDecom
parameter_list|)
block|{
name|this
operator|.
name|liveDatanodes
operator|=
name|numLive
expr_stmt|;
name|this
operator|.
name|deadDatanodes
operator|=
name|numDead
expr_stmt|;
name|this
operator|.
name|staleDatanodes
operator|=
name|numStale
expr_stmt|;
name|this
operator|.
name|decomDatanodes
operator|=
name|numDecom
expr_stmt|;
name|this
operator|.
name|liveDecomDatanodes
operator|=
name|numLiveDecom
expr_stmt|;
name|this
operator|.
name|deadDecomDatanodes
operator|=
name|numDeadDecom
expr_stmt|;
name|this
operator|.
name|statsValid
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Get the number of live blocks.    *    * @return The number of dead nodes.    */
DECL|method|getNumLiveDatanodes ()
specifier|public
name|int
name|getNumLiveDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|liveDatanodes
return|;
block|}
comment|/**    * Get the number of dead nodes.    *    * @return The number of dead nodes.    */
DECL|method|getNumDeadDatanodes ()
specifier|public
name|int
name|getNumDeadDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|deadDatanodes
return|;
block|}
comment|/**    * Get the number of stale nodes.    *    * @return The number of stale nodes.    */
DECL|method|getNumStaleDatanodes ()
specifier|public
name|int
name|getNumStaleDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|staleDatanodes
return|;
block|}
comment|/**    * Get the number of decommissionining nodes.    *    * @return The number of decommissionining nodes.    */
DECL|method|getNumDecommissioningDatanodes ()
specifier|public
name|int
name|getNumDecommissioningDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|decomDatanodes
return|;
block|}
comment|/**    * Get the number of live decommissioned nodes.    *    * @return The number of live decommissioned nodes.    */
DECL|method|getNumDecomLiveDatanodes ()
specifier|public
name|int
name|getNumDecomLiveDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|liveDecomDatanodes
return|;
block|}
comment|/**    * Get the number of dead decommissioned nodes.    *    * @return The number of dead decommissioned nodes.    */
DECL|method|getNumDecomDeadDatanodes ()
specifier|public
name|int
name|getNumDecomDeadDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|deadDecomDatanodes
return|;
block|}
comment|/**    * Set the filesystem information.    *    * @param available Available capacity.    * @param total Total capacity.    * @param numFiles Number of files.    * @param numBlocks Total number of blocks.    * @param numBlocksMissing Number of missing blocks.    * @param numBlocksPendingReplication Number of blocks pending replication.    * @param numBlocksUnderReplicated Number of blocks under replication.    * @param numBlocksPendingDeletion Number of blocks pending deletion.    * @param providedSpace Space in provided storage.    */
DECL|method|setNamesystemInfo (long available, long total, long numFiles, long numBlocks, long numBlocksMissing, long numBlocksPendingReplication, long numBlocksUnderReplicated, long numBlocksPendingDeletion, long providedSpace)
specifier|public
name|void
name|setNamesystemInfo
parameter_list|(
name|long
name|available
parameter_list|,
name|long
name|total
parameter_list|,
name|long
name|numFiles
parameter_list|,
name|long
name|numBlocks
parameter_list|,
name|long
name|numBlocksMissing
parameter_list|,
name|long
name|numBlocksPendingReplication
parameter_list|,
name|long
name|numBlocksUnderReplicated
parameter_list|,
name|long
name|numBlocksPendingDeletion
parameter_list|,
name|long
name|providedSpace
parameter_list|)
block|{
name|this
operator|.
name|totalSpace
operator|=
name|total
expr_stmt|;
name|this
operator|.
name|availableSpace
operator|=
name|available
expr_stmt|;
name|this
operator|.
name|numOfBlocks
operator|=
name|numBlocks
expr_stmt|;
name|this
operator|.
name|numOfBlocksMissing
operator|=
name|numBlocksMissing
expr_stmt|;
name|this
operator|.
name|numOfBlocksPendingReplication
operator|=
name|numBlocksPendingReplication
expr_stmt|;
name|this
operator|.
name|numOfBlocksUnderReplicated
operator|=
name|numBlocksUnderReplicated
expr_stmt|;
name|this
operator|.
name|numOfBlocksPendingDeletion
operator|=
name|numBlocksPendingDeletion
expr_stmt|;
name|this
operator|.
name|numOfFiles
operator|=
name|numFiles
expr_stmt|;
name|this
operator|.
name|statsValid
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|providedSpace
operator|=
name|providedSpace
expr_stmt|;
block|}
comment|/**    * Get the number of blocks.    *    * @return The number of blocks.    */
DECL|method|getNumBlocks ()
specifier|public
name|long
name|getNumBlocks
parameter_list|()
block|{
return|return
name|this
operator|.
name|numOfBlocks
return|;
block|}
comment|/**    * Get the number of files.    *    * @return The number of files.    */
DECL|method|getNumFiles ()
specifier|public
name|long
name|getNumFiles
parameter_list|()
block|{
return|return
name|this
operator|.
name|numOfFiles
return|;
block|}
comment|/**    * Get the total space.    *    * @return The total space.    */
DECL|method|getTotalSpace ()
specifier|public
name|long
name|getTotalSpace
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalSpace
return|;
block|}
comment|/**    * Get the available space.    *    * @return The available space.    */
DECL|method|getAvailableSpace ()
specifier|public
name|long
name|getAvailableSpace
parameter_list|()
block|{
return|return
name|this
operator|.
name|availableSpace
return|;
block|}
comment|/**    * Get the space occupied by provided storage.    *    * @return the provided capacity.    */
DECL|method|getProvidedSpace ()
specifier|public
name|long
name|getProvidedSpace
parameter_list|()
block|{
return|return
name|this
operator|.
name|providedSpace
return|;
block|}
comment|/**    * Get the number of missing blocks.    *    * @return Number of missing blocks.    */
DECL|method|getNumBlocksMissing ()
specifier|public
name|long
name|getNumBlocksMissing
parameter_list|()
block|{
return|return
name|this
operator|.
name|numOfBlocksMissing
return|;
block|}
comment|/**    * Get the number of pending replication blocks.    *    * @return Number of pending replication blocks.    */
DECL|method|getNumOfBlocksPendingReplication ()
specifier|public
name|long
name|getNumOfBlocksPendingReplication
parameter_list|()
block|{
return|return
name|this
operator|.
name|numOfBlocksPendingReplication
return|;
block|}
comment|/**    * Get the number of under replicated blocks.    *    * @return Number of under replicated blocks.    */
DECL|method|getNumOfBlocksUnderReplicated ()
specifier|public
name|long
name|getNumOfBlocksUnderReplicated
parameter_list|()
block|{
return|return
name|this
operator|.
name|numOfBlocksUnderReplicated
return|;
block|}
comment|/**    * Get the number of pending deletion blocks.    *    * @return Number of pending deletion blocks.    */
DECL|method|getNumOfBlocksPendingDeletion ()
specifier|public
name|long
name|getNumOfBlocksPendingDeletion
parameter_list|()
block|{
return|return
name|this
operator|.
name|numOfBlocksPendingDeletion
return|;
block|}
comment|/**    * Set the validity of registration.    * @param isValid The desired value to be set.    */
DECL|method|setRegistrationValid (boolean isValid)
specifier|public
name|void
name|setRegistrationValid
parameter_list|(
name|boolean
name|isValid
parameter_list|)
block|{
name|this
operator|.
name|registrationValid
operator|=
name|isValid
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s-%s:%s"
argument_list|,
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|,
name|serviceAddress
argument_list|)
return|;
block|}
block|}
end_class

end_unit

