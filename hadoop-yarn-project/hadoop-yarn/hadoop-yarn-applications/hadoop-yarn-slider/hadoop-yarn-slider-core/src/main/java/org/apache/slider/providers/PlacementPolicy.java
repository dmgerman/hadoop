begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
package|;
end_package

begin_comment
comment|/**  * Placement values.  * This is nominally a bitmask, though not all values make sense  */
end_comment

begin_class
DECL|class|PlacementPolicy
specifier|public
class|class
name|PlacementPolicy
block|{
comment|/**    * Default value: history used, anti-affinity hinted at on rebuild/flex up    */
DECL|field|NONE
specifier|public
specifier|static
specifier|final
name|int
name|NONE
init|=
literal|0
decl_stmt|;
comment|/**    * Default value: history used, anti-affinity hinted at on rebuild/flex up    */
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT
init|=
name|NONE
decl_stmt|;
comment|/**    * Strict placement: when asking for an instance for which there is    * history, mandate that it is strict    */
DECL|field|STRICT
specifier|public
specifier|static
specifier|final
name|int
name|STRICT
init|=
literal|1
decl_stmt|;
comment|/**    * No data locality; do not use placement history    */
DECL|field|ANYWHERE
specifier|public
specifier|static
specifier|final
name|int
name|ANYWHERE
init|=
literal|2
decl_stmt|;
comment|/**    * @Deprecated: use {@link #ANYWHERE}    */
annotation|@
name|Deprecated
DECL|field|NO_DATA_LOCALITY
specifier|public
specifier|static
specifier|final
name|int
name|NO_DATA_LOCALITY
init|=
name|ANYWHERE
decl_stmt|;
comment|/**    * Anti-affinity is mandatory.    */
DECL|field|ANTI_AFFINITY_REQUIRED
specifier|public
specifier|static
specifier|final
name|int
name|ANTI_AFFINITY_REQUIRED
init|=
literal|4
decl_stmt|;
comment|/**    * Exclude from flexing; used internally to mark AMs.    */
DECL|field|EXCLUDE_FROM_FLEXING
specifier|public
specifier|static
specifier|final
name|int
name|EXCLUDE_FROM_FLEXING
init|=
literal|16
decl_stmt|;
block|}
end_class

end_unit

