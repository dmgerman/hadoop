begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contract
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
operator|.
name|contract
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
name|conf
operator|.
name|Configuration
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
name|Path
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
name|utils
operator|.
name|UriUtils
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
name|contract
operator|.
name|AbstractBondedFSContract
import|;
end_import

begin_comment
comment|/**  * Azure BlobFileSystem Contract. Test paths are created using any maven fork  * identifier, if defined. This guarantees paths unique to tests  * running in parallel.  */
end_comment

begin_class
DECL|class|AbfsFileSystemContract
specifier|public
class|class
name|AbfsFileSystemContract
extends|extends
name|AbstractBondedFSContract
block|{
DECL|field|CONTRACT_XML
specifier|public
specifier|static
specifier|final
name|String
name|CONTRACT_XML
init|=
literal|"abfs.xml"
decl_stmt|;
DECL|field|isSecure
specifier|private
specifier|final
name|boolean
name|isSecure
decl_stmt|;
DECL|method|AbfsFileSystemContract (final Configuration conf, boolean secure)
specifier|protected
name|AbfsFileSystemContract
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
name|boolean
name|secure
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//insert the base features
name|addConfResource
argument_list|(
name|CONTRACT_XML
argument_list|)
expr_stmt|;
name|this
operator|.
name|isSecure
operator|=
name|secure
expr_stmt|;
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
name|isSecure
condition|?
name|FileSystemUriSchemes
operator|.
name|ABFS_SECURE_SCHEME
else|:
name|FileSystemUriSchemes
operator|.
name|ABFS_SCHEME
return|;
block|}
annotation|@
name|Override
DECL|method|getTestPath ()
specifier|public
name|Path
name|getTestPath
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
name|UriUtils
operator|.
name|generateUniqueTestPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"AbfsFileSystemContract{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"isSecureScheme="
argument_list|)
operator|.
name|append
argument_list|(
name|isSecure
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

