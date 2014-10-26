begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery.records
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|RMDelegationTokenIdentifierDataProto
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
name|client
operator|.
name|RMDelegationTokenIdentifier
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
name|client
operator|.
name|YARNDelegationTokenIdentifier
import|;
end_import

begin_class
DECL|class|RMDelegationTokenIdentifierData
specifier|public
class|class
name|RMDelegationTokenIdentifierData
block|{
DECL|field|builder
name|RMDelegationTokenIdentifierDataProto
operator|.
name|Builder
name|builder
init|=
name|RMDelegationTokenIdentifierDataProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
DECL|method|RMDelegationTokenIdentifierData ()
specifier|public
name|RMDelegationTokenIdentifierData
parameter_list|()
block|{}
DECL|method|RMDelegationTokenIdentifierData ( YARNDelegationTokenIdentifier identifier, long renewdate)
specifier|public
name|RMDelegationTokenIdentifierData
parameter_list|(
name|YARNDelegationTokenIdentifier
name|identifier
parameter_list|,
name|long
name|renewdate
parameter_list|)
block|{
name|builder
operator|.
name|setTokenIdentifier
argument_list|(
name|identifier
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setRenewDate
argument_list|(
name|renewdate
argument_list|)
expr_stmt|;
block|}
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
name|builder
operator|.
name|mergeFrom
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|toByteArray ()
specifier|public
name|byte
index|[]
name|toByteArray
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|builder
operator|.
name|build
argument_list|()
operator|.
name|toByteArray
argument_list|()
return|;
block|}
DECL|method|getTokenIdentifier ()
specifier|public
name|RMDelegationTokenIdentifier
name|getTokenIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|builder
operator|.
name|getTokenIdentifier
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|RMDelegationTokenIdentifier
name|identifer
init|=
operator|new
name|RMDelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|identifer
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|identifer
return|;
block|}
DECL|method|getRenewDate ()
specifier|public
name|long
name|getRenewDate
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getRenewDate
argument_list|()
return|;
block|}
block|}
end_class

end_unit

