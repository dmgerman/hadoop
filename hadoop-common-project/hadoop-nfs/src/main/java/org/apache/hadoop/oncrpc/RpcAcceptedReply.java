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
name|security
operator|.
name|Verifier
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
name|security
operator|.
name|RpcAuthInfo
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
name|security
operator|.
name|RpcAuthInfo
operator|.
name|AuthFlavor
import|;
end_import

begin_comment
comment|/**   * Represents RPC message MSG_ACCEPTED reply body. See RFC 1831 for details.  * This response is sent to a request to indicate success of the request.  */
end_comment

begin_class
DECL|class|RpcAcceptedReply
specifier|public
class|class
name|RpcAcceptedReply
extends|extends
name|RpcReply
block|{
DECL|enum|AcceptState
specifier|public
enum|enum
name|AcceptState
block|{
comment|// the order of the values below are significant.
DECL|enumConstant|SUCCESS
name|SUCCESS
block|,
comment|/* RPC executed successfully */
DECL|enumConstant|PROG_UNAVAIL
name|PROG_UNAVAIL
block|,
comment|/* remote hasn't exported program */
DECL|enumConstant|PROG_MISMATCH
name|PROG_MISMATCH
block|,
comment|/* remote can't support version # */
DECL|enumConstant|PROC_UNAVAIL
name|PROC_UNAVAIL
block|,
comment|/* program can't support procedure */
DECL|enumConstant|GARBAGE_ARGS
name|GARBAGE_ARGS
block|,
comment|/* procedure can't decode params */
DECL|enumConstant|SYSTEM_ERR
name|SYSTEM_ERR
block|;
comment|/* e.g. memory allocation failure */
DECL|method|fromValue (int value)
specifier|public
specifier|static
name|AcceptState
name|fromValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
name|values
argument_list|()
index|[
name|value
index|]
return|;
block|}
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|ordinal
argument_list|()
return|;
block|}
block|}
empty_stmt|;
DECL|field|verifier
specifier|private
specifier|final
name|RpcAuthInfo
name|verifier
decl_stmt|;
DECL|field|acceptState
specifier|private
specifier|final
name|AcceptState
name|acceptState
decl_stmt|;
DECL|method|RpcAcceptedReply (int xid, RpcMessage.Type messageType, ReplyState state, RpcAuthInfo verifier, AcceptState acceptState)
name|RpcAcceptedReply
parameter_list|(
name|int
name|xid
parameter_list|,
name|RpcMessage
operator|.
name|Type
name|messageType
parameter_list|,
name|ReplyState
name|state
parameter_list|,
name|RpcAuthInfo
name|verifier
parameter_list|,
name|AcceptState
name|acceptState
parameter_list|)
block|{
name|super
argument_list|(
name|xid
argument_list|,
name|messageType
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|this
operator|.
name|verifier
operator|=
name|verifier
expr_stmt|;
name|this
operator|.
name|acceptState
operator|=
name|acceptState
expr_stmt|;
block|}
DECL|method|read (int xid, RpcMessage.Type messageType, ReplyState replyState, XDR xdr)
specifier|public
specifier|static
name|RpcAcceptedReply
name|read
parameter_list|(
name|int
name|xid
parameter_list|,
name|RpcMessage
operator|.
name|Type
name|messageType
parameter_list|,
name|ReplyState
name|replyState
parameter_list|,
name|XDR
name|xdr
parameter_list|)
block|{
name|Verifier
name|verifier
init|=
name|Verifier
operator|.
name|readFlavorAndVerifier
argument_list|(
name|xdr
argument_list|)
decl_stmt|;
name|AcceptState
name|acceptState
init|=
name|AcceptState
operator|.
name|fromValue
argument_list|(
name|xdr
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RpcAcceptedReply
argument_list|(
name|xid
argument_list|,
name|messageType
argument_list|,
name|replyState
argument_list|,
name|verifier
argument_list|,
name|acceptState
argument_list|)
return|;
block|}
DECL|method|getVerifier ()
specifier|public
name|RpcAuthInfo
name|getVerifier
parameter_list|()
block|{
return|return
name|verifier
return|;
block|}
DECL|method|getAcceptState ()
specifier|public
name|AcceptState
name|getAcceptState
parameter_list|()
block|{
return|return
name|acceptState
return|;
block|}
DECL|method|voidReply (XDR xdr, int xid)
specifier|public
specifier|static
name|XDR
name|voidReply
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|)
block|{
return|return
name|voidReply
argument_list|(
name|xdr
argument_list|,
name|xid
argument_list|,
name|AcceptState
operator|.
name|SUCCESS
argument_list|)
return|;
block|}
DECL|method|voidReply (XDR xdr, int xid, AcceptState acceptState)
specifier|public
specifier|static
name|XDR
name|voidReply
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|AcceptState
name|acceptState
parameter_list|)
block|{
name|xdr
operator|.
name|writeInt
argument_list|(
name|xid
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|RpcMessage
operator|.
name|Type
operator|.
name|RPC_REPLY
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|ReplyState
operator|.
name|MSG_ACCEPTED
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|AuthFlavor
operator|.
name|AUTH_NONE
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeVariableOpaque
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|acceptState
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|xdr
return|;
block|}
block|}
end_class

end_unit

