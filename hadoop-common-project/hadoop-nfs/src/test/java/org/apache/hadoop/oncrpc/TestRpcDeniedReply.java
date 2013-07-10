begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.oncrpc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
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
name|oncrpc
operator|.
name|RpcDeniedReply
operator|.
name|RejectState
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
name|oncrpc
operator|.
name|RpcReply
operator|.
name|ReplyState
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

begin_comment
comment|/**  * Test for {@link RpcDeniedReply}  */
end_comment

begin_class
DECL|class|TestRpcDeniedReply
specifier|public
class|class
name|TestRpcDeniedReply
block|{
annotation|@
name|Test
DECL|method|testRejectStateFromValue ()
specifier|public
name|void
name|testRejectStateFromValue
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RejectState
operator|.
name|RPC_MISMATCH
argument_list|,
name|RejectState
operator|.
name|fromValue
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RejectState
operator|.
name|AUTH_ERROR
argument_list|,
name|RejectState
operator|.
name|fromValue
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|)
DECL|method|testRejectStateFromInvalidValue1 ()
specifier|public
name|void
name|testRejectStateFromInvalidValue1
parameter_list|()
block|{
name|RejectState
operator|.
name|fromValue
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConstructor ()
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
name|RpcDeniedReply
name|reply
init|=
operator|new
name|RpcDeniedReply
argument_list|(
literal|0
argument_list|,
name|RpcMessage
operator|.
name|Type
operator|.
name|RPC_REPLY
argument_list|,
name|ReplyState
operator|.
name|MSG_ACCEPTED
argument_list|,
name|RejectState
operator|.
name|AUTH_ERROR
argument_list|)
block|{
comment|// Anonymous class
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|reply
operator|.
name|getXid
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RpcMessage
operator|.
name|Type
operator|.
name|RPC_REPLY
argument_list|,
name|reply
operator|.
name|getMessageType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ReplyState
operator|.
name|MSG_ACCEPTED
argument_list|,
name|reply
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RejectState
operator|.
name|AUTH_ERROR
argument_list|,
name|reply
operator|.
name|getRejectState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

