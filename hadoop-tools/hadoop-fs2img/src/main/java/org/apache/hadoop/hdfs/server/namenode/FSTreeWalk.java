begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ConcurrentModificationException
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
name|permission
operator|.
name|AclStatus
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ACLS_IMPORT_ENABLED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ACLS_IMPORT_ENABLED_DEFAULT
import|;
end_import

begin_comment
comment|/**  * Traversal of an external FileSystem.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FSTreeWalk
specifier|public
class|class
name|FSTreeWalk
extends|extends
name|TreeWalk
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FSTreeWalk
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|root
specifier|private
specifier|final
name|Path
name|root
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|enableACLs
specifier|private
specifier|final
name|boolean
name|enableACLs
decl_stmt|;
DECL|method|FSTreeWalk (Path root, Configuration conf)
specifier|public
name|FSTreeWalk
parameter_list|(
name|Path
name|root
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|fs
operator|=
name|root
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|boolean
name|mountACLsEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFS_PROVIDED_ACLS_IMPORT_ENABLED
argument_list|,
name|DFS_PROVIDED_ACLS_IMPORT_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|localACLsEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|,
name|DFS_NAMENODE_ACLS_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|localACLsEnabled
operator|&&
name|mountACLsEnabled
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Mount ACLs have been enabled but HDFS ACLs are not. "
operator|+
literal|"Disabling ACLs on the mount {}"
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|enableACLs
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|enableACLs
operator|=
name|mountACLsEnabled
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getChildren (TreePath path, long id, TreeIterator i)
specifier|protected
name|Iterable
argument_list|<
name|TreePath
argument_list|>
name|getChildren
parameter_list|(
name|TreePath
name|path
parameter_list|,
name|long
name|id
parameter_list|,
name|TreeIterator
name|i
parameter_list|)
block|{
comment|// TODO symlinks
if|if
condition|(
operator|!
name|path
operator|.
name|getFileStatus
argument_list|()
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
try|try
block|{
name|ArrayList
argument_list|<
name|TreePath
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FileStatus
name|s
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|path
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|AclStatus
name|aclStatus
init|=
name|getAclStatus
argument_list|(
name|fs
argument_list|,
name|s
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
operator|new
name|TreePath
argument_list|(
name|s
argument_list|,
name|id
argument_list|,
name|i
argument_list|,
name|fs
argument_list|,
name|aclStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConcurrentModificationException
argument_list|(
literal|"FS modified"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|FSTreeIterator
class|class
name|FSTreeIterator
extends|extends
name|TreeIterator
block|{
DECL|method|FSTreeIterator ()
specifier|private
name|FSTreeIterator
parameter_list|()
block|{     }
DECL|method|FSTreeIterator (TreePath p)
name|FSTreeIterator
parameter_list|(
name|TreePath
name|p
parameter_list|)
block|{
name|this
argument_list|(
name|p
operator|.
name|getFileStatus
argument_list|()
argument_list|,
name|p
operator|.
name|getParentId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|FSTreeIterator (FileStatus fileStatus, long parentId)
name|FSTreeIterator
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|,
name|long
name|parentId
parameter_list|)
block|{
name|Path
name|path
init|=
name|fileStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|AclStatus
name|acls
decl_stmt|;
try|try
block|{
name|acls
operator|=
name|getAclStatus
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|TreePath
name|treePath
init|=
operator|new
name|TreePath
argument_list|(
name|fileStatus
argument_list|,
name|parentId
argument_list|,
name|this
argument_list|,
name|fs
argument_list|,
name|acls
argument_list|)
decl_stmt|;
name|getPendingQueue
argument_list|()
operator|.
name|addFirst
argument_list|(
name|treePath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fork ()
specifier|public
name|TreeIterator
name|fork
parameter_list|()
block|{
if|if
condition|(
name|getPendingQueue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|FSTreeIterator
argument_list|()
return|;
block|}
return|return
operator|new
name|FSTreeIterator
argument_list|(
name|getPendingQueue
argument_list|()
operator|.
name|removeFirst
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|getAclStatus (FileSystem fileSystem, Path path)
specifier|private
name|AclStatus
name|getAclStatus
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
return|return
name|enableACLs
condition|?
name|fileSystem
operator|.
name|getAclStatus
argument_list|(
name|path
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|TreeIterator
name|iterator
parameter_list|()
block|{
try|try
block|{
name|FileStatus
name|s
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|root
argument_list|)
decl_stmt|;
return|return
operator|new
name|FSTreeIterator
argument_list|(
name|s
argument_list|,
operator|-
literal|1L
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

