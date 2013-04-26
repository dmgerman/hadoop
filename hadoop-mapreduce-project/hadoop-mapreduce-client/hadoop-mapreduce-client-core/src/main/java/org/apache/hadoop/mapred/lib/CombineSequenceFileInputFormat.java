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
name|InputSplit
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
name|RecordReader
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
name|mapred
operator|.
name|SequenceFileInputFormat
import|;
end_import

begin_comment
comment|/**  * Input format that is a<code>CombineFileInputFormat</code>-equivalent for  *<code>SequenceFileInputFormat</code>.  *  * @see CombineFileInputFormat  */
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
DECL|class|CombineSequenceFileInputFormat
specifier|public
class|class
name|CombineSequenceFileInputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|CombineFileInputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|getRecordReader (InputSplit split, JobConf conf, Reporter reporter)
specifier|public
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CombineFileRecordReader
argument_list|(
name|conf
argument_list|,
operator|(
name|CombineFileSplit
operator|)
name|split
argument_list|,
name|reporter
argument_list|,
name|SequenceFileRecordReaderWrapper
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * A record reader that may be passed to<code>CombineFileRecordReader</code>    * so that it can be used in a<code>CombineFileInputFormat</code>-equivalent    * for<code>SequenceFileInputFormat</code>.    *    * @see CombineFileRecordReader    * @see CombineFileInputFormat    * @see SequenceFileInputFormat    */
DECL|class|SequenceFileRecordReaderWrapper
specifier|private
specifier|static
class|class
name|SequenceFileRecordReaderWrapper
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|CombineFileRecordReaderWrapper
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|// this constructor signature is required by CombineFileRecordReader
DECL|method|SequenceFileRecordReaderWrapper (CombineFileSplit split, Configuration conf, Reporter reporter, Integer idx)
specifier|public
name|SequenceFileRecordReaderWrapper
parameter_list|(
name|CombineFileSplit
name|split
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|Reporter
name|reporter
parameter_list|,
name|Integer
name|idx
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|SequenceFileInputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
argument_list|,
name|split
argument_list|,
name|conf
argument_list|,
name|reporter
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

