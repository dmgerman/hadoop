begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|impl
package|;
end_package

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
name|FileNotFoundException
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
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|NotImplementedException
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileAlreadyExistsException
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
name|FileStatus
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
name|FileSystem
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
name|PathIsNotEmptyDirectoryException
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
name|PathNotFoundException
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|BindFlags
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryOperations
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryTypeUtils
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryUtils
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
name|registry
operator|.
name|client
operator|.
name|exceptions
operator|.
name|InvalidPathnameException
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
name|registry
operator|.
name|client
operator|.
name|exceptions
operator|.
name|InvalidRecordException
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
name|registry
operator|.
name|client
operator|.
name|exceptions
operator|.
name|NoRecordException
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|RegistryPathStatus
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
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
name|service
operator|.
name|CompositeService
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Filesystem-based implementation of RegistryOperations. This class relies  * entirely on the configured FS for security and does no extra checks.  */
end_comment

begin_class
DECL|class|FSRegistryOperationsService
specifier|public
class|class
name|FSRegistryOperationsService
extends|extends
name|CompositeService
implements|implements
name|RegistryOperations
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
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
name|FSRegistryOperationsService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serviceRecordMarshal
specifier|private
specifier|final
name|RegistryUtils
operator|.
name|ServiceRecordMarshal
name|serviceRecordMarshal
init|=
operator|new
name|RegistryUtils
operator|.
name|ServiceRecordMarshal
argument_list|()
decl_stmt|;
DECL|method|FSRegistryOperationsService ()
specifier|public
name|FSRegistryOperationsService
parameter_list|()
block|{
name|super
argument_list|(
name|FSRegistryOperationsService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getFs ()
specifier|public
name|FileSystem
name|getFs
parameter_list|()
block|{
return|return
name|this
operator|.
name|fs
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized Yarn-registry with Filesystem "
operator|+
name|fs
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get FileSystem for registry"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|makePath (String path)
specifier|private
name|Path
name|makePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|formatDataPath (String basePath)
specifier|private
name|Path
name|formatDataPath
parameter_list|(
name|String
name|basePath
parameter_list|)
block|{
return|return
name|Path
operator|.
name|mergePaths
argument_list|(
operator|new
name|Path
argument_list|(
name|basePath
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/_record"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|relativize (String basePath, String childPath)
specifier|private
name|String
name|relativize
parameter_list|(
name|String
name|basePath
parameter_list|,
name|String
name|childPath
parameter_list|)
block|{
name|String
name|relative
init|=
operator|new
name|File
argument_list|(
name|basePath
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|relativize
argument_list|(
operator|new
name|File
argument_list|(
name|childPath
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|relative
return|;
block|}
annotation|@
name|Override
DECL|method|mknode (String path, boolean createParents)
specifier|public
name|boolean
name|mknode
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|createParents
parameter_list|)
throws|throws
name|PathNotFoundException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
block|{
name|Path
name|registryPath
init|=
name|makePath
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// getFileStatus throws FileNotFound if the path doesn't exist. If the
comment|// file already exists, return.
try|try
block|{
name|fs
operator|.
name|getFileStatus
argument_list|(
name|registryPath
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{     }
if|if
condition|(
name|createParents
condition|)
block|{
comment|// By default, mkdirs creates any parent dirs it needs
name|fs
operator|.
name|mkdirs
argument_list|(
name|registryPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileStatus
name|parentStatus
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|registryPath
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|parentStatus
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|registryPath
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|registryPath
operator|.
name|getParent
argument_list|()
operator|==
literal|null
operator|||
name|parentStatus
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|registryPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
literal|"no parent for "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|bind (String path, ServiceRecord record, int flags)
specifier|public
name|void
name|bind
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|PathNotFoundException
throws|,
name|FileAlreadyExistsException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
block|{
comment|// Preserve same overwrite semantics as ZK implementation
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|record
operator|!=
literal|null
argument_list|,
literal|"null record"
argument_list|)
expr_stmt|;
name|RegistryTypeUtils
operator|.
name|validateServiceRecord
argument_list|(
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
name|Path
name|dataPath
init|=
name|formatDataPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Boolean
name|overwrite
init|=
operator|(
operator|(
name|flags
operator|&
name|BindFlags
operator|.
name|OVERWRITE
operator|)
operator|!=
literal|0
operator|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|dataPath
argument_list|)
operator|&&
operator|!
name|overwrite
condition|)
block|{
throw|throw
operator|new
name|FileAlreadyExistsException
argument_list|()
throw|;
block|}
else|else
block|{
comment|// Either the file doesn't exist, or it exists and we're
comment|// overwriting. Create overwrites by default and creates parent dirs if
comment|// needed.
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|dataPath
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|serviceRecordMarshal
operator|.
name|toBytes
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Bound record to path "
operator|+
name|dataPath
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|resolve (String path)
specifier|public
name|ServiceRecord
name|resolve
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PathNotFoundException
throws|,
name|NoRecordException
throws|,
name|InvalidRecordException
throws|,
name|IOException
block|{
comment|// Read the entire file into byte array, should be small metadata
name|Long
name|size
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|formatDataPath
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|size
operator|.
name|intValue
argument_list|()
index|]
decl_stmt|;
name|FSDataInputStream
name|instream
init|=
name|fs
operator|.
name|open
argument_list|(
name|formatDataPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|bytesRead
init|=
name|instream
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|instream
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|bytesRead
operator|<
name|size
condition|)
block|{
throw|throw
operator|new
name|InvalidRecordException
argument_list|(
name|path
argument_list|,
literal|"Expected "
operator|+
name|size
operator|+
literal|" bytes, but read "
operator|+
name|bytesRead
argument_list|)
throw|;
block|}
comment|// Unmarshal, check, and return
name|ServiceRecord
name|record
init|=
name|serviceRecordMarshal
operator|.
name|fromBytes
argument_list|(
name|path
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|RegistryTypeUtils
operator|.
name|validateServiceRecord
argument_list|(
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
annotation|@
name|Override
DECL|method|stat (String path)
specifier|public
name|RegistryPathStatus
name|stat
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PathNotFoundException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
block|{
name|FileStatus
name|fstat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|formatDataPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numChildren
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|makePath
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|length
decl_stmt|;
name|RegistryPathStatus
name|regstat
init|=
operator|new
name|RegistryPathStatus
argument_list|(
name|fstat
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|fstat
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|fstat
operator|.
name|getLen
argument_list|()
argument_list|,
name|numChildren
argument_list|)
decl_stmt|;
return|return
name|regstat
return|;
block|}
annotation|@
name|Override
DECL|method|exists (String path)
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|exists
argument_list|(
name|makePath
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|list (String path)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PathNotFoundException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
block|{
name|FileStatus
index|[]
name|statArray
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|makePath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|basePath
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|makePath
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|FileStatus
name|stat
decl_stmt|;
comment|// Only count dirs; the _record files are hidden.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|statArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stat
operator|=
name|statArray
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|String
name|relativePath
init|=
name|relativize
argument_list|(
name|basePath
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|relativePath
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|paths
return|;
block|}
annotation|@
name|Override
DECL|method|delete (String path, boolean recursive)
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|PathNotFoundException
throws|,
name|PathIsNotEmptyDirectoryException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
block|{
name|Path
name|dirPath
init|=
name|makePath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|dirPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
comment|// If recursive == true, or dir is empty, delete.
if|if
condition|(
name|recursive
operator|||
name|list
argument_list|(
name|path
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|makePath
argument_list|(
name|path
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
throw|throw
operator|new
name|PathIsNotEmptyDirectoryException
argument_list|(
name|path
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|addWriteAccessor (String id, String pass)
specifier|public
name|boolean
name|addWriteAccessor
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|pass
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|clearWriteAccessors ()
specifier|public
name|void
name|clearWriteAccessors
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

