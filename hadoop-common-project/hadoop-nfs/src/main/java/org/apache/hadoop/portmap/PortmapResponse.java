begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.portmap
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|portmap
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
comment|/**  * Helper utility for sending portmap response.  */
end_comment

begin_class
DECL|class|PortmapResponse
specifier|public
class|class
name|PortmapResponse
block|{
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
name|RpcAcceptedReply
operator|.
name|voidReply
argument_list|(
name|xdr
argument_list|,
name|xid
argument_list|)
expr_stmt|;
return|return
name|xdr
return|;
block|}
DECL|method|intReply (XDR xdr, int xid, int value)
specifier|public
specifier|static
name|XDR
name|intReply
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|RpcAcceptedReply
operator|.
name|voidReply
argument_list|(
name|xdr
argument_list|,
name|xid
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|xdr
return|;
block|}
DECL|method|booleanReply (XDR xdr, int xid, boolean value)
specifier|public
specifier|static
name|XDR
name|booleanReply
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|RpcAcceptedReply
operator|.
name|voidReply
argument_list|(
name|xdr
argument_list|,
name|xid
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|xdr
return|;
block|}
DECL|method|pmapList (XDR xdr, int xid, Collection<PortmapMapping> list)
specifier|public
specifier|static
name|XDR
name|pmapList
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|Collection
argument_list|<
name|PortmapMapping
argument_list|>
name|list
parameter_list|)
block|{
name|RpcAcceptedReply
operator|.
name|voidReply
argument_list|(
name|xdr
argument_list|,
name|xid
argument_list|)
expr_stmt|;
for|for
control|(
name|PortmapMapping
name|mapping
range|:
name|list
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Value follows
name|mapping
operator|.
name|serialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
block|}
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// No value follows
return|return
name|xdr
return|;
block|}
DECL|method|pmapList (XDR xdr, int xid, PortmapMapping[] list)
specifier|public
specifier|static
name|XDR
name|pmapList
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|PortmapMapping
index|[]
name|list
parameter_list|)
block|{
return|return
name|pmapList
argument_list|(
name|xdr
argument_list|,
name|xid
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

