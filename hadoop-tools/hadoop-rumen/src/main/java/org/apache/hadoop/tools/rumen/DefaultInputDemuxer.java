begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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

begin_comment
comment|/**  * {@link DefaultInputDemuxer} acts as a pass-through demuxer. It just opens  * each file and returns back the input stream. If the input is compressed, it  * would return a decompression stream.  */
end_comment

begin_class
DECL|class|DefaultInputDemuxer
specifier|public
class|class
name|DefaultInputDemuxer
implements|implements
name|InputDemuxer
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|input
name|InputStream
name|input
decl_stmt|;
annotation|@
name|Override
DECL|method|bindTo (Path path, Configuration conf)
specifier|public
name|void
name|bindTo
parameter_list|(
name|Path
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
comment|// re-binding before the previous one was consumed.
name|close
argument_list|()
expr_stmt|;
block|}
name|name
operator|=
name|path
operator|.
name|getName
argument_list|()
expr_stmt|;
name|input
operator|=
operator|new
name|PossiblyDecompressedInputStream
argument_list|(
name|path
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return;
block|}
annotation|@
name|Override
DECL|method|getNext ()
specifier|public
name|Pair
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
name|getNext
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|Pair
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
name|ret
init|=
operator|new
name|Pair
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
argument_list|(
name|name
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
name|input
operator|=
literal|null
expr_stmt|;
return|return
name|ret
return|;
block|}
return|return
literal|null
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
try|try
block|{
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
block|}
finally|finally
block|{
name|name
operator|=
literal|null
expr_stmt|;
name|input
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

