begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|NodeId
import|;
end_import

begin_class
DECL|class|NMTokenIdentifier
specifier|public
class|class
name|NMTokenIdentifier
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
name|NMTokenIdentifier
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
literal|"NMToken"
argument_list|)
decl_stmt|;
DECL|field|appAttemptId
specifier|private
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|appSubmitter
specifier|private
name|String
name|appSubmitter
decl_stmt|;
DECL|field|masterKeyId
specifier|private
name|int
name|masterKeyId
decl_stmt|;
DECL|method|NMTokenIdentifier (ApplicationAttemptId appAttemptId, NodeId nodeId, String applicationSubmitter, int masterKeyId)
specifier|public
name|NMTokenIdentifier
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|applicationSubmitter
parameter_list|,
name|int
name|masterKeyId
parameter_list|)
block|{
name|this
operator|.
name|appAttemptId
operator|=
name|appAttemptId
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|appSubmitter
operator|=
name|applicationSubmitter
expr_stmt|;
name|this
operator|.
name|masterKeyId
operator|=
name|masterKeyId
expr_stmt|;
block|}
comment|/**    * Default constructor needed by RPC/Secret manager    */
DECL|method|NMTokenIdentifier ()
specifier|public
name|NMTokenIdentifier
parameter_list|()
block|{   }
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|appAttemptId
return|;
block|}
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
DECL|method|getApplicationSubmitter ()
specifier|public
name|String
name|getApplicationSubmitter
parameter_list|()
block|{
return|return
name|appSubmitter
return|;
block|}
DECL|method|getMastKeyId ()
specifier|public
name|int
name|getMastKeyId
parameter_list|()
block|{
return|return
name|masterKeyId
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
literal|"Writing NMTokenIdentifier to RPC layer: "
operator|+
name|this
argument_list|)
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|appAttemptId
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
name|appAttemptId
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|this
operator|.
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|this
operator|.
name|appSubmitter
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|masterKeyId
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
name|appAttemptId
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
name|String
index|[]
name|hostAddr
init|=
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|nodeId
operator|=
name|NodeId
operator|.
name|newInstance
argument_list|(
name|hostAddr
index|[
literal|0
index|]
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|hostAddr
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|appSubmitter
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|masterKeyId
operator|=
name|in
operator|.
name|readInt
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
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

