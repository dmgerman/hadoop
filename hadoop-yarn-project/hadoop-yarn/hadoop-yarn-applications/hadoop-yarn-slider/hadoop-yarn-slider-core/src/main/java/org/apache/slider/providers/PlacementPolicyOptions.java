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

begin_enum
DECL|enum|PlacementPolicyOptions
specifier|public
enum|enum
name|PlacementPolicyOptions
block|{
DECL|enumConstant|EXCLUDE_FROM_FLEXING
name|EXCLUDE_FROM_FLEXING
block|,
DECL|enumConstant|NO_DATA_LOCALITY
name|NO_DATA_LOCALITY
block|,
DECL|enumConstant|ANTI_AFFINITY_REQUIRED
name|ANTI_AFFINITY_REQUIRED
block|, }
end_enum

end_unit

