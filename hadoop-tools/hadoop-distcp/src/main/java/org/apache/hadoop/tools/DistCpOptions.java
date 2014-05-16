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
name|tools
operator|.
name|util
operator|.
name|DistCpUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * The Options class encapsulates all DistCp options.  * These may be set from command-line (via the OptionsParser)  * or may be set manually.  */
end_comment

begin_class
DECL|class|DistCpOptions
specifier|public
class|class
name|DistCpOptions
block|{
DECL|field|atomicCommit
specifier|private
name|boolean
name|atomicCommit
init|=
literal|false
decl_stmt|;
DECL|field|syncFolder
specifier|private
name|boolean
name|syncFolder
init|=
literal|false
decl_stmt|;
DECL|field|deleteMissing
specifier|private
name|boolean
name|deleteMissing
init|=
literal|false
decl_stmt|;
DECL|field|ignoreFailures
specifier|private
name|boolean
name|ignoreFailures
init|=
literal|false
decl_stmt|;
DECL|field|overwrite
specifier|private
name|boolean
name|overwrite
init|=
literal|false
decl_stmt|;
DECL|field|skipCRC
specifier|private
name|boolean
name|skipCRC
init|=
literal|false
decl_stmt|;
DECL|field|blocking
specifier|private
name|boolean
name|blocking
init|=
literal|true
decl_stmt|;
DECL|field|maxMaps
specifier|private
name|int
name|maxMaps
init|=
name|DistCpConstants
operator|.
name|DEFAULT_MAPS
decl_stmt|;
DECL|field|mapBandwidth
specifier|private
name|int
name|mapBandwidth
init|=
name|DistCpConstants
operator|.
name|DEFAULT_BANDWIDTH_MB
decl_stmt|;
DECL|field|sslConfigurationFile
specifier|private
name|String
name|sslConfigurationFile
decl_stmt|;
DECL|field|copyStrategy
specifier|private
name|String
name|copyStrategy
init|=
name|DistCpConstants
operator|.
name|UNIFORMSIZE
decl_stmt|;
DECL|field|preserveStatus
specifier|private
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|preserveStatus
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|FileAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|atomicWorkPath
specifier|private
name|Path
name|atomicWorkPath
decl_stmt|;
DECL|field|logPath
specifier|private
name|Path
name|logPath
decl_stmt|;
DECL|field|sourceFileListing
specifier|private
name|Path
name|sourceFileListing
decl_stmt|;
DECL|field|sourcePaths
specifier|private
name|List
argument_list|<
name|Path
argument_list|>
name|sourcePaths
decl_stmt|;
DECL|field|targetPath
specifier|private
name|Path
name|targetPath
decl_stmt|;
comment|// targetPathExist is a derived field, it's initialized in the
comment|// beginning of distcp.
DECL|field|targetPathExists
specifier|private
name|boolean
name|targetPathExists
init|=
literal|true
decl_stmt|;
DECL|enum|FileAttribute
specifier|public
specifier|static
enum|enum
name|FileAttribute
block|{
DECL|enumConstant|REPLICATION
DECL|enumConstant|BLOCKSIZE
DECL|enumConstant|USER
DECL|enumConstant|GROUP
DECL|enumConstant|PERMISSION
DECL|enumConstant|CHECKSUMTYPE
DECL|enumConstant|ACL
name|REPLICATION
block|,
name|BLOCKSIZE
block|,
name|USER
block|,
name|GROUP
block|,
name|PERMISSION
block|,
name|CHECKSUMTYPE
block|,
name|ACL
block|;
DECL|method|getAttribute (char symbol)
specifier|public
specifier|static
name|FileAttribute
name|getAttribute
parameter_list|(
name|char
name|symbol
parameter_list|)
block|{
for|for
control|(
name|FileAttribute
name|attribute
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|attribute
operator|.
name|name
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|Character
operator|.
name|toUpperCase
argument_list|(
name|symbol
argument_list|)
condition|)
block|{
return|return
name|attribute
return|;
block|}
block|}
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"No attribute for "
operator|+
name|symbol
argument_list|)
throw|;
block|}
block|}
comment|/**    * Constructor, to initialize source/target paths.    * @param sourcePaths List of source-paths (including wildcards)    *                     to be copied to target.    * @param targetPath Destination path for the dist-copy.    */
DECL|method|DistCpOptions (List<Path> sourcePaths, Path targetPath)
specifier|public
name|DistCpOptions
parameter_list|(
name|List
argument_list|<
name|Path
argument_list|>
name|sourcePaths
parameter_list|,
name|Path
name|targetPath
parameter_list|)
block|{
assert|assert
name|sourcePaths
operator|!=
literal|null
operator|&&
operator|!
name|sourcePaths
operator|.
name|isEmpty
argument_list|()
operator|:
literal|"Invalid source paths"
assert|;
assert|assert
name|targetPath
operator|!=
literal|null
operator|:
literal|"Invalid Target path"
assert|;
name|this
operator|.
name|sourcePaths
operator|=
name|sourcePaths
expr_stmt|;
name|this
operator|.
name|targetPath
operator|=
name|targetPath
expr_stmt|;
block|}
comment|/**    * Constructor, to initialize source/target paths.    * @param sourceFileListing File containing list of source paths    * @param targetPath Destination path for the dist-copy.    */
DECL|method|DistCpOptions (Path sourceFileListing, Path targetPath)
specifier|public
name|DistCpOptions
parameter_list|(
name|Path
name|sourceFileListing
parameter_list|,
name|Path
name|targetPath
parameter_list|)
block|{
assert|assert
name|sourceFileListing
operator|!=
literal|null
operator|:
literal|"Invalid source paths"
assert|;
assert|assert
name|targetPath
operator|!=
literal|null
operator|:
literal|"Invalid Target path"
assert|;
name|this
operator|.
name|sourceFileListing
operator|=
name|sourceFileListing
expr_stmt|;
name|this
operator|.
name|targetPath
operator|=
name|targetPath
expr_stmt|;
block|}
comment|/**    * Copy constructor.    * @param that DistCpOptions being copied from.    */
DECL|method|DistCpOptions (DistCpOptions that)
specifier|public
name|DistCpOptions
parameter_list|(
name|DistCpOptions
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|!=
name|that
operator|&&
name|that
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|atomicCommit
operator|=
name|that
operator|.
name|atomicCommit
expr_stmt|;
name|this
operator|.
name|syncFolder
operator|=
name|that
operator|.
name|syncFolder
expr_stmt|;
name|this
operator|.
name|deleteMissing
operator|=
name|that
operator|.
name|deleteMissing
expr_stmt|;
name|this
operator|.
name|ignoreFailures
operator|=
name|that
operator|.
name|ignoreFailures
expr_stmt|;
name|this
operator|.
name|overwrite
operator|=
name|that
operator|.
name|overwrite
expr_stmt|;
name|this
operator|.
name|skipCRC
operator|=
name|that
operator|.
name|skipCRC
expr_stmt|;
name|this
operator|.
name|blocking
operator|=
name|that
operator|.
name|blocking
expr_stmt|;
name|this
operator|.
name|maxMaps
operator|=
name|that
operator|.
name|maxMaps
expr_stmt|;
name|this
operator|.
name|mapBandwidth
operator|=
name|that
operator|.
name|mapBandwidth
expr_stmt|;
name|this
operator|.
name|sslConfigurationFile
operator|=
name|that
operator|.
name|getSslConfigurationFile
argument_list|()
expr_stmt|;
name|this
operator|.
name|copyStrategy
operator|=
name|that
operator|.
name|copyStrategy
expr_stmt|;
name|this
operator|.
name|preserveStatus
operator|=
name|that
operator|.
name|preserveStatus
expr_stmt|;
name|this
operator|.
name|atomicWorkPath
operator|=
name|that
operator|.
name|getAtomicWorkPath
argument_list|()
expr_stmt|;
name|this
operator|.
name|logPath
operator|=
name|that
operator|.
name|getLogPath
argument_list|()
expr_stmt|;
name|this
operator|.
name|sourceFileListing
operator|=
name|that
operator|.
name|getSourceFileListing
argument_list|()
expr_stmt|;
name|this
operator|.
name|sourcePaths
operator|=
name|that
operator|.
name|getSourcePaths
argument_list|()
expr_stmt|;
name|this
operator|.
name|targetPath
operator|=
name|that
operator|.
name|getTargetPath
argument_list|()
expr_stmt|;
name|this
operator|.
name|targetPathExists
operator|=
name|that
operator|.
name|getTargetPathExists
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Should the data be committed atomically?    *    * @return true if data should be committed automically. false otherwise    */
DECL|method|shouldAtomicCommit ()
specifier|public
name|boolean
name|shouldAtomicCommit
parameter_list|()
block|{
return|return
name|atomicCommit
return|;
block|}
comment|/**    * Set if data need to be committed automatically    *    * @param atomicCommit - boolean switch    */
DECL|method|setAtomicCommit (boolean atomicCommit)
specifier|public
name|void
name|setAtomicCommit
parameter_list|(
name|boolean
name|atomicCommit
parameter_list|)
block|{
name|validate
argument_list|(
name|DistCpOptionSwitch
operator|.
name|ATOMIC_COMMIT
argument_list|,
name|atomicCommit
argument_list|)
expr_stmt|;
name|this
operator|.
name|atomicCommit
operator|=
name|atomicCommit
expr_stmt|;
block|}
comment|/**    * Should the data be sync'ed between source and target paths?    *    * @return true if data should be sync'ed up. false otherwise    */
DECL|method|shouldSyncFolder ()
specifier|public
name|boolean
name|shouldSyncFolder
parameter_list|()
block|{
return|return
name|syncFolder
return|;
block|}
comment|/**    * Set if source and target folder contents be sync'ed up    *    * @param syncFolder - boolean switch    */
DECL|method|setSyncFolder (boolean syncFolder)
specifier|public
name|void
name|setSyncFolder
parameter_list|(
name|boolean
name|syncFolder
parameter_list|)
block|{
name|validate
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SYNC_FOLDERS
argument_list|,
name|syncFolder
argument_list|)
expr_stmt|;
name|this
operator|.
name|syncFolder
operator|=
name|syncFolder
expr_stmt|;
block|}
comment|/**    * Should target files missing in source should be deleted?    *    * @return true if zoombie target files to be removed. false otherwise    */
DECL|method|shouldDeleteMissing ()
specifier|public
name|boolean
name|shouldDeleteMissing
parameter_list|()
block|{
return|return
name|deleteMissing
return|;
block|}
comment|/**    * Set if files only present in target should be deleted    *    * @param deleteMissing - boolean switch    */
DECL|method|setDeleteMissing (boolean deleteMissing)
specifier|public
name|void
name|setDeleteMissing
parameter_list|(
name|boolean
name|deleteMissing
parameter_list|)
block|{
name|validate
argument_list|(
name|DistCpOptionSwitch
operator|.
name|DELETE_MISSING
argument_list|,
name|deleteMissing
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteMissing
operator|=
name|deleteMissing
expr_stmt|;
block|}
comment|/**    * Should failures be logged and ignored during copy?    *    * @return true if failures are to be logged and ignored. false otherwise    */
DECL|method|shouldIgnoreFailures ()
specifier|public
name|boolean
name|shouldIgnoreFailures
parameter_list|()
block|{
return|return
name|ignoreFailures
return|;
block|}
comment|/**    * Set if failures during copy be ignored    *    * @param ignoreFailures - boolean switch    */
DECL|method|setIgnoreFailures (boolean ignoreFailures)
specifier|public
name|void
name|setIgnoreFailures
parameter_list|(
name|boolean
name|ignoreFailures
parameter_list|)
block|{
name|this
operator|.
name|ignoreFailures
operator|=
name|ignoreFailures
expr_stmt|;
block|}
comment|/**    * Should DistCp be running in blocking mode    *    * @return true if should run in blocking, false otherwise    */
DECL|method|shouldBlock ()
specifier|public
name|boolean
name|shouldBlock
parameter_list|()
block|{
return|return
name|blocking
return|;
block|}
comment|/**    * Set if Disctp should run blocking or non-blocking    *    * @param blocking - boolean switch    */
DECL|method|setBlocking (boolean blocking)
specifier|public
name|void
name|setBlocking
parameter_list|(
name|boolean
name|blocking
parameter_list|)
block|{
name|this
operator|.
name|blocking
operator|=
name|blocking
expr_stmt|;
block|}
comment|/**    * Should files be overwritten always?    *    * @return true if files in target that may exist before distcp, should always    *         be overwritten. false otherwise    */
DECL|method|shouldOverwrite ()
specifier|public
name|boolean
name|shouldOverwrite
parameter_list|()
block|{
return|return
name|overwrite
return|;
block|}
comment|/**    * Set if files should always be overwritten on target    *    * @param overwrite - boolean switch    */
DECL|method|setOverwrite (boolean overwrite)
specifier|public
name|void
name|setOverwrite
parameter_list|(
name|boolean
name|overwrite
parameter_list|)
block|{
name|validate
argument_list|(
name|DistCpOptionSwitch
operator|.
name|OVERWRITE
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
name|this
operator|.
name|overwrite
operator|=
name|overwrite
expr_stmt|;
block|}
comment|/**    * Should CRC/checksum check be skipped while checking files are identical    *    * @return true if checksum check should be skipped while checking files are    *         identical. false otherwise    */
DECL|method|shouldSkipCRC ()
specifier|public
name|boolean
name|shouldSkipCRC
parameter_list|()
block|{
return|return
name|skipCRC
return|;
block|}
comment|/**    * Set if checksum comparison should be skipped while determining if    * source and destination files are identical    *    * @param skipCRC - boolean switch    */
DECL|method|setSkipCRC (boolean skipCRC)
specifier|public
name|void
name|setSkipCRC
parameter_list|(
name|boolean
name|skipCRC
parameter_list|)
block|{
name|validate
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SKIP_CRC
argument_list|,
name|skipCRC
argument_list|)
expr_stmt|;
name|this
operator|.
name|skipCRC
operator|=
name|skipCRC
expr_stmt|;
block|}
comment|/** Get the max number of maps to use for this copy    *    * @return Max number of maps    */
DECL|method|getMaxMaps ()
specifier|public
name|int
name|getMaxMaps
parameter_list|()
block|{
return|return
name|maxMaps
return|;
block|}
comment|/**    * Set the max number of maps to use for copy    *    * @param maxMaps - Number of maps    */
DECL|method|setMaxMaps (int maxMaps)
specifier|public
name|void
name|setMaxMaps
parameter_list|(
name|int
name|maxMaps
parameter_list|)
block|{
name|this
operator|.
name|maxMaps
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxMaps
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Get the map bandwidth in MB    *    * @return Bandwidth in MB    */
DECL|method|getMapBandwidth ()
specifier|public
name|int
name|getMapBandwidth
parameter_list|()
block|{
return|return
name|mapBandwidth
return|;
block|}
comment|/**    * Set per map bandwidth    *    * @param mapBandwidth - per map bandwidth    */
DECL|method|setMapBandwidth (int mapBandwidth)
specifier|public
name|void
name|setMapBandwidth
parameter_list|(
name|int
name|mapBandwidth
parameter_list|)
block|{
assert|assert
name|mapBandwidth
operator|>
literal|0
operator|:
literal|"Bandwidth "
operator|+
name|mapBandwidth
operator|+
literal|" is invalid (should be> 0)"
assert|;
name|this
operator|.
name|mapBandwidth
operator|=
name|mapBandwidth
expr_stmt|;
block|}
comment|/**    * Get path where the ssl configuration file is present to use for hftps://    *    * @return Path on local file system    */
DECL|method|getSslConfigurationFile ()
specifier|public
name|String
name|getSslConfigurationFile
parameter_list|()
block|{
return|return
name|sslConfigurationFile
return|;
block|}
comment|/**    * Set the SSL configuration file path to use with hftps:// (local path)    *    * @param sslConfigurationFile - Local ssl config file path    */
DECL|method|setSslConfigurationFile (String sslConfigurationFile)
specifier|public
name|void
name|setSslConfigurationFile
parameter_list|(
name|String
name|sslConfigurationFile
parameter_list|)
block|{
name|this
operator|.
name|sslConfigurationFile
operator|=
name|sslConfigurationFile
expr_stmt|;
block|}
comment|/**    * Returns an iterator with the list of file attributes to preserve    *    * @return iterator of file attributes to preserve    */
DECL|method|preserveAttributes ()
specifier|public
name|Iterator
argument_list|<
name|FileAttribute
argument_list|>
name|preserveAttributes
parameter_list|()
block|{
return|return
name|preserveStatus
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Checks if the input attibute should be preserved or not    *    * @param attribute - Attribute to check    * @return True if attribute should be preserved, false otherwise    */
DECL|method|shouldPreserve (FileAttribute attribute)
specifier|public
name|boolean
name|shouldPreserve
parameter_list|(
name|FileAttribute
name|attribute
parameter_list|)
block|{
return|return
name|preserveStatus
operator|.
name|contains
argument_list|(
name|attribute
argument_list|)
return|;
block|}
comment|/**    * Add file attributes that need to be preserved. This method may be    * called multiple times to add attributes.    *    * @param fileAttribute - Attribute to add, one at a time    */
DECL|method|preserve (FileAttribute fileAttribute)
specifier|public
name|void
name|preserve
parameter_list|(
name|FileAttribute
name|fileAttribute
parameter_list|)
block|{
for|for
control|(
name|FileAttribute
name|attribute
range|:
name|preserveStatus
control|)
block|{
if|if
condition|(
name|attribute
operator|.
name|equals
argument_list|(
name|fileAttribute
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|preserveStatus
operator|.
name|add
argument_list|(
name|fileAttribute
argument_list|)
expr_stmt|;
block|}
comment|/** Get work path for atomic commit. If null, the work    * path would be parentOf(targetPath) + "/._WIP_" + nameOf(targetPath)    *    * @return Atomic work path on the target cluster. Null if not set    */
DECL|method|getAtomicWorkPath ()
specifier|public
name|Path
name|getAtomicWorkPath
parameter_list|()
block|{
return|return
name|atomicWorkPath
return|;
block|}
comment|/**    * Set the work path for atomic commit    *    * @param atomicWorkPath - Path on the target cluster    */
DECL|method|setAtomicWorkPath (Path atomicWorkPath)
specifier|public
name|void
name|setAtomicWorkPath
parameter_list|(
name|Path
name|atomicWorkPath
parameter_list|)
block|{
name|this
operator|.
name|atomicWorkPath
operator|=
name|atomicWorkPath
expr_stmt|;
block|}
comment|/** Get output directory for writing distcp logs. Otherwise logs    * are temporarily written to JobStagingDir/_logs and deleted    * upon job completion    *    * @return Log output path on the cluster where distcp job is run    */
DECL|method|getLogPath ()
specifier|public
name|Path
name|getLogPath
parameter_list|()
block|{
return|return
name|logPath
return|;
block|}
comment|/**    * Set the log path where distcp output logs are stored    * Uses JobStagingDir/_logs by default    *    * @param logPath - Path where logs will be saved    */
DECL|method|setLogPath (Path logPath)
specifier|public
name|void
name|setLogPath
parameter_list|(
name|Path
name|logPath
parameter_list|)
block|{
name|this
operator|.
name|logPath
operator|=
name|logPath
expr_stmt|;
block|}
comment|/**    * Get the copy strategy to use. Uses appropriate input format    *    * @return copy strategy to use    */
DECL|method|getCopyStrategy ()
specifier|public
name|String
name|getCopyStrategy
parameter_list|()
block|{
return|return
name|copyStrategy
return|;
block|}
comment|/**    * Set the copy strategy to use. Should map to a strategy implementation    * in distp-default.xml    *    * @param copyStrategy - copy Strategy to use    */
DECL|method|setCopyStrategy (String copyStrategy)
specifier|public
name|void
name|setCopyStrategy
parameter_list|(
name|String
name|copyStrategy
parameter_list|)
block|{
name|this
operator|.
name|copyStrategy
operator|=
name|copyStrategy
expr_stmt|;
block|}
comment|/**    * File path (hdfs:// or file://) that contains the list of actual    * files to copy    *    * @return - Source listing file path    */
DECL|method|getSourceFileListing ()
specifier|public
name|Path
name|getSourceFileListing
parameter_list|()
block|{
return|return
name|sourceFileListing
return|;
block|}
comment|/**    * Getter for sourcePaths.    * @return List of source-paths.    */
DECL|method|getSourcePaths ()
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getSourcePaths
parameter_list|()
block|{
return|return
name|sourcePaths
return|;
block|}
comment|/**    * Setter for sourcePaths.    * @param sourcePaths The new list of source-paths.    */
DECL|method|setSourcePaths (List<Path> sourcePaths)
specifier|public
name|void
name|setSourcePaths
parameter_list|(
name|List
argument_list|<
name|Path
argument_list|>
name|sourcePaths
parameter_list|)
block|{
assert|assert
name|sourcePaths
operator|!=
literal|null
operator|&&
name|sourcePaths
operator|.
name|size
argument_list|()
operator|!=
literal|0
assert|;
name|this
operator|.
name|sourcePaths
operator|=
name|sourcePaths
expr_stmt|;
block|}
comment|/**    * Getter for the targetPath.    * @return The target-path.    */
DECL|method|getTargetPath ()
specifier|public
name|Path
name|getTargetPath
parameter_list|()
block|{
return|return
name|targetPath
return|;
block|}
comment|/**    * Getter for the targetPathExists.    * @return The target-path.    */
DECL|method|getTargetPathExists ()
specifier|public
name|boolean
name|getTargetPathExists
parameter_list|()
block|{
return|return
name|targetPathExists
return|;
block|}
comment|/**    * Set targetPathExists.    * @param targetPathExists Whether the target path of distcp exists.    */
DECL|method|setTargetPathExists (boolean targetPathExists)
specifier|public
name|boolean
name|setTargetPathExists
parameter_list|(
name|boolean
name|targetPathExists
parameter_list|)
block|{
return|return
name|this
operator|.
name|targetPathExists
operator|=
name|targetPathExists
return|;
block|}
DECL|method|validate (DistCpOptionSwitch option, boolean value)
specifier|public
name|void
name|validate
parameter_list|(
name|DistCpOptionSwitch
name|option
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|boolean
name|syncFolder
init|=
operator|(
name|option
operator|==
name|DistCpOptionSwitch
operator|.
name|SYNC_FOLDERS
condition|?
name|value
else|:
name|this
operator|.
name|syncFolder
operator|)
decl_stmt|;
name|boolean
name|overwrite
init|=
operator|(
name|option
operator|==
name|DistCpOptionSwitch
operator|.
name|OVERWRITE
condition|?
name|value
else|:
name|this
operator|.
name|overwrite
operator|)
decl_stmt|;
name|boolean
name|deleteMissing
init|=
operator|(
name|option
operator|==
name|DistCpOptionSwitch
operator|.
name|DELETE_MISSING
condition|?
name|value
else|:
name|this
operator|.
name|deleteMissing
operator|)
decl_stmt|;
name|boolean
name|atomicCommit
init|=
operator|(
name|option
operator|==
name|DistCpOptionSwitch
operator|.
name|ATOMIC_COMMIT
condition|?
name|value
else|:
name|this
operator|.
name|atomicCommit
operator|)
decl_stmt|;
name|boolean
name|skipCRC
init|=
operator|(
name|option
operator|==
name|DistCpOptionSwitch
operator|.
name|SKIP_CRC
condition|?
name|value
else|:
name|this
operator|.
name|skipCRC
operator|)
decl_stmt|;
if|if
condition|(
name|syncFolder
operator|&&
name|atomicCommit
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Atomic commit can't be used with "
operator|+
literal|"sync folder or overwrite options"
argument_list|)
throw|;
block|}
if|if
condition|(
name|deleteMissing
operator|&&
operator|!
operator|(
name|overwrite
operator|||
name|syncFolder
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Delete missing is applicable "
operator|+
literal|"only with update or overwrite options"
argument_list|)
throw|;
block|}
if|if
condition|(
name|overwrite
operator|&&
name|syncFolder
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Overwrite and update options are "
operator|+
literal|"mutually exclusive"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|syncFolder
operator|&&
name|skipCRC
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Skip CRC is valid only with update options"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Add options to configuration. These will be used in the Mapper/committer    *    * @param conf - Configruation object to which the options need to be added    */
DECL|method|appendToConf (Configuration conf)
specifier|public
name|void
name|appendToConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|DistCpOptionSwitch
operator|.
name|addToConf
argument_list|(
name|conf
argument_list|,
name|DistCpOptionSwitch
operator|.
name|ATOMIC_COMMIT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|atomicCommit
argument_list|)
argument_list|)
expr_stmt|;
name|DistCpOptionSwitch
operator|.
name|addToConf
argument_list|(
name|conf
argument_list|,
name|DistCpOptionSwitch
operator|.
name|IGNORE_FAILURES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ignoreFailures
argument_list|)
argument_list|)
expr_stmt|;
name|DistCpOptionSwitch
operator|.
name|addToConf
argument_list|(
name|conf
argument_list|,
name|DistCpOptionSwitch
operator|.
name|SYNC_FOLDERS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|syncFolder
argument_list|)
argument_list|)
expr_stmt|;
name|DistCpOptionSwitch
operator|.
name|addToConf
argument_list|(
name|conf
argument_list|,
name|DistCpOptionSwitch
operator|.
name|DELETE_MISSING
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|deleteMissing
argument_list|)
argument_list|)
expr_stmt|;
name|DistCpOptionSwitch
operator|.
name|addToConf
argument_list|(
name|conf
argument_list|,
name|DistCpOptionSwitch
operator|.
name|OVERWRITE
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|overwrite
argument_list|)
argument_list|)
expr_stmt|;
name|DistCpOptionSwitch
operator|.
name|addToConf
argument_list|(
name|conf
argument_list|,
name|DistCpOptionSwitch
operator|.
name|SKIP_CRC
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|skipCRC
argument_list|)
argument_list|)
expr_stmt|;
name|DistCpOptionSwitch
operator|.
name|addToConf
argument_list|(
name|conf
argument_list|,
name|DistCpOptionSwitch
operator|.
name|BANDWIDTH
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|mapBandwidth
argument_list|)
argument_list|)
expr_stmt|;
name|DistCpOptionSwitch
operator|.
name|addToConf
argument_list|(
name|conf
argument_list|,
name|DistCpOptionSwitch
operator|.
name|PRESERVE_STATUS
argument_list|,
name|DistCpUtils
operator|.
name|packAttributes
argument_list|(
name|preserveStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility to easily string-ify Options, for logging.    *    * @return String representation of the Options.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DistCpOptions{"
operator|+
literal|"atomicCommit="
operator|+
name|atomicCommit
operator|+
literal|", syncFolder="
operator|+
name|syncFolder
operator|+
literal|", deleteMissing="
operator|+
name|deleteMissing
operator|+
literal|", ignoreFailures="
operator|+
name|ignoreFailures
operator|+
literal|", maxMaps="
operator|+
name|maxMaps
operator|+
literal|", sslConfigurationFile='"
operator|+
name|sslConfigurationFile
operator|+
literal|'\''
operator|+
literal|", copyStrategy='"
operator|+
name|copyStrategy
operator|+
literal|'\''
operator|+
literal|", sourceFileListing="
operator|+
name|sourceFileListing
operator|+
literal|", sourcePaths="
operator|+
name|sourcePaths
operator|+
literal|", targetPath="
operator|+
name|targetPath
operator|+
literal|", targetPathExists="
operator|+
name|targetPathExists
operator|+
literal|'}'
return|;
block|}
annotation|@
name|Override
DECL|method|clone ()
specifier|protected
name|DistCpOptions
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
return|return
operator|(
name|DistCpOptions
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class

end_unit

