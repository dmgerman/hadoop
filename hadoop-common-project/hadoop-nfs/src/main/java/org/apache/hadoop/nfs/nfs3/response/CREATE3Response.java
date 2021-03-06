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
comment|/**  * CREATE3 Response  */
end_comment

begin_class
DECL|class|CREATE3Response
specifier|public
class|class
name|CREATE3Response
extends|extends
name|NFS3Response
block|{
DECL|field|objHandle
specifier|private
specifier|final
name|FileHandle
name|objHandle
decl_stmt|;
DECL|field|postOpObjAttr
specifier|private
specifier|final
name|Nfs3FileAttributes
name|postOpObjAttr
decl_stmt|;
DECL|field|dirWcc
specifier|private
name|WccData
name|dirWcc
decl_stmt|;
DECL|method|CREATE3Response (int status)
specifier|public
name|CREATE3Response
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
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|CREATE3Response (int status, FileHandle handle, Nfs3FileAttributes postOpObjAttr, WccData dirWcc)
specifier|public
name|CREATE3Response
parameter_list|(
name|int
name|status
parameter_list|,
name|FileHandle
name|handle
parameter_list|,
name|Nfs3FileAttributes
name|postOpObjAttr
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
name|objHandle
operator|=
name|handle
expr_stmt|;
name|this
operator|.
name|postOpObjAttr
operator|=
name|postOpObjAttr
expr_stmt|;
name|this
operator|.
name|dirWcc
operator|=
name|dirWcc
expr_stmt|;
block|}
DECL|method|getObjHandle ()
specifier|public
name|FileHandle
name|getObjHandle
parameter_list|()
block|{
return|return
name|objHandle
return|;
block|}
DECL|method|getPostOpObjAttr ()
specifier|public
name|Nfs3FileAttributes
name|getPostOpObjAttr
parameter_list|()
block|{
return|return
name|postOpObjAttr
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
DECL|method|deserialize (XDR xdr)
specifier|public
specifier|static
name|CREATE3Response
name|deserialize
parameter_list|(
name|XDR
name|xdr
parameter_list|)
block|{
name|int
name|status
init|=
name|xdr
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|FileHandle
name|objHandle
init|=
operator|new
name|FileHandle
argument_list|()
decl_stmt|;
name|Nfs3FileAttributes
name|postOpObjAttr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|Nfs3Status
operator|.
name|NFS3_OK
condition|)
block|{
name|xdr
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|objHandle
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|postOpObjAttr
operator|=
name|Nfs3FileAttributes
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
block|}
name|WccData
name|dirWcc
init|=
name|WccData
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
decl_stmt|;
return|return
operator|new
name|CREATE3Response
argument_list|(
name|status
argument_list|,
name|objHandle
argument_list|,
name|postOpObjAttr
argument_list|,
name|dirWcc
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|serialize
argument_list|(
name|out
argument_list|,
name|xid
argument_list|,
name|verifier
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
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Handle follows
name|objHandle
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
comment|// Attributes follow
name|postOpObjAttr
operator|.
name|serialize
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dirWcc
operator|==
literal|null
condition|)
block|{
name|dirWcc
operator|=
operator|new
name|WccData
argument_list|(
literal|null
argument_list|,
literal|null
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

