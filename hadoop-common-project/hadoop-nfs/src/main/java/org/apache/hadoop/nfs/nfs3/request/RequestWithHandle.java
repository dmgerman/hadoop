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

begin_comment
comment|/**  * An NFS request that uses {@link FileHandle} to identify a file.  */
end_comment

begin_class
DECL|class|RequestWithHandle
specifier|public
specifier|abstract
class|class
name|RequestWithHandle
extends|extends
name|NFS3Request
block|{
DECL|field|handle
specifier|protected
specifier|final
name|FileHandle
name|handle
decl_stmt|;
DECL|method|RequestWithHandle (FileHandle handle)
name|RequestWithHandle
parameter_list|(
name|FileHandle
name|handle
parameter_list|)
block|{
name|this
operator|.
name|handle
operator|=
name|handle
expr_stmt|;
block|}
DECL|method|getHandle ()
specifier|public
name|FileHandle
name|getHandle
parameter_list|()
block|{
return|return
name|this
operator|.
name|handle
return|;
block|}
block|}
end_class

end_unit

