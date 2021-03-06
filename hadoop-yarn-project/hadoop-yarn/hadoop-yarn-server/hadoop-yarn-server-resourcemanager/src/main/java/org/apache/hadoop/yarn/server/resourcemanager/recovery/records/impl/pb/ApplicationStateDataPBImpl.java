begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
package|;
end_package

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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|CallerContext
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
name|protobuf
operator|.
name|RpcHeaderProtos
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationSubmissionContext
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationTimeoutType
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ApplicationSubmissionContextPBImpl
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ProtoUtils
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|ApplicationTimeoutMapProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|ApplicationStateDataProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|ApplicationStateDataProtoOrBuilder
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|RMAppStateProto
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|ApplicationStateData
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppState
import|;
end_import

begin_import
import|import
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
import|;
end_import

begin_class
DECL|class|ApplicationStateDataPBImpl
specifier|public
class|class
name|ApplicationStateDataPBImpl
extends|extends
name|ApplicationStateData
block|{
DECL|field|proto
name|ApplicationStateDataProto
name|proto
init|=
name|ApplicationStateDataProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ApplicationStateDataProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|field|applicationSubmissionContext
specifier|private
name|ApplicationSubmissionContext
name|applicationSubmissionContext
init|=
literal|null
decl_stmt|;
DECL|field|applicationTimeouts
specifier|private
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|Long
argument_list|>
name|applicationTimeouts
init|=
literal|null
decl_stmt|;
DECL|method|ApplicationStateDataPBImpl ()
specifier|public
name|ApplicationStateDataPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ApplicationStateDataProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ApplicationStateDataPBImpl ( ApplicationStateDataProto proto)
specifier|public
name|ApplicationStateDataPBImpl
parameter_list|(
name|ApplicationStateDataProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|ApplicationStateDataProto
name|getProto
parameter_list|()
block|{
name|mergeLocalToProto
argument_list|()
expr_stmt|;
name|proto
operator|=
name|viaProto
condition|?
name|proto
else|:
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
return|return
name|proto
return|;
block|}
DECL|method|mergeLocalToBuilder ()
specifier|private
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationSubmissionContext
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setApplicationSubmissionContext
argument_list|(
operator|(
operator|(
name|ApplicationSubmissionContextPBImpl
operator|)
name|applicationSubmissionContext
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|applicationTimeouts
operator|!=
literal|null
condition|)
block|{
name|addApplicationTimeouts
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
name|void
name|mergeLocalToProto
parameter_list|()
block|{
if|if
condition|(
name|viaProto
condition|)
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|mergeLocalToBuilder
argument_list|()
expr_stmt|;
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|maybeInitBuilder ()
specifier|private
name|void
name|maybeInitBuilder
parameter_list|()
block|{
if|if
condition|(
name|viaProto
operator|||
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
name|ApplicationStateDataProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
name|viaProto
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubmitTime ()
specifier|public
name|long
name|getSubmitTime
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|hasSubmitTime
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getSubmitTime
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setSubmitTime (long submitTime)
specifier|public
name|void
name|setSubmitTime
parameter_list|(
name|long
name|submitTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setSubmitTime
argument_list|(
name|submitTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getStartTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setStartTime (long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLaunchTime ()
specifier|public
name|long
name|getLaunchTime
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getLaunchTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setLaunchTime (long launchTime)
specifier|public
name|void
name|setLaunchTime
parameter_list|(
name|long
name|launchTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setLaunchTime
argument_list|(
name|launchTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|hasUser
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getUser
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationSubmissionContext ()
specifier|public
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
name|applicationSubmissionContext
operator|!=
literal|null
condition|)
block|{
return|return
name|applicationSubmissionContext
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasApplicationSubmissionContext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|applicationSubmissionContext
operator|=
operator|new
name|ApplicationSubmissionContextPBImpl
argument_list|(
name|p
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|applicationSubmissionContext
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationSubmissionContext ( ApplicationSubmissionContext context)
specifier|public
name|void
name|setApplicationSubmissionContext
parameter_list|(
name|ApplicationSubmissionContext
name|context
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplicationSubmissionContext
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|applicationSubmissionContext
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|RMAppState
name|getState
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|hasApplicationState
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getApplicationState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setState (RMAppState finalState)
specifier|public
name|void
name|setState
parameter_list|(
name|RMAppState
name|finalState
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|finalState
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplicationState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setApplicationState
argument_list|(
name|convertToProtoFormat
argument_list|(
name|finalState
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|hasDiagnostics
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getDiagnostics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setDiagnostics (String diagnostics)
specifier|public
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|diagnostics
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearDiagnostics
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getFinishTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setFinishTime (long finishTime)
specifier|public
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|getProto
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getCallerContext ()
specifier|public
name|CallerContext
name|getCallerContext
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|RpcHeaderProtos
operator|.
name|RPCCallerContextProto
name|pbContext
init|=
name|p
operator|.
name|getCallerContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|pbContext
operator|!=
literal|null
condition|)
block|{
name|CallerContext
name|context
init|=
operator|new
name|CallerContext
operator|.
name|Builder
argument_list|(
name|pbContext
operator|.
name|getContext
argument_list|()
argument_list|)
operator|.
name|setSignature
argument_list|(
name|pbContext
operator|.
name|getSignature
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|context
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setCallerContext (CallerContext callerContext)
specifier|public
name|void
name|setCallerContext
parameter_list|(
name|CallerContext
name|callerContext
parameter_list|)
block|{
if|if
condition|(
name|callerContext
operator|!=
literal|null
condition|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|RpcHeaderProtos
operator|.
name|RPCCallerContextProto
operator|.
name|Builder
name|b
init|=
name|RpcHeaderProtos
operator|.
name|RPCCallerContextProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|callerContext
operator|.
name|getContext
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|setContext
argument_list|(
name|callerContext
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|callerContext
operator|.
name|getSignature
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|setSignature
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|callerContext
operator|.
name|getSignature
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setCallerContext
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
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
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
return|;
block|}
DECL|field|RM_APP_PREFIX
specifier|private
specifier|static
name|String
name|RM_APP_PREFIX
init|=
literal|"RMAPP_"
decl_stmt|;
DECL|method|convertToProtoFormat (RMAppState e)
specifier|public
specifier|static
name|RMAppStateProto
name|convertToProtoFormat
parameter_list|(
name|RMAppState
name|e
parameter_list|)
block|{
return|return
name|RMAppStateProto
operator|.
name|valueOf
argument_list|(
name|RM_APP_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (RMAppStateProto e)
specifier|public
specifier|static
name|RMAppState
name|convertFromProtoFormat
parameter_list|(
name|RMAppStateProto
name|e
parameter_list|)
block|{
return|return
name|RMAppState
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|RM_APP_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationTimeouts ()
specifier|public
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|Long
argument_list|>
name|getApplicationTimeouts
parameter_list|()
block|{
name|initApplicationTimeout
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationTimeouts
return|;
block|}
DECL|method|initApplicationTimeout ()
specifier|private
name|void
name|initApplicationTimeout
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationTimeouts
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ApplicationStateDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|ApplicationTimeoutMapProto
argument_list|>
name|lists
init|=
name|p
operator|.
name|getApplicationTimeoutsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|applicationTimeouts
operator|=
operator|new
name|HashMap
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|Long
argument_list|>
argument_list|(
name|lists
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ApplicationTimeoutMapProto
name|timeoutProto
range|:
name|lists
control|)
block|{
name|this
operator|.
name|applicationTimeouts
operator|.
name|put
argument_list|(
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|timeoutProto
operator|.
name|getApplicationTimeoutType
argument_list|()
argument_list|)
argument_list|,
name|timeoutProto
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setApplicationTimeouts ( Map<ApplicationTimeoutType, Long> appTimeouts)
specifier|public
name|void
name|setApplicationTimeouts
parameter_list|(
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|Long
argument_list|>
name|appTimeouts
parameter_list|)
block|{
if|if
condition|(
name|appTimeouts
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|initApplicationTimeout
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationTimeouts
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationTimeouts
operator|.
name|putAll
argument_list|(
name|appTimeouts
argument_list|)
expr_stmt|;
block|}
DECL|method|addApplicationTimeouts ()
specifier|private
name|void
name|addApplicationTimeouts
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearApplicationTimeouts
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationTimeouts
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|?
extends|extends
name|ApplicationTimeoutMapProto
argument_list|>
name|values
init|=
operator|new
name|Iterable
argument_list|<
name|ApplicationTimeoutMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ApplicationTimeoutMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ApplicationTimeoutMapProto
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|ApplicationTimeoutType
argument_list|>
name|iterator
init|=
name|applicationTimeouts
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ApplicationTimeoutMapProto
name|next
parameter_list|()
block|{
name|ApplicationTimeoutType
name|key
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|ApplicationTimeoutMapProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTimeout
argument_list|(
name|applicationTimeouts
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|setApplicationTimeoutType
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|this
operator|.
name|builder
operator|.
name|addAllApplicationTimeouts
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

