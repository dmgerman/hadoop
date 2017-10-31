begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
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
name|io
operator|.
name|IOUtils
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
name|NodeId
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
name|ApplicationAttemptIdPBImpl
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
name|NodeIdPBImpl
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
name|YarnSecurityTestAMRMTokenProtos
operator|.
name|AMRMTokenIdentifierForTestProto
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
name|security
operator|.
name|AMRMTokenIdentifier
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

begin_class
DECL|class|AMRMTokenIdentifierForTest
specifier|public
class|class
name|AMRMTokenIdentifierForTest
extends|extends
name|AMRMTokenIdentifier
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMRMTokenIdentifierForTest
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
literal|"YARN_AM_RM_TOKEN"
argument_list|)
decl_stmt|;
DECL|field|proto
specifier|private
name|AMRMTokenIdentifierForTestProto
name|proto
decl_stmt|;
DECL|field|builder
specifier|private
name|AMRMTokenIdentifierForTestProto
operator|.
name|Builder
name|builder
decl_stmt|;
DECL|method|AMRMTokenIdentifierForTest ()
specifier|public
name|AMRMTokenIdentifierForTest
parameter_list|()
block|{
name|builder
operator|=
name|AMRMTokenIdentifierForTestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|AMRMTokenIdentifierForTest (AMRMTokenIdentifierForTestProto proto)
specifier|public
name|AMRMTokenIdentifierForTest
parameter_list|(
name|AMRMTokenIdentifierForTestProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
block|}
DECL|method|AMRMTokenIdentifierForTest (AMRMTokenIdentifier tokenIdentifier, String message)
specifier|public
name|AMRMTokenIdentifierForTest
parameter_list|(
name|AMRMTokenIdentifier
name|tokenIdentifier
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|builder
operator|=
name|AMRMTokenIdentifierForTestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setAppAttemptId
argument_list|(
name|tokenIdentifier
operator|.
name|getProto
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setKeyId
argument_list|(
name|tokenIdentifier
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|builder
operator|=
literal|null
expr_stmt|;
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
name|DataInputStream
name|dis
init|=
operator|(
name|DataInputStream
operator|)
name|in
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|dis
argument_list|)
decl_stmt|;
name|proto
operator|=
name|AMRMTokenIdentifierForTestProto
operator|.
name|parseFrom
argument_list|(
name|buffer
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
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getMessage
argument_list|()
return|;
block|}
DECL|method|setMessage (String message)
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|builder
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|build ()
specifier|public
name|void
name|build
parameter_list|()
block|{
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|builder
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
operator|new
name|ApplicationAttemptIdPBImpl
argument_list|(
name|proto
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getKeyId ()
specifier|public
name|int
name|getKeyId
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getKeyId
argument_list|()
return|;
block|}
DECL|method|getNewProto ()
specifier|public
name|AMRMTokenIdentifierForTestProto
name|getNewProto
parameter_list|()
block|{
return|return
name|this
operator|.
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
name|this
operator|.
name|proto
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
name|getNewProto
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
name|getNewProto
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
name|this
operator|.
name|proto
argument_list|)
return|;
block|}
block|}
end_class

end_unit

