begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
package|;
end_package

begin_comment
comment|/**  * Enumeration of various aggregation type of a container log.  */
end_comment

begin_enum
DECL|enum|ContainerLogAggregationType
specifier|public
enum|enum
name|ContainerLogAggregationType
block|{
comment|/** The log is from NodeManager local log directory. */
DECL|enumConstant|LOCAL
name|LOCAL
block|,
comment|/** The log is from Remote FileSystem application log directory. */
DECL|enumConstant|AGGREGATED
name|AGGREGATED
block|}
end_enum

end_unit

