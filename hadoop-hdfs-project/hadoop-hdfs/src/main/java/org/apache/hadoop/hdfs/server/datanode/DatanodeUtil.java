begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
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
name|FileInputStream
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
name|hdfs
operator|.
name|protocol
operator|.
name|Block
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
name|hdfs
operator|.
name|protocol
operator|.
name|ExtendedBlock
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|LengthInputStream
import|;
end_import

begin_comment
comment|/** Provide utility methods for Datanode. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DatanodeUtil
specifier|public
class|class
name|DatanodeUtil
block|{
DECL|field|UNLINK_BLOCK_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|UNLINK_BLOCK_SUFFIX
init|=
literal|".unlinked"
decl_stmt|;
DECL|field|DISK_ERROR
specifier|public
specifier|static
specifier|final
name|String
name|DISK_ERROR
init|=
literal|"Possible disk error: "
decl_stmt|;
DECL|field|SEP
specifier|private
specifier|static
specifier|final
name|String
name|SEP
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
decl_stmt|;
comment|/** Get the cause of an I/O exception if caused by a possible disk error    * @param ioe an I/O exception    * @return cause if the I/O exception is caused by a possible disk error;    *         null otherwise.    */
DECL|method|getCauseIfDiskError (IOException ioe)
specifier|static
name|IOException
name|getCauseIfDiskError
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
name|DISK_ERROR
argument_list|)
condition|)
block|{
return|return
operator|(
name|IOException
operator|)
name|ioe
operator|.
name|getCause
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Create a new file.    * @throws IOException     * if the file already exists or if the file cannot be created.    */
DECL|method|createFileWithExistsCheck ( FsVolumeSpi volume, Block b, File f, FileIoProvider fileIoProvider)
specifier|public
specifier|static
name|File
name|createFileWithExistsCheck
parameter_list|(
name|FsVolumeSpi
name|volume
parameter_list|,
name|Block
name|b
parameter_list|,
name|File
name|f
parameter_list|,
name|FileIoProvider
name|fileIoProvider
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fileIoProvider
operator|.
name|exists
argument_list|(
name|volume
argument_list|,
name|f
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create temporary file for "
operator|+
name|b
operator|+
literal|".  File "
operator|+
name|f
operator|+
literal|" should not be present, but is."
argument_list|)
throw|;
block|}
comment|// Create the zero-length temp file
specifier|final
name|boolean
name|fileCreated
decl_stmt|;
try|try
block|{
name|fileCreated
operator|=
name|fileIoProvider
operator|.
name|createFile
argument_list|(
name|volume
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|DISK_ERROR
operator|+
literal|"Failed to create "
operator|+
name|f
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|fileCreated
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create temporary file for "
operator|+
name|b
operator|+
literal|".  File "
operator|+
name|f
operator|+
literal|" should be creatable, but is already present."
argument_list|)
throw|;
block|}
return|return
name|f
return|;
block|}
comment|/**    * @return the meta name given the block name and generation stamp.    */
DECL|method|getMetaName (String blockName, long generationStamp)
specifier|public
specifier|static
name|String
name|getMetaName
parameter_list|(
name|String
name|blockName
parameter_list|,
name|long
name|generationStamp
parameter_list|)
block|{
return|return
name|blockName
operator|+
literal|"_"
operator|+
name|generationStamp
operator|+
name|Block
operator|.
name|METADATA_EXTENSION
return|;
block|}
comment|/** @return the unlink file. */
DECL|method|getUnlinkTmpFile (File f)
specifier|public
specifier|static
name|File
name|getUnlinkTmpFile
parameter_list|(
name|File
name|f
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|f
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|f
operator|.
name|getName
argument_list|()
operator|+
name|UNLINK_BLOCK_SUFFIX
argument_list|)
return|;
block|}
comment|/**    * Checks whether there are any files anywhere in the directory tree rooted    * at dir (directories don't count as files). dir must exist    * @return true if there are no files    * @throws IOException if unable to list subdirectories    */
DECL|method|dirNoFilesRecursive ( FsVolumeSpi volume, File dir, FileIoProvider fileIoProvider)
specifier|public
specifier|static
name|boolean
name|dirNoFilesRecursive
parameter_list|(
name|FsVolumeSpi
name|volume
parameter_list|,
name|File
name|dir
parameter_list|,
name|FileIoProvider
name|fileIoProvider
parameter_list|)
throws|throws
name|IOException
block|{
name|File
index|[]
name|contents
init|=
name|fileIoProvider
operator|.
name|listFiles
argument_list|(
name|volume
argument_list|,
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|contents
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot list contents of "
operator|+
name|dir
argument_list|)
throw|;
block|}
for|for
control|(
name|File
name|f
range|:
name|contents
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|isDirectory
argument_list|()
operator|||
operator|(
name|f
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|dirNoFilesRecursive
argument_list|(
name|volume
argument_list|,
name|f
argument_list|,
name|fileIoProvider
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Get the directory where a finalized block with this ID should be stored.    * Do not attempt to create the directory.    * @param root the root directory where finalized blocks are stored    * @param blockId    * @return    */
DECL|method|idToBlockDir (File root, long blockId)
specifier|public
specifier|static
name|File
name|idToBlockDir
parameter_list|(
name|File
name|root
parameter_list|,
name|long
name|blockId
parameter_list|)
block|{
name|int
name|d1
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|blockId
operator|>>
literal|16
operator|)
operator|&
literal|0x1F
argument_list|)
decl_stmt|;
name|int
name|d2
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|blockId
operator|>>
literal|8
operator|)
operator|&
literal|0x1F
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|DataStorage
operator|.
name|BLOCK_SUBDIR_PREFIX
operator|+
name|d1
operator|+
name|SEP
operator|+
name|DataStorage
operator|.
name|BLOCK_SUBDIR_PREFIX
operator|+
name|d2
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/**    * @return the FileInputStream for the meta data of the given block.    * @throws FileNotFoundException    *           if the file not found.    * @throws ClassCastException    *           if the underlying input stream is not a FileInputStream.    */
DECL|method|getMetaDataInputStream ( ExtendedBlock b, FsDatasetSpi<?> data)
specifier|public
specifier|static
name|FileInputStream
name|getMetaDataInputStream
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|data
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LengthInputStream
name|lin
init|=
name|data
operator|.
name|getMetaDataInputStream
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|lin
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Meta file for "
operator|+
name|b
operator|+
literal|" not found."
argument_list|)
throw|;
block|}
return|return
operator|(
name|FileInputStream
operator|)
name|lin
operator|.
name|getWrappedStream
argument_list|()
return|;
block|}
block|}
end_class

end_unit

