begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.impl.pb
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
name|nodemanager
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResource
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
name|ProtoBase
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
name|URL
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
name|LocalResourcePBImpl
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
name|URLPBImpl
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
name|exceptions
operator|.
name|YarnRemoteException
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
name|exceptions
operator|.
name|impl
operator|.
name|pb
operator|.
name|YarnRemoteExceptionPBImpl
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
name|LocalResourceProto
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
name|URLProto
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
name|YarnRemoteExceptionProto
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
name|YarnServerNodemanagerServiceProtos
operator|.
name|LocalResourceStatusProto
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
name|YarnServerNodemanagerServiceProtos
operator|.
name|LocalResourceStatusProtoOrBuilder
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
name|YarnServerNodemanagerServiceProtos
operator|.
name|ResourceStatusTypeProto
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
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|LocalResourceStatus
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
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ResourceStatusType
import|;
end_import

begin_class
DECL|class|LocalResourceStatusPBImpl
specifier|public
class|class
name|LocalResourceStatusPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|LocalResourceStatusProto
argument_list|>
implements|implements
name|LocalResourceStatus
block|{
DECL|field|proto
name|LocalResourceStatusProto
name|proto
init|=
name|LocalResourceStatusProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|LocalResourceStatusProto
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
DECL|field|resource
specifier|private
name|LocalResource
name|resource
decl_stmt|;
DECL|field|localPath
specifier|private
name|URL
name|localPath
decl_stmt|;
DECL|field|exception
specifier|private
name|YarnRemoteException
name|exception
decl_stmt|;
DECL|method|LocalResourceStatusPBImpl ()
specifier|public
name|LocalResourceStatusPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|LocalResourceStatusProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|LocalResourceStatusPBImpl (LocalResourceStatusProto proto)
specifier|public
name|LocalResourceStatusPBImpl
parameter_list|(
name|LocalResourceStatusProto
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
name|LocalResourceStatusProto
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
name|resource
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|LocalResourcePBImpl
operator|)
name|this
operator|.
name|resource
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getResource
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setResource
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|resource
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|localPath
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|URLPBImpl
operator|)
name|this
operator|.
name|localPath
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getLocalPath
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setLocalPath
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|localPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|exception
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|YarnRemoteExceptionPBImpl
operator|)
name|this
operator|.
name|exception
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getException
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setException
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|exception
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
name|LocalResourceStatusProto
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
DECL|method|getResource ()
specifier|public
name|LocalResource
name|getResource
parameter_list|()
block|{
name|LocalResourceStatusProtoOrBuilder
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
name|resource
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|resource
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasResource
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|resource
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|resource
return|;
block|}
annotation|@
name|Override
DECL|method|getStatus ()
specifier|public
name|ResourceStatusType
name|getStatus
parameter_list|()
block|{
name|LocalResourceStatusProtoOrBuilder
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
name|hasStatus
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
name|getStatus
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalPath ()
specifier|public
name|URL
name|getLocalPath
parameter_list|()
block|{
name|LocalResourceStatusProtoOrBuilder
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
name|localPath
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|localPath
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasLocalPath
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|localPath
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getLocalPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|localPath
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalSize ()
specifier|public
name|long
name|getLocalSize
parameter_list|()
block|{
name|LocalResourceStatusProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
operator|(
name|p
operator|.
name|getLocalSize
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getException ()
specifier|public
name|YarnRemoteException
name|getException
parameter_list|()
block|{
name|LocalResourceStatusProtoOrBuilder
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
name|exception
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|exception
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasException
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|exception
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|exception
return|;
block|}
annotation|@
name|Override
DECL|method|setResource (LocalResource resource)
specifier|public
name|void
name|setResource
parameter_list|(
name|LocalResource
name|resource
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearResource
argument_list|()
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setStatus (ResourceStatusType status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|ResourceStatusType
name|status
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearStatus
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setStatus
argument_list|(
name|convertToProtoFormat
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLocalPath (URL localPath)
specifier|public
name|void
name|setLocalPath
parameter_list|(
name|URL
name|localPath
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|localPath
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearLocalPath
argument_list|()
expr_stmt|;
name|this
operator|.
name|localPath
operator|=
name|localPath
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLocalSize (long size)
specifier|public
name|void
name|setLocalSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setLocalSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setException (YarnRemoteException exception)
specifier|public
name|void
name|setException
parameter_list|(
name|YarnRemoteException
name|exception
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearException
argument_list|()
expr_stmt|;
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
DECL|method|convertToProtoFormat (LocalResource rsrc)
specifier|private
name|LocalResourceProto
name|convertToProtoFormat
parameter_list|(
name|LocalResource
name|rsrc
parameter_list|)
block|{
return|return
operator|(
operator|(
name|LocalResourcePBImpl
operator|)
name|rsrc
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (LocalResourceProto rsrc)
specifier|private
name|LocalResourcePBImpl
name|convertFromProtoFormat
parameter_list|(
name|LocalResourceProto
name|rsrc
parameter_list|)
block|{
return|return
operator|new
name|LocalResourcePBImpl
argument_list|(
name|rsrc
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (URLProto p)
specifier|private
name|URLPBImpl
name|convertFromProtoFormat
parameter_list|(
name|URLProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|URLPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (URL t)
specifier|private
name|URLProto
name|convertToProtoFormat
parameter_list|(
name|URL
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|URLPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertToProtoFormat (ResourceStatusType e)
specifier|private
name|ResourceStatusTypeProto
name|convertToProtoFormat
parameter_list|(
name|ResourceStatusType
name|e
parameter_list|)
block|{
return|return
name|ResourceStatusTypeProto
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (ResourceStatusTypeProto e)
specifier|private
name|ResourceStatusType
name|convertFromProtoFormat
parameter_list|(
name|ResourceStatusTypeProto
name|e
parameter_list|)
block|{
return|return
name|ResourceStatusType
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (YarnRemoteExceptionProto p)
specifier|private
name|YarnRemoteExceptionPBImpl
name|convertFromProtoFormat
parameter_list|(
name|YarnRemoteExceptionProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|YarnRemoteExceptionPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (YarnRemoteException t)
specifier|private
name|YarnRemoteExceptionProto
name|convertToProtoFormat
parameter_list|(
name|YarnRemoteException
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|YarnRemoteExceptionPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

