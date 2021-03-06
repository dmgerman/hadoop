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
name|InputStream
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
name|Comparator
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
name|AtomicInteger
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MapOutput
specifier|public
specifier|abstract
class|class
name|MapOutput
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
DECL|field|ID
specifier|private
specifier|static
name|AtomicInteger
name|ID
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|mapId
specifier|private
specifier|final
name|TaskAttemptID
name|mapId
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|long
name|size
decl_stmt|;
DECL|field|primaryMapOutput
specifier|private
specifier|final
name|boolean
name|primaryMapOutput
decl_stmt|;
DECL|method|MapOutput (TaskAttemptID mapId, long size, boolean primaryMapOutput)
specifier|public
name|MapOutput
parameter_list|(
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
name|this
operator|.
name|id
operator|=
name|ID
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|this
operator|.
name|mapId
operator|=
name|mapId
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|primaryMapOutput
operator|=
name|primaryMapOutput
expr_stmt|;
block|}
DECL|method|isPrimaryMapOutput ()
specifier|public
name|boolean
name|isPrimaryMapOutput
parameter_list|()
block|{
return|return
name|primaryMapOutput
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|MapOutput
condition|)
block|{
return|return
name|id
operator|==
operator|(
operator|(
name|MapOutput
operator|)
name|obj
operator|)
operator|.
name|id
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getMapId ()
specifier|public
name|TaskAttemptID
name|getMapId
parameter_list|()
block|{
return|return
name|mapId
return|;
block|}
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|shuffle (MapHost host, InputStream input, long compressedLength, long decompressedLength, ShuffleClientMetrics metrics, Reporter reporter)
specifier|public
specifier|abstract
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
function_decl|;
DECL|method|commit ()
specifier|public
specifier|abstract
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|abort ()
specifier|public
specifier|abstract
name|void
name|abort
parameter_list|()
function_decl|;
DECL|method|getDescription ()
specifier|public
specifier|abstract
name|String
name|getDescription
parameter_list|()
function_decl|;
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MapOutput("
operator|+
name|mapId
operator|+
literal|", "
operator|+
name|getDescription
argument_list|()
operator|+
literal|")"
return|;
block|}
DECL|class|MapOutputComparator
specifier|public
specifier|static
class|class
name|MapOutputComparator
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|Comparator
argument_list|<
name|MapOutput
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
block|{
DECL|method|compare (MapOutput<K, V> o1, MapOutput<K, V> o2)
specifier|public
name|int
name|compare
parameter_list|(
name|MapOutput
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|o1
parameter_list|,
name|MapOutput
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|id
operator|==
name|o2
operator|.
name|id
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|o1
operator|.
name|size
operator|<
name|o2
operator|.
name|size
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|o1
operator|.
name|size
operator|>
name|o2
operator|.
name|size
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|o1
operator|.
name|id
operator|<
name|o2
operator|.
name|id
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

