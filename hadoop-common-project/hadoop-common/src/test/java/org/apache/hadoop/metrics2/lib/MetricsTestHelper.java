begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * A helper class that can provide test cases access to package-private  * methods.  */
end_comment

begin_class
DECL|class|MetricsTestHelper
specifier|public
specifier|final
class|class
name|MetricsTestHelper
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MetricsTestHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|MetricsTestHelper ()
specifier|private
name|MetricsTestHelper
parameter_list|()
block|{
comment|//not called
block|}
comment|/**    * Replace the rolling averages windows for a    * {@link MutableRollingAverages} metric.    *    */
DECL|method|replaceRollingAveragesScheduler ( MutableRollingAverages mutableRollingAverages, int numWindows, long interval, TimeUnit timeUnit)
specifier|public
specifier|static
name|void
name|replaceRollingAveragesScheduler
parameter_list|(
name|MutableRollingAverages
name|mutableRollingAverages
parameter_list|,
name|int
name|numWindows
parameter_list|,
name|long
name|interval
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
name|mutableRollingAverages
operator|.
name|replaceScheduledTask
argument_list|(
name|numWindows
argument_list|,
name|interval
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

