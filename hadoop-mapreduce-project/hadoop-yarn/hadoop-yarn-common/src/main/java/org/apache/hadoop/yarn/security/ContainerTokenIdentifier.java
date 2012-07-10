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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|util
operator|.
name|BuilderUtils
import|;
end_import

begin_comment
comment|/**  * TokenIdentifier for a container. Encodes {@link ContainerId},  * {@link Resource} needed by the container and the target NMs host-address.  *   */
end_comment

begin_class
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
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|nmHostAddr
specifier|private
name|String
name|nmHostAddr
decl_stmt|;
DECL|field|resource
specifier|private
name|Resource
name|resource
decl_stmt|;
DECL|field|expiryTimeStamp
specifier|private
name|long
name|expiryTimeStamp
decl_stmt|;
DECL|method|ContainerTokenIdentifier (ContainerId containerID, String hostName, Resource r, long expiryTimeStamp)
specifier|public
name|ContainerTokenIdentifier
parameter_list|(
name|ContainerId
name|containerID
parameter_list|,
name|String
name|hostName
parameter_list|,
name|Resource
name|r
parameter_list|,
name|long
name|expiryTimeStamp
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerID
expr_stmt|;
name|this
operator|.
name|nmHostAddr
operator|=
name|hostName
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|r
expr_stmt|;
name|this
operator|.
name|expiryTimeStamp
operator|=
name|expiryTimeStamp
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
return|return
name|this
operator|.
name|containerId
return|;
block|}
DECL|method|getNmHostAddress ()
specifier|public
name|String
name|getNmHostAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|nmHostAddr
return|;
block|}
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
return|return
name|this
operator|.
name|resource
return|;
block|}
DECL|method|getExpiryTimeStamp ()
specifier|public
name|long
name|getExpiryTimeStamp
parameter_list|()
block|{
return|return
name|this
operator|.
name|expiryTimeStamp
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
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|this
operator|.
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|applicationAttemptId
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|applicationId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|applicationId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|applicationAttemptId
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|containerId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|this
operator|.
name|nmHostAddr
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|resource
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|this
operator|.
name|expiryTimeStamp
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
name|ApplicationId
name|applicationId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|applicationId
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|containerId
operator|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|applicationAttemptId
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nmHostAddr
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|expiryTimeStamp
operator|=
name|in
operator|.
name|readLong
argument_list|()
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
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|this
operator|.
name|containerId
operator|.
name|toString
argument_list|()
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
block|}
end_class

end_unit

