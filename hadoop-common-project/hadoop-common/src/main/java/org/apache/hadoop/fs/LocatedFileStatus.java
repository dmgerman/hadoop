begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_comment
comment|/**  * This class defines a FileStatus that includes a file's block locations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|LocatedFileStatus
specifier|public
class|class
name|LocatedFileStatus
extends|extends
name|FileStatus
block|{
DECL|field|locations
specifier|private
name|BlockLocation
index|[]
name|locations
decl_stmt|;
DECL|method|LocatedFileStatus ()
specifier|public
name|LocatedFileStatus
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructor     * @param stat a file status    * @param locations a file's block locations    */
DECL|method|LocatedFileStatus (FileStatus stat, BlockLocation[] locations)
specifier|public
name|LocatedFileStatus
parameter_list|(
name|FileStatus
name|stat
parameter_list|,
name|BlockLocation
index|[]
name|locations
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|stat
operator|.
name|getLen
argument_list|()
argument_list|,
name|stat
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|stat
operator|.
name|getReplication
argument_list|()
argument_list|,
name|stat
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|stat
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|stat
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|stat
operator|.
name|getPermission
argument_list|()
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|,
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|,
literal|null
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|,
name|locations
argument_list|)
expr_stmt|;
if|if
condition|(
name|stat
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
name|setSymlink
argument_list|(
name|stat
operator|.
name|getSymlink
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Constructor    *     * @param length a file's length    * @param isdir if the path is a directory    * @param block_replication the file's replication factor    * @param blocksize a file's block size    * @param modification_time a file's modification time    * @param access_time a file's access time    * @param permission a file's permission    * @param owner a file's owner    * @param group a file's group    * @param symlink symlink if the path is a symbolic link    * @param path the path's qualified name    * @param locations a file's block locations    */
DECL|method|LocatedFileStatus (long length, boolean isdir, int block_replication, long blocksize, long modification_time, long access_time, FsPermission permission, String owner, String group, Path symlink, Path path, BlockLocation[] locations)
specifier|public
name|LocatedFileStatus
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
name|Path
name|symlink
parameter_list|,
name|Path
name|path
parameter_list|,
name|BlockLocation
index|[]
name|locations
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|locations
operator|=
name|locations
expr_stmt|;
block|}
comment|/**    * Get the file's block locations    * @return the file's block locations    */
DECL|method|getBlockLocations ()
specifier|public
name|BlockLocation
index|[]
name|getBlockLocations
parameter_list|()
block|{
return|return
name|locations
return|;
block|}
comment|/**    * Compare this FileStatus to another FileStatus    * @param   o the FileStatus to be compared.    * @return  a negative integer, zero, or a positive integer as this object    *   is less than, equal to, or greater than the specified object.    */
annotation|@
name|Override
DECL|method|compareTo (FileStatus o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|FileStatus
name|o
parameter_list|)
block|{
return|return
name|super
operator|.
name|compareTo
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/** Compare if this object is equal to another object    * @param   o the object to be compared.    * @return  true if two file status has the same path name; false if not.    */
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/**    * Returns a hash code value for the object, which is defined as    * the hash code of the path name.    *    * @return  a hash code value for the path name.    */
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

