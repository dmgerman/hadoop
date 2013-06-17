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

begin_comment
comment|/**  * A void NFSv3 response  */
end_comment

begin_class
DECL|class|VoidResponse
specifier|public
class|class
name|VoidResponse
extends|extends
name|NFS3Response
block|{
DECL|method|VoidResponse (int status)
specifier|public
name|VoidResponse
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|super
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|send (XDR out, int xid)
specifier|public
name|XDR
name|send
parameter_list|(
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|)
block|{
name|RpcAcceptedReply
operator|.
name|voidReply
argument_list|(
name|out
argument_list|,
name|xid
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
block|}
end_class

end_unit

