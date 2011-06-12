begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|concurrent
operator|.
name|LinkedBlockingQueue
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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

begin_class
DECL|class|CleanupQueue
class|class
name|CleanupQueue
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CleanupQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cleanupThread
specifier|private
specifier|static
name|PathCleanupThread
name|cleanupThread
decl_stmt|;
comment|/**    * Create a singleton path-clean-up queue. It can be used to delete    * paths(directories/files) in a separate thread. This constructor creates a    * clean-up thread and also starts it as a daemon. Callers can instantiate one    * CleanupQueue per JVM and can use it for deleting paths. Use    * {@link CleanupQueue#addToQueue(PathDeletionContext...)} to add paths for    * deletion.    */
DECL|method|CleanupQueue ()
specifier|public
name|CleanupQueue
parameter_list|()
block|{
synchronized|synchronized
init|(
name|PathCleanupThread
operator|.
name|class
init|)
block|{
if|if
condition|(
name|cleanupThread
operator|==
literal|null
condition|)
block|{
name|cleanupThread
operator|=
operator|new
name|PathCleanupThread
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Contains info related to the path of the file/dir to be deleted    */
DECL|class|PathDeletionContext
specifier|static
class|class
name|PathDeletionContext
block|{
DECL|field|fullPath
name|String
name|fullPath
decl_stmt|;
comment|// full path of file or dir
DECL|field|fs
name|FileSystem
name|fs
decl_stmt|;
DECL|method|PathDeletionContext (FileSystem fs, String fullPath)
specifier|public
name|PathDeletionContext
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|fullPath
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|fullPath
operator|=
name|fullPath
expr_stmt|;
block|}
DECL|method|getPathForCleanup ()
specifier|protected
name|String
name|getPathForCleanup
parameter_list|()
block|{
return|return
name|fullPath
return|;
block|}
comment|/**      * Makes the path(and its subdirectories recursively) fully deletable      */
DECL|method|enablePathForCleanup ()
specifier|protected
name|void
name|enablePathForCleanup
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Do nothing by default.
comment|// Subclasses can override to provide enabling for deletion.
block|}
block|}
comment|/**    * Adds the paths to the queue of paths to be deleted by cleanupThread.    */
DECL|method|addToQueue (PathDeletionContext... contexts)
name|void
name|addToQueue
parameter_list|(
name|PathDeletionContext
modifier|...
name|contexts
parameter_list|)
block|{
name|cleanupThread
operator|.
name|addToQueue
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
block|}
DECL|method|deletePath (PathDeletionContext context)
specifier|protected
specifier|static
name|boolean
name|deletePath
parameter_list|(
name|PathDeletionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|context
operator|.
name|enablePathForCleanup
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Trying to delete "
operator|+
name|context
operator|.
name|fullPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|context
operator|.
name|fullPath
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|context
operator|.
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|context
operator|.
name|fullPath
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
comment|// currently used by tests only
DECL|method|isQueueEmpty ()
specifier|protected
name|boolean
name|isQueueEmpty
parameter_list|()
block|{
return|return
operator|(
name|cleanupThread
operator|.
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
return|;
block|}
DECL|class|PathCleanupThread
specifier|private
specifier|static
class|class
name|PathCleanupThread
extends|extends
name|Thread
block|{
comment|// cleanup queue which deletes files/directories of the paths queued up.
DECL|field|queue
specifier|private
name|LinkedBlockingQueue
argument_list|<
name|PathDeletionContext
argument_list|>
name|queue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|PathDeletionContext
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|PathCleanupThread ()
specifier|public
name|PathCleanupThread
parameter_list|()
block|{
name|setName
argument_list|(
literal|"Directory/File cleanup thread"
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|addToQueue (PathDeletionContext[] contexts)
name|void
name|addToQueue
parameter_list|(
name|PathDeletionContext
index|[]
name|contexts
parameter_list|)
block|{
for|for
control|(
name|PathDeletionContext
name|context
range|:
name|contexts
control|)
block|{
try|try
block|{
name|queue
operator|.
name|put
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{}
block|}
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|" started."
argument_list|)
expr_stmt|;
block|}
name|PathDeletionContext
name|context
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|context
operator|=
name|queue
operator|.
name|take
argument_list|()
expr_stmt|;
comment|// delete the path.
if|if
condition|(
operator|!
name|deletePath
argument_list|(
name|context
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"CleanupThread:Unable to delete path "
operator|+
name|context
operator|.
name|fullPath
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"DELETED "
operator|+
name|context
operator|.
name|fullPath
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted deletion of "
operator|+
name|context
operator|.
name|fullPath
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error deleting path "
operator|+
name|context
operator|.
name|fullPath
operator|+
literal|": "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

