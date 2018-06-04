begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|common
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
name|fs
operator|.
name|FileUtil
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|NodeType
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
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Storage information file. This Class defines the methods to check  * the consistency of the storage dir and the version file.  *<p>  * Local storage information is stored in a separate file VERSION.  * It contains type of the node,  * the storage layout version, the SCM id, and  * the KSM/SCM state creation time.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Storage
specifier|public
specifier|abstract
class|class
name|Storage
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Storage
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|STORAGE_DIR_CURRENT
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_DIR_CURRENT
init|=
literal|"current"
decl_stmt|;
DECL|field|STORAGE_FILE_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_FILE_VERSION
init|=
literal|"VERSION"
decl_stmt|;
DECL|field|STORAGE_DIR_HDDS
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_DIR_HDDS
init|=
literal|"hdds"
decl_stmt|;
DECL|field|nodeType
specifier|private
specifier|final
name|NodeType
name|nodeType
decl_stmt|;
DECL|field|root
specifier|private
specifier|final
name|File
name|root
decl_stmt|;
DECL|field|storageDir
specifier|private
specifier|final
name|File
name|storageDir
decl_stmt|;
DECL|field|state
specifier|private
name|StorageState
name|state
decl_stmt|;
DECL|field|storageInfo
specifier|private
name|StorageInfo
name|storageInfo
decl_stmt|;
comment|/**    * Determines the state of the Version file.    */
DECL|enum|StorageState
specifier|public
enum|enum
name|StorageState
block|{
DECL|enumConstant|NON_EXISTENT
DECL|enumConstant|NOT_INITIALIZED
DECL|enumConstant|INITIALIZED
name|NON_EXISTENT
block|,
name|NOT_INITIALIZED
block|,
name|INITIALIZED
block|}
DECL|method|Storage (NodeType type, File root, String sdName)
specifier|public
name|Storage
parameter_list|(
name|NodeType
name|type
parameter_list|,
name|File
name|root
parameter_list|,
name|String
name|sdName
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|nodeType
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|storageDir
operator|=
operator|new
name|File
argument_list|(
name|root
argument_list|,
name|sdName
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|getStorageState
argument_list|()
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|StorageState
operator|.
name|INITIALIZED
condition|)
block|{
name|this
operator|.
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
name|type
argument_list|,
name|getVersionFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
name|nodeType
argument_list|,
name|StorageInfo
operator|.
name|newClusterID
argument_list|()
argument_list|,
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
name|setNodeProperties
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Gets the path of the Storage dir.    * @return Stoarge dir path    */
DECL|method|getStorageDir ()
specifier|public
name|String
name|getStorageDir
parameter_list|()
block|{
return|return
name|storageDir
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Gets the state of the version file.    * @return the state of the Version file    */
DECL|method|getState ()
specifier|public
name|StorageState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getNodeType ()
specifier|public
name|NodeType
name|getNodeType
parameter_list|()
block|{
return|return
name|storageInfo
operator|.
name|getNodeType
argument_list|()
return|;
block|}
DECL|method|getClusterID ()
specifier|public
name|String
name|getClusterID
parameter_list|()
block|{
return|return
name|storageInfo
operator|.
name|getClusterID
argument_list|()
return|;
block|}
DECL|method|getCreationTime ()
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|storageInfo
operator|.
name|getCreationTime
argument_list|()
return|;
block|}
DECL|method|setClusterId (String clusterId)
specifier|public
name|void
name|setClusterId
parameter_list|(
name|String
name|clusterId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|==
name|StorageState
operator|.
name|INITIALIZED
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Storage directory "
operator|+
name|storageDir
operator|+
literal|" already initialized."
argument_list|)
throw|;
block|}
else|else
block|{
name|storageInfo
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Retreives the storageInfo instance to read/write the common    * version file properties.    * @return the instance of the storageInfo class    */
DECL|method|getStorageInfo ()
specifier|protected
name|StorageInfo
name|getStorageInfo
parameter_list|()
block|{
return|return
name|storageInfo
return|;
block|}
DECL|method|getNodeProperties ()
specifier|abstract
specifier|protected
name|Properties
name|getNodeProperties
parameter_list|()
function_decl|;
comment|/**    * Sets the Node properties spaecific to KSM/SCM.    */
DECL|method|setNodeProperties ()
specifier|private
name|void
name|setNodeProperties
parameter_list|()
block|{
name|Properties
name|nodeProperties
init|=
name|getNodeProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeProperties
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|key
range|:
name|nodeProperties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|storageInfo
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|nodeProperties
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Directory {@code current} contains latest files defining    * the file system meta-data.    *    * @return the directory path    */
DECL|method|getCurrentDir ()
specifier|private
name|File
name|getCurrentDir
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|storageDir
argument_list|,
name|STORAGE_DIR_CURRENT
argument_list|)
return|;
block|}
comment|/**    * File {@code VERSION} contains the following fields:    *<ol>    *<li>node type</li>    *<li>KSM/SCM state creation time</li>    *<li>other fields specific for this node type</li>    *</ol>    * The version file is always written last during storage directory updates.    * The existence of the version file indicates that all other files have    * been successfully written in the storage directory, the storage is valid    * and does not need to be recovered.    *    * @return the version file path    */
DECL|method|getVersionFile ()
specifier|private
name|File
name|getVersionFile
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|getCurrentDir
argument_list|()
argument_list|,
name|STORAGE_FILE_VERSION
argument_list|)
return|;
block|}
comment|/**    * Check to see if current/ directory is empty. This method is used    * before determining to format the directory.    * @throws IOException if unable to list files under the directory.    */
DECL|method|checkEmptyCurrent ()
specifier|private
name|void
name|checkEmptyCurrent
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|currentDir
init|=
name|getCurrentDir
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|currentDir
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// if current/ does not exist, it's safe to format it.
return|return;
block|}
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|dirStream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|currentDir
operator|.
name|toPath
argument_list|()
argument_list|)
init|)
block|{
if|if
condition|(
name|dirStream
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InconsistentStorageStateException
argument_list|(
name|getCurrentDir
argument_list|()
argument_list|,
literal|"Can't initialize the storage directory because the current "
operator|+
literal|"it is not empty."
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Check consistency of the storage directory.    *    * @return state {@link StorageState} of the storage directory    * @throws IOException    */
DECL|method|getStorageState ()
specifier|private
name|StorageState
name|getStorageState
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|root
operator|!=
literal|null
operator|:
literal|"root is null"
assert|;
name|String
name|rootPath
init|=
name|root
operator|.
name|getCanonicalPath
argument_list|()
decl_stmt|;
try|try
block|{
comment|// check that storage exists
if|if
condition|(
operator|!
name|root
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// storage directory does not exist
name|LOG
operator|.
name|warn
argument_list|(
literal|"Storage directory "
operator|+
name|rootPath
operator|+
literal|" does not exist"
argument_list|)
expr_stmt|;
return|return
name|StorageState
operator|.
name|NON_EXISTENT
return|;
block|}
comment|// or is inaccessible
if|if
condition|(
operator|!
name|root
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|rootPath
operator|+
literal|"is not a directory"
argument_list|)
expr_stmt|;
return|return
name|StorageState
operator|.
name|NON_EXISTENT
return|;
block|}
if|if
condition|(
operator|!
name|FileUtil
operator|.
name|canWrite
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot access storage directory "
operator|+
name|rootPath
argument_list|)
expr_stmt|;
return|return
name|StorageState
operator|.
name|NON_EXISTENT
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SecurityException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot access storage directory "
operator|+
name|rootPath
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
name|StorageState
operator|.
name|NON_EXISTENT
return|;
block|}
comment|// check whether current directory is valid
name|File
name|versionFile
init|=
name|getVersionFile
argument_list|()
decl_stmt|;
name|boolean
name|hasCurrent
init|=
name|versionFile
operator|.
name|exists
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasCurrent
condition|)
block|{
return|return
name|StorageState
operator|.
name|INITIALIZED
return|;
block|}
else|else
block|{
name|checkEmptyCurrent
argument_list|()
expr_stmt|;
return|return
name|StorageState
operator|.
name|NOT_INITIALIZED
return|;
block|}
block|}
comment|/**    * Creates the Version file if not present,    * otherwise returns with IOException.    * @throws IOException    */
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|==
name|StorageState
operator|.
name|INITIALIZED
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Storage directory already initialized."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|getCurrentDir
argument_list|()
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create directory "
operator|+
name|getCurrentDir
argument_list|()
argument_list|)
throw|;
block|}
name|storageInfo
operator|.
name|writeTo
argument_list|(
name|getVersionFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

