begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.security.client
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
operator|.
name|client
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

begin_class
DECL|class|ClientTokenIdentifier
specifier|public
class|class
name|ClientTokenIdentifier
extends|extends
name|TokenIdentifier
block|{
DECL|field|KIND_NAME
specifier|public
specifier|static
specifier|final
name|Text
name|KIND_NAME
init|=
operator|new
name|Text
argument_list|(
literal|"YARN_CLIENT_TOKEN"
argument_list|)
decl_stmt|;
DECL|field|applicationAttemptId
specifier|private
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
comment|// TODO: Add more information in the tokenID such that it is not
comment|// transferrable, more secure etc.
DECL|method|ClientTokenIdentifier ()
specifier|public
name|ClientTokenIdentifier
parameter_list|()
block|{   }
DECL|method|ClientTokenIdentifier (ApplicationAttemptId id)
specifier|public
name|ClientTokenIdentifier
parameter_list|(
name|ApplicationAttemptId
name|id
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getApplicationAttemptID ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptID
parameter_list|()
block|{
return|return
name|this
operator|.
name|applicationAttemptId
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
name|out
operator|.
name|writeLong
argument_list|(
name|this
operator|.
name|applicationAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|applicationAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|applicationAttemptId
operator|.
name|getAttemptId
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
name|this
operator|.
name|applicationAttemptId
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
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
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
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
name|KIND_NAME
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
if|if
condition|(
name|this
operator|.
name|applicationAttemptId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|this
operator|.
name|applicationAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
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
name|KIND_NAME
return|;
block|}
block|}
block|}
end_class

end_unit

