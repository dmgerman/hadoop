begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.nativeio
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|nativeio
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileDescriptor
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|NativeCodeLoader
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

begin_comment
comment|/**  * JNI wrappers for various native IO-related calls not available in Java.  * These functions should generally be used alongside a fallback to another  * more portable mechanism.  */
end_comment

begin_class
DECL|class|NativeIO
specifier|public
class|class
name|NativeIO
block|{
comment|// Flags for open() call from bits/fcntl.h
DECL|field|O_RDONLY
specifier|public
specifier|static
specifier|final
name|int
name|O_RDONLY
init|=
literal|00
decl_stmt|;
DECL|field|O_WRONLY
specifier|public
specifier|static
specifier|final
name|int
name|O_WRONLY
init|=
literal|01
decl_stmt|;
DECL|field|O_RDWR
specifier|public
specifier|static
specifier|final
name|int
name|O_RDWR
init|=
literal|02
decl_stmt|;
DECL|field|O_CREAT
specifier|public
specifier|static
specifier|final
name|int
name|O_CREAT
init|=
literal|0100
decl_stmt|;
DECL|field|O_EXCL
specifier|public
specifier|static
specifier|final
name|int
name|O_EXCL
init|=
literal|0200
decl_stmt|;
DECL|field|O_NOCTTY
specifier|public
specifier|static
specifier|final
name|int
name|O_NOCTTY
init|=
literal|0400
decl_stmt|;
DECL|field|O_TRUNC
specifier|public
specifier|static
specifier|final
name|int
name|O_TRUNC
init|=
literal|01000
decl_stmt|;
DECL|field|O_APPEND
specifier|public
specifier|static
specifier|final
name|int
name|O_APPEND
init|=
literal|02000
decl_stmt|;
DECL|field|O_NONBLOCK
specifier|public
specifier|static
specifier|final
name|int
name|O_NONBLOCK
init|=
literal|04000
decl_stmt|;
DECL|field|O_SYNC
specifier|public
specifier|static
specifier|final
name|int
name|O_SYNC
init|=
literal|010000
decl_stmt|;
DECL|field|O_ASYNC
specifier|public
specifier|static
specifier|final
name|int
name|O_ASYNC
init|=
literal|020000
decl_stmt|;
DECL|field|O_FSYNC
specifier|public
specifier|static
specifier|final
name|int
name|O_FSYNC
init|=
name|O_SYNC
decl_stmt|;
DECL|field|O_NDELAY
specifier|public
specifier|static
specifier|final
name|int
name|O_NDELAY
init|=
name|O_NONBLOCK
decl_stmt|;
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
name|NativeIO
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nativeLoaded
specifier|private
specifier|static
name|boolean
name|nativeLoaded
init|=
literal|false
decl_stmt|;
static|static
block|{
if|if
condition|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
try|try
block|{
name|initNative
argument_list|()
expr_stmt|;
name|nativeLoaded
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// This can happen if the user has an older version of libhadoop.so
comment|// installed - in this case we can continue without native IO
comment|// after warning
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to initialize NativeIO libraries"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Return true if the JNI-based native IO extensions are available.    */
DECL|method|isAvailable ()
specifier|public
specifier|static
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
operator|&&
name|nativeLoaded
return|;
block|}
comment|/** Wrapper around open(2) */
DECL|method|open (String path, int flags, int mode)
specifier|public
specifier|static
specifier|native
name|FileDescriptor
name|open
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|flags
parameter_list|,
name|int
name|mode
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Wrapper around fstat(2) */
DECL|method|fstat (FileDescriptor fd)
specifier|public
specifier|static
specifier|native
name|Stat
name|fstat
parameter_list|(
name|FileDescriptor
name|fd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Initialize the JNI method ID and class ID cache */
DECL|method|initNative ()
specifier|private
specifier|static
specifier|native
name|void
name|initNative
parameter_list|()
function_decl|;
comment|/**    * Result type of the fstat call    */
DECL|class|Stat
specifier|public
specifier|static
class|class
name|Stat
block|{
DECL|field|owner
DECL|field|group
specifier|private
name|String
name|owner
decl_stmt|,
name|group
decl_stmt|;
DECL|field|mode
specifier|private
name|int
name|mode
decl_stmt|;
comment|// Mode constants
DECL|field|S_IFMT
specifier|public
specifier|static
specifier|final
name|int
name|S_IFMT
init|=
literal|0170000
decl_stmt|;
comment|/* type of file */
DECL|field|S_IFIFO
specifier|public
specifier|static
specifier|final
name|int
name|S_IFIFO
init|=
literal|0010000
decl_stmt|;
comment|/* named pipe (fifo) */
DECL|field|S_IFCHR
specifier|public
specifier|static
specifier|final
name|int
name|S_IFCHR
init|=
literal|0020000
decl_stmt|;
comment|/* character special */
DECL|field|S_IFDIR
specifier|public
specifier|static
specifier|final
name|int
name|S_IFDIR
init|=
literal|0040000
decl_stmt|;
comment|/* directory */
DECL|field|S_IFBLK
specifier|public
specifier|static
specifier|final
name|int
name|S_IFBLK
init|=
literal|0060000
decl_stmt|;
comment|/* block special */
DECL|field|S_IFREG
specifier|public
specifier|static
specifier|final
name|int
name|S_IFREG
init|=
literal|0100000
decl_stmt|;
comment|/* regular */
DECL|field|S_IFLNK
specifier|public
specifier|static
specifier|final
name|int
name|S_IFLNK
init|=
literal|0120000
decl_stmt|;
comment|/* symbolic link */
DECL|field|S_IFSOCK
specifier|public
specifier|static
specifier|final
name|int
name|S_IFSOCK
init|=
literal|0140000
decl_stmt|;
comment|/* socket */
DECL|field|S_IFWHT
specifier|public
specifier|static
specifier|final
name|int
name|S_IFWHT
init|=
literal|0160000
decl_stmt|;
comment|/* whiteout */
DECL|field|S_ISUID
specifier|public
specifier|static
specifier|final
name|int
name|S_ISUID
init|=
literal|0004000
decl_stmt|;
comment|/* set user id on execution */
DECL|field|S_ISGID
specifier|public
specifier|static
specifier|final
name|int
name|S_ISGID
init|=
literal|0002000
decl_stmt|;
comment|/* set group id on execution */
DECL|field|S_ISVTX
specifier|public
specifier|static
specifier|final
name|int
name|S_ISVTX
init|=
literal|0001000
decl_stmt|;
comment|/* save swapped text even after use */
DECL|field|S_IRUSR
specifier|public
specifier|static
specifier|final
name|int
name|S_IRUSR
init|=
literal|0000400
decl_stmt|;
comment|/* read permission, owner */
DECL|field|S_IWUSR
specifier|public
specifier|static
specifier|final
name|int
name|S_IWUSR
init|=
literal|0000200
decl_stmt|;
comment|/* write permission, owner */
DECL|field|S_IXUSR
specifier|public
specifier|static
specifier|final
name|int
name|S_IXUSR
init|=
literal|0000100
decl_stmt|;
comment|/* execute/search permission, owner */
DECL|method|Stat (String owner, String group, int mode)
name|Stat
parameter_list|(
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Stat(owner='"
operator|+
name|owner
operator|+
literal|"', group='"
operator|+
name|group
operator|+
literal|"'"
operator|+
literal|", mode="
operator|+
name|mode
operator|+
literal|")"
return|;
block|}
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
DECL|method|getGroup ()
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
DECL|method|getMode ()
specifier|public
name|int
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
block|}
block|}
end_class

end_unit

