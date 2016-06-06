begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
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
operator|.
name|Unstable
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
name|Options
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
name|AclEntry
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
name|AclStatus
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
name|DFSOpsCountStatistics
operator|.
name|OpType
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
name|retry
operator|.
name|AsyncCallHandler
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
name|concurrent
operator|.
name|AsyncGetFuture
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
name|ipc
operator|.
name|Client
import|;
end_import

begin_comment
comment|/****************************************************************  * Implementation of the asynchronous distributed file system.  * This instance of this class is the way end-user code interacts  * with a Hadoop DistributedFileSystem in an asynchronous manner.  *  * This class is unstable, so no guarantee is provided as to reliability,  * stability or compatibility across any level of release granularity.  *  *****************************************************************/
end_comment

begin_class
annotation|@
name|Unstable
DECL|class|AsyncDistributedFileSystem
specifier|public
class|class
name|AsyncDistributedFileSystem
block|{
DECL|field|dfs
specifier|private
specifier|final
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|method|AsyncDistributedFileSystem (final DistributedFileSystem dfs)
name|AsyncDistributedFileSystem
parameter_list|(
specifier|final
name|DistributedFileSystem
name|dfs
parameter_list|)
block|{
name|this
operator|.
name|dfs
operator|=
name|dfs
expr_stmt|;
block|}
DECL|method|getReturnValue ()
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Future
argument_list|<
name|T
argument_list|>
name|getReturnValue
parameter_list|()
block|{
return|return
operator|new
name|AsyncGetFuture
argument_list|<>
argument_list|(
name|AsyncCallHandler
operator|.
name|getAsyncReturn
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Renames Path src to Path dst    *<ul>    *<li>Fails if src is a file and dst is a directory.    *<li>Fails if src is a directory and dst is a file.    *<li>Fails if the parent of dst does not exist or is a file.    *</ul>    *<p>    * If OVERWRITE option is not passed as an argument, rename fails if the dst    * already exists.    *<p>    * If OVERWRITE option is passed as an argument, rename overwrites the dst if    * it is a file or an empty directory. Rename fails if dst is a non-empty    * directory.    *<p>    * Note that atomicity of rename is dependent on the file system    * implementation. Please refer to the file system documentation for details.    * This default implementation is non atomic.    *    * @param src    *          path to be renamed    * @param dst    *          new path after rename    * @throws IOException    *           on failure    * @return an instance of Future, #get of which is invoked to wait for    *         asynchronous call being finished.    */
DECL|method|rename (Path src, Path dst, final Options.Rename... options)
specifier|public
name|Future
argument_list|<
name|Void
argument_list|>
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|,
specifier|final
name|Options
operator|.
name|Rename
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|getFsStatistics
argument_list|()
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getDFSOpsCountStatistics
argument_list|()
operator|.
name|incrementOpCounter
argument_list|(
name|OpType
operator|.
name|RENAME
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|absSrc
init|=
name|dfs
operator|.
name|fixRelativePart
argument_list|(
name|src
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|absDst
init|=
name|dfs
operator|.
name|fixRelativePart
argument_list|(
name|dst
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isAsync
init|=
name|Client
operator|.
name|isAsynchronousMode
argument_list|()
decl_stmt|;
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|rename
argument_list|(
name|dfs
operator|.
name|getPathName
argument_list|(
name|absSrc
argument_list|)
argument_list|,
name|dfs
operator|.
name|getPathName
argument_list|(
name|absDst
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
name|getReturnValue
argument_list|()
return|;
block|}
finally|finally
block|{
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
name|isAsync
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set permission of a path.    *    * @param p    *          the path the permission is set to    * @param permission    *          the permission that is set to a path.    * @return an instance of Future, #get of which is invoked to wait for    *         asynchronous call being finished.    */
DECL|method|setPermission (Path p, final FsPermission permission)
specifier|public
name|Future
argument_list|<
name|Void
argument_list|>
name|setPermission
parameter_list|(
name|Path
name|p
parameter_list|,
specifier|final
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|getFsStatistics
argument_list|()
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getDFSOpsCountStatistics
argument_list|()
operator|.
name|incrementOpCounter
argument_list|(
name|OpType
operator|.
name|SET_PERMISSION
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|absPath
init|=
name|dfs
operator|.
name|fixRelativePart
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isAsync
init|=
name|Client
operator|.
name|isAsynchronousMode
argument_list|()
decl_stmt|;
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|setPermission
argument_list|(
name|dfs
operator|.
name|getPathName
argument_list|(
name|absPath
argument_list|)
argument_list|,
name|permission
argument_list|)
expr_stmt|;
return|return
name|getReturnValue
argument_list|()
return|;
block|}
finally|finally
block|{
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
name|isAsync
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set owner of a path (i.e. a file or a directory). The parameters username    * and groupname cannot both be null.    *    * @param p    *          The path    * @param username    *          If it is null, the original username remains unchanged.    * @param groupname    *          If it is null, the original groupname remains unchanged.    * @return an instance of Future, #get of which is invoked to wait for    *         asynchronous call being finished.    */
DECL|method|setOwner (Path p, String username, String groupname)
specifier|public
name|Future
argument_list|<
name|Void
argument_list|>
name|setOwner
parameter_list|(
name|Path
name|p
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|groupname
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|username
operator|==
literal|null
operator|&&
name|groupname
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"username == null&& groupname == null"
argument_list|)
throw|;
block|}
name|dfs
operator|.
name|getFsStatistics
argument_list|()
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getDFSOpsCountStatistics
argument_list|()
operator|.
name|incrementOpCounter
argument_list|(
name|OpType
operator|.
name|SET_OWNER
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|absPath
init|=
name|dfs
operator|.
name|fixRelativePart
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isAsync
init|=
name|Client
operator|.
name|isAsynchronousMode
argument_list|()
decl_stmt|;
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|setOwner
argument_list|(
name|dfs
operator|.
name|getPathName
argument_list|(
name|absPath
argument_list|)
argument_list|,
name|username
argument_list|,
name|groupname
argument_list|)
expr_stmt|;
return|return
name|getReturnValue
argument_list|()
return|;
block|}
finally|finally
block|{
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
name|isAsync
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Fully replaces ACL of files and directories, discarding all existing    * entries.    *    * @param p    *          Path to modify    * @param aclSpec    *          List<AclEntry> describing modifications, must include entries for    *          user, group, and others for compatibility with permission bits.    * @throws IOException    *           if an ACL could not be modified    * @return an instance of Future, #get of which is invoked to wait for    *         asynchronous call being finished.    */
DECL|method|setAcl (Path p, final List<AclEntry> aclSpec)
specifier|public
name|Future
argument_list|<
name|Void
argument_list|>
name|setAcl
parameter_list|(
name|Path
name|p
parameter_list|,
specifier|final
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|getFsStatistics
argument_list|()
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getDFSOpsCountStatistics
argument_list|()
operator|.
name|incrementOpCounter
argument_list|(
name|OpType
operator|.
name|SET_ACL
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|absPath
init|=
name|dfs
operator|.
name|fixRelativePart
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isAsync
init|=
name|Client
operator|.
name|isAsynchronousMode
argument_list|()
decl_stmt|;
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|setAcl
argument_list|(
name|dfs
operator|.
name|getPathName
argument_list|(
name|absPath
argument_list|)
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
return|return
name|getReturnValue
argument_list|()
return|;
block|}
finally|finally
block|{
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
name|isAsync
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gets the ACL of a file or directory.    *    * @param p    *          Path to get    * @return AclStatus describing the ACL of the file or directory    * @throws IOException    *           if an ACL could not be read    * @return an instance of Future, #get of which is invoked to wait for    *         asynchronous call being finished.    */
DECL|method|getAclStatus (Path p)
specifier|public
name|Future
argument_list|<
name|AclStatus
argument_list|>
name|getAclStatus
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|absPath
init|=
name|dfs
operator|.
name|fixRelativePart
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isAsync
init|=
name|Client
operator|.
name|isAsynchronousMode
argument_list|()
decl_stmt|;
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getAclStatus
argument_list|(
name|dfs
operator|.
name|getPathName
argument_list|(
name|absPath
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|getReturnValue
argument_list|()
return|;
block|}
finally|finally
block|{
name|Client
operator|.
name|setAsynchronousMode
argument_list|(
name|isAsync
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

