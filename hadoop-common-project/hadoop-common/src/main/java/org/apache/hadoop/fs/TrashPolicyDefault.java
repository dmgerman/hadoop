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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|FS_TRASH_CHECKPOINT_INTERVAL_DEFAULT
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
name|CommonConfigurationKeysPublic
operator|.
name|FS_TRASH_CHECKPOINT_INTERVAL_KEY
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
name|CommonConfigurationKeysPublic
operator|.
name|FS_TRASH_INTERVAL_DEFAULT
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
name|CommonConfigurationKeysPublic
operator|.
name|FS_TRASH_INTERVAL_KEY
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
name|Collection
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
name|Options
operator|.
name|Rename
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
name|Time
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

begin_comment
comment|/** Provides a<i>trash</i> feature.  Files are moved to a user's trash  * directory, a subdirectory of their home directory named ".Trash".  Files are  * initially moved to a<i>current</i> sub-directory of the trash directory.  * Within that sub-directory their original path is preserved.  Periodically  * one may checkpoint the current trash and remove older checkpoints.  (This  * design permits trash management without enumeration of the full trash  * content, without date support in the filesystem, and without clock  * synchronization.)  */
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
DECL|class|TrashPolicyDefault
specifier|public
class|class
name|TrashPolicyDefault
extends|extends
name|TrashPolicy
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TrashPolicyDefault
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
comment|/** Format of checkpoint directories used prior to Hadoop 0.23. */
DECL|field|OLD_CHECKPOINT
specifier|private
specifier|static
specifier|final
name|DateFormat
name|OLD_CHECKPOINT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyMMddHHmm"
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
DECL|field|emptierInterval
specifier|private
name|long
name|emptierInterval
decl_stmt|;
DECL|method|TrashPolicyDefault ()
specifier|public
name|TrashPolicyDefault
parameter_list|()
block|{ }
DECL|method|TrashPolicyDefault (FileSystem fs, Configuration conf)
specifier|private
name|TrashPolicyDefault
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
name|initialize
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated Use {@link #initialize(Configuration, FileSystem)} instead.    */
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|initialize (Configuration conf, FileSystem fs, Path home)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|home
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
block|}
annotation|@
name|Override
DECL|method|initialize (Configuration conf, FileSystem fs)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
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
name|Path
operator|.
name|mergePaths
argument_list|(
name|basePath
argument_list|,
name|rmFilePath
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isEnabled ()
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|deletionInterval
operator|!=
literal|0
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
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
comment|// check that path exists
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|String
name|qpath
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Path
name|trashRoot
init|=
name|fs
operator|.
name|getTrashRoot
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Path
name|trashCurrent
init|=
operator|new
name|Path
argument_list|(
name|trashRoot
argument_list|,
name|CURRENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|qpath
operator|.
name|startsWith
argument_list|(
name|trashRoot
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
name|trashRoot
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
name|trashCurrent
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|Path
name|baseTrashPath
init|=
name|makeTrashRelativePath
argument_list|(
name|trashCurrent
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
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{
comment|// find the path which is not a directory, and modify baseTrashPath
comment|//& trashPath, then mkdirs
name|Path
name|existsFilePath
init|=
name|baseTrashPath
decl_stmt|;
while|while
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|existsFilePath
argument_list|)
condition|)
block|{
name|existsFilePath
operator|=
name|existsFilePath
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|baseTrashPath
operator|=
operator|new
name|Path
argument_list|(
name|baseTrashPath
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
name|existsFilePath
operator|.
name|toString
argument_list|()
argument_list|,
name|existsFilePath
operator|.
name|toString
argument_list|()
operator|+
name|Time
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|trashPath
operator|=
operator|new
name|Path
argument_list|(
name|baseTrashPath
argument_list|,
name|trashPath
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// retry, ignore current failure
operator|--
name|i
expr_stmt|;
continue|continue;
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
argument_list|,
name|e
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
comment|// if the target path in Trash already exists, then append with
comment|// a current time in millisecs.
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
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// move to current trash
name|fs
operator|.
name|rename
argument_list|(
name|path
argument_list|,
name|trashPath
argument_list|,
name|Rename
operator|.
name|TO_TRASH
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Moved: '"
operator|+
name|path
operator|+
literal|"' to trash at: "
operator|+
name|trashPath
argument_list|)
expr_stmt|;
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|createCheckpoint ()
specifier|public
name|void
name|createCheckpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|createCheckpoint
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|createCheckpoint (Date date)
specifier|public
name|void
name|createCheckpoint
parameter_list|(
name|Date
name|date
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|FileStatus
argument_list|>
name|trashRoots
init|=
name|fs
operator|.
name|getTrashRoots
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|trashRoot
range|:
name|trashRoots
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"TrashPolicyDefault#createCheckpoint for trashRoot: "
operator|+
name|trashRoot
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|createCheckpoint
argument_list|(
name|trashRoot
operator|.
name|getPath
argument_list|()
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteCheckpoint ()
specifier|public
name|void
name|deleteCheckpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|FileStatus
argument_list|>
name|trashRoots
init|=
name|fs
operator|.
name|getTrashRoots
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|trashRoot
range|:
name|trashRoots
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"TrashPolicyDefault#deleteCheckpoint for trashRoot: "
operator|+
name|trashRoot
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|deleteCheckpoint
argument_list|(
name|trashRoot
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCurrentTrashDir ()
specifier|public
name|Path
name|getCurrentTrashDir
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
name|fs
operator|.
name|getTrashRoot
argument_list|(
literal|null
argument_list|)
argument_list|,
name|CURRENT
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentTrashDir (Path path)
specifier|public
name|Path
name|getCurrentTrashDir
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Path
argument_list|(
name|fs
operator|.
name|getTrashRoot
argument_list|(
name|path
argument_list|)
argument_list|,
name|CURRENT
argument_list|)
return|;
block|}
annotation|@
name|Override
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
argument_list|,
name|emptierInterval
argument_list|)
return|;
block|}
DECL|class|Emptier
specifier|protected
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
DECL|method|Emptier (Configuration conf, long emptierInterval)
name|Emptier
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
name|emptierInterval
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
name|emptierInterval
expr_stmt|;
if|if
condition|(
name|emptierInterval
operator|>
name|deletionInterval
operator|||
name|emptierInterval
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The configured checkpoint interval is "
operator|+
operator|(
name|emptierInterval
operator|/
name|MSECS_PER_MINUTE
operator|)
operator|+
literal|" minutes."
operator|+
literal|" Using an interval of "
operator|+
operator|(
name|deletionInterval
operator|/
name|MSECS_PER_MINUTE
operator|)
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Namenode trash configuration: Deletion interval = "
operator|+
operator|(
name|deletionInterval
operator|/
name|MSECS_PER_MINUTE
operator|)
operator|+
literal|" minutes, Emptier interval = "
operator|+
operator|(
name|emptierInterval
operator|/
name|MSECS_PER_MINUTE
operator|)
operator|+
literal|" minutes."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|Time
operator|.
name|now
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
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
if|if
condition|(
name|now
operator|>=
name|end
condition|)
block|{
name|Collection
argument_list|<
name|FileStatus
argument_list|>
name|trashRoots
decl_stmt|;
name|trashRoots
operator|=
name|fs
operator|.
name|getTrashRoots
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// list all trash dirs
for|for
control|(
name|FileStatus
name|trashRoot
range|:
name|trashRoots
control|)
block|{
comment|// dump each trash
if|if
condition|(
operator|!
name|trashRoot
operator|.
name|isDirectory
argument_list|()
condition|)
continue|continue;
try|try
block|{
name|TrashPolicyDefault
name|trash
init|=
operator|new
name|TrashPolicyDefault
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|trash
operator|.
name|deleteCheckpoint
argument_list|(
name|trashRoot
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|trash
operator|.
name|createCheckpoint
argument_list|(
name|trashRoot
operator|.
name|getPath
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|(
name|now
argument_list|)
argument_list|)
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
name|trashRoot
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
literal|"RuntimeException during Trash.Emptier.run(): "
argument_list|,
name|e
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
literal|"Trash cannot close FileSystem: "
argument_list|,
name|e
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
annotation|@
name|VisibleForTesting
DECL|method|getEmptierInterval ()
specifier|protected
name|long
name|getEmptierInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|emptierInterval
operator|/
name|MSECS_PER_MINUTE
return|;
block|}
block|}
DECL|method|createCheckpoint (Path trashRoot, Date date)
specifier|private
name|void
name|createCheckpoint
parameter_list|(
name|Path
name|trashRoot
parameter_list|,
name|Date
name|date
parameter_list|)
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
operator|new
name|Path
argument_list|(
name|trashRoot
argument_list|,
name|CURRENT
argument_list|)
argument_list|)
condition|)
block|{
return|return;
block|}
name|Path
name|checkpointBase
decl_stmt|;
synchronized|synchronized
init|(
name|CHECKPOINT
init|)
block|{
name|checkpointBase
operator|=
operator|new
name|Path
argument_list|(
name|trashRoot
argument_list|,
name|CHECKPOINT
operator|.
name|format
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Path
name|checkpoint
init|=
name|checkpointBase
decl_stmt|;
name|Path
name|current
init|=
operator|new
name|Path
argument_list|(
name|trashRoot
argument_list|,
name|CURRENT
argument_list|)
decl_stmt|;
name|int
name|attempt
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|current
argument_list|,
name|checkpoint
argument_list|,
name|Rename
operator|.
name|NONE
argument_list|)
expr_stmt|;
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
break|break;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|++
name|attempt
operator|>
literal|1000
condition|)
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
name|checkpoint
operator|=
name|checkpointBase
operator|.
name|suffix
argument_list|(
literal|"-"
operator|+
name|attempt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|deleteCheckpoint (Path trashRoot)
specifier|private
name|void
name|deleteCheckpoint
parameter_list|(
name|Path
name|trashRoot
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"TrashPolicyDefault#deleteCheckpoint for trashRoot: "
operator|+
name|trashRoot
argument_list|)
expr_stmt|;
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
name|trashRoot
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
name|Time
operator|.
name|now
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
block|{
comment|// skip current
continue|continue;
block|}
name|long
name|time
decl_stmt|;
try|try
block|{
name|time
operator|=
name|getTimeFromCheckpoint
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
DECL|method|getTimeFromCheckpoint (String name)
specifier|private
name|long
name|getTimeFromCheckpoint
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ParseException
block|{
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
name|pe
parameter_list|)
block|{
comment|// Check for old-style checkpoint directories left over
comment|// after an upgrade from Hadoop 1.x
synchronized|synchronized
init|(
name|OLD_CHECKPOINT
init|)
block|{
name|time
operator|=
name|OLD_CHECKPOINT
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
return|return
name|time
return|;
block|}
block|}
end_class

end_unit

