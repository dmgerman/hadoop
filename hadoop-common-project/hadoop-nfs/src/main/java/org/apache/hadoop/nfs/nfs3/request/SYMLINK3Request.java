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
comment|/**  * SYMLINK3 Request  */
end_comment

begin_class
DECL|class|SYMLINK3Request
specifier|public
class|class
name|SYMLINK3Request
extends|extends
name|RequestWithHandle
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|// The name of the link
DECL|field|symAttr
specifier|private
specifier|final
name|SetAttr3
name|symAttr
decl_stmt|;
DECL|field|symData
specifier|private
specifier|final
name|String
name|symData
decl_stmt|;
comment|// It contains the target
DECL|method|SYMLINK3Request (XDR xdr)
specifier|public
name|SYMLINK3Request
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
name|name
operator|=
name|xdr
operator|.
name|readString
argument_list|()
expr_stmt|;
name|symAttr
operator|=
operator|new
name|SetAttr3
argument_list|()
expr_stmt|;
name|symAttr
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|symData
operator|=
name|xdr
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getSymAttr ()
specifier|public
name|SetAttr3
name|getSymAttr
parameter_list|()
block|{
return|return
name|symAttr
return|;
block|}
DECL|method|getSymData ()
specifier|public
name|String
name|getSymData
parameter_list|()
block|{
return|return
name|symData
return|;
block|}
block|}
end_class

end_unit

