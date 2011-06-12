begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|fs
operator|.
name|Path
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
name|IOUtils
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
name|lib
operator|.
name|input
operator|.
name|CombineFileSplit
import|;
end_import

begin_comment
comment|/**  * Given a {@link org.apache.hadoop.mapreduce.lib.input.CombineFileSplit},  * circularly read through each input source.  */
end_comment

begin_class
DECL|class|FileQueue
class|class
name|FileQueue
extends|extends
name|InputStream
block|{
DECL|field|idx
specifier|private
name|int
name|idx
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|curlen
specifier|private
name|long
name|curlen
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|input
specifier|private
name|InputStream
name|input
decl_stmt|;
DECL|field|z
specifier|private
specifier|final
name|byte
index|[]
name|z
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
DECL|field|paths
specifier|private
specifier|final
name|Path
index|[]
name|paths
decl_stmt|;
DECL|field|lengths
specifier|private
specifier|final
name|long
index|[]
name|lengths
decl_stmt|;
DECL|field|startoffset
specifier|private
specifier|final
name|long
index|[]
name|startoffset
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
comment|/**    * @param split Description of input sources.    * @param conf Used to resolve FileSystem instances.    */
DECL|method|FileQueue (CombineFileSplit split, Configuration conf)
specifier|public
name|FileQueue
parameter_list|(
name|CombineFileSplit
name|split
parameter_list|,
name|Configuration
name|conf
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
name|paths
operator|=
name|split
operator|.
name|getPaths
argument_list|()
expr_stmt|;
name|startoffset
operator|=
name|split
operator|.
name|getStartOffsets
argument_list|()
expr_stmt|;
name|lengths
operator|=
name|split
operator|.
name|getLengths
argument_list|()
expr_stmt|;
name|nextSource
argument_list|()
expr_stmt|;
block|}
DECL|method|nextSource ()
specifier|protected
name|void
name|nextSource
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|0
operator|==
name|paths
operator|.
name|length
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|idx
operator|=
operator|(
name|idx
operator|+
literal|1
operator|)
operator|%
name|paths
operator|.
name|length
expr_stmt|;
name|curlen
operator|=
name|lengths
index|[
name|idx
index|]
expr_stmt|;
specifier|final
name|Path
name|file
init|=
name|paths
index|[
name|idx
index|]
decl_stmt|;
name|input
operator|=
name|CompressionEmulationUtil
operator|.
name|getPossiblyDecompressedInputStream
argument_list|(
name|file
argument_list|,
name|conf
argument_list|,
name|startoffset
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|tmp
init|=
name|read
argument_list|(
name|z
argument_list|)
decl_stmt|;
return|return
name|tmp
operator|==
operator|-
literal|1
condition|?
operator|-
literal|1
else|:
operator|(
literal|0xFF
operator|&
name|z
index|[
literal|0
index|]
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte[] b)
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte[] b, int off, int len)
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
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
name|int
name|kvread
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|kvread
operator|<
name|len
condition|)
block|{
if|if
condition|(
name|curlen
operator|<=
literal|0
condition|)
block|{
name|nextSource
argument_list|()
expr_stmt|;
continue|continue;
block|}
specifier|final
name|int
name|srcRead
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|len
operator|-
name|kvread
argument_list|,
name|curlen
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|input
argument_list|,
name|b
argument_list|,
name|kvread
argument_list|,
name|srcRead
argument_list|)
expr_stmt|;
name|curlen
operator|-=
name|srcRead
expr_stmt|;
name|kvread
operator|+=
name|srcRead
expr_stmt|;
block|}
return|return
name|kvread
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

