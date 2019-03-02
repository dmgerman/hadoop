begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ratis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|ratis
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
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
name|conf
operator|.
name|StorageUnit
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
name|om
operator|.
name|OMConfigKeys
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|Status
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|Type
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
name|RaftConfigKeys
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
name|Message
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
name|util
operator|.
name|SizeInBytes
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
comment|/**  * Ratis helper methods for OM Ratis server and client.  */
end_comment

begin_class
DECL|class|OMRatisHelper
specifier|public
specifier|final
class|class
name|OMRatisHelper
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
name|OMRatisHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|OMRatisHelper ()
specifier|private
name|OMRatisHelper
parameter_list|()
block|{   }
comment|/**    * Creates a new RaftClient object.    * @param rpcType Replication Type    * @param omId OM id of the client    * @param group RaftGroup    * @param retryPolicy Retry policy    * @return RaftClient object    */
DECL|method|newRaftClient (RpcType rpcType, String omId, RaftGroup group, RetryPolicy retryPolicy, Configuration conf)
specifier|static
name|RaftClient
name|newRaftClient
parameter_list|(
name|RpcType
name|rpcType
parameter_list|,
name|String
name|omId
parameter_list|,
name|RaftGroup
name|group
parameter_list|,
name|RetryPolicy
name|retryPolicy
parameter_list|,
name|Configuration
name|conf
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
name|omId
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
specifier|final
name|int
name|raftSegmentPreallocatedSize
init|=
operator|(
name|int
operator|)
name|conf
operator|.
name|getStorageSize
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
argument_list|,
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
argument_list|,
name|StorageUnit
operator|.
name|BYTES
argument_list|)
decl_stmt|;
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
name|raftSegmentPreallocatedSize
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
name|getRaftPeerId
argument_list|(
name|omId
argument_list|)
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
DECL|method|getRaftPeerId (String omId)
specifier|static
name|RaftPeerId
name|getRaftPeerId
parameter_list|(
name|String
name|omId
parameter_list|)
block|{
return|return
name|RaftPeerId
operator|.
name|valueOf
argument_list|(
name|omId
argument_list|)
return|;
block|}
DECL|method|convertRequestToByteString (OMRequest request)
specifier|static
name|ByteString
name|convertRequestToByteString
parameter_list|(
name|OMRequest
name|request
parameter_list|)
block|{
name|byte
index|[]
name|requestBytes
init|=
name|request
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|requestBytes
argument_list|)
return|;
block|}
DECL|method|convertByteStringToOMRequest (ByteString byteString)
specifier|static
name|OMRequest
name|convertByteStringToOMRequest
parameter_list|(
name|ByteString
name|byteString
parameter_list|)
throws|throws
name|InvalidProtocolBufferException
block|{
name|byte
index|[]
name|bytes
init|=
name|byteString
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
name|OMRequest
operator|.
name|parseFrom
argument_list|(
name|bytes
argument_list|)
return|;
block|}
DECL|method|convertResponseToMessage (OMResponse response)
specifier|static
name|Message
name|convertResponseToMessage
parameter_list|(
name|OMResponse
name|response
parameter_list|)
block|{
name|byte
index|[]
name|requestBytes
init|=
name|response
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
name|Message
operator|.
name|valueOf
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|requestBytes
argument_list|)
argument_list|)
return|;
block|}
DECL|method|convertByteStringToOMResponse (ByteString byteString)
specifier|static
name|OMResponse
name|convertByteStringToOMResponse
parameter_list|(
name|ByteString
name|byteString
parameter_list|)
throws|throws
name|InvalidProtocolBufferException
block|{
name|byte
index|[]
name|bytes
init|=
name|byteString
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
name|OMResponse
operator|.
name|parseFrom
argument_list|(
name|bytes
argument_list|)
return|;
block|}
DECL|method|getErrorResponse (Type cmdType, Exception e)
specifier|static
name|OMResponse
name|getErrorResponse
parameter_list|(
name|Type
name|cmdType
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
return|return
name|OMResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|cmdType
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|false
argument_list|)
operator|.
name|setMessage
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|INTERNAL_ERROR
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

