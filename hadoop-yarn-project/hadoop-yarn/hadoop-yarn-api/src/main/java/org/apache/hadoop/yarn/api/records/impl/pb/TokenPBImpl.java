begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.impl.pb
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
name|nio
operator|.
name|ByteBuffer
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
name|security
operator|.
name|proto
operator|.
name|SecurityProtos
operator|.
name|TokenProtoOrBuilder
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TokenPBImpl
specifier|public
class|class
name|TokenPBImpl
extends|extends
name|Token
block|{
DECL|field|proto
specifier|private
name|TokenProto
name|proto
init|=
name|TokenProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|TokenProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
specifier|private
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|field|identifier
specifier|private
name|ByteBuffer
name|identifier
decl_stmt|;
DECL|field|password
specifier|private
name|ByteBuffer
name|password
decl_stmt|;
DECL|method|TokenPBImpl ()
specifier|public
name|TokenPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|TokenProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|TokenPBImpl (TokenProto proto)
specifier|public
name|TokenPBImpl
parameter_list|(
name|TokenProto
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
specifier|synchronized
name|TokenProto
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
DECL|method|convertFromProtoFormat (ByteString byteString)
specifier|protected
specifier|final
name|ByteBuffer
name|convertFromProtoFormat
parameter_list|(
name|ByteString
name|byteString
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|byteString
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ByteBuffer byteBuffer)
specifier|protected
specifier|final
name|ByteString
name|convertToProtoFormat
parameter_list|(
name|ByteBuffer
name|byteBuffer
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|byteBuffer
argument_list|)
return|;
block|}
DECL|method|mergeLocalToBuilder ()
specifier|private
specifier|synchronized
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|identifier
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setIdentifier
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|password
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setPassword
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|password
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
specifier|synchronized
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
specifier|synchronized
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
name|TokenProto
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
DECL|method|getIdentifier ()
specifier|public
specifier|synchronized
name|ByteBuffer
name|getIdentifier
parameter_list|()
block|{
name|TokenProtoOrBuilder
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
name|identifier
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|identifier
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasIdentifier
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|identifier
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|identifier
return|;
block|}
annotation|@
name|Override
DECL|method|setIdentifier (ByteBuffer identifier)
specifier|public
specifier|synchronized
name|void
name|setIdentifier
parameter_list|(
name|ByteBuffer
name|identifier
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|identifier
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearIdentifier
argument_list|()
expr_stmt|;
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPassword ()
specifier|public
specifier|synchronized
name|ByteBuffer
name|getPassword
parameter_list|()
block|{
name|TokenProtoOrBuilder
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
name|password
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|password
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasPassword
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|password
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|password
return|;
block|}
annotation|@
name|Override
DECL|method|setPassword (ByteBuffer password)
specifier|public
specifier|synchronized
name|void
name|setPassword
parameter_list|(
name|ByteBuffer
name|password
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|password
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearPassword
argument_list|()
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
specifier|synchronized
name|String
name|getKind
parameter_list|()
block|{
name|TokenProtoOrBuilder
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
name|hasKind
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
name|getKind
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setKind (String kind)
specifier|public
specifier|synchronized
name|void
name|setKind
parameter_list|(
name|String
name|kind
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|kind
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearKind
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setKind
argument_list|(
operator|(
name|kind
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getService ()
specifier|public
specifier|synchronized
name|String
name|getService
parameter_list|()
block|{
name|TokenProtoOrBuilder
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
name|hasService
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
name|getService
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setService (String service)
specifier|public
specifier|synchronized
name|void
name|setService
parameter_list|(
name|String
name|service
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearService
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setService
argument_list|(
operator|(
name|service
operator|)
argument_list|)
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Token { "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"kind: "
argument_list|)
operator|.
name|append
argument_list|(
name|getKind
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"service: "
argument_list|)
operator|.
name|append
argument_list|(
name|getService
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

