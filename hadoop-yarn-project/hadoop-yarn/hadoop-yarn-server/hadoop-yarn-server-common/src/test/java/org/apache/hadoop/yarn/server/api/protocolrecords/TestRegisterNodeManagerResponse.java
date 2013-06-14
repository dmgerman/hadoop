begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|api
operator|.
name|protocolrecords
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

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
name|ByteArrayOutputStream
import|;
end_import

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
name|yarn
operator|.
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RegisterNodeManagerResponsePBImpl
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
name|records
operator|.
name|MasterKey
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
name|records
operator|.
name|NodeAction
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
name|YarnServerCommonServiceProtos
operator|.
name|RegisterNodeManagerResponseProto
import|;
end_import

begin_class
DECL|class|TestRegisterNodeManagerResponse
specifier|public
class|class
name|TestRegisterNodeManagerResponse
block|{
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testRoundTrip ()
specifier|public
name|void
name|testRoundTrip
parameter_list|()
throws|throws
name|Exception
block|{
name|RegisterNodeManagerResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterNodeManagerResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|byte
name|b
index|[]
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
decl_stmt|;
name|MasterKey
name|containerTokenMK
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|MasterKey
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerTokenMK
operator|.
name|setKeyId
argument_list|(
literal|54321
argument_list|)
expr_stmt|;
name|containerTokenMK
operator|.
name|setBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setContainerTokenMasterKey
argument_list|(
name|containerTokenMK
argument_list|)
expr_stmt|;
name|MasterKey
name|nmTokenMK
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|MasterKey
operator|.
name|class
argument_list|)
decl_stmt|;
name|nmTokenMK
operator|.
name|setKeyId
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
name|nmTokenMK
operator|.
name|setBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setNMTokenMasterKey
argument_list|(
name|nmTokenMK
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setNodeAction
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|,
name|resp
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verifying containerTokenMasterKey
name|assertNotNull
argument_list|(
name|resp
operator|.
name|getContainerTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|54321
argument_list|,
name|resp
operator|.
name|getContainerTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|b
argument_list|,
name|resp
operator|.
name|getContainerTokenMasterKey
argument_list|()
operator|.
name|getBytes
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
name|RegisterNodeManagerResponse
name|respCopy
init|=
name|serDe
argument_list|(
name|resp
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|,
name|respCopy
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|respCopy
operator|.
name|getContainerTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|54321
argument_list|,
name|respCopy
operator|.
name|getContainerTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|b
argument_list|,
name|respCopy
operator|.
name|getContainerTokenMasterKey
argument_list|()
operator|.
name|getBytes
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verifying nmTokenMasterKey
name|assertNotNull
argument_list|(
name|resp
operator|.
name|getNMTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|resp
operator|.
name|getNMTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|b
argument_list|,
name|resp
operator|.
name|getNMTokenMasterKey
argument_list|()
operator|.
name|getBytes
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
name|respCopy
operator|=
name|serDe
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|,
name|respCopy
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|respCopy
operator|.
name|getNMTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|respCopy
operator|.
name|getNMTokenMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|b
argument_list|,
name|respCopy
operator|.
name|getNMTokenMasterKey
argument_list|()
operator|.
name|getBytes
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|serDe (RegisterNodeManagerResponse orig)
specifier|public
specifier|static
name|RegisterNodeManagerResponse
name|serDe
parameter_list|(
name|RegisterNodeManagerResponse
name|orig
parameter_list|)
throws|throws
name|Exception
block|{
name|RegisterNodeManagerResponsePBImpl
name|asPB
init|=
operator|(
name|RegisterNodeManagerResponsePBImpl
operator|)
name|orig
decl_stmt|;
name|RegisterNodeManagerResponseProto
name|proto
init|=
name|asPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|proto
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|RegisterNodeManagerResponseProto
operator|.
name|Builder
name|cp
init|=
name|RegisterNodeManagerResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|cp
operator|.
name|mergeFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
operator|new
name|RegisterNodeManagerResponsePBImpl
argument_list|(
name|cp
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

