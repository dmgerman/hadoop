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
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|WritableByteChannel
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
name|io
operator|.
name|ReadaheadPool
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
name|ReadaheadPool
operator|.
name|ReadaheadRequest
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
name|nativeio
operator|.
name|NativeIO
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|DefaultFileRegion
import|;
end_import

begin_class
DECL|class|FadvisedFileRegion
specifier|public
class|class
name|FadvisedFileRegion
extends|extends
name|DefaultFileRegion
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
name|FadvisedFileRegion
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|manageOsCache
specifier|private
specifier|final
name|boolean
name|manageOsCache
decl_stmt|;
DECL|field|readaheadLength
specifier|private
specifier|final
name|int
name|readaheadLength
decl_stmt|;
DECL|field|readaheadPool
specifier|private
specifier|final
name|ReadaheadPool
name|readaheadPool
decl_stmt|;
DECL|field|fd
specifier|private
specifier|final
name|FileDescriptor
name|fd
decl_stmt|;
DECL|field|identifier
specifier|private
specifier|final
name|String
name|identifier
decl_stmt|;
DECL|field|readaheadRequest
specifier|private
name|ReadaheadRequest
name|readaheadRequest
decl_stmt|;
DECL|method|FadvisedFileRegion (RandomAccessFile file, long position, long count, boolean manageOsCache, int readaheadLength, ReadaheadPool readaheadPool, String identifier)
specifier|public
name|FadvisedFileRegion
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|,
name|long
name|position
parameter_list|,
name|long
name|count
parameter_list|,
name|boolean
name|manageOsCache
parameter_list|,
name|int
name|readaheadLength
parameter_list|,
name|ReadaheadPool
name|readaheadPool
parameter_list|,
name|String
name|identifier
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|file
operator|.
name|getChannel
argument_list|()
argument_list|,
name|position
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|this
operator|.
name|manageOsCache
operator|=
name|manageOsCache
expr_stmt|;
name|this
operator|.
name|readaheadLength
operator|=
name|readaheadLength
expr_stmt|;
name|this
operator|.
name|readaheadPool
operator|=
name|readaheadPool
expr_stmt|;
name|this
operator|.
name|fd
operator|=
name|file
operator|.
name|getFD
argument_list|()
expr_stmt|;
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transferTo (WritableByteChannel target, long position)
specifier|public
name|long
name|transferTo
parameter_list|(
name|WritableByteChannel
name|target
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|manageOsCache
operator|&&
name|readaheadPool
operator|!=
literal|null
condition|)
block|{
name|readaheadRequest
operator|=
name|readaheadPool
operator|.
name|readaheadStream
argument_list|(
name|identifier
argument_list|,
name|fd
argument_list|,
name|getPosition
argument_list|()
operator|+
name|position
argument_list|,
name|readaheadLength
argument_list|,
name|getPosition
argument_list|()
operator|+
name|getCount
argument_list|()
argument_list|,
name|readaheadRequest
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|transferTo
argument_list|(
name|target
argument_list|,
name|position
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|releaseExternalResources ()
specifier|public
name|void
name|releaseExternalResources
parameter_list|()
block|{
if|if
condition|(
name|readaheadRequest
operator|!=
literal|null
condition|)
block|{
name|readaheadRequest
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|manageOsCache
operator|&&
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|NativeIO
operator|.
name|POSIX
operator|.
name|posixFadviseIfPossible
argument_list|(
name|identifier
argument_list|,
name|fd
argument_list|,
name|getPosition
argument_list|()
argument_list|,
name|getCount
argument_list|()
argument_list|,
name|NativeIO
operator|.
name|POSIX
operator|.
name|POSIX_FADV_DONTNEED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to manage OS cache for "
operator|+
name|identifier
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|releaseExternalResources
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

