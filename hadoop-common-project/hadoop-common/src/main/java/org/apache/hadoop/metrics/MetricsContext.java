begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * MetricsContext.java  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
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
name|Collection
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
name|metrics
operator|.
name|spi
operator|.
name|OutputRecord
import|;
end_import

begin_comment
comment|/**  * The main interface to the metrics package.   *  * @deprecated Use org.apache.hadoop.metrics2 package instead.  */
end_comment

begin_interface
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|MetricsContext
specifier|public
interface|interface
name|MetricsContext
block|{
comment|/**    * Default period in seconds at which data is sent to the metrics system.    */
DECL|field|DEFAULT_PERIOD
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PERIOD
init|=
literal|5
decl_stmt|;
comment|/**    * Initialize this context.    * @param contextName The given name for this context    * @param factory The creator of this context    */
DECL|method|init (String contextName, ContextFactory factory)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|contextName
parameter_list|,
name|ContextFactory
name|factory
parameter_list|)
function_decl|;
comment|/**    * Returns the context name.    *    * @return the context name    */
DECL|method|getContextName ()
specifier|public
specifier|abstract
name|String
name|getContextName
parameter_list|()
function_decl|;
comment|/**    * Starts or restarts monitoring, the emitting of metrics records as they are     * updated.     */
DECL|method|startMonitoring ()
specifier|public
specifier|abstract
name|void
name|startMonitoring
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Stops monitoring.  This does not free any data that the implementation    * may have buffered for sending at the next timer event. It    * is OK to call<code>startMonitoring()</code> again after calling     * this.    * @see #close()    */
DECL|method|stopMonitoring ()
specifier|public
specifier|abstract
name|void
name|stopMonitoring
parameter_list|()
function_decl|;
comment|/**    * Returns true if monitoring is currently in progress.    */
DECL|method|isMonitoring ()
specifier|public
specifier|abstract
name|boolean
name|isMonitoring
parameter_list|()
function_decl|;
comment|/**    * Stops monitoring and also frees any buffered data, returning this     * object to its initial state.      */
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Creates a new MetricsRecord instance with the given<code>recordName</code>.    * Throws an exception if the metrics implementation is configured with a fixed    * set of record names and<code>recordName</code> is not in that set.    *    * @param recordName the name of the record    * @throws MetricsException if recordName conflicts with configuration data    */
DECL|method|createRecord (String recordName)
specifier|public
specifier|abstract
name|MetricsRecord
name|createRecord
parameter_list|(
name|String
name|recordName
parameter_list|)
function_decl|;
comment|/**    * Registers a callback to be called at regular time intervals, as     * determined by the implementation-class specific configuration.    *    * @param updater object to be run periodically; it should updated    * some metrics records and then return    */
DECL|method|registerUpdater (Updater updater)
specifier|public
specifier|abstract
name|void
name|registerUpdater
parameter_list|(
name|Updater
name|updater
parameter_list|)
function_decl|;
comment|/**    * Removes a callback, if it exists.    *     * @param updater object to be removed from the callback list    */
DECL|method|unregisterUpdater (Updater updater)
specifier|public
specifier|abstract
name|void
name|unregisterUpdater
parameter_list|(
name|Updater
name|updater
parameter_list|)
function_decl|;
comment|/**    * Returns the timer period.    */
DECL|method|getPeriod ()
specifier|public
specifier|abstract
name|int
name|getPeriod
parameter_list|()
function_decl|;
comment|/**    * Retrieves all the records managed by this MetricsContext.    * Useful for monitoring systems that are polling-based.    *     * @return A non-null map from all record names to the records managed.    */
DECL|method|getAllRecords ()
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|OutputRecord
argument_list|>
argument_list|>
name|getAllRecords
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

