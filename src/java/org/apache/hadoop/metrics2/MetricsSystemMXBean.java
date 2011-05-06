begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
package|;
end_package

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
comment|/**  * The JMX interface to the metrics system  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|MetricsSystemMXBean
specifier|public
interface|interface
name|MetricsSystemMXBean
block|{
comment|/**    * Start the metrics system    * @throws MetricsException    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
function_decl|;
comment|/**    * Stop the metrics system    * @throws MetricsException    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
function_decl|;
comment|/**    * Start metrics MBeans    * @throws MetricsException    */
DECL|method|startMetricsMBeans ()
specifier|public
name|void
name|startMetricsMBeans
parameter_list|()
function_decl|;
comment|/**    * Stop metrics MBeans.    * Note, it doesn't stop the metrics system control MBean,    * i.e this interface.    * @throws MetricsException    */
DECL|method|stopMetricsMBeans ()
specifier|public
name|void
name|stopMetricsMBeans
parameter_list|()
function_decl|;
comment|/**    * @return the current config    * Avoided getConfig, as it'll turn into a "Config" attribute,    * which doesn't support multiple line values in jconsole.    * @throws MetricsException    */
DECL|method|currentConfig ()
specifier|public
name|String
name|currentConfig
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

