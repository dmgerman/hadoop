begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|Public
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
name|Evolving
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
name|io
operator|.
name|Text
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
name|UserGroupInformation
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
name|token
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|ContainerId
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
name|LogAggregationContext
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
name|Priority
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
name|Resource
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
name|ContainerIdPBImpl
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
name|LogAggregationContextPBImpl
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
name|PriorityPBImpl
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
name|ResourcePBImpl
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
name|YarnSecurityTokenProtos
operator|.
name|ContainerTokenIdentifierProto
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
comment|/**  * TokenIdentifier for a container. Encodes {@link ContainerId},  * {@link Resource} needed by the container and the target NMs host-address.  *   */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|ContainerTokenIdentifier
specifier|public
class|class
name|ContainerTokenIdentifier
extends|extends
name|TokenIdentifier
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ContainerTokenIdentifier
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|KIND
specifier|public
specifier|static
specifier|final
name|Text
name|KIND
init|=
operator|new
name|Text
argument_list|(
literal|"ContainerToken"
argument_list|)
decl_stmt|;
DECL|field|proto
specifier|private
name|ContainerTokenIdentifierProto
name|proto
decl_stmt|;
DECL|method|ContainerTokenIdentifier (ContainerId containerID, String hostName, String appSubmitter, Resource r, long expiryTimeStamp, int masterKeyId, long rmIdentifier, Priority priority, long creationTime)
specifier|public
name|ContainerTokenIdentifier
parameter_list|(
name|ContainerId
name|containerID
parameter_list|,
name|String
name|hostName
parameter_list|,
name|String
name|appSubmitter
parameter_list|,
name|Resource
name|r
parameter_list|,
name|long
name|expiryTimeStamp
parameter_list|,
name|int
name|masterKeyId
parameter_list|,
name|long
name|rmIdentifier
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|creationTime
parameter_list|)
block|{
name|this
argument_list|(
name|containerID
argument_list|,
name|hostName
argument_list|,
name|appSubmitter
argument_list|,
name|r
argument_list|,
name|expiryTimeStamp
argument_list|,
name|masterKeyId
argument_list|,
name|rmIdentifier
argument_list|,
name|priority
argument_list|,
name|creationTime
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ContainerTokenIdentifier (ContainerId containerID, String hostName, String appSubmitter, Resource r, long expiryTimeStamp, int masterKeyId, long rmIdentifier, Priority priority, long creationTime, LogAggregationContext logAggregationContext)
specifier|public
name|ContainerTokenIdentifier
parameter_list|(
name|ContainerId
name|containerID
parameter_list|,
name|String
name|hostName
parameter_list|,
name|String
name|appSubmitter
parameter_list|,
name|Resource
name|r
parameter_list|,
name|long
name|expiryTimeStamp
parameter_list|,
name|int
name|masterKeyId
parameter_list|,
name|long
name|rmIdentifier
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|LogAggregationContext
name|logAggregationContext
parameter_list|)
block|{
name|ContainerTokenIdentifierProto
operator|.
name|Builder
name|builder
init|=
name|ContainerTokenIdentifierProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerID
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerId
argument_list|(
operator|(
operator|(
name|ContainerIdPBImpl
operator|)
name|containerID
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setNmHostAddr
argument_list|(
name|hostName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setAppSubmitter
argument_list|(
name|appSubmitter
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setResource
argument_list|(
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|r
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setExpiryTimeStamp
argument_list|(
name|expiryTimeStamp
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMasterKeyId
argument_list|(
name|masterKeyId
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setRmIdentifier
argument_list|(
name|rmIdentifier
argument_list|)
expr_stmt|;
if|if
condition|(
name|priority
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setPriority
argument_list|(
operator|(
operator|(
name|PriorityPBImpl
operator|)
name|priority
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setCreationTime
argument_list|(
name|creationTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|logAggregationContext
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setLogAggregationContext
argument_list|(
operator|(
operator|(
name|LogAggregationContextPBImpl
operator|)
name|logAggregationContext
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Default constructor needed by RPC layer/SecretManager.    */
DECL|method|ContainerTokenIdentifier ()
specifier|public
name|ContainerTokenIdentifier
parameter_list|()
block|{   }
DECL|method|getContainerID ()
specifier|public
name|ContainerId
name|getContainerID
parameter_list|()
block|{
if|if
condition|(
operator|!
name|proto
operator|.
name|hasContainerId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ContainerIdPBImpl
argument_list|(
name|proto
operator|.
name|getContainerId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getApplicationSubmitter ()
specifier|public
name|String
name|getApplicationSubmitter
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getAppSubmitter
argument_list|()
return|;
block|}
DECL|method|getNmHostAddress ()
specifier|public
name|String
name|getNmHostAddress
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getNmHostAddr
argument_list|()
return|;
block|}
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
if|if
condition|(
operator|!
name|proto
operator|.
name|hasResource
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ResourcePBImpl
argument_list|(
name|proto
operator|.
name|getResource
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getExpiryTimeStamp ()
specifier|public
name|long
name|getExpiryTimeStamp
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getExpiryTimeStamp
argument_list|()
return|;
block|}
DECL|method|getMasterKeyId ()
specifier|public
name|int
name|getMasterKeyId
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getMasterKeyId
argument_list|()
return|;
block|}
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
if|if
condition|(
operator|!
name|proto
operator|.
name|hasPriority
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|PriorityPBImpl
argument_list|(
name|proto
operator|.
name|getPriority
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getCreationTime ()
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getCreationTime
argument_list|()
return|;
block|}
comment|/**    * Get the RMIdentifier of RM in which containers are allocated    * @return RMIdentifier    */
DECL|method|getRMIdentifier ()
specifier|public
name|long
name|getRMIdentifier
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getRmIdentifier
argument_list|()
return|;
block|}
DECL|method|getProto ()
specifier|public
name|ContainerTokenIdentifierProto
name|getProto
parameter_list|()
block|{
return|return
name|proto
return|;
block|}
DECL|method|getLogAggregationContext ()
specifier|public
name|LogAggregationContext
name|getLogAggregationContext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|proto
operator|.
name|hasLogAggregationContext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|LogAggregationContextPBImpl
argument_list|(
name|proto
operator|.
name|getLogAggregationContext
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing ContainerTokenIdentifier to RPC layer: "
operator|+
name|this
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|proto
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|proto
operator|=
name|ContainerTokenIdentifierProto
operator|.
name|parseFrom
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|KIND
return|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|UserGroupInformation
name|getUser
parameter_list|()
block|{
name|String
name|containerId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|hasContainerId
argument_list|()
condition|)
block|{
name|containerId
operator|=
operator|new
name|ContainerIdPBImpl
argument_list|(
name|proto
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|containerId
argument_list|)
return|;
block|}
comment|// TODO: Needed?
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Renewer
specifier|public
specifier|static
class|class
name|Renewer
extends|extends
name|Token
operator|.
name|TrivialRenewer
block|{
annotation|@
name|Override
DECL|method|getKind ()
specifier|protected
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|KIND
return|;
block|}
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
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

