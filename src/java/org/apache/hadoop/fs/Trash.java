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
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|conf
operator|.
name|Configured
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
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|*
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
name|FsAction
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/** Provides a<i>trash</i> feature.  Files are moved to a user's trash  * directory, a subdirectory of their home directory named ".Trash".  Files are  * initially moved to a<i>current</i> sub-directory of the trash directory.  * Within that sub-directory their original path is preserved.  Periodically  * one may checkpoint the current trash and remove older checkpoints.  (This  * design permits trash management without enumeration of the full trash  * content, without date support in the filesystem, and without clock  * synchronization.)  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|Trash
specifier|public
class|class
name|Trash
extends|extends
name|Configured
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Trash
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CURRENT
specifier|private
specifier|static
specifier|final
name|Path
name|CURRENT
init|=
operator|new
name|Path
argument_list|(
literal|"Current"
argument_list|)
decl_stmt|;
DECL|field|TRASH
specifier|private
specifier|static
specifier|final
name|Path
name|TRASH
init|=
operator|new
name|Path
argument_list|(
literal|".Trash/"
argument_list|)
decl_stmt|;
DECL|field|PERMISSION
specifier|private
specifier|static
specifier|final
name|FsPermission
name|PERMISSION
init|=
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
decl_stmt|;
DECL|field|CHECKPOINT
specifier|private
specifier|static
specifier|final
name|DateFormat
name|CHECKPOINT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyMMddHHmmss"
argument_list|)
decl_stmt|;
DECL|field|MSECS_PER_MINUTE
specifier|private
specifier|static
specifier|final
name|int
name|MSECS_PER_MINUTE
init|=
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|trash
specifier|private
specifier|final
name|Path
name|trash
decl_stmt|;
DECL|field|current
specifier|private
specifier|final
name|Path
name|current
decl_stmt|;
DECL|field|deletionInterval
specifier|private
specifier|final
name|long
name|deletionInterval
decl_stmt|;
DECL|field|homesParent
specifier|private
specifier|final
name|Path
name|homesParent
decl_stmt|;
comment|/** Construct a trash can accessor.    * @param conf a Configuration    */
DECL|method|Trash (Configuration conf)
specifier|public
name|Trash
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a trash can accessor for the FileSystem provided.    */
DECL|method|Trash (FileSystem fs, Configuration conf)
specifier|public
name|Trash
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|trash
operator|=
operator|new
name|Path
argument_list|(
name|fs
operator|.
name|getHomeDirectory
argument_list|()
argument_list|,
name|TRASH
argument_list|)
expr_stmt|;
name|this
operator|.
name|homesParent
operator|=
name|fs
operator|.
name|getHomeDirectory
argument_list|()
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|this
operator|.
name|current
operator|=
operator|new
name|Path
argument_list|(
name|trash
argument_list|,
name|CURRENT
argument_list|)
expr_stmt|;
name|this
operator|.
name|deletionInterval
operator|=
call|(
name|long
call|)
argument_list|(
name|conf
operator|.
name|getFloat
argument_list|(
name|FS_TRASH_INTERVAL_KEY
argument_list|,
name|FS_TRASH_INTERVAL_DEFAULT
argument_list|)
operator|*
name|MSECS_PER_MINUTE
argument_list|)
expr_stmt|;
block|}
DECL|method|Trash (Path home, Configuration conf)
specifier|private
name|Trash
parameter_list|(
name|Path
name|home
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|home
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|trash
operator|=
operator|new
name|Path
argument_list|(
name|home
argument_list|,
name|TRASH
argument_list|)
expr_stmt|;
name|this
operator|.
name|homesParent
operator|=
name|home
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|this
operator|.
name|current
operator|=
operator|new
name|Path
argument_list|(
name|trash
argument_list|,
name|CURRENT
argument_list|)
expr_stmt|;
name|this
operator|.
name|deletionInterval
operator|=
call|(
name|long
call|)
argument_list|(
name|conf
operator|.
name|getFloat
argument_list|(
name|FS_TRASH_INTERVAL_KEY
argument_list|,
name|FS_TRASH_INTERVAL_DEFAULT
argument_list|)
operator|*
name|MSECS_PER_MINUTE
argument_list|)
expr_stmt|;
block|}
DECL|method|makeTrashRelativePath (Path basePath, Path rmFilePath)
specifier|private
name|Path
name|makeTrashRelativePath
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|Path
name|rmFilePath
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|basePath
operator|+
name|rmFilePath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns whether the trash is enabled for this filesystem    */
DECL|method|isEnabled ()
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
operator|(
name|deletionInterval
operator|!=
literal|0
operator|)
return|;
block|}
comment|/** Move a file or directory to the current trash directory.    * @return false if the item is already in the trash or trash is disabled    */
DECL|method|moveToTrash (Path path)
specifier|public
name|boolean
name|moveToTrash
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isEnabled
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|path
operator|.
name|isAbsolute
argument_list|()
condition|)
comment|// make path absolute
name|path
operator|=
operator|new
name|Path
argument_list|(
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
comment|// check that path exists
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
name|String
name|qpath
init|=
name|path
operator|.
name|makeQualified
argument_list|(
name|fs
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|qpath
operator|.
name|startsWith
argument_list|(
name|trash
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
comment|// already in trash
block|}
if|if
condition|(
name|trash
operator|.
name|getParent
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|qpath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot move \""
operator|+
name|path
operator|+
literal|"\" to the trash, as it contains the trash"
argument_list|)
throw|;
block|}
name|Path
name|trashPath
init|=
name|makeTrashRelativePath
argument_list|(
name|current
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|Path
name|baseTrashPath
init|=
name|makeTrashRelativePath
argument_list|(
name|current
argument_list|,
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
name|IOException
name|cause
init|=
literal|null
decl_stmt|;
comment|// try twice, in case checkpoint between the mkdirs()& rename()
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|baseTrashPath
argument_list|,
name|PERMISSION
argument_list|)
condition|)
block|{
comment|// create current
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't create(mkdir) trash directory: "
operator|+
name|baseTrashPath
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't create trash directory: "
operator|+
name|baseTrashPath
argument_list|)
expr_stmt|;
name|cause
operator|=
name|e
expr_stmt|;
break|break;
block|}
try|try
block|{
comment|//
comment|// if the target path in Trash already exists, then append with
comment|// a current time in millisecs.
comment|//
name|String
name|orig
init|=
name|trashPath
operator|.
name|toString
argument_list|()
decl_stmt|;
while|while
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|trashPath
argument_list|)
condition|)
block|{
name|trashPath
operator|=
operator|new
name|Path
argument_list|(
name|orig
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fs
operator|.
name|rename
argument_list|(
name|path
argument_list|,
name|trashPath
argument_list|)
condition|)
comment|// move to current trash
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|cause
operator|=
name|e
expr_stmt|;
block|}
block|}
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Failed to move to trash: "
operator|+
name|path
argument_list|)
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
throw|;
block|}
comment|/** Create a trash checkpoint. */
DECL|method|checkpoint ()
specifier|public
name|void
name|checkpoint
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|current
argument_list|)
condition|)
comment|// no trash, no checkpoint
return|return;
name|Path
name|checkpoint
decl_stmt|;
synchronized|synchronized
init|(
name|CHECKPOINT
init|)
block|{
name|checkpoint
operator|=
operator|new
name|Path
argument_list|(
name|trash
argument_list|,
name|CHECKPOINT
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fs
operator|.
name|rename
argument_list|(
name|current
argument_list|,
name|checkpoint
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Created trash checkpoint: "
operator|+
name|checkpoint
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to checkpoint trash: "
operator|+
name|checkpoint
argument_list|)
throw|;
block|}
block|}
comment|/** Delete old checkpoints. */
DECL|method|expunge ()
specifier|public
name|void
name|expunge
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
index|[]
name|dirs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dirs
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|trash
argument_list|)
expr_stmt|;
comment|// scan trash sub-directories
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
return|return;
block|}
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|path
init|=
name|dirs
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|dir
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|path
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|CURRENT
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
comment|// skip current
continue|continue;
name|long
name|time
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|CHECKPOINT
init|)
block|{
name|time
operator|=
name|CHECKPOINT
operator|.
name|parse
argument_list|(
name|name
argument_list|)
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected item in trash: "
operator|+
name|dir
operator|+
literal|". Ignoring."
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|(
name|now
operator|-
name|deletionInterval
operator|)
operator|>
name|time
condition|)
block|{
if|if
condition|(
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleted trash checkpoint: "
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't delete checkpoint: "
operator|+
name|dir
operator|+
literal|" Ignoring."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//
comment|// get the current working directory
comment|//
DECL|method|getCurrentTrashDir ()
name|Path
name|getCurrentTrashDir
parameter_list|()
block|{
return|return
name|current
return|;
block|}
comment|/** Return a {@link Runnable} that periodically empties the trash of all    * users, intended to be run by the superuser.  Only one checkpoint is kept    * at a time.    */
DECL|method|getEmptier ()
specifier|public
name|Runnable
name|getEmptier
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Emptier
argument_list|(
name|getConf
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Emptier
specifier|private
class|class
name|Emptier
implements|implements
name|Runnable
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|emptierInterval
specifier|private
name|long
name|emptierInterval
decl_stmt|;
DECL|method|Emptier (Configuration conf)
name|Emptier
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|emptierInterval
operator|=
call|(
name|long
call|)
argument_list|(
name|conf
operator|.
name|getFloat
argument_list|(
name|FS_TRASH_CHECKPOINT_INTERVAL_KEY
argument_list|,
name|FS_TRASH_CHECKPOINT_INTERVAL_DEFAULT
argument_list|)
operator|*
name|MSECS_PER_MINUTE
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|emptierInterval
operator|>
name|deletionInterval
operator|||
name|this
operator|.
name|emptierInterval
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The configured interval for checkpoint is "
operator|+
name|this
operator|.
name|emptierInterval
operator|+
literal|" minutes."
operator|+
literal|" Using interval of "
operator|+
name|deletionInterval
operator|+
literal|" minutes that is used for deletion instead"
argument_list|)
expr_stmt|;
name|this
operator|.
name|emptierInterval
operator|=
name|deletionInterval
expr_stmt|;
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
name|emptierInterval
operator|==
literal|0
condition|)
return|return;
comment|// trash disabled
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|end
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|end
operator|=
name|ceiling
argument_list|(
name|now
argument_list|,
name|emptierInterval
argument_list|)
expr_stmt|;
try|try
block|{
comment|// sleep for interval
name|Thread
operator|.
name|sleep
argument_list|(
name|end
operator|-
name|now
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
break|break;
comment|// exit on interrupt
block|}
try|try
block|{
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
name|now
operator|>=
name|end
condition|)
block|{
name|FileStatus
index|[]
name|homes
init|=
literal|null
decl_stmt|;
try|try
block|{
name|homes
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|homesParent
argument_list|)
expr_stmt|;
comment|// list all home dirs
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trash can't list homes: "
operator|+
name|e
operator|+
literal|" Sleeping."
argument_list|)
expr_stmt|;
continue|continue;
block|}
for|for
control|(
name|FileStatus
name|home
range|:
name|homes
control|)
block|{
comment|// dump each trash
if|if
condition|(
operator|!
name|home
operator|.
name|isDirectory
argument_list|()
condition|)
continue|continue;
try|try
block|{
name|Trash
name|trash
init|=
operator|new
name|Trash
argument_list|(
name|home
operator|.
name|getPath
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|trash
operator|.
name|expunge
argument_list|()
expr_stmt|;
name|trash
operator|.
name|checkpoint
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trash caught: "
operator|+
name|e
operator|+
literal|". Skipping "
operator|+
name|home
operator|.
name|getPath
argument_list|()
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
literal|"RuntimeException during Trash.Emptier.run() "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trash cannot close FileSystem. "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|ceiling (long time, long interval)
specifier|private
name|long
name|ceiling
parameter_list|(
name|long
name|time
parameter_list|,
name|long
name|interval
parameter_list|)
block|{
return|return
name|floor
argument_list|(
name|time
argument_list|,
name|interval
argument_list|)
operator|+
name|interval
return|;
block|}
DECL|method|floor (long time, long interval)
specifier|private
name|long
name|floor
parameter_list|(
name|long
name|time
parameter_list|,
name|long
name|interval
parameter_list|)
block|{
return|return
operator|(
name|time
operator|/
name|interval
operator|)
operator|*
name|interval
return|;
block|}
block|}
comment|/** Run an emptier.*/
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|Trash
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
operator|.
name|getEmptier
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

