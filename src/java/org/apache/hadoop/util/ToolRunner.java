begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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

begin_comment
comment|/**  * A utility to help run {@link Tool}s.  *   *<p><code>ToolRunner</code> can be used to run classes implementing   *<code>Tool</code> interface. It works in conjunction with   * {@link GenericOptionsParser} to parse the   *<a href="{@docRoot}/org/apache/hadoop/util/GenericOptionsParser.html#GenericOptions">  * generic hadoop command line arguments</a> and modifies the   *<code>Configuration</code> of the<code>Tool</code>. The   * application-specific options are passed along without being modified.  *</p>  *   * @see Tool  * @see GenericOptionsParser  */
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
DECL|class|ToolRunner
specifier|public
class|class
name|ToolRunner
block|{
comment|/**    * Runs the given<code>Tool</code> by {@link Tool#run(String[])}, after     * parsing with the given generic arguments. Uses the given     *<code>Configuration</code>, or builds one if null.    *     * Sets the<code>Tool</code>'s configuration with the possibly modified     * version of the<code>conf</code>.      *     * @param conf<code>Configuration</code> for the<code>Tool</code>.    * @param tool<code>Tool</code> to run.    * @param args command-line arguments to the tool.    * @return exit code of the {@link Tool#run(String[])} method.    */
DECL|method|run (Configuration conf, Tool tool, String[] args)
specifier|public
specifier|static
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Tool
name|tool
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
block|}
name|GenericOptionsParser
name|parser
init|=
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
decl_stmt|;
comment|//set the configuration back, so that Tool can configure itself
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//get the args w/o generic hadoop args
name|String
index|[]
name|toolArgs
init|=
name|parser
operator|.
name|getRemainingArgs
argument_list|()
decl_stmt|;
return|return
name|tool
operator|.
name|run
argument_list|(
name|toolArgs
argument_list|)
return|;
block|}
comment|/**    * Runs the<code>Tool</code> with its<code>Configuration</code>.    *     * Equivalent to<code>run(tool.getConf(), tool, args)</code>.    *     * @param tool<code>Tool</code> to run.    * @param args command-line arguments to the tool.    * @return exit code of the {@link Tool#run(String[])} method.    */
DECL|method|run (Tool tool, String[] args)
specifier|public
specifier|static
name|int
name|run
parameter_list|(
name|Tool
name|tool
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|run
argument_list|(
name|tool
operator|.
name|getConf
argument_list|()
argument_list|,
name|tool
argument_list|,
name|args
argument_list|)
return|;
block|}
comment|/**    * Prints generic command-line argurments and usage information.    *     *  @param out stream to write usage information to.    */
DECL|method|printGenericCommandUsage (PrintStream out)
specifier|public
specifier|static
name|void
name|printGenericCommandUsage
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|GenericOptionsParser
operator|.
name|printGenericCommandUsage
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

