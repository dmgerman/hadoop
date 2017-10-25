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
name|translator
operator|.
name|exceptions
operator|.
name|DataFieldNotFoundException
import|;
end_import

begin_comment
comment|/**  * SingleLineParser parses one line in the log file, extracts the  * {@link ResourceSkyline}s and stores them.  */
end_comment

begin_interface
DECL|interface|SingleLineParser
specifier|public
interface|interface
name|SingleLineParser
block|{
comment|/**    * Parse one line in the log file, extract the {@link ResourceSkyline}s and    * store them.    *    * @param logLine        one line in the log file.    * @param jobMetas       the job metadata collected during parsing.    * @param skylineRecords the valid {@link ResourceSkyline}s extracted from the    *                       log.    * @throws DataFieldNotFoundException if certain data fields are not found in    *                                    the log.    * @throws ParseException if it fails to convert date string to    *     unix timestamp successfully.    */
DECL|method|parseLine (String logLine, Map<String, JobMetaData> jobMetas, Map<RecurrenceId, List<ResourceSkyline>> skylineRecords)
name|void
name|parseLine
parameter_list|(
name|String
name|logLine
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|JobMetaData
argument_list|>
name|jobMetas
parameter_list|,
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
function_decl|;
block|}
end_interface

end_unit

