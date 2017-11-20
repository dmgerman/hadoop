begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container.placement.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|JsonUtils
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
name|annotation
operator|.
name|JsonProperty
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
comment|/**  * This class represents the SCM container stat.  */
end_comment

begin_class
DECL|class|ContainerStat
specifier|public
class|class
name|ContainerStat
block|{
comment|/**    * The maximum container size.    */
annotation|@
name|JsonProperty
argument_list|(
literal|"Size"
argument_list|)
DECL|field|size
specifier|private
name|LongMetric
name|size
decl_stmt|;
comment|/**    * The number of bytes used by the container.    */
annotation|@
name|JsonProperty
argument_list|(
literal|"Used"
argument_list|)
DECL|field|used
specifier|private
name|LongMetric
name|used
decl_stmt|;
comment|/**    * The number of keys in the container.    */
annotation|@
name|JsonProperty
argument_list|(
literal|"KeyCount"
argument_list|)
DECL|field|keyCount
specifier|private
name|LongMetric
name|keyCount
decl_stmt|;
comment|/**    * The number of bytes read from the container.    */
annotation|@
name|JsonProperty
argument_list|(
literal|"ReadBytes"
argument_list|)
DECL|field|readBytes
specifier|private
name|LongMetric
name|readBytes
decl_stmt|;
comment|/**    * The number of bytes write into the container.    */
annotation|@
name|JsonProperty
argument_list|(
literal|"WriteBytes"
argument_list|)
DECL|field|writeBytes
specifier|private
name|LongMetric
name|writeBytes
decl_stmt|;
comment|/**    * The number of times the container is read.    */
annotation|@
name|JsonProperty
argument_list|(
literal|"ReadCount"
argument_list|)
DECL|field|readCount
specifier|private
name|LongMetric
name|readCount
decl_stmt|;
comment|/**    * The number of times the container is written into.    */
annotation|@
name|JsonProperty
argument_list|(
literal|"WriteCount"
argument_list|)
DECL|field|writeCount
specifier|private
name|LongMetric
name|writeCount
decl_stmt|;
DECL|method|ContainerStat ()
specifier|public
name|ContainerStat
parameter_list|()
block|{
name|this
argument_list|(
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|ContainerStat (long size, long used, long keyCount, long readBytes, long writeBytes, long readCount, long writeCount)
specifier|public
name|ContainerStat
parameter_list|(
name|long
name|size
parameter_list|,
name|long
name|used
parameter_list|,
name|long
name|keyCount
parameter_list|,
name|long
name|readBytes
parameter_list|,
name|long
name|writeBytes
parameter_list|,
name|long
name|readCount
parameter_list|,
name|long
name|writeCount
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|size
operator|>=
literal|0
argument_list|,
literal|"Container size cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|used
operator|>=
literal|0
argument_list|,
literal|"Used space cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|keyCount
operator|>=
literal|0
argument_list|,
literal|"Key count cannot be "
operator|+
literal|"negative"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|readBytes
operator|>=
literal|0
argument_list|,
literal|"Read bytes read cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|readBytes
operator|>=
literal|0
argument_list|,
literal|"Write bytes cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|readCount
operator|>=
literal|0
argument_list|,
literal|"Read count cannot be "
operator|+
literal|"negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|writeCount
operator|>=
literal|0
argument_list|,
literal|"Write count cannot be "
operator|+
literal|"negative"
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
operator|new
name|LongMetric
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|used
operator|=
operator|new
name|LongMetric
argument_list|(
name|used
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyCount
operator|=
operator|new
name|LongMetric
argument_list|(
name|keyCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|readBytes
operator|=
operator|new
name|LongMetric
argument_list|(
name|readBytes
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeBytes
operator|=
operator|new
name|LongMetric
argument_list|(
name|writeBytes
argument_list|)
expr_stmt|;
name|this
operator|.
name|readCount
operator|=
operator|new
name|LongMetric
argument_list|(
name|readCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeCount
operator|=
operator|new
name|LongMetric
argument_list|(
name|writeCount
argument_list|)
expr_stmt|;
block|}
DECL|method|getSize ()
specifier|public
name|LongMetric
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|getUsed ()
specifier|public
name|LongMetric
name|getUsed
parameter_list|()
block|{
return|return
name|used
return|;
block|}
DECL|method|getKeyCount ()
specifier|public
name|LongMetric
name|getKeyCount
parameter_list|()
block|{
return|return
name|keyCount
return|;
block|}
DECL|method|getReadBytes ()
specifier|public
name|LongMetric
name|getReadBytes
parameter_list|()
block|{
return|return
name|readBytes
return|;
block|}
DECL|method|getWriteBytes ()
specifier|public
name|LongMetric
name|getWriteBytes
parameter_list|()
block|{
return|return
name|writeBytes
return|;
block|}
DECL|method|getReadCount ()
specifier|public
name|LongMetric
name|getReadCount
parameter_list|()
block|{
return|return
name|readCount
return|;
block|}
DECL|method|getWriteCount ()
specifier|public
name|LongMetric
name|getWriteCount
parameter_list|()
block|{
return|return
name|writeCount
return|;
block|}
DECL|method|add (ContainerStat stat)
specifier|public
name|void
name|add
parameter_list|(
name|ContainerStat
name|stat
parameter_list|)
block|{
if|if
condition|(
name|stat
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|size
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getSize
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|used
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getUsed
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyCount
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getKeyCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|readBytes
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getReadBytes
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeBytes
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getWriteBytes
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|readCount
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getReadCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeCount
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getWriteCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|subtract (ContainerStat stat)
specifier|public
name|void
name|subtract
parameter_list|(
name|ContainerStat
name|stat
parameter_list|)
block|{
if|if
condition|(
name|stat
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|size
operator|.
name|subtract
argument_list|(
name|stat
operator|.
name|getSize
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|used
operator|.
name|subtract
argument_list|(
name|stat
operator|.
name|getUsed
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyCount
operator|.
name|subtract
argument_list|(
name|stat
operator|.
name|getKeyCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|readBytes
operator|.
name|subtract
argument_list|(
name|stat
operator|.
name|getReadBytes
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeBytes
operator|.
name|subtract
argument_list|(
name|stat
operator|.
name|getWriteBytes
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|readCount
operator|.
name|subtract
argument_list|(
name|stat
operator|.
name|getReadCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeCount
operator|.
name|subtract
argument_list|(
name|stat
operator|.
name|getWriteCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|toJsonString ()
specifier|public
name|String
name|toJsonString
parameter_list|()
block|{
try|try
block|{
return|return
name|JsonUtils
operator|.
name|toJsonString
argument_list|(
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

