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
comment|/**  * An NFS request that uses {@link FileHandle} to identify a file.  */
end_comment

begin_class
DECL|class|NFS3Request
specifier|public
specifier|abstract
class|class
name|NFS3Request
block|{
comment|/**    * Deserialize a handle from an XDR object    */
DECL|method|readHandle (XDR xdr)
specifier|static
name|FileHandle
name|readHandle
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
operator|new
name|FileHandle
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|handle
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"can't deserialize file handle"
argument_list|)
throw|;
block|}
return|return
name|handle
return|;
block|}
comment|/**    * Subclass should implement. Usually handle is the first to be serialized    * @param xdr XDR message    */
DECL|method|serialize (XDR xdr)
specifier|public
specifier|abstract
name|void
name|serialize
parameter_list|(
name|XDR
name|xdr
parameter_list|)
function_decl|;
block|}
end_class

end_unit

