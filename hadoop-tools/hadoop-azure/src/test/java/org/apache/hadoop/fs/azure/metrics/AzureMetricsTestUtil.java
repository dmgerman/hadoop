begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|metrics
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|metrics
operator|.
name|AzureFileSystemInstrumentation
operator|.
name|WASB_BYTES_READ
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|metrics
operator|.
name|AzureFileSystemInstrumentation
operator|.
name|WASB_BYTES_WRITTEN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|metrics
operator|.
name|AzureFileSystemInstrumentation
operator|.
name|WASB_RAW_BYTES_DOWNLOADED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|metrics
operator|.
name|AzureFileSystemInstrumentation
operator|.
name|WASB_RAW_BYTES_UPLOADED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|metrics
operator|.
name|AzureFileSystemInstrumentation
operator|.
name|WASB_WEB_RESPONSES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getLongCounter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getLongGauge
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
import|;
end_import

begin_class
DECL|class|AzureMetricsTestUtil
specifier|public
specifier|final
class|class
name|AzureMetricsTestUtil
block|{
DECL|method|getLongGaugeValue (AzureFileSystemInstrumentation instrumentation, String gaugeName)
specifier|public
specifier|static
name|long
name|getLongGaugeValue
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|,
name|String
name|gaugeName
parameter_list|)
block|{
return|return
name|getLongGauge
argument_list|(
name|gaugeName
argument_list|,
name|getMetrics
argument_list|(
name|instrumentation
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Gets the current value of the given counter.    */
DECL|method|getLongCounterValue (AzureFileSystemInstrumentation instrumentation, String counterName)
specifier|public
specifier|static
name|long
name|getLongCounterValue
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|,
name|String
name|counterName
parameter_list|)
block|{
return|return
name|getLongCounter
argument_list|(
name|counterName
argument_list|,
name|getMetrics
argument_list|(
name|instrumentation
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Gets the current value of the wasb_bytes_written_last_second counter.    */
DECL|method|getCurrentBytesWritten (AzureFileSystemInstrumentation instrumentation)
specifier|public
specifier|static
name|long
name|getCurrentBytesWritten
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|)
block|{
return|return
name|getLongGaugeValue
argument_list|(
name|instrumentation
argument_list|,
name|WASB_BYTES_WRITTEN
argument_list|)
return|;
block|}
comment|/**    * Gets the current value of the wasb_bytes_read_last_second counter.    */
DECL|method|getCurrentBytesRead (AzureFileSystemInstrumentation instrumentation)
specifier|public
specifier|static
name|long
name|getCurrentBytesRead
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|)
block|{
return|return
name|getLongGaugeValue
argument_list|(
name|instrumentation
argument_list|,
name|WASB_BYTES_READ
argument_list|)
return|;
block|}
comment|/**    * Gets the current value of the wasb_raw_bytes_uploaded counter.    */
DECL|method|getCurrentTotalBytesWritten ( AzureFileSystemInstrumentation instrumentation)
specifier|public
specifier|static
name|long
name|getCurrentTotalBytesWritten
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|)
block|{
return|return
name|getLongCounterValue
argument_list|(
name|instrumentation
argument_list|,
name|WASB_RAW_BYTES_UPLOADED
argument_list|)
return|;
block|}
comment|/**    * Gets the current value of the wasb_raw_bytes_downloaded counter.    */
DECL|method|getCurrentTotalBytesRead ( AzureFileSystemInstrumentation instrumentation)
specifier|public
specifier|static
name|long
name|getCurrentTotalBytesRead
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|)
block|{
return|return
name|getLongCounterValue
argument_list|(
name|instrumentation
argument_list|,
name|WASB_RAW_BYTES_DOWNLOADED
argument_list|)
return|;
block|}
comment|/**    * Gets the current value of the asv_web_responses counter.    */
DECL|method|getCurrentWebResponses ( AzureFileSystemInstrumentation instrumentation)
specifier|public
specifier|static
name|long
name|getCurrentWebResponses
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|)
block|{
return|return
name|getLongCounter
argument_list|(
name|WASB_WEB_RESPONSES
argument_list|,
name|getMetrics
argument_list|(
name|instrumentation
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

