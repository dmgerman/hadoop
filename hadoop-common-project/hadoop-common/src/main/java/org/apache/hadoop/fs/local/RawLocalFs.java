begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.local
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|local
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|classification
operator|.
name|InterfaceAudience
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
name|AbstractFileSystem
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
name|DelegateToFileSystem
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
name|FsConstants
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
name|FsServerDefaults
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
name|RawLocalFileSystem
import|;
end_import

begin_comment
comment|/**  * The RawLocalFs implementation of AbstractFileSystem.  *  This impl delegates to the old FileSystem  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
comment|/*Evolving for a release,to be changed to Stable */
DECL|class|RawLocalFs
specifier|public
class|class
name|RawLocalFs
extends|extends
name|DelegateToFileSystem
block|{
DECL|method|RawLocalFs (final Configuration conf)
name|RawLocalFs
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|this
argument_list|(
name|FsConstants
operator|.
name|LOCAL_FS_URI
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * This constructor has the signature needed by    * {@link AbstractFileSystem#createFileSystem(URI, Configuration)}.    *     * @param theUri which must be that of localFs    * @param conf    * @throws IOException    * @throws URISyntaxException     */
DECL|method|RawLocalFs (final URI theUri, final Configuration conf)
name|RawLocalFs
parameter_list|(
specifier|final
name|URI
name|theUri
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|super
argument_list|(
name|theUri
argument_list|,
operator|new
name|RawLocalFileSystem
argument_list|()
argument_list|,
name|conf
argument_list|,
name|FsConstants
operator|.
name|LOCAL_FS_URI
operator|.
name|getScheme
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUriDefaultPort ()
specifier|public
name|int
name|getUriDefaultPort
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
comment|// No default port for file:///
block|}
annotation|@
name|Override
DECL|method|getServerDefaults ()
specifier|public
name|FsServerDefaults
name|getServerDefaults
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|LocalConfigKeys
operator|.
name|getServerDefaults
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isValidName (String src)
specifier|public
name|boolean
name|isValidName
parameter_list|(
name|String
name|src
parameter_list|)
block|{
comment|// Different local file systems have different validation rules. Skip
comment|// validation here and just let the OS handle it. This is consistent with
comment|// RawLocalFileSystem.
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

