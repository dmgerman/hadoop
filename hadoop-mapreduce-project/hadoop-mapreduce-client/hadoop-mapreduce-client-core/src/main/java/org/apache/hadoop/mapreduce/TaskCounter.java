begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
comment|// Counters used by Task classes
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|TaskCounter
specifier|public
enum|enum
name|TaskCounter
block|{
DECL|enumConstant|MAP_INPUT_RECORDS
name|MAP_INPUT_RECORDS
block|,
DECL|enumConstant|MAP_OUTPUT_RECORDS
name|MAP_OUTPUT_RECORDS
block|,
DECL|enumConstant|MAP_SKIPPED_RECORDS
name|MAP_SKIPPED_RECORDS
block|,
DECL|enumConstant|MAP_OUTPUT_BYTES
name|MAP_OUTPUT_BYTES
block|,
DECL|enumConstant|MAP_OUTPUT_MATERIALIZED_BYTES
name|MAP_OUTPUT_MATERIALIZED_BYTES
block|,
DECL|enumConstant|SPLIT_RAW_BYTES
name|SPLIT_RAW_BYTES
block|,
DECL|enumConstant|COMBINE_INPUT_RECORDS
name|COMBINE_INPUT_RECORDS
block|,
DECL|enumConstant|COMBINE_OUTPUT_RECORDS
name|COMBINE_OUTPUT_RECORDS
block|,
DECL|enumConstant|REDUCE_INPUT_GROUPS
name|REDUCE_INPUT_GROUPS
block|,
DECL|enumConstant|REDUCE_SHUFFLE_BYTES
name|REDUCE_SHUFFLE_BYTES
block|,
DECL|enumConstant|REDUCE_INPUT_RECORDS
name|REDUCE_INPUT_RECORDS
block|,
DECL|enumConstant|REDUCE_OUTPUT_RECORDS
name|REDUCE_OUTPUT_RECORDS
block|,
DECL|enumConstant|REDUCE_SKIPPED_GROUPS
name|REDUCE_SKIPPED_GROUPS
block|,
DECL|enumConstant|REDUCE_SKIPPED_RECORDS
name|REDUCE_SKIPPED_RECORDS
block|,
DECL|enumConstant|SPILLED_RECORDS
name|SPILLED_RECORDS
block|,
DECL|enumConstant|SHUFFLED_MAPS
name|SHUFFLED_MAPS
block|,
DECL|enumConstant|FAILED_SHUFFLE
name|FAILED_SHUFFLE
block|,
DECL|enumConstant|MERGED_MAP_OUTPUTS
name|MERGED_MAP_OUTPUTS
block|,
DECL|enumConstant|GC_TIME_MILLIS
name|GC_TIME_MILLIS
block|,
DECL|enumConstant|CPU_MILLISECONDS
name|CPU_MILLISECONDS
block|,
DECL|enumConstant|PHYSICAL_MEMORY_BYTES
name|PHYSICAL_MEMORY_BYTES
block|,
DECL|enumConstant|VIRTUAL_MEMORY_BYTES
name|VIRTUAL_MEMORY_BYTES
block|,
DECL|enumConstant|COMMITTED_HEAP_BYTES
name|COMMITTED_HEAP_BYTES
block|,
DECL|enumConstant|MAP_PHYSICAL_MEMORY_BYTES_MAX
name|MAP_PHYSICAL_MEMORY_BYTES_MAX
block|,
DECL|enumConstant|MAP_VIRTUAL_MEMORY_BYTES_MAX
name|MAP_VIRTUAL_MEMORY_BYTES_MAX
block|,
DECL|enumConstant|REDUCE_PHYSICAL_MEMORY_BYTES_MAX
name|REDUCE_PHYSICAL_MEMORY_BYTES_MAX
block|,
DECL|enumConstant|REDUCE_VIRTUAL_MEMORY_BYTES_MAX
name|REDUCE_VIRTUAL_MEMORY_BYTES_MAX
block|; }
end_enum

end_unit

