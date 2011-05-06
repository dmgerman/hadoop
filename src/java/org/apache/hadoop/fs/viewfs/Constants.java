begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_comment
comment|/**  * Config variable prefixes for ViewFs -  *     see {@link org.apache.hadoop.fs.viewfs.ViewFs} for examples.  * The mount table is specified in the config using these prefixes.  * See {@link org.apache.hadoop.fs.viewfs.ConfigUtil} for convenience lib.  */
end_comment

begin_interface
DECL|interface|Constants
specifier|public
interface|interface
name|Constants
block|{
comment|/**    * Prefix for the config variable prefix for the ViewFs mount-table    */
DECL|field|CONFIG_VIEWFS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_VIEWFS_PREFIX
init|=
literal|"fs.viewfs.mounttable"
decl_stmt|;
comment|/**    * Config variable name for the default mount table.    */
DECL|field|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
init|=
literal|"default"
decl_stmt|;
comment|/**    * Config variable full prefix for the default mount table.    */
DECL|field|CONFIG_VIEWFS_PREFIX_DEFAULT_MOUNT_TABLE
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_VIEWFS_PREFIX_DEFAULT_MOUNT_TABLE
init|=
name|CONFIG_VIEWFS_PREFIX
operator|+
literal|"."
operator|+
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
decl_stmt|;
comment|/**    * Config variable for specifying a simple link    */
DECL|field|CONFIG_VIEWFS_LINK
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_VIEWFS_LINK
init|=
literal|"link"
decl_stmt|;
comment|/**    * Config variable for specifying a merge link    */
DECL|field|CONFIG_VIEWFS_LINK_MERGE
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_VIEWFS_LINK_MERGE
init|=
literal|"linkMerge"
decl_stmt|;
comment|/**    * Config variable for specifying a merge of the root of the mount-table    *  with the root of another file system.     */
DECL|field|CONFIG_VIEWFS_LINK_MERGE_SLASH
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_VIEWFS_LINK_MERGE_SLASH
init|=
literal|"linkMergeSlash"
decl_stmt|;
DECL|field|PERMISSION_RRR
specifier|static
specifier|public
specifier|final
name|FsPermission
name|PERMISSION_RRR
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0444
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

