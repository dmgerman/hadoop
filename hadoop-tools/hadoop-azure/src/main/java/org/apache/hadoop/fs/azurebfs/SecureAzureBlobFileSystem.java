begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|FileSystemUriSchemes
import|;
end_import

begin_comment
comment|/**  * A secure {@link org.apache.hadoop.fs.FileSystem} for reading and writing files stored on<a  * href="http://store.azure.com/">Windows Azure</a>  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|SecureAzureBlobFileSystem
specifier|public
class|class
name|SecureAzureBlobFileSystem
extends|extends
name|AzureBlobFileSystem
block|{
annotation|@
name|Override
DECL|method|isSecure ()
specifier|public
name|boolean
name|isSecure
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|FileSystemUriSchemes
operator|.
name|ABFS_SECURE_SCHEME
return|;
block|}
block|}
end_class

end_unit

