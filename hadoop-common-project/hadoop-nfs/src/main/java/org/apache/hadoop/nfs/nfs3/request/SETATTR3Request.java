begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs.nfs3.request
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
name|request
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|NfsTime
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
name|oncrpc
operator|.
name|XDR
import|;
end_import

begin_comment
comment|/**  * SETATTR3 Request  */
end_comment

begin_class
DECL|class|SETATTR3Request
specifier|public
class|class
name|SETATTR3Request
extends|extends
name|RequestWithHandle
block|{
DECL|field|attr
specifier|private
specifier|final
name|SetAttr3
name|attr
decl_stmt|;
comment|/* A client may request that the server check that the object is in an    * expected state before performing the SETATTR operation. If guard.check is    * TRUE, the server must compare the value of ctime to the current ctime of    * the object. If the values are different, the server must preserve the    * object attributes and must return a status of NFS3ERR_NOT_SYNC. If check is    * FALSE, the server will not perform this check.    */
DECL|field|check
specifier|private
specifier|final
name|boolean
name|check
decl_stmt|;
DECL|field|ctime
specifier|private
specifier|final
name|NfsTime
name|ctime
decl_stmt|;
DECL|method|deserialize (XDR xdr)
specifier|public
specifier|static
name|SETATTR3Request
name|deserialize
parameter_list|(
name|XDR
name|xdr
parameter_list|)
throws|throws
name|IOException
block|{
name|FileHandle
name|handle
init|=
name|readHandle
argument_list|(
name|xdr
argument_list|)
decl_stmt|;
name|SetAttr3
name|attr
init|=
operator|new
name|SetAttr3
argument_list|()
decl_stmt|;
name|attr
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|boolean
name|check
init|=
name|xdr
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
name|NfsTime
name|ctime
decl_stmt|;
if|if
condition|(
name|check
condition|)
block|{
name|ctime
operator|=
name|NfsTime
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctime
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|SETATTR3Request
argument_list|(
name|handle
argument_list|,
name|attr
argument_list|,
name|check
argument_list|,
name|ctime
argument_list|)
return|;
block|}
DECL|method|SETATTR3Request (FileHandle handle, SetAttr3 attr, boolean check, NfsTime ctime)
specifier|public
name|SETATTR3Request
parameter_list|(
name|FileHandle
name|handle
parameter_list|,
name|SetAttr3
name|attr
parameter_list|,
name|boolean
name|check
parameter_list|,
name|NfsTime
name|ctime
parameter_list|)
block|{
name|super
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|this
operator|.
name|attr
operator|=
name|attr
expr_stmt|;
name|this
operator|.
name|check
operator|=
name|check
expr_stmt|;
name|this
operator|.
name|ctime
operator|=
name|ctime
expr_stmt|;
block|}
DECL|method|getAttr ()
specifier|public
name|SetAttr3
name|getAttr
parameter_list|()
block|{
return|return
name|attr
return|;
block|}
DECL|method|isCheck ()
specifier|public
name|boolean
name|isCheck
parameter_list|()
block|{
return|return
name|check
return|;
block|}
DECL|method|getCtime ()
specifier|public
name|NfsTime
name|getCtime
parameter_list|()
block|{
return|return
name|ctime
return|;
block|}
annotation|@
name|Override
DECL|method|serialize (XDR xdr)
specifier|public
name|void
name|serialize
parameter_list|(
name|XDR
name|xdr
parameter_list|)
block|{
name|handle
operator|.
name|serialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|attr
operator|.
name|serialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeBoolean
argument_list|(
name|check
argument_list|)
expr_stmt|;
if|if
condition|(
name|check
condition|)
block|{
name|ctime
operator|.
name|serialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

