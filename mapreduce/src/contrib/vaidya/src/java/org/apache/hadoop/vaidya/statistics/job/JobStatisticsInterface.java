begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.vaidya.statistics.job
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|vaidya
operator|.
name|statistics
operator|.
name|job
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|mapred
operator|.
name|JobConf
import|;
end_import

begin_interface
DECL|interface|JobStatisticsInterface
specifier|public
interface|interface
name|JobStatisticsInterface
block|{
comment|/**    * Get job configuration (job.xml) values    */
DECL|method|getJobConf ()
specifier|public
name|JobConf
name|getJobConf
parameter_list|()
function_decl|;
comment|/*    * Get Job Counters of type long    */
DECL|method|getLongValue (Enum key)
specifier|public
name|long
name|getLongValue
parameter_list|(
name|Enum
name|key
parameter_list|)
function_decl|;
comment|/*    * Get job Counters of type Double    */
DECL|method|getDoubleValue (Enum key)
specifier|public
name|double
name|getDoubleValue
parameter_list|(
name|Enum
name|key
parameter_list|)
function_decl|;
comment|/*     * Get Job Counters of type String    */
DECL|method|getStringValue (Enum key)
specifier|public
name|String
name|getStringValue
parameter_list|(
name|Enum
name|key
parameter_list|)
function_decl|;
comment|/*    * Set key value of type long    */
DECL|method|setValue (Enum key, long value)
specifier|public
name|void
name|setValue
parameter_list|(
name|Enum
name|key
parameter_list|,
name|long
name|value
parameter_list|)
function_decl|;
comment|/*    * Set key value of type double    */
DECL|method|setValue (Enum key, double valye)
specifier|public
name|void
name|setValue
parameter_list|(
name|Enum
name|key
parameter_list|,
name|double
name|valye
parameter_list|)
function_decl|;
comment|/*    * Set key value of type String    */
DECL|method|setValue (Enum key, String value)
specifier|public
name|void
name|setValue
parameter_list|(
name|Enum
name|key
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
comment|/**    * @return mapTaskList : ArrayList of MapTaskStatistics    * @param mapTaskSortKey : Specific counter key used for sorting the task list    * @param dataType : indicates the data type of the counter key used for sorting    * If sort key is null then by default map tasks are sorted using map task ids.    */
DECL|method|getMapTaskList (Enum mapTaskSortKey, KeyDataType dataType)
specifier|public
name|ArrayList
argument_list|<
name|MapTaskStatistics
argument_list|>
name|getMapTaskList
parameter_list|(
name|Enum
name|mapTaskSortKey
parameter_list|,
name|KeyDataType
name|dataType
parameter_list|)
function_decl|;
comment|/**    * @return reduceTaskList : ArrayList of ReduceTaskStatistics    * @param reduceTaskSortKey : Specific counter key used for sorting the task list    * @param dataType : indicates the data type of the counter key used for sorting    * If sort key is null then, by default reduce tasks are sorted using task ids.    */
DECL|method|getReduceTaskList (Enum reduceTaskSortKey, KeyDataType dataType)
specifier|public
name|ArrayList
argument_list|<
name|ReduceTaskStatistics
argument_list|>
name|getReduceTaskList
parameter_list|(
name|Enum
name|reduceTaskSortKey
parameter_list|,
name|KeyDataType
name|dataType
parameter_list|)
function_decl|;
comment|/*    * Print the Job Execution Statistics    */
DECL|method|printJobExecutionStatistics ()
specifier|public
name|void
name|printJobExecutionStatistics
parameter_list|()
function_decl|;
comment|/*    * Job and Task statistics Key data types    */
DECL|enum|KeyDataType
specifier|public
specifier|static
enum|enum
name|KeyDataType
block|{
DECL|enumConstant|STRING
DECL|enumConstant|LONG
DECL|enumConstant|DOUBLE
name|STRING
block|,
name|LONG
block|,
name|DOUBLE
block|}
comment|/**    * Job Keys    */
DECL|enum|JobKeys
specifier|public
specifier|static
enum|enum
name|JobKeys
block|{
DECL|enumConstant|JOBTRACKERID
DECL|enumConstant|JOBID
DECL|enumConstant|JOBNAME
DECL|enumConstant|JOBTYPE
DECL|enumConstant|USER
DECL|enumConstant|SUBMIT_TIME
DECL|enumConstant|CONF_PATH
DECL|enumConstant|LAUNCH_TIME
DECL|enumConstant|TOTAL_MAPS
DECL|enumConstant|TOTAL_REDUCES
name|JOBTRACKERID
block|,
name|JOBID
block|,
name|JOBNAME
block|,
name|JOBTYPE
block|,
name|USER
block|,
name|SUBMIT_TIME
block|,
name|CONF_PATH
block|,
name|LAUNCH_TIME
block|,
name|TOTAL_MAPS
block|,
name|TOTAL_REDUCES
block|,
DECL|enumConstant|STATUS
DECL|enumConstant|FINISH_TIME
DECL|enumConstant|FINISHED_MAPS
DECL|enumConstant|FINISHED_REDUCES
DECL|enumConstant|FAILED_MAPS
DECL|enumConstant|FAILED_REDUCES
name|STATUS
block|,
name|FINISH_TIME
block|,
name|FINISHED_MAPS
block|,
name|FINISHED_REDUCES
block|,
name|FAILED_MAPS
block|,
name|FAILED_REDUCES
block|,
DECL|enumConstant|LAUNCHED_MAPS
DECL|enumConstant|LAUNCHED_REDUCES
DECL|enumConstant|RACKLOCAL_MAPS
DECL|enumConstant|DATALOCAL_MAPS
DECL|enumConstant|HDFS_BYTES_READ
name|LAUNCHED_MAPS
block|,
name|LAUNCHED_REDUCES
block|,
name|RACKLOCAL_MAPS
block|,
name|DATALOCAL_MAPS
block|,
name|HDFS_BYTES_READ
block|,
DECL|enumConstant|HDFS_BYTES_WRITTEN
DECL|enumConstant|FILE_BYTES_READ
DECL|enumConstant|FILE_BYTES_WRITTEN
DECL|enumConstant|COMBINE_OUTPUT_RECORDS
name|HDFS_BYTES_WRITTEN
block|,
name|FILE_BYTES_READ
block|,
name|FILE_BYTES_WRITTEN
block|,
name|COMBINE_OUTPUT_RECORDS
block|,
DECL|enumConstant|COMBINE_INPUT_RECORDS
DECL|enumConstant|REDUCE_INPUT_GROUPS
DECL|enumConstant|REDUCE_INPUT_RECORDS
DECL|enumConstant|REDUCE_OUTPUT_RECORDS
name|COMBINE_INPUT_RECORDS
block|,
name|REDUCE_INPUT_GROUPS
block|,
name|REDUCE_INPUT_RECORDS
block|,
name|REDUCE_OUTPUT_RECORDS
block|,
DECL|enumConstant|MAP_INPUT_RECORDS
DECL|enumConstant|MAP_OUTPUT_RECORDS
DECL|enumConstant|MAP_INPUT_BYTES
DECL|enumConstant|MAP_OUTPUT_BYTES
DECL|enumConstant|MAP_HDFS_BYTES_WRITTEN
name|MAP_INPUT_RECORDS
block|,
name|MAP_OUTPUT_RECORDS
block|,
name|MAP_INPUT_BYTES
block|,
name|MAP_OUTPUT_BYTES
block|,
name|MAP_HDFS_BYTES_WRITTEN
block|,
DECL|enumConstant|JOBCONF
DECL|enumConstant|JOB_PRIORITY
DECL|enumConstant|SHUFFLE_BYTES
DECL|enumConstant|SPILLED_RECORDS
name|JOBCONF
block|,
name|JOB_PRIORITY
block|,
name|SHUFFLE_BYTES
block|,
name|SPILLED_RECORDS
block|}
comment|/**    * Map Task Keys    */
DECL|enum|MapTaskKeys
specifier|public
specifier|static
enum|enum
name|MapTaskKeys
block|{
DECL|enumConstant|TASK_ID
DECL|enumConstant|TASK_TYPE
DECL|enumConstant|START_TIME
DECL|enumConstant|STATUS
DECL|enumConstant|FINISH_TIME
DECL|enumConstant|HDFS_BYTES_READ
DECL|enumConstant|HDFS_BYTES_WRITTEN
name|TASK_ID
block|,
name|TASK_TYPE
block|,
name|START_TIME
block|,
name|STATUS
block|,
name|FINISH_TIME
block|,
name|HDFS_BYTES_READ
block|,
name|HDFS_BYTES_WRITTEN
block|,
DECL|enumConstant|FILE_BYTES_READ
DECL|enumConstant|FILE_BYTES_WRITTEN
DECL|enumConstant|COMBINE_OUTPUT_RECORDS
DECL|enumConstant|COMBINE_INPUT_RECORDS
name|FILE_BYTES_READ
block|,
name|FILE_BYTES_WRITTEN
block|,
name|COMBINE_OUTPUT_RECORDS
block|,
name|COMBINE_INPUT_RECORDS
block|,
DECL|enumConstant|OUTPUT_RECORDS
DECL|enumConstant|INPUT_RECORDS
DECL|enumConstant|INPUT_BYTES
DECL|enumConstant|OUTPUT_BYTES
DECL|enumConstant|NUM_ATTEMPTS
DECL|enumConstant|ATTEMPT_ID
name|OUTPUT_RECORDS
block|,
name|INPUT_RECORDS
block|,
name|INPUT_BYTES
block|,
name|OUTPUT_BYTES
block|,
name|NUM_ATTEMPTS
block|,
name|ATTEMPT_ID
block|,
DECL|enumConstant|HOSTNAME
DECL|enumConstant|SPLITS
DECL|enumConstant|SPILLED_RECORDS
DECL|enumConstant|TRACKER_NAME
DECL|enumConstant|STATE_STRING
DECL|enumConstant|HTTP_PORT
DECL|enumConstant|ERROR
DECL|enumConstant|EXECUTION_TIME
name|HOSTNAME
block|,
name|SPLITS
block|,
name|SPILLED_RECORDS
block|,
name|TRACKER_NAME
block|,
name|STATE_STRING
block|,
name|HTTP_PORT
block|,
name|ERROR
block|,
name|EXECUTION_TIME
block|}
comment|/**    * Reduce Task Keys    */
DECL|enum|ReduceTaskKeys
specifier|public
specifier|static
enum|enum
name|ReduceTaskKeys
block|{
DECL|enumConstant|TASK_ID
DECL|enumConstant|TASK_TYPE
DECL|enumConstant|START_TIME
DECL|enumConstant|STATUS
DECL|enumConstant|FINISH_TIME
DECL|enumConstant|HDFS_BYTES_READ
DECL|enumConstant|HDFS_BYTES_WRITTEN
name|TASK_ID
block|,
name|TASK_TYPE
block|,
name|START_TIME
block|,
name|STATUS
block|,
name|FINISH_TIME
block|,
name|HDFS_BYTES_READ
block|,
name|HDFS_BYTES_WRITTEN
block|,
DECL|enumConstant|FILE_BYTES_READ
DECL|enumConstant|FILE_BYTES_WRITTEN
DECL|enumConstant|COMBINE_OUTPUT_RECORDS
DECL|enumConstant|COMBINE_INPUT_RECORDS
name|FILE_BYTES_READ
block|,
name|FILE_BYTES_WRITTEN
block|,
name|COMBINE_OUTPUT_RECORDS
block|,
name|COMBINE_INPUT_RECORDS
block|,
DECL|enumConstant|OUTPUT_RECORDS
DECL|enumConstant|INPUT_RECORDS
DECL|enumConstant|NUM_ATTEMPTS
DECL|enumConstant|ATTEMPT_ID
DECL|enumConstant|HOSTNAME
DECL|enumConstant|SHUFFLE_FINISH_TIME
name|OUTPUT_RECORDS
block|,
name|INPUT_RECORDS
block|,
name|NUM_ATTEMPTS
block|,
name|ATTEMPT_ID
block|,
name|HOSTNAME
block|,
name|SHUFFLE_FINISH_TIME
block|,
DECL|enumConstant|SORT_FINISH_TIME
DECL|enumConstant|INPUT_GROUPS
DECL|enumConstant|TRACKER_NAME
DECL|enumConstant|STATE_STRING
DECL|enumConstant|HTTP_PORT
DECL|enumConstant|SPLITS
DECL|enumConstant|SHUFFLE_BYTES
name|SORT_FINISH_TIME
block|,
name|INPUT_GROUPS
block|,
name|TRACKER_NAME
block|,
name|STATE_STRING
block|,
name|HTTP_PORT
block|,
name|SPLITS
block|,
name|SHUFFLE_BYTES
block|,
DECL|enumConstant|SPILLED_RECORDS
DECL|enumConstant|EXECUTION_TIME
name|SPILLED_RECORDS
block|,
name|EXECUTION_TIME
block|}
block|}
end_interface

end_unit

