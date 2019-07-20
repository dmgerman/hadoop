begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
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
name|ozone
operator|.
name|om
operator|.
name|OzoneManager
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
name|ratis
operator|.
name|OzoneManagerDoubleBuffer
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
name|ratis
operator|.
name|utils
operator|.
name|OzoneManagerRatisUtils
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
name|request
operator|.
name|OMClientRequest
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
name|response
operator|.
name|OMClientResponse
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

begin_comment
comment|/**  * Command Handler for OM requests. OM State Machine calls this handler for  * deserializing the client request and sending it to OM.  */
end_comment

begin_class
DECL|class|OzoneManagerHARequestHandlerImpl
specifier|public
class|class
name|OzoneManagerHARequestHandlerImpl
extends|extends
name|OzoneManagerRequestHandler
implements|implements
name|OzoneManagerHARequestHandler
block|{
DECL|field|ozoneManagerDoubleBuffer
specifier|private
name|OzoneManagerDoubleBuffer
name|ozoneManagerDoubleBuffer
decl_stmt|;
DECL|method|OzoneManagerHARequestHandlerImpl (OzoneManager om, OzoneManagerDoubleBuffer ozoneManagerDoubleBuffer)
specifier|public
name|OzoneManagerHARequestHandlerImpl
parameter_list|(
name|OzoneManager
name|om
parameter_list|,
name|OzoneManagerDoubleBuffer
name|ozoneManagerDoubleBuffer
parameter_list|)
block|{
name|super
argument_list|(
name|om
argument_list|)
expr_stmt|;
name|this
operator|.
name|ozoneManagerDoubleBuffer
operator|=
name|ozoneManagerDoubleBuffer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleApplyTransaction (OMRequest omRequest, long transactionLogIndex)
specifier|public
name|OMResponse
name|handleApplyTransaction
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received OMRequest: {}, "
argument_list|,
name|omRequest
argument_list|)
expr_stmt|;
name|Type
name|cmdType
init|=
name|omRequest
operator|.
name|getCmdType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|cmdType
condition|)
block|{
case|case
name|CreateVolume
case|:
case|case
name|SetVolumeProperty
case|:
case|case
name|DeleteVolume
case|:
case|case
name|CreateBucket
case|:
case|case
name|DeleteBucket
case|:
case|case
name|SetBucketProperty
case|:
case|case
name|AllocateBlock
case|:
case|case
name|CreateKey
case|:
case|case
name|CommitKey
case|:
case|case
name|DeleteKey
case|:
case|case
name|RenameKey
case|:
case|case
name|CreateDirectory
case|:
case|case
name|CreateFile
case|:
case|case
name|PurgeKeys
case|:
case|case
name|CreateS3Bucket
case|:
case|case
name|DeleteS3Bucket
case|:
case|case
name|InitiateMultiPartUpload
case|:
comment|//TODO: We don't need to pass transactionID, this will be removed when
comment|// complete write requests is changed to new model. And also we can
comment|// return OMClientResponse, then adding to doubleBuffer can be taken
comment|// care by stateMachine. And also integrate both HA and NON HA code
comment|// paths.
name|OMClientRequest
name|omClientRequest
init|=
name|OzoneManagerRatisUtils
operator|.
name|createClientRequest
argument_list|(
name|omRequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omClientRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|getOzoneManager
argument_list|()
argument_list|,
name|transactionLogIndex
argument_list|)
decl_stmt|;
comment|// If any error we have got when validateAndUpdateCache, OMResponse
comment|// Status is set with Error Code other than OK, in that case don't
comment|// add this to double buffer.
if|if
condition|(
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|OK
condition|)
block|{
name|ozoneManagerDoubleBuffer
operator|.
name|add
argument_list|(
name|omClientResponse
argument_list|,
name|transactionLogIndex
argument_list|)
expr_stmt|;
block|}
return|return
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
return|;
default|default:
comment|// As all request types are not changed so we need to call handle
comment|// here.
return|return
name|handle
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

