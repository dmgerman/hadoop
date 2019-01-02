begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
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
name|records
package|;
end_package

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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|StateStoreSerializer
import|;
end_import

begin_comment
comment|/**  * Data schema for storing NN stats in the  * {@link org.apache.hadoop.hdfs.server.federation.store.StateStoreService  * StateStoreService}.  */
end_comment

begin_class
DECL|class|MembershipStats
specifier|public
specifier|abstract
class|class
name|MembershipStats
extends|extends
name|BaseRecord
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|MembershipStats
name|newInstance
parameter_list|()
throws|throws
name|IOException
block|{
name|MembershipStats
name|record
init|=
name|StateStoreSerializer
operator|.
name|newRecord
argument_list|(
name|MembershipStats
operator|.
name|class
argument_list|)
decl_stmt|;
name|record
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|record
return|;
block|}
DECL|method|setTotalSpace (long space)
specifier|public
specifier|abstract
name|void
name|setTotalSpace
parameter_list|(
name|long
name|space
parameter_list|)
function_decl|;
DECL|method|getTotalSpace ()
specifier|public
specifier|abstract
name|long
name|getTotalSpace
parameter_list|()
function_decl|;
DECL|method|setAvailableSpace (long space)
specifier|public
specifier|abstract
name|void
name|setAvailableSpace
parameter_list|(
name|long
name|space
parameter_list|)
function_decl|;
DECL|method|getAvailableSpace ()
specifier|public
specifier|abstract
name|long
name|getAvailableSpace
parameter_list|()
function_decl|;
DECL|method|setProvidedSpace (long capacity)
specifier|public
specifier|abstract
name|void
name|setProvidedSpace
parameter_list|(
name|long
name|capacity
parameter_list|)
function_decl|;
DECL|method|getProvidedSpace ()
specifier|public
specifier|abstract
name|long
name|getProvidedSpace
parameter_list|()
function_decl|;
DECL|method|setNumOfFiles (long files)
specifier|public
specifier|abstract
name|void
name|setNumOfFiles
parameter_list|(
name|long
name|files
parameter_list|)
function_decl|;
DECL|method|getNumOfFiles ()
specifier|public
specifier|abstract
name|long
name|getNumOfFiles
parameter_list|()
function_decl|;
DECL|method|setNumOfBlocks (long blocks)
specifier|public
specifier|abstract
name|void
name|setNumOfBlocks
parameter_list|(
name|long
name|blocks
parameter_list|)
function_decl|;
DECL|method|getNumOfBlocks ()
specifier|public
specifier|abstract
name|long
name|getNumOfBlocks
parameter_list|()
function_decl|;
DECL|method|setNumOfBlocksMissing (long blocks)
specifier|public
specifier|abstract
name|void
name|setNumOfBlocksMissing
parameter_list|(
name|long
name|blocks
parameter_list|)
function_decl|;
DECL|method|getNumOfBlocksMissing ()
specifier|public
specifier|abstract
name|long
name|getNumOfBlocksMissing
parameter_list|()
function_decl|;
DECL|method|setNumOfBlocksPendingReplication (long blocks)
specifier|public
specifier|abstract
name|void
name|setNumOfBlocksPendingReplication
parameter_list|(
name|long
name|blocks
parameter_list|)
function_decl|;
DECL|method|getNumOfBlocksPendingReplication ()
specifier|public
specifier|abstract
name|long
name|getNumOfBlocksPendingReplication
parameter_list|()
function_decl|;
DECL|method|setNumOfBlocksUnderReplicated (long blocks)
specifier|public
specifier|abstract
name|void
name|setNumOfBlocksUnderReplicated
parameter_list|(
name|long
name|blocks
parameter_list|)
function_decl|;
DECL|method|getNumOfBlocksUnderReplicated ()
specifier|public
specifier|abstract
name|long
name|getNumOfBlocksUnderReplicated
parameter_list|()
function_decl|;
DECL|method|setNumOfBlocksPendingDeletion (long blocks)
specifier|public
specifier|abstract
name|void
name|setNumOfBlocksPendingDeletion
parameter_list|(
name|long
name|blocks
parameter_list|)
function_decl|;
DECL|method|getNumOfBlocksPendingDeletion ()
specifier|public
specifier|abstract
name|long
name|getNumOfBlocksPendingDeletion
parameter_list|()
function_decl|;
DECL|method|setNumOfActiveDatanodes (int nodes)
specifier|public
specifier|abstract
name|void
name|setNumOfActiveDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
function_decl|;
DECL|method|getNumOfActiveDatanodes ()
specifier|public
specifier|abstract
name|int
name|getNumOfActiveDatanodes
parameter_list|()
function_decl|;
DECL|method|setNumOfDeadDatanodes (int nodes)
specifier|public
specifier|abstract
name|void
name|setNumOfDeadDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
function_decl|;
DECL|method|getNumOfDeadDatanodes ()
specifier|public
specifier|abstract
name|int
name|getNumOfDeadDatanodes
parameter_list|()
function_decl|;
DECL|method|setNumOfStaleDatanodes (int nodes)
specifier|public
specifier|abstract
name|void
name|setNumOfStaleDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
function_decl|;
DECL|method|getNumOfStaleDatanodes ()
specifier|public
specifier|abstract
name|int
name|getNumOfStaleDatanodes
parameter_list|()
function_decl|;
DECL|method|setNumOfDecommissioningDatanodes (int nodes)
specifier|public
specifier|abstract
name|void
name|setNumOfDecommissioningDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
function_decl|;
DECL|method|getNumOfDecommissioningDatanodes ()
specifier|public
specifier|abstract
name|int
name|getNumOfDecommissioningDatanodes
parameter_list|()
function_decl|;
DECL|method|setNumOfDecomActiveDatanodes (int nodes)
specifier|public
specifier|abstract
name|void
name|setNumOfDecomActiveDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
function_decl|;
DECL|method|getNumOfDecomActiveDatanodes ()
specifier|public
specifier|abstract
name|int
name|getNumOfDecomActiveDatanodes
parameter_list|()
function_decl|;
DECL|method|setNumOfDecomDeadDatanodes (int nodes)
specifier|public
specifier|abstract
name|void
name|setNumOfDecomDeadDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
function_decl|;
DECL|method|getNumOfDecomDeadDatanodes ()
specifier|public
specifier|abstract
name|int
name|getNumOfDecomDeadDatanodes
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getPrimaryKeys ()
specifier|public
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPrimaryKeys
parameter_list|()
block|{
comment|// This record is not stored directly, no key needed
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
return|return
name|map
return|;
block|}
annotation|@
name|Override
DECL|method|getExpirationMs ()
specifier|public
name|long
name|getExpirationMs
parameter_list|()
block|{
comment|// This record is not stored directly, no expiration needed
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|setDateModified (long time)
specifier|public
name|void
name|setDateModified
parameter_list|(
name|long
name|time
parameter_list|)
block|{
comment|// We don't store this record directly
block|}
annotation|@
name|Override
DECL|method|getDateModified ()
specifier|public
name|long
name|getDateModified
parameter_list|()
block|{
comment|// We don't store this record directly
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|setDateCreated (long time)
specifier|public
name|void
name|setDateCreated
parameter_list|(
name|long
name|time
parameter_list|)
block|{
comment|// We don't store this record directly
block|}
annotation|@
name|Override
DECL|method|getDateCreated ()
specifier|public
name|long
name|getDateCreated
parameter_list|()
block|{
comment|// We don't store this record directly
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

