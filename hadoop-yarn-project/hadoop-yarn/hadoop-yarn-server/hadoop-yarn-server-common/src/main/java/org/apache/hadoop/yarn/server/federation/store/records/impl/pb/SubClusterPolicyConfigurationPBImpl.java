begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records.impl.pb
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
name|federation
operator|.
name|store
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
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterPolicyConfigurationProto
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
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterPolicyConfigurationProtoOrBuilder
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterPolicyConfiguration
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

begin_comment
comment|/**  * Protobuf based implementation of {@link SubClusterPolicyConfiguration}.  *  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SubClusterPolicyConfigurationPBImpl
specifier|public
class|class
name|SubClusterPolicyConfigurationPBImpl
extends|extends
name|SubClusterPolicyConfiguration
block|{
DECL|field|proto
specifier|private
name|SubClusterPolicyConfigurationProto
name|proto
init|=
name|SubClusterPolicyConfigurationProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|SubClusterPolicyConfigurationProto
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
DECL|method|SubClusterPolicyConfigurationPBImpl ()
specifier|public
name|SubClusterPolicyConfigurationPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|SubClusterPolicyConfigurationProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|SubClusterPolicyConfigurationPBImpl ( SubClusterPolicyConfigurationProto proto)
specifier|public
name|SubClusterPolicyConfigurationPBImpl
parameter_list|(
name|SubClusterPolicyConfigurationProto
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
name|SubClusterPolicyConfigurationProto
name|getProto
parameter_list|()
block|{
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
name|SubClusterPolicyConfigurationProto
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
block|{
return|return
literal|false
return|;
block|}
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
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
name|SubClusterPolicyConfigurationProtoOrBuilder
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
name|getQueue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setQueue (String queueName)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|queueName
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearType
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setQueue
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
name|SubClusterPolicyConfigurationProtoOrBuilder
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
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setType (String policyType)
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|policyType
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|policyType
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearType
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setType
argument_list|(
name|policyType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams ()
specifier|public
name|ByteBuffer
name|getParams
parameter_list|()
block|{
name|SubClusterPolicyConfigurationProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getParams
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setParams (ByteBuffer policyParams)
specifier|public
name|void
name|setParams
parameter_list|(
name|ByteBuffer
name|policyParams
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|policyParams
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearParams
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setParams
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|policyParams
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

