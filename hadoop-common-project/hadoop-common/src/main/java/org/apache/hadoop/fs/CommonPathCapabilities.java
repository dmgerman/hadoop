begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_comment
comment|/**  * Common path capabilities.  */
end_comment

begin_class
DECL|class|CommonPathCapabilities
specifier|public
specifier|final
class|class
name|CommonPathCapabilities
block|{
DECL|method|CommonPathCapabilities ()
specifier|private
name|CommonPathCapabilities
parameter_list|()
block|{   }
comment|/**    * Does the store support    * {@code FileSystem.setAcl(Path, List)},    * {@code FileSystem.getAclStatus(Path)}    * and related methods?    * Value: {@value}.    */
DECL|field|FS_ACLS
specifier|public
specifier|static
specifier|final
name|String
name|FS_ACLS
init|=
literal|"fs.capability.paths.acls"
decl_stmt|;
comment|/**    * Does the store support {@code FileSystem.append(Path)}?    * Value: {@value}.    */
DECL|field|FS_APPEND
specifier|public
specifier|static
specifier|final
name|String
name|FS_APPEND
init|=
literal|"fs.capability.paths.append"
decl_stmt|;
comment|/**    * Does the store support {@code FileSystem.getFileChecksum(Path)}?    * Value: {@value}.    */
DECL|field|FS_CHECKSUMS
specifier|public
specifier|static
specifier|final
name|String
name|FS_CHECKSUMS
init|=
literal|"fs.capability.paths.checksums"
decl_stmt|;
comment|/**    * Does the store support {@code FileSystem.concat(Path, Path[])}?    * Value: {@value}.    */
DECL|field|FS_CONCAT
specifier|public
specifier|static
specifier|final
name|String
name|FS_CONCAT
init|=
literal|"fs.capability.paths.concat"
decl_stmt|;
comment|/**    * Does the store support {@code FileSystem.listCorruptFileBlocks(Path)} ()}?    * Value: {@value}.    */
DECL|field|FS_LIST_CORRUPT_FILE_BLOCKS
specifier|public
specifier|static
specifier|final
name|String
name|FS_LIST_CORRUPT_FILE_BLOCKS
init|=
literal|"fs.capability.paths.list-corrupt-file-blocks"
decl_stmt|;
comment|/**    * Does the store support    * {@code FileSystem.createPathHandle(FileStatus, Options.HandleOpt...)}    * and related methods?    * Value: {@value}.    */
DECL|field|FS_PATHHANDLES
specifier|public
specifier|static
specifier|final
name|String
name|FS_PATHHANDLES
init|=
literal|"fs.capability.paths.pathhandles"
decl_stmt|;
comment|/**    * Does the store support {@code FileSystem.setPermission(Path, FsPermission)}    * and related methods?    * Value: {@value}.    */
DECL|field|FS_PERMISSIONS
specifier|public
specifier|static
specifier|final
name|String
name|FS_PERMISSIONS
init|=
literal|"fs.capability.paths.permissions"
decl_stmt|;
comment|/**    * Does this filesystem connector only support filesystem read operations?    * For example, the {@code HttpFileSystem} is always read-only.    * This is different from "is the specific instance and path read only?",    * which must be determined by checking permissions (where supported), or    * attempting write operations under a path.    * Value: {@value}.    */
DECL|field|FS_READ_ONLY_CONNECTOR
specifier|public
specifier|static
specifier|final
name|String
name|FS_READ_ONLY_CONNECTOR
init|=
literal|"fs.capability.paths.read-only-connector"
decl_stmt|;
comment|/**    * Does the store support snapshots through    * {@code FileSystem.createSnapshot(Path)} and related methods??    * Value: {@value}.    */
DECL|field|FS_SNAPSHOTS
specifier|public
specifier|static
specifier|final
name|String
name|FS_SNAPSHOTS
init|=
literal|"fs.capability.paths.snapshots"
decl_stmt|;
comment|/**    * Does the store support {@code FileSystem.setStoragePolicy(Path, String)}    * and related methods?    * Value: {@value}.    */
DECL|field|FS_STORAGEPOLICY
specifier|public
specifier|static
specifier|final
name|String
name|FS_STORAGEPOLICY
init|=
literal|"fs.capability.paths.storagepolicy"
decl_stmt|;
comment|/**    * Does the store support symlinks through    * {@code FileSystem.createSymlink(Path, Path, boolean)} and related methods?    * Value: {@value}.    */
DECL|field|FS_SYMLINKS
specifier|public
specifier|static
specifier|final
name|String
name|FS_SYMLINKS
init|=
literal|"fs.capability.paths.symlinks"
decl_stmt|;
comment|/**    * Does the store support {@code FileSystem#truncate(Path, long)} ?    * Value: {@value}.    */
DECL|field|FS_TRUNCATE
specifier|public
specifier|static
specifier|final
name|String
name|FS_TRUNCATE
init|=
literal|"fs.capability.paths.truncate"
decl_stmt|;
comment|/**    * Does the store support XAttributes through    * {@code FileSystem#.setXAttr()} and related methods?    * Value: {@value}.    */
DECL|field|FS_XATTRS
specifier|public
specifier|static
specifier|final
name|String
name|FS_XATTRS
init|=
literal|"fs.capability.paths.xattrs"
decl_stmt|;
block|}
end_class

end_unit

