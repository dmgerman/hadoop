begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task.reduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
operator|.
name|reduce
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
name|io
operator|.
name|InputStream
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
name|mapred
operator|.
name|IFileInputStream
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
name|mapred
operator|.
name|Reporter
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
name|mapreduce
operator|.
name|TaskAttemptID
import|;
end_import

begin_comment
comment|/**  * Common code for allowing MapOutput classes to handle streams.  *  * @param<K> key type for map output  * @param<V> value type for map output  */
end_comment

begin_class
DECL|class|IFileWrappedMapOutput
specifier|public
specifier|abstract
class|class
name|IFileWrappedMapOutput
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|MapOutput
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|merger
specifier|private
specifier|final
name|MergeManagerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|merger
decl_stmt|;
DECL|method|IFileWrappedMapOutput ( Configuration c, MergeManagerImpl<K, V> m, TaskAttemptID mapId, long size, boolean primaryMapOutput)
specifier|public
name|IFileWrappedMapOutput
parameter_list|(
name|Configuration
name|c
parameter_list|,
name|MergeManagerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|m
parameter_list|,
name|TaskAttemptID
name|mapId
parameter_list|,
name|long
name|size
parameter_list|,
name|boolean
name|primaryMapOutput
parameter_list|)
block|{
name|super
argument_list|(
name|mapId
argument_list|,
name|size
argument_list|,
name|primaryMapOutput
argument_list|)
expr_stmt|;
name|conf
operator|=
name|c
expr_stmt|;
name|merger
operator|=
name|m
expr_stmt|;
block|}
comment|/**    * @return the merger    */
DECL|method|getMerger ()
specifier|protected
name|MergeManagerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getMerger
parameter_list|()
block|{
return|return
name|merger
return|;
block|}
DECL|method|doShuffle ( MapHost host, IFileInputStream iFileInputStream, long compressedLength, long decompressedLength, ShuffleClientMetrics metrics, Reporter reporter)
specifier|protected
specifier|abstract
name|void
name|doShuffle
parameter_list|(
name|MapHost
name|host
parameter_list|,
name|IFileInputStream
name|iFileInputStream
parameter_list|,
name|long
name|compressedLength
parameter_list|,
name|long
name|decompressedLength
parameter_list|,
name|ShuffleClientMetrics
name|metrics
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|shuffle (MapHost host, InputStream input, long compressedLength, long decompressedLength, ShuffleClientMetrics metrics, Reporter reporter)
specifier|public
name|void
name|shuffle
parameter_list|(
name|MapHost
name|host
parameter_list|,
name|InputStream
name|input
parameter_list|,
name|long
name|compressedLength
parameter_list|,
name|long
name|decompressedLength
parameter_list|,
name|ShuffleClientMetrics
name|metrics
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|doShuffle
argument_list|(
name|host
argument_list|,
operator|new
name|IFileInputStream
argument_list|(
name|input
argument_list|,
name|compressedLength
argument_list|,
name|conf
argument_list|)
argument_list|,
name|compressedLength
argument_list|,
name|decompressedLength
argument_list|,
name|metrics
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

