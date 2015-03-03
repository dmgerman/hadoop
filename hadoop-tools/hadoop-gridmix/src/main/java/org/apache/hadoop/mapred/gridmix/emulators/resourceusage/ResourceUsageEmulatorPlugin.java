begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix.emulators.resourceusage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
operator|.
name|emulators
operator|.
name|resourceusage
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
name|mapred
operator|.
name|gridmix
operator|.
name|Progressive
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
name|yarn
operator|.
name|util
operator|.
name|ResourceCalculatorPlugin
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
name|tools
operator|.
name|rumen
operator|.
name|ResourceUsageMetrics
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
comment|/**  *<p>Each resource to be emulated should have a corresponding implementation   * class that implements {@link ResourceUsageEmulatorPlugin}.</p>  *<br><br>  * {@link ResourceUsageEmulatorPlugin} will be configured using the   * {@link #initialize(Configuration, ResourceUsageMetrics,   *                    ResourceCalculatorPlugin, Progressive)} call.  * Every   * {@link ResourceUsageEmulatorPlugin} is also configured with a feedback module  * i.e a {@link ResourceCalculatorPlugin}, to monitor the current resource   * usage. {@link ResourceUsageMetrics} decides the final resource usage value to  * emulate. {@link Progressive} keeps track of the task's progress.  *   *<br><br>  *   * For configuring GridMix to load and and use a resource usage emulator,   * see {@link ResourceUsageMatcher}.   */
end_comment

begin_interface
DECL|interface|ResourceUsageEmulatorPlugin
specifier|public
interface|interface
name|ResourceUsageEmulatorPlugin
extends|extends
name|Progressive
block|{
comment|/**    * Initialize the plugin. This might involve    *   - initializing the variables    *   - calibrating the plugin    */
DECL|method|initialize (Configuration conf, ResourceUsageMetrics metrics, ResourceCalculatorPlugin monitor, Progressive progress)
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ResourceUsageMetrics
name|metrics
parameter_list|,
name|ResourceCalculatorPlugin
name|monitor
parameter_list|,
name|Progressive
name|progress
parameter_list|)
function_decl|;
comment|/**    * Emulate the resource usage to match the usage target. The plugin can use    * the given {@link ResourceCalculatorPlugin} to query for the current     * resource usage.    * @throws IOException    * @throws InterruptedException    */
DECL|method|emulate ()
name|void
name|emulate
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

