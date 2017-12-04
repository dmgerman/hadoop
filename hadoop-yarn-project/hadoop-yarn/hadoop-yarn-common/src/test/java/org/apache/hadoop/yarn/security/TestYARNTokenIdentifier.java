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
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|HadoopKerberosName
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
name|ExecutionType
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
name|conf
operator|.
name|YarnConfiguration
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
name|YARNDelegationTokenIdentifierProto
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
name|ClientToAMTokenIdentifier
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
name|TimelineDelegationTokenIdentifier
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
name|api
operator|.
name|ContainerType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestYARNTokenIdentifier
specifier|public
class|class
name|TestYARNTokenIdentifier
block|{
annotation|@
name|Test
DECL|method|testNMTokenIdentifier ()
specifier|public
name|void
name|testNMTokenIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"host0"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|applicationSubmitter
init|=
literal|"usr0"
decl_stmt|;
name|int
name|masterKeyId
init|=
literal|1
decl_stmt|;
name|NMTokenIdentifier
name|token
init|=
operator|new
name|NMTokenIdentifier
argument_list|(
name|appAttemptId
argument_list|,
name|nodeId
argument_list|,
name|applicationSubmitter
argument_list|,
name|masterKeyId
argument_list|)
decl_stmt|;
name|NMTokenIdentifier
name|anotherToken
init|=
operator|new
name|NMTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenContent
init|=
name|token
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|tokenContent
argument_list|,
name|tokenContent
operator|.
name|length
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
comment|// verify the whole record equals with original record
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Token is not the same after serialization "
operator|+
literal|"and deserialization."
argument_list|,
name|token
argument_list|,
name|anotherToken
argument_list|)
expr_stmt|;
comment|// verify all properties are the same as original
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"appAttemptId from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|appAttemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"NodeId from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"applicationSubmitter from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getApplicationSubmitter
argument_list|()
argument_list|,
name|applicationSubmitter
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"masterKeyId from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|masterKeyId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAMRMTokenIdentifier ()
specifier|public
name|void
name|testAMRMTokenIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
name|masterKeyId
init|=
literal|1
decl_stmt|;
name|AMRMTokenIdentifier
name|token
init|=
operator|new
name|AMRMTokenIdentifier
argument_list|(
name|appAttemptId
argument_list|,
name|masterKeyId
argument_list|)
decl_stmt|;
name|AMRMTokenIdentifier
name|anotherToken
init|=
operator|new
name|AMRMTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenContent
init|=
name|token
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|tokenContent
argument_list|,
name|tokenContent
operator|.
name|length
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
comment|// verify the whole record equals with original record
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Token is not the same after serialization "
operator|+
literal|"and deserialization."
argument_list|,
name|token
argument_list|,
name|anotherToken
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ApplicationAttemptId from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|appAttemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"masterKeyId from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|masterKeyId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientToAMTokenIdentifier ()
specifier|public
name|void
name|testClientToAMTokenIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|clientName
init|=
literal|"user"
decl_stmt|;
name|ClientToAMTokenIdentifier
name|token
init|=
operator|new
name|ClientToAMTokenIdentifier
argument_list|(
name|appAttemptId
argument_list|,
name|clientName
argument_list|)
decl_stmt|;
name|ClientToAMTokenIdentifier
name|anotherToken
init|=
operator|new
name|ClientToAMTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenContent
init|=
name|token
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|tokenContent
argument_list|,
name|tokenContent
operator|.
name|length
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
comment|// verify the whole record equals with original record
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Token is not the same after serialization "
operator|+
literal|"and deserialization."
argument_list|,
name|token
argument_list|,
name|anotherToken
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ApplicationAttemptId from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getApplicationAttemptID
argument_list|()
argument_list|,
name|appAttemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"clientName from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getClientName
argument_list|()
argument_list|,
name|clientName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerTokenIdentifier ()
specifier|public
name|void
name|testContainerTokenIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerId
name|containerID
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|hostName
init|=
literal|"host0"
decl_stmt|;
name|String
name|appSubmitter
init|=
literal|"usr0"
decl_stmt|;
name|Resource
name|r
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|expiryTimeStamp
init|=
literal|1000
decl_stmt|;
name|int
name|masterKeyId
init|=
literal|1
decl_stmt|;
name|long
name|rmIdentifier
init|=
literal|1
decl_stmt|;
name|Priority
name|priority
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|creationTime
init|=
literal|1000
decl_stmt|;
name|ContainerTokenIdentifier
name|token
init|=
operator|new
name|ContainerTokenIdentifier
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
argument_list|)
decl_stmt|;
name|ContainerTokenIdentifier
name|anotherToken
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenContent
init|=
name|token
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|tokenContent
argument_list|,
name|tokenContent
operator|.
name|length
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
comment|// verify the whole record equals with original record
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Token is not the same after serialization "
operator|+
literal|"and deserialization."
argument_list|,
name|token
argument_list|,
name|anotherToken
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ContainerID from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Hostname from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getNmHostAddress
argument_list|()
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ApplicationSubmitter from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getApplicationSubmitter
argument_list|()
argument_list|,
name|appSubmitter
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getResource
argument_list|()
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"expiryTimeStamp from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getExpiryTimeStamp
argument_list|()
argument_list|,
name|expiryTimeStamp
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"KeyId from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getMasterKeyId
argument_list|()
argument_list|,
name|masterKeyId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"RMIdentifier from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getRMIdentifier
argument_list|()
argument_list|,
name|rmIdentifier
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Priority from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getPriority
argument_list|()
argument_list|,
name|priority
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"CreationTime from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getCreationTime
argument_list|()
argument_list|,
name|creationTime
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|anotherToken
operator|.
name|getLogAggregationContext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|,
name|anotherToken
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerType
operator|.
name|TASK
argument_list|,
name|anotherToken
operator|.
name|getContainerType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|,
name|anotherToken
operator|.
name|getExecutionType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRMDelegationTokenIdentifier ()
specifier|public
name|void
name|testRMDelegationTokenIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|testRMDelegationTokenIdentifier
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRMDelegationTokenIdentifierOldFormat ()
specifier|public
name|void
name|testRMDelegationTokenIdentifierOldFormat
parameter_list|()
throws|throws
name|IOException
block|{
name|testRMDelegationTokenIdentifier
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testRMDelegationTokenIdentifier (boolean oldFormat)
specifier|public
name|void
name|testRMDelegationTokenIdentifier
parameter_list|(
name|boolean
name|oldFormat
parameter_list|)
throws|throws
name|IOException
block|{
name|Text
name|owner
init|=
operator|new
name|Text
argument_list|(
literal|"user1"
argument_list|)
decl_stmt|;
name|Text
name|renewer
init|=
operator|new
name|Text
argument_list|(
literal|"user2"
argument_list|)
decl_stmt|;
name|Text
name|realUser
init|=
operator|new
name|Text
argument_list|(
literal|"user3"
argument_list|)
decl_stmt|;
name|long
name|issueDate
init|=
literal|1
decl_stmt|;
name|long
name|maxDate
init|=
literal|2
decl_stmt|;
name|int
name|sequenceNumber
init|=
literal|3
decl_stmt|;
name|int
name|masterKeyId
init|=
literal|4
decl_stmt|;
name|RMDelegationTokenIdentifier
name|originalToken
init|=
operator|new
name|RMDelegationTokenIdentifier
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
decl_stmt|;
name|originalToken
operator|.
name|setIssueDate
argument_list|(
name|issueDate
argument_list|)
expr_stmt|;
name|originalToken
operator|.
name|setMaxDate
argument_list|(
name|maxDate
argument_list|)
expr_stmt|;
name|originalToken
operator|.
name|setSequenceNumber
argument_list|(
name|sequenceNumber
argument_list|)
expr_stmt|;
name|originalToken
operator|.
name|setMasterKeyId
argument_list|(
name|masterKeyId
argument_list|)
expr_stmt|;
name|RMDelegationTokenIdentifier
name|anotherToken
init|=
operator|new
name|RMDelegationTokenIdentifier
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldFormat
condition|)
block|{
name|DataInputBuffer
name|inBuf
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|outBuf
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|originalToken
operator|.
name|writeInOldFormat
argument_list|(
name|outBuf
argument_list|)
expr_stmt|;
name|inBuf
operator|.
name|reset
argument_list|(
name|outBuf
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|outBuf
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFieldsInOldFormat
argument_list|(
name|inBuf
argument_list|)
expr_stmt|;
name|inBuf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|byte
index|[]
name|tokenContent
init|=
name|originalToken
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|tokenContent
argument_list|,
name|tokenContent
operator|.
name|length
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
name|dib
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// verify the whole record equals with original record
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Token is not the same after serialization and deserialization."
argument_list|,
name|originalToken
argument_list|,
name|anotherToken
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"owner from proto is not the same with original token"
argument_list|,
name|owner
argument_list|,
name|anotherToken
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"renewer from proto is not the same with original token"
argument_list|,
name|renewer
argument_list|,
name|anotherToken
operator|.
name|getRenewer
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"realUser from proto is not the same with original token"
argument_list|,
name|realUser
argument_list|,
name|anotherToken
operator|.
name|getRealUser
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"issueDate from proto is not the same with original token"
argument_list|,
name|issueDate
argument_list|,
name|anotherToken
operator|.
name|getIssueDate
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"maxDate from proto is not the same with original token"
argument_list|,
name|maxDate
argument_list|,
name|anotherToken
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"sequenceNumber from proto is not the same with original token"
argument_list|,
name|sequenceNumber
argument_list|,
name|anotherToken
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"masterKeyId from proto is not the same with original token"
argument_list|,
name|masterKeyId
argument_list|,
name|anotherToken
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test getProto
name|YARNDelegationTokenIdentifierProto
name|tokenProto
init|=
name|originalToken
operator|.
name|getProto
argument_list|()
decl_stmt|;
comment|// Write token proto to stream
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|tokenProto
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// Read token
name|byte
index|[]
name|tokenData
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|RMDelegationTokenIdentifier
name|readToken
init|=
operator|new
name|RMDelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|db
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|db
operator|.
name|reset
argument_list|(
name|tokenData
argument_list|,
name|tokenData
operator|.
name|length
argument_list|)
expr_stmt|;
name|readToken
operator|.
name|readFields
argument_list|(
name|db
argument_list|)
expr_stmt|;
comment|// Verify if read token equals with original token
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Token from getProto is not the same after "
operator|+
literal|"serialization and deserialization."
argument_list|,
name|originalToken
argument_list|,
name|readToken
argument_list|)
expr_stmt|;
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimelineDelegationTokenIdentifier ()
specifier|public
name|void
name|testTimelineDelegationTokenIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|Text
name|owner
init|=
operator|new
name|Text
argument_list|(
literal|"user1"
argument_list|)
decl_stmt|;
name|Text
name|renewer
init|=
operator|new
name|Text
argument_list|(
literal|"user2"
argument_list|)
decl_stmt|;
name|Text
name|realUser
init|=
operator|new
name|Text
argument_list|(
literal|"user3"
argument_list|)
decl_stmt|;
name|long
name|issueDate
init|=
literal|1
decl_stmt|;
name|long
name|maxDate
init|=
literal|2
decl_stmt|;
name|int
name|sequenceNumber
init|=
literal|3
decl_stmt|;
name|int
name|masterKeyId
init|=
literal|4
decl_stmt|;
name|TimelineDelegationTokenIdentifier
name|token
init|=
operator|new
name|TimelineDelegationTokenIdentifier
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
decl_stmt|;
name|token
operator|.
name|setIssueDate
argument_list|(
name|issueDate
argument_list|)
expr_stmt|;
name|token
operator|.
name|setMaxDate
argument_list|(
name|maxDate
argument_list|)
expr_stmt|;
name|token
operator|.
name|setSequenceNumber
argument_list|(
name|sequenceNumber
argument_list|)
expr_stmt|;
name|token
operator|.
name|setMasterKeyId
argument_list|(
name|masterKeyId
argument_list|)
expr_stmt|;
name|TimelineDelegationTokenIdentifier
name|anotherToken
init|=
operator|new
name|TimelineDelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenContent
init|=
name|token
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|tokenContent
argument_list|,
name|tokenContent
operator|.
name|length
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
comment|// verify the whole record equals with original record
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Token is not the same after serialization "
operator|+
literal|"and deserialization."
argument_list|,
name|token
argument_list|,
name|anotherToken
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"owner from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getOwner
argument_list|()
argument_list|,
name|owner
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"renewer from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getRenewer
argument_list|()
argument_list|,
name|renewer
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"realUser from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getRealUser
argument_list|()
argument_list|,
name|realUser
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"issueDate from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getIssueDate
argument_list|()
argument_list|,
name|issueDate
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"maxDate from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getMaxDate
argument_list|()
argument_list|,
name|maxDate
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"sequenceNumber from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getSequenceNumber
argument_list|()
argument_list|,
name|sequenceNumber
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"masterKeyId from proto is not the same with original token"
argument_list|,
name|anotherToken
operator|.
name|getMasterKeyId
argument_list|()
argument_list|,
name|masterKeyId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseTimelineDelegationTokenIdentifierRenewer ()
specifier|public
name|void
name|testParseTimelineDelegationTokenIdentifierRenewer
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Server side when generation a timeline DT
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTH_TO_LOCAL
argument_list|,
literal|"RULE:[2:$1@$0]([nr]m@.*EXAMPLE.COM)s/.*/yarn/"
argument_list|)
expr_stmt|;
name|HadoopKerberosName
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Text
name|owner
init|=
operator|new
name|Text
argument_list|(
literal|"owner"
argument_list|)
decl_stmt|;
name|Text
name|renewer
init|=
operator|new
name|Text
argument_list|(
literal|"rm/localhost@EXAMPLE.COM"
argument_list|)
decl_stmt|;
name|Text
name|realUser
init|=
operator|new
name|Text
argument_list|(
literal|"realUser"
argument_list|)
decl_stmt|;
name|TimelineDelegationTokenIdentifier
name|token
init|=
operator|new
name|TimelineDelegationTokenIdentifier
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Text
argument_list|(
literal|"yarn"
argument_list|)
argument_list|,
name|token
operator|.
name|getRenewer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAMContainerTokenIdentifier ()
specifier|public
name|void
name|testAMContainerTokenIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerId
name|containerID
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|hostName
init|=
literal|"host0"
decl_stmt|;
name|String
name|appSubmitter
init|=
literal|"usr0"
decl_stmt|;
name|Resource
name|r
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|expiryTimeStamp
init|=
literal|1000
decl_stmt|;
name|int
name|masterKeyId
init|=
literal|1
decl_stmt|;
name|long
name|rmIdentifier
init|=
literal|1
decl_stmt|;
name|Priority
name|priority
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|creationTime
init|=
literal|1000
decl_stmt|;
name|ContainerTokenIdentifier
name|token
init|=
operator|new
name|ContainerTokenIdentifier
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
argument_list|,
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|,
name|ContainerType
operator|.
name|APPLICATION_MASTER
argument_list|)
decl_stmt|;
name|ContainerTokenIdentifier
name|anotherToken
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenContent
init|=
name|token
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|tokenContent
argument_list|,
name|tokenContent
operator|.
name|length
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerType
operator|.
name|APPLICATION_MASTER
argument_list|,
name|anotherToken
operator|.
name|getContainerType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|,
name|anotherToken
operator|.
name|getExecutionType
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|=
operator|new
name|ContainerTokenIdentifier
argument_list|(
name|containerID
argument_list|,
literal|0
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
argument_list|,
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|,
name|ContainerType
operator|.
name|TASK
argument_list|,
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|)
expr_stmt|;
name|anotherToken
operator|=
operator|new
name|ContainerTokenIdentifier
argument_list|()
expr_stmt|;
name|tokenContent
operator|=
name|token
operator|.
name|getBytes
argument_list|()
expr_stmt|;
name|dib
operator|=
operator|new
name|DataInputBuffer
argument_list|()
expr_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|tokenContent
argument_list|,
name|tokenContent
operator|.
name|length
argument_list|)
expr_stmt|;
name|anotherToken
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerType
operator|.
name|TASK
argument_list|,
name|anotherToken
operator|.
name|getContainerType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|,
name|anotherToken
operator|.
name|getExecutionType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

