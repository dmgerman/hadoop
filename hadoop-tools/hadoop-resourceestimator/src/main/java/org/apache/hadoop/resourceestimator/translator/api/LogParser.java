begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.translator.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|translator
operator|.
name|api
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
name|resourceestimator
operator|.
name|common
operator|.
name|exception
operator|.
name|ResourceEstimatorException
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|api
operator|.
name|HistorySkylineStore
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|exceptions
operator|.
name|SkylineStoreException
import|;
end_import

begin_comment
comment|/**  * LogParser iterates over a stream of logs, uses {@link SingleLineParser} to  * parse each line, and adds extracted {@code ResourceSkyline}s to the  * {@code SkylineStore}.  */
end_comment

begin_interface
DECL|interface|LogParser
specifier|public
interface|interface
name|LogParser
extends|extends
name|AutoCloseable
block|{
comment|/**    * Initializing the LogParser, including loading solver parameters from    * configuration file.    *    * @param config       {@link Configuration} for the LogParser.    * @param skylineStore the {@link HistorySkylineStore} which stores recurring    *                     pipeline's {@code    *                     ResourceSkyline}s.    * @throws ResourceEstimatorException if initialization of a    *     {@code SingleLineParser} fails.    */
DECL|method|init (Configuration config, HistorySkylineStore skylineStore)
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|HistorySkylineStore
name|skylineStore
parameter_list|)
throws|throws
name|ResourceEstimatorException
function_decl|;
comment|/**    * Parses each line in the log stream, and adds extracted    * {@code ResourceSkyline}s to the {@code    * SkylineStore}.    *    * @param logs the stream of input logs.    * @throws SkylineStoreException if it fails to addHistory extracted    *     {@code ResourceSkyline}s to the {@code SkylineStore}.    * @throws IOException if it fails to read from the {@link InputStream}.    */
DECL|method|parseStream (InputStream logs)
name|void
name|parseStream
parameter_list|(
name|InputStream
name|logs
parameter_list|)
throws|throws
name|SkylineStoreException
throws|,
name|IOException
function_decl|;
DECL|method|close ()
annotation|@
name|Override
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

