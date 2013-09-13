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
name|nfs
operator|.
name|nfs3
operator|.
name|FileHandle
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

begin_comment
comment|/**  * SYMLINK3 Response  */
end_comment

begin_class
DECL|class|SYMLINK3Response
specifier|public
class|class
name|SYMLINK3Response
extends|extends
name|NFS3Response
block|{
DECL|field|objFileHandle
specifier|private
specifier|final
name|FileHandle
name|objFileHandle
decl_stmt|;
DECL|field|objPostOpAttr
specifier|private
specifier|final
name|Nfs3FileAttributes
name|objPostOpAttr
decl_stmt|;
DECL|field|dirWcc
specifier|private
specifier|final
name|WccData
name|dirWcc
decl_stmt|;
DECL|method|SYMLINK3Response (int status)
specifier|public
name|SYMLINK3Response
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
argument_list|(
name|status
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|WccData
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|SYMLINK3Response (int status, FileHandle handle, Nfs3FileAttributes attrs, WccData dirWcc)
specifier|public
name|SYMLINK3Response
parameter_list|(
name|int
name|status
parameter_list|,
name|FileHandle
name|handle
parameter_list|,
name|Nfs3FileAttributes
name|attrs
parameter_list|,
name|WccData
name|dirWcc
parameter_list|)
block|{
name|super
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|this
operator|.
name|objFileHandle
operator|=
name|handle
expr_stmt|;
name|this
operator|.
name|objPostOpAttr
operator|=
name|attrs
expr_stmt|;
name|this
operator|.
name|dirWcc
operator|=
name|dirWcc
expr_stmt|;
block|}
DECL|method|getObjFileHandle ()
specifier|public
name|FileHandle
name|getObjFileHandle
parameter_list|()
block|{
return|return
name|objFileHandle
return|;
block|}
DECL|method|getObjPostOpAttr ()
specifier|public
name|Nfs3FileAttributes
name|getObjPostOpAttr
parameter_list|()
block|{
return|return
name|objPostOpAttr
return|;
block|}
DECL|method|getDirWcc ()
specifier|public
name|WccData
name|getDirWcc
parameter_list|()
block|{
return|return
name|dirWcc
return|;
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
name|super
operator|.
name|send
argument_list|(
name|out
argument_list|,
name|xid
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|getStatus
argument_list|()
operator|==
name|Nfs3Status
operator|.
name|NFS3_OK
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|objFileHandle
operator|.
name|serialize
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|objPostOpAttr
operator|.
name|serialize
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|dirWcc
operator|.
name|serialize
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
block|}
end_class

end_unit

