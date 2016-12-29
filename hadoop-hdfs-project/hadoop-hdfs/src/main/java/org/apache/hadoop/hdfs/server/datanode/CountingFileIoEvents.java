begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonProcessingException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|FileIoProvider
operator|.
name|OPERATION
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * {@link FileIoEvents} that simply counts the number of operations.  * Not meant to be used outside of testing.  */
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
DECL|class|CountingFileIoEvents
specifier|public
class|class
name|CountingFileIoEvents
extends|extends
name|FileIoEvents
block|{
DECL|field|counts
specifier|private
specifier|final
name|Map
argument_list|<
name|OPERATION
argument_list|,
name|Counts
argument_list|>
name|counts
decl_stmt|;
DECL|class|Counts
specifier|private
specifier|static
class|class
name|Counts
block|{
DECL|field|successes
specifier|private
specifier|final
name|AtomicLong
name|successes
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|failures
specifier|private
specifier|final
name|AtomicLong
name|failures
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"Successes"
argument_list|)
DECL|method|getSuccesses ()
specifier|public
name|long
name|getSuccesses
parameter_list|()
block|{
return|return
name|successes
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"Failures"
argument_list|)
DECL|method|getFailures ()
specifier|public
name|long
name|getFailures
parameter_list|()
block|{
return|return
name|failures
operator|.
name|get
argument_list|()
return|;
block|}
block|}
DECL|method|CountingFileIoEvents ()
specifier|public
name|CountingFileIoEvents
parameter_list|()
block|{
name|counts
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|OPERATION
name|op
range|:
name|OPERATION
operator|.
name|values
argument_list|()
control|)
block|{
name|counts
operator|.
name|put
argument_list|(
name|op
argument_list|,
operator|new
name|Counts
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|beforeMetadataOp ( @ullable FsVolumeSpi volume, OPERATION op)
specifier|public
name|long
name|beforeMetadataOp
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|afterMetadataOp ( @ullable FsVolumeSpi volume, OPERATION op, long begin)
specifier|public
name|void
name|afterMetadataOp
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|,
name|long
name|begin
parameter_list|)
block|{
name|counts
operator|.
name|get
argument_list|(
name|op
argument_list|)
operator|.
name|successes
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeFileIo ( @ullable FsVolumeSpi volume, OPERATION op, long len)
specifier|public
name|long
name|beforeFileIo
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|,
name|long
name|len
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|afterFileIo ( @ullable FsVolumeSpi volume, OPERATION op, long begin, long len)
specifier|public
name|void
name|afterFileIo
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|,
name|long
name|begin
parameter_list|,
name|long
name|len
parameter_list|)
block|{
name|counts
operator|.
name|get
argument_list|(
name|op
argument_list|)
operator|.
name|successes
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure ( @ullable FsVolumeSpi volume, OPERATION op, Exception e, long begin)
specifier|public
name|void
name|onFailure
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|,
name|Exception
name|e
parameter_list|,
name|long
name|begin
parameter_list|)
block|{
name|counts
operator|.
name|get
argument_list|(
name|op
argument_list|)
operator|.
name|failures
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStatistics ()
specifier|public
name|String
name|getStatistics
parameter_list|()
block|{
name|ObjectMapper
name|objectMapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|objectMapper
operator|.
name|writeValueAsString
argument_list|(
name|counts
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|JsonProcessingException
name|e
parameter_list|)
block|{
comment|// Failed to serialize. Don't log the exception call stack.
name|FileIoProvider
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to serialize statistics"
operator|+
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

