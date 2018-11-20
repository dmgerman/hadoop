begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.ratis
package|package
name|org
operator|.
name|apache
operator|.
name|ratis
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
name|scm
operator|.
name|pipeline
operator|.
name|Pipeline
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
name|ratis
operator|.
name|client
operator|.
name|RaftClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|conf
operator|.
name|RaftProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|grpc
operator|.
name|GrpcConfigKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroupId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftPeer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftPeerId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|retry
operator|.
name|RetryPolicies
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|retry
operator|.
name|RetryPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|RpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|thirdparty
operator|.
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|proto
operator|.
name|RaftProtos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
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
name|ratis
operator|.
name|util
operator|.
name|SizeInBytes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|TimeDuration
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
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
name|TimeUnit
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
import|;
end_import

begin_comment
comment|/**  * Ratis helper methods.  */
end_comment

begin_interface
DECL|interface|RatisHelper
specifier|public
interface|interface
name|RatisHelper
block|{
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RatisHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|toRaftPeerIdString (DatanodeDetails id)
specifier|static
name|String
name|toRaftPeerIdString
parameter_list|(
name|DatanodeDetails
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|getUuidString
argument_list|()
return|;
block|}
DECL|method|toDatanodeId (String peerIdString)
specifier|static
name|UUID
name|toDatanodeId
parameter_list|(
name|String
name|peerIdString
parameter_list|)
block|{
return|return
name|UUID
operator|.
name|fromString
argument_list|(
name|peerIdString
argument_list|)
return|;
block|}
DECL|method|toDatanodeId (RaftPeerId peerId)
specifier|static
name|UUID
name|toDatanodeId
parameter_list|(
name|RaftPeerId
name|peerId
parameter_list|)
block|{
return|return
name|toDatanodeId
argument_list|(
name|peerId
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toDatanodeId (RaftProtos.RaftPeerProto peerId)
specifier|static
name|UUID
name|toDatanodeId
parameter_list|(
name|RaftProtos
operator|.
name|RaftPeerProto
name|peerId
parameter_list|)
block|{
return|return
name|toDatanodeId
argument_list|(
name|RaftPeerId
operator|.
name|valueOf
argument_list|(
name|peerId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toRaftPeerAddressString (DatanodeDetails id)
specifier|static
name|String
name|toRaftPeerAddressString
parameter_list|(
name|DatanodeDetails
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|getIpAddress
argument_list|()
operator|+
literal|":"
operator|+
name|id
operator|.
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|RATIS
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|toRaftPeerId (DatanodeDetails id)
specifier|static
name|RaftPeerId
name|toRaftPeerId
parameter_list|(
name|DatanodeDetails
name|id
parameter_list|)
block|{
return|return
name|RaftPeerId
operator|.
name|valueOf
argument_list|(
name|toRaftPeerIdString
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toRaftPeer (DatanodeDetails id)
specifier|static
name|RaftPeer
name|toRaftPeer
parameter_list|(
name|DatanodeDetails
name|id
parameter_list|)
block|{
return|return
operator|new
name|RaftPeer
argument_list|(
name|toRaftPeerId
argument_list|(
name|id
argument_list|)
argument_list|,
name|toRaftPeerAddressString
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toRaftPeers (Pipeline pipeline)
specifier|static
name|List
argument_list|<
name|RaftPeer
argument_list|>
name|toRaftPeers
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
return|return
name|toRaftPeers
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toRaftPeers ( List<E> datanodes)
specifier|static
parameter_list|<
name|E
extends|extends
name|DatanodeDetails
parameter_list|>
name|List
argument_list|<
name|RaftPeer
argument_list|>
name|toRaftPeers
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|datanodes
parameter_list|)
block|{
return|return
name|datanodes
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|RatisHelper
operator|::
name|toRaftPeer
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
comment|/* TODO: use a dummy id for all groups for the moment.    *       It should be changed to a unique id for each group.    */
DECL|field|DUMMY_GROUP_ID
name|RaftGroupId
name|DUMMY_GROUP_ID
init|=
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|ByteString
operator|.
name|copyFromUtf8
argument_list|(
literal|"AOzoneRatisGroup"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|EMPTY_GROUP
name|RaftGroup
name|EMPTY_GROUP
init|=
name|RaftGroup
operator|.
name|valueOf
argument_list|(
name|DUMMY_GROUP_ID
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|emptyRaftGroup ()
specifier|static
name|RaftGroup
name|emptyRaftGroup
parameter_list|()
block|{
return|return
name|EMPTY_GROUP
return|;
block|}
DECL|method|newRaftGroup (Collection<RaftPeer> peers)
specifier|static
name|RaftGroup
name|newRaftGroup
parameter_list|(
name|Collection
argument_list|<
name|RaftPeer
argument_list|>
name|peers
parameter_list|)
block|{
return|return
name|peers
operator|.
name|isEmpty
argument_list|()
condition|?
name|emptyRaftGroup
argument_list|()
else|:
name|RaftGroup
operator|.
name|valueOf
argument_list|(
name|DUMMY_GROUP_ID
argument_list|,
name|peers
argument_list|)
return|;
block|}
DECL|method|newRaftGroup (RaftGroupId groupId, Collection<DatanodeDetails> peers)
specifier|static
name|RaftGroup
name|newRaftGroup
parameter_list|(
name|RaftGroupId
name|groupId
parameter_list|,
name|Collection
argument_list|<
name|DatanodeDetails
argument_list|>
name|peers
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RaftPeer
argument_list|>
name|newPeers
init|=
name|peers
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|RatisHelper
operator|::
name|toRaftPeer
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|peers
operator|.
name|isEmpty
argument_list|()
condition|?
name|RaftGroup
operator|.
name|valueOf
argument_list|(
name|groupId
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
else|:
name|RaftGroup
operator|.
name|valueOf
argument_list|(
name|groupId
argument_list|,
name|newPeers
argument_list|)
return|;
block|}
DECL|method|newRaftGroup (Pipeline pipeline)
specifier|static
name|RaftGroup
name|newRaftGroup
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
return|return
name|RaftGroup
operator|.
name|valueOf
argument_list|(
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|toRaftPeers
argument_list|(
name|pipeline
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newRaftClient (RpcType rpcType, Pipeline pipeline, RetryPolicy retryPolicy)
specifier|static
name|RaftClient
name|newRaftClient
parameter_list|(
name|RpcType
name|rpcType
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|,
name|RetryPolicy
name|retryPolicy
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|newRaftClient
argument_list|(
name|rpcType
argument_list|,
name|toRaftPeerId
argument_list|(
name|pipeline
operator|.
name|getFirstNode
argument_list|()
argument_list|)
argument_list|,
name|newRaftGroup
argument_list|(
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|pipeline
operator|.
name|getNodes
argument_list|()
argument_list|)
argument_list|,
name|retryPolicy
argument_list|)
return|;
block|}
DECL|method|newRaftClient (RpcType rpcType, RaftPeer leader, RetryPolicy retryPolicy)
specifier|static
name|RaftClient
name|newRaftClient
parameter_list|(
name|RpcType
name|rpcType
parameter_list|,
name|RaftPeer
name|leader
parameter_list|,
name|RetryPolicy
name|retryPolicy
parameter_list|)
block|{
return|return
name|newRaftClient
argument_list|(
name|rpcType
argument_list|,
name|leader
operator|.
name|getId
argument_list|()
argument_list|,
name|newRaftGroup
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|leader
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|retryPolicy
argument_list|)
return|;
block|}
DECL|method|newRaftClient (RpcType rpcType, RaftPeer leader, RaftGroup group, RetryPolicy retryPolicy)
specifier|static
name|RaftClient
name|newRaftClient
parameter_list|(
name|RpcType
name|rpcType
parameter_list|,
name|RaftPeer
name|leader
parameter_list|,
name|RaftGroup
name|group
parameter_list|,
name|RetryPolicy
name|retryPolicy
parameter_list|)
block|{
return|return
name|newRaftClient
argument_list|(
name|rpcType
argument_list|,
name|leader
operator|.
name|getId
argument_list|()
argument_list|,
name|group
argument_list|,
name|retryPolicy
argument_list|)
return|;
block|}
DECL|method|newRaftClient (RpcType rpcType, RaftPeerId leader, RaftGroup group, RetryPolicy retryPolicy)
specifier|static
name|RaftClient
name|newRaftClient
parameter_list|(
name|RpcType
name|rpcType
parameter_list|,
name|RaftPeerId
name|leader
parameter_list|,
name|RaftGroup
name|group
parameter_list|,
name|RetryPolicy
name|retryPolicy
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"newRaftClient: {}, leader={}, group={}"
argument_list|,
name|rpcType
argument_list|,
name|leader
argument_list|,
name|group
argument_list|)
expr_stmt|;
specifier|final
name|RaftProperties
name|properties
init|=
operator|new
name|RaftProperties
argument_list|()
decl_stmt|;
name|RaftConfigKeys
operator|.
name|Rpc
operator|.
name|setType
argument_list|(
name|properties
argument_list|,
name|rpcType
argument_list|)
expr_stmt|;
name|GrpcConfigKeys
operator|.
name|setMessageSizeMax
argument_list|(
name|properties
argument_list|,
name|SizeInBytes
operator|.
name|valueOf
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_CHUNK_MAX_SIZE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|RaftClient
operator|.
name|newBuilder
argument_list|()
operator|.
name|setRaftGroup
argument_list|(
name|group
argument_list|)
operator|.
name|setLeaderId
argument_list|(
name|leader
argument_list|)
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
operator|.
name|setRetryPolicy
argument_list|(
name|retryPolicy
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createRetryPolicy (Configuration conf)
specifier|static
name|RetryPolicy
name|createRetryPolicy
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|maxRetryCount
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|retryInterval
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_DEFAULT
operator|.
name|toInt
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|long
name|leaderElectionTimeout
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
argument_list|,
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
operator|.
name|toInt
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|long
name|clientRequestTimeout
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
operator|.
name|toInt
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|long
name|retryCacheTimeout
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_DEFAULT
operator|.
name|toInt
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|assertTrue
argument_list|(
name|maxRetryCount
operator|*
name|retryInterval
operator|>
literal|5
operator|*
name|leaderElectionTimeout
argument_list|,
literal|"Please make sure dfs.ratis.client.request.max.retries * "
operator|+
literal|"dfs.ratis.client.request.retry.interval> "
operator|+
literal|"5 * dfs.ratis.leader.election.minimum.timeout.duration"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|assertTrue
argument_list|(
name|maxRetryCount
operator|*
operator|(
name|retryInterval
operator|+
name|clientRequestTimeout
operator|)
operator|<
name|retryCacheTimeout
argument_list|,
literal|"Please make sure "
operator|+
literal|"(dfs.ratis.client.request.max.retries * "
operator|+
literal|"(dfs.ratis.client.request.retry.interval + "
operator|+
literal|"dfs.ratis.client.request.timeout.duration)) "
operator|+
literal|"< dfs.ratis.server.retry-cache.timeout.duration"
argument_list|)
expr_stmt|;
name|TimeDuration
name|sleepDuration
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
name|retryInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|RetryPolicy
name|retryPolicy
init|=
name|RetryPolicies
operator|.
name|retryUpToMaximumCountWithFixedSleep
argument_list|(
name|maxRetryCount
argument_list|,
name|sleepDuration
argument_list|)
decl_stmt|;
return|return
name|retryPolicy
return|;
block|}
block|}
end_interface

end_unit

