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
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/** Default {@link MapRunnable} implementation.*/
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
DECL|class|MapRunner
specifier|public
class|class
name|MapRunner
parameter_list|<
name|K1
parameter_list|,
name|V1
parameter_list|,
name|K2
parameter_list|,
name|V2
parameter_list|>
implements|implements
name|MapRunnable
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
block|{
DECL|field|mapper
specifier|private
name|Mapper
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
name|mapper
decl_stmt|;
DECL|field|incrProcCount
specifier|private
name|boolean
name|incrProcCount
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|this
operator|.
name|mapper
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|job
operator|.
name|getMapperClass
argument_list|()
argument_list|,
name|job
argument_list|)
expr_stmt|;
comment|//increment processed counter only if skipping feature is enabled
name|this
operator|.
name|incrProcCount
operator|=
name|SkipBadRecords
operator|.
name|getMapperMaxSkipRecords
argument_list|(
name|job
argument_list|)
operator|>
literal|0
operator|&&
name|SkipBadRecords
operator|.
name|getAutoIncrMapperProcCount
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
DECL|method|run (RecordReader<K1, V1> input, OutputCollector<K2, V2> output, Reporter reporter)
specifier|public
name|void
name|run
parameter_list|(
name|RecordReader
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
name|input
parameter_list|,
name|OutputCollector
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// allocate key& value instances that are re-used for all entries
name|K1
name|key
init|=
name|input
operator|.
name|createKey
argument_list|()
decl_stmt|;
name|V1
name|value
init|=
name|input
operator|.
name|createValue
argument_list|()
decl_stmt|;
while|while
condition|(
name|input
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
comment|// map pair to output
name|mapper
operator|.
name|map
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|output
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
if|if
condition|(
name|incrProcCount
condition|)
block|{
name|reporter
operator|.
name|incrCounter
argument_list|(
name|SkipBadRecords
operator|.
name|COUNTER_GROUP
argument_list|,
name|SkipBadRecords
operator|.
name|COUNTER_MAP_PROCESSED_RECORDS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|mapper
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getMapper ()
specifier|protected
name|Mapper
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
name|getMapper
parameter_list|()
block|{
return|return
name|mapper
return|;
block|}
block|}
end_class

end_unit

