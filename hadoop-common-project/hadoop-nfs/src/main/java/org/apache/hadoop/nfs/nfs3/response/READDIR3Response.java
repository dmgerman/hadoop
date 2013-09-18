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
name|Collections
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
name|nfs3
operator|.
name|Nfs3FileAttributes
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
name|nfs3
operator|.
name|Nfs3Status
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
comment|/**  * READDIR3 Response  */
end_comment

begin_class
DECL|class|READDIR3Response
specifier|public
class|class
name|READDIR3Response
extends|extends
name|NFS3Response
block|{
DECL|field|postOpDirAttr
specifier|private
specifier|final
name|Nfs3FileAttributes
name|postOpDirAttr
decl_stmt|;
DECL|field|cookieVerf
specifier|private
specifier|final
name|long
name|cookieVerf
decl_stmt|;
DECL|field|dirList
specifier|private
specifier|final
name|DirList3
name|dirList
decl_stmt|;
DECL|class|Entry3
specifier|public
specifier|static
class|class
name|Entry3
block|{
DECL|field|fileId
specifier|private
specifier|final
name|long
name|fileId
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|cookie
specifier|private
specifier|final
name|long
name|cookie
decl_stmt|;
DECL|method|Entry3 (long fileId, String name, long cookie)
specifier|public
name|Entry3
parameter_list|(
name|long
name|fileId
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|cookie
parameter_list|)
block|{
name|this
operator|.
name|fileId
operator|=
name|fileId
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|cookie
operator|=
name|cookie
expr_stmt|;
block|}
DECL|method|getFileId ()
name|long
name|getFileId
parameter_list|()
block|{
return|return
name|fileId
return|;
block|}
DECL|method|getName ()
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getCookie ()
name|long
name|getCookie
parameter_list|()
block|{
return|return
name|cookie
return|;
block|}
block|}
DECL|class|DirList3
specifier|public
specifier|static
class|class
name|DirList3
block|{
DECL|field|entries
specifier|final
name|List
argument_list|<
name|Entry3
argument_list|>
name|entries
decl_stmt|;
DECL|field|eof
specifier|final
name|boolean
name|eof
decl_stmt|;
DECL|method|DirList3 (Entry3[] entries, boolean eof)
specifier|public
name|DirList3
parameter_list|(
name|Entry3
index|[]
name|entries
parameter_list|,
name|boolean
name|eof
parameter_list|)
block|{
name|this
operator|.
name|entries
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|entries
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|eof
operator|=
name|eof
expr_stmt|;
block|}
block|}
DECL|method|READDIR3Response (int status)
specifier|public
name|READDIR3Response
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
argument_list|(
name|status
argument_list|,
operator|new
name|Nfs3FileAttributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|READDIR3Response (int status, Nfs3FileAttributes postOpAttr)
specifier|public
name|READDIR3Response
parameter_list|(
name|int
name|status
parameter_list|,
name|Nfs3FileAttributes
name|postOpAttr
parameter_list|)
block|{
name|this
argument_list|(
name|status
argument_list|,
name|postOpAttr
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|READDIR3Response (int status, Nfs3FileAttributes postOpAttr, final long cookieVerf, final DirList3 dirList)
specifier|public
name|READDIR3Response
parameter_list|(
name|int
name|status
parameter_list|,
name|Nfs3FileAttributes
name|postOpAttr
parameter_list|,
specifier|final
name|long
name|cookieVerf
parameter_list|,
specifier|final
name|DirList3
name|dirList
parameter_list|)
block|{
name|super
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|this
operator|.
name|postOpDirAttr
operator|=
name|postOpAttr
expr_stmt|;
name|this
operator|.
name|cookieVerf
operator|=
name|cookieVerf
expr_stmt|;
name|this
operator|.
name|dirList
operator|=
name|dirList
expr_stmt|;
block|}
DECL|method|getPostOpAttr ()
specifier|public
name|Nfs3FileAttributes
name|getPostOpAttr
parameter_list|()
block|{
return|return
name|postOpDirAttr
return|;
block|}
DECL|method|getCookieVerf ()
specifier|public
name|long
name|getCookieVerf
parameter_list|()
block|{
return|return
name|cookieVerf
return|;
block|}
DECL|method|getDirList ()
specifier|public
name|DirList3
name|getDirList
parameter_list|()
block|{
return|return
name|dirList
return|;
block|}
annotation|@
name|Override
DECL|method|writeHeaderAndResponse (XDR xdr, int xid, Verifier verifier)
specifier|public
name|XDR
name|writeHeaderAndResponse
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|int
name|xid
parameter_list|,
name|Verifier
name|verifier
parameter_list|)
block|{
name|super
operator|.
name|writeHeaderAndResponse
argument_list|(
name|xdr
argument_list|,
name|xid
argument_list|,
name|verifier
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Attributes follow
name|postOpDirAttr
operator|.
name|serialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
if|if
condition|(
name|getStatus
argument_list|()
operator|==
name|Nfs3Status
operator|.
name|NFS3_OK
condition|)
block|{
name|xdr
operator|.
name|writeLongAsHyper
argument_list|(
name|cookieVerf
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry3
name|e
range|:
name|dirList
operator|.
name|entries
control|)
block|{
name|xdr
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Value follows
name|xdr
operator|.
name|writeLongAsHyper
argument_list|(
name|e
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeString
argument_list|(
name|e
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeLongAsHyper
argument_list|(
name|e
operator|.
name|getCookie
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
name|xdr
operator|.
name|writeBoolean
argument_list|(
name|dirList
operator|.
name|eof
argument_list|)
expr_stmt|;
block|}
return|return
name|xdr
return|;
block|}
block|}
end_class

end_unit

