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

begin_comment
comment|/**  * Expert: Generic interface for {@link Mapper}s.  *   *<p>Custom implementations of<code>MapRunnable</code> can exert greater   * control on map processing e.g. multi-threaded, asynchronous mappers etc.</p>  *   * @see Mapper  * @deprecated Use {@link org.apache.hadoop.mapreduce.Mapper} instead.  */
end_comment

begin_interface
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|MapRunnable
specifier|public
interface|interface
name|MapRunnable
parameter_list|<
name|K1
parameter_list|,
name|V1
parameter_list|,
name|K2
parameter_list|,
name|V2
parameter_list|>
extends|extends
name|JobConfigurable
block|{
comment|/**     * Start mapping input<tt>&lt;key, value&gt;</tt> pairs.    *      *<p>Mapping of input records to output records is complete when this method     * returns.</p>    *     * @param input the {@link RecordReader} to read the input records.    * @param output the {@link OutputCollector} to collect the outputrecords.    * @param reporter {@link Reporter} to report progress, status-updates etc.    * @throws IOException    */
DECL|method|run (RecordReader<K1, V1> input, OutputCollector<K2, V2> output, Reporter reporter)
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
function_decl|;
block|}
end_interface

end_unit

