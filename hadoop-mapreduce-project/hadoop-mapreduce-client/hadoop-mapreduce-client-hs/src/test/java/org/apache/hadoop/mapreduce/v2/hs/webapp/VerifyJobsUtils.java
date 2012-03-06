begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobReport
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRApps
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
name|yarn
operator|.
name|webapp
operator|.
name|WebServicesTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
import|;
end_import

begin_class
DECL|class|VerifyJobsUtils
specifier|public
class|class
name|VerifyJobsUtils
block|{
DECL|method|verifyHsJobPartial (JSONObject info, Job job)
specifier|public
specifier|static
name|void
name|verifyHsJobPartial
parameter_list|(
name|JSONObject
name|info
parameter_list|,
name|Job
name|job
parameter_list|)
throws|throws
name|JSONException
block|{
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|11
argument_list|,
name|info
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// everyone access fields
name|verifyHsJobGeneric
argument_list|(
name|job
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"user"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"state"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"queue"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"startTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"finishTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"mapsTotal"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"mapsCompleted"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"reducesTotal"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"reducesCompleted"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyHsJob (JSONObject info, Job job)
specifier|public
specifier|static
name|void
name|verifyHsJob
parameter_list|(
name|JSONObject
name|info
parameter_list|,
name|Job
name|job
parameter_list|)
throws|throws
name|JSONException
block|{
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|24
argument_list|,
name|info
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// everyone access fields
name|verifyHsJobGeneric
argument_list|(
name|job
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"user"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"state"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"queue"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"startTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"finishTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"mapsTotal"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"mapsCompleted"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"reducesTotal"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"reducesCompleted"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|diagnostics
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|has
argument_list|(
literal|"diagnostics"
argument_list|)
condition|)
block|{
name|diagnostics
operator|=
name|info
operator|.
name|getString
argument_list|(
literal|"diagnostics"
argument_list|)
expr_stmt|;
block|}
comment|// restricted access fields - if security and acls set
name|verifyHsJobGenericSecure
argument_list|(
name|job
argument_list|,
name|info
operator|.
name|getBoolean
argument_list|(
literal|"uberized"
argument_list|)
argument_list|,
name|diagnostics
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"avgMapTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"avgReduceTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"avgShuffleTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"avgMergeTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"failedReduceAttempts"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"killedReduceAttempts"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"successfulReduceAttempts"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"failedMapAttempts"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"killedMapAttempts"
argument_list|)
argument_list|,
name|info
operator|.
name|getInt
argument_list|(
literal|"successfulMapAttempts"
argument_list|)
argument_list|)
expr_stmt|;
comment|// acls not being checked since
comment|// we are using mock job instead of CompletedJob
block|}
DECL|method|verifyHsJobGeneric (Job job, String id, String user, String name, String state, String queue, long startTime, long finishTime, int mapsTotal, int mapsCompleted, int reducesTotal, int reducesCompleted)
specifier|public
specifier|static
name|void
name|verifyHsJobGeneric
parameter_list|(
name|Job
name|job
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|state
parameter_list|,
name|String
name|queue
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|int
name|mapsTotal
parameter_list|,
name|int
name|mapsCompleted
parameter_list|,
name|int
name|reducesTotal
parameter_list|,
name|int
name|reducesCompleted
parameter_list|)
block|{
name|JobReport
name|report
init|=
name|job
operator|.
name|getReport
argument_list|()
decl_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"id"
argument_list|,
name|MRApps
operator|.
name|toString
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"user"
argument_list|,
name|job
operator|.
name|getUserName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"name"
argument_list|,
name|job
operator|.
name|getName
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"state"
argument_list|,
name|job
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"queue"
argument_list|,
name|job
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"startTime incorrect"
argument_list|,
name|report
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|startTime
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"finishTime incorrect"
argument_list|,
name|report
operator|.
name|getFinishTime
argument_list|()
argument_list|,
name|finishTime
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapsTotal incorrect"
argument_list|,
name|job
operator|.
name|getTotalMaps
argument_list|()
argument_list|,
name|mapsTotal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapsCompleted incorrect"
argument_list|,
name|job
operator|.
name|getCompletedMaps
argument_list|()
argument_list|,
name|mapsCompleted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reducesTotal incorrect"
argument_list|,
name|job
operator|.
name|getTotalReduces
argument_list|()
argument_list|,
name|reducesTotal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reducesCompleted incorrect"
argument_list|,
name|job
operator|.
name|getCompletedReduces
argument_list|()
argument_list|,
name|reducesCompleted
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyHsJobGenericSecure (Job job, Boolean uberized, String diagnostics, long avgMapTime, long avgReduceTime, long avgShuffleTime, long avgMergeTime, int failedReduceAttempts, int killedReduceAttempts, int successfulReduceAttempts, int failedMapAttempts, int killedMapAttempts, int successfulMapAttempts)
specifier|public
specifier|static
name|void
name|verifyHsJobGenericSecure
parameter_list|(
name|Job
name|job
parameter_list|,
name|Boolean
name|uberized
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|long
name|avgMapTime
parameter_list|,
name|long
name|avgReduceTime
parameter_list|,
name|long
name|avgShuffleTime
parameter_list|,
name|long
name|avgMergeTime
parameter_list|,
name|int
name|failedReduceAttempts
parameter_list|,
name|int
name|killedReduceAttempts
parameter_list|,
name|int
name|successfulReduceAttempts
parameter_list|,
name|int
name|failedMapAttempts
parameter_list|,
name|int
name|killedMapAttempts
parameter_list|,
name|int
name|successfulMapAttempts
parameter_list|)
block|{
name|String
name|diagString
init|=
literal|""
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|diagList
init|=
name|job
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
if|if
condition|(
name|diagList
operator|!=
literal|null
operator|&&
operator|!
name|diagList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|diag
range|:
name|diagList
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|diag
argument_list|)
expr_stmt|;
block|}
name|diagString
operator|=
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"diagnostics"
argument_list|,
name|diagString
argument_list|,
name|diagnostics
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"isUber incorrect"
argument_list|,
name|job
operator|.
name|isUber
argument_list|()
argument_list|,
name|uberized
argument_list|)
expr_stmt|;
comment|// unfortunately the following fields are all calculated in JobInfo
comment|// so not easily accessible without doing all the calculations again.
comment|// For now just make sure they are present.
name|assertTrue
argument_list|(
literal|"failedReduceAttempts not>= 0"
argument_list|,
name|failedReduceAttempts
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"killedReduceAttempts not>= 0"
argument_list|,
name|killedReduceAttempts
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"successfulReduceAttempts not>= 0"
argument_list|,
name|successfulReduceAttempts
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"failedMapAttempts not>= 0"
argument_list|,
name|failedMapAttempts
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"killedMapAttempts not>= 0"
argument_list|,
name|killedMapAttempts
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"successfulMapAttempts not>= 0"
argument_list|,
name|successfulMapAttempts
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"avgMapTime not>= 0"
argument_list|,
name|avgMapTime
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"avgReduceTime not>= 0"
argument_list|,
name|avgReduceTime
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"avgShuffleTime not>= 0"
argument_list|,
name|avgShuffleTime
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"avgMergeTime not>= 0"
argument_list|,
name|avgMergeTime
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

