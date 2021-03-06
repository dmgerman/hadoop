begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
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
name|mapred
operator|.
name|JobConf
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
name|RecordWriter
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
name|TextOutputFormat
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

begin_comment
comment|/**  * This class extends the MultipleOutputFormat, allowing to write the output  * data to different output files in Text output format.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|MultipleTextOutputFormat
specifier|public
class|class
name|MultipleTextOutputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|MultipleOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|theTextOutputFormat
specifier|private
name|TextOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|theTextOutputFormat
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|getBaseRecordWriter (FileSystem fs, JobConf job, String name, Progressable arg3)
specifier|protected
name|RecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getBaseRecordWriter
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|String
name|name
parameter_list|,
name|Progressable
name|arg3
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|theTextOutputFormat
operator|==
literal|null
condition|)
block|{
name|theTextOutputFormat
operator|=
operator|new
name|TextOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|theTextOutputFormat
operator|.
name|getRecordWriter
argument_list|(
name|fs
argument_list|,
name|job
argument_list|,
name|name
argument_list|,
name|arg3
argument_list|)
return|;
block|}
block|}
end_class

end_unit

