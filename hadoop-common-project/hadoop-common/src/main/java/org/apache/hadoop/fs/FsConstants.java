begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

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

begin_comment
comment|/**  * FileSystem related constants.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|FsConstants
specifier|public
interface|interface
name|FsConstants
block|{
comment|// URI for local filesystem
DECL|field|LOCAL_FS_URI
specifier|public
specifier|static
specifier|final
name|URI
name|LOCAL_FS_URI
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"file:///"
argument_list|)
decl_stmt|;
comment|// URI scheme for FTP
DECL|field|FTP_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|FTP_SCHEME
init|=
literal|"ftp"
decl_stmt|;
comment|// Maximum number of symlinks to recursively resolve in a path
DECL|field|MAX_PATH_LINKS
specifier|static
specifier|final
name|int
name|MAX_PATH_LINKS
init|=
literal|32
decl_stmt|;
comment|/**    * ViewFs: viewFs file system (ie the mount file system on client side)    */
DECL|field|VIEWFS_URI
specifier|public
specifier|static
specifier|final
name|URI
name|VIEWFS_URI
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"viewfs:///"
argument_list|)
decl_stmt|;
DECL|field|VIEWFS_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|VIEWFS_SCHEME
init|=
literal|"viewfs"
decl_stmt|;
block|}
end_interface

end_unit

