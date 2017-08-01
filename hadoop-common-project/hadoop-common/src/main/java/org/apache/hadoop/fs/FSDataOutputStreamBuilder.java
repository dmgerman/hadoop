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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|HadoopIllegalArgumentException
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
name|Options
operator|.
name|ChecksumOpt
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
name|Progressable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|EnumSet
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
name|IO_FILE_BUFFER_SIZE_DEFAULT
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
name|IO_FILE_BUFFER_SIZE_KEY
import|;
end_import

begin_comment
comment|/**  * Builder for {@link FSDataOutputStream} and its subclasses.  *  * It is used to create {@link FSDataOutputStream} when creating a new file or  * appending an existing file on {@link FileSystem}.  *  * By default, it does not create parent directory that do not exist.  * {@link FileSystem#createNonRecursive(Path, boolean, int, short, long,  * Progressable)}.  *  * To create missing parent directory, use {@link #recursive()}.  */
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
DECL|class|FSDataOutputStreamBuilder
specifier|public
specifier|abstract
class|class
name|FSDataOutputStreamBuilder
parameter_list|<
name|S
extends|extends
name|FSDataOutputStream
parameter_list|,
name|B
extends|extends
name|FSDataOutputStreamBuilder
parameter_list|<
name|S
parameter_list|,
name|B
parameter_list|>
parameter_list|>
block|{
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|permission
specifier|private
name|FsPermission
name|permission
init|=
literal|null
decl_stmt|;
DECL|field|bufferSize
specifier|private
name|int
name|bufferSize
decl_stmt|;
DECL|field|replication
specifier|private
name|short
name|replication
decl_stmt|;
DECL|field|blockSize
specifier|private
name|long
name|blockSize
decl_stmt|;
comment|/** set to true to create missing directory. */
DECL|field|recursive
specifier|private
name|boolean
name|recursive
init|=
literal|false
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|CreateFlag
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|progress
specifier|private
name|Progressable
name|progress
init|=
literal|null
decl_stmt|;
DECL|field|checksumOpt
specifier|private
name|ChecksumOpt
name|checksumOpt
init|=
literal|null
decl_stmt|;
comment|/**    * Return the concrete implementation of the builder instance.    */
DECL|method|getThisBuilder ()
specifier|protected
specifier|abstract
name|B
name|getThisBuilder
parameter_list|()
function_decl|;
comment|/**    * Constructor.    */
DECL|method|FSDataOutputStreamBuilder (@onnull FileSystem fileSystem, @Nonnull Path p)
specifier|protected
name|FSDataOutputStreamBuilder
parameter_list|(
annotation|@
name|Nonnull
name|FileSystem
name|fileSystem
parameter_list|,
annotation|@
name|Nonnull
name|Path
name|p
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|fileSystem
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|fs
operator|=
name|fileSystem
expr_stmt|;
name|path
operator|=
name|p
expr_stmt|;
name|bufferSize
operator|=
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|replication
operator|=
name|fs
operator|.
name|getDefaultReplication
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|blockSize
operator|=
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
DECL|method|getFS ()
specifier|protected
name|FileSystem
name|getFS
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
DECL|method|getPath ()
specifier|protected
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getPermission ()
specifier|protected
name|FsPermission
name|getPermission
parameter_list|()
block|{
if|if
condition|(
name|permission
operator|==
literal|null
condition|)
block|{
name|permission
operator|=
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
expr_stmt|;
block|}
return|return
name|permission
return|;
block|}
comment|/**    * Set permission for the file.    */
DECL|method|permission (@onnull final FsPermission perm)
specifier|public
name|B
name|permission
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|FsPermission
name|perm
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|perm
argument_list|)
expr_stmt|;
name|permission
operator|=
name|perm
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
DECL|method|getBufferSize ()
specifier|protected
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|bufferSize
return|;
block|}
comment|/**    * Set the size of the buffer to be used.    */
DECL|method|bufferSize (int bufSize)
specifier|public
name|B
name|bufferSize
parameter_list|(
name|int
name|bufSize
parameter_list|)
block|{
name|bufferSize
operator|=
name|bufSize
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
DECL|method|getReplication ()
specifier|protected
name|short
name|getReplication
parameter_list|()
block|{
return|return
name|replication
return|;
block|}
comment|/**    * Set replication factor.    */
DECL|method|replication (short replica)
specifier|public
name|B
name|replication
parameter_list|(
name|short
name|replica
parameter_list|)
block|{
name|replication
operator|=
name|replica
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
DECL|method|getBlockSize ()
specifier|protected
name|long
name|getBlockSize
parameter_list|()
block|{
return|return
name|blockSize
return|;
block|}
comment|/**    * Set block size.    */
DECL|method|blockSize (long blkSize)
specifier|public
name|B
name|blockSize
parameter_list|(
name|long
name|blkSize
parameter_list|)
block|{
name|blockSize
operator|=
name|blkSize
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
comment|/**    * Return true to create the parent directories if they do not exist.    */
DECL|method|isRecursive ()
specifier|protected
name|boolean
name|isRecursive
parameter_list|()
block|{
return|return
name|recursive
return|;
block|}
comment|/**    * Create the parent directory if they do not exist.    */
DECL|method|recursive ()
specifier|public
name|B
name|recursive
parameter_list|()
block|{
name|recursive
operator|=
literal|true
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
DECL|method|getProgress ()
specifier|protected
name|Progressable
name|getProgress
parameter_list|()
block|{
return|return
name|progress
return|;
block|}
comment|/**    * Set the facility of reporting progress.    */
DECL|method|progress (@onnull final Progressable prog)
specifier|public
name|B
name|progress
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Progressable
name|prog
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|prog
argument_list|)
expr_stmt|;
name|progress
operator|=
name|prog
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
DECL|method|getFlags ()
specifier|protected
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
comment|/**    * Create an FSDataOutputStream at the specified path.    */
DECL|method|create ()
specifier|public
name|B
name|create
parameter_list|()
block|{
name|flags
operator|.
name|add
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
comment|/**    * Set to true to overwrite the existing file.    * Set it to false, an exception will be thrown when calling {@link #build()}    * if the file exists.    */
DECL|method|overwrite (boolean overwrite)
specifier|public
name|B
name|overwrite
parameter_list|(
name|boolean
name|overwrite
parameter_list|)
block|{
if|if
condition|(
name|overwrite
condition|)
block|{
name|flags
operator|.
name|add
argument_list|(
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|flags
operator|.
name|remove
argument_list|(
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
block|}
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
comment|/**    * Append to an existing file (optional operation).    */
DECL|method|append ()
specifier|public
name|B
name|append
parameter_list|()
block|{
name|flags
operator|.
name|add
argument_list|(
name|CreateFlag
operator|.
name|APPEND
argument_list|)
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
DECL|method|getChecksumOpt ()
specifier|protected
name|ChecksumOpt
name|getChecksumOpt
parameter_list|()
block|{
return|return
name|checksumOpt
return|;
block|}
comment|/**    * Set checksum opt.    */
DECL|method|checksumOpt (@onnull final ChecksumOpt chksumOpt)
specifier|public
name|B
name|checksumOpt
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|ChecksumOpt
name|chksumOpt
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|chksumOpt
argument_list|)
expr_stmt|;
name|checksumOpt
operator|=
name|chksumOpt
expr_stmt|;
return|return
name|getThisBuilder
argument_list|()
return|;
block|}
comment|/**    * Create the FSDataOutputStream to write on the file system.    *    * @throws HadoopIllegalArgumentException if the parameters are not valid.    * @throws IOException on errors when file system creates or appends the file.    */
DECL|method|build ()
specifier|public
specifier|abstract
name|S
name|build
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

