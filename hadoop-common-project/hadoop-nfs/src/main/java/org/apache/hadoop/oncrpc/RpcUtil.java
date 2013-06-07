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

begin_comment
comment|/**  * The XID in RPC call. It is used for starting with new seed after each reboot.  */
end_comment

begin_class
DECL|class|RpcUtil
specifier|public
class|class
name|RpcUtil
block|{
DECL|field|xid
specifier|private
specifier|static
name|int
name|xid
init|=
call|(
name|int
call|)
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|/
literal|1000
argument_list|)
operator|<<
literal|12
decl_stmt|;
DECL|method|getNewXid (String caller)
specifier|public
specifier|static
name|int
name|getNewXid
parameter_list|(
name|String
name|caller
parameter_list|)
block|{
return|return
name|xid
operator|=
operator|++
name|xid
operator|+
name|caller
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

