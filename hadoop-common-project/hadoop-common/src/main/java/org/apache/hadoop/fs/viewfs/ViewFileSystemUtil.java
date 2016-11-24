begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
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
name|FsConstants
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
name|FsStatus
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
name|UnsupportedFileSystemException
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
name|viewfs
operator|.
name|ViewFileSystem
operator|.
name|MountPoint
import|;
end_import

begin_comment
comment|/**  * Utility APIs for ViewFileSystem.  */
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
DECL|class|ViewFileSystemUtil
specifier|public
specifier|final
class|class
name|ViewFileSystemUtil
block|{
DECL|method|ViewFileSystemUtil ()
specifier|private
name|ViewFileSystemUtil
parameter_list|()
block|{
comment|// Private Constructor
block|}
comment|/**    * Check if the FileSystem is a ViewFileSystem.    *    * @param fileSystem    * @return true if the fileSystem is ViewFileSystem    */
DECL|method|isViewFileSystem (final FileSystem fileSystem)
specifier|public
specifier|static
name|boolean
name|isViewFileSystem
parameter_list|(
specifier|final
name|FileSystem
name|fileSystem
parameter_list|)
block|{
return|return
name|fileSystem
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
name|FsConstants
operator|.
name|VIEWFS_SCHEME
argument_list|)
return|;
block|}
comment|/**    * Get FsStatus for all ViewFsMountPoints matching path for the given    * ViewFileSystem.    *    * Say ViewFileSystem has following mount points configured    *  (1) hdfs://NN0_host:port/sales mounted on /dept/sales    *  (2) hdfs://NN1_host:port/marketing mounted on /dept/marketing    *  (3) hdfs://NN2_host:port/eng_usa mounted on /dept/eng/usa    *  (4) hdfs://NN3_host:port/eng_asia mounted on /dept/eng/asia    *    * For the above config, here is a sample list of paths and their matching    * mount points while getting FsStatus    *    *  Path                  Description                      Matching MountPoint    *    *  "/"                   Root ViewFileSystem lists all    (1), (2), (3), (4)    *                         mount points.    *    *  "/dept"               Not a mount point, but a valid   (1), (2), (3), (4)    *                         internal dir in the mount tree    *                         and resolved down to "/" path.    *    *  "/dept/sales"         Matches a mount point            (1)    *    *  "/dept/sales/india"   Path is over a valid mount point (1)    *                         and resolved down to    *                         "/dept/sales"    *    *  "/dept/eng"           Not a mount point, but a valid   (1), (2), (3), (4)    *                         internal dir in the mount tree    *                         and resolved down to "/" path.    *    *  "/erp"                Doesn't match or leads to or    *                         over any valid mount points     None    *    *    * @param fileSystem - ViewFileSystem on which mount point exists    * @param path - URI for which FsStatus is requested    * @return Map of ViewFsMountPoint and FsStatus    */
DECL|method|getStatus ( FileSystem fileSystem, Path path)
specifier|public
specifier|static
name|Map
argument_list|<
name|MountPoint
argument_list|,
name|FsStatus
argument_list|>
name|getStatus
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isViewFileSystem
argument_list|(
name|fileSystem
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedFileSystemException
argument_list|(
literal|"FileSystem '"
operator|+
name|fileSystem
operator|.
name|getUri
argument_list|()
operator|+
literal|"'is not a ViewFileSystem."
argument_list|)
throw|;
block|}
name|ViewFileSystem
name|viewFileSystem
init|=
operator|(
name|ViewFileSystem
operator|)
name|fileSystem
decl_stmt|;
name|String
name|viewFsUriPath
init|=
name|viewFileSystem
operator|.
name|getUriPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|boolean
name|isPathOverMountPoint
init|=
literal|false
decl_stmt|;
name|boolean
name|isPathLeadingToMountPoint
init|=
literal|false
decl_stmt|;
name|boolean
name|isPathIncludesAllMountPoint
init|=
literal|false
decl_stmt|;
name|Map
argument_list|<
name|MountPoint
argument_list|,
name|FsStatus
argument_list|>
name|mountPointMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MountPoint
name|mountPoint
range|:
name|viewFileSystem
operator|.
name|getMountPoints
argument_list|()
control|)
block|{
name|String
index|[]
name|mountPointPathComponents
init|=
name|InodeTree
operator|.
name|breakIntoPathComponents
argument_list|(
name|mountPoint
operator|.
name|getMountedOnPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|incomingPathComponents
init|=
name|InodeTree
operator|.
name|breakIntoPathComponents
argument_list|(
name|viewFsUriPath
argument_list|)
decl_stmt|;
name|int
name|pathCompIndex
decl_stmt|;
for|for
control|(
name|pathCompIndex
operator|=
literal|0
init|;
name|pathCompIndex
operator|<
name|mountPointPathComponents
operator|.
name|length
operator|&&
name|pathCompIndex
operator|<
name|incomingPathComponents
operator|.
name|length
condition|;
name|pathCompIndex
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|mountPointPathComponents
index|[
name|pathCompIndex
index|]
operator|.
name|equals
argument_list|(
name|incomingPathComponents
index|[
name|pathCompIndex
index|]
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|pathCompIndex
operator|>=
name|mountPointPathComponents
operator|.
name|length
condition|)
block|{
comment|// Patch matches or over a valid mount point
name|isPathOverMountPoint
operator|=
literal|true
expr_stmt|;
name|mountPointMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|updateMountPointFsStatus
argument_list|(
name|viewFileSystem
argument_list|,
name|mountPointMap
argument_list|,
name|mountPoint
argument_list|,
operator|new
name|Path
argument_list|(
name|viewFsUriPath
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
if|if
condition|(
name|pathCompIndex
operator|>
literal|1
condition|)
block|{
comment|// Path is in the mount tree
name|isPathLeadingToMountPoint
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|incomingPathComponents
operator|.
name|length
operator|<=
literal|1
condition|)
block|{
comment|// Special case of "/" path
name|isPathIncludesAllMountPoint
operator|=
literal|true
expr_stmt|;
block|}
name|updateMountPointFsStatus
argument_list|(
name|viewFileSystem
argument_list|,
name|mountPointMap
argument_list|,
name|mountPoint
argument_list|,
name|mountPoint
operator|.
name|getMountedOnPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|isPathOverMountPoint
operator|&&
operator|!
name|isPathLeadingToMountPoint
operator|&&
operator|!
name|isPathIncludesAllMountPoint
condition|)
block|{
throw|throw
operator|new
name|NotInMountpointException
argument_list|(
name|path
argument_list|,
literal|"getStatus"
argument_list|)
throw|;
block|}
return|return
name|mountPointMap
return|;
block|}
comment|/**    * Update FsStatus for the given the mount point.    *    * @param viewFileSystem    * @param mountPointMap    * @param mountPoint    * @param path    */
DECL|method|updateMountPointFsStatus ( final ViewFileSystem viewFileSystem, final Map<MountPoint, FsStatus> mountPointMap, final MountPoint mountPoint, final Path path)
specifier|private
specifier|static
name|void
name|updateMountPointFsStatus
parameter_list|(
specifier|final
name|ViewFileSystem
name|viewFileSystem
parameter_list|,
specifier|final
name|Map
argument_list|<
name|MountPoint
argument_list|,
name|FsStatus
argument_list|>
name|mountPointMap
parameter_list|,
specifier|final
name|MountPoint
name|mountPoint
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FsStatus
name|fsStatus
init|=
name|viewFileSystem
operator|.
name|getStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|mountPointMap
operator|.
name|put
argument_list|(
name|mountPoint
argument_list|,
name|fsStatus
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

