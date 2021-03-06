begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mount
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mount
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|nfs
operator|.
name|NfsExports
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
name|VerifierNone
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
comment|/**  * Helper class for sending MountResponse  */
end_comment

begin_class
DECL|class|MountResponse
specifier|public
class|class
name|MountResponse
block|{
DECL|field|MNT_OK
specifier|public
specifier|final
specifier|static
name|int
name|MNT_OK
init|=
literal|0
decl_stmt|;
comment|/** Hidden constructor */
DECL|method|MountResponse ()
specifier|private
name|MountResponse
parameter_list|()
block|{   }
comment|/**    * Response for RPC call {@link MountInterface.MNTPROC#MNT}.    * @param status status of mount response    * @param xdr XDR message object    * @param xid transaction id    * @param handle file handle    * @return response XDR    */
DECL|method|writeMNTResponse (int status, XDR xdr, int xid, byte[] handle)
specifier|public
specifier|static
name|XDR
name|writeMNTResponse
parameter_list|(
name|int
name|status
parameter_list|,
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|byte
index|[]
name|handle
parameter_list|)
block|{
name|RpcAcceptedReply
operator|.
name|getAcceptInstance
argument_list|(
name|xid
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|==
name|MNT_OK
condition|)
block|{
name|xdr
operator|.
name|writeVariableOpaque
argument_list|(
name|handle
argument_list|)
expr_stmt|;
comment|// Only MountV3 returns a list of supported authFlavors
name|xdr
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|AuthFlavor
operator|.
name|AUTH_SYS
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|xdr
return|;
block|}
comment|/**    * Response for RPC call {@link MountInterface.MNTPROC#DUMP}.    * @param xdr XDR message object    * @param xid transaction id    * @param mounts mount entries    * @return response XDR    */
DECL|method|writeMountList (XDR xdr, int xid, List<MountEntry> mounts)
specifier|public
specifier|static
name|XDR
name|writeMountList
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|List
argument_list|<
name|MountEntry
argument_list|>
name|mounts
parameter_list|)
block|{
name|RpcAcceptedReply
operator|.
name|getAcceptInstance
argument_list|(
name|xid
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
for|for
control|(
name|MountEntry
name|mountEntry
range|:
name|mounts
control|)
block|{
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Value follows yes
name|xdr
operator|.
name|writeString
argument_list|(
name|mountEntry
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeString
argument_list|(
name|mountEntry
operator|.
name|getPath
argument_list|()
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
comment|// Value follows no
return|return
name|xdr
return|;
block|}
comment|/**    * Response for RPC call {@link MountInterface.MNTPROC#EXPORT}.    * @param xdr XDR message object    * @param xid transaction id    * @param exports export list    * @param hostMatcher the list of export host    * @return response XDR    */
DECL|method|writeExportList (XDR xdr, int xid, List<String> exports, List<NfsExports> hostMatcher)
specifier|public
specifier|static
name|XDR
name|writeExportList
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|exports
parameter_list|,
name|List
argument_list|<
name|NfsExports
argument_list|>
name|hostMatcher
parameter_list|)
block|{
assert|assert
operator|(
name|exports
operator|.
name|size
argument_list|()
operator|==
name|hostMatcher
operator|.
name|size
argument_list|()
operator|)
assert|;
name|RpcAcceptedReply
operator|.
name|getAcceptInstance
argument_list|(
name|xid
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exports
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Value follows - yes
name|xdr
operator|.
name|writeString
argument_list|(
name|exports
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// List host groups
name|String
index|[]
name|hostGroups
init|=
name|hostMatcher
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getHostGroupList
argument_list|()
decl_stmt|;
if|if
condition|(
name|hostGroups
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hostGroups
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Value follows - yes
name|xdr
operator|.
name|writeVariableOpaque
argument_list|(
name|hostGroups
index|[
name|j
index|]
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Value follows - no more group
block|}
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Value follows - no
return|return
name|xdr
return|;
block|}
block|}
end_class

end_unit

