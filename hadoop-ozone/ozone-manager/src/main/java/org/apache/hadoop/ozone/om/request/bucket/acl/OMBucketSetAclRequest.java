begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.bucket.acl
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
name|request
operator|.
name|bucket
operator|.
name|acl
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
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneAcl
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
name|OMMetrics
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
name|helpers
operator|.
name|OmBucketInfo
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
name|util
operator|.
name|BooleanBiFunction
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
name|om
operator|.
name|response
operator|.
name|bucket
operator|.
name|acl
operator|.
name|OMBucketAclResponse
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
name|SetAclResponse
import|;
end_import

begin_comment
comment|/**  * Handle setAcl request for bucket.  */
end_comment

begin_class
DECL|class|OMBucketSetAclRequest
specifier|public
class|class
name|OMBucketSetAclRequest
extends|extends
name|OMBucketAclRequest
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
name|OMBucketAddAclRequest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|BooleanBiFunction
argument_list|<
name|List
argument_list|<
name|OzoneAcl
argument_list|>
argument_list|,
DECL|field|bucketAddAclOp
name|OmBucketInfo
argument_list|>
name|bucketAddAclOp
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|ozoneAcls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|ozoneAcls
decl_stmt|;
static|static
block|{
name|bucketAddAclOp
operator|=
parameter_list|(
name|ozoneAcls
parameter_list|,
name|omBucketInfo
parameter_list|)
lambda|->
block|{
return|return
name|omBucketInfo
operator|.
name|setAcls
argument_list|(
name|ozoneAcls
argument_list|)
return|;
block|}
expr_stmt|;
block|}
DECL|method|OMBucketSetAclRequest (OMRequest omRequest)
specifier|public
name|OMBucketSetAclRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|super
argument_list|(
name|omRequest
argument_list|,
name|bucketAddAclOp
argument_list|)
expr_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|SetAclRequest
name|setAclRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getSetAclRequest
argument_list|()
decl_stmt|;
name|path
operator|=
name|setAclRequest
operator|.
name|getObj
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|ozoneAcls
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|setAclRequest
operator|.
name|getAclList
argument_list|()
operator|.
name|forEach
argument_list|(
name|aclInfo
lambda|->
name|ozoneAcls
operator|.
name|add
argument_list|(
name|OzoneAcl
operator|.
name|fromProtobuf
argument_list|(
name|aclInfo
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAcls ()
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAcls
parameter_list|()
block|{
return|return
name|ozoneAcls
return|;
block|}
annotation|@
name|Override
DECL|method|getPath ()
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Override
DECL|method|onInit ()
name|OMResponse
operator|.
name|Builder
name|onInit
parameter_list|()
block|{
return|return
name|OMResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|SetAcl
argument_list|)
operator|.
name|setStatus
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onSuccess (OMResponse.Builder omResponse, OmBucketInfo omBucketInfo, boolean operationResult)
name|OMClientResponse
name|onSuccess
parameter_list|(
name|OMResponse
operator|.
name|Builder
name|omResponse
parameter_list|,
name|OmBucketInfo
name|omBucketInfo
parameter_list|,
name|boolean
name|operationResult
parameter_list|)
block|{
name|omResponse
operator|.
name|setSuccess
argument_list|(
name|operationResult
argument_list|)
expr_stmt|;
name|omResponse
operator|.
name|setSetAclResponse
argument_list|(
name|SetAclResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setResponse
argument_list|(
name|operationResult
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|OMBucketAclResponse
argument_list|(
name|omBucketInfo
argument_list|,
name|omResponse
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onFailure (OMResponse.Builder omResponse, IOException exception)
name|OMClientResponse
name|onFailure
parameter_list|(
name|OMResponse
operator|.
name|Builder
name|omResponse
parameter_list|,
name|IOException
name|exception
parameter_list|)
block|{
return|return
operator|new
name|OMBucketAclResponse
argument_list|(
literal|null
argument_list|,
name|createErrorOMResponse
argument_list|(
name|omResponse
argument_list|,
name|exception
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onComplete (boolean operationResult, IOException exception, OMMetrics omMetrics)
name|void
name|onComplete
parameter_list|(
name|boolean
name|operationResult
parameter_list|,
name|IOException
name|exception
parameter_list|,
name|OMMetrics
name|omMetrics
parameter_list|)
block|{
if|if
condition|(
name|operationResult
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set acl: {} for path: {} success!"
argument_list|,
name|getAcls
argument_list|()
argument_list|,
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|omMetrics
operator|.
name|incNumBucketUpdateFails
argument_list|()
expr_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Set acl {} for path {} failed"
argument_list|,
name|getAcls
argument_list|()
argument_list|,
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Set acl {} for path {} failed!"
argument_list|,
name|getAcls
argument_list|()
argument_list|,
name|getPath
argument_list|()
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

