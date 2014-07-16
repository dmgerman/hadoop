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
comment|/**  * The metrics system interface.  *   * The following components are used for metrics.  *<ul>  *<li>{@link MetricsSource} generate and update metrics information.</li>  *<li>{@link MetricsSink} consume the metrics information</li>  *</ul>  *   * {@link MetricsSource} and {@link MetricsSink} register with the metrics  * system. Implementations of {@link MetricsSystem} polls the  * {@link MetricsSource}s periodically and pass the {@link MetricsRecord}s to  * {@link MetricsSink}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MetricsSystem
specifier|public
specifier|abstract
class|class
name|MetricsSystem
implements|implements
name|MetricsSystemMXBean
block|{
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|init (String prefix)
specifier|public
specifier|abstract
name|MetricsSystem
name|init
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
comment|/**    * Register a metrics source    * @param<T>   the actual type of the source object    * @param source object to register    * @param name  of the source. Must be unique or null (then extracted from    *              the annotations of the source object.)    * @param desc  the description of the source (or null. See above.)    * @return the source object    * @exception MetricsException    */
DECL|method|register (String name, String desc, T source)
specifier|public
specifier|abstract
parameter_list|<
name|T
parameter_list|>
name|T
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|,
name|T
name|source
parameter_list|)
function_decl|;
comment|/**    * Unregister a metrics source    * @param name of the source. This is the name you use to call register()    */
DECL|method|unregisterSource (String name)
specifier|public
specifier|abstract
name|void
name|unregisterSource
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Register a metrics source (deriving name and description from the object)    * @param<T>   the actual type of the source object    * @param source  object to register    * @return  the source object    * @exception MetricsException    */
DECL|method|register (T source)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|register
parameter_list|(
name|T
name|source
parameter_list|)
block|{
return|return
name|register
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|source
argument_list|)
return|;
block|}
comment|/**    * @param name  of the metrics source    * @return the metrics source (potentially wrapped) object    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|getSource (String name)
specifier|public
specifier|abstract
name|MetricsSource
name|getSource
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Register a metrics sink    * @param<T>   the type of the sink    * @param sink  to register    * @param name  of the sink. Must be unique.    * @param desc  the description of the sink    * @return the sink    * @exception MetricsException    */
specifier|public
specifier|abstract
parameter_list|<
name|T
extends|extends
name|MetricsSink
parameter_list|>
DECL|method|register (String name, String desc, T sink)
name|T
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|,
name|T
name|sink
parameter_list|)
function_decl|;
comment|/**    * Register a callback interface for JMX events    * @param callback  the callback object implementing the MBean interface.    */
DECL|method|register (Callback callback)
specifier|public
specifier|abstract
name|void
name|register
parameter_list|(
name|Callback
name|callback
parameter_list|)
function_decl|;
comment|/**    * Requests an immediate publish of all metrics from sources to sinks.    *     * This is a "soft" request: the expectation is that a best effort will be    * done to synchronously snapshot the metrics from all the sources and put    * them in all the sinks (including flushing the sinks) before returning to    * the caller. If this can't be accomplished in reasonable time it's OK to    * return to the caller before everything is done.     */
DECL|method|publishMetricsNow ()
specifier|public
specifier|abstract
name|void
name|publishMetricsNow
parameter_list|()
function_decl|;
comment|/**    * Shutdown the metrics system completely (usually during server shutdown.)    * The MetricsSystemMXBean will be unregistered.    * @return true if shutdown completed    */
DECL|method|shutdown ()
specifier|public
specifier|abstract
name|boolean
name|shutdown
parameter_list|()
function_decl|;
comment|/**    * The metrics system callback interface (needed for proxies.)    */
DECL|interface|Callback
specifier|public
interface|interface
name|Callback
block|{
comment|/**      * Called before start()      */
DECL|method|preStart ()
name|void
name|preStart
parameter_list|()
function_decl|;
comment|/**      * Called after start()      */
DECL|method|postStart ()
name|void
name|postStart
parameter_list|()
function_decl|;
comment|/**      * Called before stop()      */
DECL|method|preStop ()
name|void
name|preStop
parameter_list|()
function_decl|;
comment|/**      * Called after stop()      */
DECL|method|postStop ()
name|void
name|postStop
parameter_list|()
function_decl|;
block|}
comment|/**    * Convenient abstract class for implementing callback interface    */
DECL|class|AbstractCallback
specifier|public
specifier|static
specifier|abstract
class|class
name|AbstractCallback
implements|implements
name|Callback
block|{
DECL|method|preStart ()
annotation|@
name|Override
specifier|public
name|void
name|preStart
parameter_list|()
block|{}
DECL|method|postStart ()
annotation|@
name|Override
specifier|public
name|void
name|postStart
parameter_list|()
block|{}
DECL|method|preStop ()
annotation|@
name|Override
specifier|public
name|void
name|preStop
parameter_list|()
block|{}
DECL|method|postStop ()
annotation|@
name|Override
specifier|public
name|void
name|postStop
parameter_list|()
block|{}
block|}
block|}
end_class

end_unit

