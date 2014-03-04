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
name|FileInputStream
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
name|io
operator|.
name|FileDescriptor
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
name|lang
operator|.
name|SystemUtils
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

begin_comment
comment|/**  * A factory for creating shared file descriptors inside a given directory.  * Typically, the directory will be /dev/shm or /tmp.  *  * We will hand out file descriptors that correspond to unlinked files residing  * in that directory.  These file descriptors are suitable for sharing across  * multiple processes and are both readable and writable.  *  * Because we unlink the temporary files right after creating them, a JVM crash  * usually does not leave behind any temporary files in the directory.  However,  * it may happen that we crash right after creating the file and before  * unlinking it.  In the constructor, we attempt to clean up after any such  * remnants by trying to unlink any temporary files created by previous  * SharedFileDescriptorFactory instances that also used our prefix.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SharedFileDescriptorFactory
specifier|public
class|class
name|SharedFileDescriptorFactory
block|{
DECL|field|prefix
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|method|getLoadingFailureReason ()
specifier|public
specifier|static
name|String
name|getLoadingFailureReason
parameter_list|()
block|{
if|if
condition|(
operator|!
name|NativeIO
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
return|return
literal|"NativeIO is not available."
return|;
block|}
if|if
condition|(
operator|!
name|SystemUtils
operator|.
name|IS_OS_UNIX
condition|)
block|{
return|return
literal|"The OS is not UNIX."
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Create a SharedFileDescriptorFactory.    *    * @param prefix    Prefix to add to all file names we use.    * @param path      Path to use.    */
DECL|method|SharedFileDescriptorFactory (String prefix, String path)
specifier|public
name|SharedFileDescriptorFactory
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|getLoadingFailureReason
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|deleteStaleTemporaryFiles0
argument_list|(
name|prefix
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a shared file descriptor which will be both readable and writable.    *    * @param info           Information to include in the path of the     *                         generated descriptor.    * @param length         The starting file length.    *    * @return               The file descriptor, wrapped in a FileInputStream.    * @throws IOException   If there was an I/O or configuration error creating    *                         the descriptor.    */
DECL|method|createDescriptor (String info, int length)
specifier|public
name|FileInputStream
name|createDescriptor
parameter_list|(
name|String
name|info
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|createDescriptor0
argument_list|(
name|prefix
operator|+
name|info
argument_list|,
name|path
argument_list|,
name|length
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Delete temporary files in the directory, NOT following symlinks.    */
DECL|method|deleteStaleTemporaryFiles0 (String prefix, String path)
specifier|private
specifier|static
specifier|native
name|void
name|deleteStaleTemporaryFiles0
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a file with O_EXCL, and then resize it to the desired size.    */
DECL|method|createDescriptor0 (String prefix, String path, int length)
specifier|private
specifier|static
specifier|native
name|FileDescriptor
name|createDescriptor0
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

