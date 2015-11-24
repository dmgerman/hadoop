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
comment|/**  * Used to describe the priority of the running job.   * DEFAULT : While submitting a job, if the user is not specifying priority,  * YARN has the capability to pick the default priority as per its config.  * Hence MapReduce can indicate such cases with this new enum.  * UNDEFINED_PRIORITY : YARN supports priority as an integer. Hence other than  * the five defined enums, YARN can consider other integers also. To generalize  * such cases, this specific enum is used.  */
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
DECL|enum|JobPriority
specifier|public
enum|enum
name|JobPriority
block|{
DECL|enumConstant|VERY_HIGH
name|VERY_HIGH
block|,
DECL|enumConstant|HIGH
name|HIGH
block|,
DECL|enumConstant|NORMAL
name|NORMAL
block|,
DECL|enumConstant|LOW
name|LOW
block|,
DECL|enumConstant|VERY_LOW
name|VERY_LOW
block|,
DECL|enumConstant|DEFAULT
name|DEFAULT
block|,
DECL|enumConstant|UNDEFINED_PRIORITY
name|UNDEFINED_PRIORITY
block|; }
end_enum

end_unit

