begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.translator.impl
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
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|api
operator|.
name|RecurrenceId
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
name|api
operator|.
name|ResourceSkyline
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
name|config
operator|.
name|ResourceEstimatorConfiguration
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
name|config
operator|.
name|ResourceEstimatorUtil
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
name|translator
operator|.
name|api
operator|.
name|JobMetaData
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
name|translator
operator|.
name|api
operator|.
name|LogParser
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
name|translator
operator|.
name|api
operator|.
name|SingleLineParser
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
name|translator
operator|.
name|exceptions
operator|.
name|DataFieldNotFoundException
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
name|translator
operator|.
name|validator
operator|.
name|ParserValidator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Base class to implement {@link LogParser}. It wraps a  * {@link SingleLineParser} from the {@link Configuration} to parse a log  * dir/file.  */
end_comment

begin_class
DECL|class|BaseLogParser
specifier|public
class|class
name|BaseLogParser
implements|implements
name|LogParser
block|{
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BaseLogParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|INPUT_VALIDATOR
specifier|private
specifier|static
specifier|final
name|ParserValidator
name|INPUT_VALIDATOR
init|=
operator|new
name|ParserValidator
argument_list|()
decl_stmt|;
DECL|field|singleLineParser
specifier|private
name|SingleLineParser
name|singleLineParser
decl_stmt|;
DECL|field|historySkylineStore
specifier|private
name|HistorySkylineStore
name|historySkylineStore
decl_stmt|;
DECL|method|init (Configuration config, HistorySkylineStore skylineStore)
annotation|@
name|Override
specifier|public
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
block|{
name|singleLineParser
operator|=
name|ResourceEstimatorUtil
operator|.
name|createProviderInstance
argument_list|(
name|config
argument_list|,
name|ResourceEstimatorConfiguration
operator|.
name|TRANSLATOR_LINE_PARSER
argument_list|,
name|ResourceEstimatorConfiguration
operator|.
name|DEFAULT_TRANSLATOR_LINE_PARSER
argument_list|,
name|SingleLineParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|historySkylineStore
operator|=
name|skylineStore
expr_stmt|;
block|}
comment|/**    * Add job's {@link ResourceSkyline}s to the {@link HistorySkylineStore}.    *    * @param skylineRecords the {@link Map} which records the completed recurring    *                       pipeline's {@link ResourceSkyline}s.    * @throws SkylineStoreException if it failes to addHistory job's    *     {@link ResourceSkyline}s to the {@link HistorySkylineStore}.    */
DECL|method|addToSkylineStore ( final Map<RecurrenceId, List<ResourceSkyline>> skylineRecords)
specifier|private
name|void
name|addToSkylineStore
parameter_list|(
specifier|final
name|Map
argument_list|<
name|RecurrenceId
argument_list|,
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|>
name|skylineRecords
parameter_list|)
throws|throws
name|SkylineStoreException
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|RecurrenceId
argument_list|,
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|>
name|entry
range|:
name|skylineRecords
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|historySkylineStore
operator|.
name|addHistory
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parseLine (final String logLine, final Map<String, JobMetaData> jobMetas, final Map<RecurrenceId, List<ResourceSkyline>> skylineRecords)
specifier|public
name|void
name|parseLine
parameter_list|(
specifier|final
name|String
name|logLine
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|JobMetaData
argument_list|>
name|jobMetas
parameter_list|,
specifier|final
name|Map
argument_list|<
name|RecurrenceId
argument_list|,
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|>
name|skylineRecords
parameter_list|)
throws|throws
name|DataFieldNotFoundException
throws|,
name|ParseException
block|{
name|singleLineParser
operator|.
name|parseLine
argument_list|(
name|logLine
argument_list|,
name|jobMetas
argument_list|,
name|skylineRecords
argument_list|)
expr_stmt|;
block|}
DECL|method|parseStream (final InputStream logs)
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|parseStream
parameter_list|(
specifier|final
name|InputStream
name|logs
parameter_list|)
throws|throws
name|SkylineStoreException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|INPUT_VALIDATOR
operator|.
name|validate
argument_list|(
name|logs
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|error
argument_list|(
literal|"Input validation fails, please specify with"
operator|+
literal|" valid input parameters."
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Map
argument_list|<
name|RecurrenceId
argument_list|,
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|>
name|skylineRecords
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|JobMetaData
argument_list|>
name|jobMetas
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|JobMetaData
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|BufferedReader
name|bf
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|logs
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|bf
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|parseLine
argument_list|(
name|line
argument_list|,
name|jobMetas
argument_list|,
name|skylineRecords
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataFieldNotFoundException
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Data field not found"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Date conversion error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|addToSkylineStore
argument_list|(
name|skylineRecords
argument_list|)
expr_stmt|;
block|}
comment|/**    * Release the resource used by the ParserUtil.    */
DECL|method|close ()
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|close
parameter_list|()
block|{
name|historySkylineStore
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

