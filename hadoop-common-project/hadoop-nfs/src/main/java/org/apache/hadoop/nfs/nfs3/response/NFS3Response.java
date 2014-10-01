begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs.nfs3.response
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
operator|.
name|nfs3
operator|.
name|response
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
name|RpcAcceptedReply
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
name|XDR
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
name|Verifier
import|;
end_import

begin_comment
comment|/**  * Base class for a NFSv3 response. This class and its subclasses contain  * the response from NFSv3 handlers.  */
end_comment

begin_class
DECL|class|NFS3Response
specifier|public
class|class
name|NFS3Response
block|{
DECL|field|status
specifier|protected
name|int
name|status
decl_stmt|;
DECL|method|NFS3Response (int status)
specifier|public
name|NFS3Response
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
DECL|method|getStatus ()
specifier|public
name|int
name|getStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|status
return|;
block|}
DECL|method|setStatus (int status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
comment|/**    * Write the response, along with the rpc header (including verifier), to the    * XDR.    */
DECL|method|serialize (XDR out, int xid, Verifier verifier)
specifier|public
name|XDR
name|serialize
parameter_list|(
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|,
name|Verifier
name|verifier
parameter_list|)
block|{
name|RpcAcceptedReply
name|reply
init|=
name|RpcAcceptedReply
operator|.
name|getAcceptInstance
argument_list|(
name|xid
argument_list|,
name|verifier
argument_list|)
decl_stmt|;
name|reply
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
block|}
end_class

end_unit

