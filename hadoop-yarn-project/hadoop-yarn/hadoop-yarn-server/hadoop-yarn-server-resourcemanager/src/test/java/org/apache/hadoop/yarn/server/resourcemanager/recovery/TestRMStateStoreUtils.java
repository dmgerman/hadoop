begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|RMDelegationTokenIdentifierData
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
DECL|class|TestRMStateStoreUtils
specifier|public
class|class
name|TestRMStateStoreUtils
block|{
annotation|@
name|Test
DECL|method|testReadRMDelegationTokenIdentifierData ()
specifier|public
name|void
name|testReadRMDelegationTokenIdentifierData
parameter_list|()
throws|throws
name|Exception
block|{
name|testReadRMDelegationTokenIdentifierData
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadRMDelegationTokenIdentifierDataOldFormat ()
specifier|public
name|void
name|testReadRMDelegationTokenIdentifierDataOldFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|testReadRMDelegationTokenIdentifierData
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadRMDelegationTokenIdentifierData (boolean oldFormat)
specifier|public
name|void
name|testReadRMDelegationTokenIdentifierData
parameter_list|(
name|boolean
name|oldFormat
parameter_list|)
throws|throws
name|Exception
block|{
name|RMDelegationTokenIdentifier
name|token
init|=
operator|new
name|RMDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"alice"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"bob"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"colin"
argument_list|)
argument_list|)
decl_stmt|;
name|token
operator|.
name|setIssueDate
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|token
operator|.
name|setMasterKeyId
argument_list|(
literal|321
argument_list|)
expr_stmt|;
name|token
operator|.
name|setMaxDate
argument_list|(
literal|314
argument_list|)
expr_stmt|;
name|token
operator|.
name|setSequenceNumber
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|inBuf
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldFormat
condition|)
block|{
name|DataOutputBuffer
name|outBuf
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|token
operator|.
name|writeInOldFormat
argument_list|(
name|outBuf
argument_list|)
expr_stmt|;
name|outBuf
operator|.
name|writeLong
argument_list|(
literal|42
argument_list|)
expr_stmt|;
comment|// renewDate
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
block|}
else|else
block|{
name|RMDelegationTokenIdentifierData
name|tokenIdentifierData
init|=
operator|new
name|RMDelegationTokenIdentifierData
argument_list|(
name|token
argument_list|,
literal|42
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|tokenIdentifierData
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|inBuf
operator|.
name|reset
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|RMDelegationTokenIdentifierData
name|identifierData
init|=
name|RMStateStoreUtils
operator|.
name|readRMDelegationTokenIdentifierData
argument_list|(
name|inBuf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Found unexpected data still in the InputStream"
argument_list|,
operator|-
literal|1
argument_list|,
name|inBuf
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|RMDelegationTokenIdentifier
name|identifier
init|=
name|identifierData
operator|.
name|getTokenIdentifier
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"alice"
argument_list|,
name|identifier
operator|.
name|getUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Text
argument_list|(
literal|"bob"
argument_list|)
argument_list|,
name|identifier
operator|.
name|getRenewer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"colin"
argument_list|,
name|identifier
operator|.
name|getUser
argument_list|()
operator|.
name|getRealUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|identifier
operator|.
name|getIssueDate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|321
argument_list|,
name|identifier
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|314
argument_list|,
name|identifier
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|identifierData
operator|.
name|getRenewDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

