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
name|Mapper
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
name|OutputCollector
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
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * An {@link Mapper} that delegates behaviour of paths to multiple other  * mappers.  *   * @see MultipleInputs#addInputPath(JobConf, Path, Class, Class)  * @deprecated Use   * {@link org.apache.hadoop.mapreduce.lib.input.DelegatingMapper} instead  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DelegatingMapper
specifier|public
class|class
name|DelegatingMapper
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
block|{
DECL|field|conf
specifier|private
name|JobConf
name|conf
decl_stmt|;
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|map (K1 key, V1 value, OutputCollector<K2, V2> outputCollector, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|K1
name|key
parameter_list|,
name|V1
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|outputCollector
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|mapper
operator|==
literal|null
condition|)
block|{
comment|// Find the Mapper from the TaggedInputSplit.
name|TaggedInputSplit
name|inputSplit
init|=
operator|(
name|TaggedInputSplit
operator|)
name|reporter
operator|.
name|getInputSplit
argument_list|()
decl_stmt|;
name|mapper
operator|=
operator|(
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
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|inputSplit
operator|.
name|getMapperClass
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|mapper
operator|.
name|map
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|outputCollector
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
block|}
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|mapper
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

