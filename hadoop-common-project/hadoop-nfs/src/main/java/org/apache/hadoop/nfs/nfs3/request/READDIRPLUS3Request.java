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
name|oncrpc
operator|.
name|XDR
import|;
end_import

begin_comment
comment|/**  * READDIRPLUS3 Request  */
end_comment

begin_class
DECL|class|READDIRPLUS3Request
specifier|public
class|class
name|READDIRPLUS3Request
extends|extends
name|RequestWithHandle
block|{
DECL|field|cookie
specifier|private
specifier|final
name|long
name|cookie
decl_stmt|;
DECL|field|cookieVerf
specifier|private
specifier|final
name|long
name|cookieVerf
decl_stmt|;
DECL|field|dirCount
specifier|private
specifier|final
name|int
name|dirCount
decl_stmt|;
DECL|field|maxCount
specifier|private
specifier|final
name|int
name|maxCount
decl_stmt|;
DECL|method|READDIRPLUS3Request (XDR xdr)
specifier|public
name|READDIRPLUS3Request
parameter_list|(
name|XDR
name|xdr
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|cookie
operator|=
name|xdr
operator|.
name|readHyper
argument_list|()
expr_stmt|;
name|cookieVerf
operator|=
name|xdr
operator|.
name|readHyper
argument_list|()
expr_stmt|;
name|dirCount
operator|=
name|xdr
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|maxCount
operator|=
name|xdr
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
DECL|method|getCookie ()
specifier|public
name|long
name|getCookie
parameter_list|()
block|{
return|return
name|this
operator|.
name|cookie
return|;
block|}
DECL|method|getCookieVerf ()
specifier|public
name|long
name|getCookieVerf
parameter_list|()
block|{
return|return
name|this
operator|.
name|cookieVerf
return|;
block|}
DECL|method|getDirCount ()
specifier|public
name|int
name|getDirCount
parameter_list|()
block|{
return|return
name|dirCount
return|;
block|}
DECL|method|getMaxCount ()
specifier|public
name|int
name|getMaxCount
parameter_list|()
block|{
return|return
name|maxCount
return|;
block|}
block|}
end_class

end_unit

