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
name|metrics2
operator|.
name|MetricsSource
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
name|MetricsAnnotations
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
name|MetricsSourceBuilder
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
name|MutableGaugeLong
import|;
end_import

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

begin_comment
comment|/**  * This class is for maintaining the various Cleaner activity statistics and  * publishing them through the metrics interfaces.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
annotation|@
name|Metrics
argument_list|(
name|name
operator|=
literal|"CleanerActivity"
argument_list|,
name|about
operator|=
literal|"Cleaner service metrics"
argument_list|,
name|context
operator|=
literal|"yarn"
argument_list|)
DECL|class|CleanerMetrics
specifier|public
class|class
name|CleanerMetrics
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
name|CleanerMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"cleaner"
argument_list|)
decl_stmt|;
DECL|field|INSTANCE
specifier|private
specifier|final
specifier|static
name|CleanerMetrics
name|INSTANCE
init|=
name|create
argument_list|()
decl_stmt|;
DECL|method|getInstance ()
specifier|public
specifier|static
name|CleanerMetrics
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
annotation|@
name|Metric
argument_list|(
literal|"number of deleted files over all runs"
argument_list|)
DECL|field|totalDeletedFiles
specifier|private
name|MutableCounterLong
name|totalDeletedFiles
decl_stmt|;
DECL|method|getTotalDeletedFiles ()
specifier|public
name|long
name|getTotalDeletedFiles
parameter_list|()
block|{
return|return
name|totalDeletedFiles
operator|.
name|value
argument_list|()
return|;
block|}
specifier|private
annotation|@
name|Metric
argument_list|(
literal|"number of deleted files in the last run"
argument_list|)
DECL|field|deletedFiles
name|MutableGaugeLong
name|deletedFiles
decl_stmt|;
DECL|method|getDeletedFiles ()
specifier|public
name|long
name|getDeletedFiles
parameter_list|()
block|{
return|return
name|deletedFiles
operator|.
name|value
argument_list|()
return|;
block|}
specifier|private
annotation|@
name|Metric
argument_list|(
literal|"number of processed files over all runs"
argument_list|)
DECL|field|totalProcessedFiles
name|MutableCounterLong
name|totalProcessedFiles
decl_stmt|;
DECL|method|getTotalProcessedFiles ()
specifier|public
name|long
name|getTotalProcessedFiles
parameter_list|()
block|{
return|return
name|totalProcessedFiles
operator|.
name|value
argument_list|()
return|;
block|}
specifier|private
annotation|@
name|Metric
argument_list|(
literal|"number of processed files in the last run"
argument_list|)
DECL|field|processedFiles
name|MutableGaugeLong
name|processedFiles
decl_stmt|;
DECL|method|getProcessedFiles ()
specifier|public
name|long
name|getProcessedFiles
parameter_list|()
block|{
return|return
name|processedFiles
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|Metric
argument_list|(
literal|"number of file errors over all runs"
argument_list|)
DECL|field|totalFileErrors
specifier|private
name|MutableCounterLong
name|totalFileErrors
decl_stmt|;
DECL|method|getTotalFileErrors ()
specifier|public
name|long
name|getTotalFileErrors
parameter_list|()
block|{
return|return
name|totalFileErrors
operator|.
name|value
argument_list|()
return|;
block|}
specifier|private
annotation|@
name|Metric
argument_list|(
literal|"number of file errors in the last run"
argument_list|)
DECL|field|fileErrors
name|MutableGaugeLong
name|fileErrors
decl_stmt|;
DECL|method|getFileErrors ()
specifier|public
name|long
name|getFileErrors
parameter_list|()
block|{
return|return
name|fileErrors
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|CleanerMetrics ()
specifier|private
name|CleanerMetrics
parameter_list|()
block|{   }
comment|/**    * The metric source obtained after parsing the annotations    */
DECL|field|metricSource
name|MetricsSource
name|metricSource
decl_stmt|;
DECL|method|create ()
specifier|static
name|CleanerMetrics
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
name|CleanerMetrics
name|metricObject
init|=
operator|new
name|CleanerMetrics
argument_list|()
decl_stmt|;
name|MetricsSourceBuilder
name|sb
init|=
name|MetricsAnnotations
operator|.
name|newSourceBuilder
argument_list|(
name|metricObject
argument_list|)
decl_stmt|;
specifier|final
name|MetricsSource
name|s
init|=
name|sb
operator|.
name|build
argument_list|()
decl_stmt|;
name|ms
operator|.
name|register
argument_list|(
literal|"cleaner"
argument_list|,
literal|"The cleaner service of truly shared cache"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|metricObject
operator|.
name|metricSource
operator|=
name|s
expr_stmt|;
return|return
name|metricObject
return|;
block|}
comment|/**    * Report a delete operation at the current system time    */
DECL|method|reportAFileDelete ()
specifier|public
name|void
name|reportAFileDelete
parameter_list|()
block|{
name|totalProcessedFiles
operator|.
name|incr
argument_list|()
expr_stmt|;
name|processedFiles
operator|.
name|incr
argument_list|()
expr_stmt|;
name|totalDeletedFiles
operator|.
name|incr
argument_list|()
expr_stmt|;
name|deletedFiles
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Report a process operation at the current system time    */
DECL|method|reportAFileProcess ()
specifier|public
name|void
name|reportAFileProcess
parameter_list|()
block|{
name|totalProcessedFiles
operator|.
name|incr
argument_list|()
expr_stmt|;
name|processedFiles
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Report a process operation error at the current system time    */
DECL|method|reportAFileError ()
specifier|public
name|void
name|reportAFileError
parameter_list|()
block|{
name|totalProcessedFiles
operator|.
name|incr
argument_list|()
expr_stmt|;
name|processedFiles
operator|.
name|incr
argument_list|()
expr_stmt|;
name|totalFileErrors
operator|.
name|incr
argument_list|()
expr_stmt|;
name|fileErrors
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Report the start a new run of the cleaner.    *    */
DECL|method|reportCleaningStart ()
specifier|public
name|void
name|reportCleaningStart
parameter_list|()
block|{
name|processedFiles
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|deletedFiles
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fileErrors
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

