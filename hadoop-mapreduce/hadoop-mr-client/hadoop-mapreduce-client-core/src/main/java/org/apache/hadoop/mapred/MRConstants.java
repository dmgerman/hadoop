begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_comment
comment|/*******************************  * Some handy constants  *   *******************************/
end_comment

begin_interface
DECL|interface|MRConstants
interface|interface
name|MRConstants
block|{
comment|//
comment|// Timeouts, constants
comment|//
DECL|field|COUNTER_UPDATE_INTERVAL
specifier|public
specifier|static
specifier|final
name|long
name|COUNTER_UPDATE_INTERVAL
init|=
literal|60
operator|*
literal|1000
decl_stmt|;
comment|//
comment|// Result codes
comment|//
DECL|field|SUCCESS
specifier|public
specifier|static
name|int
name|SUCCESS
init|=
literal|0
decl_stmt|;
DECL|field|FILE_NOT_FOUND
specifier|public
specifier|static
name|int
name|FILE_NOT_FOUND
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * The custom http header used for the map output length.    */
DECL|field|MAP_OUTPUT_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MAP_OUTPUT_LENGTH
init|=
literal|"Map-Output-Length"
decl_stmt|;
comment|/**    * The custom http header used for the "raw" map output length.    */
DECL|field|RAW_MAP_OUTPUT_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|RAW_MAP_OUTPUT_LENGTH
init|=
literal|"Raw-Map-Output-Length"
decl_stmt|;
comment|/**    * The map task from which the map output data is being transferred    */
DECL|field|FROM_MAP_TASK
specifier|public
specifier|static
specifier|final
name|String
name|FROM_MAP_TASK
init|=
literal|"from-map-task"
decl_stmt|;
comment|/**    * The reduce task number for which this map output is being transferred    */
DECL|field|FOR_REDUCE_TASK
specifier|public
specifier|static
specifier|final
name|String
name|FOR_REDUCE_TASK
init|=
literal|"for-reduce-task"
decl_stmt|;
DECL|field|WORKDIR
specifier|public
specifier|static
specifier|final
name|String
name|WORKDIR
init|=
literal|"work"
decl_stmt|;
block|}
end_interface

end_unit

