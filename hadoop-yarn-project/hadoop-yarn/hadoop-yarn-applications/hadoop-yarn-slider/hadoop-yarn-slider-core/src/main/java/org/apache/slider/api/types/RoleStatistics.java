begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api.types
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
package|;
end_package

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
operator|.
name|JsonIgnoreProperties
import|;
end_import

begin_comment
comment|/**  * Simple role statistics for state views; can be generated by RoleStatus  * instances, and aggregated for summary information.  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
DECL|class|RoleStatistics
specifier|public
class|class
name|RoleStatistics
block|{
DECL|field|activeAA
specifier|public
name|long
name|activeAA
init|=
literal|0L
decl_stmt|;
DECL|field|actual
specifier|public
name|long
name|actual
init|=
literal|0L
decl_stmt|;
DECL|field|completed
specifier|public
name|long
name|completed
init|=
literal|0L
decl_stmt|;
DECL|field|desired
specifier|public
name|long
name|desired
init|=
literal|0L
decl_stmt|;
DECL|field|failed
specifier|public
name|long
name|failed
init|=
literal|0L
decl_stmt|;
DECL|field|failedRecently
specifier|public
name|long
name|failedRecently
init|=
literal|0L
decl_stmt|;
DECL|field|limitsExceeded
specifier|public
name|long
name|limitsExceeded
init|=
literal|0L
decl_stmt|;
DECL|field|nodeFailed
specifier|public
name|long
name|nodeFailed
init|=
literal|0L
decl_stmt|;
DECL|field|preempted
specifier|public
name|long
name|preempted
init|=
literal|0L
decl_stmt|;
DECL|field|releasing
specifier|public
name|long
name|releasing
init|=
literal|0L
decl_stmt|;
DECL|field|requested
specifier|public
name|long
name|requested
init|=
literal|0L
decl_stmt|;
DECL|field|started
specifier|public
name|long
name|started
init|=
literal|0L
decl_stmt|;
DECL|field|startFailed
specifier|public
name|long
name|startFailed
init|=
literal|0L
decl_stmt|;
DECL|field|totalRequested
specifier|public
name|long
name|totalRequested
init|=
literal|0L
decl_stmt|;
comment|/**    * Add another statistics instance    * @param that the other value    * @return this entry    */
DECL|method|add (final RoleStatistics that)
specifier|public
name|RoleStatistics
name|add
parameter_list|(
specifier|final
name|RoleStatistics
name|that
parameter_list|)
block|{
name|activeAA
operator|+=
name|that
operator|.
name|activeAA
expr_stmt|;
name|actual
operator|+=
name|that
operator|.
name|actual
expr_stmt|;
name|completed
operator|+=
name|that
operator|.
name|completed
expr_stmt|;
name|desired
operator|+=
name|that
operator|.
name|desired
expr_stmt|;
name|failed
operator|+=
name|that
operator|.
name|failed
expr_stmt|;
name|failedRecently
operator|+=
name|that
operator|.
name|failedRecently
expr_stmt|;
name|limitsExceeded
operator|+=
name|that
operator|.
name|limitsExceeded
expr_stmt|;
name|nodeFailed
operator|+=
name|that
operator|.
name|nodeFailed
expr_stmt|;
name|preempted
operator|+=
name|that
operator|.
name|preempted
expr_stmt|;
name|releasing
operator|+=
name|that
operator|.
name|releasing
expr_stmt|;
name|requested
operator|+=
name|that
operator|.
name|requested
expr_stmt|;
name|started
operator|+=
name|that
operator|.
name|started
expr_stmt|;
name|startFailed
operator|+=
name|that
operator|.
name|totalRequested
expr_stmt|;
name|totalRequested
operator|+=
name|that
operator|.
name|totalRequested
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

