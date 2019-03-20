begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.workloadgenerator.audit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
operator|.
name|audit
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
name|util
operator|.
name|function
operator|.
name|Function
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
name|io
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * This interface represents a pluggable command parser. It will accept in one  * line of {@link Text} input at a time and return an {@link AuditReplayCommand}  * which represents the input text. Each input line should produce exactly one  * command.  */
end_comment

begin_interface
DECL|interface|AuditCommandParser
specifier|public
interface|interface
name|AuditCommandParser
block|{
comment|/**    * Initialize this parser with the given configuration. Guaranteed to be    * called prior to any calls to {@link #parse(Text, Function)}.    *    * @param conf The Configuration to be used to set up this parser.    */
DECL|method|initialize (Configuration conf)
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Convert a line of input into an {@link AuditReplayCommand}. Since    * {@link AuditReplayCommand}s store absolute timestamps, relativeToAbsolute    * can be used to convert relative timestamps (i.e., milliseconds elapsed    * between the start of the audit log and this command) into absolute    * timestamps.    *    * @param inputLine Single input line to convert.    * @param relativeToAbsolute Function converting relative timestamps    *                           (in milliseconds) to absolute timestamps    *                           (in milliseconds).    * @return A command representing the input line.    */
DECL|method|parse (Text inputLine, Function<Long, Long> relativeToAbsolute)
name|AuditReplayCommand
name|parse
parameter_list|(
name|Text
name|inputLine
parameter_list|,
name|Function
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|relativeToAbsolute
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

