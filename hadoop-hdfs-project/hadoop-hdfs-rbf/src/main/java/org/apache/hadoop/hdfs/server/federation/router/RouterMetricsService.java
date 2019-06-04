begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|metrics
operator|.
name|RBFMetrics
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|metrics
operator|.
name|NamenodeBeanMetrics
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
name|source
operator|.
name|JvmMetrics
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_comment
comment|/**  * Service to manage the metrics of the Router.  */
end_comment

begin_class
DECL|class|RouterMetricsService
specifier|public
class|class
name|RouterMetricsService
extends|extends
name|AbstractService
block|{
comment|/** Router for this metrics. */
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
comment|/** Router metrics. */
DECL|field|routerMetrics
specifier|private
name|RouterMetrics
name|routerMetrics
decl_stmt|;
comment|/** Federation metrics. */
DECL|field|rbfMetrics
specifier|private
name|RBFMetrics
name|rbfMetrics
decl_stmt|;
comment|/** Namenode mock metrics. */
DECL|field|nnMetrics
specifier|private
name|NamenodeBeanMetrics
name|nnMetrics
decl_stmt|;
DECL|method|RouterMetricsService (final Router router)
specifier|public
name|RouterMetricsService
parameter_list|(
specifier|final
name|Router
name|router
parameter_list|)
block|{
name|super
argument_list|(
name|RouterMetricsService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration configuration)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|routerMetrics
operator|=
name|RouterMetrics
operator|.
name|create
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Wrapper for all the FSNamesystem JMX interfaces
name|this
operator|.
name|nnMetrics
operator|=
operator|new
name|NamenodeBeanMetrics
argument_list|(
name|this
operator|.
name|router
argument_list|)
expr_stmt|;
comment|// Federation MBean JMX interface
name|this
operator|.
name|rbfMetrics
operator|=
operator|new
name|RBFMetrics
argument_list|(
name|this
operator|.
name|router
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Remove JMX interfaces
if|if
condition|(
name|this
operator|.
name|rbfMetrics
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|rbfMetrics
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Remove Namenode JMX interfaces
if|if
condition|(
name|this
operator|.
name|nnMetrics
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nnMetrics
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Shutdown metrics
if|if
condition|(
name|this
operator|.
name|routerMetrics
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|routerMetrics
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get the metrics system for the Router.    *    * @return Router metrics.    */
DECL|method|getRouterMetrics ()
specifier|public
name|RouterMetrics
name|getRouterMetrics
parameter_list|()
block|{
return|return
name|this
operator|.
name|routerMetrics
return|;
block|}
comment|/**    * Get the federation metrics.    *    * @return Federation metrics.    */
DECL|method|getRBFMetrics ()
specifier|public
name|RBFMetrics
name|getRBFMetrics
parameter_list|()
block|{
return|return
name|this
operator|.
name|rbfMetrics
return|;
block|}
comment|/**    * Get the Namenode metrics.    *    * @return Namenode metrics.    */
DECL|method|getNamenodeMetrics ()
specifier|public
name|NamenodeBeanMetrics
name|getNamenodeMetrics
parameter_list|()
block|{
return|return
name|this
operator|.
name|nnMetrics
return|;
block|}
comment|/**    * Get the JVM metrics for the Router.    *    * @return JVM metrics.    */
DECL|method|getJvmMetrics ()
specifier|public
name|JvmMetrics
name|getJvmMetrics
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|routerMetrics
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|this
operator|.
name|routerMetrics
operator|.
name|getJvmMetrics
argument_list|()
return|;
block|}
block|}
end_class

end_unit

