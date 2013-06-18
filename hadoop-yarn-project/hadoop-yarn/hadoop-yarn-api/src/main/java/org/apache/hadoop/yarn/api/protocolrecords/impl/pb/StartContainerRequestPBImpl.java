begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords.impl.pb
package|package
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|security
operator|.
name|proto
operator|.
name|SecurityProtos
operator|.
name|TokenProto
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
name|protocolrecords
operator|.
name|StartContainerRequest
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
name|ContainerLaunchContext
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
name|Token
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
name|ContainerLaunchContextPBImpl
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
name|TokenPBImpl
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
name|ContainerLaunchContextProto
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
name|YarnServiceProtos
operator|.
name|StartContainerRequestProto
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
name|YarnServiceProtos
operator|.
name|StartContainerRequestProtoOrBuilder
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|StartContainerRequestPBImpl
specifier|public
class|class
name|StartContainerRequestPBImpl
extends|extends
name|StartContainerRequest
block|{
DECL|field|proto
name|StartContainerRequestProto
name|proto
init|=
name|StartContainerRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|StartContainerRequestProto
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
DECL|field|containerLaunchContext
specifier|private
name|ContainerLaunchContext
name|containerLaunchContext
init|=
literal|null
decl_stmt|;
DECL|field|containerToken
specifier|private
name|Token
name|containerToken
init|=
literal|null
decl_stmt|;
DECL|method|StartContainerRequestPBImpl ()
specifier|public
name|StartContainerRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|StartContainerRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|StartContainerRequestPBImpl (StartContainerRequestProto proto)
specifier|public
name|StartContainerRequestPBImpl
parameter_list|(
name|StartContainerRequestProto
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
DECL|method|getProto ()
specifier|public
name|StartContainerRequestProto
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
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|", "
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
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
name|containerLaunchContext
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerLaunchContext
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|containerLaunchContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|containerToken
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerToken
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|containerToken
argument_list|)
argument_list|)
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
name|StartContainerRequestProto
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
DECL|method|getContainerLaunchContext ()
specifier|public
name|ContainerLaunchContext
name|getContainerLaunchContext
parameter_list|()
block|{
name|StartContainerRequestProtoOrBuilder
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
name|this
operator|.
name|containerLaunchContext
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|containerLaunchContext
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasContainerLaunchContext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|containerLaunchContext
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getContainerLaunchContext
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|containerLaunchContext
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerLaunchContext (ContainerLaunchContext containerLaunchContext)
specifier|public
name|void
name|setContainerLaunchContext
parameter_list|(
name|ContainerLaunchContext
name|containerLaunchContext
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|containerLaunchContext
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearContainerLaunchContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerLaunchContext
operator|=
name|containerLaunchContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainerToken ()
specifier|public
name|Token
name|getContainerToken
parameter_list|()
block|{
name|StartContainerRequestProtoOrBuilder
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
name|this
operator|.
name|containerToken
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|containerToken
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasContainerToken
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|containerToken
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|containerToken
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerToken (Token containerToken)
specifier|public
name|void
name|setContainerToken
parameter_list|(
name|Token
name|containerToken
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|containerToken
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearContainerToken
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|containerToken
operator|=
name|containerToken
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ContainerLaunchContextProto p)
specifier|private
name|ContainerLaunchContextPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerLaunchContextProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerLaunchContextPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ContainerLaunchContext t)
specifier|private
name|ContainerLaunchContextProto
name|convertToProtoFormat
parameter_list|(
name|ContainerLaunchContext
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerLaunchContextPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (TokenProto containerProto)
specifier|private
name|TokenPBImpl
name|convertFromProtoFormat
parameter_list|(
name|TokenProto
name|containerProto
parameter_list|)
block|{
return|return
operator|new
name|TokenPBImpl
argument_list|(
name|containerProto
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Token container)
specifier|private
name|TokenProto
name|convertToProtoFormat
parameter_list|(
name|Token
name|container
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TokenPBImpl
operator|)
name|container
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

