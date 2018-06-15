begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.services
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
name|contracts
operator|.
name|services
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|azurebfs
operator|.
name|AzureBlobFileSystem
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
name|contracts
operator|.
name|exceptions
operator|.
name|AzureBlobFileSystemException
import|;
end_import

begin_comment
comment|/**  * File System http service to provide network calls for file system operations.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|AbfsHttpService
specifier|public
interface|interface
name|AbfsHttpService
extends|extends
name|InjectableService
block|{
comment|/**    * Gets filesystem properties on the Azure service.    * @param azureBlobFileSystem filesystem to get the properties.    * @return Hashtable<String, String> hash table containing all the filesystem properties.    */
DECL|method|getFilesystemProperties (AzureBlobFileSystem azureBlobFileSystem)
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getFilesystemProperties
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Sets filesystem properties on the Azure service.    * @param azureBlobFileSystem filesystem to get the properties.    * @param properties file system properties to set.    */
DECL|method|setFilesystemProperties (AzureBlobFileSystem azureBlobFileSystem, Hashtable<String, String> properties)
name|void
name|setFilesystemProperties
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Gets path properties on the Azure service.    * @param azureBlobFileSystem filesystem to get the properties of the path.    * @param path path to get properties.    * @return Hashtable<String, String> hash table containing all the path properties.    */
DECL|method|getPathProperties (AzureBlobFileSystem azureBlobFileSystem, Path path)
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPathProperties
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Sets path properties on the Azure service.    * @param azureBlobFileSystem filesystem to get the properties of the path.    * @param path path to set properties.    * @param properties hash table containing all the path properties.    */
DECL|method|setPathProperties (AzureBlobFileSystem azureBlobFileSystem, Path path, Hashtable<String, String> properties)
name|void
name|setPathProperties
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|,
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Creates filesystem on the Azure service.    * @param azureBlobFileSystem filesystem to be created.    */
DECL|method|createFilesystem (AzureBlobFileSystem azureBlobFileSystem)
name|void
name|createFilesystem
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Deletes filesystem on the Azure service.    * @param azureBlobFileSystem filesystem to be deleted.    */
DECL|method|deleteFilesystem (AzureBlobFileSystem azureBlobFileSystem)
name|void
name|deleteFilesystem
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Creates a file on the Azure service.    * @param azureBlobFileSystem filesystem to create file or directory.    * @param path path of the file to be created.    * @param overwrite should overwrite.    * @return OutputStream stream to the file.    */
DECL|method|createFile (AzureBlobFileSystem azureBlobFileSystem, Path path, boolean overwrite)
name|OutputStream
name|createFile
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Creates a directory on the Azure service.    * @param azureBlobFileSystem filesystem to create file or directory.    * @param path path of the directory to be created.    * @return OutputStream stream to the file.    */
DECL|method|createDirectory (AzureBlobFileSystem azureBlobFileSystem, Path path)
name|Void
name|createDirectory
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Opens a file to read and returns the stream.    * @param azureBlobFileSystem filesystem to read a file from.    * @param path file path to read.    * @return InputStream a stream to the file to read.    */
DECL|method|openFileForRead (AzureBlobFileSystem azureBlobFileSystem, Path path, FileSystem.Statistics statistics)
name|InputStream
name|openFileForRead
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|statistics
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Opens a file to write and returns the stream.    * @param azureBlobFileSystem filesystem to write a file to.    * @param path file path to write.    * @param overwrite should overwrite.    * @return OutputStream a stream to the file to write.    */
DECL|method|openFileForWrite (AzureBlobFileSystem azureBlobFileSystem, Path path, boolean overwrite)
name|OutputStream
name|openFileForWrite
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Renames a file or directory from source to destination.    * @param azureBlobFileSystem filesystem to rename a path.    * @param source source path.    * @param destination destination path.    */
DECL|method|rename (AzureBlobFileSystem azureBlobFileSystem, Path source, Path destination)
name|void
name|rename
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|source
parameter_list|,
name|Path
name|destination
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Deletes a file or directory.    * @param azureBlobFileSystem filesystem to delete the path.    * @param path file path to be deleted.    * @param recursive true if path is a directory and recursive deletion is desired.    */
DECL|method|delete (AzureBlobFileSystem azureBlobFileSystem, Path path, boolean recursive)
name|void
name|delete
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Gets path's status under the provided path on the Azure service.    * @param azureBlobFileSystem filesystem to perform the get file status operation.    * @param path path delimiter.    * @return FileStatus FileStatus of the path in the file system.    */
DECL|method|getFileStatus (AzureBlobFileSystem azureBlobFileSystem, Path path)
name|FileStatus
name|getFileStatus
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Lists all the paths under the provided path on the Azure service.    * @param azureBlobFileSystem filesystem to perform the list operation.    * @param path path delimiter.    * @return FileStatus[] list of all paths in the file system.    */
DECL|method|listStatus (AzureBlobFileSystem azureBlobFileSystem, Path path)
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Closes the client to filesystem to Azure service.    * @param azureBlobFileSystem filesystem to perform the list operation.    */
DECL|method|closeFileSystem (AzureBlobFileSystem azureBlobFileSystem)
name|void
name|closeFileSystem
parameter_list|(
name|AzureBlobFileSystem
name|azureBlobFileSystem
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
function_decl|;
comment|/**    * Checks for the given path if it is marked as atomic rename directory or not.    * @param key    * @return True if the given path is listed under atomic rename property otherwise False.    */
DECL|method|isAtomicRenameKey (String key)
name|boolean
name|isAtomicRenameKey
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

