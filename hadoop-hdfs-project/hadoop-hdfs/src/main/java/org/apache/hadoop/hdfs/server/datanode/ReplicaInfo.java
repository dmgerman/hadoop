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
name|FileOutputStream
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
name|HashMap
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
name|Map
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
name|fs
operator|.
name|HardLink
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
name|io
operator|.
name|IOUtils
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

begin_comment
comment|/**  * This class is used by datanodes to maintain meta data of its replicas.  * It provides a general interface for meta information of a replica.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ReplicaInfo
specifier|abstract
specifier|public
class|class
name|ReplicaInfo
extends|extends
name|Block
implements|implements
name|Replica
block|{
comment|/** volume where the replica belongs */
DECL|field|volume
specifier|private
name|FsVolumeSpi
name|volume
decl_stmt|;
comment|/** directory where block& meta files belong */
comment|/**    * Base directory containing numerically-identified sub directories and    * possibly blocks.    */
DECL|field|baseDir
specifier|private
name|File
name|baseDir
decl_stmt|;
comment|/**    * Whether or not this replica's parent directory includes subdirs, in which    * case we can generate them based on the replica's block ID    */
DECL|field|hasSubdirs
specifier|private
name|boolean
name|hasSubdirs
decl_stmt|;
DECL|field|internedBaseDirs
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|internedBaseDirs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Constructor    * @param block a block    * @param vol volume where replica is located    * @param dir directory path where block and meta files are located    */
DECL|method|ReplicaInfo (Block block, FsVolumeSpi vol, File dir)
name|ReplicaInfo
parameter_list|(
name|Block
name|block
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|,
name|File
name|dir
parameter_list|)
block|{
name|this
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|,
name|vol
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    * @param blockId block id    * @param len replica length    * @param genStamp replica generation stamp    * @param vol volume where replica is located    * @param dir directory path where block and meta files are located    */
DECL|method|ReplicaInfo (long blockId, long len, long genStamp, FsVolumeSpi vol, File dir)
name|ReplicaInfo
parameter_list|(
name|long
name|blockId
parameter_list|,
name|long
name|len
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|,
name|File
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|blockId
argument_list|,
name|len
argument_list|,
name|genStamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|volume
operator|=
name|vol
expr_stmt|;
name|setDirInternal
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copy constructor.    * @param from where to copy from    */
DECL|method|ReplicaInfo (ReplicaInfo from)
name|ReplicaInfo
parameter_list|(
name|ReplicaInfo
name|from
parameter_list|)
block|{
name|this
argument_list|(
name|from
argument_list|,
name|from
operator|.
name|getVolume
argument_list|()
argument_list|,
name|from
operator|.
name|getDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the full path of this replica's data file    * @return the full path of this replica's data file    */
DECL|method|getBlockFile ()
specifier|public
name|File
name|getBlockFile
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|getDir
argument_list|()
argument_list|,
name|getBlockName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the full path of this replica's meta file    * @return the full path of this replica's meta file    */
DECL|method|getMetaFile ()
specifier|public
name|File
name|getMetaFile
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|getDir
argument_list|()
argument_list|,
name|DatanodeUtil
operator|.
name|getMetaName
argument_list|(
name|getBlockName
argument_list|()
argument_list|,
name|getGenerationStamp
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the volume where this replica is located on disk    * @return the volume where this replica is located on disk    */
DECL|method|getVolume ()
specifier|public
name|FsVolumeSpi
name|getVolume
parameter_list|()
block|{
return|return
name|volume
return|;
block|}
comment|/**    * Set the volume where this replica is located on disk    */
DECL|method|setVolume (FsVolumeSpi vol)
name|void
name|setVolume
parameter_list|(
name|FsVolumeSpi
name|vol
parameter_list|)
block|{
name|this
operator|.
name|volume
operator|=
name|vol
expr_stmt|;
block|}
comment|/**    * Get the storageUuid of the volume that stores this replica.    */
annotation|@
name|Override
DECL|method|getStorageUuid ()
specifier|public
name|String
name|getStorageUuid
parameter_list|()
block|{
return|return
name|volume
operator|.
name|getStorageID
argument_list|()
return|;
block|}
comment|/**    * Return the parent directory path where this replica is located    * @return the parent directory path where this replica is located    */
DECL|method|getDir ()
name|File
name|getDir
parameter_list|()
block|{
return|return
name|hasSubdirs
condition|?
name|DatanodeUtil
operator|.
name|idToBlockDir
argument_list|(
name|baseDir
argument_list|,
name|getBlockId
argument_list|()
argument_list|)
else|:
name|baseDir
return|;
block|}
comment|/**    * Set the parent directory where this replica is located    * @param dir the parent directory where the replica is located    */
DECL|method|setDir (File dir)
specifier|public
name|void
name|setDir
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|setDirInternal
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|setDirInternal (File dir)
specifier|private
name|void
name|setDirInternal
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|baseDir
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|ReplicaDirInfo
name|dirInfo
init|=
name|parseBaseDir
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|this
operator|.
name|hasSubdirs
operator|=
name|dirInfo
operator|.
name|hasSubidrs
expr_stmt|;
synchronized|synchronized
init|(
name|internedBaseDirs
init|)
block|{
if|if
condition|(
operator|!
name|internedBaseDirs
operator|.
name|containsKey
argument_list|(
name|dirInfo
operator|.
name|baseDirPath
argument_list|)
condition|)
block|{
comment|// Create a new String path of this file and make a brand new File object
comment|// to guarantee we drop the reference to the underlying char[] storage.
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|dirInfo
operator|.
name|baseDirPath
argument_list|)
decl_stmt|;
name|internedBaseDirs
operator|.
name|put
argument_list|(
name|dirInfo
operator|.
name|baseDirPath
argument_list|,
name|baseDir
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|baseDir
operator|=
name|internedBaseDirs
operator|.
name|get
argument_list|(
name|dirInfo
operator|.
name|baseDirPath
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|class|ReplicaDirInfo
specifier|public
specifier|static
class|class
name|ReplicaDirInfo
block|{
DECL|field|baseDirPath
specifier|public
name|String
name|baseDirPath
decl_stmt|;
DECL|field|hasSubidrs
specifier|public
name|boolean
name|hasSubidrs
decl_stmt|;
DECL|method|ReplicaDirInfo (String baseDirPath, boolean hasSubidrs)
specifier|public
name|ReplicaDirInfo
parameter_list|(
name|String
name|baseDirPath
parameter_list|,
name|boolean
name|hasSubidrs
parameter_list|)
block|{
name|this
operator|.
name|baseDirPath
operator|=
name|baseDirPath
expr_stmt|;
name|this
operator|.
name|hasSubidrs
operator|=
name|hasSubidrs
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|parseBaseDir (File dir)
specifier|public
specifier|static
name|ReplicaDirInfo
name|parseBaseDir
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|File
name|currentDir
init|=
name|dir
decl_stmt|;
name|boolean
name|hasSubdirs
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|currentDir
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|DataStorage
operator|.
name|BLOCK_SUBDIR_PREFIX
argument_list|)
condition|)
block|{
name|hasSubdirs
operator|=
literal|true
expr_stmt|;
name|currentDir
operator|=
name|currentDir
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ReplicaDirInfo
argument_list|(
name|currentDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|hasSubdirs
argument_list|)
return|;
block|}
comment|/**    * check if this replica has already been unlinked.    * @return true if the replica has already been unlinked     *         or no need to be detached; false otherwise    */
DECL|method|isUnlinked ()
specifier|public
name|boolean
name|isUnlinked
parameter_list|()
block|{
return|return
literal|true
return|;
comment|// no need to be unlinked
block|}
comment|/**    * set that this replica is unlinked    */
DECL|method|setUnlinked ()
specifier|public
name|void
name|setUnlinked
parameter_list|()
block|{
comment|// no need to be unlinked
block|}
comment|/**    * Copy specified file into a temporary file. Then rename the    * temporary file to the original name. This will cause any    * hardlinks to the original file to be removed. The temporary    * files are created in the same directory. The temporary files will    * be recovered (especially on Windows) on datanode restart.    */
DECL|method|unlinkFile (File file, Block b)
specifier|private
name|void
name|unlinkFile
parameter_list|(
name|File
name|file
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tmpFile
init|=
name|DatanodeUtil
operator|.
name|createTmpFile
argument_list|(
name|b
argument_list|,
name|DatanodeUtil
operator|.
name|getUnlinkTmpFile
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
literal|16
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|.
name|length
argument_list|()
operator|!=
name|tmpFile
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Copy of file "
operator|+
name|file
operator|+
literal|" size "
operator|+
name|file
operator|.
name|length
argument_list|()
operator|+
literal|" into file "
operator|+
name|tmpFile
operator|+
literal|" resulted in a size of "
operator|+
name|tmpFile
operator|.
name|length
argument_list|()
argument_list|)
throw|;
block|}
name|FileUtil
operator|.
name|replaceFile
argument_list|(
name|tmpFile
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|boolean
name|done
init|=
name|tmpFile
operator|.
name|delete
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|done
condition|)
block|{
name|DataNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"detachFile failed to delete temporary file "
operator|+
name|tmpFile
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Remove a hard link by copying the block to a temporary place and     * then moving it back    * @param numLinks number of hard links    * @return true if copy is successful;     *         false if it is already detached or no need to be detached    * @throws IOException if there is any copy error    */
DECL|method|unlinkBlock (int numLinks)
specifier|public
name|boolean
name|unlinkBlock
parameter_list|(
name|int
name|numLinks
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isUnlinked
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|File
name|file
init|=
name|getBlockFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
operator|||
name|getVolume
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"detachBlock:Block not found. "
operator|+
name|this
argument_list|)
throw|;
block|}
name|File
name|meta
init|=
name|getMetaFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|HardLink
operator|.
name|getLinkCount
argument_list|(
name|file
argument_list|)
operator|>
name|numLinks
condition|)
block|{
name|DataNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"CopyOnWrite for block "
operator|+
name|this
argument_list|)
expr_stmt|;
name|unlinkFile
argument_list|(
name|file
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|HardLink
operator|.
name|getLinkCount
argument_list|(
name|meta
argument_list|)
operator|>
name|numLinks
condition|)
block|{
name|unlinkFile
argument_list|(
name|meta
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
name|setUnlinked
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
comment|//Object
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", "
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|", "
operator|+
name|getState
argument_list|()
operator|+
literal|"\n  getNumBytes()     = "
operator|+
name|getNumBytes
argument_list|()
operator|+
literal|"\n  getBytesOnDisk()  = "
operator|+
name|getBytesOnDisk
argument_list|()
operator|+
literal|"\n  getVisibleLength()= "
operator|+
name|getVisibleLength
argument_list|()
operator|+
literal|"\n  getVolume()       = "
operator|+
name|getVolume
argument_list|()
operator|+
literal|"\n  getBlockFile()    = "
operator|+
name|getBlockFile
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isOnTransientStorage ()
specifier|public
name|boolean
name|isOnTransientStorage
parameter_list|()
block|{
return|return
name|volume
operator|.
name|isTransientStorage
argument_list|()
return|;
block|}
block|}
end_class

end_unit

