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
name|util
operator|.
name|Arrays
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Utilities for config variables of the viewFs See {@link ViewFs}  */
end_comment

begin_class
DECL|class|ConfigUtil
specifier|public
class|class
name|ConfigUtil
block|{
comment|/**    * Get the config variable prefix for the specified mount table    * @param mountTableName - the name of the mount table    * @return the config variable prefix for the specified mount table    */
DECL|method|getConfigViewFsPrefix (final String mountTableName)
specifier|public
specifier|static
name|String
name|getConfigViewFsPrefix
parameter_list|(
specifier|final
name|String
name|mountTableName
parameter_list|)
block|{
return|return
name|Constants
operator|.
name|CONFIG_VIEWFS_PREFIX
operator|+
literal|"."
operator|+
name|mountTableName
return|;
block|}
comment|/**    * Get the config variable prefix for the default mount table    * @return the config variable prefix for the default mount table    */
DECL|method|getConfigViewFsPrefix ()
specifier|public
specifier|static
name|String
name|getConfigViewFsPrefix
parameter_list|()
block|{
return|return
name|getConfigViewFsPrefix
argument_list|(
name|Constants
operator|.
name|CONFIG_VIEWFS_PREFIX_DEFAULT_MOUNT_TABLE
argument_list|)
return|;
block|}
comment|/**    * Add a link to the config for the specified mount table    * @param conf - add the link to this conf    * @param mountTableName    * @param src - the src path name    * @param target - the target URI link    */
DECL|method|addLink (Configuration conf, final String mountTableName, final String src, final URI target)
specifier|public
specifier|static
name|void
name|addLink
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|mountTableName
parameter_list|,
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|URI
name|target
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|getConfigViewFsPrefix
argument_list|(
name|mountTableName
argument_list|)
operator|+
literal|"."
operator|+
name|Constants
operator|.
name|CONFIG_VIEWFS_LINK
operator|+
literal|"."
operator|+
name|src
argument_list|,
name|target
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a link to the config for the default mount table    * @param conf - add the link to this conf    * @param src - the src path name    * @param target - the target URI link    */
DECL|method|addLink (final Configuration conf, final String src, final URI target)
specifier|public
specifier|static
name|void
name|addLink
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|URI
name|target
parameter_list|)
block|{
name|addLink
argument_list|(
name|conf
argument_list|,
name|Constants
operator|.
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
argument_list|,
name|src
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a LinkMergeSlash to the config for the specified mount table.    * @param conf    * @param mountTableName    * @param target    */
DECL|method|addLinkMergeSlash (Configuration conf, final String mountTableName, final URI target)
specifier|public
specifier|static
name|void
name|addLinkMergeSlash
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|mountTableName
parameter_list|,
specifier|final
name|URI
name|target
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|getConfigViewFsPrefix
argument_list|(
name|mountTableName
argument_list|)
operator|+
literal|"."
operator|+
name|Constants
operator|.
name|CONFIG_VIEWFS_LINK_MERGE_SLASH
argument_list|,
name|target
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a LinkMergeSlash to the config for the default mount table.    * @param conf    * @param target    */
DECL|method|addLinkMergeSlash (Configuration conf, final URI target)
specifier|public
specifier|static
name|void
name|addLinkMergeSlash
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|URI
name|target
parameter_list|)
block|{
name|addLinkMergeSlash
argument_list|(
name|conf
argument_list|,
name|Constants
operator|.
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a LinkFallback to the config for the specified mount table.    * @param conf    * @param mountTableName    * @param target    */
DECL|method|addLinkFallback (Configuration conf, final String mountTableName, final URI target)
specifier|public
specifier|static
name|void
name|addLinkFallback
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|mountTableName
parameter_list|,
specifier|final
name|URI
name|target
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|getConfigViewFsPrefix
argument_list|(
name|mountTableName
argument_list|)
operator|+
literal|"."
operator|+
name|Constants
operator|.
name|CONFIG_VIEWFS_LINK_FALLBACK
argument_list|,
name|target
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a LinkFallback to the config for the default mount table.    * @param conf    * @param target    */
DECL|method|addLinkFallback (Configuration conf, final URI target)
specifier|public
specifier|static
name|void
name|addLinkFallback
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|URI
name|target
parameter_list|)
block|{
name|addLinkFallback
argument_list|(
name|conf
argument_list|,
name|Constants
operator|.
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a LinkMerge to the config for the specified mount table.    * @param conf    * @param mountTableName    * @param targets    */
DECL|method|addLinkMerge (Configuration conf, final String mountTableName, final URI[] targets)
specifier|public
specifier|static
name|void
name|addLinkMerge
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|mountTableName
parameter_list|,
specifier|final
name|URI
index|[]
name|targets
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|getConfigViewFsPrefix
argument_list|(
name|mountTableName
argument_list|)
operator|+
literal|"."
operator|+
name|Constants
operator|.
name|CONFIG_VIEWFS_LINK_MERGE
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|targets
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a LinkMerge to the config for the default mount table.    * @param conf    * @param targets    */
DECL|method|addLinkMerge (Configuration conf, final URI[] targets)
specifier|public
specifier|static
name|void
name|addLinkMerge
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|URI
index|[]
name|targets
parameter_list|)
block|{
name|addLinkMerge
argument_list|(
name|conf
argument_list|,
name|Constants
operator|.
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
argument_list|,
name|targets
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * @param conf    * @param mountTableName    * @param src    * @param settings    * @param targets    */
DECL|method|addLinkNfly (Configuration conf, String mountTableName, String src, String settings, final URI ... targets)
specifier|public
specifier|static
name|void
name|addLinkNfly
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|mountTableName
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|settings
parameter_list|,
specifier|final
name|URI
modifier|...
name|targets
parameter_list|)
block|{
name|settings
operator|=
name|settings
operator|==
literal|null
condition|?
literal|"minReplication=2,repairOnRead=true"
else|:
name|settings
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|getConfigViewFsPrefix
argument_list|(
name|mountTableName
argument_list|)
operator|+
literal|"."
operator|+
name|Constants
operator|.
name|CONFIG_VIEWFS_LINK_NFLY
operator|+
literal|"."
operator|+
name|settings
operator|+
literal|"."
operator|+
name|src
argument_list|,
name|StringUtils
operator|.
name|uriToString
argument_list|(
name|targets
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addLinkNfly (final Configuration conf, final String src, final URI ... targets)
specifier|public
specifier|static
name|void
name|addLinkNfly
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|URI
modifier|...
name|targets
parameter_list|)
block|{
name|addLinkNfly
argument_list|(
name|conf
argument_list|,
name|Constants
operator|.
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
argument_list|,
name|src
argument_list|,
literal|null
argument_list|,
name|targets
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add config variable for homedir for default mount table    * @param conf - add to this conf    * @param homedir - the home dir path starting with slash    */
DECL|method|setHomeDirConf (final Configuration conf, final String homedir)
specifier|public
specifier|static
name|void
name|setHomeDirConf
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|homedir
parameter_list|)
block|{
name|setHomeDirConf
argument_list|(
name|conf
argument_list|,
name|Constants
operator|.
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
argument_list|,
name|homedir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add config variable for homedir the specified mount table    * @param conf - add to this conf    * @param homedir - the home dir path starting with slash    */
DECL|method|setHomeDirConf (final Configuration conf, final String mountTableName, final String homedir)
specifier|public
specifier|static
name|void
name|setHomeDirConf
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|mountTableName
parameter_list|,
specifier|final
name|String
name|homedir
parameter_list|)
block|{
if|if
condition|(
operator|!
name|homedir
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Home dir should start with /:"
operator|+
name|homedir
argument_list|)
throw|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|getConfigViewFsPrefix
argument_list|(
name|mountTableName
argument_list|)
operator|+
literal|"."
operator|+
name|Constants
operator|.
name|CONFIG_VIEWFS_HOMEDIR
argument_list|,
name|homedir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the value of the home dir conf value for default mount table    * @param conf - from this conf    * @return home dir value, null if variable is not in conf    */
DECL|method|getHomeDirValue (final Configuration conf)
specifier|public
specifier|static
name|String
name|getHomeDirValue
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getHomeDirValue
argument_list|(
name|conf
argument_list|,
name|Constants
operator|.
name|CONFIG_VIEWFS_DEFAULT_MOUNT_TABLE
argument_list|)
return|;
block|}
comment|/**    * Get the value of the home dir conf value for specified mount table    * @param conf - from this conf    * @param mountTableName - the mount table    * @return home dir value, null if variable is not in conf    */
DECL|method|getHomeDirValue (final Configuration conf, final String mountTableName)
specifier|public
specifier|static
name|String
name|getHomeDirValue
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|mountTableName
parameter_list|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|getConfigViewFsPrefix
argument_list|(
name|mountTableName
argument_list|)
operator|+
literal|"."
operator|+
name|Constants
operator|.
name|CONFIG_VIEWFS_HOMEDIR
argument_list|)
return|;
block|}
block|}
end_class

end_unit

