begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|metrics2
operator|.
name|MetricsRecordBuilder
import|;
end_import

begin_comment
comment|/**  * Helper class to manage a group of mutable rate metrics  *  * This class synchronizes all accesses to the metrics it  * contains, so it should not be used in situations where  * there is high contention on the metrics.  * {@link MutableRatesWithAggregation} is preferable in that  * situation.  */
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
DECL|class|MutableRates
specifier|public
class|class
name|MutableRates
extends|extends
name|MutableMetric
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MutableRates
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|field|protocolCache
specifier|private
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|protocolCache
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
DECL|method|MutableRates (MetricsRegistry registry)
name|MutableRates
parameter_list|(
name|MetricsRegistry
name|registry
parameter_list|)
block|{
name|this
operator|.
name|registry
operator|=
name|checkNotNull
argument_list|(
name|registry
argument_list|,
literal|"metrics registry"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize the registry with all the methods in a protocol    * so they all show up in the first snapshot.    * Convenient for JMX implementations.    * @param protocol the protocol class    */
DECL|method|init (Class<?> protocol)
specifier|public
name|void
name|init
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|)
block|{
if|if
condition|(
name|protocolCache
operator|.
name|contains
argument_list|(
name|protocol
argument_list|)
condition|)
return|return;
name|protocolCache
operator|.
name|add
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|protocol
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|registry
operator|.
name|newRate
argument_list|(
name|name
argument_list|,
name|name
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error creating rate metrics for "
operator|+
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Add a rate sample for a rate metric    * @param name of the rate metric    * @param elapsed time    */
DECL|method|add (String name, long elapsed)
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|elapsed
parameter_list|)
block|{
name|registry
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|snapshot (MetricsRecordBuilder rb, boolean all)
specifier|public
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|rb
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|registry
operator|.
name|snapshot
argument_list|(
name|rb
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

