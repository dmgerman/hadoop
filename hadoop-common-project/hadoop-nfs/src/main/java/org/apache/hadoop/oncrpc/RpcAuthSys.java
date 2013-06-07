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
comment|/**  * AUTH_SYS as defined in RFC 1831  */
end_comment

begin_class
DECL|class|RpcAuthSys
specifier|public
class|class
name|RpcAuthSys
block|{
DECL|field|uid
specifier|private
specifier|final
name|int
name|uid
decl_stmt|;
DECL|field|gid
specifier|private
specifier|final
name|int
name|gid
decl_stmt|;
DECL|method|RpcAuthSys (int uid, int gid)
specifier|public
name|RpcAuthSys
parameter_list|(
name|int
name|uid
parameter_list|,
name|int
name|gid
parameter_list|)
block|{
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
block|}
DECL|method|from (byte[] credentials)
specifier|public
specifier|static
name|RpcAuthSys
name|from
parameter_list|(
name|byte
index|[]
name|credentials
parameter_list|)
block|{
name|XDR
name|sys
init|=
operator|new
name|XDR
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
name|sys
operator|.
name|skip
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// Stamp
name|sys
operator|.
name|skipVariableOpaque
argument_list|()
expr_stmt|;
comment|// Machine name
return|return
operator|new
name|RpcAuthSys
argument_list|(
name|sys
operator|.
name|readInt
argument_list|()
argument_list|,
name|sys
operator|.
name|readInt
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getUid ()
specifier|public
name|int
name|getUid
parameter_list|()
block|{
return|return
name|uid
return|;
block|}
DECL|method|getGid ()
specifier|public
name|int
name|getGid
parameter_list|()
block|{
return|return
name|gid
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"(AuthSys: uid="
operator|+
name|uid
operator|+
literal|" gid="
operator|+
name|gid
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

