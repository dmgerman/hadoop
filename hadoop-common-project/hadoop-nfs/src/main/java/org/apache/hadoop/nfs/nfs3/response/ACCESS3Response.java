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
comment|/**  * ACCESS3 Response   */
end_comment

begin_class
DECL|class|ACCESS3Response
specifier|public
class|class
name|ACCESS3Response
extends|extends
name|NFS3Response
block|{
comment|/*    * A bit mask of access permissions indicating access rights for the    * authentication credentials provided with the request.    */
DECL|field|access
specifier|private
specifier|final
name|int
name|access
decl_stmt|;
DECL|field|postOpAttr
specifier|private
specifier|final
name|Nfs3FileAttributes
name|postOpAttr
decl_stmt|;
DECL|method|ACCESS3Response (int status)
specifier|public
name|ACCESS3Response
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
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|ACCESS3Response (int status, Nfs3FileAttributes postOpAttr, int access)
specifier|public
name|ACCESS3Response
parameter_list|(
name|int
name|status
parameter_list|,
name|Nfs3FileAttributes
name|postOpAttr
parameter_list|,
name|int
name|access
parameter_list|)
block|{
name|super
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|this
operator|.
name|postOpAttr
operator|=
name|postOpAttr
expr_stmt|;
name|this
operator|.
name|access
operator|=
name|access
expr_stmt|;
block|}
DECL|method|deserialize (XDR xdr)
specifier|public
specifier|static
name|ACCESS3Response
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
name|Nfs3FileAttributes
name|postOpAttr
init|=
literal|null
decl_stmt|;
name|int
name|access
init|=
literal|0
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
name|postOpAttr
operator|=
name|Nfs3FileAttributes
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|access
operator|=
name|xdr
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ACCESS3Response
argument_list|(
name|status
argument_list|,
name|postOpAttr
argument_list|,
name|access
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
name|postOpAttr
operator|.
name|serialize
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|access
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
block|}
end_class

end_unit

