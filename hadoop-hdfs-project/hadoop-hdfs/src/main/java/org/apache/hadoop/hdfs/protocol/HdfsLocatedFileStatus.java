begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|FileEncryptionInfo
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
name|LocatedFileStatus
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
name|permission
operator|.
name|FsPermission
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
name|DFSUtilClient
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
name|erasurecode
operator|.
name|ECSchema
import|;
end_import

begin_comment
comment|/**   * Interface that represents the over the wire information  * including block locations for a file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|HdfsLocatedFileStatus
specifier|public
class|class
name|HdfsLocatedFileStatus
extends|extends
name|HdfsFileStatus
block|{
DECL|field|locations
specifier|private
specifier|final
name|LocatedBlocks
name|locations
decl_stmt|;
comment|/**    * Constructor    *     * @param length size    * @param isdir if this is directory    * @param block_replication the file's replication factor    * @param blocksize the file's block size    * @param modification_time most recent modification time    * @param access_time most recent access time    * @param permission permission    * @param owner owner    * @param group group    * @param symlink symbolic link    * @param path local path name in java UTF8 format     * @param fileId the file id    * @param locations block locations    * @param feInfo file encryption info    */
DECL|method|HdfsLocatedFileStatus (long length, boolean isdir, int block_replication, long blocksize, long modification_time, long access_time, FsPermission permission, String owner, String group, byte[] symlink, byte[] path, long fileId, LocatedBlocks locations, int childrenNum, FileEncryptionInfo feInfo, byte storagePolicy, ECSchema schema, int stripeCellSize)
specifier|public
name|HdfsLocatedFileStatus
parameter_list|(
name|long
name|length
parameter_list|,
name|boolean
name|isdir
parameter_list|,
name|int
name|block_replication
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|long
name|modification_time
parameter_list|,
name|long
name|access_time
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|byte
index|[]
name|symlink
parameter_list|,
name|byte
index|[]
name|path
parameter_list|,
name|long
name|fileId
parameter_list|,
name|LocatedBlocks
name|locations
parameter_list|,
name|int
name|childrenNum
parameter_list|,
name|FileEncryptionInfo
name|feInfo
parameter_list|,
name|byte
name|storagePolicy
parameter_list|,
name|ECSchema
name|schema
parameter_list|,
name|int
name|stripeCellSize
parameter_list|)
block|{
name|super
argument_list|(
name|length
argument_list|,
name|isdir
argument_list|,
name|block_replication
argument_list|,
name|blocksize
argument_list|,
name|modification_time
argument_list|,
name|access_time
argument_list|,
name|permission
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
name|symlink
argument_list|,
name|path
argument_list|,
name|fileId
argument_list|,
name|childrenNum
argument_list|,
name|feInfo
argument_list|,
name|storagePolicy
argument_list|,
name|schema
argument_list|,
name|stripeCellSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|locations
operator|=
name|locations
expr_stmt|;
block|}
DECL|method|getBlockLocations ()
specifier|public
name|LocatedBlocks
name|getBlockLocations
parameter_list|()
block|{
return|return
name|locations
return|;
block|}
DECL|method|makeQualifiedLocated (URI defaultUri, Path path)
specifier|public
specifier|final
name|LocatedFileStatus
name|makeQualifiedLocated
parameter_list|(
name|URI
name|defaultUri
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
return|return
operator|new
name|LocatedFileStatus
argument_list|(
name|getLen
argument_list|()
argument_list|,
name|isDir
argument_list|()
argument_list|,
name|getReplication
argument_list|()
argument_list|,
name|getBlockSize
argument_list|()
argument_list|,
name|getModificationTime
argument_list|()
argument_list|,
name|getAccessTime
argument_list|()
argument_list|,
name|getPermission
argument_list|()
argument_list|,
name|getOwner
argument_list|()
argument_list|,
name|getGroup
argument_list|()
argument_list|,
name|isSymlink
argument_list|()
condition|?
operator|new
name|Path
argument_list|(
name|getSymlink
argument_list|()
argument_list|)
else|:
literal|null
argument_list|,
operator|(
name|getFullPath
argument_list|(
name|path
argument_list|)
operator|)
operator|.
name|makeQualified
argument_list|(
name|defaultUri
argument_list|,
literal|null
argument_list|)
argument_list|,
comment|// fully-qualify path
name|DFSUtilClient
operator|.
name|locatedBlocks2Locations
argument_list|(
name|getBlockLocations
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

