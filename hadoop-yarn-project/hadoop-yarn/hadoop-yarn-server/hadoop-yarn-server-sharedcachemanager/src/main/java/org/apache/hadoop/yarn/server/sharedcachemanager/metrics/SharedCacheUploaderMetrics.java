begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|sharedcachemanager
operator|.
name|metrics
package|;
end_package

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
operator|.
name|Private
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
operator|.
name|Evolving
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
name|metrics2
operator|.
name|MetricsSystem
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
name|annotation
operator|.
name|Metric
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
name|annotation
operator|.
name|Metrics
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
name|lib
operator|.
name|DefaultMetricsSystem
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
name|lib
operator|.
name|MetricsRegistry
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
name|lib
operator|.
name|MutableCounterLong
import|;
end_import

begin_comment
comment|/**  * This class is for maintaining shared cache uploader requests metrics  * and publishing them through the metrics interfaces.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"shared cache upload metrics"
argument_list|,
name|context
operator|=
literal|"yarn"
argument_list|)
DECL|class|SharedCacheUploaderMetrics
specifier|public
class|class
name|SharedCacheUploaderMetrics
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
name|SharedCacheUploaderMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|registry
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|method|SharedCacheUploaderMetrics ()
name|SharedCacheUploaderMetrics
parameter_list|()
block|{
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"SharedCacheUploaderRequests"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initialized "
operator|+
name|registry
argument_list|)
expr_stmt|;
block|}
DECL|enum|Singleton
enum|enum
name|Singleton
block|{
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
DECL|field|impl
name|SharedCacheUploaderMetrics
name|impl
decl_stmt|;
DECL|method|init (Configuration conf)
specifier|synchronized
name|SharedCacheUploaderMetrics
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|impl
operator|==
literal|null
condition|)
block|{
name|impl
operator|=
name|create
argument_list|()
expr_stmt|;
block|}
return|return
name|impl
return|;
block|}
block|}
specifier|public
specifier|static
name|SharedCacheUploaderMetrics
DECL|method|initSingleton (Configuration conf)
name|initSingleton
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|Singleton
operator|.
name|INSTANCE
operator|.
name|init
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|getInstance ()
specifier|public
specifier|static
name|SharedCacheUploaderMetrics
name|getInstance
parameter_list|()
block|{
name|SharedCacheUploaderMetrics
name|topMetrics
init|=
name|Singleton
operator|.
name|INSTANCE
operator|.
name|impl
decl_stmt|;
if|if
condition|(
name|topMetrics
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The SharedCacheUploaderMetrics singleton instance is not"
operator|+
literal|"initialized. Have you called init first?"
argument_list|)
throw|;
return|return
name|topMetrics
return|;
block|}
DECL|method|create ()
specifier|static
name|SharedCacheUploaderMetrics
name|create
parameter_list|()
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|SharedCacheUploaderMetrics
name|metrics
init|=
operator|new
name|SharedCacheUploaderMetrics
argument_list|()
decl_stmt|;
name|ms
operator|.
name|register
argument_list|(
literal|"SharedCacheUploaderRequests"
argument_list|,
literal|null
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
return|return
name|metrics
return|;
block|}
DECL|field|acceptedUploads
annotation|@
name|Metric
argument_list|(
literal|"Number of accepted uploads"
argument_list|)
name|MutableCounterLong
name|acceptedUploads
decl_stmt|;
DECL|field|rejectedUploads
annotation|@
name|Metric
argument_list|(
literal|"Number of rejected uploads"
argument_list|)
name|MutableCounterLong
name|rejectedUploads
decl_stmt|;
comment|/**    * One accepted upload event    */
DECL|method|incAcceptedUploads ()
specifier|public
name|void
name|incAcceptedUploads
parameter_list|()
block|{
name|acceptedUploads
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * One rejected upload event    */
DECL|method|incRejectedUploads ()
specifier|public
name|void
name|incRejectedUploads
parameter_list|()
block|{
name|rejectedUploads
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|getAcceptedUploads ()
specifier|public
name|long
name|getAcceptedUploads
parameter_list|()
block|{
return|return
name|acceptedUploads
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getRejectUploads ()
specifier|public
name|long
name|getRejectUploads
parameter_list|()
block|{
return|return
name|rejectedUploads
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

