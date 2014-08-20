begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Option
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

begin_comment
comment|/**  * Enumeration mapping configuration keys to distcp command line  * options.  */
end_comment

begin_enum
DECL|enum|DistCpOptionSwitch
specifier|public
enum|enum
name|DistCpOptionSwitch
block|{
comment|/**    * Ignores any failures during copy, and continues with rest.    * Logs failures in a file    */
DECL|enumConstant|IGNORE_FAILURES
name|IGNORE_FAILURES
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_IGNORE_FAILURES
argument_list|,
operator|new
name|Option
argument_list|(
literal|"i"
argument_list|,
literal|false
argument_list|,
literal|"Ignore failures during copy"
argument_list|)
argument_list|)
block|,
comment|/**    * Preserves status of file/path in the target.    * Default behavior with -p, is to preserve replication,    * block size, user, group, permission and checksum type on the target file.    * Note that when preserving checksum type, block size is also preserved.    *    * If any of the optional switches are present among rbugpc, then    * only the corresponding file attribute is preserved.    *    */
DECL|enumConstant|PRESERVE_STATUS
name|PRESERVE_STATUS
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_PRESERVE_STATUS
argument_list|,
operator|new
name|Option
argument_list|(
literal|"p"
argument_list|,
literal|true
argument_list|,
literal|"preserve status (rbugpcax)(replication, "
operator|+
literal|"block-size, user, group, permission, checksum-type, ACL, XATTR).  "
operator|+
literal|"If -p is specified with no<arg>, then preserves replication, "
operator|+
literal|"block size, user, group, permission and checksum type."
operator|+
literal|"raw.* xattrs are preserved when both the source and destination "
operator|+
literal|"paths are in the /.reserved/raw hierarchy (HDFS only). raw.* xattr"
operator|+
literal|"preservation is independent of the -p flag."
operator|+
literal|"Refer to the DistCp documentation for more details."
argument_list|)
argument_list|)
block|,
comment|/**    * Update target location by copying only files that are missing    * in the target. This can be used to periodically sync two folders    * across source and target. Typically used with DELETE_MISSING    * Incompatible with ATOMIC_COMMIT    */
DECL|enumConstant|SYNC_FOLDERS
name|SYNC_FOLDERS
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_SYNC_FOLDERS
argument_list|,
operator|new
name|Option
argument_list|(
literal|"update"
argument_list|,
literal|false
argument_list|,
literal|"Update target, copying only missing"
operator|+
literal|"files or directories"
argument_list|)
argument_list|)
block|,
comment|/**    * Deletes missing files in target that are missing from source    * This allows the target to be in sync with the source contents    * Typically used in conjunction with SYNC_FOLDERS    * Incompatible with ATOMIC_COMMIT    */
DECL|enumConstant|DELETE_MISSING
name|DELETE_MISSING
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_DELETE_MISSING
argument_list|,
operator|new
name|Option
argument_list|(
literal|"delete"
argument_list|,
literal|false
argument_list|,
literal|"Delete from target, "
operator|+
literal|"files missing in source"
argument_list|)
argument_list|)
block|,
comment|/**    * Configuration file to use with hftps:// for securely copying    * files across clusters. Typically the configuration file contains    * truststore/keystore information such as location, password and type    */
DECL|enumConstant|SSL_CONF
name|SSL_CONF
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_SSL_CONF
argument_list|,
operator|new
name|Option
argument_list|(
literal|"mapredSslConf"
argument_list|,
literal|true
argument_list|,
literal|"Configuration for ssl config file"
operator|+
literal|", to use with hftps://"
argument_list|)
argument_list|)
block|,
comment|/**    * Max number of maps to use during copy. DistCp will split work    * as equally as possible among these maps    */
DECL|enumConstant|MAX_MAPS
name|MAX_MAPS
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_MAX_MAPS
argument_list|,
operator|new
name|Option
argument_list|(
literal|"m"
argument_list|,
literal|true
argument_list|,
literal|"Max number of concurrent maps to use for copy"
argument_list|)
argument_list|)
block|,
comment|/**    * Source file listing can be provided to DistCp in a file.    * This allows DistCp to copy random list of files from source    * and copy them to target    */
DECL|enumConstant|SOURCE_FILE_LISTING
name|SOURCE_FILE_LISTING
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_SOURCE_LISTING
argument_list|,
operator|new
name|Option
argument_list|(
literal|"f"
argument_list|,
literal|true
argument_list|,
literal|"List of files that need to be copied"
argument_list|)
argument_list|)
block|,
comment|/**    * Copy all the source files and commit them atomically to the target    * This is typically useful in cases where there is a process    * polling for availability of a file/dir. This option is incompatible    * with SYNC_FOLDERS& DELETE_MISSING    */
DECL|enumConstant|ATOMIC_COMMIT
name|ATOMIC_COMMIT
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_ATOMIC_COPY
argument_list|,
operator|new
name|Option
argument_list|(
literal|"atomic"
argument_list|,
literal|false
argument_list|,
literal|"Commit all changes or none"
argument_list|)
argument_list|)
block|,
comment|/**    * Work path to be used only in conjunction in Atomic commit    */
DECL|enumConstant|WORK_PATH
name|WORK_PATH
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_WORK_PATH
argument_list|,
operator|new
name|Option
argument_list|(
literal|"tmp"
argument_list|,
literal|true
argument_list|,
literal|"Intermediate work path to be used for atomic commit"
argument_list|)
argument_list|)
block|,
comment|/**    * Log path where distcp output logs are written to    */
DECL|enumConstant|LOG_PATH
name|LOG_PATH
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_LOG_PATH
argument_list|,
operator|new
name|Option
argument_list|(
literal|"log"
argument_list|,
literal|true
argument_list|,
literal|"Folder on DFS where distcp execution logs are saved"
argument_list|)
argument_list|)
block|,
comment|/**    * Copy strategy is use. This could be dynamic or uniform size etc.    * DistCp would use an appropriate input format based on this.    */
DECL|enumConstant|COPY_STRATEGY
name|COPY_STRATEGY
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_COPY_STRATEGY
argument_list|,
operator|new
name|Option
argument_list|(
literal|"strategy"
argument_list|,
literal|true
argument_list|,
literal|"Copy strategy to use. Default is "
operator|+
literal|"dividing work based on file sizes"
argument_list|)
argument_list|)
block|,
comment|/**    * Skip CRC checks between source and target, when determining what    * files need to be copied.    */
DECL|enumConstant|SKIP_CRC
name|SKIP_CRC
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_SKIP_CRC
argument_list|,
operator|new
name|Option
argument_list|(
literal|"skipcrccheck"
argument_list|,
literal|false
argument_list|,
literal|"Whether to skip CRC checks between "
operator|+
literal|"source and target paths."
argument_list|)
argument_list|)
block|,
comment|/**    * Overwrite target-files unconditionally.    */
DECL|enumConstant|OVERWRITE
name|OVERWRITE
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_OVERWRITE
argument_list|,
operator|new
name|Option
argument_list|(
literal|"overwrite"
argument_list|,
literal|false
argument_list|,
literal|"Choose to overwrite target files "
operator|+
literal|"unconditionally, even if they exist."
argument_list|)
argument_list|)
block|,
DECL|enumConstant|APPEND
name|APPEND
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_APPEND
argument_list|,
operator|new
name|Option
argument_list|(
literal|"append"
argument_list|,
literal|false
argument_list|,
literal|"Reuse existing data in target files and append new data to them if possible"
argument_list|)
argument_list|)
block|,
comment|/**    * Should DisctpExecution be blocking    */
DECL|enumConstant|BLOCKING
name|BLOCKING
argument_list|(
literal|""
argument_list|,
operator|new
name|Option
argument_list|(
literal|"async"
argument_list|,
literal|false
argument_list|,
literal|"Should distcp execution be blocking"
argument_list|)
argument_list|)
block|,
DECL|enumConstant|FILE_LIMIT
name|FILE_LIMIT
argument_list|(
literal|""
argument_list|,
operator|new
name|Option
argument_list|(
literal|"filelimit"
argument_list|,
literal|true
argument_list|,
literal|"(Deprecated!) Limit number of files "
operator|+
literal|"copied to<= n"
argument_list|)
argument_list|)
block|,
DECL|enumConstant|SIZE_LIMIT
name|SIZE_LIMIT
argument_list|(
literal|""
argument_list|,
operator|new
name|Option
argument_list|(
literal|"sizelimit"
argument_list|,
literal|true
argument_list|,
literal|"(Deprecated!) Limit number of files "
operator|+
literal|"copied to<= n bytes"
argument_list|)
argument_list|)
block|,
comment|/**    * Specify bandwidth per map in MB    */
DECL|enumConstant|BANDWIDTH
name|BANDWIDTH
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_BANDWIDTH_MB
argument_list|,
operator|new
name|Option
argument_list|(
literal|"bandwidth"
argument_list|,
literal|true
argument_list|,
literal|"Specify bandwidth per map in MB"
argument_list|)
argument_list|)
block|;
DECL|field|PRESERVE_STATUS_DEFAULT
specifier|static
specifier|final
name|String
name|PRESERVE_STATUS_DEFAULT
init|=
literal|"-prbugpc"
decl_stmt|;
DECL|field|confLabel
specifier|private
specifier|final
name|String
name|confLabel
decl_stmt|;
DECL|field|option
specifier|private
specifier|final
name|Option
name|option
decl_stmt|;
DECL|method|DistCpOptionSwitch (String confLabel, Option option)
name|DistCpOptionSwitch
parameter_list|(
name|String
name|confLabel
parameter_list|,
name|Option
name|option
parameter_list|)
block|{
name|this
operator|.
name|confLabel
operator|=
name|confLabel
expr_stmt|;
name|this
operator|.
name|option
operator|=
name|option
expr_stmt|;
block|}
comment|/**    * Get Configuration label for the option    * @return configuration label name    */
DECL|method|getConfigLabel ()
specifier|public
name|String
name|getConfigLabel
parameter_list|()
block|{
return|return
name|confLabel
return|;
block|}
comment|/**    * Get CLI Option corresponding to the distcp option    * @return option    */
DECL|method|getOption ()
specifier|public
name|Option
name|getOption
parameter_list|()
block|{
return|return
name|option
return|;
block|}
comment|/**    * Get Switch symbol    * @return switch symbol char    */
DECL|method|getSwitch ()
specifier|public
name|String
name|getSwitch
parameter_list|()
block|{
return|return
name|option
operator|.
name|getOpt
argument_list|()
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
return|return
name|super
operator|.
name|name
argument_list|()
operator|+
literal|" {"
operator|+
literal|"confLabel='"
operator|+
name|confLabel
operator|+
literal|'\''
operator|+
literal|", option="
operator|+
name|option
operator|+
literal|'}'
return|;
block|}
comment|/**    * Helper function to add an option to hadoop configuration object    * @param conf - Configuration object to include the option    * @param option - Option to add    * @param value - Value    */
DECL|method|addToConf (Configuration conf, DistCpOptionSwitch option, String value)
specifier|public
specifier|static
name|void
name|addToConf
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DistCpOptionSwitch
name|option
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|option
operator|.
name|getConfigLabel
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper function to set an option to hadoop configuration object    * @param conf - Configuration object to include the option    * @param option - Option to add    */
DECL|method|addToConf (Configuration conf, DistCpOptionSwitch option)
specifier|public
specifier|static
name|void
name|addToConf
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DistCpOptionSwitch
name|option
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|option
operator|.
name|getConfigLabel
argument_list|()
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
end_enum

end_unit

