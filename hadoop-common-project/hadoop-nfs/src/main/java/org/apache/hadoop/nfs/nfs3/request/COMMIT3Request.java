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
comment|/**  * COMMIT3 Request  */
end_comment

begin_class
DECL|class|COMMIT3Request
specifier|public
class|class
name|COMMIT3Request
extends|extends
name|RequestWithHandle
block|{
DECL|field|offset
specifier|private
specifier|final
name|long
name|offset
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|int
name|count
decl_stmt|;
DECL|method|deserialize (XDR xdr)
specifier|public
specifier|static
name|COMMIT3Request
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
name|long
name|offset
init|=
name|xdr
operator|.
name|readHyper
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|xdr
operator|.
name|readInt
argument_list|()
decl_stmt|;
return|return
operator|new
name|COMMIT3Request
argument_list|(
name|handle
argument_list|,
name|offset
argument_list|,
name|count
argument_list|)
return|;
block|}
DECL|method|COMMIT3Request (FileHandle handle, long offset, int count)
specifier|public
name|COMMIT3Request
parameter_list|(
name|FileHandle
name|handle
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|super
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
DECL|method|getOffset ()
specifier|public
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|this
operator|.
name|offset
return|;
block|}
DECL|method|getCount ()
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|count
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
name|xdr
operator|.
name|writeLongAsHyper
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

