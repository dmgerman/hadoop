begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or  * implied. See the License for the specific language governing  * permissions and limitations under the License.  *  *   * Implements the Hadoop FSInputStream interfaces to allow applications to read  * files in Kosmos File System (KFS).  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.kfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|kfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|FSInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kosmix
operator|.
name|kosmosfs
operator|.
name|access
operator|.
name|KfsAccess
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kosmix
operator|.
name|kosmosfs
operator|.
name|access
operator|.
name|KfsInputChannel
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|KFSInputStream
class|class
name|KFSInputStream
extends|extends
name|FSInputStream
block|{
DECL|field|kfsChannel
specifier|private
name|KfsInputChannel
name|kfsChannel
decl_stmt|;
DECL|field|statistics
specifier|private
name|FileSystem
operator|.
name|Statistics
name|statistics
decl_stmt|;
DECL|field|fsize
specifier|private
name|long
name|fsize
decl_stmt|;
annotation|@
name|Deprecated
DECL|method|KFSInputStream (KfsAccess kfsAccess, String path)
specifier|public
name|KFSInputStream
parameter_list|(
name|KfsAccess
name|kfsAccess
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
argument_list|(
name|kfsAccess
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|KFSInputStream (KfsAccess kfsAccess, String path, FileSystem.Statistics stats)
specifier|public
name|KFSInputStream
parameter_list|(
name|KfsAccess
name|kfsAccess
parameter_list|,
name|String
name|path
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|stats
parameter_list|)
block|{
name|this
operator|.
name|statistics
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|kfsChannel
operator|=
name|kfsAccess
operator|.
name|kfs_open
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|kfsChannel
operator|!=
literal|null
condition|)
name|this
operator|.
name|fsize
operator|=
name|kfsAccess
operator|.
name|kfs_filesize
argument_list|(
name|path
argument_list|)
expr_stmt|;
else|else
name|this
operator|.
name|fsize
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|kfsChannel
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File closed"
argument_list|)
throw|;
block|}
return|return
name|kfsChannel
operator|.
name|tell
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
specifier|synchronized
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|kfsChannel
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File closed"
argument_list|)
throw|;
block|}
return|return
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|fsize
operator|-
name|getPos
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seek (long targetPos)
specifier|public
specifier|synchronized
name|void
name|seek
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|kfsChannel
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File closed"
argument_list|)
throw|;
block|}
name|kfsChannel
operator|.
name|seek
argument_list|(
name|targetPos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
specifier|synchronized
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|kfsChannel
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File closed"
argument_list|)
throw|;
block|}
name|byte
name|b
index|[]
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|int
name|res
init|=
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|statistics
operator|!=
literal|null
condition|)
block|{
name|statistics
operator|.
name|incrementBytesRead
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|b
index|[
literal|0
index|]
operator|&
literal|0xff
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte b[], int off, int len)
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|kfsChannel
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File closed"
argument_list|)
throw|;
block|}
name|int
name|res
decl_stmt|;
name|res
operator|=
name|kfsChannel
operator|.
name|read
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
comment|// Use -1 to signify EOF
if|if
condition|(
name|res
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|statistics
operator|!=
literal|null
condition|)
block|{
name|statistics
operator|.
name|incrementBytesRead
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|kfsChannel
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|kfsChannel
operator|.
name|close
argument_list|()
expr_stmt|;
name|kfsChannel
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|markSupported ()
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|mark (int readLimit)
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readLimit
parameter_list|)
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mark not supported"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

