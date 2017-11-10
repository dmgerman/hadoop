begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.source
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|source
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
name|metrics2
operator|.
name|MetricsInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringJoiner
import|;
end_import

begin_comment
comment|/**  * JVM and logging related metrics info instances  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|JvmMetricsInfo
specifier|public
enum|enum
name|JvmMetricsInfo
implements|implements
name|MetricsInfo
block|{
DECL|enumConstant|JvmMetrics
name|JvmMetrics
argument_list|(
literal|"JVM related metrics etc."
argument_list|)
block|,
comment|// record info
comment|// metrics
DECL|enumConstant|MemNonHeapUsedM
name|MemNonHeapUsedM
argument_list|(
literal|"Non-heap memory used in MB"
argument_list|)
block|,
DECL|enumConstant|MemNonHeapCommittedM
name|MemNonHeapCommittedM
argument_list|(
literal|"Non-heap memory committed in MB"
argument_list|)
block|,
DECL|enumConstant|MemNonHeapMaxM
name|MemNonHeapMaxM
argument_list|(
literal|"Non-heap memory max in MB"
argument_list|)
block|,
DECL|enumConstant|MemHeapUsedM
name|MemHeapUsedM
argument_list|(
literal|"Heap memory used in MB"
argument_list|)
block|,
DECL|enumConstant|MemHeapCommittedM
name|MemHeapCommittedM
argument_list|(
literal|"Heap memory committed in MB"
argument_list|)
block|,
DECL|enumConstant|MemHeapMaxM
name|MemHeapMaxM
argument_list|(
literal|"Heap memory max in MB"
argument_list|)
block|,
DECL|enumConstant|MemMaxM
name|MemMaxM
argument_list|(
literal|"Max memory size in MB"
argument_list|)
block|,
DECL|enumConstant|GcCount
name|GcCount
argument_list|(
literal|"Total GC count"
argument_list|)
block|,
DECL|enumConstant|GcTimeMillis
name|GcTimeMillis
argument_list|(
literal|"Total GC time in milliseconds"
argument_list|)
block|,
DECL|enumConstant|ThreadsNew
name|ThreadsNew
argument_list|(
literal|"Number of new threads"
argument_list|)
block|,
DECL|enumConstant|ThreadsRunnable
name|ThreadsRunnable
argument_list|(
literal|"Number of runnable threads"
argument_list|)
block|,
DECL|enumConstant|ThreadsBlocked
name|ThreadsBlocked
argument_list|(
literal|"Number of blocked threads"
argument_list|)
block|,
DECL|enumConstant|ThreadsWaiting
name|ThreadsWaiting
argument_list|(
literal|"Number of waiting threads"
argument_list|)
block|,
DECL|enumConstant|ThreadsTimedWaiting
name|ThreadsTimedWaiting
argument_list|(
literal|"Number of timed waiting threads"
argument_list|)
block|,
DECL|enumConstant|ThreadsTerminated
name|ThreadsTerminated
argument_list|(
literal|"Number of terminated threads"
argument_list|)
block|,
DECL|enumConstant|LogFatal
name|LogFatal
argument_list|(
literal|"Total number of fatal log events"
argument_list|)
block|,
DECL|enumConstant|LogError
name|LogError
argument_list|(
literal|"Total number of error log events"
argument_list|)
block|,
DECL|enumConstant|LogWarn
name|LogWarn
argument_list|(
literal|"Total number of warning log events"
argument_list|)
block|,
DECL|enumConstant|LogInfo
name|LogInfo
argument_list|(
literal|"Total number of info log events"
argument_list|)
block|,
DECL|enumConstant|GcNumWarnThresholdExceeded
name|GcNumWarnThresholdExceeded
argument_list|(
literal|"Number of times that the GC warn threshold is exceeded"
argument_list|)
block|,
DECL|enumConstant|GcNumInfoThresholdExceeded
name|GcNumInfoThresholdExceeded
argument_list|(
literal|"Number of times that the GC info threshold is exceeded"
argument_list|)
block|,
DECL|enumConstant|GcTotalExtraSleepTime
name|GcTotalExtraSleepTime
argument_list|(
literal|"Total GC extra sleep time in milliseconds"
argument_list|)
block|,
DECL|enumConstant|GcTimePercentage
name|GcTimePercentage
argument_list|(
literal|"Percentage of time the JVM was paused in GC"
argument_list|)
block|;
DECL|field|desc
specifier|private
specifier|final
name|String
name|desc
decl_stmt|;
DECL|method|JvmMetricsInfo (String desc)
name|JvmMetricsInfo
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
name|this
operator|.
name|desc
operator|=
name|desc
expr_stmt|;
block|}
DECL|method|description ()
annotation|@
name|Override
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|desc
return|;
block|}
DECL|method|toString ()
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringJoiner
argument_list|(
literal|", "
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{"
argument_list|,
literal|"}"
argument_list|)
operator|.
name|add
argument_list|(
literal|"name="
operator|+
name|name
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"description="
operator|+
name|desc
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

